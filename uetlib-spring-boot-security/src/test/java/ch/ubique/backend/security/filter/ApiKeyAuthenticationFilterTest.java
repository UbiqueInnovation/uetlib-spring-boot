package ch.ubique.backend.security.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import jakarta.servlet.ServletException;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.stream.Stream;

class ApiKeyAuthenticationFilterTest {

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilter_extractsAuthorizationHeaderAndAuthenticates()
            throws ServletException, IOException {
        final var mockAuthManager = mock(AuthenticationManager.class);
        final var filter = new ApiKeyAuthenticationFilter(mockAuthManager);

        final var request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "ApiKey my-token");

        final var response = new MockHttpServletResponse();
        final var chain = new MockFilterChain();

        final var authenticatedToken = mock(Authentication.class);
        when(authenticatedToken.isAuthenticated()).thenReturn(true);
        when(mockAuthManager.authenticate(any(Authentication.class)))
                .thenReturn(authenticatedToken);

        filter.doFilter(request, response, chain);

        // Verify manager was called with the correct principal
        final var authCaptor = ArgumentCaptor.forClass(Authentication.class);
        verify(mockAuthManager).authenticate(authCaptor.capture());
        assertThat(authCaptor.getValue().getPrincipal()).isEqualTo("my-token");

        // Verify context populated
        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isSameAs(authenticatedToken);
    }

    @Test
    void doFilter_withAuthorizationHeaderMissingPrefix_doesNotAuthenticate()
            throws ServletException, IOException {
        final var mockAuthManager = mock(AuthenticationManager.class);
        final var filter = new ApiKeyAuthenticationFilter(mockAuthManager);

        final var request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer my-token");

        final var response = new MockHttpServletResponse();
        final var chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        // Verify manager was not called since getPreAuthenticatedPrincipal returned null
        verifyNoInteractions(mockAuthManager);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilter_withoutAuthorizationHeader_doesNotAuthenticate()
            throws ServletException, IOException {
        final var mockAuthManager = mock(AuthenticationManager.class);
        final var filter = new ApiKeyAuthenticationFilter(mockAuthManager);

        final var request = new MockHttpServletRequest(); // no authorization header
        final var response = new MockHttpServletResponse();
        final var chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        // Verify manager was not called since getPreAuthenticatedPrincipal returned null
        verifyNoInteractions(mockAuthManager);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilter_withCustomHeader_extractsCustomHeaderAndAuthenticates()
            throws ServletException, IOException {
        final var mockAuthManager = mock(AuthenticationManager.class);
        final var filter = new ApiKeyAuthenticationFilter(mockAuthManager, "X-API-KEY");

        final var request = new MockHttpServletRequest();
        request.addHeader("X-API-KEY", "custom-api-key-value");

        final var response = new MockHttpServletResponse();
        final var chain = new MockFilterChain();

        final var authenticatedToken = mock(Authentication.class);
        when(authenticatedToken.isAuthenticated()).thenReturn(true);
        when(mockAuthManager.authenticate(any(Authentication.class)))
                .thenReturn(authenticatedToken);

        filter.doFilter(request, response, chain);

        // Verify manager was called with the correct principal
        final var authCaptor = ArgumentCaptor.forClass(Authentication.class);
        verify(mockAuthManager).authenticate(authCaptor.capture());
        assertThat(authCaptor.getValue().getPrincipal()).isEqualTo("custom-api-key-value");

        // Verify context populated
        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isSameAs(authenticatedToken);
    }

    @Test
    void doFilter_withCustomHeaderApiKey_extractsRawValueWithoutPrefix()
            throws ServletException, IOException {
        final var mockAuthManager = mock(AuthenticationManager.class);
        final var filter = new ApiKeyAuthenticationFilter(mockAuthManager, "ApiKey");

        final var request = new MockHttpServletRequest();
        request.addHeader("ApiKey", "raw-token-without-prefix");

        final var response = new MockHttpServletResponse();
        final var chain = new MockFilterChain();

        final var authenticatedToken = mock(Authentication.class);
        when(authenticatedToken.isAuthenticated()).thenReturn(true);
        when(mockAuthManager.authenticate(any(Authentication.class)))
                .thenReturn(authenticatedToken);

        filter.doFilter(request, response, chain);

        // Verify manager was called with the correct principal (no prefix processing)
        final var authCaptor = ArgumentCaptor.forClass(Authentication.class);
        verify(mockAuthManager).authenticate(authCaptor.capture());
        assertThat(authCaptor.getValue().getPrincipal()).isEqualTo("raw-token-without-prefix");

        // Verify context populated
        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isSameAs(authenticatedToken);
    }

    @Test
    void doFilter_withConfigurablePrefix_extractsPrefixAndAuthenticates()
            throws ServletException, IOException {
        final var mockAuthManager = mock(AuthenticationManager.class);
        final var filter =
                new ApiKeyAuthenticationFilter(
                        mockAuthManager, HttpHeaders.AUTHORIZATION, "CustomTokenPrefix ");

        final var request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "CustomTokenPrefix my-token");

        final var response = new MockHttpServletResponse();
        final var chain = new MockFilterChain();

        final var authenticatedToken = mock(Authentication.class);
        when(authenticatedToken.isAuthenticated()).thenReturn(true);
        when(mockAuthManager.authenticate(any(Authentication.class)))
                .thenReturn(authenticatedToken);

        filter.doFilter(request, response, chain);

        final var authCaptor = ArgumentCaptor.forClass(Authentication.class);
        verify(mockAuthManager).authenticate(authCaptor.capture());
        assertThat(authCaptor.getValue().getPrincipal()).isEqualTo("my-token");

        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isSameAs(authenticatedToken);
    }

    @Test
    void doFilter_withConfigurablePrefixMismatch_doesNotAuthenticate()
            throws ServletException, IOException {
        final var mockAuthManager = mock(AuthenticationManager.class);
        final var filter =
                new ApiKeyAuthenticationFilter(
                        mockAuthManager, HttpHeaders.AUTHORIZATION, "CustomTokenPrefix ");

        final var request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "WrongPrefix my-token");

        final var response = new MockHttpServletResponse();
        final var chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        verifyNoInteractions(mockAuthManager);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilter_withConfigurableNullPrefix_extractsRawValue()
            throws ServletException, IOException {
        final var mockAuthManager = mock(AuthenticationManager.class);
        final var filter =
                new ApiKeyAuthenticationFilter(
                        mockAuthManager, HttpHeaders.AUTHORIZATION, (String[]) null);

        final var request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "raw-token-value");

        final var response = new MockHttpServletResponse();
        final var chain = new MockFilterChain();

        final var authenticatedToken = mock(Authentication.class);
        when(authenticatedToken.isAuthenticated()).thenReturn(true);
        when(mockAuthManager.authenticate(any(Authentication.class)))
                .thenReturn(authenticatedToken);

        filter.doFilter(request, response, chain);

        final var authCaptor = ArgumentCaptor.forClass(Authentication.class);
        verify(mockAuthManager).authenticate(authCaptor.capture());
        assertThat(authCaptor.getValue().getPrincipal()).isEqualTo("raw-token-value");

        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isSameAs(authenticatedToken);
    }

    static Stream<Arguments> multiplePrefixTestCases() {
        return Stream.of(
                Arguments.of(
                        new String[] {"PrefixOne ", "PrefixTwo "}, "PrefixOne value1", "value1"),
                Arguments.of(
                        new String[] {"PrefixOne ", "PrefixTwo "}, "PrefixTwo value2", "value2"),
                Arguments.of(new String[] {"PrefixOne ", "PrefixTwo "}, "PrefixThree value3", null),
                Arguments.of(new String[] {"PrefixOne ", ""}, "raw-value", "raw-value"));
    }

    @ParameterizedTest
    @MethodSource("multiplePrefixTestCases")
    void doFilter_withPrefixes_extractsPrincipalCorrectly(
            final String[] prefixes,
            final String headerValue,
            final @Nullable String expectedPrincipal)
            throws ServletException, IOException {
        final var mockAuthManager = mock(AuthenticationManager.class);
        final var filter =
                new ApiKeyAuthenticationFilter(
                        mockAuthManager, HttpHeaders.AUTHORIZATION, prefixes);

        final var request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, headerValue);

        final var response = new MockHttpServletResponse();
        final var chain = new MockFilterChain();

        if (expectedPrincipal != null) {
            final var authenticatedToken = mock(Authentication.class);
            when(authenticatedToken.isAuthenticated()).thenReturn(true);
            when(mockAuthManager.authenticate(any(Authentication.class)))
                    .thenReturn(authenticatedToken);

            filter.doFilter(request, response, chain);

            final var authCaptor = ArgumentCaptor.forClass(Authentication.class);
            verify(mockAuthManager).authenticate(authCaptor.capture());
            assertThat(authCaptor.getValue().getPrincipal()).isEqualTo(expectedPrincipal);

            assertThat(SecurityContextHolder.getContext().getAuthentication())
                    .isSameAs(authenticatedToken);
        } else {
            filter.doFilter(request, response, chain);

            verifyNoInteractions(mockAuthManager);
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        }
    }
}
