package ch.ubique.backend.security.jwt;

import static ch.ubique.backend.security.jwt.MoreJwtValidators.*;

import org.jspecify.annotations.Nullable;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder.JwkSetUriJwtDecoderBuilder;
import org.springframework.web.client.RestOperations;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings({"unused", "java:S1133", "DeprecatedIsStillUsed"})
public final class MoreJwtDecoders {

    private MoreJwtDecoders() {}

    /**
     * Creates a new Builder for configuring and creating a JwtDecoder.
     *
     * @param restOperations RestOperations A pre-configured RestOperations instance, most likely an
     *     instance of {@link RestTemplateBuilder#build()}.
     * @param issuerUri String representation of the issuer URI.
     * @return Builder instance.
     */
    public static Builder builder(final RestOperations restOperations, final String issuerUri) {
        return new Builder(restOperations, issuerUri);
    }

    /**
     * Creates a JwtDecoder that validates issuer and audience claims.
     *
     * @param restOperations RestOperations A pre-configured RestOperations instance, most likely an
     *     instance of {@link RestTemplateBuilder#build()}.
     * @param issuerUri String representation of the issuer URI.
     * @param audiences List of allowed audience values or null to allow any audience.
     * @return JwtDecoder instance.
     * @deprecated since 2.x, for removal in 3.0.0. Use:
     *     {@snippet :
     *     MoreJwtDecoders.builder(restOperations, issuerUri)
     *             .audiences(audiences)
     *             .build();
     *     }
     */
    // TODO remove in 3.0.0
    @Deprecated(forRemoval = true)
    public static JwtDecoder issuerAndAudienceValidatingJwtDecoder(
            final RestOperations restOperations,
            final String issuerUri,
            final @Nullable List<String> audiences) {
        return builder(restOperations, issuerUri).audiences(audiences).build();
    }

    /**
     * Creates a JwtDecoder that validates issuer and audience claims.
     *
     * @param restOperations RestOperations A pre-configured RestOperations instance, most likely an
     *     instance of {@link RestTemplateBuilder#build()}. * @param issuerUri String representation
     *     of the issuer URI.
     * @param audiences List of allowed audience values or null to allow any audience.
     * @param jwtDecoderCustomizer Consumer to customize the NimbusJwtDecoderBuilder. E.g. to set
     *     {@link JwkSetUriJwtDecoderBuilder#validateType(boolean)}
     * @return JwtDecoder instance.
     * @deprecated since 2.x, for removal in 3.0.0. Use:
     *     {@snippet :
     *     MoreJwtDecoders.builder(restOperations, issuerUri)
     *             .audiences(audiences)
     *             .customizer(jwtDecoderCustomizer)
     *             .build();
     *     }
     */
    // TODO remove in 3.0.0
    @Deprecated(forRemoval = true)
    public static JwtDecoder issuerAndAudienceValidatingJwtDecoder(
            final RestOperations restOperations,
            final String issuerUri,
            final @Nullable List<String> audiences,
            final Consumer<JwkSetUriJwtDecoderBuilder> jwtDecoderCustomizer) {
        return builder(restOperations, issuerUri)
                .audiences(audiences)
                .customizer(jwtDecoderCustomizer)
                .build();
    }

