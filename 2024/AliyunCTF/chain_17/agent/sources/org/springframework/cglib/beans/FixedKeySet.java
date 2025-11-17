package org.springframework.cglib.beans;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/beans/FixedKeySet.class */
public class FixedKeySet extends AbstractSet {
    private Set set;
    private int size;

    public FixedKeySet(String[] keys) {
        this.size = keys.length;
        this.set = Collections.unmodifiableSet(new HashSet(Arrays.asList(keys)));
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
    public Iterator iterator() {
        return this.set.iterator();
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public int size() {
        return this.size;
    }
}
