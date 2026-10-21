package ch.ubique.backend.spring.boot.problem.handler;

import static ch.ubique.backend.spring.boot.problem.errortype.TestErrorType.ERROR_TEST1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import ch.ubique.backend.spring.boot.problem.AbstractThrowableProblem;
import ch.ubique.backend.spring.boot.problem.TestException;
import ch.ubique.backend.spring.validation.ResponseValidationException;

import jakarta.validation.ConstraintViolationException;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.Set;
import java.util.stream.Stream;

class UetlibExceptionLoggingTest {
    private final TestUetlibExceptionHandler handler = new TestUetlibExceptionHandler();
    private final Logger logger = (Logger) handler.logger();
    private final ListAppender<ILoggingEvent> appender = new ListAppender<>();
    private final StaticApplicationContext context = new StaticApplicationContext();
    private Level previousLevel;

    @BeforeEach
    void setUp() {
        context.refresh();
        handler.setApplicationContext(context);
        previousLevel = logger.getLevel();
        logger.setLevel(Level.WARN);
        appender.start();
        logger.addAppender(appender);
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(appender);
        logger.setLevel(previousLevel);
        appender.stop();
        context.close();
    }

    static Stream<Arguments> handledExceptions() {
        return Stream.of(
                Arguments.of(new MissingServletRequestParameterException("id", "String"), 400),
                Arguments.of(new HttpMessageNotWritableException("cannot serialize"), 500),
                Arguments.of(new HttpClientErrorException(HttpStatus.NOT_FOUND), 404),
                Arguments.of(new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE), 503),
                Arguments.of(new TestException(ERROR_TEST1), 400),
                Arguments.of(
                        new AbstractThrowableProblem(
                                ERROR_TEST1, "Unavailable", HttpStatus.SERVICE_UNAVAILABLE) {},
                        503),
                Arguments.of(new ConstraintViolationException(Set.of()), 400),
                Arguments.of(new ResponseValidationException(Set.of(), String.class), 500));
    }

    @ParameterizedTest
    @MethodSource("handledExceptions")
    void logsHandledExceptionOnceWithStackTraceAndPreservesStatus(
            final Exception exception, final int expectedStatus) throws Exception {
        final var mvc =
                MockMvcBuilders.standaloneSetup(new FailingController(exception))
                        .setControllerAdvice(handler)
                        .build();

        mvc.perform(get("/fail")).andExpect(status().is(expectedStatus));

        assertLogged(exception, expectedStatus >= 500 ? Level.ERROR : Level.WARN);
    }

    @Test
    void logsAndRethrowsUnmatchedExceptionWithoutChangingContainerHandling() {
        final var exception = new NullPointerException();
        final var mvc =
                MockMvcBuilders.standaloneSetup(new FailingController(exception))
                        .setControllerAdvice(handler)
                        .build();

        final var thrown =
                assertThrows(
                        jakarta.servlet.ServletException.class, () -> mvc.perform(get("/fail")));

        assertSame(exception, thrown.getCause());
        assertLogged(exception, Level.ERROR);
        assertThat(appender.list.getFirst().getFormattedMessage()).doesNotContain("HTTP");
    }

    @Test
    void extensionTraitsUsingHandleExceptionInternalAreLogged() {
        final var exception = new IllegalStateException("extension failure");
        final var request = new ServletWebRequest(new MockHttpServletRequest("GET", "/fail"));
        final var response =
                handler.handleExceptionInternal(
                        exception,
                        ProblemDetail.forStatus(HttpStatus.BAD_GATEWAY),
                        new HttpHeaders(),
                        HttpStatus.BAD_GATEWAY,
                        request);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertLogged(exception, Level.ERROR);
    }

    @Test
    void loggingPolicyCanBeOverridden() {
        final var customHandler =
                new TestUetlibExceptionHandler() {
                    @Override
                    public void logException(
                            final Exception ex,
                            final @Nullable HttpStatusCode status,
                            final WebRequest request) {
                        // Applications can suppress or replace the default logging policy.
                    }
                };
        final var exception = new IllegalStateException();
        final var request = new ServletWebRequest(new MockHttpServletRequest("GET", "/fail"));

        assertSame(
                exception,
                assertThrows(
                        IllegalStateException.class,
                        () -> customHandler.logUnhandledException(exception, request)));
        assertThat(appender.list).isEmpty();
    }

    private void assertLogged(final Exception exception, final Level expectedLevel) {
        assertThat(appender.list).hasSize(1);
        final var event = appender.list.getFirst();
        assertThat(event.getLevel()).isEqualTo(expectedLevel);
        if (exception instanceof ConstraintViolationException) {
            assertThat(event.getThrowableProxy()).isNull();
            assertThat(event.getFormattedMessage()).contains(exception.getClass().getSimpleName());
            return;
        }
        assertThat(event.getThrowableProxy()).isNotNull();
        assertThat(event.getThrowableProxy().getClassName())
                .isEqualTo(exception.getClass().getName());
        assertThat(event.getThrowableProxy().getStackTraceElementProxyArray()).isNotEmpty();
        assertThat(event.getFormattedMessage()).contains("uri=/fail");
    }

    @RestController
    static class FailingController {
        private final Exception exception;

        FailingController(final Exception exception) {
            this.exception = exception;
        }

        @GetMapping("/fail")
        void fail() throws Exception {
            throw exception;
        }
    }
}
