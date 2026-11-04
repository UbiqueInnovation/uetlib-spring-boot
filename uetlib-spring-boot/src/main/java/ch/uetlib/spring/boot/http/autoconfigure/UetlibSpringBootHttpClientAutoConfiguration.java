package ch.uetlib.spring.boot.http.autoconfigure;

import ch.uetlib.spring.boot.http.UetlibSpringBootHttpProperties;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@AutoConfiguration
@EnableConfigurationProperties(UetlibSpringBootHttpProperties.class)
public class UetlibSpringBootHttpClientAutoConfiguration {}
