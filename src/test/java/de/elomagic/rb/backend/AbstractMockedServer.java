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
    }

    @AfterEach
    public void afterEach() {
        mockServer.stop();
    }

}
