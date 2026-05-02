package com.bookstore.management.repository;

import com.bookstore.management.config.StorageProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class JsonLineFileRepository<T> {

    private final ObjectMapper objectMapper;
    private final Path filePath;
    private final Class<T> recordType;

    protected JsonLineFileRepository(ObjectMapper objectMapper, StorageProperties storageProperties, String fileName,
                                     Class<T> recordType) {
        this.objectMapper = objectMapper;
        this.recordType = recordType;
        this.filePath = Path.of(storageProperties.getRoot(), fileName).toAbsolutePath().normalize();
        initializeStorage();
    }

    protected synchronized List<T> readAll() {
        try {
            initializeStorage();
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            List<T> records = new ArrayList<>();
            for (String line : lines) {
                if (!line.isBlank()) {
                    records.add(objectMapper.readValue(line, recordType));
                }
            }
            return records;
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to read file: " + filePath, exception);
        }
    }

    protected synchronized void writeAll(List<T> records) {
        try {
            initializeStorage();
            List<String> lines = new ArrayList<>();
            for (T record : records) {
                lines.add(objectMapper.writeValueAsString(record));
            }
            Files.write(filePath, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to write file: " + filePath, exception);
        }
    }

    protected synchronized Optional<T> findOne(Predicate<T> predicate) {
        return readAll().stream().filter(predicate).findFirst();
    }

    protected synchronized void store(T record, Function<T, String> idExtractor) {
        List<T> records = new ArrayList<>(readAll());
        String id = idExtractor.apply(record);
        records.removeIf(existing -> idExtractor.apply(existing).equals(id));
        records.add(record);
        writeAll(records);
    }

    protected synchronized void deleteWhere(Predicate<T> predicate) {
        List<T> records = new ArrayList<>(readAll());
        records.removeIf(predicate);
        writeAll(records);
    }

    protected Path getFilePath() {
        return filePath;
    }

    private void initializeStorage() {
        try {
            Files.createDirectories(filePath.getParent());
            if (Files.notExists(filePath)) {
                Files.writeString(filePath, "", StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to prepare storage file: " + filePath, exception);
        }
    }
}
