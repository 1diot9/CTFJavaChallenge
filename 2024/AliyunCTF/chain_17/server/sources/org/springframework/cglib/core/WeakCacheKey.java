package org.springframework.cglib.core;

import java.lang.ref.WeakReference;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/core/WeakCacheKey.class */
public class WeakCacheKey<T> extends WeakReference<T> {
    private final int hash;

    public WeakCacheKey(T referent) {
        super(referent);
        this.hash = referent.hashCode();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof WeakCacheKey)) {
            return false;
        }
        WeakCacheKey<?> weakCacheKey = (WeakCacheKey) obj;
        Object ours = get();
        Object theirs = weakCacheKey.get();
        return (ours == null || theirs == null || !ours.equals(theirs)) ? false : true;
    }

    public int hashCode() {
        return this.hash;
    }

    public String toString() {
        Object obj = get();
        return obj == null ? "Clean WeakIdentityKey, hash: " + this.hash : obj.toString();
    }
}
