package org.yaml.snakeyaml.util;

/* loaded from: server.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/util/EnumUtils.class */
public class EnumUtils {
    public static <T extends Enum<T>> T findEnumInsensitiveCase(Class<T> enumType, String name) {
        for (T constant : enumType.getEnumConstants()) {
            if (constant.name().compareToIgnoreCase(name) == 0) {
                return constant;
            }
        }
        throw new IllegalArgumentException("No enum constant " + enumType.getCanonicalName() + "." + name);
    }
}
