package ch.ubique.backend.spring.boot.http.autoconfigure;

import ch.ubique.backend.spring.boot.http.UetlibSpringBootHttpProperties;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@AutoConfiguration
@EnableConfigurationProperties(UetlibSpringBootHttpProperties.class)
public class UetlibSpringBootHttpClientAutoConfiguration {}
