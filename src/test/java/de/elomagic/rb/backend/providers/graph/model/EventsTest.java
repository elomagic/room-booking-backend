package de.elomagic.rb.backend.providers.graph.model;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.elomagic.rb.backend.utils.Json5MapperFactory;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class EventsTest {

    @Test
    void testJsonRead() throws Exception {

        ObjectMapper mapper = Json5MapperFactory.create();
        Events events = mapper.readValue(IOUtils.resourceToString("/ms-graph-events1.json5", StandardCharsets.UTF_8), Events.class);

        assertEquals(1, events.getValue().size());

        Event event = events.getValue().getFirst();

        assertEquals("AAMkADBmYTFkMzUyLTgxODQtNDA0YS05YzdlLWRkYjJlY2U4NTljZgBGAAAAAACdCqnIfBTiS7nPzH--j6RvBwDvenXPVP3FQpzwdU3ADBy_AAAAAAENAADvenXPVP3FQpzwdU3ADBy_AABH5Vj3AAA=", event.getId());
        assertEquals("2021-09-14T08:00Z[UTC]", event.getStart().getZonedDateTime().toString());
        assertEquals("2021-09-14T08:30Z[UTC]", event.getEnd().getZonedDateTime().toString());
        assertEquals("All APIs Testing", event.getSubject());
        assertEquals("Microsoft Teams meeting", event.getBody().getPreview());

    }

}