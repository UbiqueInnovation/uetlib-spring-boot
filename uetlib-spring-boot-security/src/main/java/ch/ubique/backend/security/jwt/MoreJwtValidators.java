package ch.ubique.backend.security.jwt;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;

import java.util.Collection;
import java.util.Collections;

public final class MoreJwtValidators {

    private MoreJwtValidators() {}

    public static JwtTypeValidator typeAtJwtValidator() {
        return new JwtTypeValidator("at+jwt");
    }

    public static JwtClaimValidator<Collection<String>> audienceValidator(
            final Collection<String> audiences) {
        return new JwtClaimValidator<>(
                JwtClaimNames.AUD, aud -> nullSafeNotDisjoint(aud, audiences));
    }

    public static OAuth2TokenValidator<Jwt> issuerValidator(final String issuer) {
        return new JwtIssuerValidator(issuer);
    }

    @Contract("null, _ -> false; _, null -> false")
    static boolean nullSafeNotDisjoint(
            final @Nullable Collection<String> c1, final @Nullable Collection<String> c2) {
        return c1 != null && c2 != null && !Collections.disjoint(c1, c2);
    }
}
