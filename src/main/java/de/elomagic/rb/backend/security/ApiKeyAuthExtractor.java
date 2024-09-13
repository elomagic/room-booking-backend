package de.elomagic.rb.backend.security;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.http.client.methods.HttpOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ApiKeyAuthExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiKeyAuthExtractor.class);

    @Value("${rb.apiKey}")
    private String apiKey;

    public Optional<Authentication> extract(HttpServletRequest request) {
        String providedKey = request.getHeader("RB-ApiKey");
        String method = request.getMethod();
        if (!HttpOptions.METHOD_NAME.equals(method) && (providedKey == null || !providedKey.equals(apiKey))) {
            LOGGER.info("Invalid API key: {}", providedKey);

            return Optional.empty();
        }

        return Optional.of(new ApiKeyAuth(providedKey, AuthorityUtils.NO_AUTHORITIES));
    }

}
