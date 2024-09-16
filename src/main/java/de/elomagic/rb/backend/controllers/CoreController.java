package de.elomagic.rb.backend.controllers;

import jakarta.annotation.Nonnull;

import de.elomagic.rb.backend.components.CoreComponent;
import de.elomagic.rb.backend.dtos.PinDTO;
import de.elomagic.rb.backend.dtos.VersionDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CoreController {

    private final CoreComponent coreComponent;

    public CoreController(@Autowired CoreComponent coreComponent) {
        this.coreComponent = coreComponent;
    }

    @GetMapping("/api/version")
    VersionDTO getVersion() {
        return coreComponent.getVersion();
    }

    @PostMapping("/api/validate")
    void validate(@Nonnull PinDTO dto) {
        coreComponent.validatePin(dto.getPin());
    }

}
