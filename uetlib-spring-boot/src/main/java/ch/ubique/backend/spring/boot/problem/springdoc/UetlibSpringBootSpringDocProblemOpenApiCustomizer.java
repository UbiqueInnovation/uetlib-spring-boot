package ch.ubique.backend.spring.boot.problem.springdoc;

import ch.ubique.backend.spring.boot.problem.errortype.ProblemErrorType;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.customizers.OpenApiCustomizer;

import java.lang.reflect.ParameterizedType;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
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
 *       public static final URI TYPE} field).
 *   <li>If the second generic type has a `TYPE` field of type {@link URI}, it extracts its value
 *       and adds it as the {@code example} for the `type` property in the OpenAPI schema.
 *   <li>Uses reflection to modify the OpenAPI schema dynamically to include the example values for
 *       any classes implementing {@link SpringDocProblem} that meet the criteria.
 * </ul>
 *
 * The {@link OpenApiCustomizer} is invoked during the Spring Boot application startup to ensure the
 * OpenAPI specification includes accurate examples based on the runtime class details. This allows
 * for dynamic schema generation, making it easier to document APIs that use generic types with
 * specific constant fields, such as {@code TYPE}.
 *
 * <p>For example, if a class like {@code MailpieceOrderProblem} implements {@code
 * SpringDocProblem<MailpieceOrderErrorType, MailpieceOrderException>} and {@code
 * MailpieceOrderException} contains a static constant field {@code TYPE}, this method will
 * automatically add {@code TYPE.toString()} as the example value for the `type` field in the
 * generated OpenAPI schema.
 *
 * <p>This class is useful for dynamically adjusting the OpenAPI documentation without the need for
 * manually specifying examples for each class. It is particularly helpful when dealing with a large
 * number of problem types that share common patterns (e.g. using static fields for common
 * constants).
 */
public class UetlibSpringBootSpringDocProblemOpenApiCustomizer implements OpenApiCustomizer {

    private static final Logger logger =
            LoggerFactory.getLogger(UetlibSpringBootSpringDocProblemOpenApiCustomizer.class);

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

            try {
                // Check if the second generic type has a public static final URI TYPE field
                final var typeField = exceptionGenericType.getDeclaredField("TYPE");
                // Check if it's of type URI and static and final
                if (!typeField.getType().equals(URI.class)
                        || !java.lang.reflect.Modifier.isStatic(typeField.getModifiers())
                        || !java.lang.reflect.Modifier.isFinal(typeField.getModifiers())) {
                    continue;
                }

                // Get the value of the TYPE field
                final var typeValue = typeField.get(null).toString();

                // Edit the existing schema dynamically
                final var problemSchema =
                        openApi.getComponents().getSchemas().get(problemClass.getSimpleName());

                if (problemSchema == null) {
                    // openApi.getComponents().getSchemas() only contains all the schemas that are
                    // actually documented on some @Operation annotation and not all problemClasses
                    // are necessarily contained in there
                    continue;
                }

                // set 'type' example value to 'typeValue' and also the only possible value
                final var typeFieldSchema = (Schema) problemSchema.getProperties().get("type");
                problemSchema.addProperty(
                        typeFieldSchema.getName(),
                        new StringSchema().example(typeValue)._enum(List.of(typeValue)));

                // set errorCode of enum constant UNKNOWN which always exists
                final var unknownErrorTypeCode =
                        Arrays.stream(errorTypeGenericType.getEnumConstants())
                                .filter(e -> "UNKNOWN".equals(e.name()))
                                .findFirst()
                                .orElseThrow()
                                .code();
                final var allErrorCodes =
                        Arrays.stream(errorTypeGenericType.getEnumConstants())
                                .map(ProblemErrorType::code)
                                .toList();
                // set 'errorCode' example value to 'unknownErrorTypeCode' and also as the only
                // possible values
                final var errorCodeFieldSchema =
                        (Schema) problemSchema.getProperties().get("errorCode");
                problemSchema.addProperty(
                        errorCodeFieldSchema.getName(),
                        new IntegerSchema().example(unknownErrorTypeCode)._enum(allErrorCodes));

            } catch (final NoSuchFieldException | IllegalAccessException e) {
                // Handle the case where the TYPE field is not present or accessible
                logger.info(
                        "Cannot find a 'public static final URI TYPE' in the second generic type class {} of {} to set the example value in swagger. Skipping.",
                        exceptionGenericType.getSimpleName(),
                        problemClass.getSimpleName());
            }
        }
    }

    // Use Reflections to find all SpringDocProblem implementations
    @SuppressWarnings("rawtypes")
    private Set<Class<? extends SpringDocProblem>> findAllSpringDocProblemClasses() {
        // Initialize Reflections to scan for types implementing SpringDocProblem in your package
        final var reflections = new Reflections("ch.post", Scanners.SubTypes);

        // Return all types that implement SpringDocProblem
        return reflections.getSubTypesOf(SpringDocProblem.class);
    }

    // Extract the first generic type (the related error type) from the class
    private Class<ProblemErrorType> getErrorTypeGenericType(final Class<?> problemClass) {
        return getNthGenericType(problemClass, 0);
    }

    // Extract the second generic type (the related exception) from the class
    private Class<RuntimeException> getExceptionGenericType(final Class<?> problemClass) {
        return getNthGenericType(problemClass, 1);
    }

    // Extract the second generic type (the related exception) from the class
    @SuppressWarnings("unchecked")
    private <T> Class<T> getNthGenericType(final Class<?> problemClass, final int genericIndex) {
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
