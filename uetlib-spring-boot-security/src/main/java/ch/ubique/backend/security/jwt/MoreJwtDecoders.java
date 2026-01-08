package ch.ubique.backend.security.jwt;

import static ch.ubique.backend.security.jwt.MoreJwtValidators.*;

import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder.JwkSetUriJwtDecoderBuilder;

import java.util.List;
import java.util.function.Consumer;

public final class MoreJwtDecoders {

    private MoreJwtDecoders() {}

    /**
     * Creates a JwtDecoder that validates issuer and audience claims.
     *
     * @param issuerUri String representation of the issuer URI.
     * @param audiences List of allowed audience values.
     * @return JwtDecoder instance.
     */
    public static JwtDecoder issuerAndAudienceValidatingJwtDecoder(
            final String issuerUri, final List<String> audiences) {
        final var jwtValidator =
                new DelegatingOAuth2TokenValidator<>(
                        List.of(issuerValidator(issuerUri), audienceValidator(audiences)));

        final var jwtDecoder = NimbusJwtDecoder.withIssuerLocation(issuerUri).build();
        jwtDecoder.setJwtValidator(jwtValidator);
        return jwtDecoder;
    }

    /**
     * Creates a JwtDecoder that validates issuer and audience claims.
     *
     * @param issuerUri String representation of the issuer URI.
     * @param audiences List of allowed audience values.
     * @param nimbusJwtDecoderCustomizer Consumer to customize the NimbusJwtDecoderBuilder. E.g. to
     *     set {@link JwkSetUriJwtDecoderBuilder#validateType(boolean)}
     * @return JwtDecoder instance.
     */
    public static JwtDecoder issuerAndAudienceValidatingJwtDecoder(
            final String issuerUri,
            final List<String> audiences,
            final Consumer<JwkSetUriJwtDecoderBuilder> nimbusJwtDecoderCustomizer) {
        final var jwtValidator =
                new DelegatingOAuth2TokenValidator<>(
                        List.of(issuerValidator(issuerUri), audienceValidator(audiences)));

        final var nimbusJwtDecoderBuilder = NimbusJwtDecoder.withIssuerLocation(issuerUri);

        nimbusJwtDecoderCustomizer.accept(nimbusJwtDecoderBuilder);

        final var jwtDecoder = nimbusJwtDecoderBuilder.build();
        jwtDecoder.setJwtValidator(jwtValidator);
        return jwtDecoder;
    }

    /**
     * Creates a JwtDecoder that validates issuer and audience claims.
     *
     * @param issuerUri String representation of the issuer URI.
     * @param audiences List of allowed audience values.
     * @param nimbusJwtDecoderCustomizer Consumer to customize the NimbusJwtDecoderBuilder. E.g. to
     *     set {@link JwkSetUriJwtDecoderBuilder#validateType(boolean)}
     * @return JwtDecoder instance.
     */
    public static JwtDecoder issuerAndAudienceValidatingAtJwtDecoder(
            final String issuerUri,
            final List<String> audiences,
            final Consumer<JwkSetUriJwtDecoderBuilder> nimbusJwtDecoderCustomizer) {
        final var jwtValidator =
                new DelegatingOAuth2TokenValidator<>(
                        List.of(
                                issuerValidator(issuerUri),
                                audienceValidator(audiences),
                                typeAtJwtValidator()));

        final var nimbusJwtDecoderBuilder = NimbusJwtDecoder.withIssuerLocation(issuerUri);
        // we are validating the type as an at+jwt with typeAtJwtValidator(), hence we need to
        // disable it here
        nimbusJwtDecoderBuilder.validateType(false);

        nimbusJwtDecoderCustomizer.accept(nimbusJwtDecoderBuilder);

        final var jwtDecoder = nimbusJwtDecoderBuilder.build();
        jwtDecoder.setJwtValidator(jwtValidator);
        return jwtDecoder;
    }
}
