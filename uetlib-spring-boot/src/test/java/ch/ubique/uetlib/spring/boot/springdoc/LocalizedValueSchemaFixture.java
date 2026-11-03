package ch.ubique.uetlib.spring.boot.springdoc;

import static ch.ubique.uetlib.core.localized.annotation.JsonLocalizedValue.SerializationMethod.*;

import ch.ubique.uetlib.core.localized.LocalizedValue;
import ch.ubique.uetlib.core.localized.annotation.JsonLocalizedValue;

import java.io.Serializable;

@SuppressWarnings("unused") // used for tests
class LocalizedValueSchemaFixture {

    LocalizedValue<String> defaultValue;

    @JsonLocalizedValue(keySerialization = UPPERCASE)
    LocalizedValue<Integer> uppercaseValue;

    @JsonLocalizedValue(keySerialization = CAMEL_CASE)
    LocalizedValue<TestValue> camelCaseValue;

    @JsonLocalizedValue(keySerialization = PASCAL_CASE)
    LocalizedValue<String> pascalCaseValue;

    record TestValue(String value) implements Serializable {}
}
