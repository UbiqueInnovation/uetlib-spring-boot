package ch.ubique.backend.security.jwt;

import org.jspecify.annotations.NonNull;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;

import java.util.Collections;
import java.util.List;

public final class MoreJwtValidators {

    private MoreJwtValidators() {}

    public static @NonNull JwtTypeValidator typeAtJwtValidator() {
        return new JwtTypeValidator("at+jwt");
    }

    public static @NonNull JwtClaimValidator<List<String>> audienceValidator(
            final List<String> audiences) {
        return new JwtClaimValidator<>(
                JwtClaimNames.AUD, aud -> nullSafeNotDisjoint(aud, audiences));
    }

    @NonNull
    public static OAuth2TokenValidator<Jwt> issuerValidator(final @NonNull String issuerUri) {
        return JwtValidators.createDefaultWithIssuer(issuerUri);
    }

    private static boolean nullSafeNotDisjoint(final List<String> c1, final List<String> c2) {
        return c1 != null && !Collections.disjoint(c1, c2);
    }
}
