package org.curiosity.rover.store.io;

import org.curiosity.rover.store.DataFormat;

public class DataWriterFactory {

    public static DataWriter getDataWriter(DataFormat format) {
        switch (format) {
            case JSON:
                return new JsonDataWriter();
            default:
                throw new IllegalArgumentException("Unsupported data format: " + format);
        }
    }
}
