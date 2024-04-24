package ch.ubique.backend.core.http.header;

import static ch.ubique.backend.core.utils.TimeUtils.TIME_UTILS_UTC;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

/** Helper class to generate {@link ResponseEntity} responses and/or request header. */
public class HeaderUtility {

    public static final String NEXT_REFRESH = "X-Next-Refresh";

    public static final String CONTENT_RANGE = "Content-Range";

    private static final DateTimeFormatter HTTP_HEADER_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z", Locale.US)
                    .withZone(ZoneId.of("UTC"));

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

    /**
     * Parse Retry-After HTTP Header which can either be a date in format of {@link
     * #HTTP_HEADER_DATE_FORMATTER} or a deltaSeconds offset from now
     *
     * @param dateOrOffsetHeaderValue date to parse as date or seconds offset from now
     * @param offsetStart offset start to add offset seconds in the case of the header representing
     *     a seconds offset
     * @return parse date
     */
    public static ZonedDateTime parseHttpDateOrOffsetHeader(
            final String dateOrOffsetHeaderValue, final ZonedDateTime offsetStart) {
        if (NumberUtils.isParsable(dateOrOffsetHeaderValue)) {
            final var deltaSeconds = Long.parseLong(dateOrOffsetHeaderValue);
            return offsetStart.plusSeconds(deltaSeconds);
        }
        return ZonedDateTime.parse(dateOrOffsetHeaderValue, HTTP_HEADER_DATE_FORMATTER);
    }
}
