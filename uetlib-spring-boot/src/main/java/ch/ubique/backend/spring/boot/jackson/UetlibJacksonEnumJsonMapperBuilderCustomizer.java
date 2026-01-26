package ch.ubique.backend.spring.boot.jackson;

import ch.ubique.backend.core.jackson.JsonMapperUtils;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;

import tools.jackson.databind.json.JsonMapper;

public class UetlibJacksonEnumJsonMapperBuilderCustomizer implements JsonMapperBuilderCustomizer {

    @Override
    public void customize(final JsonMapper.@NonNull Builder jsonMapperBuilder) {
        JsonMapperUtils.configureEnum(jsonMapperBuilder);
    }
}
