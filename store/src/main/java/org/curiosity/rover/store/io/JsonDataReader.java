package org.curiosity.rover.store.io;

import com.fasterxml.jackson.databind.JsonNode;
import org.curiosity.rover.store.record.JsonRecord;
import org.curiosity.rover.store.record.Record;
import org.curiosity.rover.store.util.IOUtil;

import java.io.File;
import java.util.Collection;
import java.util.function.Function;

public class JsonDataReader implements DataReader {
    @Override
    public Collection<Record> readData(File file) {
        Function<JsonNode, Record> mapper = (JsonRecord::new);
        return IOUtil.readFile(file, mapper);
    }
}
