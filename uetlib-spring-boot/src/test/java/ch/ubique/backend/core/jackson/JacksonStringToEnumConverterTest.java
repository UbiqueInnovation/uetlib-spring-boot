package ch.ubique.backend.core.jackson;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;

import tools.jackson.databind.ObjectMapper;

class JacksonStringToEnumConverterTest {

    private static final Converter<String, JacksonTestEnum> STRING_TO_ENUM_CONVERTER =
            new JacksonStringToEnumConverterFactory(new ObjectMapper())
                    .getConverter(JacksonTestEnum.class);

    @Test
    void testStringToEnum() {
        assertEquals(JacksonTestEnum.OPTION_A, STRING_TO_ENUM_CONVERTER.convert("option_a"));
        assertEquals(JacksonTestEnum.OPTION_B, STRING_TO_ENUM_CONVERTER.convert("option_b"));
    }
}
