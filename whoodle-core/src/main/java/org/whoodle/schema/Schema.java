package org.whoodle.schema;

import org.whoodle.Structured;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Schema<T extends Structured> {
    String name;
    List<SchemaField> fieldss = new ArrayList<>();
    Map<String, SchemaField> fieldsByName = new HashMap<>();

    public T get(T object, String name) {
        return null;
    }

    public T get(T object, int index) {
        return null;
    }

    public void set(T object, String name, Object value) {

    }

    public void set(T object, int index, Object value) {

    }
}
