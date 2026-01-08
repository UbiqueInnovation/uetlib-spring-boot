package ch.ubique.backend.security.jwt;

import static ch.ubique.backend.security.jwt.MoreJwtValidators.*;

import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder.JwkSetUriJwtDecoderBuilder;
import org.springframework.web.client.RestOperations;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public final class MoreJwtDecoders {

    private MoreJwtDecoders() {}

    /**
     * Creates a JwtDecoder that validates issuer and audience claims.
     *
     * @param restOperations RestOperations A pre-configured RestOperations instance, most likely an
     *     instance of {@link RestTemplateBuilder#build()}.
     * @param issuerUri String representation of the issuer URI.
     * @param audiences List of allowed audience values.
     * @return JwtDecoder instance.
     */
    public static JwtDecoder issuerAndAudienceValidatingJwtDecoder(
            final RestOperations restOperations,
            final String issuerUri,
            final List<String> audiences) {
        return issuerAndAudienceValidatingJwtDecoder(restOperations, issuerUri, audiences, b -> {});
    }

    /**
     * Creates a JwtDecoder that validates issuer and audience claims.
     *
     * @param restOperations RestOperations A pre-configured RestOperations instance, most likely an
     *     instance of {@link RestTemplateBuilder#build()}. * @param issuerUri String representation
     *     of the issuer URI.
     * @param audiences List of allowed audience values.
     * @param jwtDecoderCustomizer Consumer to customize the NimbusJwtDecoderBuilder. E.g. to set
     *     {@link JwkSetUriJwtDecoderBuilder#validateType(boolean)}
     * @return JwtDecoder instance.
     */
    public static JwtDecoder issuerAndAudienceValidatingJwtDecoder(
            final RestOperations restOperations,
            final String issuerUri,
            final List<String> audiences,
            final Consumer<JwkSetUriJwtDecoderBuilder> jwtDecoderCustomizer) {
        return issuerAndAudienceValidatingJwtDecoder(
                restOperations, issuerUri, audiences, List.of(), jwtDecoderCustomizer);
    }

    /**
     * Creates a JwtDecoder that validates issuer and audience claims and the type {@code at+jwt}.
     *
     * @param restOperations RestOperations A pre-configured RestOperations instance, most likely an
     *     instance of {@link RestTemplateBuilder#build()}.
     * @param issuerUri String representation of the issuer URI.
     * @param audiences List of allowed audience values.
     * @return JwtDecoder instance.
     */
    public static JwtDecoder issuerAndAudienceValidatingAtJwtDecoder(
            final RestOperations restOperations,
            final String issuerUri,
            final List<String> audiences) {

        final Consumer<JwkSetUriJwtDecoderBuilder> noJwtTypeVerifier = b -> b.validateType(false);

        return issuerAndAudienceValidatingJwtDecoder(
                restOperations,
                issuerUri,
                audiences,
                List.of(typeAtJwtValidator()),
                noJwtTypeVerifier);
    }

    /**
     * Creates a JwtDecoder that validates issuer and audience claims and the type {@code at+jwt}.
     *
     * @param restOperations RestOperations A pre-configured RestOperations instance, most likely an
     *     instance of {@link RestTemplateBuilder#build()}.
     * @param issuerUri String representation of the issuer URI.
     * @param audiences List of allowed audience values.
     * @param additionalJwtDecoderCustomizer Consumer to customize the NimbusJwtDecoderBuilder. E.g.
     *     to set {@link JwkSetUriJwtDecoderBuilder#validateType(boolean)}
     * @return JwtDecoder instance.
     */
    public static JwtDecoder issuerAndAudienceValidatingAtJwtDecoder(
            final RestOperations restOperations,
            final String issuerUri,
            final List<String> audiences,
            final Consumer<JwkSetUriJwtDecoderBuilder> additionalJwtDecoderCustomizer) {

        final Consumer<JwkSetUriJwtDecoderBuilder> noJwtTypeVerifier = b -> b.validateType(false);
        final var jwtDecoderCustomizer = additionalJwtDecoderCustomizer.andThen(noJwtTypeVerifier);

        return issuerAndAudienceValidatingJwtDecoder(
                restOperations,
                issuerUri,
                audiences,
                List.of(typeAtJwtValidator()),
                jwtDecoderCustomizer);
    }

    /**
     * Creates a JwtDecoder that validates issuer and audience claims.
     *
     * @param restOperations RestOperations A pre-configured RestOperations instance, most likely an
     *     instance of {@link RestTemplateBuilder#build()}.
     * @param issuerUri String representation of the issuer URI.
     * @param audiences List of allowed audience values.
     * @param additionalTokenValidators Additional OAuth2TokenValidators to apply.
     * @param additionalJwtDecoderCustomizer Consumer to customize the NimbusJwtDecoderBuilder. E.g.
     *     to set {@link JwkSetUriJwtDecoderBuilder#validateType(boolean)}
     * @return JwtDecoder instance.
     */
    public static JwtDecoder issuerAndAudienceValidatingJwtDecoder(
            final RestOperations restOperations,
            final String issuerUri,
            final List<String> audiences,
            final Collection<OAuth2TokenValidator<Jwt>> additionalTokenValidators,
            final Consumer<JwkSetUriJwtDecoderBuilder> additionalJwtDecoderCustomizer) {
        final var tokenValidators =
                Stream.concat(
                                Stream.of(issuerValidator(issuerUri), audienceValidator(audiences)),
                                additionalTokenValidators.stream())
                        .toList();
        final var jwtValidator = JwtValidators.createDefaultWithValidators(jwtValidators);

        final var jwtDecoderBuilder = NimbusJwtDecoder.withIssuerLocation(issuerUri);
        jwtDecoderBuilder.restOperations(restOperations);

        additionalJwtDecoderCustomizer.accept(jwtDecoderBuilder);

        final var jwtDecoder = jwtDecoderBuilder.build();
        jwtDecoder.setJwtValidator(jwtValidator);
        return jwtDecoder;
    }
}
