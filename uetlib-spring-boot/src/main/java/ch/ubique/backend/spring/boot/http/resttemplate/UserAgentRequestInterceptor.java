package ch.ubique.backend.spring.boot.http.resttemplate;

import static org.springframework.http.HttpHeaders.USER_AGENT;

import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class UserAgentRequestInterceptor implements ClientHttpRequestInterceptor {
    private final @NonNull String defaultUserAgent;

    public UserAgentRequestInterceptor(final @NonNull String defaultUserAgent) {
        this.defaultUserAgent = defaultUserAgent;
    }

    @Override
    public @NonNull ClientHttpResponse intercept(
            final @NonNull HttpRequest request,
            final byte @NonNull [] body,
            final @NonNull ClientHttpRequestExecution execution)
            throws IOException {
        request.getHeaders().add(USER_AGENT, defaultUserAgent);
        return execution.execute(request, body);
    }
}
