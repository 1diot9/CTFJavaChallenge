package org.springframework.aot.generate;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/generate/MethodName.class */
public final class MethodName {
    private static final String[] PREFIXES = {BeanUtil.PREFIX_GETTER_GET, "set", BeanUtil.PREFIX_GETTER_IS};
    public static final MethodName NONE = of(new String[0]);
    private final String value;

    private MethodName(String value) {
        this.value = value;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static MethodName of(String... parts) {
        Assert.notNull(parts, "'parts' must not be null");
        return new MethodName(join(parts));
    }

    MethodName and(MethodName name) {
        Assert.notNull(name, "'name' must not be null");
        return and(name.value);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MethodName and(String... parts) {
        Assert.notNull(parts, "'parts' must not be null");
        String joined = join(parts);
        String prefix = getPrefix(joined);
        String suffix = joined.substring(prefix.length());
        return of(prefix, this.value, suffix);
    }

    private String getPrefix(String name) {
        for (String candidate : PREFIXES) {
            if (name.startsWith(candidate)) {
                return candidate;
            }
        }
        return "";
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof MethodName) {
                MethodName that = (MethodName) other;
                if (this.value.equals(that.value)) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.value.hashCode();
    }

    public String toString() {
        return !StringUtils.hasLength(this.value) ? "$$aot" : this.value;
    }

    private static String join(String[] parts) {
        return StringUtils.uncapitalize((String) Arrays.stream(parts).map(MethodName::clean).map(StringUtils::capitalize).collect(Collectors.joining()));
    }

    private static String clean(@Nullable String part) {
        char[] chars = part != null ? part.toCharArray() : new char[0];
        StringBuilder name = new StringBuilder(chars.length);
        boolean uppercase = false;
        int length = chars.length;
        for (int i = 0; i < length; i++) {
            char ch2 = chars[i];
            char outputChar = !uppercase ? ch2 : Character.toUpperCase(ch2);
            name.append(!Character.isLetter(ch2) ? "" : Character.valueOf(outputChar));
            uppercase = ch2 == '.';
        }
        return name.toString();
    }
}
