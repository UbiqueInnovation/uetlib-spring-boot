package ch.ubique.uetlib.spring.boot.geo.jackson;

import ch.ubique.uetlib.geo.jackson.JsonMapperJtsUtils;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;

import tools.jackson.databind.json.JsonMapper;

@SuppressWarnings("unused")
public class UetlibJacksonGeoJtsLv03JsonMapperBuilderCustomizer
        implements JsonMapperBuilderCustomizer {

    @Override
    public void customize(final JsonMapper.Builder jsonMapperBuilder) {
        JsonMapperJtsUtils.configureJtsLv03(jsonMapperBuilder);
    }
}
