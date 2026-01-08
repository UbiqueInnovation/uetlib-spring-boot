package ch.ubique.backend.spring.boot.http.resttemplate.autoconfigure;

import ch.ubique.backend.spring.boot.http.resttemplate.HttpClientRestTemplateCustomizer;
import ch.ubique.backend.spring.boot.http.resttemplate.LogbookRestTemplateCustomizer;
import ch.ubique.backend.spring.boot.http.resttemplate.UserAgentRestTemplateCustomizer;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

@AutoConfiguration
@ConditionalOnClass(RestTemplate.class)
@Import({
    HttpClientRestTemplateCustomizer.class,
    LogbookRestTemplateCustomizer.class,
    UserAgentRestTemplateCustomizer.class
})
public class UetlibSpringBootRestTemplateAutoConfiguration {}
