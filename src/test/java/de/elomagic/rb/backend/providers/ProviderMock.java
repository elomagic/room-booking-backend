package de.elomagic.rb.backend.providers;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import de.elomagic.rb.backend.dtos.AppointmentDTO;

import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Set;

@Component
public class ProviderMock implements IProvider {

    private AppointmentDTO createAppointment() {
        return new AppointmentDTO(
                null,
                ZonedDateTime.now(),
                ZonedDateTime.now().plusHours(1),
                "Subject",
                "Body",
                "room_maui@my-company.internal"
        );
    }

    @Nonnull
    @Override
    public Set<AppointmentDTO> queryAppointments(@Nonnull String resourceAddress, @Nonnull ZonedDateTime start, @Nonnull ZonedDateTime end) {
        return resourceAddress.equals("room_maui@my-company.internal") ? Set.of(createAppointment()) : Set.of();
    }

    @Nonnull
    @Override
    public AppointmentDTO createAppointment(@Nonnull AppointmentDTO appointment) {
        return new AppointmentDTO(
                null,
                ZonedDateTime.now(),
                ZonedDateTime.now().plusHours(1),
                "Subject",
                "Body",
                "room_maui@my-company.internal"
        );
    }

    @Nullable
    @Override
    public AppointmentDTO update(@Nonnull AppointmentDTO dto) {
        return null;
    }

    @Override
    public void close() {
        // nop. It's only a mock
    }
}
