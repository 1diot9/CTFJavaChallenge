package org.jooq.impl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import org.jooq.ExecutorProvider;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ExecutorProviderCompletionStage.class */
final class ExecutorProviderCompletionStage<T> implements CompletionStage<T> {
    private final CompletionStage<T> delegate;
    private final ExecutorProvider provider;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> ExecutorProviderCompletionStage<T> of(CompletionStage<T> delegate, ExecutorProvider provider) {
        return new ExecutorProviderCompletionStage<>(delegate, provider);
    }

    ExecutorProviderCompletionStage(CompletionStage<T> delegate, ExecutorProvider provider) {
        this.delegate = delegate;
        this.provider = provider;
    }

    @Override // java.util.concurrent.CompletionStage
    public final <U> CompletionStage<U> thenApply(java.util.function.Function<? super T, ? extends U> fn) {
        return of(this.delegate.thenApply(fn), this.provider);
    }

    @Override // java.util.concurrent.CompletionStage
    public final <U> CompletionStage<U> thenApplyAsync(java.util.function.Function<? super T, ? extends U> fn) {
        return of(this.delegate.thenApplyAsync(fn, this.provider.provide()), this.provider);
    }

    @Override // java.util.concurrent.CompletionStage
    public final <U> CompletionStage<U> thenApplyAsync(java.util.function.Function<? super T, ? extends U> fn, Executor executor) {
        return of(this.delegate.thenApplyAsync(fn, executor), this.provider);
    }

    @Override // java.util.concurrent.CompletionStage
    public final CompletionStage<Void> thenAccept(Consumer<? super T> action) {
        return of(this.delegate.thenAccept(action), this.provider);
    }

    @Override // java.util.concurrent.CompletionStage
    public final CompletionStage<Void> thenAcceptAsync(Consumer<? super T> action) {
        return of(this.delegate.thenAcceptAsync(action, this.provider.provide()), this.provider);
    }

    @Override // java.util.concurrent.CompletionStage
    public final CompletionStage<Void> thenAcceptAsync(Consumer<? super T> action, Executor executor) {
        return of(this.delegate.thenAcceptAsync(action, executor), this.provider);
    }

    @Override // java.util.concurrent.CompletionStage
    public final CompletionStage<Void> thenRun(Runnable action) {
        return of(this.delegate.thenRun(action), this.provider);
    }

    @Override // java.util.concurrent.CompletionStage
    public final CompletionStage<Void> thenRunAsync(Runnable action) {
        return of(this.delegate.thenRunAsync(action, this.provider.provide()), this.provider);
    }

    @Override // java.util.concurrent.CompletionStage
    public final CompletionStage<Void> thenRunAsync(Runnable action, Executor executor) {
        return of(this.delegate.thenRunAsync(action, executor), this.provider);
    }

    @Override // java.util.concurrent.CompletionStage
    public final <U, V> CompletionStage<V> thenCombine(CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn) {
        return of(this.delegate.thenCombine(other, fn), this.provider);
    }

    @Override // java.util.concurrent.CompletionStage
    public final <U, V> CompletionStage<V> thenCombineAsync(CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn) {
        return of(this.delegate.thenCombineAsync(other, fn, this.provider.provide()), this.provider);
    }

    @Override // java.util.concurrent.CompletionStage
    public final <U, V> CompletionStage<V> thenCombineAsync(CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn, Executor executor) {
        return of(this.delegate.thenCombineAsync(other, fn, executor), this.provider);
    }

    @Override // java.util.concurrent.CompletionStage
    public final <U> CompletionStage<Void> thenAcceptBoth(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action) {
        return of(this.delegate.thenAcceptBoth(other, action), this.provider);
    }

    @Override // java.util.concurrent.CompletionStage
    public final <U> CompletionStage<Void> thenAcceptBothAsync(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action) {
        return of(this.delegate.thenAcceptBothAsync(other, action, this.provider.provide()), this.provider);
    }

    @Override // java.util.concurrent.CompletionStage
    public final <U> CompletionStage<Void> thenAcceptBothAsync(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action, Executor executor) {
        return of(this.delegate.thenAcceptBothAsync(other, action, executor), this.provider);
    }

    @Override // java.util.concurrent.CompletionStage
    public final CompletionStage<Void> runAfterBoth(CompletionStage<?> other, Runnable action) {
        return of(this.delegate.runAfterBoth(other, action), this.provider);
    }

