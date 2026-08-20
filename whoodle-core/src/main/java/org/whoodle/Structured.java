package org.whoodle;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.whoodle.schema.Schema;
import org.whoodle.schema.StructuredClasses;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Supplier;

public interface Structured {
    Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    default <T extends Structured> Schema<T> getSchema(){
        return StructuredClasses.getInstance().schema(getClass());
    }

    default void clear() {
    }

    default boolean isEmpty() {
        return true;
    }

    default Object get(String fieldName) {
        return StructuralSupport.get(this, fieldName);
    }

    default Object get(int fieldId) {
        return StructuralSupport.get(this, fieldId);
    }

    default void set(String fieldName, Object value) {
        StructuralSupport.set(this, fieldName, value);
    }

    default void set(int fieldId, Object value) {
        StructuralSupport.set(this, fieldId, value);
    }

    default void fromJsonBytes(byte[] bytes) {
        fromJsonBytes(bytes, DEFAULT_CHARSET);
    }

    default void fromJsonBytes(byte[] bytes, final String charsetName) {
        fromJsonBytes(bytes, 0, bytes.length, Charset.forName(charsetName));
    }

    default void fromJsonBytes(byte[] bytes, final Charset charset) {
        fromJsonBytes(bytes, 0, bytes.length, charset);
    }

    default void fromJsonBytes(byte[] bytes, int offset, int length, final Charset charset) {
        String containerJsonString = new String(bytes, offset, length, charset);
        throw new UnsupportedOperationException("fromJsonBytes(bytes, offset, length, charset) is not supported");
    }

    default byte[] toJsonBytes() {
        return toJsonBytes(DEFAULT_CHARSET);
    }

    default byte[] toJsonBytes(final String charset) {
        return toJsonBytes(Charset.forName(charset));
    }

    default byte[] toJsonBytes(final Charset charset) {
        throw new UnsupportedOperationException("toJsonBytes(bytes, charset) is not supported");
    }

    default ObjectNode toJson() {
        throw new UnsupportedOperationException("toJson(bytes, charset) is not supported");
    }

    default Map<String, Object> toMap(Supplier<Map<String, Object>> mapFactory) {
        throw new UnsupportedOperationException("toMap(mapFactory) is not supported");
    }

    default <T extends Structured> T fromMap(Map<String, Object> map) {
        throw new UnsupportedOperationException("fromMap(map) is not supported");
    }

    default <T extends Structured> T fromJson(JsonNode json) {
        throw new UnsupportedOperationException("fromJson(json) is not supported");
    }

    default <T extends Structured> T fromYaml(String yaml) {
        throw new UnsupportedOperationException("fromYaml(yaml) is not supported");
    }

    static boolean equals(Structured o1, Object o2) {
        if (o1 == o2) return true;
        if (o2 == null || o1.getClass() != o2.getClass()) return false;
        Structured other = (Structured) o2;
        if (!o1.getSchema().equals(other.getSchema()))
            return false;

        // TODO: compare fields
        return false;
    }

    static String toString(Structured o) {
            return o.toJson().toString();
    }
}
