package cn.hutool.core.exceptions;

import cn.hutool.core.io.FastByteArrayOutputStream;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/exceptions/ExceptionUtil.class */
public class ExceptionUtil {
    public static String getMessage(Throwable e) {
        if (null == e) {
            return "null";
        }
        return StrUtil.format("{}: {}", e.getClass().getSimpleName(), e.getMessage());
    }

    public static String getSimpleMessage(Throwable e) {
        return null == e ? "null" : e.getMessage();
    }

    public static RuntimeException wrapRuntime(Throwable throwable) {
        if (throwable instanceof RuntimeException) {
            return (RuntimeException) throwable;
        }
        return new RuntimeException(throwable);
    }

    public static RuntimeException wrapRuntime(String message) {
        return new RuntimeException(message);
    }

    public static <T extends Throwable> T wrap(Throwable th, Class<T> wrapThrowable) {
        if (wrapThrowable.isInstance(th)) {
            return th;
        }
        return (T) ReflectUtil.newInstance(wrapThrowable, th);
    }

    public static void wrapAndThrow(Throwable throwable) {
        if (throwable instanceof RuntimeException) {
            throw ((RuntimeException) throwable);
        }
        if (throwable instanceof Error) {
            throw ((Error) throwable);
        }
        throw new UndeclaredThrowableException(throwable);
    }

    public static void wrapRuntimeAndThrow(String message) {
        throw new RuntimeException(message);
    }

    public static Throwable unwrap(Throwable wrapped) {
        Throwable th = wrapped;
        while (true) {
            Throwable unwrapped = th;
            if (unwrapped instanceof InvocationTargetException) {
                th = ((InvocationTargetException) unwrapped).getTargetException();
            } else if (unwrapped instanceof UndeclaredThrowableException) {
                th = ((UndeclaredThrowableException) unwrapped).getUndeclaredThrowable();
            } else {
                return unwrapped;
            }
        }
    }

    public static StackTraceElement[] getStackElements() {
        return Thread.currentThread().getStackTrace();
    }

    public static StackTraceElement getStackElement(int i) {
        return Thread.currentThread().getStackTrace()[i];
    }

    public static StackTraceElement getStackElement(String fqcn, int i) {
        StackTraceElement[] stackTraceArray = Thread.currentThread().getStackTrace();
        int index = ArrayUtil.matchIndex(ele -> {
            return StrUtil.equals(fqcn, ele.getClassName());
        }, stackTraceArray);
        if (index > 0) {
            return stackTraceArray[index + i];
        }
        return null;
    }

    public static StackTraceElement getRootStackElement() {
        StackTraceElement[] stackElements = Thread.currentThread().getStackTrace();
        return Thread.currentThread().getStackTrace()[stackElements.length - 1];
    }

    public static String stacktraceToOneLineString(Throwable throwable) {
        return stacktraceToOneLineString(throwable, 3000);
    }

    public static String stacktraceToOneLineString(Throwable throwable, int limit) {
        Map<Character, String> replaceCharToStrMap = new HashMap<>();
        replaceCharToStrMap.put('\r', CharSequenceUtil.SPACE);
        replaceCharToStrMap.put('\n', CharSequenceUtil.SPACE);
        replaceCharToStrMap.put('\t', CharSequenceUtil.SPACE);
        return stacktraceToString(throwable, limit, replaceCharToStrMap);
    }

    public static String stacktraceToString(Throwable throwable) {
        return stacktraceToString(throwable, 3000);
    }

    public static String stacktraceToString(Throwable throwable, int limit) {
        return stacktraceToString(throwable, limit, null);
    }

    public static String stacktraceToString(Throwable throwable, int limit, Map<Character, String> replaceCharToStrMap) {
        FastByteArrayOutputStream baos = new FastByteArrayOutputStream();
        throwable.printStackTrace(new PrintStream(baos));
        String exceptionStr = baos.toString();
        int length = exceptionStr.length();
        if (limit < 0 || limit > length) {
            limit = length;
        }
        if (MapUtil.isNotEmpty(replaceCharToStrMap)) {
            StringBuilder sb = StrUtil.builder();
            for (int i = 0; i < limit; i++) {
                char c = exceptionStr.charAt(i);
                String value = replaceCharToStrMap.get(Character.valueOf(c));
                if (null != value) {
                    sb.append(value);
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }
        if (limit == length) {
            return exceptionStr;
        }
        return StrUtil.subPre(exceptionStr, limit);
    }

    public static boolean isCausedBy(Throwable throwable, Class<? extends Exception>... causeClasses) {
        return null != getCausedBy(throwable, causeClasses);
    }

    public static Throwable getCausedBy(Throwable throwable, Class<? extends Exception>... causeClasses) {
        Throwable th = throwable;
        while (true) {
            Throwable cause = th;
            if (cause != null) {
                for (Class<? extends Exception> causeClass : causeClasses) {
                    if (causeClass.isInstance(cause)) {
                        return cause;
                    }
                }
                th = cause.getCause();
            } else {
                return null;
            }
        }
    }

    public static boolean isFromOrSuppressedThrowable(Throwable throwable, Class<? extends Throwable> exceptionClass) {
        return convertFromOrSuppressedThrowable(throwable, exceptionClass, true) != null;
    }

    public static boolean isFromOrSuppressedThrowable(Throwable throwable, Class<? extends Throwable> exceptionClass, boolean checkCause) {
        return convertFromOrSuppressedThrowable(throwable, exceptionClass, checkCause) != null;
    }

    public static <T extends Throwable> T convertFromOrSuppressedThrowable(Throwable th, Class<T> cls) {
        return (T) convertFromOrSuppressedThrowable(th, cls, true);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <T extends Throwable> T convertFromOrSuppressedThrowable(Throwable th, Class<T> cls, boolean z) {
        T t;
        if (th == 0 || cls == null) {
            return null;
        }
        if (cls.isAssignableFrom(th.getClass())) {
            return th;
        }
        if (z && (t = (T) th.getCause()) != null && cls.isAssignableFrom(t.getClass())) {
            return t;
        }
        Throwable[] suppressed = th.getSuppressed();
        if (ArrayUtil.isNotEmpty((Object[]) suppressed)) {
            for (Throwable th2 : suppressed) {
                T t2 = (T) th2;
                if (cls.isAssignableFrom(t2.getClass())) {
                    return t2;
                }
            }
            return null;
        }
        return null;
    }

    public static List<Throwable> getThrowableList(Throwable throwable) {
        List<Throwable> list = new ArrayList<>();
        while (throwable != null && false == list.contains(throwable)) {
            list.add(throwable);
            throwable = throwable.getCause();
        }
        return list;
    }

    public static Throwable getRootCause(Throwable throwable) {
        List<Throwable> list = getThrowableList(throwable);
        if (list.size() < 1) {
            return null;
        }
        return list.get(list.size() - 1);
    }

    public static String getRootCauseMessage(Throwable th) {
        return getMessage(getRootCause(th));
    }
}