    /**
     * Creates a JwtDecoder that validates issuer and audience claims and the type {@code at+jwt}.
     *
     * @param restOperations RestOperations A pre-configured RestOperations instance, most likely an
     *     instance of {@link RestTemplateBuilder#build()}.
     * @param issuerUri String representation of the issuer URI.
     * @param audiences List of allowed audience values.
     * @return JwtDecoder instance.
     * @deprecated since 2.x, for removal in 3.0.0. Use:
     *     {@snippet :
     *     MoreJwtDecoders.builder(restOperations, issuerUri)
     *             .audiences(audiences)
     *             .validTypes(List.of("at+jwt"))
     *             .build();
     *     }
     */
    // TODO remove in 3.0.0
    @Deprecated(forRemoval = true)
    public static JwtDecoder issuerAndAudienceValidatingAtJwtDecoder(
            final RestOperations restOperations,
            final String issuerUri,
            final List<String> audiences) {
        return builder(restOperations, issuerUri)
                .audiences(audiences)
                .validTypes(List.of("at+jwt"))
                .build();
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
     * @deprecated since 2.x, for removal in 3.0.0. Use:
     *     {@snippet :
     *     MoreJwtDecoders.builder(restOperations, issuerUri)
     *             .audiences(audiences)
     *             .validTypes(List.of("at+jwt"))
     *             .customizer(additionalJwtDecoderCustomizer)
     *             .build();
     *     }
     */
    // TODO remove in 3.0.0
    @Deprecated(forRemoval = true)
    public static JwtDecoder issuerAndAudienceValidatingAtJwtDecoder(
            final RestOperations restOperations,
            final String issuerUri,
            final List<String> audiences,
            final Consumer<JwkSetUriJwtDecoderBuilder> additionalJwtDecoderCustomizer) {
        return builder(restOperations, issuerUri)
                .audiences(audiences)
                .validTypes(List.of("at+jwt"))
                .customizer(additionalJwtDecoderCustomizer)
                .build();
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
     * @deprecated since 2.x, for removal in 3.0.0. Use:
     *     {@snippet :
     *     var builder = MoreJwtDecoders.builder(restOperations, issuerUri)
     *             .audiences(audiences)
     *             .customizer(additionalJwtDecoderCustomizer);
     *     for (var validator : additionalTokenValidators) {
     *         builder.addTokenValidator(validator);
     *     }
     *     var decoder = builder.build();
     *     }
     */
    // TODO remove in 3.0.0
    @Deprecated(forRemoval = true)
    public static JwtDecoder issuerAndAudienceValidatingJwtDecoder(
            final RestOperations restOperations,
            final String issuerUri,
            final @Nullable List<String> audiences,
            final List<OAuth2TokenValidator<Jwt>> additionalTokenValidators,
            final Consumer<JwkSetUriJwtDecoderBuilder> additionalJwtDecoderCustomizer) {
        final var builder =
                builder(restOperations, issuerUri)
                        .audiences(audiences)
                        .customizer(additionalJwtDecoderCustomizer);
        for (final var validator : additionalTokenValidators) {
            builder.addTokenValidator(validator);
        }
        return builder.build();
    }

    public static final class Builder {

        /** The RestOperations instance used to fetch OIDC configuration metadata and JWKS. */
        private final RestOperations restOperations;

        /** The issuer URI of the JWT token provider. */
        private final String issuerUri;

        /** The list of allowed audience values, or null if any audience is accepted. */
        private @Nullable List<String> audiences;

        /** The list of allowed token types (e.g. "JWT", "at+jwt"). */
        private List<String> validTypes = List.of();

        /** Additional token validators to apply during JWT validation. */
        private final List<OAuth2TokenValidator<Jwt>> additionalTokenValidators = new ArrayList<>();

        /** Customizer for the NimbusJwtDecoder builder. */
        private Consumer<NimbusJwtDecoder.JwkSetUriJwtDecoderBuilder> jwtDecoderCustomizer =
                b -> {};

        private Builder(final RestOperations restOperations, final String issuerUri) {
            this.restOperations = restOperations;
            this.issuerUri = issuerUri;
        }

        /**
         * Sets the list of allowed audience values.
         *
         * @param audiences List of allowed audience values or null to allow any audience.
         * @return This Builder instance.
         */
        public Builder audiences(final @Nullable List<String> audiences) {
            this.audiences = audiences;
            return this;
        }

        /**
         * Sets the list of valid token types.
         *
         * <p>If the list of valid types is not empty and contains types other than just "JWT", the
         * builder will automatically configure the Nimbus decoder with {@code validateType(false)}
         * to bypass the default Nimbus type validation. In that case, validation of the configured
         * types is instead delegated to a custom {@link JwtTypeValidator} inside the builder.
         *
         * @param validTypes List of valid token types.
         * @return This Builder instance.
         */
        public Builder validTypes(final List<String> validTypes) {
            this.validTypes = validTypes;
            return this;
        }

        /**
         * Adds an additional token validator.
         *
         * <p>Note that if you add a custom {@link JwtIssuerValidator} or {@link JwtTypeValidator},
         * the builder will automatically skip registering the default issuer or type validators to
         * prevent duplicate validation.
         *
         * @param validator Additional OAuth2TokenValidator to apply.
         * @return This Builder instance.
         */
        @SuppressWarnings("UnusedReturnValue")
        public Builder addTokenValidator(final OAuth2TokenValidator<Jwt> validator) {
            additionalTokenValidators.add(validator);
            return this;
        }

        /**
         * Adds a customizer consumer to further configure the {@link
         * NimbusJwtDecoder.JwkSetUriJwtDecoderBuilder}. Multiple customizers will be chained
         * together in order of addition.
         *
         * @param customizer Consumer to customize the Nimbus builder.
         * @return This Builder instance.
         */
        public Builder customizer(
                final Consumer<NimbusJwtDecoder.JwkSetUriJwtDecoderBuilder> customizer) {
            this.jwtDecoderCustomizer = jwtDecoderCustomizer.andThen(customizer);
            return this;
        }

        /**
         * Builds the configured {@link JwtDecoder}.
         *
         * @return A fully configured JwtDecoder instance.
         */
        public JwtDecoder build() {
            final var jwtValidators = new ArrayList<OAuth2TokenValidator<Jwt>>();
            final var hasIssuerValidator =
                    additionalTokenValidators.stream()
                            .anyMatch(JwtIssuerValidator.class::isInstance);
            if (!hasIssuerValidator) {
                jwtValidators.add(new JwtIssuerValidator(issuerUri));
            }

            if (audiences != null) {
                jwtValidators.add(audienceValidator(audiences));
            }

            final var hasTypeValidator =
                    additionalTokenValidators.stream().anyMatch(JwtTypeValidator.class::isInstance);
            if (!hasTypeValidator) {
                final var typeValidator =
                        validTypes.isEmpty()
                                ? JwtTypeValidator.jwt()
                                : new JwtTypeValidator(List.copyOf(validTypes));
                jwtValidators.add(typeValidator);
            }

            jwtValidators.addAll(additionalTokenValidators);

            final var jwtValidator = JwtValidators.createDefaultWithValidators(jwtValidators);

            final var jwtDecoderBuilder = NimbusJwtDecoder.withIssuerLocation(issuerUri);
            jwtDecoderBuilder.restOperations(restOperations);

            // Automatically set validateType(false) if we have custom validTypes (i.e. not the
            // default "JWT")
            final var isDefaultJwtType = validTypes.isEmpty() || validTypes.equals(List.of("JWT"));
            if (!isDefaultJwtType) {
                jwtDecoderBuilder.validateType(false);
            }

            jwtDecoderCustomizer.accept(jwtDecoderBuilder);

            final var jwtDecoder = jwtDecoderBuilder.build();
            jwtDecoder.setJwtValidator(jwtValidator);
            return jwtDecoder;
        }
    }
}
