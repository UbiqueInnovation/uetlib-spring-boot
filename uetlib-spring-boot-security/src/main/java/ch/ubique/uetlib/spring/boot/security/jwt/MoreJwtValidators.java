package ch.ubique.uetlib.spring.boot.security.jwt;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;

import java.util.Collections;
import java.util.List;

public final class MoreJwtValidators {

    private MoreJwtValidators() {}

    public static JwtTypeValidator typeAtJwtValidator() {
        return new JwtTypeValidator("at+jwt");
    }

    public static JwtClaimValidator<List<String>> audienceValidator(final List<String> audiences) {
        return new JwtClaimValidator<>(
                JwtClaimNames.AUD, claim -> hasElementsInCommon(claim, audiences));
    }

    public static OAuth2TokenValidator<Jwt> issuerValidator(final String issuer) {
        return new JwtIssuerValidator(issuer);
    }

    @Contract("null, _ -> false")
    static <E> boolean hasElementsInCommon(@Nullable final List<E> c1, final List<E> c2) {
        return c1 != null && !Collections.disjoint(c1, c2);
    }
}
