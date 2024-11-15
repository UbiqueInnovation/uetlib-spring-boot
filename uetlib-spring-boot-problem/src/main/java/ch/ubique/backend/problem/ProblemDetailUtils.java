package ch.ubique.backend.problem;

import org.springframework.http.ProblemDetail;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.StatusType;
import org.zalando.problem.ThrowableProblem;

public final class ProblemDetailUtils {

    private ProblemDetailUtils() {}

    public static ThrowableProblem toThrowableProblem(final ProblemDetail problemDetail) {
        final StatusType status = Status.valueOf(problemDetail.getStatus());

        final var type = problemDetail.getType();
        final var title =
                problemDetail.getTitle() != null
                        ? problemDetail.getTitle()
                        : status.getReasonPhrase();
        final var detail = problemDetail.getDetail();
        final var instance = problemDetail.getInstance();
        final var parameters = problemDetail.getProperties();

        final var problem =
                Problem.builder()
                        .withType(type)
                        .withTitle(title)
                        .withStatus(status)
                        .withDetail(detail)
                        .withInstance(instance);

        if (parameters != null) {
            parameters.forEach(problem::with);
        }

        return problem.build();
    }
}
