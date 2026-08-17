package org.whoodle.schema;

import org.whoodle.Structured;

import java.sql.Timestamp;

public enum DataTypes{

    binary,
    string,
    int32,
    int64,
    float64,
    date,
    localDate,
    timestamp,
    clazz,
    struct,
    variant,
    invalid;

    public static Class<?> toClass(String className){
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            try{
                return Class.forName(className);
            } catch (ClassNotFoundException ee) {
                throw new SchemaException("Cannot load class " + className + ". Classpath:" + System.getProperty("java.class.path"), ee);
            }
        }
    }
}
