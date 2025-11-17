package org.springframework.objenesis.instantiator.gcj;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import org.springframework.objenesis.ObjenesisException;
import org.springframework.objenesis.instantiator.ObjectInstantiator;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/objenesis/instantiator/gcj/GCJInstantiatorBase.class */
public abstract class GCJInstantiatorBase<T> implements ObjectInstantiator<T> {
    static Method newObjectMethod = null;
    static ObjectInputStream dummyStream;
    protected final Class<T> type;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/objenesis/instantiator/gcj/GCJInstantiatorBase$DummyStream.class */
    public static class DummyStream extends ObjectInputStream {
    }

    @Override // org.springframework.objenesis.instantiator.ObjectInstantiator
    public abstract T newInstance();

    private static void initialize() {
        if (newObjectMethod == null) {
            try {
                newObjectMethod = ObjectInputStream.class.getDeclaredMethod("newObject", Class.class, Class.class);
                newObjectMethod.setAccessible(true);
                dummyStream = new DummyStream();
            } catch (IOException | NoSuchMethodException | RuntimeException e) {
                throw new ObjenesisException(e);
            }
        }
    }

    public GCJInstantiatorBase(Class<T> type) {
        this.type = type;
        initialize();
    }
}
