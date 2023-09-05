package ch.ubique.backend.core.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.jetbrains.annotations.NotNull;
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
public class JacksonStringToEnumConverterFactory implements ConverterFactory<String, Enum> {

    private final ObjectMapper objectMapper;

    public JacksonStringToEnumConverterFactory(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Enum> @NotNull Converter<String, T> getConverter(
            final @NotNull Class<T> targetType) {
        return new JacksonStringToEnumConverter(targetType, objectMapper);
    }

    @SuppressWarnings("ClassCanBeRecord")
    private static class JacksonStringToEnumConverter<T extends Enum>
            implements Converter<String, T> {

        private final Class<T> enumType;
        private final ObjectMapper objectMapper;

        public JacksonStringToEnumConverter(final Class<T> enumType, final ObjectMapper objectMapper) {
            this.enumType = enumType;
            this.objectMapper = objectMapper;
        }

        public T convert(final String source) {
            return objectMapper.convertValue(source.trim(), enumType);
        }
    }
}
