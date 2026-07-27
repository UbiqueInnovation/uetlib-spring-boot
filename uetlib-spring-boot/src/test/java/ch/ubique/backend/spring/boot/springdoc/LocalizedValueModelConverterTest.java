package ch.ubique.backend.spring.boot.springdoc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.models.media.Schema;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springdoc.core.providers.ObjectMapperProvider;

@SuppressWarnings("unchecked")
class LocalizedValueModelConverterTest {

    private final ObjectMapper objectMapper = JsonMapper.builder().build();

    private final ObjectMapperProvider objectMapperProvider = mock(ObjectMapperProvider.class);

    private ModelConverters modelConverters;

    @BeforeEach
    void setUp() {
        when(objectMapperProvider.jsonMapper()).thenReturn(objectMapper);

        modelConverters = new ModelConverters(true);
        modelConverters.addConverter(new LocalizedValueModelConverter(objectMapperProvider));
    }

    @Test
    void modelsStringValueAsNonEmptyStringMap() throws Exception {
        final var schema = resolveField("defaultValue");

        assertThat(schema.getType()).isEqualTo("object");
        assertThat(schema.getMinProperties()).isEqualTo(1);
        assertThat(schema.getPropertyNames()).isNotNull();

        assertThat(schema.getAdditionalProperties())
                .isInstanceOfSatisfying(
                        Schema.class,
                        valueSchema ->
                                assertThat(valueSchema.getTypes()).containsExactly("string"));
    }

    @Test
    void retainsGenericIntegerType() throws Exception {
        final var schema = resolveField("uppercaseValue");

        assertThat(schema.getMinProperties()).isEqualTo(1);

        assertThat(schema.getAdditionalProperties())
                .isInstanceOfSatisfying(
                        Schema.class,
                        valueSchema -> {
                            assertThat(valueSchema.getTypes()).containsExactly("integer");
                            assertThat(valueSchema.getFormat()).isEqualTo("int32");
                        });
    }

    @Test
    void retainsReferenceForComplexValueType() throws Exception {
        final var resolved = resolveFieldSchema("camelCaseValue");
        final var schema = requireNonNull(resolved.schema);

        assertThat(schema.getAdditionalProperties())
                .isInstanceOfSatisfying(
                        Schema.class,
                        valueSchema -> assertThat(valueSchema.get$ref()).endsWith("/TestValue"));

        assertThat(resolved.referencedSchemas.keySet()).contains("TestValue");
    }

    @ParameterizedTest
    @CsvSource({
        "defaultValue, de-CH",
        "uppercaseValue, DE-CH",
        "camelCaseValue, deCh",
        "pascalCaseValue, DeCh"
    })
    void modelsKeySerialization(final String fieldName, final String expectedExample)
            throws Exception {

        final var schema = resolveField(fieldName);
        final Schema<?> propertyNames = schema.getPropertyNames();

        assertThat(propertyNames).isNotNull();
        assertThat(propertyNames.getPattern()).isNotBlank();
        assertThat(propertyNames.getExample()).isEqualTo(expectedExample);
    }

    private Schema<?> resolveField(final String fieldName) throws NoSuchFieldException {

        final var field = LocalizedValueSchemaFixture.class.getDeclaredField(fieldName);

        final var annotatedType =
                new AnnotatedType(field.getGenericType())
                        .ctxAnnotations(field.getAnnotations())
                        .resolveAsRef(false);

        final var resolvedSchema = modelConverters.resolveAsResolvedSchema(annotatedType);

        return requireNonNull(
                resolvedSchema.schema, () -> "No schema resolved for field: " + fieldName);
    }

    private ResolvedSchema resolveFieldSchema(final String fieldName) throws NoSuchFieldException {

        final var field = LocalizedValueSchemaFixture.class.getDeclaredField(fieldName);

        return modelConverters.resolveAsResolvedSchema(
                new AnnotatedType(field.getGenericType())
                        .ctxAnnotations(field.getAnnotations())
                        .resolveAsRef(false));
    }
}
