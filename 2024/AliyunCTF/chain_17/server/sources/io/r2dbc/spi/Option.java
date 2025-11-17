package io.r2dbc.spi;

import java.util.Objects;

/* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/Option.class */
public final class Option<T> {
    private static final ConstantPool<Option<?>> CONSTANTS = new ConstantPool<Option<?>>() { // from class: io.r2dbc.spi.Option.1
        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // io.r2dbc.spi.ConstantPool
        public Option<?> createConstant(String name, boolean sensitive) {
            return new Option<>(name, sensitive);
        }
    };
    private final String name;
    private final boolean sensitive;

    private Option(String name, boolean sensitive) {
        this.name = name;
        this.sensitive = sensitive;
    }

    public static <T> Option<T> sensitiveValueOf(String name) {
        Assert.requireNonNull(name, "name must not be null");
        Assert.requireNonEmpty(name, "name must not be empty");
        return (Option) CONSTANTS.valueOf(name, true);
    }

    public static <T> Option<T> valueOf(String name) {
        Assert.requireNonNull(name, "name must not be null");
        Assert.requireNonEmpty(name, "name must not be empty");
        return (Option) CONSTANTS.valueOf(name, false);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Nullable
    public T cast(@Nullable Object obj) {
        if (obj == 0) {
            return null;
        }
        return obj;
    }

    public String name() {
        return this.name;
    }

    public String toString() {
        return "Option{name='" + this.name + "', sensitive=" + this.sensitive + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Option<?> option = (Option) o;
        return this.sensitive == option.sensitive && this.name.equals(option.name);
    }

    public int hashCode() {
        return Objects.hash(this.name, Boolean.valueOf(this.sensitive));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean sensitive() {
        return this.sensitive;
    }
}
