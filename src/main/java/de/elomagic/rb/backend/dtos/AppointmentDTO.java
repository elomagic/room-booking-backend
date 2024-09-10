package de.elomagic.rb.backend.dtos;

import jakarta.annotation.Nonnull;

import java.time.ZonedDateTime;

public record AppointmentDTO(
        @Nonnull String uid,
        @Nonnull ZonedDateTime start,
        @Nonnull ZonedDateTime end,
        @Nonnull String subject) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppointmentDTO that = (AppointmentDTO) o;
        return uid.equals(that.uid);
    }

    @Override
    public int hashCode() {
        return uid.hashCode();
    }

    @Override
    public String toString() {
        return "AppointmentDTO{" +
                "uid='" + uid + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", subject='" + subject + '\'' +
                '}';
    }

}
