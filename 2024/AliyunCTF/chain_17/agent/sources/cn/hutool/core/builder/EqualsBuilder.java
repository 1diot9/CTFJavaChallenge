package cn.hutool.core.builder;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ArrayUtil;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/builder/EqualsBuilder.class */
public class EqualsBuilder implements Builder<Boolean> {
    private static final long serialVersionUID = 1;
    private static final ThreadLocal<Set<Pair<IDKey, IDKey>>> REGISTRY = new ThreadLocal<>();
    private boolean isEquals = true;

    static Set<Pair<IDKey, IDKey>> getRegistry() {
        return REGISTRY.get();
    }

    static Pair<IDKey, IDKey> getRegisterPair(Object lhs, Object rhs) {
        IDKey left = new IDKey(lhs);
        IDKey right = new IDKey(rhs);
        return new Pair<>(left, right);
    }

    static boolean isRegistered(Object lhs, Object rhs) {
        Set<Pair<IDKey, IDKey>> registry = getRegistry();
        Pair<IDKey, IDKey> pair = getRegisterPair(lhs, rhs);
        Pair<IDKey, IDKey> swappedPair = new Pair<>(pair.getKey(), pair.getValue());
        return registry != null && (registry.contains(pair) || registry.contains(swappedPair));
    }

    static void register(Object lhs, Object rhs) {
        synchronized (EqualsBuilder.class) {
            if (getRegistry() == null) {
                REGISTRY.set(new HashSet());
            }
        }
        Set<Pair<IDKey, IDKey>> registry = getRegistry();
        Pair<IDKey, IDKey> pair = getRegisterPair(lhs, rhs);
        registry.add(pair);
    }

    static void unregister(Object lhs, Object rhs) {
        Set<Pair<IDKey, IDKey>> registry = getRegistry();
        if (registry != null) {
            Pair<IDKey, IDKey> pair = getRegisterPair(lhs, rhs);
            registry.remove(pair);
            synchronized (EqualsBuilder.class) {
                Set<Pair<IDKey, IDKey>> registry2 = getRegistry();
                if (registry2 != null && registry2.isEmpty()) {
                    REGISTRY.remove();
                }
            }
        }
    }

    public static boolean reflectionEquals(Object lhs, Object rhs, Collection<String> excludeFields) {
        return reflectionEquals(lhs, rhs, (String[]) ArrayUtil.toArray((Collection) excludeFields, String.class));
    }

    public static boolean reflectionEquals(Object lhs, Object rhs, String... excludeFields) {
        return reflectionEquals(lhs, rhs, false, null, excludeFields);
    }

    public static boolean reflectionEquals(Object lhs, Object rhs, boolean testTransients) {
        return reflectionEquals(lhs, rhs, testTransients, null, new String[0]);
    }

    public static boolean reflectionEquals(Object lhs, Object rhs, boolean testTransients, Class<?> reflectUpToClass, String... excludeFields) {
        Class<?> testClass;
        if (lhs == rhs) {
            return true;
        }
        if (lhs == null || rhs == null) {
            return false;
        }
        Class<?> lhsClass = lhs.getClass();
        Class<?> rhsClass = rhs.getClass();
        if (lhsClass.isInstance(rhs)) {
            testClass = lhsClass;
            if (!rhsClass.isInstance(lhs)) {
                testClass = rhsClass;
            }
        } else if (rhsClass.isInstance(lhs)) {
            testClass = rhsClass;
            if (!lhsClass.isInstance(rhs)) {
                testClass = lhsClass;
            }
        } else {
            return false;
        }
        EqualsBuilder equalsBuilder = new EqualsBuilder();
        try {
            if (testClass.isArray()) {
                equalsBuilder.append(lhs, rhs);
            } else {
                reflectionAppend(lhs, rhs, testClass, equalsBuilder, testTransients, excludeFields);
                while (testClass.getSuperclass() != null && testClass != reflectUpToClass) {
                    testClass = testClass.getSuperclass();
                    reflectionAppend(lhs, rhs, testClass, equalsBuilder, testTransients, excludeFields);
                }
            }
            return equalsBuilder.isEquals();
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static void reflectionAppend(Object lhs, Object rhs, Class<?> clazz, EqualsBuilder builder, boolean useTransients, String[] excludeFields) {
        if (isRegistered(lhs, rhs)) {
            return;
        }
        try {
            register(lhs, rhs);
            Field[] fields = clazz.getDeclaredFields();
            AccessibleObject.setAccessible(fields, true);
            for (int i = 0; i < fields.length && builder.isEquals; i++) {
                Field f = fields[i];
                if (false == ArrayUtil.contains(excludeFields, f.getName()) && f.getName().indexOf(36) == -1 && ((useTransients || !Modifier.isTransient(f.getModifiers())) && !Modifier.isStatic(f.getModifiers()))) {
                    try {
                        builder.append(f.get(lhs), f.get(rhs));
                    } catch (IllegalAccessException e) {
                        throw new InternalError("Unexpected IllegalAccessException");
                    }
                }
            }
        } finally {
            unregister(lhs, rhs);
        }
    }

    public EqualsBuilder appendSuper(boolean superEquals) {
        if (!this.isEquals) {
            return this;
        }
        this.isEquals = superEquals;
        return this;
    }

    public EqualsBuilder append(Object lhs, Object rhs) {
        if (!this.isEquals) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            return setEquals(false);
        }
        if (ArrayUtil.isArray(lhs)) {
            return setEquals(ArrayUtil.equals(lhs, rhs));
        }
        return setEquals(lhs.equals(rhs));
    }

    public EqualsBuilder append(long lhs, long rhs) {
        if (!this.isEquals) {
            return this;
        }
        this.isEquals = lhs == rhs;
        return this;
    }

    public EqualsBuilder append(int lhs, int rhs) {
        if (!this.isEquals) {
            return this;
        }
        this.isEquals = lhs == rhs;
        return this;
    }

    public EqualsBuilder append(short lhs, short rhs) {
        if (!this.isEquals) {
            return this;
        }
        this.isEquals = lhs == rhs;
        return this;
    }

    public EqualsBuilder append(char lhs, char rhs) {
        if (!this.isEquals) {
            return this;
        }
        this.isEquals = lhs == rhs;
        return this;
    }

    public EqualsBuilder append(byte lhs, byte rhs) {
        if (!this.isEquals) {
            return this;
        }
        this.isEquals = lhs == rhs;
        return this;
    }

    public EqualsBuilder append(double lhs, double rhs) {
        if (!this.isEquals) {
            return this;
        }
        return append(Double.doubleToLongBits(lhs), Double.doubleToLongBits(rhs));
    }

    public EqualsBuilder append(float lhs, float rhs) {
        if (!this.isEquals) {
            return this;
        }
        return append(Float.floatToIntBits(lhs), Float.floatToIntBits(rhs));
    }

    public EqualsBuilder append(boolean lhs, boolean rhs) {
        if (!this.isEquals) {
            return this;
        }
        this.isEquals = lhs == rhs;
        return this;
    }

    public boolean isEquals() {
        return this.isEquals;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.core.builder.Builder
    public Boolean build() {
        return Boolean.valueOf(isEquals());
    }

    protected EqualsBuilder setEquals(boolean isEquals) {
        this.isEquals = isEquals;
        return this;
    }

    public void reset() {
        this.isEquals = true;
    }
}
