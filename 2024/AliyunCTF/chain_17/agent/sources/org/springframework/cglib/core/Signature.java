package org.springframework.cglib.core;

import org.springframework.asm.Type;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/core/Signature.class */
public class Signature {
    private String name;
    private String desc;

    public Signature(String name, String desc) {
        if (name.indexOf(40) >= 0) {
            throw new IllegalArgumentException("Name '" + name + "' is invalid");
        }
        this.name = name;
        this.desc = desc;
    }

    public Signature(String name, Type returnType, Type[] argumentTypes) {
        this(name, Type.getMethodDescriptor(returnType, argumentTypes));
    }

    public String getName() {
        return this.name;
    }

    public String getDescriptor() {
        return this.desc;
    }

    public Type getReturnType() {
        return Type.getReturnType(this.desc);
    }

    public Type[] getArgumentTypes() {
        return Type.getArgumentTypes(this.desc);
    }

    public String toString() {
        return this.name + this.desc;
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof Signature)) {
            return false;
        }
        Signature other = (Signature) o;
        return this.name.equals(other.name) && this.desc.equals(other.desc);
    }

    public int hashCode() {
        return this.name.hashCode() ^ this.desc.hashCode();
    }
}
