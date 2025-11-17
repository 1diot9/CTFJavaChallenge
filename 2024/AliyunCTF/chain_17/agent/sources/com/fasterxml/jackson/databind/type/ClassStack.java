package com.fasterxml.jackson.databind.type;

import com.fasterxml.jackson.databind.JavaType;
import java.util.ArrayList;
import java.util.Iterator;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/type/ClassStack.class */
public final class ClassStack {
    protected final ClassStack _parent;
    protected final Class<?> _current;
    private ArrayList<ResolvedRecursiveType> _selfRefs;

    public ClassStack(Class<?> rootType) {
        this(null, rootType);
    }

    private ClassStack(ClassStack parent, Class<?> curr) {
        this._parent = parent;
        this._current = curr;
    }

    public ClassStack child(Class<?> cls) {
        return new ClassStack(this, cls);
    }

    public void addSelfReference(ResolvedRecursiveType ref) {
        if (this._selfRefs == null) {
            this._selfRefs = new ArrayList<>();
        }
        this._selfRefs.add(ref);
    }

    public void resolveSelfReferences(JavaType resolved) {
        if (this._selfRefs != null) {
            Iterator<ResolvedRecursiveType> it = this._selfRefs.iterator();
            while (it.hasNext()) {
                ResolvedRecursiveType ref = it.next();
                ref.setReference(resolved);
            }
        }
    }

    public ClassStack find(Class<?> cls) {
        if (this._current == cls) {
            return this;
        }
        ClassStack classStack = this._parent;
        while (true) {
            ClassStack curr = classStack;
            if (curr != null) {
                if (curr._current != cls) {
                    classStack = curr._parent;
                } else {
                    return curr;
                }
            } else {
                return null;
            }
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ClassStack (self-refs: ").append(this._selfRefs == null ? CustomBooleanEditor.VALUE_0 : String.valueOf(this._selfRefs.size())).append(')');
        ClassStack classStack = this;
        while (true) {
            ClassStack curr = classStack;
            if (curr != null) {
                sb.append(' ').append(curr._current.getName());
                classStack = curr._parent;
            } else {
                sb.append(']');
                return sb.toString();
            }
        }
    }
}
