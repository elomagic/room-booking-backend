package de.elomagic.rb.backend.restclient;

import jakarta.annotation.Nonnull;

import de.elomagic.rb.backend.exceptions.CommonRbException;
import de.elomagic.rb.backend.restclient.model.TokenResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.Map;

public abstract class OAuth2RestClient extends AbstractRestClient {

    private String oauth2Uri;
    private String clientId;
    private String clientSecret;
    private String scopeUrl;

    private String bearerToken;
    private boolean authPhase;

    public void setOAuthConfiguration(
            @Nonnull String oauth2Uri,
            @Nonnull String clientId,
            @Nonnull String clientSecret,
            @Nonnull String scopeUrl
    ) {
        this.oauth2Uri = oauth2Uri;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.scopeUrl = scopeUrl;
    }

    private void createAccessToken() {
        try {
            URI uri = URI.create(oauth2Uri);

            Map<String, String> formData = new HashMap<>();
            formData.put("grant_type", "client_credentials");
            formData.put("client_id", clientId);
            formData.put("client_secret", clientSecret);
            formData.put("scope", scopeUrl + (scopeUrl.endsWith("/") ? "" : "/") + ".default");

            HttpRequest request = createDefaultPOST(uri, BodyPublisherWrap.of(formData));

            TokenResponse tokenResponse = executeRequest(request, TokenResponse.class);
            bearerToken = tokenResponse.getAccessToken();
        } catch (Exception e) {
            throw new CommonRbException(e.getMessage(), e);
        }
    }

    @Nonnull
    @Override
    protected HttpRequest.Builder createDefaultRequest(@Nonnull URI uri) {
        HttpRequest.Builder builder = super.createDefaultRequest(uri);

        if (!authPhase && StringUtils.isBlank(bearerToken)) {
            authPhase = true;
            try {
                createAccessToken();
            } finally {
                authPhase = false;
            }
        }

        if (StringUtils.isNoneBlank(bearerToken)) {
            builder.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken);
        }

        return builder;
    }

}
