package ch.ubique.backend.spring.boot.logbook;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Set;

/** Configuration properties for logbook configuration . */
@ConfigurationProperties(prefix = "ch.ubique.backend.spring.boot.logbook")
@Validated
public final class UetlibSpringBootLogbookProperties {
    @Schema(description = "Configure logbook obfuscation")
    private @Valid @NotNull Obfuscate obfuscate = new Obfuscate();

    @Schema(description = "Configure logbook response filter")
    private @Valid @NotNull ResponseFilter responseFilter = new ResponseFilter();

    public Obfuscate getObfuscate() {
        return obfuscate;
    }

    public void setObfuscate(final Obfuscate obfuscate) {
        this.obfuscate = obfuscate;
    }

    public ResponseFilter getResponseFilter() {
        return responseFilter;
    }

    public void setResponseFilter(final ResponseFilter responseFilter) {
        this.responseFilter = responseFilter;
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

    public static class ResponseFilter {

        @Schema(description = "Configure additional body replacers")
        private BodyReplacers bodyReplacers = new BodyReplacers();

        public BodyReplacers getBodyReplacers() {
            return bodyReplacers;
        }

        public void setBodyReplacers(final BodyReplacers bodyReplacers) {
            this.bodyReplacers = bodyReplacers;
        }

        public static class BodyReplacers {
            @Schema(
                    description = "Configure additional body replacers by content type",
                    defaultValue =
                            "text/html,text/css,application/javascript,font/*,image/*,application/protobuf")
            private List<MediaType> contentTypes =
                    List.of(
                            MediaType.TEXT_HTML,
                            MediaType.valueOf("text/css"),
                            MediaType.valueOf("application/javascript"),
                            MediaType.valueOf("font/*"),
                            MediaType.valueOf("image/*"),
                            MediaType.valueOf("application/protobuf"));

            @Schema(
                    description = "Configure additional body replacers by content encoding",
                    defaultValue = "gzip")
            private List<String> contentEncodings = List.of("gzip");

            public List<MediaType> getContentTypes() {
                return contentTypes;
            }

            public void setContentTypes(final List<MediaType> contentTypes) {
                this.contentTypes = contentTypes;
            }

            public List<String> getContentEncodings() {
                return contentEncodings;
            }

            public void setContentEncodings(final List<String> contentEncodings) {
                this.contentEncodings = contentEncodings;
            }
        }
    }
}
