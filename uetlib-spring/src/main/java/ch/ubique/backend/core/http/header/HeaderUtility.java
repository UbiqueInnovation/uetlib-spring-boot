package ch.ubique.backend.core.http.header;

import static ch.ubique.backend.core.utils.TimeUtils.TIME_UTILS_UTC;

import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

/** Helper class to generate {@link ResponseEntity} responses and/or request header. */
public class HeaderUtility {

    /**
     * @deprecated since v1.5, use {@link org.springframework.http.HttpHeaders#CACHE_CONTROL} or set
     *     directly in {@link ResponseEntity} using {@code
     *     ResponseEntity.ok().cacheControl(CacheControl.maxAge(Duration.ofSeconds(maxAgeSeconds))).body(body)}
     *     or similar.
     */
    @Deprecated(since = "1.5", forRemoval = true)
    public static final String CACHE_CONTROL = "Cache-Control";

    public static final String NEXT_REFRESH = "X-Next-Refresh";
    public static final String CONTENT_RANGE = "Content-Range";

    /**
     * @deprecated since v1.5, use {@link CacheControl#maxAge(Duration)} or set directly in {@link
     *     ResponseEntity} using {@code
     *     ResponseEntity.ok().cacheControl(CacheControl.maxAge(Duration.ofSeconds(maxAgeSeconds))).body(body)}
     *     or similar.
     */
    @Deprecated(since = "1.5", forRemoval = true)
    public static final String MAX_AGE = "max-age";

    private static final DateTimeFormatter HTTP_HEADER_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z", Locale.US)
                    .withZone(ZoneId.of("UTC"));

    private HeaderUtility() {}

    /**
     * @deprecated since 1.5, use {@link #createOKResponseEntity(T, Duration, Duration)}
     */
    @Deprecated(since = "1.5", forRemoval = true)
    public static <T> ResponseEntity<T> createOKResponseEntity(
            final T body, final int nextRefreshSeconds, final int maxAgeSeconds) {
        return createOKResponseEntity(
                body, Duration.ofSeconds(nextRefreshSeconds), Duration.ofSeconds(maxAgeSeconds));
    }

    /**
     * Creates a {@link ResponseEntity<T>} with the given body, HTTP-Status 200, and the max age and
     * next refresh headers.
     *
     * @param body {@link T} The body of the response
     * @return {@link ResponseEntity<T>} with the given body, headers and status OK
     */
    public static <T> ResponseEntity<T> createOKResponseEntity(
            final T body, final Duration nextRefresh, final Duration maxAge) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(maxAge))
                .header(NEXT_REFRESH, createNextRefreshHeaderValue(nextRefresh))
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
     * @deprecated since 1.5, use {@link #createNoContentResponseEntity(Duration, Duration)}
     */
    @Deprecated(since = "1.5", forRemoval = true)
    public static <T> ResponseEntity<T> createNoContentResponseEntity(
            final int nextRefreshSeconds, final int maxAgeSeconds) {
        return createNoContentResponseEntity(
                Duration.ofSeconds(nextRefreshSeconds), Duration.ofSeconds(maxAgeSeconds));
    }

    /**
     * Returns an empty {@link ResponseEntity<T>} with HTTP-Status 204
     *
     * @return {@link ResponseEntity<T>} with the given headers and status NO_CONTENT
     */
    public static <T> ResponseEntity<T> createNoContentResponseEntity(
            final Duration nextRefresh, final Duration maxAge) {
        return ResponseEntity.noContent()
                .cacheControl(CacheControl.maxAge(maxAge))
                .header(NEXT_REFRESH, createNextRefreshHeaderValue(nextRefresh))
                .build();
    }

    /**
     * @deprecated since v1.5, use {@link CacheControl#maxAge(Duration)} or set directly in {@link
     *     ResponseEntity} using {@code
     *     ResponseEntity.ok().cacheControl(CacheControl.maxAge(Duration.ofSeconds(maxAgeSeconds))).body(body)}
     *     or similar.
     */
    @Deprecated(since = "1.5", forRemoval = true)
    public static String createMaxAgeHeaderValue(final int maxAgeSeconds) {
        return MAX_AGE + "=" + maxAgeSeconds;
    }

    /**
     * @deprecated since 1.5, use {@link #createNextRefreshHeaderValue(Duration)}
     */
    @Deprecated(since = "1.5", forRemoval = true)
    public static String createNextRefreshHeaderValue(final int nextRefreshSeconds) {
        return createNextRefreshHeaderValue(Duration.ofSeconds(nextRefreshSeconds));
    }

    /** return the header value for X-Next-Refresh (package-private to enable tests) */
    public static String createNextRefreshHeaderValue(final Duration nextRefresh) {
        return formatHttpHeaderDate(TIME_UTILS_UTC.now().plus(nextRefresh));
    }

    /**
     * return the given date format for HTTP headers
     *
     * @param temporalAccessor date to format
     * @return formatted date
     */
    public static String formatHttpHeaderDate(final TemporalAccessor temporalAccessor) {
        return HTTP_HEADER_DATE_FORMATTER.format(temporalAccessor);
    }
}
