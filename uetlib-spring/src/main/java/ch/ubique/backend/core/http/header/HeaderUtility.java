package ch.ubique.backend.core.http.header;

import static ch.ubique.backend.core.utils.TimeUtils.TIME_UTILS_UTC;

import org.springframework.http.ResponseEntity;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Helper class to generate {@link ResponseEntity} responses and/or request header. */
public class HeaderUtility {

    public static final String CACHE_CONTROL = "Cache-Control";
    public static final String NEXT_REFRESH = "X-Next-Refresh";
    public static final String CONTENT_RANGE = "Content-Range";
    public static final String MAX_AGE = "max-age";

    private static final DateTimeFormatter HEADER_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z", Locale.US)
                    .withZone(ZoneId.of("UTC"));

    private HeaderUtility() {}

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
            final T body, final int nextRefreshSeconds, final int maxAgeSeconds) {
        return ResponseEntity.ok()
                .header(CACHE_CONTROL, createMaxAgeHeaderValue(maxAgeSeconds))
                .header(NEXT_REFRESH, createNextRefreshHeaderValue(nextRefreshSeconds))
                .body(body);
    }

    /**
     * Creates a {@link ResponseEntity<T>} with the given body and HTTP-Status 200 and
     * 'Content-Range' header.
     *
     * @param body T The body of the response
     * @return {@link ResponseEntity<T>} with the given body, headers and status OK
     */
    public static <T> ResponseEntity<T> createOKContentRangeResponseEntity(
            final T body, final int perPage, final long totalElements) {
        return ResponseEntity.ok().header(CONTENT_RANGE, perPage + "/" + totalElements).body(body);
    }

    /**
     * Returns an empty {@link ResponseEntity<T>} with HTTP-Status 204
     *
     * @param nextRefreshSeconds X-Next-Refresh value in seconds
     * @param maxAgeSeconds max-age value in seconds
     * @return {@link ResponseEntity<T>} with the given headers and status NO_CONTENT
     */
    public static <T> ResponseEntity<T> createNoContentResponseEntity(
            final int nextRefreshSeconds, final int maxAgeSeconds) {
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
    public static String createMaxAgeHeaderValue(final int maxAgeSeconds) {
        return MAX_AGE + "=" + maxAgeSeconds;
    }

    /**
     * return the header value for X-Next-Refresh (package-private to enable tests)
     *
     * @param nextRefreshSeconds X-Next-Refresh value in seconds
     * @return X-Next-Refresh string
     */
    public static String createNextRefreshHeaderValue(final int nextRefreshSeconds) {
        return formatHttpHeaderDate(TIME_UTILS_UTC.now().plusSeconds(nextRefreshSeconds));
    }

    /**
     * return the given date format for HTTP headers
     *
     * @param zonedDateTime date to format
     * @return formatted date
     */
    public static String formatHttpHeaderDate(final ZonedDateTime zonedDateTime) {
        return HEADER_DATE_FORMATTER.format(zonedDateTime);
    }
}
