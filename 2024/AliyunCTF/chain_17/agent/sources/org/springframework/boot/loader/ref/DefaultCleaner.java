package org.springframework.boot.loader.ref;

import java.lang.ref.Cleaner;
import java.util.function.BiConsumer;

/* loaded from: agent.jar:org/springframework/boot/loader/ref/DefaultCleaner.class */
class DefaultCleaner implements Cleaner {
    static final DefaultCleaner instance = new DefaultCleaner();
    static BiConsumer<Object, Cleaner.Cleanable> tracker;
    private final java.lang.ref.Cleaner cleaner = java.lang.ref.Cleaner.create();

    DefaultCleaner() {
    }

    @Override // org.springframework.boot.loader.ref.Cleaner
    public Cleaner.Cleanable register(Object obj, Runnable action) {
        Cleaner.Cleanable cleanable = action != null ? this.cleaner.register(obj, action) : null;
        if (tracker != null) {
            tracker.accept(obj, cleanable);
        }
        return cleanable;
    }
}
