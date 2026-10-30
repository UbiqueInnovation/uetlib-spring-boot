package ch.ubique.uetlib.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

@SuppressWarnings("ALL")
class JwtUtilTest {

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private void setupJwt(final Jwt jwt) {
        final var auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(jwt);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private static Jwt.Builder jwt() {
        return Jwt.withTokenValue("token").header("alg", "none");
    }

    // ======== getJwt() ========

    @Test
    void getJwt_withValidJwtPrincipal_returnsJwt() {
        final var jwt = jwt().claim("sub", "user").build();
        setupJwt(jwt);

        assertSame(jwt, JwtUtil.getJwt());
    }

    @Test
    void getJwt_withNoAuthentication_throwsBadCredentials() {
        assertThrows(BadCredentialsException.class, JwtUtil::getJwt);
    }

    @Test
    void getJwt_withNullAuthentication_throwsBadCredentials() {
        SecurityContextHolder.getContext().setAuthentication(null);
        assertThrows(BadCredentialsException.class, JwtUtil::getJwt);
    }

    @Test
    void getJwt_withNonJwtPrincipal_throwsBadCredentials() {
        final var auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn("not-a-jwt");
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThrows(BadCredentialsException.class, JwtUtil::getJwt);
    }

    @Test
    void getJwt_withNullPrincipal_throwsBadCredentials() {
        final var auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(null);
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThrows(BadCredentialsException.class, JwtUtil::getJwt);
    }

    // ======== getTokenValue() ========

    @Test
    void getTokenValue_returnsTokenValue() {
        setupJwt(
                Jwt.withTokenValue("my-bearer-token")
                        .header("alg", "none")
                        .claim("sub", "u")
                        .build());

        assertEquals("my-bearer-token", JwtUtil.getTokenValue());
    }

    // ======== getClaimAsString() ========

    @Test
    void getClaimAsString_claimPresent_returnsClaim() {
        setupJwt(jwt().claim("name", "John").build());

        assertEquals("John", JwtUtil.getClaimAsString("name"));
    }

    @Test
    void getClaimAsString_claimAbsent_throwsAccessDenied() {
        setupJwt(jwt().claim("other", "value").build());

        assertThrows(AccessDeniedException.class, () -> JwtUtil.getClaimAsString("name"));
    }

    @Test
    void getClaimAsString_claimAbsent_errorMessageContainsClaimName() {
        setupJwt(jwt().claim("other", "value").build());

        final var ex =
                assertThrows(
                        AccessDeniedException.class,
                        () -> JwtUtil.getClaimAsString("my-special-claim"));
        assertTrue(ex.getMessage().contains("my-special-claim"));
    }

    @Test
    void getClaimAsString_withFallback_primaryPresent_returnsPrimary() {
        setupJwt(
                jwt().claim("primary", "primary-value")
                        .claim("fallback", "fallback-value")
                        .build());

        assertEquals("primary-value", JwtUtil.getClaimAsString("primary", "fallback"));
    }

    @Test
    void getClaimAsString_withFallback_primaryAbsent_returnsFallback() {
        setupJwt(jwt().claim("fallback", "fallback-value").build());

        assertEquals("fallback-value", JwtUtil.getClaimAsString("primary", "fallback"));
    }

    @Test
    void getClaimAsString_withFallback_bothAbsent_throwsAccessDenied() {
        setupJwt(jwt().claim("other", "value").build());

        assertThrows(
                AccessDeniedException.class, () -> JwtUtil.getClaimAsString("primary", "fallback"));
    }

    // ======== getClaimAsStringList() ========

    @Test
    void getClaimAsStringList_claimPresent_returnsList() {
        setupJwt(jwt().claim("roles", List.of("admin", "user")).build());

        assertEquals(List.of("admin", "user"), JwtUtil.getClaimAsStringList("roles"));
    }

    @Test
    void getClaimAsStringList_claimAbsent_throwsAccessDenied() {
        setupJwt(jwt().claim("other", "value").build());

        assertThrows(AccessDeniedException.class, () -> JwtUtil.getClaimAsStringList("roles"));
    }

    @Test
    void getClaimAsStringList_withFallback_primaryPresent_returnsPrimary() {
        setupJwt(
                jwt().claim("primary", List.of("a", "b"))
                        .claim("fallback", List.of("c", "d"))
                        .build());

        assertEquals(List.of("a", "b"), JwtUtil.getClaimAsStringList("primary", "fallback"));
    }

    @Test
    void getClaimAsStringList_withFallback_primaryAbsent_returnsFallback() {
        setupJwt(jwt().claim("fallback", List.of("c", "d")).build());

        assertEquals(List.of("c", "d"), JwtUtil.getClaimAsStringList("primary", "fallback"));
    }

    @Test
    void getClaimAsStringList_withFallback_bothAbsent_throwsAccessDenied() {
        setupJwt(jwt().claim("other", "value").build());

        assertThrows(
                AccessDeniedException.class,
                () -> JwtUtil.getClaimAsStringList("primary", "fallback"));
    }

    // ======== getClaimAsBoolean() ========

    @Test
    void getClaimAsBoolean_trueValue_returnsTrue() {
        setupJwt(jwt().claim("active", true).build());

        assertTrue(JwtUtil.getClaimAsBoolean("active"));
    }

    @Test
    void getClaimAsBoolean_falseValue_returnsFalse() {
        setupJwt(jwt().claim("active", false).build());

        assertFalse(JwtUtil.getClaimAsBoolean("active"));
    }

    @Test
    void getClaimAsBoolean_claimAbsent_throwsAccessDenied() {
        setupJwt(jwt().claim("other", "value").build());

        assertThrows(AccessDeniedException.class, () -> JwtUtil.getClaimAsBoolean("active"));
    }

    @Test
    void getClaimAsBoolean_withFallback_primaryPresent_returnsPrimary() {
        setupJwt(jwt().claim("primary", true).claim("fallback", false).build());

        assertTrue(JwtUtil.getClaimAsBoolean("primary", "fallback"));
    }

    @Test
    void getClaimAsBoolean_withFallback_primaryAbsent_returnsFallback() {
        setupJwt(jwt().claim("fallback", true).build());

        assertTrue(JwtUtil.getClaimAsBoolean("primary", "fallback"));
    }

    @Test
    void getClaimAsBoolean_withFallback_bothAbsent_throwsAccessDenied() {
        setupJwt(jwt().claim("other", "value").build());

        assertThrows(
                AccessDeniedException.class,
                () -> JwtUtil.getClaimAsBoolean("primary", "fallback"));
    }

    // ======== getClaimAsInstant() ========

    @Test
    void getClaimAsInstant_claimPresent_returnsInstant() {
        final var now = Instant.ofEpochSecond(1_700_000_000);
        setupJwt(jwt().claim("ts", now).build());

        assertEquals(now, JwtUtil.getClaimAsInstant("ts"));
    }

    @Test
    void getClaimAsInstant_claimAbsent_throwsAccessDenied() {
        setupJwt(jwt().claim("other", "value").build());

        assertThrows(AccessDeniedException.class, () -> JwtUtil.getClaimAsInstant("ts"));
    }

    @Test
    void getClaimAsInstant_withFallback_primaryPresent_returnsPrimary() {
        final var t1 = Instant.ofEpochSecond(1000);
        final var t2 = Instant.ofEpochSecond(2000);
        setupJwt(jwt().claim("primary", t1).claim("fallback", t2).build());

        assertEquals(t1, JwtUtil.getClaimAsInstant("primary", "fallback"));
    }

    @Test
    void getClaimAsInstant_withFallback_primaryAbsent_returnsFallback() {
        final var t2 = Instant.ofEpochSecond(2000);
        setupJwt(jwt().claim("fallback", t2).build());

        assertEquals(t2, JwtUtil.getClaimAsInstant("primary", "fallback"));
    }

    @Test
    void getClaimAsInstant_withFallback_bothAbsent_throwsAccessDenied() {
        setupJwt(jwt().claim("other", "value").build());

        assertThrows(
                AccessDeniedException.class,
                () -> JwtUtil.getClaimAsInstant("primary", "fallback"));
    }

    // ======== getClaimAsLong() ========

    @Test
    void getClaimAsLong_fromInteger_returnsLong() {
        setupJwt(jwt().claim("count", 42).build());

        assertEquals(42L, JwtUtil.getClaimAsLong("count"));
    }

    @Test
    void getClaimAsLong_fromLong_returnsLong() {
        setupJwt(jwt().claim("count", 999_999_999_999L).build());

        assertEquals(999_999_999_999L, JwtUtil.getClaimAsLong("count"));
    }

    @Test
    void getClaimAsLong_fromString_parsesAndReturnsLong() {
        setupJwt(jwt().claim("count", "123").build());

        assertEquals(123L, JwtUtil.getClaimAsLong("count"));
    }

    @Test
    void getClaimAsLong_fromDouble_truncatesAndReturnsLong() {
        setupJwt(jwt().claim("count", 7.9).build());

        assertEquals(7L, JwtUtil.getClaimAsLong("count"));
    }

    @Test
    void getClaimAsLong_claimAbsent_throwsAccessDenied() {
        setupJwt(jwt().claim("other", "value").build());

        assertThrows(AccessDeniedException.class, () -> JwtUtil.getClaimAsLong("count"));
    }

    @Test
    void getClaimAsLong_fromInvalidString_throwsAccessDenied() {
        setupJwt(jwt().claim("count", "not-a-number").build());

        assertThrows(AccessDeniedException.class, () -> JwtUtil.getClaimAsLong("count"));
    }

    @Test
    void getClaimAsLong_withFallback_primaryPresent_returnsPrimary() {
        setupJwt(jwt().claim("primary", 10).claim("fallback", 20).build());

        assertEquals(10L, JwtUtil.getClaimAsLong("primary", "fallback"));
    }

    @Test
    void getClaimAsLong_withFallback_primaryAbsent_returnsFallback() {
        setupJwt(jwt().claim("fallback", 20).build());

        assertEquals(20L, JwtUtil.getClaimAsLong("primary", "fallback"));
    }

    @Test
    void getClaimAsLong_withFallback_primaryInvalidString_returnsFallback() {
        setupJwt(jwt().claim("primary", "not-a-number").claim("fallback", 99).build());

        assertEquals(99L, JwtUtil.getClaimAsLong("primary", "fallback"));
    }

    @Test
    void getClaimAsLong_withFallback_bothAbsent_throwsAccessDenied() {
        setupJwt(jwt().claim("other", "value").build());

        assertThrows(
                AccessDeniedException.class, () -> JwtUtil.getClaimAsLong("primary", "fallback"));
    }

    // ======== getClaimAsLongList() ========

    @Test
    void getClaimAsLongList_fromListOfIntegers_returnsList() {
        setupJwt(jwt().claim("ids", List.of(1, 2, 3)).build());

        assertEquals(List.of(1L, 2L, 3L), JwtUtil.getClaimAsLongList("ids"));
    }

    @Test
    void getClaimAsLongList_fromListOfLongs_returnsList() {
        setupJwt(jwt().claim("ids", List.of(100L, 200L, 300L)).build());

        assertEquals(List.of(100L, 200L, 300L), JwtUtil.getClaimAsLongList("ids"));
    }

    @Test
    void getClaimAsLongList_fromListOfStrings_parsesEachElement() {
        setupJwt(jwt().claim("ids", List.of("10", "20", "30")).build());

        assertEquals(List.of(10L, 20L, 30L), JwtUtil.getClaimAsLongList("ids"));
    }

    @Test
    void getClaimAsLongList_fromSingleInteger_returnsListOfOne() {
        setupJwt(jwt().claim("ids", 42).build());

        assertEquals(List.of(42L), JwtUtil.getClaimAsLongList("ids"));
    }

    @Test
    void getClaimAsLongList_claimAbsent_throwsNullPointerException() {
        setupJwt(jwt().claim("other", "value").build());

        // claimAsList uses requireNonNull directly — absent claim causes NPE
        assertThrows(NullPointerException.class, () -> JwtUtil.getClaimAsLongList("ids"));
    }

    @Test
    void getClaimAsLongList_listContainsInvalidString_throwsAccessDenied() {
        setupJwt(jwt().claim("ids", List.of("1", "bad", "3")).build());

        assertThrows(AccessDeniedException.class, () -> JwtUtil.getClaimAsLongList("ids"));
    }

    @Test
    void getClaimAsLongList_withFallback_primaryPresent_returnsPrimary() {
        setupJwt(jwt().claim("primary", List.of(1, 2)).claim("fallback", List.of(3, 4)).build());

        assertEquals(List.of(1L, 2L), JwtUtil.getClaimAsLongList("primary", "fallback"));
    }

    @Test
    void getClaimAsLongList_withFallback_primaryAbsent_returnsFallback() {
        setupJwt(jwt().claim("fallback", List.of(3, 4)).build());

        assertEquals(List.of(3L, 4L), JwtUtil.getClaimAsLongList("primary", "fallback"));
    }

    // ======== getClaimAsDouble() ========

    @Test
    void getClaimAsDouble_fromDouble_returnsDouble() {
        setupJwt(jwt().claim("score", 3.14).build());

        assertEquals(3.14, JwtUtil.getClaimAsDouble("score"), 1.0e-9);
    }

    @Test
    void getClaimAsDouble_fromInteger_returnsDouble() {
        setupJwt(jwt().claim("score", 5).build());

        assertEquals(5.0, JwtUtil.getClaimAsDouble("score"), 1.0e-9);
    }

    @Test
    void getClaimAsDouble_fromString_parsesAndReturnsDouble() {
        setupJwt(jwt().claim("score", "2.71").build());

        assertEquals(2.71, JwtUtil.getClaimAsDouble("score"), 1.0e-9);
    }

    @Test
    void getClaimAsDouble_claimAbsent_throwsAccessDenied() {
        setupJwt(jwt().claim("other", "value").build());

        assertThrows(AccessDeniedException.class, () -> JwtUtil.getClaimAsDouble("score"));
    }

    @Test
    void getClaimAsDouble_fromInvalidString_throwsAccessDenied() {
        setupJwt(jwt().claim("score", "not-a-number").build());

        assertThrows(AccessDeniedException.class, () -> JwtUtil.getClaimAsDouble("score"));
    }

    @Test
    void getClaimAsDouble_withFallback_primaryPresent_returnsPrimary() {
        setupJwt(jwt().claim("primary", 1.1).claim("fallback", 2.2).build());

        assertEquals(1.1, JwtUtil.getClaimAsDouble("primary", "fallback"), 1.0e-9);
    }

    @Test
    void getClaimAsDouble_withFallback_primaryAbsent_returnsFallback() {
        setupJwt(jwt().claim("fallback", 2.2).build());

        assertEquals(2.2, JwtUtil.getClaimAsDouble("primary", "fallback"), 1.0e-9);
    }

    @Test
    void getClaimAsDouble_withFallback_primaryInvalidString_returnsFallback() {
        setupJwt(jwt().claim("primary", "not-a-number").claim("fallback", 9.9).build());

        assertEquals(9.9, JwtUtil.getClaimAsDouble("primary", "fallback"), 1.0e-9);
    }

    @Test
    void getClaimAsDouble_withFallback_bothAbsent_throwsAccessDenied() {
        setupJwt(jwt().claim("other", "value").build());

        assertThrows(
                AccessDeniedException.class, () -> JwtUtil.getClaimAsDouble("primary", "fallback"));
    }

    // ======== getClaimAsDoubleList() ========

    @Test
    void getClaimAsDoubleList_fromListOfDoubles_returnsList() {
        setupJwt(jwt().claim("values", List.of(1.1, 2.2, 3.3)).build());

        final var result = JwtUtil.getClaimAsDoubleList("values");
        assertAll(
                () -> assertEquals(3, result.size()),
                () -> assertEquals(1.1, result.get(0), 1.0e-9),
                () -> assertEquals(2.2, result.get(1), 1.0e-9),
                () -> assertEquals(3.3, result.get(2), 1.0e-9));
    }

    @Test
    void getClaimAsDoubleList_fromListOfStrings_parsesEachElement() {
        setupJwt(jwt().claim("values", List.of("1.5", "2.5")).build());

        final var result = JwtUtil.getClaimAsDoubleList("values");
        assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals(1.5, result.get(0), 1.0e-9),
                () -> assertEquals(2.5, result.get(1), 1.0e-9));
    }

