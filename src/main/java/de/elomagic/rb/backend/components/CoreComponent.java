package de.elomagic.rb.backend.components;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.elomagic.rb.backend.CommonRbException;
import de.elomagic.rb.backend.dtos.VersionDTO;
import de.elomagic.rb.backend.utils.Json5MapperFactory;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class CoreComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoreComponent.class);

    public VersionDTO getVersion() {
        try {
            ObjectMapper mapper = Json5MapperFactory.create();
            return mapper.readValue(IOUtils.resourceToString("/de/elomagic/rb/backend/version.json", StandardCharsets.UTF_8), VersionDTO.class);
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new CommonRbException(ex.getMessage(), ex);
        }
    }

}
