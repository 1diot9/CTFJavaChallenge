package org.springframework.boot.context.properties.bind;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/bind/DataObjectPropertyName.class */
public abstract class DataObjectPropertyName {
    private DataObjectPropertyName() {
    }

    public static String toDashedForm(String name) {
        StringBuilder result = new StringBuilder(name.length());
        boolean inIndex = false;
        for (int i = 0; i < name.length(); i++) {
            char ch2 = name.charAt(i);
            if (inIndex) {
                result.append(ch2);
                if (ch2 == ']') {
                    inIndex = false;
                }
            } else if (ch2 == '[') {
                inIndex = true;
                result.append(ch2);
            } else {
                char ch3 = ch2 != '_' ? ch2 : '-';
                if (Character.isUpperCase(ch3) && !result.isEmpty() && result.charAt(result.length() - 1) != '-') {
                    result.append('-');
                }
                result.append(Character.toLowerCase(ch3));
            }
        }
        return result.toString();
    }
}
