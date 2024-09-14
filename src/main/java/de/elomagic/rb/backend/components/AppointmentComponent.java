package de.elomagic.rb.backend.components;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import de.elomagic.rb.backend.dtos.AppointmentDTO;
import de.elomagic.rb.backend.providers.EwsProvider;

import org.apache.commons.lang3.StringUtils;
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

    private final EwsProvider provider;

    @Value("${rb.ext.ews.resourcesFilter}")
    private String resourcesFilter;

    public AppointmentComponent(@Autowired EwsProvider provider) {
        this.provider = provider;
    }

    @Nonnull
    public Set<AppointmentDTO> getAppointmentsOfToday(@Nonnull String resourceAddress) {
        if (StringUtils.isNoneBlank(resourcesFilter) && !resourcesFilter.contains(resourceAddress)) {
            LOGGER.warn("Resource '{}' does not match configured resource filter. Query denied", resourceAddress);
            return Set.of();
        }

        ZonedDateTime start = ZonedDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        ZonedDateTime end = ZonedDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(0);

        return provider.queryAppointments(resourceAddress, start, end);
    }

    @Nonnull
    public AppointmentDTO createAdHocAppointment(@Nonnull AppointmentDTO appointment) {
        if (StringUtils.isNoneBlank(resourcesFilter) && !resourcesFilter.contains(appointment.resourceMailAddress())) {
            LOGGER.warn("Resource '{}' does not match configured resource filter. Creation denied", appointment.resourceMailAddress());
            throw new IllegalArgumentException("Resource '" + appointment.resourceMailAddress() + "' does not match configured resource filter");
        }

        // TODO Validate time range appointment ???
        return provider.createAppointment(appointment);
    }

    @Nullable
    public AppointmentDTO updateCurrentAppointment(@Nonnull AppointmentDTO appointment) {
        if (StringUtils.isNoneBlank(resourcesFilter) && !resourcesFilter.contains(appointment.resourceMailAddress())) {
            LOGGER.warn("Resource '{}' does not match configured resource filter. Update denied", appointment.resourceMailAddress());
            throw new IllegalArgumentException("Resource '" + appointment.resourceMailAddress() + "' does not match configured resource filter");
        }

        // TODO Validate appointment
        return provider.update(appointment);
    }

}
