package org.curiosity.rover.store.model;

public enum DataFormat {

    JSON("json"),
    CSV("csv");

    private String formatName;

    DataFormat(String formatName) {
        this.formatName = formatName;
    }

    public String getFormatName() {
        return formatName;
    }

    public static DataFormat getDataFormat(String formatName) {
        if (DataFormat.JSON.getFormatName().equalsIgnoreCase(formatName)) {
            return DataFormat.JSON;
        } else if (DataFormat.CSV.getFormatName().equalsIgnoreCase(formatName)) {
            return DataFormat.CSV;
        } else {
            throw new IllegalArgumentException("Unsupported data format: " + formatName);
        }
    }
}
