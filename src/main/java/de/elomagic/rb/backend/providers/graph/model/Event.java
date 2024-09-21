package de.elomagic.rb.backend.providers.graph.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Event {

    private String id;
    @JsonProperty("start")
    private DateTimeZoned start;
    @JsonProperty("end")
    private DateTimeZoned end;
    private String subject;
    private Body body;

    public String getId() {
        return id;
    }

    public DateTimeZoned getStart() {
        return start;
    }

    public DateTimeZoned getEnd() {
        return end;
    }

    public String getSubject() {
        return subject;
    }

    public Body getBody() {
        return body;
    }

}
