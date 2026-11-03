package ch.ubique.uetlib.spring.boot.http.resttemplate.autoconfigure;

import ch.ubique.uetlib.spring.boot.http.autoconfigure.UetlibSpringBootHttpClientAutoConfiguration;
import ch.ubique.uetlib.spring.boot.http.resttemplate.HttpClientRestTemplateCustomizer;
import ch.ubique.uetlib.spring.boot.http.resttemplate.LogbookRestTemplateCustomizer;
import ch.ubique.uetlib.spring.boot.http.resttemplate.UserAgentRestTemplateCustomizer;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

@AutoConfiguration(after = UetlibSpringBootHttpClientAutoConfiguration.class)
@ConditionalOnClass(RestTemplate.class)
@Import({
    HttpClientRestTemplateCustomizer.class,
    LogbookRestTemplateCustomizer.class,
    UserAgentRestTemplateCustomizer.class
})
public class UetlibSpringBootRestTemplateAutoConfiguration {}
