package de.elomagic.rb.backend.dtos;

import jakarta.annotation.Nonnull;

public class VersionDTO {

    private String backendVersion;
    private String timestamp;
    private String frontendVersion;

    public VersionDTO() {}

    public VersionDTO(@Nonnull String backendversion, @Nonnull String timestamp, @Nonnull String frontendVersion) {
        this.backendVersion = backendversion;
        this.timestamp = timestamp;
        this.frontendVersion = frontendVersion;
    }

    public String getBackendVersion() {
        return backendVersion;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getFrontendVersion() {
        return frontendVersion;
    }

}
