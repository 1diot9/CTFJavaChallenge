package org.springframework.beans;

import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/PropertyAccessorUtils.class */
public abstract class PropertyAccessorUtils {
    public static String getPropertyName(String propertyPath) {
        int separatorIndex = propertyPath.endsWith("]") ? propertyPath.indexOf(91) : -1;
        return separatorIndex != -1 ? propertyPath.substring(0, separatorIndex) : propertyPath;
    }

    public static boolean isNestedOrIndexedProperty(@Nullable String propertyPath) {
        if (propertyPath == null) {
            return false;
        }
        for (int i = 0; i < propertyPath.length(); i++) {
            char ch2 = propertyPath.charAt(i);
            if (ch2 == '.' || ch2 == '[') {
                return true;
            }
        }
        return false;
    }

    public static int getFirstNestedPropertySeparatorIndex(String propertyPath) {
        return getNestedPropertySeparatorIndex(propertyPath, false);
    }

    public static int getLastNestedPropertySeparatorIndex(String propertyPath) {
        return getNestedPropertySeparatorIndex(propertyPath, true);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:10:0x002c. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:24:0x006e A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:28:0x0068 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static int getNestedPropertySeparatorIndex(java.lang.String r3, boolean r4) {
        /*
            r0 = 0
            r5 = r0
            r0 = r3
            int r0 = r0.length()
            r6 = r0
            r0 = r4
            if (r0 == 0) goto L11
            r0 = r6
            r1 = 1
            int r0 = r0 - r1
            goto L12
        L11:
            r0 = 0
        L12:
            r7 = r0
        L14:
            r0 = r4
            if (r0 == 0) goto L20
            r0 = r7
            if (r0 < 0) goto L74
            goto L26
        L20:
            r0 = r7
            r1 = r6
            if (r0 >= r1) goto L74
        L26:
            r0 = r3
            r1 = r7
            char r0 = r0.charAt(r1)
            switch(r0) {
                case 46: goto L5d;
                case 91: goto L50;
                case 93: goto L50;
                default: goto L64;
            }
        L50:
            r0 = r5
            if (r0 != 0) goto L58
            r0 = 1
            goto L59
        L58:
            r0 = 0
        L59:
            r5 = r0
            goto L64
        L5d:
            r0 = r5
            if (r0 != 0) goto L64
            r0 = r7
            return r0
        L64:
            r0 = r4
            if (r0 == 0) goto L6e
            int r7 = r7 + (-1)
            goto L14
        L6e:
            int r7 = r7 + 1
            goto L14
        L74:
            r0 = -1
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.beans.PropertyAccessorUtils.getNestedPropertySeparatorIndex(java.lang.String, boolean):int");
    }

    public static boolean matchesProperty(String registeredPath, String propertyPath) {
        if (!registeredPath.startsWith(propertyPath)) {
            return false;
        }
        if (registeredPath.length() == propertyPath.length()) {
            return true;
        }
        return registeredPath.charAt(propertyPath.length()) == '[' && registeredPath.indexOf(93, propertyPath.length() + 1) == registeredPath.length() - 1;
    }

    public static String canonicalPropertyName(@Nullable String propertyName) {
        if (propertyName == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(propertyName);
        int searchIndex = 0;
        while (searchIndex != -1) {
            int keyStart = sb.indexOf(PropertyAccessor.PROPERTY_KEY_PREFIX, searchIndex);
            searchIndex = -1;
            if (keyStart != -1) {
                int keyEnd = sb.indexOf("]", keyStart + PropertyAccessor.PROPERTY_KEY_PREFIX.length());
                if (keyEnd != -1) {
                    String key = sb.substring(keyStart + PropertyAccessor.PROPERTY_KEY_PREFIX.length(), keyEnd);
                    if ((key.startsWith("'") && key.endsWith("'")) || (key.startsWith("\"") && key.endsWith("\""))) {
                        sb.delete(keyStart + 1, keyStart + 2);
                        sb.delete(keyEnd - 2, keyEnd - 1);
                        keyEnd -= 2;
                    }
                    searchIndex = keyEnd + "]".length();
                }
            }
        }
        return sb.toString();
    }

    @Nullable
    public static String[] canonicalPropertyNames(@Nullable String[] propertyNames) {
        if (propertyNames == null) {
            return null;
        }
        String[] result = new String[propertyNames.length];
        for (int i = 0; i < propertyNames.length; i++) {
            result[i] = canonicalPropertyName(propertyNames[i]);
        }
        return result;
    }
}
