package de.elomagic.rb.backend.components;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import de.elomagic.rb.backend.dtos.AppointmentDTO;
import de.elomagic.rb.backend.providers.EwsProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Set;

@Component
public class AppointmentComponent {

    private final EwsProvider provider;

    @Value("${rb.ext.ews.resourceMailAddress}")
    private String resourceMailAddress;

    public AppointmentComponent(@Autowired EwsProvider provider) {
        this.provider = provider;
    }

    @Nonnull
    public Set<AppointmentDTO> getAppointmentsOfToday() {
        ZonedDateTime now = ZonedDateTime.now();
        return provider.queryAppointments(resourceMailAddress, now.minusDays(1), now.plusDays(1));
    }

    @Nonnull
    public AppointmentDTO updateCurrentAppointment(@Nullable Integer durationInMinutes) {
        return null;
    }

    @Nonnull
    public AppointmentDTO createAdHocAppointment(int durationInMinutes) {
        return null;
    }

}
