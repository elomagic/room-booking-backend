package de.elomagic.rb.backend.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import de.elomagic.rb.backend.exceptions.CommonRbException;
import de.elomagic.rb.backend.dtos.VersionDTO;
import de.elomagic.rb.backend.exceptions.NotAuthorizedException;
import de.elomagic.rb.backend.utils.Json5MapperFactory;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class CoreComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoreComponent.class);

    @Value("${rb.pin}")
    private String authorizationPin;

    @Nonnull
    public VersionDTO getVersion() {
        try {
            ObjectMapper mapper = Json5MapperFactory.create();
            return mapper.readValue(IOUtils.resourceToString("/de/elomagic/rb/backend/version.json", StandardCharsets.UTF_8), VersionDTO.class);
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new CommonRbException(ex.getMessage(), ex);
        }
    }

    public void validatePin(@Nullable String pin) {
        if (!authorizationPin.equals(pin)) {
            throw new NotAuthorizedException();
        }
    }

}
