package org.springframework.boot.logging;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/CorrelationIdFormatter.class */
public final class CorrelationIdFormatter {
    public static final CorrelationIdFormatter DEFAULT = of("traceId(32),spanId(16)");
    private final List<Part> parts;
    private final String blank;

    private CorrelationIdFormatter(List<Part> parts) {
        this.parts = parts;
        this.blank = String.format("[%s] ", parts.stream().map((v0) -> {
            return v0.blank();
        }).collect(Collectors.joining(" ")));
    }

    public String format(UnaryOperator<String> resolver) {
        StringBuilder result = new StringBuilder();
        formatTo(resolver, result);
        return result.toString();
    }

    public void formatTo(UnaryOperator<String> resolver, Appendable appendable) {
        Predicate<Part> canResolve = part -> {
            return StringUtils.hasLength((String) resolver.apply(part.name()));
        };
        try {
            if (this.parts.stream().anyMatch(canResolve)) {
                appendable.append('[');
                Iterator<Part> iterator = this.parts.iterator();
                while (iterator.hasNext()) {
                    appendable.append(iterator.next().resolve(resolver));
                    if (iterator.hasNext()) {
                        appendable.append('-');
                    }
                }
                appendable.append("] ");
            } else {
                appendable.append(this.blank);
            }
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public String toString() {
        return (String) this.parts.stream().map((v0) -> {
            return v0.toString();
        }).collect(Collectors.joining(","));
    }

    public static CorrelationIdFormatter of(String spec) {
        try {
            return !StringUtils.hasText(spec) ? DEFAULT : of(List.of((Object[]) spec.split(",")));
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to parse correlation formatter spec '%s'".formatted(spec), ex);
        }
    }

    public static CorrelationIdFormatter of(String[] spec) {
        return of(spec != null ? List.of((Object[]) spec) : Collections.emptyList());
    }

    public static CorrelationIdFormatter of(Collection<String> spec) {
        if (CollectionUtils.isEmpty(spec)) {
            return DEFAULT;
        }
        List<Part> parts = spec.stream().map(Part::of).toList();
        return new CorrelationIdFormatter(parts);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/CorrelationIdFormatter$Part.class */
    public static final class Part extends Record {
        private final String name;
        private final int length;
        private static final Pattern pattern = Pattern.compile("^(.+?)\\((\\d+)\\)$");

        Part(String name, int length) {
            this.name = name;
            this.length = length;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Part.class), Part.class, "name;length", "FIELD:Lorg/springframework/boot/logging/CorrelationIdFormatter$Part;->name:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/logging/CorrelationIdFormatter$Part;->length:I").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Part.class, Object.class), Part.class, "name;length", "FIELD:Lorg/springframework/boot/logging/CorrelationIdFormatter$Part;->name:Ljava/lang/String;", "FIELD:Lorg/springframework/boot/logging/CorrelationIdFormatter$Part;->length:I").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public String name() {
            return this.name;
        }

        public int length() {
            return this.length;
        }

        String resolve(UnaryOperator<String> resolver) {
            String resolved = (String) resolver.apply(name());
            if (resolved == null) {
                return blank();
            }
            int padding = length() - resolved.length();
            return padding <= 0 ? resolved : resolved + " ".repeat(padding);
        }

        String blank() {
            return " ".repeat(this.length);
        }

        @Override // java.lang.Record
        public String toString() {
            return "%s(%s)".formatted(name(), Integer.valueOf(length()));
        }

        static Part of(String part) {
            Matcher matcher = pattern.matcher(part.trim());
            Assert.state(matcher.matches(), (Supplier<String>) () -> {
                return "Invalid specification part '%s'".formatted(part);
            });
            String name = matcher.group(1);
            int length = Integer.parseInt(matcher.group(2));
            return new Part(name, length);
        }
    }
}
