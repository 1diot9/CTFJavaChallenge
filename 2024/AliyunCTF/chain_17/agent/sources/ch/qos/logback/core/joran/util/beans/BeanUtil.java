package ch.qos.logback.core.joran.util.beans;

import java.lang.reflect.Method;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/util/beans/BeanUtil.class */
public class BeanUtil {
    public static final String PREFIX_GETTER_IS = "is";
    public static final String PREFIX_GETTER_GET = "get";
    public static final String PREFIX_SETTER = "set";
    public static final String PREFIX_ADDER = "add";

    public static boolean isAdder(Method method) {
        int parameterCount = getParameterCount(method);
        if (parameterCount != 1) {
            return false;
        }
        Class<?> returnType = method.getReturnType();
        if (returnType != Void.TYPE) {
            return false;
        }
        String methodName = method.getName();
        return methodName.startsWith(PREFIX_ADDER);
    }

    public static boolean isGetter(Method method) {
        Class<?> returnType;
        int parameterCount = getParameterCount(method);
        if (parameterCount > 0 || (returnType = method.getReturnType()) == Void.TYPE) {
            return false;
        }
        String methodName = method.getName();
        if (!methodName.startsWith(PREFIX_GETTER_GET) && !methodName.startsWith(PREFIX_GETTER_IS)) {
            return false;
        }
        if (methodName.startsWith(PREFIX_GETTER_IS) && !returnType.equals(Boolean.TYPE) && !returnType.equals(Boolean.class)) {
            return false;
        }
        return true;
    }

    private static int getParameterCount(Method method) {
        return method.getParameterTypes().length;
    }

    public static boolean isSetter(Method method) {
        int parameterCount = getParameterCount(method);
        if (parameterCount != 1) {
            return false;
        }
        Class<?> returnType = method.getReturnType();
        if (returnType != Void.TYPE) {
            return false;
        }
        String methodName = method.getName();
        if (!methodName.startsWith("set")) {
            return false;
        }
        return true;
    }

    public static String getPropertyName(Method method) {
        String methodName = method.getName();
        String rawPropertyName = getSubstringIfPrefixMatches(methodName, PREFIX_GETTER_GET);
        if (rawPropertyName == null) {
            rawPropertyName = getSubstringIfPrefixMatches(methodName, "set");
        }
        if (rawPropertyName == null) {
            rawPropertyName = getSubstringIfPrefixMatches(methodName, PREFIX_GETTER_IS);
        }
        if (rawPropertyName == null) {
            rawPropertyName = getSubstringIfPrefixMatches(methodName, PREFIX_ADDER);
        }
        return toLowerCamelCase(rawPropertyName);
    }

    public static String toLowerCamelCase(String string) {
        if (string == null) {
            return null;
        }
        if (string.isEmpty()) {
            return string;
        }
        if (string.length() > 1 && Character.isUpperCase(string.charAt(1)) && Character.isUpperCase(string.charAt(0))) {
            return string;
        }
        char[] chars = string.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    private static String getSubstringIfPrefixMatches(String wholeString, String prefix) {
        if (wholeString.startsWith(prefix)) {
            return wholeString.substring(prefix.length());
        }
        return null;
    }
}
