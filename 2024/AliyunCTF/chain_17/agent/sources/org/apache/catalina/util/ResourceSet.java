package org.apache.catalina.util;

import java.util.Collection;
import java.util.HashSet;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/util/ResourceSet.class */
public final class ResourceSet<T> extends HashSet<T> {
    private static final long serialVersionUID = 1;
    private boolean locked;
    private static final StringManager sm = StringManager.getManager("org.apache.catalina.util");

    public ResourceSet() {
        this.locked = false;
    }

    public ResourceSet(int initialCapacity) {
        super(initialCapacity);
        this.locked = false;
    }

    public ResourceSet(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
        this.locked = false;
    }

    public ResourceSet(Collection<T> coll) {
        super(coll);
        this.locked = false;
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Override // java.util.HashSet, java.util.AbstractCollection, java.util.Collection, java.util.Set
    public boolean add(T o) {
        if (this.locked) {
            throw new IllegalStateException(sm.getString("resourceSet.locked"));
        }
        return super.add(o);
    }

    @Override // java.util.HashSet, java.util.AbstractCollection, java.util.Collection, java.util.Set
    public void clear() {
        if (this.locked) {
            throw new IllegalStateException(sm.getString("resourceSet.locked"));
        }
        super.clear();
    }

    @Override // java.util.HashSet, java.util.AbstractCollection, java.util.Collection, java.util.Set
    public boolean remove(Object o) {
        if (this.locked) {
            throw new IllegalStateException(sm.getString("resourceSet.locked"));
        }
        return super.remove(o);
    }
}
