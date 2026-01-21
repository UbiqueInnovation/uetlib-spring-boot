package ch.ubique.backend.spring.boot.problem.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

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
     * UetlibExceptionHandlerSkeleton} (apart from the {@link
     * UetlibExceptionHandlerSkeleton#logger()} method) with matching signatures and return types.
     * This ensures that the skeleton remains synchronized with the Spring Framework's base
     * exception handler.
     */
    @Test
    void testResponseEntityExceptionHandlerHasSkeletonMethods() {
        final var skeletonMethodsToCheck =
                Arrays.stream(UetlibExceptionHandlerSkeleton.class.getMethods())
                        .filter(skeletonMethod -> !"logger".equals(skeletonMethod.getName()))
                        .toList();
        for (final var skeletonMethod : skeletonMethodsToCheck) {
            final var classMethod =
                    Arrays.stream(ResponseEntityExceptionHandler.class.getDeclaredMethods())
                            .filter(m -> m.getName().equals(skeletonMethod.getName()))
                            .filter(
                                    m ->
                                            Arrays.equals(
                                                    m.getParameterTypes(),
                                                    skeletonMethod.getParameterTypes()))
                            .findFirst()
                            .orElseGet(
                                    () ->
                                            fail(
                                                    "Method "
                                                            + skeletonMethod.getName()
                                                            + " with parameters "
                                                            + Arrays.toString(
                                                                    skeletonMethod
                                                                            .getParameterTypes())
                                                            + " not found in ResponseEntityExceptionHandler"));

            assertThat(classMethod.getReturnType()).isEqualTo(skeletonMethod.getReturnType());
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
