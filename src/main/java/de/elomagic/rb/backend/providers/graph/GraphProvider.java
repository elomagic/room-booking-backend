package de.elomagic.rb.backend.providers.graph;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import de.elomagic.rb.backend.dtos.AppointmentDTO;
import de.elomagic.rb.backend.exceptions.CommonRbException;
import de.elomagic.rb.backend.exceptions.NotSupportedException;
import de.elomagic.rb.backend.providers.AbstractRestClient;
import de.elomagic.rb.backend.providers.IProvider;
import de.elomagic.rb.backend.providers.graph.model.Event;
import de.elomagic.rb.backend.providers.graph.model.Events;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * MS Graph provider
 */
@Component
public class GraphProvider extends AbstractRestClient implements IProvider {

    @Value("${rb.ext.graph.uri}")
    private String baseURL;

    @Nonnull
    @Override
    public Set<AppointmentDTO> queryAppointments(@Nonnull String resourceAddress, @Nonnull ZonedDateTime start, @Nonnull ZonedDateTime end) {
        try {
            URI uri = URI.create(
                    "%s/v1.0/users/%s/calendar/events?startDateTime=%s&endDateTime=%s".formatted(
                            baseURL,
                            resourceAddress,
                            start,
                            end
                    )
            );

            HttpRequest request = createDefaultGET(uri);

            return Stream
                    .of(executeRequest(request, Events.class))
                    .flatMap(e -> e.getValue().stream())
                    .map(e -> mapAppointmentToDTO(e, resourceAddress))
                    .collect(Collectors.toSet());
        } catch (IOException | InterruptedException ex) {
            throw new CommonRbException(ex);
        }
    }

    @Nonnull
    @Override
    public AppointmentDTO createAppointment(@Nonnull AppointmentDTO appointment) {
        throw new NotSupportedException("Creating an appointment is currently unsupported");
    }

    @Nullable
    @Override
    public AppointmentDTO update(@Nonnull AppointmentDTO dto) {
        throw new NotSupportedException("Updating an appointment is currently unsupported");
    }

    @Override
    public void close() throws IOException {
        // noop
    }

    @Nonnull
    private AppointmentDTO mapAppointmentToDTO(@Nonnull Event appointment, @Nonnull String resourceMailAddress) throws CommonRbException {
        try {
            return new AppointmentDTO(
                    appointment.getId(),
                    appointment.getStart().getZonedDateTime(),
                    appointment.getEnd().getZonedDateTime(),
                    appointment.getSubject() == null ? "" : appointment.getSubject(),
                    appointment.getBody().getPreview(),
                    resourceMailAddress
            );
        } catch(Exception ex) {
            throw new CommonRbException(ex);
        }
    }

}
