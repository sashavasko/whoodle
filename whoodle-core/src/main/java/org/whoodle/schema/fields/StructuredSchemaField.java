package org.whoodle.schema.fields;

import org.whoodle.Structured;
import org.whoodle.schema.SchemaField;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;

public class StructuredSchemaField implements SchemaField {
    VarHandle handle;

    public StructuredSchemaField(Class<? extends Structured> clazz, MethodHandles.Lookup lookup, Field field) {
        handle = lookup.findVarHandle(
                clazz,
                field.getName(),
                field.getType()
        );
    }
}
