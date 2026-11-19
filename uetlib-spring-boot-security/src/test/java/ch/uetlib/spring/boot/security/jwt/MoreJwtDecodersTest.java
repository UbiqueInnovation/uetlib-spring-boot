package ch.uetlib.spring.boot.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.JwtTypeValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.web.client.RestOperations;

import java.util.List;
import java.util.Map;

@SuppressWarnings({"SameParameterValue", "removal"})
class MoreJwtDecodersTest {

    @SuppressWarnings("unchecked")
    private OAuth2TokenValidator<Jwt> getJwtValidator(final JwtDecoder jwtDecoder)
            throws Exception {
        final var field = NimbusJwtDecoder.class.getDeclaredField("jwtValidator");
        field.setAccessible(true);
        return (OAuth2TokenValidator<Jwt>) field.get(jwtDecoder);
    }

    @SuppressWarnings("unchecked")
    private List<OAuth2TokenValidator<Jwt>> getTokenValidators(
            final OAuth2TokenValidator<Jwt> validator) throws Exception {
        final var field = DelegatingOAuth2TokenValidator.class.getDeclaredField("tokenValidators");
        field.setAccessible(true);
        return (List<OAuth2TokenValidator<Jwt>>) field.get(validator);
    }

    @SuppressWarnings("unchecked")
    private RestOperations mockRestOperations(final String issuer) {
        final var restOperations = mock(RestOperations.class);

        final var configResponse =
                new ResponseEntity<Map<String, Object>>(
                        Map.of("issuer", issuer, "jwks_uri", issuer + "/jwks"), HttpStatus.OK);

        final var jwksResponse =
                new ResponseEntity<>(
                        "{\"keys\":[{\"kty\":\"EC\",\"crv\":\"P-256\",\"x\":\"f83OJ3D2xF1Bg8vub9tLe1gHMzV76e8Tus9uPHvRVEU\",\"y\":\"x_FEzRu9m36HLN_tue659LNpXW6pCyStikYjKIWI5a0\",\"kid\":\"example-key-id\",\"use\":\"sig\",\"alg\":\"ES256\"}]}",
                        HttpStatus.OK);

        when(restOperations.exchange(any(RequestEntity.class), any(Class.class)))
                .thenAnswer(
                        invocation -> {
                            final Class<?> responseType = invocation.getArgument(1);
                            if (responseType == String.class) {
                                return jwksResponse;
                            }
                            return configResponse;
                        });

        when(restOperations.exchange(
                        any(RequestEntity.class), any(ParameterizedTypeReference.class)))
                .thenAnswer(invocation -> configResponse);

        return restOperations;
    }

    private boolean hasValidatorOfClass(
            final List<OAuth2TokenValidator<Jwt>> validators, final Class<?> clazz) {
        return validators.stream().anyMatch(clazz::isInstance);
    }

    private boolean hasValidatorOfClassName(
            final List<OAuth2TokenValidator<Jwt>> validators, final String simpleClassName) {
        return validators.stream()
                .anyMatch(v -> v.getClass().getSimpleName().equals(simpleClassName));
    }

    // ======== Builder tests ========

    @Test
    void builder_withAudiences_configuresDecoderCorrectly() throws Exception {
        final var issuer = "https://example.com/issuer";
        final var audiences = List.of("aud1", "aud2");
        final var restOperations = mockRestOperations(issuer);

        final var jwtDecoder =
                MoreJwtDecoders.builder(restOperations, issuer).audiences(audiences).build();

        assertThat(jwtDecoder).isNotNull().isInstanceOf(NimbusJwtDecoder.class);

        final var validator = getJwtValidator(jwtDecoder);
        assertThat(validator).isNotNull().isInstanceOf(DelegatingOAuth2TokenValidator.class);

        final var subValidators = getTokenValidators(validator);
        assertThat(subValidators).hasSize(5);
        assertThat(hasValidatorOfClass(subValidators, JwtTimestampValidator.class)).isTrue();
        assertThat(hasValidatorOfClass(subValidators, JwtIssuerValidator.class)).isTrue();
        assertThat(hasValidatorOfClass(subValidators, JwtClaimValidator.class)).isTrue();
        assertThat(hasValidatorOfClass(subValidators, JwtTypeValidator.class)).isTrue();
        assertThat(hasValidatorOfClassName(subValidators, "X509CertificateThumbprintValidator"))
                .isTrue();
    }

    @Test
    void builder_withCustomValidTypes_configuresDecoderCorrectly() throws Exception {
        final var issuer = "https://example.com/issuer";
        final var audiences = List.of("aud1");
        final var restOperations = mockRestOperations(issuer);
        final var validTypes = List.of("custom-typ");

        final var jwtDecoder =
                MoreJwtDecoders.builder(restOperations, issuer)
                        .audiences(audiences)
                        .validTypes(validTypes)
                        .build();

        assertThat(jwtDecoder).isNotNull();

        final var validator = getJwtValidator(jwtDecoder);
        assertThat(validator).isNotNull();

        final var subValidators = getTokenValidators(validator);
        assertThat(subValidators).hasSize(5);
        assertThat(hasValidatorOfClass(subValidators, JwtTimestampValidator.class)).isTrue();
        assertThat(hasValidatorOfClass(subValidators, JwtIssuerValidator.class)).isTrue();
        assertThat(hasValidatorOfClass(subValidators, JwtClaimValidator.class)).isTrue();
        assertThat(hasValidatorOfClass(subValidators, JwtTypeValidator.class)).isTrue();
        assertThat(hasValidatorOfClassName(subValidators, "X509CertificateThumbprintValidator"))
                .isTrue();
    }

    @Test
    void builder_withCustomJwtTypeValidator_doesNotAddDefaultTypeValidator() throws Exception {
        final var issuer = "https://example.com/issuer";
        final var audiences = List.of("aud1");
        final var restOperations = mockRestOperations(issuer);
        final var customTypeValidator = new JwtTypeValidator("custom-type");

        final var jwtDecoder =
                MoreJwtDecoders.builder(restOperations, issuer)
                        .audiences(audiences)
                        .addTokenValidator(customTypeValidator)
                        .build();

        final var validator = getJwtValidator(jwtDecoder);
        final var subValidators = getTokenValidators(validator);

        // Should have exactly 1 JwtTypeValidator (the custom one), not 2
        final var typeValidatorCount =
                subValidators.stream().filter(JwtTypeValidator.class::isInstance).count();
        assertThat(typeValidatorCount).isEqualTo(1);
        assertThat(subValidators).contains(customTypeValidator);
    }

    @Test
    void builder_withCustomJwtIssuerValidator_doesNotAddDefaultIssuerValidator() throws Exception {
        final var issuer = "https://example.com/issuer";
        final var audiences = List.of("aud1");
        final var restOperations = mockRestOperations(issuer);
        final var customIssuerValidator = new JwtIssuerValidator(issuer);

        final var jwtDecoder =
                MoreJwtDecoders.builder(restOperations, issuer)
                        .audiences(audiences)
                        .addTokenValidator(customIssuerValidator)
                        .build();

        final var validator = getJwtValidator(jwtDecoder);
        final var subValidators = getTokenValidators(validator);

        // Should have exactly 1 JwtIssuerValidator (the custom one), not 2
        final var issuerValidatorCount =
                subValidators.stream().filter(JwtIssuerValidator.class::isInstance).count();
        assertThat(issuerValidatorCount).isEqualTo(1);
        assertThat(subValidators).contains(customIssuerValidator);
    }
}
