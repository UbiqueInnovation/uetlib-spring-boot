package ch.ubique.backend.security.jwt;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;

import java.util.Collection;
import java.util.Collections;

public final class MoreJwtValidators {

    private MoreJwtValidators() {}

    @NonNull
    public static JwtTypeValidator typeAtJwtValidator() {
        return new JwtTypeValidator("at+jwt");
    }

    @NonNull
    public static JwtClaimValidator<Collection<String>> audienceValidator(
            final Collection<String> audiences) {
        return new JwtClaimValidator<>(
                JwtClaimNames.AUD, aud -> nullSafeNotDisjoint(aud, audiences));
    }

    @NonNull
    public static OAuth2TokenValidator<Jwt> issuerValidator(final @NonNull String issuer) {
        return new JwtIssuerValidator(issuer);
    }

    @Contract("null, _ -> false")
    private static boolean nullSafeNotDisjoint(
            final Collection<String> c1, final Collection<String> c2) {
        return c1 != null && !Collections.disjoint(c1, c2);
    }
}
