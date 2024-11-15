package ch.ubique.backend.core.jackson;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.constraints.NotNull;

import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;

@SuppressWarnings("rawtypes")
class JacksonEnumToStringConverterTest {

    private static final @NotNull Converter<Enum, String> ENUM_TO_STRING_CONVERTER =
            new JacksonEnumToStringConverterFactory(new ObjectMapper()).getConverter(String.class);

    @Test
    void testEnumToString() {
        assertEquals("option_a", ENUM_TO_STRING_CONVERTER.convert(JacksonTestEnum.OPTION_A));
        assertEquals("option_b", ENUM_TO_STRING_CONVERTER.convert(JacksonTestEnum.OPTION_B));
    }
}
