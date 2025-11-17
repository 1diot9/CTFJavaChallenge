package org.h2.util;

import ch.qos.logback.core.util.FileSize;
import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/Utils.class */
public class Utils {
    public static final byte[] EMPTY_BYTES = new byte[0];
    public static final int[] EMPTY_INT_ARRAY = new int[0];
    private static final HashMap<String, byte[]> RESOURCES = new HashMap<>();

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/Utils$ClassFactory.class */
    public interface ClassFactory {
        boolean match(String str);

        Class<?> loadClass(String str) throws ClassNotFoundException;
    }

    private Utils() {
    }

    /* JADX WARN: Code restructure failed: missing block: B:19:0x0042, code lost:            r6 = r6 + 1;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static int indexOf(byte[] r4, byte[] r5, int r6) {
        /*
            r0 = r5
            int r0 = r0.length
            if (r0 != 0) goto L7
            r0 = r6
            return r0
        L7:
            r0 = r6
            r1 = r4
            int r1 = r1.length
            if (r0 <= r1) goto Lf
            r0 = -1
            return r0
        Lf:
            r0 = r4
            int r0 = r0.length
            r1 = r5
            int r1 = r1.length
            int r0 = r0 - r1
            r1 = 1
            int r0 = r0 + r1
            r7 = r0
            r0 = r5
            int r0 = r0.length
            r8 = r0
        L1b:
            r0 = r6
            r1 = r7
            if (r0 >= r1) goto L48
            r0 = 0
            r9 = r0
        L23:
            r0 = r9
            r1 = r8
            if (r0 >= r1) goto L40
            r0 = r4
            r1 = r6
            r2 = r9
            int r1 = r1 + r2
            r0 = r0[r1]
            r1 = r5
            r2 = r9
            r1 = r1[r2]
            if (r0 == r1) goto L3a
            goto L42
        L3a:
            int r9 = r9 + 1
            goto L23
        L40:
            r0 = r6
            return r0
        L42:
            int r6 = r6 + 1
            goto L1b
        L48:
            r0 = -1
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.util.Utils.indexOf(byte[], byte[], int):int");
    }

    public static int getByteArrayHash(byte[] bArr) {
        int length = bArr.length;
        int i = length;
        if (length < 50) {
            for (byte b : bArr) {
                i = (31 * i) + b;
            }
        } else {
            int i2 = length / 16;
            for (int i3 = 0; i3 < 4; i3++) {
                length--;
                i = (31 * ((31 * i) + bArr[i3])) + bArr[length];
            }
            int i4 = 4;
            while (true) {
                int i5 = i4 + i2;
                if (i5 >= length) {
                    break;
                }
                i = (31 * i) + bArr[i5];
                i4 = i5;
            }
        }
        return i;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static boolean compareSecure(byte[] bArr, byte[] bArr2) {
        if (bArr == null || bArr2 == null) {
            return bArr == null && bArr2 == null;
        }
        int length = bArr.length;
        if (length != bArr2.length) {
            return false;
        }
        if (length == 0) {
            return true;
        }
        byte b = false;
        for (int i = 0; i < length; i++) {
            b = ((b == true ? 1 : 0) | (bArr[i] ^ bArr2[i])) == true ? 1 : 0;
        }
        return b == false;
    }

    public static byte[] copy(byte[] bArr, byte[] bArr2) {
        int length = bArr.length;
        if (length > bArr2.length) {
            bArr2 = new byte[length];
        }
        System.arraycopy(bArr, 0, bArr2, 0, length);
        return bArr2;
    }

    public static byte[] newBytes(int i) {
        if (i == 0) {
            return EMPTY_BYTES;
        }
        try {
            return new byte[i];
        } catch (OutOfMemoryError e) {
            OutOfMemoryError outOfMemoryError = new OutOfMemoryError("Requested memory: " + i);
            outOfMemoryError.initCause(e);
            throw outOfMemoryError;
        }
    }

    public static byte[] copyBytes(byte[] bArr, int i) {
        if (i == 0) {
            return EMPTY_BYTES;
        }
        try {
            return Arrays.copyOf(bArr, i);
        } catch (OutOfMemoryError e) {
            OutOfMemoryError outOfMemoryError = new OutOfMemoryError("Requested memory: " + i);
            outOfMemoryError.initCause(e);
            throw outOfMemoryError;
        }
    }

    public static byte[] cloneByteArray(byte[] bArr) {
        if (bArr == null) {
            return null;
        }
        int length = bArr.length;
        if (length == 0) {
            return EMPTY_BYTES;
        }
        return Arrays.copyOf(bArr, length);
    }

    public static long getMemoryUsed() {
        collectGarbage();
        Runtime runtime = Runtime.getRuntime();
        return (runtime.totalMemory() - runtime.freeMemory()) >> 10;
    }

    public static long getMemoryFree() {
        collectGarbage();
        return Runtime.getRuntime().freeMemory() >> 10;
    }

    public static long getMemoryMax() {
        return Runtime.getRuntime().maxMemory() >> 10;
    }

    public static long getGarbageCollectionTime() {
        long j = 0;
        Iterator it = ManagementFactory.getGarbageCollectorMXBeans().iterator();
        while (it.hasNext()) {
            long collectionTime = ((GarbageCollectorMXBean) it.next()).getCollectionTime();
            if (collectionTime > 0) {
                j += collectionTime;
            }
        }
        return j;
    }

    public static long getGarbageCollectionCount() {
        long j = 0;
        int i = 0;
        for (GarbageCollectorMXBean garbageCollectorMXBean : ManagementFactory.getGarbageCollectorMXBeans()) {
            long collectionTime = garbageCollectorMXBean.getCollectionTime();
            if (collectionTime > 0) {
                j += collectionTime;
                i += garbageCollectorMXBean.getMemoryPoolNames().length;
            }
        }
        return (j + (r0 >> 1)) / Math.max(i, 1);
    }

    public static synchronized void collectGarbage() {
        Runtime runtime = Runtime.getRuntime();
        long garbageCollectionCount = getGarbageCollectionCount();
        while (garbageCollectionCount == getGarbageCollectionCount()) {
            runtime.gc();
            Thread.yield();
        }
    }

    public static <T> ArrayList<T> newSmallArrayList() {
        return new ArrayList<>(4);
    }

    public static <X> void sortTopN(X[] xArr, int i, int i2, Comparator<? super X> comparator) {
        int length = xArr.length - 1;
        if (length > 0 && i2 > i) {
            partialQuickSort(xArr, 0, length, comparator, i, i2 - 1);
            Arrays.sort(xArr, i, i2, comparator);
        }
    }

    private static <X> void partialQuickSort(X[] xArr, int i, int i2, Comparator<? super X> comparator, int i3, int i4) {
        if (i >= i3 && i2 <= i4) {
            return;
        }
        int i5 = i;
        int i6 = i2;
        int randomInt = i + MathUtils.randomInt(i2 - i);
        X x = xArr[randomInt];
        int i7 = (i + i2) >>> 1;
        X x2 = xArr[i7];
        xArr[i7] = x;
        xArr[randomInt] = x2;
        while (i5 <= i6) {
            while (comparator.compare(xArr[i5], x) < 0) {
                i5++;
            }
            while (comparator.compare(xArr[i6], x) > 0) {
                i6--;
            }
            if (i5 <= i6) {
                X x3 = xArr[i5];
                int i8 = i5;
                i5++;
                xArr[i8] = xArr[i6];
                int i9 = i6;
                i6--;
                xArr[i9] = x3;
            }
        }
        if (i < i6 && i3 <= i6) {
            partialQuickSort(xArr, i, i6, comparator, i3, i4);
        }
        if (i5 < i2 && i5 <= i4) {
            partialQuickSort(xArr, i5, i2, comparator, i3, i4);
        }
    }

    public static byte[] getResource(String str) throws IOException {
        byte[] bArr = RESOURCES.get(str);
        if (bArr == null) {
            bArr = loadResource(str);
            if (bArr != null) {
                RESOURCES.put(str, bArr);
            }
        }
        return bArr;
    }

    /* JADX WARN: Code restructure failed: missing block: B:21:0x0068, code lost:            r0 = new java.io.ByteArrayOutputStream();        org.h2.util.IOUtils.copy(r0, r0);        r0.closeEntry();        r0 = r0.toByteArray();     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x0084, code lost:            if (r0 == null) goto L27;     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x0088, code lost:            if (0 == 0) goto L26;     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x009d, code lost:            r0.close();     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x008b, code lost:            r0.close();     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x0092, code lost:            r12 = move-exception;     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x0094, code lost:            r7.addSuppressed(r12);     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x00ac, code lost:            if (r0 == null) goto L52;     */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x00b0, code lost:            if (0 == 0) goto L37;     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x00c5, code lost:            r0.close();     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x00b3, code lost:            r0.close();     */
    /* JADX WARN: Code restructure failed: missing block: B:44:0x00ba, code lost:            r8 = move-exception;     */
    /* JADX WARN: Code restructure failed: missing block: B:45:0x00bc, code lost:            r7.addSuppressed(r8);     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static byte[] loadResource(java.lang.String r4) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 257
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.util.Utils.loadResource(java.lang.String):byte[]");
    }

    public static Object callStaticMethod(String str, Object... objArr) throws Exception {
        int lastIndexOf = str.lastIndexOf(46);
        String substring = str.substring(0, lastIndexOf);
        return callMethod(null, Class.forName(substring), str.substring(lastIndexOf + 1), objArr);
    }

    public static Object callMethod(Object obj, String str, Object... objArr) throws Exception {
        return callMethod(obj, obj.getClass(), str, objArr);
    }

    private static Object callMethod(Object obj, Class<?> cls, String str, Object... objArr) throws Exception {
        int match;
        Method method = null;
        int i = 0;
        boolean z = obj == null;
        for (Method method2 : cls.getMethods()) {
            if (Modifier.isStatic(method2.getModifiers()) == z && method2.getName().equals(str) && (match = match(method2.getParameterTypes(), objArr)) > i) {
                i = match;
                method = method2;
            }
        }
        if (method == null) {
            throw new NoSuchMethodException(str);
        }
        return method.invoke(obj, objArr);
    }

    public static Object newInstance(String str, Object... objArr) throws Exception {
        Constructor<?> constructor = null;
        int i = 0;
        for (Constructor<?> constructor2 : Class.forName(str).getConstructors()) {
            int match = match(constructor2.getParameterTypes(), objArr);
            if (match > i) {
                i = match;
                constructor = constructor2;
            }
        }
        if (constructor == null) {
            throw new NoSuchMethodException(str);
        }
        return constructor.newInstance(objArr);
    }

    private static int match(Class<?>[] clsArr, Object[] objArr) {
        int length = clsArr.length;
        if (length == objArr.length) {
            int i = 1;
            for (int i2 = 0; i2 < length; i2++) {
                Class<?> nonPrimitiveClass = getNonPrimitiveClass(clsArr[i2]);
                Object obj = objArr[i2];
                Class<?> cls = obj == null ? null : obj.getClass();
                if (nonPrimitiveClass == cls) {
                    i++;
                } else if (cls != null && !nonPrimitiveClass.isAssignableFrom(cls)) {
                    return 0;
                }
            }
            return i;
        }
        return 0;
    }

    public static Class<?> getNonPrimitiveClass(Class<?> cls) {
        if (!cls.isPrimitive()) {
            return cls;
        }
        if (cls == Boolean.TYPE) {
            return Boolean.class;
        }
        if (cls == Byte.TYPE) {
            return Byte.class;
        }
        if (cls == Character.TYPE) {
            return Character.class;
        }
        if (cls == Double.TYPE) {
            return Double.class;
        }
        if (cls == Float.TYPE) {
            return Float.class;
        }
        if (cls == Integer.TYPE) {
            return Integer.class;
        }
        if (cls == Long.TYPE) {
            return Long.class;
        }
        if (cls == Short.TYPE) {
            return Short.class;
        }
        if (cls == Void.TYPE) {
            return Void.class;
        }
        return cls;
    }

    public static boolean parseBoolean(String str, boolean z, boolean z2) {
        if (str == null) {
            return z;
        }
        switch (str.length()) {
            case 1:
                if (str.equals(CustomBooleanEditor.VALUE_1) || str.equalsIgnoreCase("t") || str.equalsIgnoreCase("y")) {
                    return true;
                }
                if (str.equals(CustomBooleanEditor.VALUE_0) || str.equalsIgnoreCase("f") || str.equalsIgnoreCase("n")) {
                    return false;
                }
                break;
            case 2:
                if (str.equalsIgnoreCase("no")) {
                    return false;
                }
                break;
            case 3:
                if (str.equalsIgnoreCase(CustomBooleanEditor.VALUE_YES)) {
                    return true;
                }
                break;
            case 4:
                if (str.equalsIgnoreCase("true")) {
                    return true;
                }
                break;
            case 5:
                if (str.equalsIgnoreCase("false")) {
                    return false;
                }
                break;
        }
        if (z2) {
            throw new IllegalArgumentException(str);
        }
        return z;
    }

    public static String getProperty(String str, String str2) {
        try {
            return System.getProperty(str, str2);
        } catch (SecurityException e) {
            return str2;
        }
    }

    public static int getProperty(String str, int i) {
        String property = getProperty(str, (String) null);
        if (property != null) {
            try {
                return Integer.decode(property).intValue();
            } catch (NumberFormatException e) {
            }
        }
        return i;
    }

    public static boolean getProperty(String str, boolean z) {
        return parseBoolean(getProperty(str, (String) null), z, false);
    }

    public static int scaleForAvailableMemory(int i) {
        long maxMemory = Runtime.getRuntime().maxMemory();
        if (maxMemory != Long.MAX_VALUE) {
            return (int) ((i * maxMemory) / FileSize.GB_COEFFICIENT);
        }
        try {
            return (int) ((i * ((Number) Class.forName("com.sun.management.OperatingSystemMXBean").getMethod("getTotalPhysicalMemorySize", new Class[0]).invoke(ManagementFactory.getOperatingSystemMXBean(), new Object[0])).longValue()) / FileSize.GB_COEFFICIENT);
        } catch (Error | Exception e) {
            return i;
        }
    }

    public static long currentNanoTime() {
        long nanoTime = System.nanoTime();
        if (nanoTime == 0) {
            nanoTime = 1;
        }
        return nanoTime;
    }

    public static long currentNanoTimePlusMillis(int i) {
        return nanoTimePlusMillis(System.nanoTime(), i);
    }

    public static long nanoTimePlusMillis(long j, int i) {
        long j2 = j + (i * 1000000);
        if (j2 == 0) {
            j2 = 1;
        }
        return j2;
    }

    public static ThreadPoolExecutor createSingleThreadExecutor(String str) {
        return createSingleThreadExecutor(str, new LinkedBlockingQueue());
    }

    public static ThreadPoolExecutor createSingleThreadExecutor(String str, BlockingQueue<Runnable> blockingQueue) {
        return new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, blockingQueue, runnable -> {
            Thread thread = new Thread(runnable, str);
            thread.setDaemon(true);
            return thread;
        });
    }

    public static void flushExecutor(ThreadPoolExecutor threadPoolExecutor) {
        if (threadPoolExecutor != null) {
            try {
                threadPoolExecutor.submit(() -> {
                }).get();
            } catch (InterruptedException e) {
            } catch (ExecutionException e2) {
                throw new RuntimeException(e2);
            } catch (RejectedExecutionException e3) {
                shutdownExecutor(threadPoolExecutor);
            }
        }
    }

    public static void shutdownExecutor(ThreadPoolExecutor threadPoolExecutor) {
        if (threadPoolExecutor != null) {
            threadPoolExecutor.shutdown();
            try {
                threadPoolExecutor.awaitTermination(1L, TimeUnit.DAYS);
            } catch (InterruptedException e) {
            }
        }
    }
}
