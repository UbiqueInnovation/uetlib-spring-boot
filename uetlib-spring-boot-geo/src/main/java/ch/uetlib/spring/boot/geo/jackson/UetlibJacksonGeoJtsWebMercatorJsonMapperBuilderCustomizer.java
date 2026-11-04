package ch.uetlib.spring.boot.geo.jackson;

import ch.uetlib.geo.jackson.JsonMapperJtsUtils;

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
