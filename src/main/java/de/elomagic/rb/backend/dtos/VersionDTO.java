package de.elomagic.rb.backend.dtos;

import jakarta.annotation.Nonnull;

public record VersionDTO(
        @Nonnull String backendversion,
        @Nonnull String timestamp,
        @Nonnull String frontendVersion
) {
}
