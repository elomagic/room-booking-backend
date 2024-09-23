package de.elomagic.rb.backend.providers.graph;

import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

public final class BodyPublisher {

    private BodyPublisher() {}

    public static HttpRequest.BodyPublisher of(final Map<String, String> data) {

        String s = data
                .entrySet()
                .stream()
                .map(e -> "%s=%s".formatted(
                        URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8),
                        URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8)))
                .collect(Collectors.joining("&"));

        return HttpRequest.BodyPublishers.ofString(s, StandardCharsets.UTF_8);

    }
}
