package cn.hutool.core.lang;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.func.Func0;
import cn.hutool.core.lang.func.VoidFunc0;
import cn.hutool.core.util.StrUtil;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/Opt.class */
public class Opt<T> {
    private static final Opt<?> EMPTY = new Opt<>(null);
    private final T value;
    private Exception exception;

    public static <T> Opt<T> empty() {
        return (Opt<T>) EMPTY;
    }

    public static <T> Opt<T> of(T value) {
        return new Opt<>(Objects.requireNonNull(value));
    }

    public static <T> Opt<T> ofNullable(T value) {
        return value == null ? empty() : new Opt<>(value);
    }

    public static <T> Opt<T> ofBlankAble(T value) {
        return StrUtil.isBlankIfStr(value) ? empty() : new Opt<>(value);
    }

    public static <T, R extends Collection<T>> Opt<R> ofEmptyAble(R value) {
        return CollectionUtil.isEmpty((Collection<?>) value) ? empty() : new Opt<>(value);
    }

    public static <T> Opt<T> ofTry(Func0<T> supplier) {
        try {
            return ofNullable(supplier.call());
        } catch (Exception e) {
            Opt<T> empty = new Opt<>(null);
            ((Opt) empty).exception = e;
            return empty;
        }
    }

    private Opt(T value) {
        this.value = value;
    }

    public T get() {
        return this.value;
    }

    public boolean isEmpty() {
        return this.value == null;
    }

    public Exception getException() {
        return this.exception;
    }

    public boolean isFail() {
        return null != this.exception;
    }

    public boolean isPresent() {
        return this.value != null;
    }

    public Opt<T> ifPresent(Consumer<? super T> consumer) {
        if (isPresent()) {
            consumer.accept(this.value);
        }
        return this;
    }

    public Opt<T> ifPresentOrElse(Consumer<? super T> consumer, VoidFunc0 voidFunc0) {
        if (isPresent()) {
            consumer.accept(this.value);
        } else {
            voidFunc0.callWithRuntimeException();
        }
        return this;
    }

    public <U> Opt<U> mapOrElse(Function<? super T, ? extends U> function, VoidFunc0 voidFunc0) {
        if (isPresent()) {
            return ofNullable(function.apply(this.value));
        }
        voidFunc0.callWithRuntimeException();
        return empty();
    }

    public Opt<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        if (isEmpty()) {
            return this;
        }
        return predicate.test(this.value) ? this : empty();
    }

    public <U> Opt<U> map(Function<? super T, ? extends U> function) {
        Objects.requireNonNull(function);
        if (isEmpty()) {
            return empty();
        }
        return ofNullable(function.apply(this.value));
    }

    public <U> Opt<U> flatMap(Function<? super T, ? extends Opt<? extends U>> function) {
        Objects.requireNonNull(function);
        if (isEmpty()) {
            return empty();
        }
        return (Opt) Objects.requireNonNull(function.apply(this.value));
    }

    public <U> Opt<U> flattedMap(Function<? super T, ? extends Optional<? extends U>> function) {
        Objects.requireNonNull(function);
        if (isEmpty()) {
            return empty();
        }
        return ofNullable(function.apply(this.value).orElse(null));
    }

    public Opt<T> peek(Consumer<T> action) throws NullPointerException {
        Objects.requireNonNull(action);
        if (isEmpty()) {
            return empty();
        }
        action.accept(this.value);
        return this;
    }

    @SafeVarargs
    public final Opt<T> peeks(Consumer<T>... actions) throws NullPointerException {
        return (Opt) Stream.of((Object[]) actions).reduce(this, (v0, v1) -> {
            return v0.peek(v1);
        }, (opts, opt) -> {
            return null;
        });
    }

    public Opt<T> or(Supplier<? extends Opt<? extends T>> supplier) {
        Objects.requireNonNull(supplier);
        if (isPresent()) {
            return this;
        }
        return (Opt) Objects.requireNonNull(supplier.get());
    }

    public Stream<T> stream() {
        if (isEmpty()) {
            return Stream.empty();
        }
        return Stream.of(this.value);
    }

    public T orElse(T other) {
        return isPresent() ? this.value : other;
    }

    public T exceptionOrElse(T other) {
        return isFail() ? other : this.value;
    }

    public T orElseGet(Supplier<? extends T> supplier) {
        return isPresent() ? this.value : supplier.get();
    }

    public T orElseThrow() {
        return orElseThrow(NoSuchElementException::new, "No value present");
    }

    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws Throwable {
        if (isPresent()) {
            return this.value;
        }
        throw exceptionSupplier.get();
    }

    public <X extends Throwable> T orElseThrow(Function<String, ? extends X> exceptionFunction, String message) throws Throwable {
        if (isPresent()) {
            return this.value;
        }
        throw exceptionFunction.apply(message);
    }

    public Optional<T> toOptional() {
        return Optional.ofNullable(this.value);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Opt)) {
            return false;
        }
        Opt<?> other = (Opt) obj;
        return Objects.equals(this.value, other.value);
    }

    public int hashCode() {
        return Objects.hashCode(this.value);
    }

    public String toString() {
        return StrUtil.toStringOrNull(this.value);
    }
}
