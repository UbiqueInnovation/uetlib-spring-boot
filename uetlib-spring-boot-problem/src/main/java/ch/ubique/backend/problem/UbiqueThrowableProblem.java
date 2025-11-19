package ch.ubique.backend.problem;

import ch.ubique.backend.problem.errortype.ProblemErrorType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;

import org.zalando.problem.Problem;
import org.zalando.problem.StatusType;

import java.net.URI;

@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface UbiqueThrowableProblem<E extends ProblemErrorType> extends Problem {

    @NotNull
    @JsonProperty(required = true)
    default URI type() {
        return getType();
    }

    @NotNull
    @JsonProperty(required = true)
    default StatusType status() {
        return getStatus();
    }

    @JsonProperty(required = true)
    @NotNull
    E errorType();

    @JsonProperty(required = true)
    default int errorCode() {
        return errorType().code();
    }
}
