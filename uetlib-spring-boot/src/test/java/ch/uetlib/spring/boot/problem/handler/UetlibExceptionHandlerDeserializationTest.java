package ch.uetlib.spring.boot.problem.handler;

import static org.junit.jupiter.api.Assertions.*;

import ch.uetlib.spring.boot.problem.handler.trait.violation.Violation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.Nulls;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

@SpringJUnitConfig(UetlibExceptionHandlerDeserializationTest.TestConfig.class)
class UetlibExceptionHandlerDeserializationTest {

    private final JsonMapper mapper =
            JsonMapper.builder()
                    .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .enable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
                    .build();

    @Autowired private TestHandler handler;

    static Stream<Arguments> invalidInputs() {
        return Stream.of(
                Arguments.of(
                        /* language=JSON */
                        """
                        {
                            "flag": true,
                            "required": {}
                        }
                        """,
                        "required.value",
                        "required property is missing"),
                Arguments.of(
                        /* language=JSON */
                        """
                        {
                            "flag": true,
                            "items": [
                                {}
                            ]
                        }
                        """,
                        "items[0]",
                        "must be a string"),
                Arguments.of(
                        /* language=JSON */
                        """
                        {
                            "flag": true,
                            "option": "UNKNOWN"
                        }
                        """,
                        "option",
                        "must be a valid enum value"),
                Arguments.of(
                        /* language=JSON */
                        """
                        {
                            "flag": true,
                            "date": "invalid"
                        }
                        """,
                        "date",
                        "must be a valid date and time"),
                Arguments.of(
                        /* language=JSON */
                        """
                        {
                            "flag": true,
                            "duration": "invalid"
                        }
                        """,
                        "duration",
                        "must be a valid duration or period"),
                Arguments.of(
                        /* language=JSON */
                        """
                        {
                            "flag": true,
                            "zone": "invalid"
                        }
                        """,
                        "zone",
                        "must be a valid time zone"),
                Arguments.of(
                        /* language=JSON */
                        """
                        {
                            "flag": true,
                            "uuid": "invalid"
                        }
                        """,
                        "uuid",
                        "must be a valid UUID"),
                Arguments.of(
                        /* language=JSON */
                        """
                        {
                            "flag": true,
                            "uri": "bad uri"
                        }
                        """,
                        "uri",
                        "must be a valid URI"),
                Arguments.of(
                        /* language=JSON */
                        """
                        {
                            "flag": true,
                            "url": "bad-url"
                        }
                        """,
                        "url",
                        "must be a valid URL"),
                Arguments.of(
                        /* language=JSON */
                        """
                        {
                            "flag": true,
                            "bytes": "!!!"
                        }
                        """,
                        "bytes",
                        "must be a valid Base64 string"),
                Arguments.of(
                        /* language=JSON */
                        """
                        {
                            "flag": true,
                            "number": "invalid"
                        }
                        """,
                        "number",
                        "must be a number"),
                Arguments.of(
                        /* language=JSON */
                        """
                        {
                            "flag": "invalid"
                        }
                        """,
                        "flag",
                        "must be a boolean"),
                Arguments.of(
                        /* language=JSON */
                        """
                        {
                            "flag": null
                        }
                        """,
                        "flag",
                        "must not be null"),
                Arguments.of(
                        /* language=JSON */
                        """
                        {
                            "flag": true,
                            "character": "long"
                        }
                        """,
                        "character",
                        "must be a single character"),
                Arguments.of(
                        /* language=JSON */
                        """
                        {
                            "flag": true,
                            "items": {}
                        }
                        """,
                        "items",
                        "must be an array"),
                Arguments.of(
                        /* language=JSON */
                        """
                        {
                            "flag": true,
                            "properties": []
                        }
                        """,
                        "properties",
                        "must be an object"),
                Arguments.of(
                        /* language=JSON */
                        """
                        {
                            "flag": true,
                            "text": {}
                        }
                        """,
                        "text",
                        "must be a string"),
                Arguments.of(
                        /* language=JSON */
                        """
                        {
                            "flag": true,
                            "unknown": true
                        }
                        """,
                        "unknown",
                        "unknown property"),
                Arguments.of(
                        /* language=JSON */
                        """
                        {
                            "flag": true,
                            "ignored": true
                        }
                        """,
                        "ignored",
                        "property must not be provided"),
                Arguments.of(
                        /* language=JSON */
                        """
                        {
                            "flag": true,
                            "typed": {}
                        }
                        """,
                        "typed",
                        "type discriminator is missing"),
                Arguments.of(
                        /* language=JSON */
                        """
                        {
                            "flag": true,
                            "typed": {
                                "kind": "unknown"
                            }
                        }
                        """,
                        "typed",
                        "invalid type discriminator"));
    }

