package ch.ubique.backend.spring.boot.problem.handler;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Arrays;

/**
 * Tests for {@link UetlibExceptionHandlerSkeleton} to ensure compatibility with {@link
 * ResponseEntityExceptionHandler} and that it is correctly implemented by {@link
 * UetlibExceptionHandler}.
 */
class ResponseEntityExceptionSkeletonTest {

    /**
     * Verifies that {@link ResponseEntityExceptionHandler} contains all methods defined in {@link
     * UetlibExceptionHandlerSkeleton} with matching signatures and return types. This ensures that
     * the skeleton remains synchronized with the Spring Framework's base exception handler.
     */
    @Test
    void testResponseEntityExceptionHandlerHasSkeletonMethods() {
        for (final var interfaceMethod : UetlibExceptionHandlerSkeleton.class.getMethods()) {
            final var classMethod =
                    Arrays.stream(ResponseEntityExceptionHandler.class.getDeclaredMethods())
                            .filter(m -> m.getName().equals(interfaceMethod.getName()))
                            .filter(
                                    m ->
                                            Arrays.equals(
                                                    m.getParameterTypes(),
                                                    interfaceMethod.getParameterTypes()))
                            .findFirst()
                            .orElseThrow(
                                    () ->
                                            new AssertionError(
                                                    "Method "
                                                            + interfaceMethod.getName()
                                                            + " with parameters "
                                                            + Arrays.toString(
                                                                    interfaceMethod
                                                                            .getParameterTypes())
                                                            + " not found in ResponseEntityExceptionHandler"));

            assertThat(classMethod.getReturnType()).isEqualTo(interfaceMethod.getReturnType());
        }
    }

    /**
     * Confirms that {@link UetlibExceptionHandler} explicitly implements {@link
     * UetlibExceptionHandlerSkeleton}.
     */
    @Test
    void testUetlibExceptionHandlerImplementsSkeleton() {
        assertThat(UetlibExceptionHandler.class)
                .isAssignableTo(UetlibExceptionHandlerSkeleton.class);
    }
}
