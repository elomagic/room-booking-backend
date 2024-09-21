package de.elomagic.rb.backend.providers.graph.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Body {

    private String contentType;
    private String content;

    @JsonIgnore
    public String getPreview() {
        if ("html".equals(contentType)) {
            Document doc = Jsoup.parse(content);
            return doc.text();
        }

        return content;
    }

}
