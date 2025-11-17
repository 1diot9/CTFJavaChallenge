package org.springframework.aot.hint;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/ResourcePatternHint.class */
public final class ResourcePatternHint implements ConditionalHint {
    private final String pattern;

    @Nullable
    private final TypeReference reachableType;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ResourcePatternHint(String pattern, @Nullable TypeReference reachableType) {
        Assert.isTrue("/".equals(pattern) || !pattern.startsWith("/"), (Supplier<String>) () -> {
            return "Resource pattern [%s] must not start with a '/' unless it is the root directory".formatted(pattern);
        });
        this.pattern = pattern;
        this.reachableType = reachableType;
    }

    public String getPattern() {
        return this.pattern;
    }

    public Pattern toRegex() {
        String prefix = this.pattern.startsWith("*") ? ".*" : "";
        String suffix = this.pattern.endsWith("*") ? ".*" : "";
        String regex = (String) Arrays.stream(this.pattern.split("\\*")).filter(s -> {
            return !s.isEmpty();
        }).map(Pattern::quote).collect(Collectors.joining(".*", prefix, suffix));
        return Pattern.compile(regex);
    }

    @Override // org.springframework.aot.hint.ConditionalHint
    @Nullable
    public TypeReference getReachableType() {
        return this.reachableType;
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ResourcePatternHint that = (ResourcePatternHint) o;
        return this.pattern.equals(that.pattern) && Objects.equals(this.reachableType, that.reachableType);
    }

    public int hashCode() {
        return Objects.hash(this.pattern, this.reachableType);
    }
}
