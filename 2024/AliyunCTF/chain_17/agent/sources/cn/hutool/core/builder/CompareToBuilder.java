package cn.hutool.core.builder;

import cn.hutool.core.util.ArrayUtil;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Comparator;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/builder/CompareToBuilder.class */
public class CompareToBuilder implements Builder<Integer> {
    private static final long serialVersionUID = 1;
    private int comparison = 0;

    public static int reflectionCompare(Object lhs, Object rhs) {
        return reflectionCompare(lhs, rhs, false, null, new String[0]);
    }

    public static int reflectionCompare(Object lhs, Object rhs, boolean compareTransients) {
        return reflectionCompare(lhs, rhs, compareTransients, null, new String[0]);
    }

    public static int reflectionCompare(Object lhs, Object rhs, Collection<String> excludeFields) {
        return reflectionCompare(lhs, rhs, (String[]) ArrayUtil.toArray((Collection) excludeFields, String.class));
    }

    public static int reflectionCompare(Object lhs, Object rhs, String... excludeFields) {
        return reflectionCompare(lhs, rhs, false, null, excludeFields);
    }

    public static int reflectionCompare(Object lhs, Object rhs, boolean compareTransients, Class<?> reflectUpToClass, String... excludeFields) {
        if (lhs == rhs) {
            return 0;
        }
        if (lhs == null || rhs == null) {
            throw new NullPointerException();
        }
        Class<?> lhsClazz = lhs.getClass();
        if (!lhsClazz.isInstance(rhs)) {
            throw new ClassCastException();
        }
        CompareToBuilder compareToBuilder = new CompareToBuilder();
        reflectionAppend(lhs, rhs, lhsClazz, compareToBuilder, compareTransients, excludeFields);
        while (lhsClazz.getSuperclass() != null && lhsClazz != reflectUpToClass) {
            lhsClazz = lhsClazz.getSuperclass();
            reflectionAppend(lhs, rhs, lhsClazz, compareToBuilder, compareTransients, excludeFields);
        }
        return compareToBuilder.toComparison();
    }

    private static void reflectionAppend(Object lhs, Object rhs, Class<?> clazz, CompareToBuilder builder, boolean useTransients, String[] excludeFields) {
        Field[] fields = clazz.getDeclaredFields();
        AccessibleObject.setAccessible(fields, true);
        for (int i = 0; i < fields.length && builder.comparison == 0; i++) {
            Field f = fields[i];
            if (false == ArrayUtil.contains(excludeFields, f.getName()) && f.getName().indexOf(36) == -1 && ((useTransients || !Modifier.isTransient(f.getModifiers())) && !Modifier.isStatic(f.getModifiers()))) {
                try {
                    builder.append(f.get(lhs), f.get(rhs));
                } catch (IllegalAccessException e) {
                    throw new InternalError("Unexpected IllegalAccessException");
                }
            }
        }
    }

    public CompareToBuilder appendSuper(int superCompareTo) {
        if (this.comparison != 0) {
            return this;
        }
        this.comparison = superCompareTo;
        return this;
    }

    public CompareToBuilder append(Object lhs, Object rhs) {
        return append(lhs, rhs, (Comparator<?>) null);
    }

