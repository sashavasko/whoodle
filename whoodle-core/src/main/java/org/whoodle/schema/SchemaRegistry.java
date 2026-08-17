package org.whoodle.schema;

import org.whoodle.Structured;

public interface SchemaRegistry {
    <T extends Structured> Schema<T> schema(String className);
    <T extends Structured> Schema<T> schema(Class<?> type);
}
