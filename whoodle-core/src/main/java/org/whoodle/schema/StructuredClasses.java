package org.whoodle.schema;

import org.whoodle.Structured;

public class StructuredClasses implements SchemaRegistry {

    private SchemaIntrospector introspector;
    private final ClassValue<Schema<?>> schemas =
            new ClassValue<>() {
                @Override
                protected Schema<?> computeValue(Class<?> type) {
                    return introspector.introspect(type);
                }
            };
    private static StructuredClasses instance = new StructuredClasses(new StructuredSchemaIntrospector());

    public StructuredClasses(SchemaIntrospector introspector) {
        this.introspector = introspector;
    }

    public static StructuredClasses getInstance(){
        return instance;
    }

    public <T extends Structured> Schema<T> schema(String className) {
        Class<?> clazz = DataTypes.toClass(className);
        return schema(clazz);
    }

    public <T extends Structured> Schema<T> schema(Class<?> type) {
        return cast(schemas.get(type));
    }

    @SuppressWarnings("unchecked")
    private static <T extends Structured> Schema<T> cast(
            Schema<?> schema) {
        return (Schema<T>) schema;
    }
}
