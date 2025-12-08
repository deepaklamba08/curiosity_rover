package org.curiosity.rover.store.io;

import org.curiosity.rover.store.record.Record;

import java.io.File;
import java.util.Collection;

public interface DataWriter {

    public void writeData(File file, Collection<Record> records);
}
