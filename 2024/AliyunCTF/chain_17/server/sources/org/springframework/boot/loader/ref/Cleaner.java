package org.springframework.boot.loader.ref;

import java.lang.ref.Cleaner;

/* loaded from: server.jar:org/springframework/boot/loader/ref/Cleaner.class */
public interface Cleaner {
    public static final Cleaner instance = DefaultCleaner.instance;

    Cleaner.Cleanable register(Object obj, Runnable action);
}
