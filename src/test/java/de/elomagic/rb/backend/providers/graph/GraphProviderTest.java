package de.elomagic.rb.backend.providers.graph;

import de.elomagic.rb.backend.AbstractMockedServer;
import de.elomagic.rb.backend.dtos.AppointmentDTO;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest
@TestPropertySource(properties = {
        "rb.ext.apiType = graph",
        "key2 = value2"
})
class GraphProviderTest extends AbstractMockedServer {

    @Autowired
    private BeanFactory beanFactory;

    @Value("${rb.ext.graph.token}")
    private String token;

    @Test
    void testQueryAppointments() throws IOException {
        String ps = IOUtils.resourceToString("ms-graph-events1.json5", StandardCharsets.UTF_8, GraphProviderTest.class.getClassLoader());

        try (MockServerClient client = new MockServerClient("localhost", getPort())) {

            ZonedDateTime start = ZonedDateTime.now();
            ZonedDateTime end = ZonedDateTime.now().plusDays(1);

            client.when(
                    request()
                            // /v1.0/users/%s/calendar/events?startDateTime=%s&endDateTime=%s
                            .withMethod("GET")
                            .withPath("/v1.0/users/room_maui@my-company.internal/calendar/events")
                            .withHeader("Authorization", "Bearer " + token)
                            //.withQueryStringParameter("startDateTime", start.toString())
                            //.withQueryStringParameter("endDateTime", end.toString())
                    )
                    .respond(
                            response()
                                    .withStatusCode(200)
                                    .withBody(ps)
                    );

            // Execute the text
            GraphProvider provider = beanFactory.getBean(GraphProvider.class);
            Set<AppointmentDTO> appointments = provider.queryAppointments("room_maui@my-company.internal", start, end);
            assertEquals(1, appointments.size());

        }
    }
}