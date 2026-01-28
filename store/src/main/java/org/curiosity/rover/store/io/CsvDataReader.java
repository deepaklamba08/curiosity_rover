package org.curiosity.rover.store.io;

import org.curiosity.rover.store.record.MapRecord;
import org.curiosity.rover.store.record.Record;
import org.curiosity.rover.store.value.Value;
import org.curiosity.rover.store.value.Values;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CsvDataReader implements DataReader {

    @Override
    public Collection<Record> readData(File file) {
        List<Record> records = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                return records;
            }

            String[] headers = headerLine.split(",");
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",", -1);
                Map<String, Value> recordMap = new HashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    String value = i < values.length ? values[i] : "";
                    recordMap.put(headers[i].trim(), Values.stringValue(value.trim()));
                }
                records.add(new MapRecord(recordMap));
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading CSV file: " + file.getAbsolutePath(), e);
        }
        return records;
    }
}
