package ch.ubique.backend.spring.boot.problem;

import static java.util.Map.entry;
import static java.util.Map.ofEntries;

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

public abstract class AbstractUetlibThrowableProblem extends RuntimeException
        implements BaseUetlibThrowableProblem {

    @NonNull private final URI type;
    @NonNull private final HttpStatusCode status;
    @NonNull private final String title;
    @NonNull private final String detail;
    @Nullable private final URI instance;

    protected AbstractUetlibThrowableProblem(
            @NonNull final URI type,
            @NonNull final String title,
            @NonNull final HttpStatus status) {
        this(type, title, status, status.getReasonPhrase());
    }

    protected AbstractUetlibThrowableProblem(
            @NonNull final URI type,
            @NonNull final String title,
            @NonNull final HttpStatusCode status,
            @NonNull final String detail) {
        this(type, title, status, detail, null);
    }

    protected AbstractUetlibThrowableProblem(
            @NonNull final URI type,
            @NonNull final String title,
            @NonNull final HttpStatusCode status,
            @NonNull final String detail,
            @Nullable final URI instance) {
        this(type, title, status, detail, instance, null);
    }

    protected AbstractUetlibThrowableProblem(
            @NonNull final URI type,
            @NonNull final String title,
            @NonNull final HttpStatusCode status,
            @NonNull final String detail,
            @Nullable final URI instance,
            @Nullable final AbstractUetlibThrowableProblem cause) {
        super(cause);
        this.type = type;
        this.title = title;
        this.status = status;
        this.detail = detail;
        this.instance = instance;
    }

    @Override
    public @NonNull URI type() {
        return type;
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

    @JsonIgnore
    public ProblemDetail toProblemDetail() {
        final var problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setType(type);
        problemDetail.setTitle(title);
        problemDetail.setDetail(detail);
        problemDetail.setInstance(instance);
        problemDetail.setProperties(
                ofEntries(
                        entry("errorType", errorType().name()),
                        entry("errorCode", errorType().code())));
        return problemDetail;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof final AbstractUetlibThrowableProblem that)) {
            return false;
        }

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(type, that.type)
                .append(status, that.status)
                .append(title, that.title)
                .append(detail, that.detail)
                .append(instance, that.instance)
                .append(errorType(), that.errorType())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(type)
                .append(status)
                .append(title)
                .append(detail)
                .append(instance)
                .append(errorType())
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .appendSuper(super.toString())
                .append("type", type)
                .append("status", status)
                .append("title", title)
                .append("detail", detail)
                .append("instance", instance)
                .append("errorType", errorType())
                .append("errorCode", errorType().code())
                .toString();
    }
}
