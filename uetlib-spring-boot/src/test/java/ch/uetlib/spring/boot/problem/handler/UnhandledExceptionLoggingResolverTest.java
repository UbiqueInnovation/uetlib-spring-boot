package ch.uetlib.spring.boot.problem.handler;

import static ch.uetlib.spring.boot.problem.errortype.TestErrorType.ERROR_TEST1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.uetlib.spring.boot.problem.TestException;
import ch.uetlib.spring.boot.problem.autoconfigure.UetlibSpringBootProblemAutoConfiguration;

import jakarta.servlet.ServletException;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.ArrayList;
import java.util.List;

class UnhandledExceptionLoggingResolverTest {
    private final WebApplicationContextRunner contextRunner =
            new WebApplicationContextRunner()
                    .withConfiguration(
                            AutoConfigurations.of(UetlibSpringBootProblemAutoConfiguration.class))
                    .withUserConfiguration(MvcConfiguration.class);

    @Test
    void wrappedTypedProblemStillReturnsBadRequest() {
        assertResolved(new IllegalStateException(new TestException(ERROR_TEST1)), 400);
    }

    @Test
    void lowerPriorityAdviceStillReturnsConflict() {
        assertResolved(new ProjectException(), 409);
    }

    private void assertResolved(final Exception exception, final int expectedStatus) {
        contextRunner.run(
                context -> {
                    context.getBean(FailingController.class).exception = exception;
                    final var mvc = MockMvcBuilders.webAppContextSetup(context).build();

                    mvc.perform(get("/fail")).andExpect(status().is(expectedStatus));

                    assertThat(context.getBean(LoggingAdvice.class).unhandled).isEmpty();
                });
    }

    @Test
    void unmatchedExceptionRemainsUnresolvedAndIsLoggedOnce() {
        contextRunner.run(
                context -> {
                    final var exception = new IllegalStateException("unmatched");
                    context.getBean(FailingController.class).exception = exception;
                    final var mvc = MockMvcBuilders.webAppContextSetup(context).build();

                    final var thrown =
                            assertThrows(ServletException.class, () -> mvc.perform(get("/fail")));

                    assertThat(thrown.getCause()).isSameAs(exception);
                    assertThat(context.getBean(LoggingAdvice.class).unhandled)
                            .containsExactly(exception);
                });
    }

    @Configuration(proxyBeanMethods = false)
    @EnableWebMvc
    static class MvcConfiguration {
        @Bean
        LoggingAdvice loggingAdvice() {
            return new LoggingAdvice();
        }

        @Bean
        ProjectAdvice projectAdvice() {
            return new ProjectAdvice();
        }

        @Bean
        FailingController failingController() {
            return new FailingController();
        }
    }

    @RestControllerAdvice
    @Order(0)
    static class LoggingAdvice extends UetlibExceptionHandler {
        final List<Exception> unhandled = new ArrayList<>();

        @Override
        public void logException(
                final Exception ex,
                final @Nullable HttpStatusCode status,
                final WebRequest request) {
            if (status == null) {
                unhandled.add(ex);
            }
        }
    }

    @RestControllerAdvice
    @Order(1)
    static class ProjectAdvice {
        @ExceptionHandler(ProjectException.class)
        ResponseEntity<Void> handleProjectException() {
            return ResponseEntity.status(409).build();
        }
    }

    static class ProjectException extends RuntimeException {}

    @RestController
    static class FailingController {
        Exception exception = new IllegalStateException();

        @GetMapping("/fail")
        void fail() throws Exception {
            throw exception;
        }
    }
}
