package org.apache.logging.log4j.util;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.logging.log4j.status.StatusLogger;

/* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/util/SortedArrayStringMap.class */
public class SortedArrayStringMap implements IndexedStringMap {
    private static final int DEFAULT_INITIAL_CAPACITY = 4;
    private static final long serialVersionUID = -5748905872274478116L;
    private static final int HASHVAL = 31;
    private static final TriConsumer<String, Object, StringMap> PUT_ALL = (key, value, contextData) -> {
        contextData.putValue(key, value);
    };
    private static final String[] EMPTY = Strings.EMPTY_ARRAY;
    private static final String FROZEN = "Frozen collection cannot be modified";
    private transient String[] keys;
    private transient Object[] values;
    private transient int size;
    private static final Method setObjectInputFilter;
    private static final Method getObjectInputFilter;
    private static final Method newObjectInputFilter;
    private int threshold;
    private boolean immutable;
    private transient boolean iterating;

    static {
        Method[] methods = ObjectInputStream.class.getMethods();
        Method setMethod = null;
        Method getMethod = null;
        for (Method method : methods) {
            if (method.getName().equals("setObjectInputFilter")) {
                setMethod = method;
            } else if (method.getName().equals("getObjectInputFilter")) {
                getMethod = method;
            }
        }
        Method newMethod = null;
        if (setMethod != null) {
            try {
                Class<?> clazz = Class.forName("org.apache.logging.log4j.util.internal.DefaultObjectInputFilter");
                Method[] methods2 = clazz.getMethods();
                int length = methods2.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        break;
                    }
                    Method method2 = methods2[i];
                    if (!method2.getName().equals("newInstance") || !Modifier.isStatic(method2.getModifiers())) {
                        i++;
                    } else {
                        newMethod = method2;
                        break;
                    }
                }
            } catch (ClassNotFoundException e) {
            }
        }
        newObjectInputFilter = newMethod;
        setObjectInputFilter = setMethod;
        getObjectInputFilter = getMethod;
    }

    public SortedArrayStringMap() {
        this(4);
    }

    public SortedArrayStringMap(final int initialCapacity) {
        this.keys = EMPTY;
        this.values = EMPTY;
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Initial capacity must be at least zero but was " + initialCapacity);
        }
        this.threshold = ceilingNextPowerOfTwo(initialCapacity == 0 ? 1 : initialCapacity);
    }

    public SortedArrayStringMap(final ReadOnlyStringMap other) {
        this.keys = EMPTY;
        this.values = EMPTY;
        if (other instanceof SortedArrayStringMap) {
            initFrom0((SortedArrayStringMap) other);
        } else if (other != null) {
            resize(ceilingNextPowerOfTwo(other.size()));
            other.forEach(PUT_ALL, this);
        }
    }

    public SortedArrayStringMap(final Map<String, ?> map) {
        this.keys = EMPTY;
        this.values = EMPTY;
        resize(ceilingNextPowerOfTwo(map.size()));
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            putValue(Objects.toString(entry.getKey(), null), entry.getValue());
        }
    }

    private void assertNotFrozen() {
        if (this.immutable) {
            throw new UnsupportedOperationException(FROZEN);
        }
    }

    private void assertNoConcurrentModification() {
        if (this.iterating) {
            throw new ConcurrentModificationException();
        }
    }

    @Override // org.apache.logging.log4j.util.StringMap
    public void clear() {
        if (this.keys == EMPTY) {
            return;
        }
        assertNotFrozen();
        assertNoConcurrentModification();
        Arrays.fill(this.keys, 0, this.size, (Object) null);
        Arrays.fill(this.values, 0, this.size, (Object) null);
        this.size = 0;
    }

    @Override // org.apache.logging.log4j.util.ReadOnlyStringMap
    public boolean containsKey(final String key) {
        return indexOfKey(key) >= 0;
    }

    @Override // org.apache.logging.log4j.util.ReadOnlyStringMap
    public Map<String, String> toMap() {
        Map<String, String> result = new HashMap<>(size());
        for (int i = 0; i < size(); i++) {
            Object value = getValueAt(i);
            result.put(getKeyAt(i), value == null ? null : String.valueOf(value));
        }
        return result;
    }

    @Override // org.apache.logging.log4j.util.StringMap
    public void freeze() {
        this.immutable = true;
    }

    @Override // org.apache.logging.log4j.util.StringMap
    public boolean isFrozen() {
        return this.immutable;
    }

    @Override // org.apache.logging.log4j.util.ReadOnlyStringMap
    public <V> V getValue(String str) {
        int indexOfKey = indexOfKey(str);
        if (indexOfKey < 0) {
            return null;
        }
        return (V) this.values[indexOfKey];
    }

    @Override // org.apache.logging.log4j.util.ReadOnlyStringMap
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override // org.apache.logging.log4j.util.IndexedReadOnlyStringMap
    public int indexOfKey(final String key) {
        if (this.keys == EMPTY) {
            return -1;
        }
        if (key == null) {
            return nullKeyIndex();
        }
        int start = (this.size <= 0 || this.keys[0] != null) ? 0 : 1;
        return Arrays.binarySearch(this.keys, start, this.size, key);
    }

    private int nullKeyIndex() {
        return (this.size <= 0 || this.keys[0] != null) ? -1 : 0;
    }

    @Override // org.apache.logging.log4j.util.StringMap
    public void putValue(final String key, final Object value) {
        assertNotFrozen();
        assertNoConcurrentModification();
        if (this.keys == EMPTY) {
            inflateTable(this.threshold);
        }
        int index = indexOfKey(key);
        if (index >= 0) {
            this.keys[index] = key;
            this.values[index] = value;
        } else {
            insertAt(index ^ (-1), key, value);
        }
    }

    private void insertAt(final int index, final String key, final Object value) {
        ensureCapacity();
        System.arraycopy(this.keys, index, this.keys, index + 1, this.size - index);
        System.arraycopy(this.values, index, this.values, index + 1, this.size - index);
        this.keys[index] = key;
        this.values[index] = value;
        this.size++;
    }

    @Override // org.apache.logging.log4j.util.StringMap
    public void putAll(final ReadOnlyStringMap source) {
        if (source == this || source == null || source.isEmpty()) {
            return;
        }
        assertNotFrozen();
        assertNoConcurrentModification();
        if (source instanceof SortedArrayStringMap) {
            if (this.size == 0) {
                initFrom0((SortedArrayStringMap) source);
                return;
            } else {
                merge((SortedArrayStringMap) source);
                return;
            }
        }
        if (source != null) {
            source.forEach(PUT_ALL, this);
        }
    }

    private void initFrom0(final SortedArrayStringMap other) {
        if (this.keys.length < other.size) {
            this.keys = new String[other.threshold];
            this.values = new Object[other.threshold];
        }
        System.arraycopy(other.keys, 0, this.keys, 0, other.size);
        System.arraycopy(other.values, 0, this.values, 0, other.size);
        this.size = other.size;
        this.threshold = other.threshold;
    }

    private void merge(final SortedArrayStringMap other) {
        String[] myKeys = this.keys;
        Object[] myVals = this.values;
        int newSize = other.size + this.size;
        this.threshold = ceilingNextPowerOfTwo(newSize);
        if (this.keys.length < this.threshold) {
            this.keys = new String[this.threshold];
            this.values = new Object[this.threshold];
        }
        boolean overwrite = true;
        if (other.size() > size()) {
            System.arraycopy(myKeys, 0, this.keys, other.size, this.size);
            System.arraycopy(myVals, 0, this.values, other.size, this.size);
            System.arraycopy(other.keys, 0, this.keys, 0, other.size);
            System.arraycopy(other.values, 0, this.values, 0, other.size);
            this.size = other.size;
            overwrite = false;
        } else {
            System.arraycopy(myKeys, 0, this.keys, 0, this.size);
            System.arraycopy(myVals, 0, this.values, 0, this.size);
            System.arraycopy(other.keys, 0, this.keys, this.size, other.size);
            System.arraycopy(other.values, 0, this.values, this.size, other.size);
        }
        for (int i = this.size; i < newSize; i++) {
            int index = indexOfKey(this.keys[i]);
            if (index < 0) {
                insertAt(index ^ (-1), this.keys[i], this.values[i]);
            } else if (overwrite) {
                this.keys[index] = this.keys[i];
                this.values[index] = this.values[i];
            }
        }
        Arrays.fill(this.keys, this.size, newSize, (Object) null);
        Arrays.fill(this.values, this.size, newSize, (Object) null);
    }

    private void ensureCapacity() {
        if (this.size >= this.threshold) {
            resize(this.threshold * 2);
        }
    }

    private void resize(final int newCapacity) {
        String[] oldKeys = this.keys;
        Object[] oldValues = this.values;
        this.keys = new String[newCapacity];
        this.values = new Object[newCapacity];
        System.arraycopy(oldKeys, 0, this.keys, 0, this.size);
        System.arraycopy(oldValues, 0, this.values, 0, this.size);
        this.threshold = newCapacity;
    }

    private void inflateTable(final int toSize) {
        this.threshold = toSize;
        this.keys = new String[toSize];
        this.values = new Object[toSize];
    }

    @Override // org.apache.logging.log4j.util.StringMap
    public void remove(final String key) {
        int index;
        if (this.keys != EMPTY && (index = indexOfKey(key)) >= 0) {
            assertNotFrozen();
            assertNoConcurrentModification();
            System.arraycopy(this.keys, index + 1, this.keys, index, (this.size - 1) - index);
            System.arraycopy(this.values, index + 1, this.values, index, (this.size - 1) - index);
            this.keys[this.size - 1] = null;
            this.values[this.size - 1] = null;
            this.size--;
        }
    }

    @Override // org.apache.logging.log4j.util.IndexedReadOnlyStringMap
    public String getKeyAt(final int index) {
        if (index < 0 || index >= this.size) {
            return null;
        }
        return this.keys[index];
    }

    @Override // org.apache.logging.log4j.util.IndexedReadOnlyStringMap
    public <V> V getValueAt(int i) {
        if (i < 0 || i >= this.size) {
            return null;
        }
        return (V) this.values[i];
    }

    @Override // org.apache.logging.log4j.util.ReadOnlyStringMap
    public int size() {
        return this.size;
    }

    @Override // org.apache.logging.log4j.util.ReadOnlyStringMap
    public <V> void forEach(final BiConsumer<String, ? super V> action) {
        this.iterating = true;
        for (int i = 0; i < this.size; i++) {
            try {
                action.accept(this.keys[i], this.values[i]);
            } finally {
                this.iterating = false;
            }
        }
    }

    @Override // org.apache.logging.log4j.util.ReadOnlyStringMap
    public <V, T> void forEach(final TriConsumer<String, ? super V, T> action, final T state) {
        this.iterating = true;
        for (int i = 0; i < this.size; i++) {
            try {
                action.accept(this.keys[i], this.values[i], state);
            } finally {
                this.iterating = false;
            }
        }
    }

    @Override // org.apache.logging.log4j.util.StringMap
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SortedArrayStringMap)) {
            return false;
        }
        SortedArrayStringMap other = (SortedArrayStringMap) obj;
        if (size() != other.size()) {
            return false;
        }
        for (int i = 0; i < size(); i++) {
            if (!Objects.equals(this.keys[i], other.keys[i]) || !Objects.equals(this.values[i], other.values[i])) {
                return false;
            }
        }
        return true;
    }

    @Override // org.apache.logging.log4j.util.StringMap
    public int hashCode() {
        int result = (31 * 37) + this.size;
        return (31 * ((31 * result) + hashCode(this.keys, this.size))) + hashCode(this.values, this.size);
    }

    private static int hashCode(final Object[] values, final int length) {
        int result = 1;
        for (int i = 0; i < length; i++) {
            result = (31 * result) + (values[i] == null ? 0 : values[i].hashCode());
        }
        return result;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(256);
        sb.append('{');
        for (int i = 0; i < this.size; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(this.keys[i]).append('=');
            sb.append(this.values[i] == this ? "(this map)" : this.values[i]);
        }
        sb.append('}');
        return sb.toString();
    }

    private void writeObject(final ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        if (this.keys == EMPTY) {
            s.writeInt(ceilingNextPowerOfTwo(this.threshold));
        } else {
            s.writeInt(this.keys.length);
        }
        s.writeInt(this.size);
        if (this.size > 0) {
            for (int i = 0; i < this.size; i++) {
                s.writeObject(this.keys[i]);
                try {
                    s.writeObject(marshall(this.values[i]));
                } catch (Exception e) {
                    handleSerializationException(e, i, this.keys[i]);
                    s.writeObject(null);
                }
            }
        }
    }

    private static byte[] marshall(final Object obj) throws IOException {
        if (obj == null) {
            return null;
        }
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bout);
        try {
            oos.writeObject(obj);
            oos.flush();
            byte[] byteArray = bout.toByteArray();
            oos.close();
            return byteArray;
        } catch (Throwable th) {
            try {
                oos.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    @SuppressFBWarnings(value = {"OBJECT_DESERIALIZATION"}, justification = "Object deserialization uses either Java 9 native filter or our custom filter to limit the kinds of classes deserialized.")
    private static Object unmarshall(final byte[] data, final ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        ObjectInputStream ois;
        ByteArrayInputStream bin = new ByteArrayInputStream(data);
        if (inputStream instanceof FilteredObjectInputStream) {
            Collection<String> allowedClasses = ((FilteredObjectInputStream) inputStream).getAllowedClasses();
            ois = new FilteredObjectInputStream(bin, allowedClasses);
        } else {
            try {
                Object obj = getObjectInputFilter.invoke(inputStream, new Object[0]);
                Object filter = newObjectInputFilter.invoke(null, obj);
                ois = new ObjectInputStream(bin);
                setObjectInputFilter.invoke(ois, filter);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new StreamCorruptedException("Unable to set ObjectInputFilter on stream");
            }
        }
        try {
            Object readObject = ois.readObject();
            ois.close();
            return readObject;
        } catch (Throwable th) {
            ois.close();
            throw th;
        }
    }

    private static int ceilingNextPowerOfTwo(final int x) {
        return 1 << (32 - Integer.numberOfLeadingZeros(x - 1));
    }

    private void readObject(final ObjectInputStream s) throws IOException, ClassNotFoundException {
        if (!(s instanceof FilteredObjectInputStream) && setObjectInputFilter == null) {
            throw new IllegalArgumentException("readObject requires a FilteredObjectInputStream or an ObjectInputStream that accepts an ObjectInputFilter");
        }
        s.defaultReadObject();
        this.keys = EMPTY;
        this.values = EMPTY;
        int capacity = s.readInt();
        if (capacity < 0) {
            throw new InvalidObjectException("Illegal capacity: " + capacity);
        }
        int mappings = s.readInt();
        if (mappings < 0) {
            throw new InvalidObjectException("Illegal mappings count: " + mappings);
        }
        if (mappings > 0) {
            inflateTable(capacity);
        } else {
            this.threshold = capacity;
        }
        for (int i = 0; i < mappings; i++) {
            this.keys[i] = (String) s.readObject();
            try {
                byte[] marshalledObject = (byte[]) s.readObject();
                this.values[i] = marshalledObject == null ? null : unmarshall(marshalledObject, s);
            } catch (Exception | LinkageError error) {
                handleSerializationException(error, i, this.keys[i]);
                this.values[i] = null;
            }
        }
        this.size = mappings;
    }

    private void handleSerializationException(final Throwable t, final int i, final String key) {
        StatusLogger.getLogger().warn("Ignoring {} for key[{}] ('{}')", String.valueOf(t), Integer.valueOf(i), this.keys[i]);
    }
}
