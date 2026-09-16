package ch.uetlib.spring.boot.springdoc;

import ch.uetlib.core.openenum.OpenEnumValue;

import com.fasterxml.jackson.databind.JavaType;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.oas.models.media.Schema;

import org.jspecify.annotations.Nullable;
import org.springdoc.core.providers.ObjectMapperProvider;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Documents the known constants of an {@link OpenEnumValue} without constraining the schema to
 * them.
 *
 * <p>A plain {@code enum} resolves to {@code {"type": "string", "enum": [...]}}. An open enum
 * cannot use {@code enum}: by design it also carries values the backend does not know yet, and
 * {@code enum} is a closed constraint, so a validator would reject exactly the values the type
 * exists to preserve. Left alone, swagger-core sees only the {@code @JsonValue} accessor and emits
 * a bare {@code {"type": "string"}}, which loses the known values entirely.
 *
 * <p>This converter keeps the unconstrained base schema and attaches the known values as {@code
 * x-extensible-enum}, the common convention for "these are the known values, but more may appear".
 * Validators and generators ignore the extension, while documentation and clients that understand
 * it still get the list.
 */
public final class OpenEnumModelConverter implements ModelConverter {

    static final String EXTENSIBLE_ENUM = "x-extensible-enum";

    /**
     * Swagger UI renders {@code description} but ignores unrecognized vendor extensions, so the
     * known values are spelled out here as well: the extension alone would be invisible to anyone
     * reading the rendered documentation.
     */
    private static final int MAX_VALUES_IN_DESCRIPTION = 25;

    private static final String TOLERANCE_NOTE =
            "Other values may be returned and must be tolerated by clients.";

    private final ObjectMapperProvider objectMapperProvider;

    public OpenEnumModelConverter(final ObjectMapperProvider objectMapperProvider) {
        this.objectMapperProvider = objectMapperProvider;
    }

    @Override
    public @Nullable Schema<?> resolve(
            final AnnotatedType annotatedType,
            final ModelConverterContext context,
            final Iterator<ModelConverter> chain) {

        final var rawClass = resolveJavaType(annotatedType).getRawClass();

        if (!OpenEnumValue.class.isAssignableFrom(rawClass)) {
            return resolveNext(annotatedType, context, chain);
        }

        final var knownValues = knownValuesOf(rawClass);

        // Resolve the base schema first: the @JsonValue accessor is what tells swagger-core the
        // underlying type (string, integer, ...), so we never have to infer it ourselves.
        final var schema = resolveNext(annotatedType, context, chain);

        if (schema == null || knownValues.isEmpty()) {
            return schema;
        }

        schema.addExtension(EXTENSIBLE_ENUM, knownValues);
        schema.setDescription(appendDescription(schema.getDescription(), knownValues));

        return schema;
    }

    /**
     * The raw values of the known constants, taken from the sealed hierarchy's {@code enum}
     * permitted subclass. Reading the permitted subclasses rather than a conventionally named field
     * keeps this independent of how a domain type names its nested types.
     */
    private static List<Object> knownValuesOf(final Class<?> rawClass) {
        final var permitted = rawClass.getPermittedSubclasses();

        if (permitted == null) {
            return List.of();
        }

        return Arrays.stream(permitted)
                .filter(Class::isEnum)
                .findFirst()
                .map(Class::getEnumConstants)
                .stream()
                .flatMap(Arrays::stream)
                .map(OpenEnumValue.class::cast)
                .map(OpenEnumValue::value)
                .toList();
    }

    private static String appendDescription(
            final @Nullable String existing, final List<Object> knownValues) {

        final var shown =
                knownValues.stream()
                        .limit(MAX_VALUES_IN_DESCRIPTION)
                        .map(value -> "`" + value + "`")
                        .collect(Collectors.joining(", "));

        final var omitted = knownValues.size() - MAX_VALUES_IN_DESCRIPTION;
        final var suffix =
                omitted > 0 ? " and %d more (see `%s`)".formatted(omitted, EXTENSIBLE_ENUM) : "";

        final var note = "Known values: " + shown + suffix + ". " + TOLERANCE_NOTE;

        return existing == null || existing.isBlank() ? note : existing + "\n\n" + note;
    }

    private JavaType resolveJavaType(final AnnotatedType annotatedType) {
        final var type = annotatedType.getType();

        Objects.requireNonNull(type, "AnnotatedType.type must not be null");

        if (type instanceof final JavaType javaType) {
            return javaType;
        }

        return objectMapperProvider.jsonMapper().getTypeFactory().constructType(type);
    }

    private static @Nullable Schema<?> resolveNext(
            final AnnotatedType annotatedType,
            final ModelConverterContext context,
            final Iterator<ModelConverter> chain) {

        if (!chain.hasNext()) {
            return null;
        }

        return chain.next().resolve(annotatedType, context, chain);
    }
}
