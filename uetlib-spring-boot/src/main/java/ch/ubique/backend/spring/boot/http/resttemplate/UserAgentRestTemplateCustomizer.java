package ch.ubique.backend.spring.boot.http.resttemplate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.restclient.RestTemplateCustomizer;
import org.springframework.web.client.RestTemplate;

public class UserAgentRestTemplateCustomizer implements RestTemplateCustomizer {

    private final String defaultUserAgent;

    public UserAgentRestTemplateCustomizer(
            @Value("${ch.post.backend.external-api.default-user-agent}")
                    final String defaultUserAgent) {
        this.defaultUserAgent = defaultUserAgent;
    }

    @Override
    public void customize(final RestTemplate restTemplate) {
        restTemplate.getInterceptors().add(new UserAgentRequestInterceptor(defaultUserAgent));
    }
}
