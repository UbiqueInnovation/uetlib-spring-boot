package ch.ubique.uetlib.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;

@SuppressWarnings({"SameParameterValue", "DataFlowIssue"})
class MoreJwtValidatorsTest {

    private static Jwt jwtWithHeader(final String key, final String value) {
        return Jwt.withTokenValue("token")
                .header("alg", "none")
                .header(key, value)
                .claim("sub", "user")
                .build();
    }

    private static Jwt jwtWithClaim(final String key, final Object value) {
        return Jwt.withTokenValue("token").header("alg", "none").claim(key, value).build();
    }

    @Test
    void typeAtJwtValidator_withCorrectType_returnsSuccess() {
        final OAuth2TokenValidator<Jwt> validator = MoreJwtValidators.typeAtJwtValidator();
        final var jwt = jwtWithHeader("typ", "at+jwt");

        final var result = validator.validate(jwt);

        assertThat(result.hasErrors()).isFalse();
    }

    @Test
    void typeAtJwtValidator_withIncorrectType_returnsFailure() {
        final OAuth2TokenValidator<Jwt> validator = MoreJwtValidators.typeAtJwtValidator();
        final var jwt = jwtWithHeader("typ", "JWT");

        final var result = validator.validate(jwt);

        assertThat(result.hasErrors()).isTrue();
    }

    @Test
    void typeAtJwtValidator_withoutType_returnsFailure() {
        final OAuth2TokenValidator<Jwt> validator = MoreJwtValidators.typeAtJwtValidator();
        final var jwt =
                Jwt.withTokenValue("token").header("alg", "none").claim("sub", "user").build();

        final var result = validator.validate(jwt);

        assertThat(result.hasErrors()).isTrue();
    }

    @Test
    void audienceValidator_withOverlappingAudience_returnsSuccess() {
        final OAuth2TokenValidator<Jwt> validator =
                MoreJwtValidators.audienceValidator(List.of("aud1", "aud2"));
        final var jwt = jwtWithClaim("aud", List.of("aud2", "aud3"));

        final var result = validator.validate(jwt);

        assertThat(result.hasErrors()).isFalse();
    }

    @Test
    void audienceValidator_withSingleAudienceInListOverlapping_returnsSuccess() {
        final OAuth2TokenValidator<Jwt> validator =
                MoreJwtValidators.audienceValidator(List.of("aud1", "aud2"));
        final var jwt = jwtWithClaim("aud", List.of("aud2"));

        final var result = validator.validate(jwt);

        assertThat(result.hasErrors()).isFalse();
    }

    @Test
    void audienceValidator_withNonOverlappingAudience_returnsFailure() {
        final OAuth2TokenValidator<Jwt> validator =
                MoreJwtValidators.audienceValidator(List.of("aud1", "aud2"));
        final var jwt = jwtWithClaim("aud", List.of("aud3", "aud4"));

        final var result = validator.validate(jwt);

        assertThat(result.hasErrors()).isTrue();
    }

    @Test
    void audienceValidator_withEmptyAudienceInJwt_returnsFailure() {
        final OAuth2TokenValidator<Jwt> validator =
                MoreJwtValidators.audienceValidator(List.of("aud1", "aud2"));
        final var jwt = jwtWithClaim("aud", List.of());

        final var result = validator.validate(jwt);

        assertThat(result.hasErrors()).isTrue();
    }

    @Test
    void audienceValidator_withNullAudienceInJwt_returnsFailure() {
        final OAuth2TokenValidator<Jwt> validator =
                MoreJwtValidators.audienceValidator(List.of("aud1", "aud2"));
        final var jwt =
                Jwt.withTokenValue("token").header("alg", "none").claim("sub", "user").build();

        final var result = validator.validate(jwt);

        assertThat(result.hasErrors()).isTrue();
    }

    @Test
    void audienceValidator_withEmptyAllowedAudiences_returnsFailure() {
        final OAuth2TokenValidator<Jwt> validator = MoreJwtValidators.audienceValidator(List.of());
        final var jwt = jwtWithClaim("aud", List.of("aud1"));

        final var result = validator.validate(jwt);

        assertThat(result.hasErrors()).isTrue();
    }

    @Test
    void issuerValidator_withMatchingIssuer_returnsSuccess() {
        final var issuer = "https://issuer.example.com";
        final var validator = MoreJwtValidators.issuerValidator(issuer);
        final var jwt = jwtWithClaim("iss", issuer);

        final var result = validator.validate(jwt);

        assertThat(result.hasErrors()).isFalse();
    }

    @Test
    void issuerValidator_withMismatchingIssuer_returnsFailure() {
        final var validator = MoreJwtValidators.issuerValidator("https://issuer.example.com");
        final var jwt = jwtWithClaim("iss", "https://other.example.com");

        final var result = validator.validate(jwt);

        assertThat(result.hasErrors()).isTrue();
    }

    @Test
    void issuerValidator_withNullIssuerInJwt_returnsFailure() {
        final var validator = MoreJwtValidators.issuerValidator("https://issuer.example.com");
        final var jwt =
                Jwt.withTokenValue("token").header("alg", "none").claim("sub", "user").build();

        final var result = validator.validate(jwt);

        assertThat(result.hasErrors()).isTrue();
    }

    @Test
    void hasElementsInCommon_withFirstNull_returnsFalse() {
        assertThat(MoreJwtValidators.hasElementsInCommon(null, List.of("a"))).isFalse();
    }

    @Test
    void hasElementsInCommon_withDisjointLists_returnsFalse() {
        assertThat(MoreJwtValidators.hasElementsInCommon(List.of("a", "b"), List.of("c", "d")))
                .isFalse();
    }

    @Test
    void hasElementsInCommon_withOverlappingLists_returnsTrue() {
        assertThat(MoreJwtValidators.hasElementsInCommon(List.of("a", "b"), List.of("b", "c")))
                .isTrue();
    }

    @Test
    void hasElementsInCommon_withEmptyLists_returnsFalse() {
        assertThat(MoreJwtValidators.hasElementsInCommon(List.of(), List.of())).isFalse();
    }
}