    @Override // java.util.concurrent.CompletionStage
    public final CompletionStage<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action) {
        return of(this.delegate.runAfterBothAsync(other, action, this.provider.provide()), this.provider);
    }

    @Override // java.util.concurrent.CompletionStage
    public final CompletionStage<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action, Executor executor) {
        return of(this.delegate.runAfterBothAsync(other, action, executor), this.provider);
    }

    @Override // java.util.concurrent.CompletionStage
    public final <U> CompletionStage<U> applyToEither(CompletionStage<? extends T> other, java.util.function.Function<? super T, U> fn) {
        return of(this.delegate.applyToEither(other, fn), this.provider);
    }

    @Override // java.util.concurrent.CompletionStage
    public final <U> CompletionStage<U> applyToEitherAsync(CompletionStage<? extends T> other, java.util.function.Function<? super T, U> fn) {
        return of(this.delegate.applyToEitherAsync(other, fn, this.provider.provide()), this.provider);
    }

    @Override // java.util.concurrent.CompletionStage
    public final <U> CompletionStage<U> applyToEitherAsync(CompletionStage<? extends T> other, java.util.function.Function<? super T, U> fn, Executor executor) {
        return of(this.delegate.applyToEitherAsync(other, fn, executor), this.provider);
    }

    @Override // java.util.concurrent.CompletionStage
    public final CompletionStage<Void> acceptEither(CompletionStage<? extends T> other, Consumer<? super T> action) {
        return of(this.delegate.acceptEither(other, action), this.provider);
    }

    @Override // java.util.concurrent.CompletionStage
    public final CompletionStage<Void> acceptEitherAsync(CompletionStage<? extends T> other, Consumer<? super T> action) {
        return of(this.delegate.acceptEitherAsync(other, action, this.provider.provide()), this.provider);
    }

    @Override // java.util.concurrent.CompletionStage
    public final CompletionStage<Void> acceptEitherAsync(CompletionStage<? extends T> other, Consumer<? super T> action, Executor executor) {
        return of(this.delegate.acceptEitherAsync(other, action, executor), this.provider);
    }

    @Override // java.util.concurrent.CompletionStage
    public final CompletionStage<Void> runAfterEither(CompletionStage<?> other, Runnable action) {
        return of(this.delegate.runAfterEither(other, action), this.provider);
    }

    @Override // java.util.concurrent.CompletionStage
    public final CompletionStage<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action) {
        return of(this.delegate.runAfterEitherAsync(other, action, this.provider.provide()), this.provider);
    }

    @Override // java.util.concurrent.CompletionStage
    public final CompletionStage<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action, Executor executor) {
        return of(this.delegate.runAfterEitherAsync(other, action, executor), this.provider);
    }

    @Override // java.util.concurrent.CompletionStage
    public final <U> CompletionStage<U> thenCompose(java.util.function.Function<? super T, ? extends CompletionStage<U>> fn) {
        return of(this.delegate.thenCompose(fn), this.provider);
    }

    @Override // java.util.concurrent.CompletionStage
    public final <U> CompletionStage<U> thenComposeAsync(java.util.function.Function<? super T, ? extends CompletionStage<U>> fn) {
        return of(this.delegate.thenComposeAsync(fn, this.provider.provide()), this.provider);
    }

    @Override // java.util.concurrent.CompletionStage
    public final <U> CompletionStage<U> thenComposeAsync(java.util.function.Function<? super T, ? extends CompletionStage<U>> fn, Executor executor) {
        return of(this.delegate.thenComposeAsync(fn, executor), this.provider);
    }

    @Override // java.util.concurrent.CompletionStage
    public final CompletionStage<T> exceptionally(java.util.function.Function<Throwable, ? extends T> fn) {
        return of(this.delegate.exceptionally(fn), this.provider);
    }

    @Override // java.util.concurrent.CompletionStage
    public final CompletionStage<T> whenComplete(BiConsumer<? super T, ? super Throwable> action) {
        return of(this.delegate.whenComplete(action), this.provider);
    }

    @Override // java.util.concurrent.CompletionStage
    public final CompletionStage<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action) {
        return of(this.delegate.whenCompleteAsync(action, this.provider.provide()), this.provider);
    }

    @Override // java.util.concurrent.CompletionStage
    public final CompletionStage<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action, Executor executor) {
        return of(this.delegate.whenCompleteAsync(action, executor), this.provider);
    }

    @Override // java.util.concurrent.CompletionStage
    public final <U> CompletionStage<U> handle(BiFunction<? super T, Throwable, ? extends U> fn) {
        return of(this.delegate.handle(fn), this.provider);
    }

    @Override // java.util.concurrent.CompletionStage
    public final <U> CompletionStage<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn) {
        return of(this.delegate.handleAsync(fn, this.provider.provide()), this.provider);
    }

    @Override // java.util.concurrent.CompletionStage
    public final <U> CompletionStage<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn, Executor executor) {
        return of(this.delegate.handleAsync(fn, executor), this.provider);
    }

    @Override // java.util.concurrent.CompletionStage
    public final CompletableFuture<T> toCompletableFuture() {
        return this.delegate.toCompletableFuture();
    }
}
