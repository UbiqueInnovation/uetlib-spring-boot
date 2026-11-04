package ch.uetlib.spring.boot.problem;

import ch.uetlib.spring.boot.problem.errortype.ProblemErrorType;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractThrowableProblem extends RuntimeException
        implements BaseThrowableProblem {

    private final ProblemErrorType errorType;

    private final HttpStatusCode status;
    private final String title;
    private final String detail;
    @Nullable private final URI instance;
    @Nullable private final Boolean retryable;

    protected AbstractThrowableProblem(
            final ProblemErrorType errorType, final String title, final HttpStatus status) {
        this(errorType, title, status, status.getReasonPhrase());
    }

    protected AbstractThrowableProblem(
            final ProblemErrorType errorType,
            final String title,
            final HttpStatusCode status,
            final String detail) {
        this(errorType, title, status, detail, null);
    }

    protected AbstractThrowableProblem(
            final ProblemErrorType errorType,
            final String title,
            final HttpStatusCode status,
            final String detail,
            @Nullable final URI instance) {
        this(errorType, title, status, detail, instance, null);
    }

    protected AbstractThrowableProblem(
            final ProblemErrorType errorType,
            final String title,
            final HttpStatusCode status,
            final String detail,
            @Nullable final URI instance,
            @Nullable final AbstractThrowableProblem cause) {
        this(errorType, title, status, detail, instance, cause, null);
    }

    protected AbstractThrowableProblem(
            final ProblemErrorType errorType,
            final String title,
            final HttpStatusCode status,
            final String detail,
            @Nullable final URI instance,
            @Nullable final AbstractThrowableProblem cause,
            @Nullable final Boolean retryable) {
        super(cause);
        this.errorType = errorType;
        this.title = title;
        this.status = status;
        this.detail = detail;
        this.instance = instance;
        this.retryable = retryable;
    }

    /**
     * In implementing classes, you can override this method to return the actual error type by
     * casting or just cast in usages. This will always succeed since the only way to set the error
     * type is via our own constructor.
     */
    @Override
    public final ProblemErrorType errorType() {
        return errorType;
    }

    @Override
    public final HttpStatusCode status() {
        return status;
    }

    @Override
    public final String title() {
        return title;
    }

    @Override
    public final String detail() {
        return detail;
    }

    @Override
    @Nullable
    public final URI instance() {
        return instance;
    }

    /**
     * Override this method to add properties for your specific {@link ProblemDetail} to the
     * generated in {@link #toProblemDetail(String, String)} and returned to a client.
     */
    public Map<String, Object> additionalProperties() {
        return Map.of();
    }

    @JsonIgnore
    public final ProblemDetail toProblemDetail(
            final @Nullable String localizedTitle, final @Nullable String localizedDetail) {
        final var problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setType(errorType.type());
        problemDetail.setTitle(title);
        problemDetail.setDetail(detail);
        problemDetail.setInstance(instance);

        final var properties = new HashMap<>(additionalProperties());
        properties.put("problemCode", errorType.problemCode());
        if (localizedTitle != null) {
            properties.put("localizedTitle", localizedTitle);
        }
        if (localizedDetail != null) {
            properties.put("localizedDetail", localizedDetail);
        }
        if (retryable != null) {
            properties.put("retryable", retryable);
        }
        problemDetail.setProperties(Map.copyOf(properties));

        return problemDetail;
    }

    @Override
    @JsonIgnore
    public String getMessage() {
        return title + ": " + detail;
    }

    @Override
    public boolean equals(final @Nullable Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof final AbstractThrowableProblem that)) {
            return false;
        }

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(errorType, that.errorType)
                .append(status, that.status)
                .append(title, that.title)
                .append(detail, that.detail)
                .append(instance, that.instance)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(errorType)
                .append(status)
                .append(title)
                .append(detail)
                .append(instance)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .appendSuper(super.toString())
                .append("errorType", errorType)
                .append("status", status)
                .append("title", title)
                .append("detail", detail)
                .append("instance", instance)
                .toString();
    }

    public Object[] localizedTitleArgs() {
        return new Object[0];
    }

    public Object[] localizedDetailArgs() {
        return new Object[0];
    }
}
