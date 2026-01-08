package ch.ubique.backend.spring.boot.http.resttemplate;

import ch.ubique.backend.spring.boot.http.UetlibSpringBootHttpProperties;

import org.springframework.boot.restclient.RestTemplateCustomizer;
import org.springframework.web.client.RestTemplate;

public class UserAgentRestTemplateCustomizer implements RestTemplateCustomizer {

    private final String defaultUserAgent;

    public UserAgentRestTemplateCustomizer(final UetlibSpringBootHttpProperties httpProperties) {
        this.defaultUserAgent = httpProperties.defaultUserAgent();
    }

    @Override
    public void customize(final RestTemplate restTemplate) {
        restTemplate.getInterceptors().add(new UserAgentRequestInterceptor(defaultUserAgent));
    }
}
