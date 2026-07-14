package ch.ubique.backend.security.jwt;

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

import java.util.Collection;
import java.util.List;
import java.util.Map;

@SuppressWarnings("SameParameterValue")
class MoreJwtDecodersTest {

    @SuppressWarnings("unchecked")
    private OAuth2TokenValidator<Jwt> getJwtValidator(final JwtDecoder jwtDecoder)
            throws Exception {
        final var field = NimbusJwtDecoder.class.getDeclaredField("jwtValidator");
        field.setAccessible(true);
        return (OAuth2TokenValidator<Jwt>) field.get(jwtDecoder);
    }

    @SuppressWarnings("unchecked")
    private Collection<OAuth2TokenValidator<Jwt>> getTokenValidators(
            final OAuth2TokenValidator<Jwt> validator) throws Exception {
        final var field = DelegatingOAuth2TokenValidator.class.getDeclaredField("tokenValidators");
        field.setAccessible(true);
        return (Collection<OAuth2TokenValidator<Jwt>>) field.get(validator);
    }

    @SuppressWarnings("unchecked")
    private RestOperations mockRestOperations(final String issuer) {
        final var restOperations = mock(RestOperations.class);

        final var configResponse =
                new ResponseEntity<Map<String, Object>>(
                        Map.of("issuer", issuer, "jwks_uri", issuer + "/jwks"), HttpStatus.OK);

        final var jwksResponse =
                new ResponseEntity<String>(
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
            final Collection<OAuth2TokenValidator<Jwt>> validators, final Class<?> clazz) {
        return validators.stream().anyMatch(clazz::isInstance);
    }

    @Test
    void issuerAndAudienceValidatingJwtDecoder_withAudiences_configuresDecoderCorrectly()
            throws Exception {
        final var issuer = "https://example.com/issuer";
        final Collection<String> audiences = List.of("aud1", "aud2");
        final var restOperations = mockRestOperations(issuer);

        final var jwtDecoder =
                MoreJwtDecoders.issuerAndAudienceValidatingJwtDecoder(
                        restOperations, issuer, audiences);

        assertThat(jwtDecoder).isNotNull().isInstanceOf(NimbusJwtDecoder.class);

        final var validator = getJwtValidator(jwtDecoder);
        assertThat(validator).isNotNull().isInstanceOf(DelegatingOAuth2TokenValidator.class);

        final var subValidators = getTokenValidators(validator);
        assertThat(subValidators).hasSize(5);
        assertThat(hasValidatorOfClass(subValidators, JwtTimestampValidator.class)).isTrue();
        assertThat(hasValidatorOfClass(subValidators, JwtIssuerValidator.class)).isTrue();
        assertThat(hasValidatorOfClass(subValidators, JwtClaimValidator.class)).isTrue();
        assertThat(hasValidatorOfClass(subValidators, JwtTypeValidator.class)).isTrue();
    }

    @Test
    void issuerAndAudienceValidatingJwtDecoder_withNullAudiences_configuresDecoderCorrectly()
            throws Exception {
        final var issuer = "https://example.com/issuer";
        final var restOperations = mockRestOperations(issuer);

        final var jwtDecoder =
                MoreJwtDecoders.issuerAndAudienceValidatingJwtDecoder(restOperations, issuer, null);

        assertThat(jwtDecoder).isNotNull().isInstanceOf(NimbusJwtDecoder.class);

        final var validator = getJwtValidator(jwtDecoder);
        assertThat(validator).isNotNull();

        final var subValidators = getTokenValidators(validator);
        assertThat(subValidators).hasSize(4);
        assertThat(hasValidatorOfClass(subValidators, JwtTimestampValidator.class)).isTrue();
        assertThat(hasValidatorOfClass(subValidators, JwtIssuerValidator.class)).isTrue();
        assertThat(hasValidatorOfClass(subValidators, JwtClaimValidator.class)).isFalse();
        assertThat(hasValidatorOfClass(subValidators, JwtTypeValidator.class)).isTrue();
    }

    @Test
    void issuerAndAudienceValidatingAtJwtDecoder_configuresDecoderCorrectly() throws Exception {
        final var issuer = "https://example.com/issuer";
        final Collection<String> audiences = List.of("aud1");
        final var restOperations = mockRestOperations(issuer);

        final var jwtDecoder =
                MoreJwtDecoders.issuerAndAudienceValidatingAtJwtDecoder(
                        restOperations, issuer, audiences);

        assertThat(jwtDecoder).isNotNull();

        final var validator = getJwtValidator(jwtDecoder);
        assertThat(validator).isNotNull();

        final var subValidators = getTokenValidators(validator);
        assertThat(subValidators).hasSize(5);
        assertThat(hasValidatorOfClass(subValidators, JwtTimestampValidator.class)).isTrue();
        assertThat(hasValidatorOfClass(subValidators, JwtIssuerValidator.class)).isTrue();
        assertThat(hasValidatorOfClass(subValidators, JwtClaimValidator.class)).isTrue();
        assertThat(hasValidatorOfClass(subValidators, JwtTypeValidator.class)).isTrue();
    }
}
