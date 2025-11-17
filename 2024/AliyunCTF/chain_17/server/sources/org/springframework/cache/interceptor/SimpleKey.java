package org.springframework.cache.interceptor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/interceptor/SimpleKey.class */
public class SimpleKey implements Serializable {
    public static final SimpleKey EMPTY = new SimpleKey(new Object[0]);
    private final Object[] params;
    private transient int hashCode;

    public SimpleKey(Object... elements) {
        Assert.notNull(elements, "Elements must not be null");
        this.params = (Object[]) elements.clone();
        this.hashCode = Arrays.deepHashCode(this.params);
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof SimpleKey) {
                SimpleKey that = (SimpleKey) other;
                if (Arrays.deepEquals(this.params, that.params)) {
                }
            }
            return false;
        }
        return true;
    }

    public final int hashCode() {
        return this.hashCode;
    }

    public String toString() {
        return getClass().getSimpleName() + " " + Arrays.deepToString(this.params);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.hashCode = Arrays.deepHashCode(this.params);
    }
}
