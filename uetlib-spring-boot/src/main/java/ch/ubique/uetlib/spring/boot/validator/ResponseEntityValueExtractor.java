package ch.ubique.uetlib.spring.boot.validator;

import jakarta.validation.valueextraction.ExtractedValue;
import jakarta.validation.valueextraction.ValueExtractor;
import org.springframework.http.ResponseEntity;

/**
 * A {@link ValueExtractor} for {@link ResponseEntity}, enabling bean validation on the body of the
 * ResponseEntity.
 *
 * @see ValueExtractor
 * @see ResponseEntity
 */
public class ResponseEntityValueExtractor
        implements ValueExtractor<ResponseEntity<@ExtractedValue ?>> {

    @Override
    public void extractValues(final ResponseEntity<?> originalValue, final ValueReceiver receiver) {
        if (originalValue != null) {
            receiver.value("body", originalValue.getBody());
        } else {
            receiver.value(null, null);
        }
    }
}
