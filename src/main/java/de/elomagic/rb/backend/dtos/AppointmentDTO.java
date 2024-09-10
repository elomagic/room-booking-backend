package de.elomagic.rb.backend.dtos;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.time.ZonedDateTime;
import java.util.Objects;

public record AppointmentDTO(
        @Nullable String uid,
        @Nonnull ZonedDateTime start,
        @Nonnull ZonedDateTime end,
        @Nonnull String subject,
        @Nonnull String body,
        @Nonnull String resourceMailAddress) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppointmentDTO that = (AppointmentDTO) o;
        return Objects.equals(uid, that.uid) && resourceMailAddress.equals(that.resourceMailAddress);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(uid);
        result = 31 * result + resourceMailAddress.hashCode();
        return result;
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
