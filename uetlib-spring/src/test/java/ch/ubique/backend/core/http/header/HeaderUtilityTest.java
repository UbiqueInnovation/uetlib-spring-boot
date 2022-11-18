package ch.ubique.backend.core.http.header;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class HeaderUtilityTest {

    @Test
    void testCreateNoContentResponseEntity() {
        var responseEntity = HeaderUtility.createNoContentResponseEntity(3600, 10800);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT.value(), responseEntity.getStatusCodeValue());
        assertNull(responseEntity.getBody());
    }

    @Test
    void testCreateMaxAgeHeaderValue() {
        var maxAge = "max-age";
        assertEquals(HeaderUtility.MAX_AGE, maxAge);

        var maxAgeSeconds = 0;
        var expectedHeaderValue = maxAge + "=" + maxAgeSeconds;
        var actualHeaderValue = HeaderUtility.createMaxAgeHeaderValue(maxAgeSeconds);
        assertEquals(expectedHeaderValue, actualHeaderValue);

        maxAgeSeconds = -1;
        expectedHeaderValue = maxAge + "=" + maxAgeSeconds;
        actualHeaderValue = HeaderUtility.createMaxAgeHeaderValue(maxAgeSeconds);
        assertEquals(expectedHeaderValue, actualHeaderValue);

        maxAgeSeconds = 60 * 60;
        expectedHeaderValue = maxAge + "=" + maxAgeSeconds;
        actualHeaderValue = HeaderUtility.createMaxAgeHeaderValue(maxAgeSeconds);
        assertEquals(expectedHeaderValue, actualHeaderValue);

        maxAgeSeconds = Integer.MAX_VALUE;
        expectedHeaderValue = maxAge + "=" + maxAgeSeconds;
        actualHeaderValue = HeaderUtility.createMaxAgeHeaderValue(maxAgeSeconds);
        assertEquals(expectedHeaderValue, actualHeaderValue);

        maxAgeSeconds = Integer.MIN_VALUE;
        expectedHeaderValue = maxAge + "=" + maxAgeSeconds;
        actualHeaderValue = HeaderUtility.createMaxAgeHeaderValue(maxAgeSeconds);
        assertEquals(expectedHeaderValue, actualHeaderValue);
    }
}
