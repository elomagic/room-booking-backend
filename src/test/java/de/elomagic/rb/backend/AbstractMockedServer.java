package de.elomagic.rb.backend;

import de.elomagic.rb.backend.providers.graph.GraphProvider;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockserver.integration.ClientAndServer;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;

public abstract class AbstractMockedServer {

    private ClientAndServer mockServer;

    protected Integer getPort() {
        return mockServer.getPort();
    }

    @Autowired
    private GraphProvider provider;

    @BeforeEach
    public void beforeEach() throws Exception {
        mockServer = startClientAndServer();

        Field field = provider.getClass().getDeclaredField("baseURL");
        field.setAccessible(true);
        field.set(provider, "http://localhost:%s".formatted(getPort()));
        // "rb.ext.graph.uri"

        provider.setOAuthConfiguration(
                "http://localhost:%s/token".formatted(getPort()),
                "f256b324-1b01-4de7-9eaf-023bcdf34c38",
                "a96fb733-ad9b-4cc2-af22-5c77b589b964",
                "http://localhost:%s".formatted(getPort())
        );
    }

    @AfterEach
    public void afterEach() {
        mockServer.stop();
    }

}
