package de.elomagic.rb.backend.providers.graph;

import de.elomagic.rb.backend.AbstractMockedServer;
import de.elomagic.rb.backend.dtos.AppointmentDTO;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockserver.model.Header.header;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.Parameter.param;
import static org.mockserver.model.ParameterBody.params;

@SpringBootTest
@TestPropertySource(properties = {
        "rb.ext.apiType = graph",
        "key2 = value2"
})
class GraphProviderTest extends AbstractMockedServer {

    @Autowired
    private BeanFactory beanFactory;

    @Test
    void testQueryAppointments() throws IOException {
        String ps = IOUtils.resourceToString("ms-graph-events1.json5", StandardCharsets.UTF_8, GraphProviderTest.class.getClassLoader());
        String t = IOUtils.resourceToString("token.json5", StandardCharsets.UTF_8, GraphProviderTest.class.getClassLoader());

        try (MockServerClient client = new MockServerClient("localhost", getPort())) {

            ZonedDateTime start = ZonedDateTime.now();
            ZonedDateTime end = ZonedDateTime.now().plusDays(1);

            client.when(
                            request()
                                    .withMethod("POST")
                                    .withPath("/token")
                                    .withHeaders(
                                            header("Content-Type", "application/x-www-form-urlencoded")
                                    )
                                    .withBody(
                                            params(
                                                    param("grant_type", "client_credentials"),
                                                    param("client_id", "f256b324-1b01-4de7-9eaf-023bcdf34c38"),
                                                    param("client_secret", "a96fb733-ad9b-4cc2-af22-5c77b589b964")
                                            )
                                    )
                    )
                    .respond(
                            response()
                                    .withStatusCode(200)
                                    .withBody(t)
                    );

            client.when(
                    request()
                            // /v1.0/users/%s/calendar/events?startDateTime=%s&endDateTime=%s
                            .withMethod("GET")
                            .withPath("/v1.0/users/room_maui@my-company.internal/calendar/events")
                            .withHeader("Authorization", "Bearer FOmiJVpD1VKSIKfyILpf+2OzLdW7t2grZdJ8NuHLAVoRkck2SyzAyFZ0B7XwTl4h")
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