    @Test
    void getClaimAsDoubleList_fromSingleValue_returnsListOfOne() {
        setupJwt(jwt().claim("values", 9.9).build());

        final var result = JwtUtil.getClaimAsDoubleList("values");
        assertEquals(1, result.size());
        assertEquals(9.9, result.get(0), 1.0e-9);
    }

    @Test
    void getClaimAsDoubleList_claimAbsent_throwsNullPointerException() {
        setupJwt(jwt().claim("other", "value").build());

        assertThrows(NullPointerException.class, () -> JwtUtil.getClaimAsDoubleList("values"));
    }

    @Test
    void getClaimAsDoubleList_listContainsInvalidString_throwsAccessDenied() {
        setupJwt(jwt().claim("values", List.of("1.0", "bad", "3.0")).build());

        assertThrows(AccessDeniedException.class, () -> JwtUtil.getClaimAsDoubleList("values"));
    }

    @Test
    void getClaimAsDoubleList_withFallback_primaryPresent_returnsPrimary() {
        setupJwt(
                jwt().claim("primary", List.of(1.0, 2.0))
                        .claim("fallback", List.of(3.0, 4.0))
                        .build());

        assertEquals(List.of(1.0, 2.0), JwtUtil.getClaimAsDoubleList("primary", "fallback"));
    }

    @Test
    void getClaimAsDoubleList_withFallback_primaryAbsent_returnsFallback() {
        setupJwt(jwt().claim("fallback", List.of(3.0, 4.0)).build());

        assertEquals(List.of(3.0, 4.0), JwtUtil.getClaimAsDoubleList("primary", "fallback"));
    }
}
