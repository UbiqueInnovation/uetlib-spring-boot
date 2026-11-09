package ch.uetlib.spring.boot.springdoc;

import ch.uetlib.core.localized.LocalizedValue;
import ch.uetlib.core.localized.annotation.JsonLocalizedValue;

import com.fasterxml.jackson.databind.JavaType;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.oas.models.media.MapSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;

import org.jspecify.annotations.Nullable;
import org.springdoc.core.providers.ObjectMapperProvider;

import java.util.Iterator;
import java.util.Objects;

public final class LocalizedValueModelConverter implements ModelConverter {

    /*
     * A practical syntactic approximation of IETF BCP 47 language tags.
     *
     * It intentionally does not attempt to validate registered languages,
     * regions, scripts, variants, extensions, or grandfathered tags.
     */
    private static final String DEFAULT_KEY_PATTERN =
            "^(?:[A-Za-z]{2,8}(?:-[A-Za-z0-9]{1,8})*" + "|x(?:-[A-Za-z0-9]{1,8})+)$";

    private static final String UPPERCASE_KEY_PATTERN =
            "^(?:[A-Z]{2,8}(?:-[A-Z0-9]{1,8})*" + "|X(?:-[A-Z0-9]{1,8})+)$";

    private static final String CAMEL_CASE_KEY_PATTERN = "^[a-z][A-Za-z0-9]*$";

    private static final String PASCAL_CASE_KEY_PATTERN = "^[A-Z][A-Za-z0-9]*$";

    private static final String DESCRIPTION =
            "Non-empty map of localized values. Keys are serialized locale identifiers.";

    private final ObjectMapperProvider objectMapperProvider;

    public LocalizedValueModelConverter(final ObjectMapperProvider objectMapperProvider) {
        this.objectMapperProvider = objectMapperProvider;
    }

    @Override
    public @Nullable Schema<?> resolve(
            final AnnotatedType annotatedType,
            final ModelConverterContext context,
            final Iterator<ModelConverter> chain) {

        final var javaType = resolveJavaType(annotatedType);

        if (!LocalizedValue.class.isAssignableFrom(javaType.getRawClass())) {
            return resolveNext(annotatedType, context, chain);
        }

        final var keySerialization = resolveKeySerialization(annotatedType);
        final var typeFactory = objectMapperProvider.jsonMapper().getTypeFactory();

        final JavaType containedValueType = javaType.containedType(0);
        final JavaType valueType =
                containedValueType != null
                        ? containedValueType
                        : typeFactory.constructType(Object.class);

        /*
         * Resolve only T. Re-resolving Map<String, T> from inside this
         * converter can be suppressed by the converter context's recursive
         * resolution tracking.
         */
        final var resolvedValueSchema =
                context.resolve(
                        new AnnotatedType(valueType).resolveAsRef(true).schemaProperty(true));

        /*
         * Object or otherwise unresolvable T means unrestricted JSON values.
         * For concrete types the normal ModelResolver should return a schema.
         */
        final var valueSchema = resolvedValueSchema != null ? resolvedValueSchema : new Schema<>();

        final var schema = new MapSchema();
        schema.setAdditionalProperties(valueSchema);
        schema.setMinProperties(1);
        schema.setPropertyNames(createPropertyNamesSchema(keySerialization));
        schema.setDescription(DESCRIPTION);
        schema.set$comment(
                "UB LocalizedValue: when present, the object must contain "
                        + "at least one localized value.");

        return schema;
    }

    private JavaType resolveJavaType(final AnnotatedType annotatedType) {
        final var type = annotatedType.getType();

        Objects.requireNonNull(type, "AnnotatedType.type must not be null");

        if (type instanceof JavaType javaType) {
            return javaType;
        }

        return objectMapperProvider.jsonMapper().getTypeFactory().constructType(type);
    }

    private static JsonLocalizedValue.SerializationMethod resolveKeySerialization(
            final AnnotatedType annotatedType) {

        final var annotations = annotatedType.getCtxAnnotations();

        if (annotations == null) {
            return JsonLocalizedValue.SerializationMethod.DEFAULT;
        }

        for (final var annotation : annotations) {
            if (annotation instanceof JsonLocalizedValue jsonLocalizedValue) {
                return jsonLocalizedValue.keySerialization();
            }
        }

        return JsonLocalizedValue.SerializationMethod.DEFAULT;
    }

    private static StringSchema createPropertyNamesSchema(
            final JsonLocalizedValue.SerializationMethod serializationMethod) {

        final var schema = new StringSchema();

        switch (serializationMethod) {
            case DEFAULT -> {
                schema.setDescription("IETF BCP 47 language tag, for example de or de-CH.");
                schema.setPattern(DEFAULT_KEY_PATTERN);
                schema.setExample("de-CH");
            }

            case UPPERCASE -> {
                schema.setDescription("Uppercase locale key, for example DE or DE-CH.");
                schema.setPattern(UPPERCASE_KEY_PATTERN);
                schema.setExample("DE-CH");
            }

            case CAMEL_CASE -> {
                schema.setDescription("Lower camel-case locale key, for example de or deCh.");
                schema.setPattern(CAMEL_CASE_KEY_PATTERN);
                schema.setExample("deCh");
            }

            case PASCAL_CASE -> {
                schema.setDescription("Upper camel-case locale key, for example De or DeCh.");
                schema.setPattern(PASCAL_CASE_KEY_PATTERN);
                schema.setExample("DeCh");
            }
        }

        return schema;
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
