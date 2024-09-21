package de.elomagic.rb.backend.providers.graph.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.TimeZone;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class DateTimeZoned {

    // "dateTime": "2021-09-14T08:00:00.0000000",
    private String dateTime;
    // "timeZone": "UTC"
    private String timeZone;

    @JsonIgnore
    public ZonedDateTime getZonedDateTime() throws ParseException {
        SimpleDateFormat graphDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.0000000'");
        graphDateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));

        Date date = graphDateFormat.parse(dateTime);
        Instant instant = date.toInstant();
        ZoneId timeZoneID = ZoneId.of(timeZone);

        return instant.atZone(timeZoneID);
    }

}
