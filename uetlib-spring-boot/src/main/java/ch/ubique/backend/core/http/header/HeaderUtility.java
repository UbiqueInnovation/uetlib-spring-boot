package ch.ubique.backend.core.http.header;

import ch.ubique.backend.core.utils.HttpHeaderUtils;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;

/** Helper class to generate {@link ResponseEntity} responses and/or request header. */
public final class HeaderUtility {

    public static final String NEXT_REFRESH = "X-Next-Refresh";

    public static final String CONTENT_RANGE = "Content-Range";

    private HeaderUtility() {}

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
     * Returns an empty {@link ResponseEntity<T>} with HTTP-Status 304
     *
     * @return {@link ResponseEntity<T>} with the given headers and status NOT_MODIFIED
     */
    public static <T> ResponseEntity<T> createNotModifiedResponseEntity(
            final Duration nextRefresh, final Duration maxAge) {
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
                .header(NEXT_REFRESH, createNextRefreshHeaderValue(nextRefresh))
                .cacheControl(CacheControl.maxAge(maxAge))
                .build();
    }

    /**
     * @deprecated since 1.5, will be removed in 2.0. Use {@link
     *     HttpHeaderUtils#createNextRefreshHeaderValue(Duration)} in uetlib-core >= 1.3.3
     */
    @Deprecated(since = "1.5", forRemoval = true)
    public static String createNextRefreshHeaderValue(final Duration nextRefresh) {
        return HttpHeaderUtils.createNextRefreshHeaderValue(nextRefresh);
    }

    /**
     * @deprecated since 1.5, will be removed in 2.0. Use {@link
     *     HttpHeaderUtils#formatHttpHeaderDate(Temporal)} in uetlib-core >= 1.3.3
     */
    @Deprecated(since = "1.5", forRemoval = true)
    public static String formatHttpHeaderDate(final Temporal temporal) {
        return HttpHeaderUtils.formatHttpHeaderDate(temporal);
    }

    /**
     * @deprecated since 1.5, will be removed in 2.0. Use {@link
     *     HttpHeaderUtils#parseHttpDateOrOffsetHeader(String, ZonedDateTime)} in uetlib-core >=
     *     1.3.3
     */
    @Deprecated(since = "1.5", forRemoval = true)
    public static ZonedDateTime parseHttpDateOrOffsetHeader(
            final String dateOrOffsetHeaderValue, final ZonedDateTime offsetStart) {
        return HttpHeaderUtils.parseHttpDateOrOffsetHeader(dateOrOffsetHeaderValue, offsetStart);
    }
}
