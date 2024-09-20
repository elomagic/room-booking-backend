package de.elomagic.rb.backend.components;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import de.elomagic.rb.backend.dtos.AppointmentDTO;
import de.elomagic.rb.backend.exceptions.IllegalResourceException;
import de.elomagic.rb.backend.providers.IProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Set;

@Component
public class AppointmentComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppointmentComponent.class);

    private final IProvider provider;

    // Default all resources
    @Value("${rb.ext.ews.resourcesFilter:.*}")
    private String resourcesFilterRegEx;

    public AppointmentComponent(@Autowired IProvider provider) {
        this.provider = provider;
    }

    @Nonnull
    public Set<AppointmentDTO> getAppointmentsOfToday(@Nonnull String resourceAddress) {
        if (!resourceAddress.matches(resourcesFilterRegEx)) {
            LOGGER.warn("Resource '{}' does not match configured resource filter. Query denied", resourceAddress);
            return Set.of();
        }

        ZonedDateTime start = ZonedDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        ZonedDateTime end = ZonedDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(0);

        return provider.queryAppointments(resourceAddress, start, end);
    }

    @Nonnull
    public AppointmentDTO createAdHocAppointment(@Nonnull AppointmentDTO appointment) {
        if (!appointment.resourceMailAddress().matches(resourcesFilterRegEx)) {
            LOGGER.warn("Resource '{}' does not match configured resource filter. Creation denied", appointment.resourceMailAddress());
            throw new IllegalResourceException("Resource '" + appointment.resourceMailAddress() + "' does not match configured resource filter");
        }

        // TODO Validate time range appointment ???
        return provider.createAppointment(appointment);
    }

    @Nullable
    public AppointmentDTO updateCurrentAppointment(@Nonnull AppointmentDTO appointment) {
        if (!appointment.resourceMailAddress().matches(resourcesFilterRegEx)) {
            LOGGER.warn("Resource '{}' does not match configured resource filter. Update denied", appointment.resourceMailAddress());
            throw new IllegalResourceException("Resource '" + appointment.resourceMailAddress() + "' does not match configured resource filter");
        }

        // TODO Validate appointment
        return provider.update(appointment);
    }

}
