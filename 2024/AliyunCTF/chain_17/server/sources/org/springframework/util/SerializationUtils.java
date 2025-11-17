package org.springframework.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/SerializationUtils.class */
public abstract class SerializationUtils {
    @Nullable
    public static byte[] serialize(@Nullable Object object) {
        if (object == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            try {
                oos.writeObject(object);
                oos.flush();
                oos.close();
                return baos.toByteArray();
            } finally {
            }
        } catch (IOException ex) {
            throw new IllegalArgumentException("Failed to serialize object of type: " + object.getClass(), ex);
        }
    }

    @Nullable
    @Deprecated
    public static Object deserialize(@Nullable byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            try {
                Object readObject = ois.readObject();
                ois.close();
                return readObject;
            } finally {
            }
        } catch (IOException ex) {
            throw new IllegalArgumentException("Failed to deserialize object", ex);
        } catch (ClassNotFoundException ex2) {
            throw new IllegalStateException("Failed to deserialize object type", ex2);
        }
    }

    public static <T extends Serializable> T clone(T object) {
        return (T) deserialize(serialize(object));
    }
}
