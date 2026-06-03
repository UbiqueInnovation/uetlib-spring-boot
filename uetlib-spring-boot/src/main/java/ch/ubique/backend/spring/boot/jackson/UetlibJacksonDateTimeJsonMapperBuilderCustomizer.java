package ch.ubique.backend.spring.boot.jackson;

import ch.ubique.backend.core.jackson.JsonMapperUtils;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;

import tools.jackson.databind.json.JsonMapper;

public class UetlibJacksonDateTimeJsonMapperBuilderCustomizer
        implements JsonMapperBuilderCustomizer {

    @Override
    public void customize(final JsonMapper.Builder jsonMapperBuilder) {
        JsonMapperUtils.configureDateTime(jsonMapperBuilder);
    }
}
