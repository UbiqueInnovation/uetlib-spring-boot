package ch.ubique.backend.spring.boot.http.restclient.autoconfigure;

import ch.ubique.backend.spring.boot.http.restclient.HttpClientRestClientCustomizer;
import ch.ubique.backend.spring.boot.http.restclient.LogbookRestClientCustomizer;
import ch.ubique.backend.spring.boot.http.restclient.UserAgentRestClientCustomizer;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestClient;

@AutoConfiguration
@ConditionalOnClass(RestClient.class)
@Import({
    HttpClientRestClientCustomizer.class,
    LogbookRestClientCustomizer.class,
    UserAgentRestClientCustomizer.class,
})
public class UetlibSpringBootRestClientAutoConfiguration {}
