package ch.ubique.uetlib.core.http.header;

import static ch.ubique.uetlib.core.utils.HttpHeaderUtils.createNextRefreshHeaderValue;

import java.time.Duration;
import org.jspecify.annotations.Nullable;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/** Helper class to generate {@link ResponseEntity} responses and/or request header. */
public final class HeaderUtility {

    public static final String NEXT_REFRESH = "X-Next-Refresh";
    public static final String BACKOFF = "X-Backoff";

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
        return createOKResponseEntity(body, nextRefresh, maxAge, null);
    }

    /**
     * Creates a {@link ResponseEntity<T>} with the given body, HTTP-Status 200, and the max age,
     * next refresh and backoff headers.
     *
     * @param body {@link T} The body of the response
     * @return {@link ResponseEntity<T>} with the given body, headers and status OK
     */
    public static <T> ResponseEntity<T> createOKResponseEntity(
            final T body,
            final Duration nextRefresh,
            final Duration maxAge,
            final @Nullable Duration backoff) {
        final var builder =
                ResponseEntity.ok()
                        .cacheControl(CacheControl.maxAge(maxAge))
                        .header(NEXT_REFRESH, createNextRefreshHeaderValue(nextRefresh));
        if (backoff != null) {
            builder.header(BACKOFF, String.valueOf(backoff.getSeconds()));
        }
        return builder.body(body);
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
        return createNoContentResponseEntity(nextRefresh, maxAge, null);
    }

    /**
     * Returns an empty {@link ResponseEntity<T>} with HTTP-Status 204
     *
     * @return {@link ResponseEntity<T>} with the given headers and status NO_CONTENT
     */
    public static <T extends @Nullable Object> ResponseEntity<T> createNoContentResponseEntity(
            final Duration nextRefresh, final Duration maxAge, final @Nullable Duration backoff) {
        final var builder =
                ResponseEntity.noContent()
                        .cacheControl(CacheControl.maxAge(maxAge))
                        .header(NEXT_REFRESH, createNextRefreshHeaderValue(nextRefresh));
        if (backoff != null) {
            builder.header(BACKOFF, String.valueOf(backoff.getSeconds()));
        }
        return builder.build();
    }

    /**
     * Returns an empty {@link ResponseEntity<T>} with HTTP-Status 304
     *
     * @return {@link ResponseEntity<T>} with the given headers and status NOT_MODIFIED
     */
    public static <T> ResponseEntity<T> createNotModifiedResponseEntity(
            final Duration nextRefresh, final Duration maxAge) {
        return createNotModifiedResponseEntity(nextRefresh, maxAge, null);
    }

    /**
     * Returns an empty {@link ResponseEntity<T>} with HTTP-Status 304
     *
     * @return {@link ResponseEntity<T>} with the given headers and status NOT_MODIFIED
     */
    public static <T> ResponseEntity<T> createNotModifiedResponseEntity(
            final Duration nextRefresh, final Duration maxAge, final @Nullable Duration backoff) {
        final var builder =
                ResponseEntity.status(HttpStatus.NOT_MODIFIED)
                        .header(NEXT_REFRESH, createNextRefreshHeaderValue(nextRefresh))
                        .cacheControl(CacheControl.maxAge(maxAge));
        if (backoff != null) {
            builder.header(BACKOFF, String.valueOf(backoff.getSeconds()));
        }
        return builder.build();
    }
}
