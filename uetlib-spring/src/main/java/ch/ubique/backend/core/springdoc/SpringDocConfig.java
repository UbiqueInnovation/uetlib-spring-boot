package ch.ubique.backend.core.springdoc;

import static ch.ubique.backend.core.utils.TimeUtils.TIME_UTILS_EUROPE_ZURICH;

import ch.ubique.backend.core.localized.LocalizedValue;
import ch.ubique.backend.core.utils.TimeUtils;

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
import java.util.Map;

public final class SpringDocConfig {
    private SpringDocConfig() {}

    public static void setDefaultConfig() {
        SpringDocUtils.getConfig()
                // Instant as millis
                .replaceWithSchema(
                        Instant.class,
                        new NumberSchema()
                                ._default(
                                        BigDecimal.valueOf(
                                                TimeUtils.toMillis(
                                                        TIME_UTILS_EUROPE_ZURICH.now()))))
                // ZonedDateTime as millis
                .replaceWithSchema(
                        ZonedDateTime.class,
                        new NumberSchema()
                                ._default(
                                        BigDecimal.valueOf(
                                                TimeUtils.toMillis(
                                                        TIME_UTILS_EUROPE_ZURICH.now()))))
                // LocalTime as ISO HH:mm:ss and optionally nanos
                .replaceWithSchema(
                        LocalTime.class,
                        new StringSchema()
                                ._default(LocalTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME)))
                // Duration as millis
                .replaceWithSchema(
                        Duration.class,
                        new NumberSchema()
                                ._default(
                                        BigDecimal.valueOf(
                                                Duration.between(
                                                                TIME_UTILS_EUROPE_ZURICH
                                                                        .startOfToday(),
                                                                TIME_UTILS_EUROPE_ZURICH.now())
                                                        .toMillis())))
                .replaceWithSchema(
                        LocalizedValue.class,
                        new MapSchema()
                                ._default(
                                        Map.ofEntries(
                                                Map.entry(
                                                        "de",
                                                        "de value of the correct type"
                                                                + " (string/int/etc.)"),
                                                Map.entry(
                                                        "it",
                                                        "it value of the correct type"
                                                                + " (string/int/etc.)"),
                                                Map.entry(
                                                        "fr",
                                                        "fr value of the correct type"
                                                                + " (string/int/etc.)"),
                                                Map.entry(
                                                        "en",
                                                        "en value of the correct type"
                                                                + " (string/int/etc.)"))));
    }
}
