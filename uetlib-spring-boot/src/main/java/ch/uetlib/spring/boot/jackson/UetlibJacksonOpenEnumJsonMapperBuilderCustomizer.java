package ch.uetlib.spring.boot.jackson;

import ch.uetlib.core.jackson.JsonMapperUtils;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;

import tools.jackson.databind.json.JsonMapper;

public class UetlibJacksonOpenEnumJsonMapperBuilderCustomizer
        implements JsonMapperBuilderCustomizer {

    @Override
    public void customize(final JsonMapper.Builder jsonMapperBuilder) {
        JsonMapperUtils.configureOpenEnum(jsonMapperBuilder);
    }
}
