package de.elomagic.rb.backend.controllers;

import de.elomagic.rb.backend.components.CoreComponent;
import de.elomagic.rb.backend.dtos.VersionDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VersionController {

    private final CoreComponent coreComponent;

    public VersionController(@Autowired CoreComponent coreComponent) {
        this.coreComponent = coreComponent;
    }

    @GetMapping("/api/version")
    VersionDTO getVersion() {
        return coreComponent.getVersion();
    }

}
