package ch.ubique.backend.spring.boot.problem.autoconfigure;

import ch.ubique.backend.spring.boot.problem.handler.UetlibSpringBootProblemProperties;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@AutoConfiguration
@EnableConfigurationProperties(UetlibSpringBootProblemProperties.class)
public class UetlibSpringBootProblemAutoConfiguration {}
