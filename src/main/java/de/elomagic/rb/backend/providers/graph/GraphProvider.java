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
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Profile;
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
@Profile("!test")
@ConditionalOnExpression("'${rb.ext.apiType}' == 'graph'")
public class GraphProvider extends AbstractRestClient implements IProvider {

    // private GraphServiceClient graphClient;

    @Value("${rb.ext.graph.uri}")
    private String baseURL;

    /*
    // TODO
    private String clientId = "YOUR_CLIENT_ID";
    // TODO
    private String tenantId = "YOUR_TENANT_ID"; // or "common" for multi-tenant apps

    @PostConstruct
    public void init() throws CommonRbException {

        DeviceCodeCredential credential = new DeviceCodeCredentialBuilder()
                .clientId(clientId)
                .tenantId(tenantId)
                .challengeConsumer(challenge -> System.out.println(challenge.getMessage()))
                .build();

        graphClient = new GraphServiceClient(credential, "User.Read");

    }
    */

    @Nonnull
    @Override
    public Set<AppointmentDTO> queryAppointments(@Nonnull String resourceAddress, @Nonnull ZonedDateTime start, @Nonnull ZonedDateTime end) {
        /*
        EventCollectionResponse events = graphClient
                .users()
                .byUserId(resourceAddress)
                .calendarView()
                .get(requestConfiguration -> {
                    if (requestConfiguration.queryParameters != null) {
                        requestConfiguration.queryParameters.startDateTime = start.toString();
                        requestConfiguration.queryParameters.endDateTime = end.toString();
                    }
                });

        return events == null || events.getValue() == null ? Set.of() : events.getValue()
                .stream()
                .map(e -> mapAppointmentToDTO(e, resourceAddress))
                .collect(Collectors.toSet());
         */

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
                    .map(a -> mapAppointmentToDTO(a, resourceAddress))
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
