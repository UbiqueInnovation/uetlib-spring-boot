package ch.ubique.backend.spring.boot.problem.handler;

import static ch.ubique.backend.spring.boot.problem.errortype.TestErrorType.ERROR_TEST1;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.ubique.backend.spring.boot.problem.TestException;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.*;

class CatchAllRegressionTest {
    @Test
    void wrappedTypedProblemStillResolves() throws Exception {
        try (final var context = new StaticApplicationContext()) {
            context.refresh();
            final var handler = new FirstAdvice();
            handler.setApplicationContext(context);
            final var mvc =
                    MockMvcBuilders.standaloneSetup(
                                    new FailingController(
                                            new RuntimeException(new TestException(ERROR_TEST1))))
                            .setControllerAdvice(handler)
                            .build();
            mvc.perform(get("/regression")).andExpect(status().isBadRequest());
        }
    }

    @Test
    void lowerPriorityAdviceStillResolves() throws Exception {
        try (final var context = new StaticApplicationContext()) {
            context.refresh();
            final var handler = new FirstAdvice();
            handler.setApplicationContext(context);
            final var mvc =
                    MockMvcBuilders.standaloneSetup(new FailingController(new ProjectException()))
                            .setControllerAdvice(handler, new SecondAdvice())
                            .build();
            mvc.perform(get("/regression")).andExpect(status().isConflict());
        }
    }

    @RestControllerAdvice
    @Order(0)
    static class FirstAdvice extends UetlibExceptionHandler {}

    @RestControllerAdvice
    @Order(1)
    static class SecondAdvice {
        @ExceptionHandler(ProjectException.class)
        ResponseEntity<Void> handle(final ProjectException ex) {
            return ResponseEntity.status(409).build();
        }
    }

    static class ProjectException extends RuntimeException {}

    @RestController
    static class FailingController {
        private final RuntimeException exception;

        FailingController(final RuntimeException exception) {
            this.exception = exception;
        }

        @GetMapping("/regression")
        void fail() {
            throw exception;
        }
    }
}
