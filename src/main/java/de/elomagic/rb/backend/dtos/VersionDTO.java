package de.elomagic.rb.backend.dtos;

import jakarta.annotation.Nonnull;

public class VersionDTO {

    private String version;
    private String timestamp;

    public VersionDTO() {}

    public VersionDTO(@Nonnull String version, @Nonnull String timestamp) {
        this.version = version;
        this.timestamp = timestamp;
    }

    public String getVersion() {
        return version;
    }

    public String getTimestamp() {
        return timestamp;
    }

}
