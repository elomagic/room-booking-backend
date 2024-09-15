package de.elomagic.rb.backend.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Nonnull;

import de.elomagic.rb.backend.exceptions.CommonRbException;

import java.io.IOException;
import java.nio.file.Path;

public final class JsonReader {

    private JsonReader() {}

    public static <T> T read(@Nonnull String s, Class<? extends T> clazz) {
        try {
            return Json5MapperFactory.create().readValue(s, clazz);
        } catch(JsonProcessingException ex) {
            throw new CommonRbException(ex.getMessage(), ex);
        }
    }

    public static <T> T read(@Nonnull Path file, Class<? extends T> clazz) {
        try {
            return Json5MapperFactory.create().readValue(file.toFile(), clazz);
        } catch(IOException ex) {
            throw new CommonRbException(ex.getMessage(), ex);
        }
    }

}
