package ch.ubique.backend.spring.boot.problem;

import org.springframework.http.HttpHeaders;

public interface ProblemResponseHeadersProvider {

    void addProblemResponseHeaders(HttpHeaders headers);
}
