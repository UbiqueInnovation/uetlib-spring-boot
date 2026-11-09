package ch.uetlib.spring.boot.springdoc;

import static ch.uetlib.core.utils.TimeUtils.TIME_UTILS_EUROPE_ZURICH;
import static ch.uetlib.core.utils.TimeUtils.toMillis;

import com.github.zafarkhaja.semver.Version;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;

import org.apache.commons.lang3.RandomUtils;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.utils.SpringDocUtils;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZonedDateTime;

@SuppressWarnings("unused")
public final class UetlibSpringBootSpringDocConfig {

    private UetlibSpringBootSpringDocConfig() {}

    public static GlobalOpenApiCustomizer setDefaultConfig() {

        final var config =
                SpringDocUtils.getConfig()
                        .replaceWithSchema(Instant.class, ref("InstantMillis"))
                        .replaceWithSchema(ZonedDateTime.class, ref("ZonedDateTimeMillis"))
                        .replaceWithSchema(LocalTime.class, ref("IsoLocalTime"))
                        .replaceWithSchema(Duration.class, ref("DurationMillis"))
                        .replaceWithSchema(Version.class, ref("Version"));

        return openApi -> {
            final var components = ensureComponents(openApi);
            addDefaultSchemas(components);
        };
    }

    public static void addDefaultSchemas(final Components components) {
        final var nowEuZh = TIME_UTILS_EUROPE_ZURICH.now();
        final var nowEuZhMillis = BigDecimal.valueOf(toMillis(nowEuZh));
        final var nowEuZhLocalTimeFormatted = nowEuZh.toLocalTime().toString();
        final var durationToday =
                BigDecimal.valueOf(
                        Duration.between(TIME_UTILS_EUROPE_ZURICH.startOfToday(), nowEuZh)
                                .toMillis());

        final var random = RandomUtils.insecure();

        addSchemaIfMissing(
                components,
                "InstantMillis",
                new NumberSchema()
                        .description("Instant represented as Unix epoch milliseconds.")
                        .example(nowEuZhMillis));

        addSchemaIfMissing(
                components,
                "ZonedDateTimeMillis",
                new NumberSchema()
                        .description("ZonedDateTime represented as Unix epoch milliseconds.")
                        .example(nowEuZhMillis));

        addSchemaIfMissing(
                components,
                "IsoLocalTime",
                new StringSchema()
                        .description(
                                "Local time in ISO format, usually HH:mm:ss, optionally with fractional seconds.")
                        .example(nowEuZhLocalTimeFormatted));

        addSchemaIfMissing(
                components,
                "DurationMillis",
                new NumberSchema()
                        .description("Duration represented as milliseconds.")
                        .example(durationToday));

        addSchemaIfMissing(
                components,
                "Version",
                new StringSchema()
                        .description("Semantic version string.")
                        .example(
                                random.randomInt(0, 10)
                                        + "."
                                        + random.randomInt(1, 100)
                                        + "."
                                        + random.randomInt(1, 10)));
    }

    public static Schema<?> ref(final String schemaName) {
        return new Schema<>().$ref("#/components/schemas/" + schemaName);
    }

    private static Components ensureComponents(final OpenAPI openApi) {
        if (openApi.getComponents() == null) {
            openApi.setComponents(new Components());
        }

        return openApi.getComponents();
    }

    private static void addSchemaIfMissing(
            final Components components, final String name, final Schema<?> schema) {
        if (components.getSchemas() == null || !components.getSchemas().containsKey(name)) {
            components.addSchemas(name, schema);
        }
    }
}
