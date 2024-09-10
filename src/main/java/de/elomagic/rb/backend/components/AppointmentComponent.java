package de.elomagic.rb.backend.components;

import jakarta.annotation.Nonnull;

import de.elomagic.rb.backend.dtos.AppointmentDTO;
import de.elomagic.rb.backend.providers.EwsProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Set;

@Component
public class AppointmentComponent {

    private final EwsProvider provider;

    public AppointmentComponent(@Autowired EwsProvider provider) {
        this.provider = provider;
    }

    @Nonnull
    public Set<AppointmentDTO> getAppointmentsOfToday(@Nonnull String resourceAddress) {
        ZonedDateTime now = ZonedDateTime.now();
        return provider.queryAppointments(resourceAddress, now.minusDays(1), now.plusDays(1));
    }

    @Nonnull
    public AppointmentDTO createAdHocAppointment(@Nonnull AppointmentDTO appointment) {
        // TODO Validate appointment
        return provider.createAppointment(appointment);
    }

    @Nonnull
    public AppointmentDTO updateCurrentAppointment(@Nonnull AppointmentDTO appointment) {
        // TODO Validate appointment
        return provider.update(appointment);
    }

}
