package ch.uetlib.spring.boot.springdoc;

import static ch.uetlib.spring.boot.springdoc.OpenEnumModelConverter.EXTENSIBLE_ENUM;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static java.util.Objects.requireNonNull;

import ch.uetlib.core.openenum.OpenEnumSet;
import ch.uetlib.core.openenum.OpenEnumValue;
import ch.uetlib.core.openenum.UnknownOpenEnumValue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.media.Schema;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springdoc.core.providers.ObjectMapperProvider;

import java.util.Collection;
import java.util.List;

@SuppressWarnings("unchecked")
class OpenEnumModelConverterTest {

    sealed interface Color extends OpenEnumValue<String> {

        OpenEnumSet<Color, String> KNOWN_VALUES = OpenEnumSet.of(Known.class, Unknown::new);

        static Collection<Color> knownValues() {
            return KNOWN_VALUES.values();
        }

        enum Known implements Color {
            RED("red"),
            GREEN("green");

            private final String value;

            Known(final String value) {
                this.value = value;
            }

            @Override
            public String value() {
                return value;
            }
        }

        record Unknown(String value) implements Color, UnknownOpenEnumValue<String> {}
    }

    enum PlainColor {
        RED,
        GREEN
    }

    record Holder(Color openEnum, PlainColor plainEnum) {}

    private final ObjectMapper objectMapper = JsonMapper.builder().build();
    private final ObjectMapperProvider objectMapperProvider = mock(ObjectMapperProvider.class);

    private ModelConverters modelConverters;

    @BeforeEach
    void setUp() {
        when(objectMapperProvider.jsonMapper()).thenReturn(objectMapper);

        modelConverters = new ModelConverters(true);
        modelConverters.addConverter(new OpenEnumModelConverter(objectMapperProvider));
    }

    @Test
    void documentsKnownValuesWithoutConstrainingTheSchema() {
        final var schema = resolveProperty("openEnum");

        // OpenAPI 3.1 mode carries the type in `types`, not `type`.
        assertThat(schema.getTypes()).containsExactly("string");
        // `enum` would reject the unknown values this type exists to preserve.
        assertThat(schema.getEnum()).isNullOrEmpty();
        assertThat(schema.getExtensions()).containsKey(EXTENSIBLE_ENUM);
        assertThat((List<?>) schema.getExtensions().get(EXTENSIBLE_ENUM))
                .isEqualTo(List.of("red", "green"));
        assertThat(schema.getDescription())
                .contains("Known values: `red`, `green`.")
                .contains("must be tolerated by clients");
    }

    @Test
    void leavesPlainEnumsAsClosedEnumSchemas() {
        final var schema = resolveProperty("plainEnum");

        assertThat(schema.getEnum()).isEqualTo(List.of("RED", "GREEN"));
        // The converter leaves plain enums untouched, so no extensions are added at all.
        assertThat(schema.getExtensions()).isNull();
    }

    @Test
    void knownValuesExposesTheConstantsWithoutLeakingIntoTheSchema() {
        assertThat(Color.knownValues()).containsExactly(Color.Known.RED, Color.Known.GREEN);

        // `knownValues()` is static, so it must not be introspected as a schema property.
        final var holder =
                requireNonNull(
                        modelConverters
                                .readAll(new AnnotatedType(Holder.class).resolveAsRef(false))
                                .get(Holder.class.getSimpleName()));

        assertThat(holder.getProperties()).containsOnlyKeys("openEnum", "plainEnum");
    }

    private Schema<?> resolveProperty(final String propertyName) {
        final var resolved =
                modelConverters.readAll(new AnnotatedType(Holder.class).resolveAsRef(false));
        final var holder = requireNonNull(resolved.get(Holder.class.getSimpleName()));

        return requireNonNull((Schema<?>) holder.getProperties().get(propertyName));
    }
}
