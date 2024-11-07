package ch.ubique.backend.problem.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.net.URI;
import java.util.Map;

public class ProblemDetailBuilder {

    private HttpStatus status;
    private URI type;
    private String title;
    private String detail;
    private URI instance;
    private Map<String, Object> properties;

    public ProblemDetailBuilder status(final HttpStatus status) {
        this.status = status;
        return this;
    }

    public ProblemDetailBuilder type(final URI type) {
        this.type = type;
        return this;
    }

    public ProblemDetailBuilder title(final String title) {
        this.title = title;
        return this;
    }

    public ProblemDetailBuilder detail(final String detail) {
        this.detail = detail;
        return this;
    }

    public ProblemDetailBuilder instance(final URI instance) {
        this.instance = instance;
        return this;
    }

    public ProblemDetailBuilder properties(final Map<String, Object> properties) {
        this.properties = properties;
        return this;
    }

    public ProblemDetail build() {
        final var problemDetail = ProblemDetail.forStatus(status);
        if (type != null) {
            problemDetail.setType(type);
        }
        problemDetail.setTitle(title);
        problemDetail.setDetail(detail);
        problemDetail.setInstance(instance);
        problemDetail.setProperties(properties);
        return problemDetail;
    }
}
