package ch.ubique.uetlib.spring.boot.http.restclient;

import static org.springframework.http.HttpHeaders.USER_AGENT;

import ch.ubique.uetlib.spring.boot.http.UetlibSpringBootHttpProperties;

import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.web.client.RestClient;

public class UserAgentRestClientCustomizer implements RestClientCustomizer {

    private final String defaultUserAgent;

    public UserAgentRestClientCustomizer(final UetlibSpringBootHttpProperties httpProperties) {
        this.defaultUserAgent = httpProperties.getDefaultUserAgent();
    }

    @Override
    public void customize(final RestClient.Builder restClientBuilder) {
        restClientBuilder.defaultHeader(USER_AGENT, defaultUserAgent);
    }
}
