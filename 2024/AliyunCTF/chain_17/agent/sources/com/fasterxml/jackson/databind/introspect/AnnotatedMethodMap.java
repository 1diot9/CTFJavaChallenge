package com.fasterxml.jackson.databind.introspect;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/introspect/AnnotatedMethodMap.class */
public final class AnnotatedMethodMap implements Iterable<AnnotatedMethod> {
    protected Map<MemberKey, AnnotatedMethod> _methods;

    public AnnotatedMethodMap() {
    }

    public AnnotatedMethodMap(Map<MemberKey, AnnotatedMethod> m) {
        this._methods = m;
    }

    public int size() {
        if (this._methods == null) {
            return 0;
        }
        return this._methods.size();
    }

    public AnnotatedMethod find(String name, Class<?>[] paramTypes) {
        if (this._methods == null) {
            return null;
        }
        return this._methods.get(new MemberKey(name, paramTypes));
    }

    public AnnotatedMethod find(Method m) {
        if (this._methods == null) {
            return null;
        }
        return this._methods.get(new MemberKey(m));
    }

    @Override // java.lang.Iterable
    public Iterator<AnnotatedMethod> iterator() {
        if (this._methods == null) {
            return Collections.emptyIterator();
        }
        return this._methods.values().iterator();
    }
}
