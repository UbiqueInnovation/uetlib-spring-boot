package ch.ubique.backend.spring.boot.jackson;

import ch.ubique.backend.geo.jackson.JsonMapperJtsUtils;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;

import tools.jackson.databind.json.JsonMapper;

public class UetlibJacksonGeoJtsWgs84JsonMapperBuilderCustomizer
        implements JsonMapperBuilderCustomizer {

    @Override
    public void customize(final JsonMapper.@NonNull Builder jsonMapperBuilder) {
        JsonMapperJtsUtils.configureJtsWgs84(jsonMapperBuilder);
    }
}
