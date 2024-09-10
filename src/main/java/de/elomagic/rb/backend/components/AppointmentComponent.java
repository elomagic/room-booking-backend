package de.elomagic.rb.backend.components;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import de.elomagic.rb.backend.dtos.AppointmentDTO;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AppointmentComponent {

    @Nonnull
    public List<AppointmentDTO> getAppointmentsOfToday() {
        return List.of();
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
