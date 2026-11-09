package ch.uetlib.spring.boot.problem.springdoc.autoconfigure;

import ch.uetlib.spring.boot.problem.springdoc.UetlibSpringBootProblemDetailOpenApiCustomizer;
import ch.uetlib.spring.boot.problem.springdoc.UetlibSpringBootProblemSpringDocProblemOpenApiCustomizer;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({
    UetlibSpringBootProblemDetailOpenApiCustomizer.class,
    UetlibSpringBootProblemSpringDocProblemOpenApiCustomizer.class
})
public class UetlibSpringBootProblemSpringDocAutoConfiguration {}
