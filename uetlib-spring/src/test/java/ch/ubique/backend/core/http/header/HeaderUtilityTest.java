package ch.ubique.backend.core.http.header;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class HeaderUtilityTest {

    @Test
    void testCreateNotFoundResponseEntity() {
        ResponseEntity<String> responseEntity = HeaderUtility.createNotFoundResponseEntity();
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND.value(), responseEntity.getStatusCodeValue());
        assertNull(responseEntity.getBody());
    }

    @Test
    void testCreateOKResponseEntity() {
        var body = "BODY";
        var responseEntity = HeaderUtility.createOKResponseEntity(body);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCodeValue());
        assertEquals(body, responseEntity.getBody());

        body = "BODY AND HEADERS";
        var maxAgeSeconds = 5 * 60 * 60;
        var nextRefreshSeconds = 3 * 60 * 60;
        responseEntity =
                HeaderUtility.createOKResponseEntity(body, nextRefreshSeconds, maxAgeSeconds);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCodeValue());
        assertEquals(body, responseEntity.getBody());
    }

    @Test
    void testCreateInternalServerErrorResponseEntity() {
        var body = "BODY";
        ResponseEntity<String> responseEntity =
                HeaderUtility.createInternalServerErrorResponseEntity(body);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), responseEntity.getStatusCodeValue());
        assertEquals(body, responseEntity.getBody());
    }

    @Test
    void testCreateServiceUnavailableResponse() {
        var responseEntity = HeaderUtility.createServiceUnavailableResponseEntity();
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, responseEntity.getStatusCode());
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), responseEntity.getStatusCodeValue());
        assertNull(responseEntity.getBody());
    }

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
