package de.elomagic.rb.backend.restclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Nonnull;

import de.elomagic.rb.backend.utils.Json5MapperFactory;

import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Improved body publisher.
 */
public final class BodyPublisherWrap {

    private BodyPublisherWrap() {}

    private String contentType;
    private HttpRequest.BodyPublisher bodyPublisher;

    /**
     * Serialize and provide object as a JSON string with content type "application/json".
     *
     * @param o Object to serialize
     * @return Returns the wrapper
     * @throws JsonProcessingException Thrown when unable to serialize object
     */
    public static BodyPublisherWrap ofObject(final Object o) throws JsonProcessingException {
        String json = Json5MapperFactory.create().writeValueAsString(o);

        return wrapHttpBodyPublisher(
                HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8),
                "application/json"
        );
    }

    /**
     * Serialize and provide form data with content type "application/x-www-form-urlencoded".
     *
     * @param data Form data as a map
     * @return Returns the wrapper
     */
    public static BodyPublisherWrap of(final Map<String, String> data) {

        String s = data
                .entrySet()
                .stream()
                .map(e -> "%s=%s".formatted(
                        URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8),
                        URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8)))
                .collect(Collectors.joining("&"));

        return wrapHttpBodyPublisher(
                HttpRequest.BodyPublishers.ofString(s, StandardCharsets.UTF_8),
                "application/x-www-form-urlencoded"
        );

    }

    public static BodyPublisherWrap wrapHttpBodyPublisher(
            @Nonnull HttpRequest.BodyPublisher bodyPublisher,

            @Nonnull String contentType
    ) {
        BodyPublisherWrap bp = new BodyPublisherWrap();
        bp.bodyPublisher = bodyPublisher;
        bp.contentType = contentType;

        return bp;
    }

    @Nonnull
    public String getContentType() {
        return contentType;
    }

    @Nonnull
    public HttpRequest.BodyPublisher getBodyPublisher() {
        return bodyPublisher;
    }

}
