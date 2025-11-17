package org.springframework.core.annotation;

import java.util.Arrays;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/PackagesAnnotationFilter.class */
public final class PackagesAnnotationFilter implements AnnotationFilter {
    private final String[] prefixes;
    private final int hashCode;

    /* JADX INFO: Access modifiers changed from: package-private */
    public PackagesAnnotationFilter(String... packages) {
        Assert.notNull(packages, "Packages array must not be null");
        this.prefixes = new String[packages.length];
        for (int i = 0; i < packages.length; i++) {
            String pkg = packages[i];
            Assert.hasText(pkg, "Packages array must not have empty elements");
            this.prefixes[i] = pkg + ".";
        }
        Arrays.sort(this.prefixes);
        this.hashCode = Arrays.hashCode(this.prefixes);
    }

    @Override // org.springframework.core.annotation.AnnotationFilter
    public boolean matches(String annotationType) {
        for (String prefix : this.prefixes) {
            if (annotationType.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        return Arrays.equals(this.prefixes, ((PackagesAnnotationFilter) other).prefixes);
    }

    public int hashCode() {
        return this.hashCode;
    }

    public String toString() {
        return "Packages annotation filter: " + StringUtils.arrayToCommaDelimitedString(this.prefixes);
    }
}