    public CompareToBuilder append(Object lhs, Object rhs, Comparator<?> comparator) {
        if (this.comparison != 0) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null) {
            this.comparison = -1;
            return this;
        }
        if (rhs == null) {
            this.comparison = 1;
            return this;
        }
        if (lhs.getClass().isArray()) {
            if (lhs instanceof long[]) {
                append((long[]) lhs, (long[]) rhs);
            } else if (lhs instanceof int[]) {
                append((int[]) lhs, (int[]) rhs);
            } else if (lhs instanceof short[]) {
                append((short[]) lhs, (short[]) rhs);
            } else if (lhs instanceof char[]) {
                append((char[]) lhs, (char[]) rhs);
            } else if (lhs instanceof byte[]) {
                append((byte[]) lhs, (byte[]) rhs);
            } else if (lhs instanceof double[]) {
                append((double[]) lhs, (double[]) rhs);
            } else if (lhs instanceof float[]) {
                append((float[]) lhs, (float[]) rhs);
            } else if (lhs instanceof boolean[]) {
                append((boolean[]) lhs, (boolean[]) rhs);
            } else {
                append((Object[]) lhs, (Object[]) rhs, comparator);
            }
        } else if (comparator == null) {
            Comparable<Object> comparable = (Comparable) lhs;
            this.comparison = comparable.compareTo(rhs);
        } else {
            this.comparison = comparator.compare(lhs, rhs);
        }
        return this;
    }

    public CompareToBuilder append(long lhs, long rhs) {
        if (this.comparison != 0) {
            return this;
        }
        this.comparison = Long.compare(lhs, rhs);
        return this;
    }

    public CompareToBuilder append(int lhs, int rhs) {
        if (this.comparison != 0) {
            return this;
        }
        this.comparison = Integer.compare(lhs, rhs);
        return this;
    }

    public CompareToBuilder append(short lhs, short rhs) {
        if (this.comparison != 0) {
            return this;
        }
        this.comparison = Short.compare(lhs, rhs);
        return this;
    }

    public CompareToBuilder append(char lhs, char rhs) {
        if (this.comparison != 0) {
            return this;
        }
        this.comparison = Character.compare(lhs, rhs);
        return this;
    }

    public CompareToBuilder append(byte lhs, byte rhs) {
        if (this.comparison != 0) {
            return this;
        }
        this.comparison = Byte.compare(lhs, rhs);
        return this;
    }

    public CompareToBuilder append(double lhs, double rhs) {
        if (this.comparison != 0) {
            return this;
        }
        this.comparison = Double.compare(lhs, rhs);
        return this;
    }

    public CompareToBuilder append(float lhs, float rhs) {
        if (this.comparison != 0) {
            return this;
        }
        this.comparison = Float.compare(lhs, rhs);
        return this;
    }

    public CompareToBuilder append(boolean lhs, boolean rhs) {
        if (this.comparison != 0) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (!lhs) {
            this.comparison = -1;
        } else {
            this.comparison = 1;
        }
        return this;
    }

    public CompareToBuilder append(Object[] lhs, Object[] rhs) {
        return append(lhs, rhs, (Comparator<?>) null);
    }

    public CompareToBuilder append(Object[] lhs, Object[] rhs, Comparator<?> comparator) {
        if (this.comparison != 0) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null) {
            this.comparison = -1;
            return this;
        }
        if (rhs == null) {
            this.comparison = 1;
            return this;
        }
        if (lhs.length != rhs.length) {
            this.comparison = lhs.length < rhs.length ? -1 : 1;
            return this;
        }
        for (int i = 0; i < lhs.length && this.comparison == 0; i++) {
            append(lhs[i], rhs[i], comparator);
        }
        return this;
    }

    public CompareToBuilder append(long[] lhs, long[] rhs) {
        if (this.comparison != 0) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null) {
            this.comparison = -1;
            return this;
        }
        if (rhs == null) {
            this.comparison = 1;
            return this;
        }
        if (lhs.length != rhs.length) {
            this.comparison = lhs.length < rhs.length ? -1 : 1;
            return this;
        }
        for (int i = 0; i < lhs.length && this.comparison == 0; i++) {
            append(lhs[i], rhs[i]);
        }
        return this;
    }

    public CompareToBuilder append(int[] lhs, int[] rhs) {
        if (this.comparison != 0) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null) {
            this.comparison = -1;
            return this;
        }
        if (rhs == null) {
            this.comparison = 1;
            return this;
        }
        if (lhs.length != rhs.length) {
            this.comparison = lhs.length < rhs.length ? -1 : 1;
            return this;
        }
        for (int i = 0; i < lhs.length && this.comparison == 0; i++) {
            append(lhs[i], rhs[i]);
        }
        return this;
    }

    public CompareToBuilder append(short[] lhs, short[] rhs) {
        if (this.comparison != 0) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null) {
            this.comparison = -1;
            return this;
        }
        if (rhs == null) {
            this.comparison = 1;
            return this;
        }
        if (lhs.length != rhs.length) {
            this.comparison = lhs.length < rhs.length ? -1 : 1;
            return this;
        }
        for (int i = 0; i < lhs.length && this.comparison == 0; i++) {
            append(lhs[i], rhs[i]);
        }
        return this;
    }

    public CompareToBuilder append(char[] lhs, char[] rhs) {
        if (this.comparison != 0) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null) {
            this.comparison = -1;
            return this;
        }
        if (rhs == null) {
            this.comparison = 1;
            return this;
        }
        if (lhs.length != rhs.length) {
            this.comparison = lhs.length < rhs.length ? -1 : 1;
            return this;
        }
        for (int i = 0; i < lhs.length && this.comparison == 0; i++) {
            append(lhs[i], rhs[i]);
        }
        return this;
    }

    public CompareToBuilder append(byte[] lhs, byte[] rhs) {
        if (this.comparison != 0) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null) {
            this.comparison = -1;
            return this;
        }
        if (rhs == null) {
            this.comparison = 1;
            return this;
        }
        if (lhs.length != rhs.length) {
            this.comparison = lhs.length < rhs.length ? -1 : 1;
            return this;
        }
        for (int i = 0; i < lhs.length && this.comparison == 0; i++) {
            append(lhs[i], rhs[i]);
        }
        return this;
    }

    public CompareToBuilder append(double[] lhs, double[] rhs) {
        if (this.comparison != 0) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null) {
            this.comparison = -1;
            return this;
        }
        if (rhs == null) {
            this.comparison = 1;
            return this;
        }
        if (lhs.length != rhs.length) {
            this.comparison = lhs.length < rhs.length ? -1 : 1;
            return this;
        }
        for (int i = 0; i < lhs.length && this.comparison == 0; i++) {
            append(lhs[i], rhs[i]);
        }
        return this;
    }

    public CompareToBuilder append(float[] lhs, float[] rhs) {
        if (this.comparison != 0) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null) {
            this.comparison = -1;
            return this;
        }
        if (rhs == null) {
            this.comparison = 1;
            return this;
        }
        if (lhs.length != rhs.length) {
            this.comparison = lhs.length < rhs.length ? -1 : 1;
            return this;
        }
        for (int i = 0; i < lhs.length && this.comparison == 0; i++) {
            append(lhs[i], rhs[i]);
        }
        return this;
    }

    public CompareToBuilder append(boolean[] lhs, boolean[] rhs) {
        if (this.comparison != 0) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null) {
            this.comparison = -1;
            return this;
        }
        if (rhs == null) {
            this.comparison = 1;
            return this;
        }
        if (lhs.length != rhs.length) {
            this.comparison = lhs.length < rhs.length ? -1 : 1;
            return this;
        }
        for (int i = 0; i < lhs.length && this.comparison == 0; i++) {
            append(lhs[i], rhs[i]);
        }
        return this;
    }

    public int toComparison() {
        return this.comparison;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.core.builder.Builder
    public Integer build() {
        return Integer.valueOf(toComparison());
    }
}