    @ParameterizedTest
    @MethodSource("invalidInputs")
    void mapsJacksonInputErrors(final String json, final String field, final String message) {
        final var cause =
                assertThrows(JacksonException.class, () -> mapper.readValue(json, Payload.class));
        final var exception =
                new HttpMessageNotReadableException(
                        "Failed to read request", cause, new MockHttpInputMessage(new byte[0]));
        final var response =
                handler.handleHttpMessageNotReadable(
                        exception,
                        HttpHeaders.EMPTY,
                        HttpStatus.BAD_REQUEST,
                        new ServletWebRequest(new MockHttpServletRequest()));
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(List.of(new Violation(field, message)), response.getBody());
    }

    @Test
    void acceptsJsonPropertyEnumNames() {
        assertEquals(
                new RenamedPayload(RenamedOption.FIRST),
                mapper.readValue("{\"option\":\"first\"}", RenamedPayload.class));
        assertEquals(
                new RenamedPayload(RenamedOption.SECOND),
                mapper.readValue("{\"option\":\"second\"}", RenamedPayload.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"FIRST", "SECOND", "unknown"})
    void renamedEnumsDoNotSuggestRejectedConstantNames(final String value) {
        final var cause =
                assertThrows(
                        MismatchedInputException.class,
                        () ->
                                mapper.readValue(
                                        "{\"option\":\"" + value + "\"}", RenamedPayload.class));
        final var exception =
                new HttpMessageNotReadableException(
                        "Failed to read request", cause, new MockHttpInputMessage(new byte[0]));
        final var response =
                handler.handleHttpMessageNotReadable(
                        exception,
                        HttpHeaders.EMPTY,
                        HttpStatus.BAD_REQUEST,
                        new ServletWebRequest(new MockHttpServletRequest()));
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(
                List.of(new Violation("option", "must be a valid enum value")), response.getBody());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(classes = Payload.class)
    void unknownOrMissingTargetTypeReturnsGenericViolation(final @Nullable Class<?> targetType) {
        final var mismatch =
                MismatchedInputException.from(null, targetType, "Unclassified input mismatch");
        mismatch.prependPath(Payload.class, "value");
        final var exception =
                new HttpMessageNotReadableException(
                        "Failed to read request", mismatch, new MockHttpInputMessage(new byte[0]));
        final var response =
                handler.handleHttpMessageNotReadable(
                        exception,
                        HttpHeaders.EMPTY,
                        HttpStatus.BAD_REQUEST,
                        new ServletWebRequest(new MockHttpServletRequest()));
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(List.of(new Violation("value", "invalid value")), response.getBody());
    }

    @Test
    void malformedJsonKeepsGenericResponse() {
        assertGenericResponse("{");
    }

    @Test
    void errorWithoutFieldPathKeepsGenericResponse() {
        assertGenericResponse("[]");
    }

    private void assertGenericResponse(final String json) {
        final var cause =
                assertThrows(JacksonException.class, () -> mapper.readValue(json, Payload.class));
        final var exception =
                new HttpMessageNotReadableException(
                        "Failed to read request", cause, new MockHttpInputMessage(new byte[0]));
        final var response =
                handler.handleHttpMessageNotReadable(
                        exception,
                        HttpHeaders.EMPTY,
                        HttpStatus.BAD_REQUEST,
                        new ServletWebRequest(new MockHttpServletRequest()));
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        final var problem = assertInstanceOf(ProblemDetail.class, response.getBody());
        assertEquals("Failed to read request", problem.getDetail());
        assertTrue(
                problem.getProperties() == null
                        || !problem.getProperties().containsKey("violations"));
    }

    @Configuration(proxyBeanMethods = false)
    static class TestConfig {
        @Bean
        TestHandler handler() {
            return new TestHandler();
        }

        @Bean
        UetlibSpringBootProblemProperties problemProperties() {
            final var properties = new UetlibSpringBootProblemProperties();
            properties.setIncludeConstraintViolations(true);
            return properties;
        }
    }

    enum RenamedOption {
        @JsonProperty("first")
        FIRST,
        @JsonProperty("second")
        SECOND
    }

    record RenamedPayload(RenamedOption option) {}

    enum Option {
        FIRST,
        SECOND
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
    interface Typed {}

    @JsonIgnoreProperties("ignored")
    record Payload(
            Option option,
            ZonedDateTime date,
            Duration duration,
            UUID uuid,
            ZoneId zone,
            URI uri,
            URL url,
            byte[] bytes,
            Integer number,
            @JsonSetter(nulls = Nulls.FAIL) Boolean flag,
            Character character,
            List<String> items,
            Map<String, String> properties,
            String text,
            Typed typed,
            Required required) {}

    record Required(@JsonProperty(required = true) String value) {}

    private static class TestHandler extends UetlibExceptionHandler {
        @Override
        public ResponseEntity<Object> newConstraintViolationProblemDetail(
                final Exception ex,
                final HttpHeaders headers,
                final HttpStatusCode status,
                final @Nullable Stream<Violation> violations,
                final WebRequest request) {
            assertNotNull(violations);
            return ResponseEntity.status(status).body(violations.toList());
        }
    }
}
