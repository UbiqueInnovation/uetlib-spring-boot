package ch.ubique.backend.spring.boot.problem.springdoc;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.http.ProblemDetail;

/**
 * The Spring Boot {@link ProblemDetail} class is documented in SpringDoc with a field 'properties'
 * that can be used to store additional properties for the problem detail. While this is correct
 * internally, these additional properties would be flat-mapped in the JSON representation. We take
 * the schema of the 'additionalProperties' and apply it to the top-level schema of the
 * ProblemDetail schema
 */
public class UetlibSpringBootProblemDetailOpenApiCustomizer implements OpenApiCustomizer {

    @Override
    public void customise(final OpenAPI openApi) {
        final var problemDetailSchema =
                openApi.getComponents().getSchemas().get(ProblemDetail.class.getSimpleName());
        if (problemDetailSchema == null) {
            // openApi.getComponents().getSchemas() only contains all the schemas that are
            // actually documented on some @Operation annotation, and not all problemClasses
            // are necessarily contained in there
            return;
        }

        final var additionalPropertiesSchema =
                ((Schema<?>) problemDetailSchema.getProperties().get("properties"))
                        .getAdditionalProperties();
        problemDetailSchema.additionalProperties(additionalPropertiesSchema);

        problemDetailSchema.getProperties().remove("properties");
    }
}
