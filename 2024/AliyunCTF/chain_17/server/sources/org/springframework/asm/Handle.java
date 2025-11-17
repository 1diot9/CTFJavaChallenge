package org.springframework.asm;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/asm/Handle.class */
public final class Handle {
    private final int tag;
    private final String owner;
    private final String name;
    private final String descriptor;
    private final boolean isInterface;

    @Deprecated
    public Handle(final int tag, final String owner, final String name, final String descriptor) {
        this(tag, owner, name, descriptor, tag == 9);
    }

    public Handle(final int tag, final String owner, final String name, final String descriptor, final boolean isInterface) {
        this.tag = tag;
        this.owner = owner;
        this.name = name;
        this.descriptor = descriptor;
        this.isInterface = isInterface;
    }

    public int getTag() {
        return this.tag;
    }

    public String getOwner() {
        return this.owner;
    }

    public String getName() {
        return this.name;
    }

    public String getDesc() {
        return this.descriptor;
    }

    public boolean isInterface() {
        return this.isInterface;
    }

    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof Handle)) {
            return false;
        }
        Handle handle = (Handle) object;
        return this.tag == handle.tag && this.isInterface == handle.isInterface && this.owner.equals(handle.owner) && this.name.equals(handle.name) && this.descriptor.equals(handle.descriptor);
    }

    public int hashCode() {
        return this.tag + (this.isInterface ? 64 : 0) + (this.owner.hashCode() * this.name.hashCode() * this.descriptor.hashCode());
    }

    public String toString() {
        return this.owner + "." + this.name + this.descriptor + " (" + this.tag + (this.isInterface ? " itf" : "") + ")";
    }
}
