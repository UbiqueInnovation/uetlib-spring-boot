package ch.ubique.backend.security.oauth2;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import jakarta.annotation.Nullable;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.client.endpoint.AbstractRestClientOAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

/**
 * Set this converter as a parameter converter in your {@link
 * AbstractRestClientOAuth2AccessTokenResponseClient<OAuth2ClientCredentialsGrantRequest>} for
 * RFC-8707 support.
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc8707">RFC-8707 Resource Indicators for
 *     OAuth 2.0</a>
 */
@SuppressWarnings("unused")
public class ResourceIndicatorClientCredentialsRequestConverter
        implements Converter<OAuth2ClientCredentialsGrantRequest, MultiValueMap<String, String>> {
    private final Map<String, String> clientResource;

    public ResourceIndicatorClientCredentialsRequestConverter(
            final Map<String, String> clientResource) {
        this.clientResource = clientResource;
    }

    @Override
    @Nullable
    public MultiValueMap<String, String> convert(
            final OAuth2ClientCredentialsGrantRequest tokenRequest) {
        final var registrationId = tokenRequest.getClientRegistration().getRegistrationId();
        final var resource = clientResource.get(registrationId);
        if (!isNotBlank(resource)) {
            return null;
        }

        final var customParams = new LinkedMultiValueMap<String, String>();
        customParams.add("resource", resource);
        return customParams;
    }
}
