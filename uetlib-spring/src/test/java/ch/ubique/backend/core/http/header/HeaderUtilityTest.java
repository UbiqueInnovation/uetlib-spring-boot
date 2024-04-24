package ch.ubique.backend.core.http.header;

import static ch.ubique.backend.core.http.header.HeaderUtility.formatHttpHeaderDate;
import static ch.ubique.backend.core.http.header.HeaderUtility.parseHttpDateOrOffsetHeader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;

class HeaderUtilityTest {

    @Test
    void testCreateNoContentResponseEntity() {
        final var responseEntity =
                HeaderUtility.createNoContentResponseEntity(
                        Duration.ofHours(1), Duration.ofHours(3));
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    void testFormatHttpHeaderDate() {
        final var lastSecond1999 = ZonedDateTime.of(1999, 12, 31, 23, 59, 59, 0, ZoneId.of("GMT"));
        assertEquals("Fri, 31 Dec 1999 23:59:59 UTC", formatHttpHeaderDate(lastSecond1999));
    }

    @Test
    void testParseHttpDateOrOffsetHeader() {
        final var lastSecond1999 = ZonedDateTime.of(1999, 12, 31, 23, 59, 59, 0, ZoneId.of("GMT"));
        assertEquals(
                lastSecond1999,
                parseHttpDateOrOffsetHeader("Fri, 31 Dec 1999 23:59:59 GMT", ZonedDateTime.now()));
        assertEquals(
                lastSecond1999.plusSeconds(120), parseHttpDateOrOffsetHeader("120", lastSecond1999));
    }
}
