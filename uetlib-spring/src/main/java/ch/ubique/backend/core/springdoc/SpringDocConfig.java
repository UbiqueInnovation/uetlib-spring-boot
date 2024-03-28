package ch.ubique.backend.core.springdoc;

import static ch.ubique.backend.core.utils.TimeUtils.TIME_UTILS_EUROPE_ZURICH;
import static ch.ubique.backend.core.utils.TimeUtils.toMillis;

import static java.util.Map.entry;
import static java.util.Map.ofEntries;

import ch.ubique.backend.core.localized.LocalizedValue;

import io.swagger.v3.oas.models.media.MapSchema;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.StringSchema;

import org.springdoc.core.utils.SpringDocUtils;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@SuppressWarnings("unused")
public final class SpringDocConfig {
    private SpringDocConfig() {}

    public static void setDefaultConfig() {
        final var nowEuZh = TIME_UTILS_EUROPE_ZURICH.now();
        final var nowEuZhMillis = BigDecimal.valueOf(toMillis(nowEuZh));
        final var nowEuZhLocalTimeFormatted = nowEuZh.format(DateTimeFormatter.ISO_LOCAL_TIME);
        final var durationToday =
                BigDecimal.valueOf(
                        Duration.between(TIME_UTILS_EUROPE_ZURICH.startOfToday(), nowEuZh)
                                .toMillis());

        SpringDocUtils.getConfig()
                // Instant as millis
                .replaceWithSchema(Instant.class, new NumberSchema()._default(nowEuZhMillis))
                // ZonedDateTime as millis
                .replaceWithSchema(ZonedDateTime.class, new NumberSchema()._default(nowEuZhMillis))
                // LocalTime as ISO HH:mm:ss and optionally nanos
                .replaceWithSchema(
                        LocalTime.class, new StringSchema()._default(nowEuZhLocalTimeFormatted))
                // Duration as millis
                .replaceWithSchema(Duration.class, new NumberSchema()._default(durationToday))
                // Duration as Map<String, String> with description
                .replaceWithSchema(
                        LocalizedValue.class,
                        new MapSchema()
                                ._default(
                                        ofEntries(
                                                entry(
                                                        "de",
                                                        "de value of the correct type"
                                                                + " (string/int/object/etc.)"),
                                                entry(
                                                        "it",
                                                        "it value of the correct type"
                                                                + " (string/int/object/etc.)"),
                                                entry(
                                                        "fr",
                                                        "fr value of the correct type"
                                                                + " (string/int/object/etc.)"),
                                                entry(
                                                        "en",
                                                        "en value of the correct type"
                                                                + " (string/int/object/etc.)"))));
    }
}
