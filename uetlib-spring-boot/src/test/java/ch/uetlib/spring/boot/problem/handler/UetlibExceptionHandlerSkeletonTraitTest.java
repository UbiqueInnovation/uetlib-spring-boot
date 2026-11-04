package ch.uetlib.spring.boot.problem.handler;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class UetlibExceptionHandlerSkeletonTraitTest {

    private final UetlibExceptionHandlerSkeletonTrait handler = new UetlibExceptionHandler() {};

    @Test
    void resolveStatusCodeOrDefault_withStandardStatus_returnsValue() {
        final var resolvedStatusCode =
                handler.resolveStatusCodeOrDefault(404, HttpStatus.INTERNAL_SERVER_ERROR);
        assertEquals(HttpStatus.NOT_FOUND, resolvedStatusCode);
    }

    @Test
    void resolveStatusCodeOrDefault_withNonStandardStatus_returnsValue() {
        final var resolvedStatusCode =
                handler.resolveStatusCodeOrDefault(299, HttpStatus.INTERNAL_SERVER_ERROR);

        assertEquals(299, resolvedStatusCode.value());
        assertNotEquals(HttpStatus.INTERNAL_SERVER_ERROR, resolvedStatusCode);
    }

    @Test
    void resolveStatusCodeOrDefault_withInvalidStatus_returnsDefault() {
        final var defaultStatus = HttpStatus.BAD_REQUEST;

        final var resolvedStatusCode = handler.resolveStatusCodeOrDefault(99, defaultStatus);

        assertSame(defaultStatus, resolvedStatusCode);
    }

    @Test
    void resolveStatusCodeOrDefault_withNegativeStatus_returnsDefault() {
        final var defaultStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        final var resolvedStatusCode = handler.resolveStatusCodeOrDefault(-1, defaultStatus);

        assertSame(defaultStatus, resolvedStatusCode);
    }
}
