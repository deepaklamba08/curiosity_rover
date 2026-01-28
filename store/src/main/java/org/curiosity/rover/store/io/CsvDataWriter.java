package org.curiosity.rover.store.io;

import org.curiosity.rover.store.record.Record;
import org.curiosity.rover.store.value.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class CsvDataWriter implements DataWriter {

    @Override
    public void writeData(File file, Collection<Record> records) {
        if (records == null || records.isEmpty()) {
            return;
        }

        // Get all unique field names for the header
        List<String> headers = records.stream()
                .flatMap(r -> r.fieldNames().stream())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Write header
            writer.write(String.join(",", headers));
            writer.newLine();

            // Write records
            for (Record record : records) {
                List<String> values = new ArrayList<>();
                for (String header : headers) {
                    Value value = record.getValue(header);
                    values.add(formatValue(value));
                }
                writer.write(String.join(",", values));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error writing CSV file: " + file.getAbsolutePath(), e);
        }
    }

    private String formatValue(Value value) {
        if (value == null) {
            return "";
        }
        if (value instanceof StringValue) {
            return ((StringValue) value).getValue();
        } else if (value instanceof IntegerValue) {
            return String.valueOf(((IntegerValue) value).getValue());
        } else if (value instanceof LongValue) {
            return String.valueOf(((LongValue) value).getValue());
        } else if (value instanceof BooleanValue) {
            return String.valueOf(((BooleanValue) value).getValue());
        }
        // For complex types like ArrayValue or MapValue, we might need JSON-like string
        // representation
        // but for basic CSV, we'll just use a placeholder or empty string
        return "";
    }
}
