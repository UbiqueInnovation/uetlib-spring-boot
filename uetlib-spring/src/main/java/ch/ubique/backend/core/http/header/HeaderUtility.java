package ch.ubique.backend.core.http.header;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/** Helper class to generate {@link ResponseEntity} responses and/or request header. */
public class HeaderUtility {

    public static final String CACHE_CONTROL = "Cache-Control";
    public static final String NEXT_REFRESH = "X-Next-Refresh";
    public static final String MAX_AGE = "max-age";
    /** format Date as String or parse to Date (package-private to enable tests) */
    static final ThreadLocal<SimpleDateFormat> headerDateFormatter = new ThreadLocal<>();

    private static final Logger logger = LoggerFactory.getLogger(HeaderUtility.class);

    // only static
    private HeaderUtility() {}

    /**
     * Create a {@link ResponseEntity<T>} with HTTP-Status 404, not found. Does not contain any body
     * data.
     *
     * @return {@link ResponseEntity<T>}
     */
    public static <T> ResponseEntity<T> createNotFoundResponseEntity() {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Creates a {@link ResponseEntity<T>} with the given body, HTTP-Status 200, and the max age and
     * next refresh headers.
     *
     * @param body {@link T} The body of the response
     * @param maxAgeSeconds max-age value in seconds
     * @param nextRefreshSeconds X-Next-Refresh value in seconds
     * @return {@link ResponseEntity<T>} with the given body, headers and status OK
     */
    public static <T> ResponseEntity<T> createOKResponseEntity(
            T body, int nextRefreshSeconds, int maxAgeSeconds) {
        return new ResponseEntity<>(
                body, createHeaders(maxAgeSeconds, nextRefreshSeconds), HttpStatus.OK);
    }

    /**
     * Creates a {@link ResponseEntity<T>} with the given body and HTTP-Status 200.
     *
     * @param body {@link T} The body of the response
     * @return {@link ResponseEntity<T>} with the given body, headers and status OK
     */
    public static <T> ResponseEntity<T> createOKResponseEntity(T body) {
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    /**
     * Creates a {@link ResponseEntity<T>} with the given body and HTTP-Status 200 and
     * 'content-range' header.
     *
     * @param body T The body of the response
     * @return {@link ResponseEntity<T>} with the given body, headers and status OK
     */
    public static <T> ResponseEntity<T> createOKContentRangeResponseEntity(
            T body, int perPage, long totalElements) {
        return ResponseEntity.ok().header("content-range", "$perPage/$totalElements").body(body);
    }

    /**
     * Creates a {@link ResponseEntity<T>} with the given body and HTTP-Status 500.
     *
     * @param body {@link T} The body of the response
     * @return {@link ResponseEntity<T>} with the given body, headers and status
     *     INTERNAL_SERVER_ERROR
     */
    public static <T> ResponseEntity<T> createInternalServerErrorResponseEntity(T body) {
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Creates an empty {@link ResponseEntity<T>} with HTTP-Status 500.
     *
     * @return {@link ResponseEntity<T>} with status INTERNAL_SERVER_ERROR
     */
    public static <T> ResponseEntity<T> createInternalServerErrorResponseEntity() {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Creates a {@link ResponseEntity<T>} with the given body and HTTP-Status 400.
     *
     * @param body [T] The body of the response
     * @return [ResponseEntity] with the given body, headers and status 400
     */
    public static <T> ResponseEntity<T> createBadRequestResponseEntity(T body) {
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Creates an empty {@link ResponseEntity<T>} with HTTP-Status 503.
     *
     * @return {@link ResponseEntity<T>} with the given body, headers and status SERVICE_UNAVAILABLE
     */
    public static <T> ResponseEntity<T> createServiceUnavailableResponseEntity() {
        return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * Returns an empty {@link ResponseEntity<T>} with HTTP-Status 204
     *
     * @param nextRefreshSeconds X-Next-Refresh value in seconds
     * @param maxAgeSeconds max-age value in seconds
     * @return {@link ResponseEntity<T>} with the given headers and status NO_CONTENT
     */
    public static <T> ResponseEntity<T> createNoContentResponseEntity(
            int nextRefreshSeconds, int maxAgeSeconds) {
        final var headers = createHeaders(maxAgeSeconds, nextRefreshSeconds);
        return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
    }

    /**
     * Sets the following two headers:
     *
     * <ul>
     *   <li>Cache-Control: max-age=maxAgeSeconds
     *   <li>X-Next-Refresh: now + nextRefreshSeconds
     * </ul>
     *
     * @param response to set header values on
     * @param nextRefreshSeconds X-Next-Refresh value in seconds
     * @param maxAgeSeconds max-age value in seconds
     */
    public static void setHeaders(
            HttpServletResponse response, int maxAgeSeconds, int nextRefreshSeconds) {
        response.setHeader(CACHE_CONTROL, createMaxAgeHeaderValue(maxAgeSeconds));
        response.setHeader(NEXT_REFRESH, createNextRefreshHeaderValue(nextRefreshSeconds));
    }

    /**
     * Creates headers with the following two entries:
     *
     * <ul>
     *   <li>Cache-Control: max-age=maxAgeSeconds
     *   <li>X-Next-Refresh: now + nextRefreshSeconds
     * </ul>
     *
     * @param maxAgeSeconds max-age value in seconds
     * @param nextRefreshSeconds X-Next-Refresh value in seconds
     * @return {@link HttpHeaders} with the headers set
     */
    public static HttpHeaders createHeaders(int maxAgeSeconds, int nextRefreshSeconds) {
        var headers = new HttpHeaders();
        headers.add(CACHE_CONTROL, createMaxAgeHeaderValue(maxAgeSeconds));
        headers.add(NEXT_REFRESH, createNextRefreshHeaderValue(nextRefreshSeconds));
        return headers;
    }

    /**
     * Creates the max-age header value with the given max-age in seconds (package-private to enable
     * tests)
     *
     * @param maxAgeSeconds max-age value in seconds
     * @return max-age string
     */
    static String createMaxAgeHeaderValue(int maxAgeSeconds) {
        return MAX_AGE + "=" + maxAgeSeconds;
    }

    /**
     * return the header value for X-Next-Refresh (package-private to enable tests)
     *
     * @param nextRefreshSeconds X-Next-Refresh value in seconds
     * @return X-Next-Refresh string
     */
    static String createNextRefreshHeaderValue(int nextRefreshSeconds) {
        return formatHeaderDate(getDate(nextRefreshSeconds * 1000L));
    }

    /**
     * Formats the date to a http timestamp. (package-private to enable tests)
     *
     * @param date to format
     * @return formatted date string
     */
    static String formatHeaderDate(Date date) {
        var sdf = headerDateFormatter.get();
        if (sdf == null) {
            sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            headerDateFormatter.set(sdf);
        }
        return sdf.format(date);
    }

    /**
     * Returns the date from now plus the given number of milliseconds. (package-private to enable
     * tests)
     *
     * @param millis Millisecond offset from now
     * @return {@link Date} with offset millis from now
     */
    static Date getDate(long millis) {
        return new Date(System.currentTimeMillis() + millis);
    }
}
