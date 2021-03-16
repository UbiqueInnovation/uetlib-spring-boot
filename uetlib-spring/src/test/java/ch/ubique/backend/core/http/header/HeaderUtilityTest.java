package ch.ubique.backend.core.http.header;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;

class HeaderUtilityTest {

    private static int getSeconds(Date date) {
        var calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.SECOND);
    }

    private static Date parseDate(String date) throws ParseException {
        return HeaderUtility.headerDateFormatter.get().parse(date);
    }

    private static String formatDate(Date date) {
        return HeaderUtility.headerDateFormatter.get().format(date);
    }

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
    void testSetHeaders() {
        var response = new MockHttpServletResponse();

        var maxAgeSeconds = 60 * 60;
        var nextRefreshSeconds = 30 * 60;
        HeaderUtility.setHeaders(response, maxAgeSeconds, nextRefreshSeconds);

        var expectedCacheControl = HeaderUtility.createMaxAgeHeaderValue(maxAgeSeconds);
        var actualCacheControl = response.getHeader(HeaderUtility.CACHE_CONTROL);
        assertEquals(expectedCacheControl, actualCacheControl);
    }

    @Test
    void testCreateHeaders() throws ParseException {
        var maxAgeSeconds = 60 * 60;
        var nextRefreshSeconds = 30 * 60;
        HttpHeaders headers = HeaderUtility.createHeaders(maxAgeSeconds, nextRefreshSeconds);

        var expectedCacheControl = HeaderUtility.createMaxAgeHeaderValue(maxAgeSeconds);
        var actualCacheControl = headers.getCacheControl();
        assertEquals(expectedCacheControl, actualCacheControl);

        var nextRefreshHeaders = headers.get(HeaderUtility.NEXT_REFRESH);
        assertNotEquals(null, nextRefreshHeaders);
        assert nextRefreshHeaders != null;
        assertEquals(1, nextRefreshHeaders.size());

        var actualDate = parseDate(nextRefreshHeaders.get(0));
        var expectedDate =
                parseDate(HeaderUtility.createNextRefreshHeaderValue(nextRefreshSeconds));

        // either both dates have equal millis and thus have the same formatted string ...
        var equalMillisAndEqualString =
                actualDate.getTime() == expectedDate.getTime()
                        && formatDate(actualDate).equals(formatDate(expectedDate));
        // ... or the actual date is before the expected date but at most ten milliseconds earlier
        var actualIsBeforeAndAtMostTenMillis =
                actualDate.before(expectedDate)
                        && expectedDate.getTime() - actualDate.getTime() <= 10L;
        assertTrue(equalMillisAndEqualString || actualIsBeforeAndAtMostTenMillis);
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

    @Test
    void testCreateNextRefreshHeaderValue() {}

    @Test
    void testGetDate() {
        var millis = 0L;
        var expectedDate = new Date(System.currentTimeMillis() + millis);
        var actualDate = HeaderUtility.getDate(millis);
        assertEquals(getSeconds(expectedDate), getSeconds(actualDate));

        millis = -1L;
        expectedDate = new Date(System.currentTimeMillis() + millis);
        actualDate = HeaderUtility.getDate(millis);
        assertEquals(getSeconds(expectedDate), getSeconds(actualDate));

        millis = Long.MAX_VALUE;
        expectedDate = new Date(System.currentTimeMillis() + millis);
        actualDate = HeaderUtility.getDate(millis);
        assertEquals(getSeconds(expectedDate), getSeconds(actualDate));

        millis = Long.MIN_VALUE;
        expectedDate = new Date(System.currentTimeMillis() + millis);
        actualDate = HeaderUtility.getDate(millis);
        assertEquals(getSeconds(expectedDate), getSeconds(actualDate));

        millis = 60 * 60 * 1000L;
        expectedDate = new Date(System.currentTimeMillis() + millis);
        actualDate = HeaderUtility.getDate(millis);
        assertEquals(getSeconds(expectedDate), getSeconds(actualDate));
    }
}
