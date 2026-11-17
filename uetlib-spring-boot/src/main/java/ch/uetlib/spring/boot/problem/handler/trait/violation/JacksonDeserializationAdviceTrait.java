package ch.uetlib.spring.boot.problem.handler.trait.violation;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.context.request.WebRequest;

import tools.jackson.databind.exc.IgnoredPropertyException;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.InvalidNullException;
import tools.jackson.databind.exc.InvalidTypeIdException;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.exc.UnrecognizedPropertyException;

import java.net.URI;
import java.net.URL;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAmount;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Converts Jackson request binding errors into field violations without exposing parser details.
 */
@NullMarked
public interface JacksonDeserializationAdviceTrait extends ViolationProblemDetailTrait {

    /**
     * Handles Jackson field binding errors as violations and preserves the generic Spring response
     * for malformed JSON or errors without a field path.
     */
    default @Nullable ResponseEntity<Object> handleHttpMessageNotReadable(
            final HttpMessageNotReadableException ex,
            final HttpHeaders headers,
            final HttpStatusCode status,
            final WebRequest request) {

        final var includeConstraintViolations =
                getUetlibSpringBootProblemProperties().isIncludeConstraintViolations();

        if (includeConstraintViolations
                && ex.getCause() instanceof final MismatchedInputException mismatch) {
            final var violation = createDeserializationViolation(mismatch);
            if (violation != null) {
                return newConstraintViolationProblemDetail(
                        ex, headers, status, Stream.of(violation), request);
            }
        }
        final var body =
                createProblemDetail(ex, status, "Failed to read request", null, null, request);
        return handleExceptionInternal(ex, body, headers, status, request);
    }

    default @Nullable Violation createDeserializationViolation(
            final MismatchedInputException mismatch) {
        final var fieldName = new StringBuilder();
        for (final var reference : mismatch.getPath()) {
            final var property = reference.getPropertyName();
            if (property != null) {
                if (!fieldName.isEmpty()) {
                    fieldName.append('.');
                }
                fieldName.append(property);
            } else if (reference.getIndex() >= 0) {
                fieldName.append('[').append(reference.getIndex()).append(']');
            }
        }
        return fieldName.isEmpty()
                ? null
                : new Violation(fieldName.toString(), deserializationMessage(mismatch));
    }

    default String deserializationMessage(final MismatchedInputException mismatch) {
        return switch (mismatch) {
            // Jackson exposes no dedicated exception type for missing required properties.
            case final MismatchedInputException missing
                    when missing.getOriginalMessage()
                            .startsWith("Missing required creator property") ->
                    "required property is missing";
            case final InvalidNullException _ -> "must not be null";
            case final UnrecognizedPropertyException _ -> "unknown property";
            case final IgnoredPropertyException _ -> "property must not be provided";
            case final InvalidTypeIdException typeId ->
                    typeId.getTypeId() == null
                            ? "type discriminator is missing"
                            : "invalid type discriminator";
            default ->
                    deserializationTypeMessage(
                            mismatch.getTargetType(), mismatch instanceof InvalidFormatException);
        };
    }

    private static String deserializationTypeMessage(
            final @Nullable Class<?> targetType, final boolean invalidFormat) {
        return switch (targetType) {
            case final Class<?> type when type.isEnum() ->
                    "must be one of "
                            + Arrays.stream(type.getEnumConstants())
                                    .map(value -> ((Enum<?>) value).name())
                                    .toList();
            case final Class<?> type
                    when invalidFormat
                            && (TemporalAccessor.class.isAssignableFrom(type)
                                    || Date.class.isAssignableFrom(type)
                                    || Calendar.class.isAssignableFrom(type)) ->
                    "must be a valid date and time";
            case final Class<?> type
                    when invalidFormat && TemporalAmount.class.isAssignableFrom(type) ->
                    "must be a valid duration or period";
            case final Class<?> type when invalidFormat && ZoneId.class.isAssignableFrom(type) ->
                    "must be a valid time zone";
            case final Class<?> type when invalidFormat && type == UUID.class ->
                    "must be a valid UUID";
            case final Class<?> type when invalidFormat && type == URI.class ->
                    "must be a valid URI";
            case final Class<?> type when invalidFormat && type == URL.class ->
                    "must be a valid URL";
            case final Class<?> type when invalidFormat && type == byte[].class ->
                    "must be a valid Base64 string";
            case final Class<?> type
                    when type.isArray() || Collection.class.isAssignableFrom(type) ->
                    "must be an array";
            case final Class<?> type when Map.class.isAssignableFrom(type) -> "must be an object";
            case final Class<?> type when type == Character.class || type == char.class ->
                    "must be a single character";
            case final Class<?> type when type == String.class -> "must be a string";
            case final Class<?> type when type == Boolean.class || type == boolean.class ->
                    "must be a boolean";
            case final Class<?> type
                    when Number.class.isAssignableFrom(type)
                            || type == byte.class
                            || type == short.class
                            || type == int.class
                            || type == long.class
                            || type == float.class
                            || type == double.class ->
                    "must be a number";
            case null, default -> "invalid value";
        };
    }
}
