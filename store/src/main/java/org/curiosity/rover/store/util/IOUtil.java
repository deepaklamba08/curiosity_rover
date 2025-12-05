package org.curiosity.rover.store.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;


import java.io.File;
import java.util.function.Function;

public class IOUtil {

    public static <T> List<T> readFile(File filePath, Function<JsonNode, T> mapper) {
        try (InputStream inputStream = Files.newInputStream(filePath.toPath())) {
            List<T> elements = new ArrayList<>();
            JsonNode jsonNode = DataUtil.OBJECT_MAPPER.readTree(inputStream);
            jsonNode.elements().forEachRemaining(element -> {
                elements.add(mapper.apply(element));
            });
            return elements;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> void writeFile(File filePath, Function<T, JsonNode> mapper, List<T> elements) {
        ArrayNode jsonElement = DataUtil.OBJECT_MAPPER.createArrayNode();
        elements.stream().map(mapper).forEach(jsonElement::add);
        try (OutputStream outputStream = Files.newOutputStream(filePath.toPath())) {
            DataUtil.OBJECT_MAPPER.writeValue(outputStream, jsonElement);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
