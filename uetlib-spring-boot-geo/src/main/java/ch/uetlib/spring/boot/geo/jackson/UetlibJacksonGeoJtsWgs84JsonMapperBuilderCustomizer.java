package ch.uetlib.spring.boot.geo.jackson;

import ch.uetlib.geo.jackson.JsonMapperJtsUtils;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;

import tools.jackson.databind.json.JsonMapper;

@SuppressWarnings("unused")
public class UetlibJacksonGeoJtsWgs84JsonMapperBuilderCustomizer
        implements JsonMapperBuilderCustomizer {

    @Override
    public void customize(final JsonMapper.Builder jsonMapperBuilder) {
        JsonMapperJtsUtils.configureJtsWgs84(jsonMapperBuilder);
    }
}
