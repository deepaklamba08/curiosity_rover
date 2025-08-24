package org.curiosity.rover.basic.cfg;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * wrapper for configuration
 */
public interface Configuration {

    public Iterator<String> getFieldNames();

    public Configuration getConfiguration(String fieldName);

    public Configuration getConfiguration(String fieldName, Configuration defaultValue);

    public boolean isNull();

    public boolean isNull(String fieldName);

    public boolean hasField(String fieldName);

    public boolean isPresent(String fieldName);

    public String getString(String fieldName);

    public String getString(String fieldName, String defaultValue);

    public boolean isString(String fieldName);

    public boolean isString();

    public Integer getInt(String fieldName);

    public Integer getInt(String fieldName, int defaultValue);

    public boolean isInt(String fieldName);

    public boolean isInt();

    public Long getLong(String fieldName);

    public Long getLong(String fieldName, long defaultValue);

    public boolean isLong(String fieldName);

    public boolean isLong();

    public Float getFloat(String fieldName);

    public Float getFloat(String fieldName, float defaultValue);

    public boolean isFloat(String fieldName);

    public boolean isFloat();

    public boolean isDouble();

    public Boolean getBoolean(String fieldName);

    public Boolean getBoolean(String fieldName, boolean defaultValue);

    public boolean isBoolean(String fieldName);

    public boolean isBoolean();

    public Date getDate(String fieldName);

    public Date getDate(String fieldName, Date defaultValue);

    public List<Configuration> getListValue(String fieldName);

    public boolean isArray(String fieldName);

    public boolean isArray();

    public <T> List<T> getListValue(String fieldName, Function<Configuration, T> converter);

    public List<Configuration> asList();

    public <T> List<T> asList(Function<Configuration, T> converter);

    public <T> Map<String, T> getMapValues(String fieldName, Function<Configuration, T> converter);

    public Map<String, Configuration> getMapValues(String fieldName);

    public boolean isObject(String fieldName);

    public boolean isObject();

    public String asString();

    public boolean asBoolean();

    public int asInt();

    public long asLong();

    public double asDouble();

}
