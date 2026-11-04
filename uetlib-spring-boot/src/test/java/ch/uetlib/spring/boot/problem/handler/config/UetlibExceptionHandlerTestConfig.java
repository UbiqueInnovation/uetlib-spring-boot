package ch.uetlib.spring.boot.problem.handler.config;

import ch.uetlib.spring.boot.problem.autoconfigure.UetlibSpringBootProblemAutoConfiguration;
import ch.uetlib.spring.boot.problem.handler.TestUetlibExceptionHandler;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

@SpringBootConfiguration
@ImportAutoConfiguration(UetlibSpringBootProblemAutoConfiguration.class)
@Import(TestUetlibExceptionHandler.class)
public class UetlibExceptionHandlerTestConfig {

    @Bean
    ResourceBundleMessageSource messageSource() {
        final var source = new ResourceBundleMessageSource();
        source.setDefaultEncoding("UTF-8");
        source.setBasenames("i18n/messages");
        source.setDefaultLocale(Locale.ENGLISH);
        return source;
    }
}
