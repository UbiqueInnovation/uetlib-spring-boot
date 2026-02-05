package ch.ubique.backend.spring.boot.geo.jackson;

import ch.ubique.backend.geo.jackson.JsonMapperJtsUtils;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;

import tools.jackson.databind.json.JsonMapper;

@SuppressWarnings("unused")
public class UetlibJacksonGeoJtsLv03JsonMapperBuilderCustomizer
        implements JsonMapperBuilderCustomizer {

    @Override
    public void customize(final JsonMapper.@NonNull Builder jsonMapperBuilder) {
        JsonMapperJtsUtils.configureJtsLv03(jsonMapperBuilder);
    }
}
