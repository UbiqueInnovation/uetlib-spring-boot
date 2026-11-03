package ch.ubique.uetlib.spring.boot.jackson;

import ch.ubique.uetlib.core.jackson.JsonMapperUtils;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;

import tools.jackson.databind.json.JsonMapper;

public class UetlibJacksonVersionJsonMapperBuilderCustomizer
        implements JsonMapperBuilderCustomizer {

    @Override
    public void customize(final JsonMapper.Builder jsonMapperBuilder) {
        JsonMapperUtils.configureVersion(jsonMapperBuilder);
    }
}
