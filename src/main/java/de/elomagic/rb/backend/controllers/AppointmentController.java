package de.elomagic.rb.backend.controllers;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import de.elomagic.rb.backend.components.AppointmentComponent;
import de.elomagic.rb.backend.dtos.AppointmentDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class AppointmentController {

    private final AppointmentComponent appointmentComponent;

    public AppointmentController(@Autowired AppointmentComponent appointmentComponent) {
        this.appointmentComponent = appointmentComponent;
    }

    @GetMapping("/api/appointment")
    Set<AppointmentDTO> getAppointment(@RequestHeader("RB-Resource-ID") @Nullable String resourceAddress) {
        return appointmentComponent.getAppointmentsOfToday(resourceAddress == null ? "unknown@localhost" : resourceAddress);
    }

    @PostMapping("/api/appointment")
    AppointmentDTO createAppointment(@Nonnull @RequestBody AppointmentDTO appointment) {
        return appointmentComponent.createAdHocAppointment(appointment);
    }

    @PutMapping("/api/appointment")
    AppointmentDTO updateAppointment(@Nonnull @RequestBody AppointmentDTO appointment) {
        return appointmentComponent.updateCurrentAppointment(appointment);
    }

}
