package de.elomagic.rb.backend.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nonnull;

import de.elomagic.rb.backend.exceptions.CommonRbException;
import de.elomagic.rb.backend.providers.ews.EwsProvider;
import de.elomagic.rb.backend.utils.Json5MapperFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

public class AbstractRestClient {

    private static final String APPLICATION_JSON = "application/json";
    private static final Logger LOGGER = LoggerFactory.getLogger(EwsProvider.class);
    private final ObjectMapper objectMapper = Json5MapperFactory.create();

    @Value("${rb.ext.graph.token}")
    private String token;

    private HttpRequest.Builder createDefaultRequest(@Nonnull URI uri) {
        return HttpRequest
                .newBuilder(uri)
                .setHeader("Authentication", "Bearer " + token)
                .timeout(Duration.ofSeconds(60));
    }

    @Nonnull
    protected HttpRequest createDefaultGET(@Nonnull URI uri, @Nonnull String... headers) {
        HttpRequest.Builder builder = createDefaultRequest(uri);

        if (headers.length != 0) {
            builder = builder.headers(headers);
        }

        return builder
                .GET()
                .build();
    }

    @Nonnull
    protected HttpRequest createDefaultDELETE(@Nonnull URI uri, @Nonnull String... headers) {
        HttpRequest.Builder builder = createDefaultRequest(uri);

        if (headers.length != 0) {
            builder = builder.headers(headers);
        }

        return builder
                .DELETE()
                .build();
    }

    @Nonnull
    protected HttpRequest createDefaultPUT(@Nonnull URI uri, @Nonnull HttpRequest.BodyPublisher publisher, @Nonnull String... headers) {
        HttpRequest.Builder builder = createDefaultRequest(uri).header("Content-Type", APPLICATION_JSON);

        if (headers.length != 0) {
            builder = builder.headers(headers);
        }

        return builder
                .PUT(publisher)
                .build();
    }

    @Nonnull
    protected HttpRequest createDefaultPOST(@Nonnull URI uri, @Nonnull HttpRequest.BodyPublisher publisher, @Nonnull String... headers) {
        HttpRequest.Builder builder = createDefaultRequest(uri)
                .header("Content-Type", APPLICATION_JSON)
                .header("Accept",APPLICATION_JSON);

        if (headers.length != 0) {
            builder = builder.headers(headers);
        }

        return builder
                .POST(publisher)
                .build();
    }

    protected String executeRequest(@Nonnull HttpRequest request) throws IOException, InterruptedException {
        try (HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build()) {
            LOGGER.debug("Executing HTTP {} to {}", request.method(), request.uri());
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            LOGGER.debug("Responses HTTP status {}", response.statusCode());
            if (!List.of(200, 204, 302).contains(response.statusCode())) {
                throw new CommonRbException("Unexpected HTTP status code %s: Body=%s".formatted(response.statusCode(), response.body()));
            }
            return response.body();
        } catch (Exception ex) {
            LOGGER.error("Error on url request '{}' occurred.", request.uri());
            throw ex;
        }
    }

    /**
     * Execute a HTTP request and the result JSON body will be mapped into a given class type.
     *
     * @param request HTTP to be requested
     * @param classType Class type to be mapped
     * @return Returns instance of class type
     * @param <T> Class type to be mapped
     * @throws IOException Thrown if an I/ O error occurs when sending or receiving, or the client has shut down
     * @throws InterruptedException Thrown if the operation is interrupted
     */
    protected <T> T executeRequest(@Nonnull HttpRequest request, Class<? extends T> classType) throws IOException, InterruptedException {
        try {
            String content = executeRequest(request);

            LOGGER.trace("HTTP response body={}", content);

            return objectMapper.readValue(content, classType);
        } catch (Exception ex) {
            LOGGER.error("Error on url request '{}' occurred.", request.uri());
            throw ex;
        }
    }

}