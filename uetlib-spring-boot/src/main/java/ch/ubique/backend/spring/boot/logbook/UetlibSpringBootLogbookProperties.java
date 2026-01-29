package ch.ubique.backend.spring.boot.logbook;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Set;

/** Configuration properties for logbook configuration . */
@ConfigurationProperties(prefix = "ch.ubique.backend.spring.boot.logbook")
@Validated
public final class UetlibSpringBootLogbookProperties {
    @Schema(description = "Configure logbook obfuscation")
    private @Valid @NotNull Obfuscate obfuscate = new Obfuscate();

    public Obfuscate getObfuscate() {
        return obfuscate;
    }

    public void setObfuscate(final Obfuscate obfuscate) {
        this.obfuscate = obfuscate;
    }

    public static class Obfuscate {

        @Schema(
                description =
                        "form url encoded fields to obfuscate. Set to empty list to disable all"
                            + " body replacements",
                defaultValue = "subject_token,authToken")
        private Set<String> formUrlEncodedBodyFields = Set.of("subject_token", "authToken");

        public Set<String> getFormUrlEncodedBodyFields() {
            return formUrlEncodedBodyFields;
        }

        public void setFormUrlEncodedBodyFields(final Set<String> formUrlEncodedBodyFields) {
            this.formUrlEncodedBodyFields = formUrlEncodedBodyFields;
        }
    }
}
