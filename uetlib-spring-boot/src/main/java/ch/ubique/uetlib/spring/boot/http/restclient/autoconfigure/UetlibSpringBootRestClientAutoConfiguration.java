package ch.ubique.uetlib.spring.boot.http.restclient.autoconfigure;

import ch.ubique.uetlib.spring.boot.http.autoconfigure.UetlibSpringBootHttpClientAutoConfiguration;
import ch.ubique.uetlib.spring.boot.http.restclient.HttpClientRestClientCustomizer;
import ch.ubique.uetlib.spring.boot.http.restclient.LogbookRestClientCustomizer;
import ch.ubique.uetlib.spring.boot.http.restclient.UserAgentRestClientCustomizer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestClient;

@AutoConfiguration(after = UetlibSpringBootHttpClientAutoConfiguration.class)
@ConditionalOnClass(RestClient.class)
@Import({
    HttpClientRestClientCustomizer.class,
    LogbookRestClientCustomizer.class,
    UserAgentRestClientCustomizer.class,
})
public class UetlibSpringBootRestClientAutoConfiguration {}
