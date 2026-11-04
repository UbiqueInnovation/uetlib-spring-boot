package ch.uetlib.spring.boot.problem.autoconfigure;

import ch.uetlib.spring.boot.problem.handler.UetlibExceptionHandler;
import ch.uetlib.spring.boot.problem.handler.UetlibSpringBootProblemProperties;
import ch.uetlib.spring.boot.problem.handler.UnhandledExceptionLoggingResolver;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(UetlibSpringBootProblemProperties.class)
public class UetlibSpringBootProblemAutoConfiguration {

    @Bean
    @ConditionalOnBean(UetlibExceptionHandler.class)
    UnhandledExceptionLoggingResolver unhandledExceptionLoggingResolver(
            final ObjectProvider<UetlibExceptionHandler> exceptionHandlers) {
        return new UnhandledExceptionLoggingResolver(
                exceptionHandlers.orderedStream().findFirst().orElseThrow());
    }
}
