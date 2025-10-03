package ch.ubique.backend.security.jwt;

import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder.JwkSetUriJwtDecoderBuilder;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public final class MoreJwtValidators {
    private MoreJwtValidators() {}

    /**
     * Creates a JwtDecoder that validates issuer and audience claims.
     *
     * @param issuerUri String representation of the issuer URI.
     * @param audiences List of allowed audience values.
     * @return JwtDecoder instance.
     */
    public static JwtDecoder issuerAndAudienceValidatingJwtDecoder(
            final String issuerUri, final List<String> audiences) {
        final var issuerValidator = JwtValidators.createDefaultWithIssuer(issuerUri);
        final var audValidator =
                new JwtClaimValidator<List<String>>(
                        JwtClaimNames.AUD, aud -> nullSafeNotDisjoint(aud, audiences));
        final var jwtValidator =
                new DelegatingOAuth2TokenValidator<>(List.of(issuerValidator, audValidator));

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
        final var issuerValidator = JwtValidators.createDefaultWithIssuer(issuerUri);
        final var audValidator =
                new JwtClaimValidator<List<String>>(
                        JwtClaimNames.AUD, aud -> nullSafeNotDisjoint(aud, audiences));
        final var jwtValidator =
                new DelegatingOAuth2TokenValidator<>(List.of(issuerValidator, audValidator));

        final var nimbusJwtDecoderBuilder = NimbusJwtDecoder.withIssuerLocation(issuerUri);

        nimbusJwtDecoderCustomizer.accept(nimbusJwtDecoderBuilder);

        final var jwtDecoder = nimbusJwtDecoderBuilder.build();
        jwtDecoder.setJwtValidator(jwtValidator);
        return jwtDecoder;
    }

    private static boolean nullSafeNotDisjoint(final List<String> c1, final List<String> c2) {
        return c1 != null && !Collections.disjoint(c1, c2);
    }
}
