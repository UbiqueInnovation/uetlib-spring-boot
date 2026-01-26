package ch.ubique.backend.spring.boot.jackson.autoconfigure;

import ch.ubique.backend.spring.boot.jackson.UetlibJacksonDateTimeJsonMapperBuilderCustomizer;
import ch.ubique.backend.spring.boot.jackson.UetlibJacksonEnumJsonMapperBuilderCustomizer;
import ch.ubique.backend.spring.boot.jackson.UetlibJacksonVersionJsonMapperBuilderCustomizer;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration(before = JacksonAutoConfiguration.class)
@Import({
    UetlibJacksonDateTimeJsonMapperBuilderCustomizer.class,
    UetlibJacksonEnumJsonMapperBuilderCustomizer.class,
    UetlibJacksonVersionJsonMapperBuilderCustomizer.class
})
public class UetlibSpringBootJacksonAutoConfiguration {}
