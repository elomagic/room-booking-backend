package de.elomagic.rb.backend.components;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AppointmentComponentTest {

    @Autowired
    AppointmentComponent appointmentComponent;

    @Test
    void getAppointmentsOfToday() {

        assertEquals(0, appointmentComponent.getAppointmentsOfToday("room_unknown@my-company.internal").size());
        assertEquals(1, appointmentComponent.getAppointmentsOfToday("room_maui@my-company.internal").size());

    }

}