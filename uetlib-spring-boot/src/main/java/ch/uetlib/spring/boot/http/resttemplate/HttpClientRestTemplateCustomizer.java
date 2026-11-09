package ch.uetlib.spring.boot.http.resttemplate;

import org.apache.hc.client5.http.classic.HttpClient;
import org.springframework.boot.restclient.RestTemplateCustomizer;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class HttpClientRestTemplateCustomizer implements RestTemplateCustomizer {

    private final HttpClient httpClient;

    public HttpClientRestTemplateCustomizer(final HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public void customize(final RestTemplate restTemplate) {
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
    }
}
