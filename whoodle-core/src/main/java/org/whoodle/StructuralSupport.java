package org.whoodle;

import org.whoodle.schema.Schema;
import org.whoodle.schema.StructuredClasses;

final class StructuralSupport {
    static Object get(Structured object, String name) {
        return schema(object).get(object, name);
    }

    static Object get(Structured object, int index) {
        return schema(object).get(object, index);
    }

    static void set(
            Structured object,
            String name,
            Object value
    ) {
        schema(object).set(object, name, value);
    }

    static void set(
            Structured object,
            int index,
            Object value
    ) {
        schema(object).set(object, index, value);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Structured> Schema<T> schema(T object) {
        return (Schema<T>) StructuredClasses.getInstance().schema(object.getClass());
    }
}