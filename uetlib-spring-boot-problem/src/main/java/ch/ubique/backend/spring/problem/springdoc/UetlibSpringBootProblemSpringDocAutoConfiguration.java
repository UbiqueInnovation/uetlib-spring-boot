package ch.ubique.backend.spring.problem.springdoc;

import ch.ubique.backend.problem.springdoc.SpringDocRfc9457Problem;

import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.problem.Problem;

@Configuration
public class UetlibSpringBootProblemSpringDocAutoConfiguration {

    static {
        SpringDocUtils.getConfig().replaceWithClass(Problem.class, SpringDocRfc9457Problem.class);
    }

    @Bean
    UetlibSpringBootProblemSpringDocProblemOpenApiCustomizer customOpenApi() {
        return new UetlibSpringBootProblemSpringDocProblemOpenApiCustomizer();
    }
}
