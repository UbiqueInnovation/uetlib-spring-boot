package ch.ubique.backend.core.http.header;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/** Helper class to generate {@link ResponseEntity} responses and/or request header. */
public class HeaderUtility {

    public static final String CACHE_CONTROL = "Cache-Control";
    public static final String NEXT_REFRESH = "X-Next-Refresh";
    public static final String CONTENT_RANGE = "Content-Range";
    public static final String MAX_AGE = "max-age";
    /** format Date as String or parse to Date (package-private to enable tests) */
    static final ThreadLocal<SimpleDateFormat> headerDateFormatter = new ThreadLocal<>();

    private HeaderUtility() {}

    /**
     * Create a {@link ResponseEntity<T>} with HTTP-Status 404, not found. Does not contain any body
     * data.
     *
     * @return {@link ResponseEntity<T>}
     */
    public static <T> ResponseEntity<T> createNotFoundResponseEntity() {
        return ResponseEntity.notFound().build();
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
        return ResponseEntity.ok()
                .header(CACHE_CONTROL, createMaxAgeHeaderValue(maxAgeSeconds))
                .header(NEXT_REFRESH, createNextRefreshHeaderValue(nextRefreshSeconds))
                .body(body);
    }

    /**
     * Creates a {@link ResponseEntity<T>} with the given body and HTTP-Status 200.
     *
     * @param body {@link T} The body of the response
     * @return {@link ResponseEntity<T>} with the given body, headers and status OK
     */
    public static <T> ResponseEntity<T> createOKResponseEntity(T body) {
        return ResponseEntity.ok(body);
    }

    /**
     * Creates a {@link ResponseEntity<T>} with the given body and HTTP-Status 200 and
     * 'Content-Range' header.
     *
     * @param body T The body of the response
     * @return {@link ResponseEntity<T>} with the given body, headers and status OK
     */
    public static <T> ResponseEntity<T> createOKContentRangeResponseEntity(
            T body, int perPage, long totalElements) {
        return ResponseEntity.ok().header(CONTENT_RANGE, perPage + "/" + totalElements).body(body);
    }

    /**
     * Creates a {@link ResponseEntity<T>} with the given body and HTTP-Status 500.
     *
     * @param body {@link T} The body of the response
     * @return {@link ResponseEntity<T>} with the given body, headers and status
     *     INTERNAL_SERVER_ERROR
     */
    public static <T> ResponseEntity<T> createInternalServerErrorResponseEntity(T body) {
        return ResponseEntity.internalServerError().body(body);
    }

    /**
     * Creates an empty {@link ResponseEntity<T>} with HTTP-Status 500.
     *
     * @return {@link ResponseEntity<T>} with status INTERNAL_SERVER_ERROR
     */
    public static <T> ResponseEntity<T> createInternalServerErrorResponseEntity() {
        return ResponseEntity.internalServerError().build();
    }

    /**
     * Creates a {@link ResponseEntity<T>} with the given body and HTTP-Status 400.
     *
     * @param body [T] The body of the response
     * @return [ResponseEntity] with the given body, headers and status 400
     */
    public static <T> ResponseEntity<T> createBadRequestResponseEntity(T body) {
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Creates an empty {@link ResponseEntity<T>} with HTTP-Status 503.
     *
     * @return {@link ResponseEntity<T>} with the given body, headers and status SERVICE_UNAVAILABLE
     */
    public static <T> ResponseEntity<T> createServiceUnavailableResponseEntity() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
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
        return ResponseEntity.noContent()
                .header(CACHE_CONTROL, createMaxAgeHeaderValue(maxAgeSeconds))
                .header(NEXT_REFRESH, createNextRefreshHeaderValue(nextRefreshSeconds))
                .build();
    }

    /**
     * Creates the max-age header value with the given max-age in seconds (package-private to enable
     * tests)
     *
     * @param maxAgeSeconds max-age value in seconds
     * @return max-age string
     */
    public static String createMaxAgeHeaderValue(int maxAgeSeconds) {
        return MAX_AGE + "=" + maxAgeSeconds;
    }

    /**
     * return the header value for X-Next-Refresh (package-private to enable tests)
     *
     * @param nextRefreshSeconds X-Next-Refresh value in seconds
     * @return X-Next-Refresh string
     */
    public static String createNextRefreshHeaderValue(int nextRefreshSeconds) {
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
