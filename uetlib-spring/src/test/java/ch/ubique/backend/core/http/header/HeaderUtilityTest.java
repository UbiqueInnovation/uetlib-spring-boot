package ch.ubique.backend.core.http.header;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
}
