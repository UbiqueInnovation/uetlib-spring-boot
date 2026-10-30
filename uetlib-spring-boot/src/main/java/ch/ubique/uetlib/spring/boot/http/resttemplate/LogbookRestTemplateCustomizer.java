package ch.ubique.uetlib.spring.boot.http.resttemplate;

import org.springframework.boot.restclient.RestTemplateCustomizer;
import org.springframework.web.client.RestTemplate;
import org.zalando.logbook.spring.LogbookClientHttpRequestInterceptor;

public class LogbookRestTemplateCustomizer implements RestTemplateCustomizer {

    private final LogbookClientHttpRequestInterceptor logbookInterceptor;

    public LogbookRestTemplateCustomizer(
            final LogbookClientHttpRequestInterceptor logbookInterceptor) {
        this.logbookInterceptor = logbookInterceptor;
    }

    @Override
    public void customize(final RestTemplate restTemplate) {
        restTemplate.getInterceptors().add(logbookInterceptor);
    }
}
