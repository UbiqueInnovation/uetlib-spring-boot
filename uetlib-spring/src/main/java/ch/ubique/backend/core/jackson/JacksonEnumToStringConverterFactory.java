package ch.ubique.backend.core.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.constraints.NotNull;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.stereotype.Component;

/**
 * deserialize Enums from String using Jackson. SpringBoot by itself just calls {@link Enum#valueOf}
 * which ignores Jackson annotations such as {@link com.fasterxml.jackson.annotation.JsonProperty},
 * {@link com.fasterxml.jackson.annotation.JsonValue} or {@link
 * com.fasterxml.jackson.annotation.JsonCreator}
 */
@Component
@SuppressWarnings("rawtypes")
public class JacksonEnumToStringConverterFactory implements ConverterFactory<Enum, String> {

    private final ObjectMapper objectMapper;

    public JacksonEnumToStringConverterFactory(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull Converter<Enum, String> getConverter(
            final @NotNull Class<String> targetType) {
        return new JacksonStringToEnumConverter(objectMapper);
    }

    static class JacksonStringToEnumConverter<T extends Enum> implements Converter<T, String> {

        private final ObjectMapper objectMapper;

        public JacksonStringToEnumConverter(final ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        public String convert(@NotNull final T source) {
            return objectMapper.convertValue(source, String.class);
        }
    }
}
