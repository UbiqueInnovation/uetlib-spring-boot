package ch.ubique.backend.core.http.header;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.Duration;

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
    void testCreateNotModifiedResponseEntity() {
        final var responseEntity =
                HeaderUtility.createNotModifiedResponseEntity(
                        Duration.ofHours(1), Duration.ofHours(3));
        assertEquals(HttpStatus.NOT_MODIFIED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getHeaders().getFirst("X-Next-Refresh"));
        assertEquals("max-age=10800", responseEntity.getHeaders().getCacheControl());
        assertNull(responseEntity.getBody());
    }
}
