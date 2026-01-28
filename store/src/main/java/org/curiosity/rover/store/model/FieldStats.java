package org.curiosity.rover.store.model;

import java.util.Objects;

/**
 * Represents statistical information for a field within a data file.
 * Tracks metrics such as null count, total count, duplicates, and numeric
 * statistics.
 */

public class FieldStats {

    private final String fieldName;
    private final long totalCount;
    private final long nullCount;
    private final long distinctCount;
    private final long duplicateCount;

    // Numeric statistics (null if field is non-numeric)
    private final Double minValue;
    private final Double maxValue;
    private final Double sum;
    private final Double average;

    // String statistics (null if field is non-string)
    private final Integer minLength;
    private final Integer maxLength;
    private final Double avgLength;

    private FieldStats(String fieldName, long totalCount, long nullCount, long distinctCount,
            long duplicateCount, Double minValue, Double maxValue, Double sum,
            Double average, Integer minLength, Integer maxLength, Double avgLength) {
        this.fieldName = fieldName;
        this.totalCount = totalCount;
        this.nullCount = nullCount;
        this.distinctCount = distinctCount;
        this.duplicateCount = duplicateCount;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.sum = sum;
        this.average = average;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.avgLength = avgLength;
    }

    public String getFieldName() {
        return fieldName;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public long getNullCount() {
        return nullCount;
    }

    public long getNonNullCount() {
        return totalCount - nullCount;
    }

    public long getDistinctCount() {
        return distinctCount;
    }

    public long getDuplicateCount() {
        return duplicateCount;
    }

    public Double getMinValue() {
        return minValue;
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public Double getSum() {
        return sum;
    }

    public Double getAverage() {
        return average;
    }

    public Integer getMinLength() {
        return minLength;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public Double getAvgLength() {
        return avgLength;
    }

    public boolean hasNumericStats() {
        return minValue != null || maxValue != null || sum != null || average != null;
    }

    public boolean hasStringStats() {
        return minLength != null || maxLength != null || avgLength != null;
    }

    public double getNullPercentage() {
        return totalCount > 0 ? (nullCount * 100.0) / totalCount : 0.0;
    }

    public double getDistinctPercentage() {
        return totalCount > 0 ? (distinctCount * 100.0) / totalCount : 0.0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        FieldStats that = (FieldStats) o;
        return totalCount == that.totalCount &&
                nullCount == that.nullCount &&
                distinctCount == that.distinctCount &&
                duplicateCount == that.duplicateCount &&
                Objects.equals(fieldName, that.fieldName) &&
                Objects.equals(minValue, that.minValue) &&
                Objects.equals(maxValue, that.maxValue) &&
                Objects.equals(sum, that.sum) &&
                Objects.equals(average, that.average) &&
                Objects.equals(minLength, that.minLength) &&
                Objects.equals(maxLength, that.maxLength) &&
                Objects.equals(avgLength, that.avgLength);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldName, totalCount, nullCount, distinctCount, duplicateCount,
                minValue, maxValue, sum, average, minLength, maxLength, avgLength);
    }

    @Override
    public String toString() {
        return "FieldStats{" +
                "fieldName='" + fieldName + '\'' +
                ", totalCount=" + totalCount +
                ", nullCount=" + nullCount +
                ", distinctCount=" + distinctCount +
                ", duplicateCount=" + duplicateCount +
                ", minValue=" + minValue +
                ", maxValue=" + maxValue +
                ", sum=" + sum +
                ", average=" + average +
                ", minLength=" + minLength +
                ", maxLength=" + maxLength +
                ", avgLength=" + avgLength +
                '}';
    }

    public static class Builder {
        private String fieldName;
        private long totalCount;
        private long nullCount;
        private long distinctCount;
        private long duplicateCount;
        private Double minValue;
        private Double maxValue;
        private Double sum;
        private Double average;
        private Integer minLength;
        private Integer maxLength;
        private Double avgLength;

        public Builder withFieldName(String fieldName) {
            this.fieldName = fieldName;
            return this;
        }

        public Builder withTotalCount(long totalCount) {
            this.totalCount = totalCount;
            return this;
        }

        public Builder withNullCount(long nullCount) {
            this.nullCount = nullCount;
            return this;
        }

        public Builder withDistinctCount(long distinctCount) {
            this.distinctCount = distinctCount;
            return this;
        }

        public Builder withDuplicateCount(long duplicateCount) {
            this.duplicateCount = duplicateCount;
            return this;
        }

        public Builder withMinValue(Double minValue) {
            this.minValue = minValue;
            return this;
        }

        public Builder withMaxValue(Double maxValue) {
            this.maxValue = maxValue;
            return this;
        }

        public Builder withSum(Double sum) {
            this.sum = sum;
            return this;
        }

        public Builder withAverage(Double average) {
            this.average = average;
            return this;
        }

        public Builder withMinLength(Integer minLength) {
            this.minLength = minLength;
            return this;
        }

        public Builder withMaxLength(Integer maxLength) {
            this.maxLength = maxLength;
            return this;
        }

        public Builder withAvgLength(Double avgLength) {
            this.avgLength = avgLength;
            return this;
        }

        public FieldStats build() {
            return new FieldStats(fieldName, totalCount, nullCount, distinctCount, duplicateCount,
                    minValue, maxValue, sum, average, minLength, maxLength, avgLength);
        }
    }
}
