package ch.uetlib.spring.boot.problem.springdoc;

import static java.util.Objects.requireNonNull;

import ch.uetlib.spring.boot.problem.errortype.ProblemErrorType;
import ch.uetlib.spring.boot.problem.errortype.ProblemType;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;

import org.jspecify.annotations.Nullable;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.springdoc.core.customizers.OpenApiCustomizer;

import java.lang.reflect.ParameterizedType;
import java.net.URI;
import java.util.Arrays;
import java.util.Set;

/**
 * Customises the OpenAPI specification for the application by dynamically adding example values for
 * the `type` property in schemas of classes implementing the {@link SpringDocProblem} interface.
 *
 * <p>This method:
 *
 * <ul>
 *   <li>Scans all classes in the project that implement {@link SpringDocProblem}.
 *   <li>For each class, it extracts the second generic type (which is expected to have a {@code
 *       public static final ProblemType PROBLEM_TYPE} field).
 *   <li>If the second generic type has a `PROBLEM_TYPE` field of type {@link ProblemType}, it
 *       extracts its value and adds it as the {@code example} for the `type` property in the
 *       OpenAPI schema.
 *   <li>Uses reflection to modify the OpenAPI schema dynamically to include the example values for
 *       any classes implementing {@link SpringDocProblem} that meet the criteria.
 * </ul>
 *
 * The {@link OpenApiCustomizer} is invoked during the Spring Boot application startup to ensure the
 * OpenAPI specification includes accurate examples based on the runtime class details. This allows
 * for dynamic schema generation, making it easier to document APIs that use generic types with
 * specific constant fields, such as {@code PROBLEM_TYPE}.
 *
 * <p>The {@code type} and {@code problemCode} fields are automatically updated with all possible
 * values and an example value.
 *
 * <p>This class is useful for dynamically adjusting the OpenAPI documentation without the need for
 * manually specifying examples for each class. It is particularly helpful when dealing with a large
 * number of problem types that share common patterns (e.g. using static fields for common
 * constants).
 */
public class UetlibSpringBootProblemSpringDocProblemOpenApiCustomizer implements OpenApiCustomizer {

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void customise(final OpenAPI openApi) {
        // Find all classes implementing SpringDocProblem
        final var problemClasses = findAllSpringDocProblemClasses();

        // Iterate through the classes
        for (final var problemClass : problemClasses) {
            // Extract the error type (first generic parameter type) (if available)
            final var errorTypeGenericType = getErrorTypeGenericType(problemClass);
            // Extract the exception type (second generic parameter type) (if available)
            final var exceptionGenericType = getExceptionGenericType(problemClass);
            if (errorTypeGenericType == null || exceptionGenericType == null) {
                continue;
            }

            // Edit the existing schema dynamically
            final var problemSchema =
                    openApi.getComponents().getSchemas().get(problemClass.getSimpleName());

            if (problemSchema == null) {
                // openApi.getComponents().getSchemas() only contains all the schemas that are
                // actually documented on some @Operation annotation, and not all problemClasses
                // are necessarily contained in there
                continue;
            }

            // get type and problemCode of all enum constants
            final var allProblemErrorTypes =
                    Arrays.stream(errorTypeGenericType.getEnumConstants()).toList();
            final var allTypes =
                    allProblemErrorTypes.stream()
                            .map(ProblemErrorType::type)
                            .map(URI::toString)
                            .toList();
            final var allProblemCodes =
                    allProblemErrorTypes.stream().map(ProblemErrorType::problemCode).toList();

            // set 'type' example value to 'unknownType' and also set all the
            // possible values
            final var typeFieldSchema =
                    (Schema) requireNonNull(problemSchema.getProperties().get("type"));
            problemSchema.addProperty(
                    typeFieldSchema.getName(),
                    new StringSchema().example(allTypes.getFirst())._enum(allTypes));

            // set 'problemCode' example value to 'unknownProblemCode' and also set all the
            // possible values
            final var problemCodeFieldSchema =
                    (Schema) requireNonNull(problemSchema.getProperties().get("problemCode"));
            problemSchema.addProperty(
                    problemCodeFieldSchema.getName(),
                    new StringSchema().example(allProblemCodes.getFirst())._enum(allProblemCodes));
        }
    }

    // Use Reflections to find all SpringDocProblem implementations
    @SuppressWarnings("rawtypes")
    private Set<Class<? extends SpringDocProblem>> findAllSpringDocProblemClasses() {
        // Initialize Reflections to scan for types implementing SpringDocProblem in your package
        final var reflections =
                new Reflections(ClasspathHelper.forJavaClassPath(), Scanners.SubTypes);

        // Return all types that implement SpringDocProblem
        return reflections.getSubTypesOf(SpringDocProblem.class);
    }

    // Extract the first generic type (the related error type) from the class
    private @Nullable Class<ProblemErrorType> getErrorTypeGenericType(final Class<?> problemClass) {
        return getNthGenericType(problemClass, 0);
    }

    // Extract the second generic type (the related exception) from the class
    private @Nullable Class<RuntimeException> getExceptionGenericType(final Class<?> problemClass) {
        return getNthGenericType(problemClass, 1);
    }

    // Extract the second generic type (the related exception) from the class
    @SuppressWarnings("unchecked")
    private <T> @Nullable Class<T> getNthGenericType(
            final Class<?> problemClass, final int genericIndex) {
        // Check if the class is parameterised (i.e. has generics)
        final var genericInterfaces = problemClass.getGenericInterfaces();
        for (final var genericInterface : genericInterfaces) {
            if (genericInterface instanceof final ParameterizedType parameterizedType) {
                // Get the actual type arguments of the generic interface
                final var typeArguments = parameterizedType.getActualTypeArguments();
                if (typeArguments.length > genericIndex) {
                    // Return the second generic type
                    return (Class<T>) typeArguments[genericIndex];
                }
            }
        }
        return null; // Return null if no second generic type is found
    }
}
