package ch.ubique.backend.spring.problem.springdoc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UetlibSpringBootProblemSpringDocAutoConfiguration {

    @Bean
    UetlibSpringBootProblemSpringDocProblemOpenApiCustomizer customOpenApi() {
        return new UetlibSpringBootProblemSpringDocProblemOpenApiCustomizer();
    }
}
