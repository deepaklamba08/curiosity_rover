package org.curiosity.rover.store.stats;

import org.curiosity.rover.store.model.FieldStats;
import org.curiosity.rover.store.record.Record;
import org.curiosity.rover.store.value.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Calculator for computing field-level statistics from a collection of records.
 * Supports both numeric and string field statistics.
 */
public class FieldStatsCalculator {

    /**
     * Calculate statistics for a single field across all records.
     *
     * @param records   Collection of records to analyze
     * @param fieldName Name of the field to calculate statistics for
     * @return FieldStats object containing computed statistics
     */
    public FieldStats calculateStats(Collection<Record> records, String fieldName) {
        if (records == null || records.isEmpty()) {
            return createEmptyStats(fieldName);
        }

        long totalCount = records.size();
        long nullCount = 0;
        Set<Object> distinctValues = new HashSet<>();

        // Numeric statistics
        double sum = 0.0;
        Double minValue = null;
        Double maxValue = null;
        boolean isNumeric = false;
        long numericCount = 0;

        // String statistics
        int totalLength = 0;
        Integer minLength = null;
        Integer maxLength = null;
        boolean isString = false;
        long stringCount = 0;

        for (Record record : records) {
            Value value = record.getValue(fieldName);

            if (value == null) {
                nullCount++;
                distinctValues.add(null);
                continue;
            }

            // Handle numeric values
            if (value instanceof IntegerValue) {
                isNumeric = true;
                int intVal = ((IntegerValue) value).getValue();
                double doubleVal = (double) intVal;

                sum += doubleVal;
                numericCount++;

                if (minValue == null || doubleVal < minValue) {
                    minValue = doubleVal;
                }
                if (maxValue == null || doubleVal > maxValue) {
                    maxValue = doubleVal;
                }

                distinctValues.add(intVal);

            } else if (value instanceof LongValue) {
                isNumeric = true;
                long longVal = ((LongValue) value).getValue();
                double doubleVal = (double) longVal;

                sum += doubleVal;
                numericCount++;

                if (minValue == null || doubleVal < minValue) {
                    minValue = doubleVal;
                }
                if (maxValue == null || doubleVal > maxValue) {
                    maxValue = doubleVal;
                }

                distinctValues.add(longVal);

            } else if (value instanceof StringValue) {
                isString = true;
                String strVal = ((StringValue) value).getValue();

                if (strVal != null) {
                    int length = strVal.length();
                    totalLength += length;
                    stringCount++;

                    if (minLength == null || length < minLength) {
                        minLength = length;
                    }
                    if (maxLength == null || length > maxLength) {
                        maxLength = length;
                    }

                    distinctValues.add(strVal);
                } else {
                    nullCount++;
                    distinctValues.add(null);
                }
            } else {
                // For other value types (Boolean, Array, Map), just track distinct values
                distinctValues.add(value);
            }
        }

        long distinctCount = distinctValues.size();
        long duplicateCount = totalCount - distinctCount;

        Double average = null;
        if (isNumeric && numericCount > 0) {
            average = sum / numericCount;
        }

        Double avgLength = null;
        if (isString && stringCount > 0) {
            avgLength = (double) totalLength / stringCount;
        }

        return new FieldStats.Builder()
                .withFieldName(fieldName)
                .withTotalCount(totalCount)
                .withNullCount(nullCount)
                .withDistinctCount(distinctCount)
                .withDuplicateCount(duplicateCount)
                .withMinValue(minValue)
                .withMaxValue(maxValue)
                .withSum(isNumeric ? sum : null)
                .withAverage(average)
                .withMinLength(minLength)
                .withMaxLength(maxLength)
                .withAvgLength(avgLength)
                .build();
    }

    /**
     * Calculate statistics for multiple fields across all records.
     *
     * @param records    Collection of records to analyze
     * @param fieldNames List of field names to calculate statistics for
     * @return Map of field name to FieldStats
     */
    public List<FieldStats> calculateStats(Collection<Record> records, List<String> fieldNames) {
        if (fieldNames == null || fieldNames.isEmpty()) {
            return Collections.emptyList();
        }

        return fieldNames.stream().map(fieldName -> calculateStats(records, fieldName)).collect(Collectors.toList());
    }

    /**
     * Calculate statistics for all fields present in the records.
     *
     * @param records Collection of records to analyze
     * @return Map of field name to FieldStats for all fields
     */
    public List<FieldStats> calculateAllStats(Collection<Record> records) {
        if (records == null || records.isEmpty()) {
            return Collections.emptyList();
        }

        // Collect all unique field names from all records
        Set<String> allFieldNames = new LinkedHashSet<>();
        for (Record record : records) {
            allFieldNames.addAll(record.fieldNames());
        }

        return calculateStats(records, new ArrayList<>(allFieldNames));
    }

    /**
     * Create empty statistics for a field when no records are available.
     *
     * @param fieldName Name of the field
     * @return FieldStats with zero counts
     */
    private FieldStats createEmptyStats(String fieldName) {
        return new FieldStats.Builder()
                .withFieldName(fieldName)
                .withTotalCount(0)
                .withNullCount(0)
                .withDistinctCount(0)
                .withDuplicateCount(0)
                .build();
    }
}
