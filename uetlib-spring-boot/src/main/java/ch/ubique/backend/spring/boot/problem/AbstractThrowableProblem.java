package ch.ubique.backend.spring.boot.problem;

import ch.ubique.backend.spring.boot.problem.errortype.ProblemErrorType;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractThrowableProblem extends RuntimeException
        implements BaseThrowableProblem {

    @NonNull private final ProblemErrorType errorType;

    @NonNull private final HttpStatusCode status;
    @NonNull private final String title;
    @NonNull private final String detail;
    @Nullable private final URI instance;

    protected AbstractThrowableProblem(
            @NonNull final ProblemErrorType errorType,
            @NonNull final String title,
            @NonNull final HttpStatus status) {
        this(errorType, title, status, status.getReasonPhrase());
    }

    protected AbstractThrowableProblem(
            @NonNull final ProblemErrorType errorType,
            @NonNull final String title,
            @NonNull final HttpStatusCode status,
            @NonNull final String detail) {
        this(errorType, title, status, detail, null);
    }

    protected AbstractThrowableProblem(
            @NonNull final ProblemErrorType errorType,
            @NonNull final String title,
            @NonNull final HttpStatusCode status,
            @NonNull final String detail,
            @Nullable final URI instance) {
        this(errorType, title, status, detail, instance, null);
    }

    protected AbstractThrowableProblem(
            @NonNull final ProblemErrorType errorType,
            @NonNull final String title,
            @NonNull final HttpStatusCode status,
            @NonNull final String detail,
            @Nullable final URI instance,
            @Nullable final AbstractThrowableProblem cause) {
        super(cause);
        this.errorType = errorType;
        this.title = title;
        this.status = status;
        this.detail = detail;
        this.instance = instance;
    }

    /**
     * In implementing classes, you can override this method to return the actual error type by
     * casting. This will always succeed since the only way to set the error type is via our own
     * constructor:
     *
     * <p>
     *
     * {@snippet lang = java:
     * @Override
     * public @NonNull MyProblemErrorType errorType() {
     *     return (MyProblemErrorType) super.errorType();
     * }
     * }
     *
     * @return
     */
    @Override
    public @NonNull ProblemErrorType errorType() {
        return errorType;
    }

    @Override
    public @NonNull URI type() {
        return errorType.type();
    }

    @Override
    public @NonNull HttpStatusCode status() {
        return status;
    }

    @Override
    public @NonNull String title() {
        return title;
    }

    @Override
    public @NonNull String detail() {
        return detail;
    }

    @Override
    public @NonNull String problemCode() {
        return errorType.problemCode();
    }

    public @NonNull Map<@NonNull String, @NonNull Object> additionalProperties() {
        return Map.of();
    }

    @JsonIgnore
    public ProblemDetail toProblemDetail(
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
        problemDetail.setProperties(Map.copyOf(properties));

        return problemDetail;
    }

    @Override
    @JsonIgnore
    public String getMessage() {
        return String.join(": ", title, detail);
    }

    @Override
    public boolean equals(final Object o) {
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
                .append("type", errorType.type())
                .append("status", status)
                .append("title", title)
                .append("detail", detail)
                .append("instance", instance)
                .append("problemCode", errorType.problemCode())
                .toString();
    }

    public Object @Nullable [] localizedTitleArgs() {
        return new Object[0];
    }

    public Object @Nullable [] localizedDetailArgs() {
        return new Object[0];
    }
}
