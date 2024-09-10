package de.elomagic.rb.backend.controllers;

import de.elomagic.rb.backend.components.AppointmentComponent;
import de.elomagic.rb.backend.dtos.AppointmentDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AppointmentController {

    private final AppointmentComponent appointmentComponent;

    public AppointmentController(@Autowired AppointmentComponent appointmentComponent) {
        this.appointmentComponent = appointmentComponent;
    }


    @GetMapping("/api/appointment")
    List<AppointmentDTO> getAppointment() {
        return appointmentComponent.getAppointmentsOfToday();
    }

    @PostMapping("/api/appointment")
    AppointmentDTO createAppointment() {
        return appointmentComponent.createAdHocAppointment(123);
    }

    @PutMapping("/api/appointment")
    AppointmentDTO updateAppointment() {
        return appointmentComponent.updateCurrentAppointment(123);
    }

}
