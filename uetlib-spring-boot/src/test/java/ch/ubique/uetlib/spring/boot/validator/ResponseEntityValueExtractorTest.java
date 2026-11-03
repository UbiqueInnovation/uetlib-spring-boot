package ch.ubique.uetlib.spring.boot.validator;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.valueextraction.ValueExtractor.ValueReceiver;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Set;

final class ResponseEntityValueExtractorTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        // Ensure default provider is used and services are discovered (including our
        // ValueExtractor)
        final var factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private static <T> Set<ConstraintViolation<T>> validate(final T bean) {
        assertNotNull(validator, "Validator must be initialized in @BeforeAll");
        return validator.validate(bean);
    }

    @Test
    void extractsBodyValue_whenEntityAndBodyPresent() {
        final var receiver = new CapturingReceiver();
        final var extractor = new ResponseEntityValueExtractor();

        extractor.extractValues(ResponseEntity.ok("hello"), receiver);

        assertEquals("body", receiver.lastNodeName);
        assertEquals("hello", receiver.lastValue);
        assertEquals(1, receiver.calls);
    }

    @Test
    void extractsNull_whenEntityPresentButBodyIsNull() {
        final var receiver = new CapturingReceiver();
        final var extractor = new ResponseEntityValueExtractor();

        extractor.extractValues(ResponseEntity.ok().build(), receiver);

        assertEquals("body", receiver.lastNodeName);
        assertNull(receiver.lastValue);
        assertEquals(1, receiver.calls);
    }

    @Test
    void extractsNullNode_whenEntityIsNull() {
        final var receiver = new CapturingReceiver();
        final var extractor = new ResponseEntityValueExtractor();

        extractor.extractValues(null, receiver);

        assertNull(receiver.lastNodeName);
        assertNull(receiver.lastValue);
        assertEquals(1, receiver.calls);
    }

    @Test
    void containerElementConstraint_validAndInvalidBodies() {
        final var validBean = new StringBodyBean(ResponseEntity.ok("valid"));
        final var violationsValid = validate(validBean);
        assertTrue(violationsValid.isEmpty(), "Expected no violations for valid body");

        final var blankBean = new StringBodyBean(ResponseEntity.ok("   "));
        final var violationsBlank = validate(blankBean);
        assertEquals(1, violationsBlank.size());
        final var v1 = violationsBlank.iterator().next();
        assertTrue(v1.getPropertyPath().toString().contains("body"));

        final var nullBodyBean = new StringBodyBean(ResponseEntity.ok().build());
        final var violationsNull = validate(nullBodyBean);
        assertEquals(1, violationsNull.size());
        final var v2 = violationsNull.iterator().next();
        assertTrue(v2.getPropertyPath().toString().contains("body"));
    }

    @Test
    void cascadedValidationOnBody_objectInvalidAndValid() {
        final var invalid = new UserBodyBean(ResponseEntity.ok(new User("")));
        final var violationsInvalid = validate(invalid);
        assertEquals(1, violationsInvalid.size());
        final var v = violationsInvalid.iterator().next();
        // Should traverse through the ResponseEntity body
        assertTrue(v.getPropertyPath().toString().contains("body"));

        final var valid = new UserBodyBean(ResponseEntity.ok(new User("Alice")));
        final var violationsValid = validate(valid);
        assertTrue(violationsValid.isEmpty());
    }

    // --- Test helpers

    private static class CapturingReceiver implements ValueReceiver {
        String lastNodeName;
        Object lastValue;
        int calls;

        @Override
        public void value(final String nodeName, final Object object) {
            this.lastNodeName = nodeName;
            this.lastValue = object;
            this.calls++;
        }

        @Override
        public void iterableValue(final String nodeName, final Object value) {
            value(nodeName, value);
        }

        @Override
        public void indexedValue(final String nodeName, final int index, final Object value) {
            value(nodeName, value);
        }

        @Override
        public void keyedValue(final String nodeName, final Object key, final Object value) {
            value(nodeName, value);
        }
    }

    private record StringBodyBean(ResponseEntity<@NotBlank String> body) {}

    private record UserBodyBean(@Valid ResponseEntity<User> body) {}

    private record User(@NotBlank String name) {}
}
