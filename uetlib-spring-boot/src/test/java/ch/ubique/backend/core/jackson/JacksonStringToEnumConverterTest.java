package ch.ubique.backend.core.jackson;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.constraints.NotNull;

import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;

class JacksonStringToEnumConverterTest {

    private static final @NotNull Converter<String, JacksonTestEnum> STRING_TO_ENUM_CONVERTER =
            new JacksonStringToEnumConverterFactory(new ObjectMapper())
                    .getConverter(JacksonTestEnum.class);

    @Test
    void testStringToEnum() {
        assertEquals(JacksonTestEnum.OPTION_A, STRING_TO_ENUM_CONVERTER.convert("option_a"));
        assertEquals(JacksonTestEnum.OPTION_B, STRING_TO_ENUM_CONVERTER.convert("option_b"));
    }
}
