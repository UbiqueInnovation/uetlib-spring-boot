package ch.ubique.backend.spring.boot.problem.controller.config;

import ch.ubique.backend.spring.boot.problem.controller.ExceptionThrowingController;
import ch.ubique.backend.spring.boot.problem.handler.TestUetlibExceptionHandler;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

@SpringBootConfiguration
@Import({ExceptionThrowingController.class, TestUetlibExceptionHandler.class})
public class ExceptionThrowingControllerTestConfig {

    @Bean
    public ResourceBundleMessageSource messageSource() {
        final var source = new ResourceBundleMessageSource();
        source.setDefaultEncoding("UTF-8");
        source.setBasenames("i18n/messages");
        source.setDefaultLocale(Locale.ENGLISH);
        return source;
    }
}
