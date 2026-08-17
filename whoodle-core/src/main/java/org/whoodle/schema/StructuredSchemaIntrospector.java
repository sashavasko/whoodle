package org.whoodle.schema;

import org.whoodle.Structured;

public class StructuredSchemaIntrospector implements SchemaIntrospector{
    @Override
    public <T extends Structured> Schema<T> introspect(Class<?> type) {
        if (!Structured.class.isAssignableFrom(type)) {
            throw new SchemaException("Invalid structured type " + type.getName());
        }

        @SuppressWarnings("unchecked")
        Class<T> structuredClass = (Class<T>) type;

        return null;
    }
}
