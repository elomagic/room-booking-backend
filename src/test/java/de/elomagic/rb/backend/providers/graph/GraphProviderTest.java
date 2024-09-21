package de.elomagic.rb.backend.providers.graph;

import de.elomagic.rb.backend.dtos.AppointmentDTO;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.ZonedDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Disabled
class GraphProviderTest {

    @Autowired
    GraphProvider provider;

    @Test

    void testQueryAppointments() {
        Set<AppointmentDTO> appointments = provider.queryAppointments("room_maui@my-company.internal", ZonedDateTime.now(), ZonedDateTime.now().plusDays(1));

        assertEquals(0, appointments.size());
    }
}