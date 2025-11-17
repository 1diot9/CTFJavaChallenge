package org.h2.mvstore.type;

import java.lang.Thread;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import org.h2.mvstore.DataUtils;
import org.h2.mvstore.WriteBuffer;
import org.h2.mvstore.type.StatefulDataType;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/type/MetaType.class */
public final class MetaType<D> extends BasicDataType<DataType<?>> {
    private final D database;
    private final Thread.UncaughtExceptionHandler exceptionHandler;
    private final Map<String, Object> cache = new HashMap();

    public MetaType(D d, Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        this.database = d;
        this.exceptionHandler = uncaughtExceptionHandler;
    }

    @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType, java.util.Comparator
    public int compare(DataType<?> dataType, DataType<?> dataType2) {
        throw new UnsupportedOperationException();
    }

    @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
    public int getMemory(DataType<?> dataType) {
        return 24;
    }

    @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
    public void write(WriteBuffer writeBuffer, DataType<?> dataType) {
        Class<?> cls = dataType.getClass();
        StatefulDataType statefulDataType = null;
        if (dataType instanceof StatefulDataType) {
            statefulDataType = (StatefulDataType) dataType;
            StatefulDataType.Factory<D> factory = statefulDataType.getFactory();
            if (factory != null) {
                cls = factory.getClass();
            }
        }
        String name = cls.getName();
        int length = name.length();
        writeBuffer.putVarInt(length).putStringData(name, length);
        if (statefulDataType != null) {
            statefulDataType.save(writeBuffer, this);
        }
    }

    @Override // org.h2.mvstore.type.BasicDataType, org.h2.mvstore.type.DataType
    public DataType<?> read(ByteBuffer byteBuffer) {
        Object newInstance;
        String readString = DataUtils.readString(byteBuffer, DataUtils.readVarInt(byteBuffer));
        try {
            Object obj = this.cache.get(readString);
            if (obj != null) {
                if (obj instanceof StatefulDataType.Factory) {
                    return ((StatefulDataType.Factory) obj).create(byteBuffer, this, this.database);
                }
                return (DataType) obj;
            }
            Class<?> cls = Class.forName(readString);
            boolean z = false;
            try {
                newInstance = cls.getDeclaredField("INSTANCE").get(null);
                z = true;
            } catch (NullPointerException | ReflectiveOperationException e) {
                newInstance = cls.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            }
            if (newInstance instanceof StatefulDataType.Factory) {
                StatefulDataType.Factory factory = (StatefulDataType.Factory) newInstance;
                this.cache.put(readString, factory);
                return factory.create(byteBuffer, this, this.database);
            }
            if (z) {
                this.cache.put(readString, newInstance);
            }
            return (DataType) newInstance;
        } catch (IllegalArgumentException | ReflectiveOperationException | SecurityException e2) {
            if (this.exceptionHandler != null) {
                this.exceptionHandler.uncaughtException(Thread.currentThread(), e2);
            }
            throw new RuntimeException(e2);
        }
    }

    @Override // org.h2.mvstore.type.DataType
    public DataType<?>[] createStorage(int i) {
        return new DataType[i];
    }
}
