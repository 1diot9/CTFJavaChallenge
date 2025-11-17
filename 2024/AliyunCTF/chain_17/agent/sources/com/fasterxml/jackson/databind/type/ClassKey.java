package com.fasterxml.jackson.databind.type;

import java.io.Serializable;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/type/ClassKey.class */
public final class ClassKey implements Comparable<ClassKey>, Serializable {
    private static final long serialVersionUID = 1;
    private String _className;
    private Class<?> _class;
    private int _hashCode;

    public ClassKey() {
        this._class = null;
        this._className = null;
        this._hashCode = 0;
    }

    public ClassKey(Class<?> clz) {
        this._class = clz;
        this._className = clz.getName();
        this._hashCode = this._className.hashCode();
    }

    public void reset(Class<?> clz) {
        this._class = clz;
        this._className = clz.getName();
        this._hashCode = this._className.hashCode();
    }

    @Override // java.lang.Comparable
    public int compareTo(ClassKey other) {
        return this._className.compareTo(other._className);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != getClass()) {
            return false;
        }
        ClassKey other = (ClassKey) o;
        return other._class == this._class;
    }

    public int hashCode() {
        return this._hashCode;
    }

    public String toString() {
        return this._className;
    }
}
