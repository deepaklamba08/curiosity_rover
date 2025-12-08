package org.curiosity.rover.store.io;

import org.curiosity.rover.store.DataFormat;

public class DataReaderFactory {

    public static DataReader getDataReader(DataFormat format) {
        switch (format) {
            case JSON:
                return new JsonDataReader();
            default:
                throw new IllegalArgumentException("Unsupported data format: " + format);
        }
    }
}
