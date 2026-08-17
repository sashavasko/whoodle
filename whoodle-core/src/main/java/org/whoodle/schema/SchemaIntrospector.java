package org.whoodle.schema;

import org.whoodle.Structured;

public interface SchemaIntrospector {
    <T extends Structured> Schema<T> introspect(Class<?> type);
}
