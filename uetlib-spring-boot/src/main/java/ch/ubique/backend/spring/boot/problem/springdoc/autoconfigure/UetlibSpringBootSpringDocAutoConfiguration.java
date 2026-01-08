package ch.ubique.backend.spring.boot.problem.springdoc.autoconfigure;

import ch.ubique.backend.spring.boot.problem.springdoc.UetlibSpringBootProblemDetailOpenApiCustomizer;
import ch.ubique.backend.spring.boot.problem.springdoc.UetlibSpringBootSpringDocProblemOpenApiCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
    UetlibSpringBootProblemDetailOpenApiCustomizer.class,
    UetlibSpringBootSpringDocProblemOpenApiCustomizer.class
})
public class UetlibSpringBootSpringDocAutoConfiguration {}
