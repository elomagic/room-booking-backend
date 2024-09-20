package de.elomagic.rb.backend.providers;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import de.elomagic.rb.backend.dtos.AppointmentDTO;

import java.io.Closeable;
import java.time.ZonedDateTime;
import java.util.Set;

public interface IProvider extends Closeable {

    @Nonnull
    Set<AppointmentDTO> queryAppointments(@Nonnull String resourceAddress, @Nonnull ZonedDateTime start, @Nonnull ZonedDateTime end);

    @Nonnull
    AppointmentDTO createAppointment(@Nonnull AppointmentDTO appointment);

    @Nullable
    AppointmentDTO update(@Nonnull AppointmentDTO dto);

}
