package ch.ubique.uetlib.spring.boot.http.resttemplate;

import static org.springframework.http.HttpHeaders.USER_AGENT;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class UserAgentRequestInterceptor implements ClientHttpRequestInterceptor {
    private final String defaultUserAgent;

    public UserAgentRequestInterceptor(final String defaultUserAgent) {
        this.defaultUserAgent = defaultUserAgent;
    }

    @Override
    public ClientHttpResponse intercept(
            final HttpRequest request,
            final byte[] body,
            final ClientHttpRequestExecution execution)
            throws IOException {
        if (!request.getHeaders().containsHeader(USER_AGENT)) {
            request.getHeaders().set(USER_AGENT, defaultUserAgent);
        }
        return execution.execute(request, body);
    }
}
