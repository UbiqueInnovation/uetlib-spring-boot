package ch.ubique.backend.spring.boot.geo.jackson;

import ch.ubique.backend.geo.jackson.JsonMapperJtsUtils;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;

import tools.jackson.databind.json.JsonMapper;

@SuppressWarnings("unused")
public class UetlibJacksonGeoJtsWebMercatorJsonMapperBuilderCustomizer
        implements JsonMapperBuilderCustomizer {

    @Override
    public void customize(final JsonMapper.Builder jsonMapperBuilder) {
        JsonMapperJtsUtils.configureJtsWebMercator(jsonMapperBuilder);
    }
}
