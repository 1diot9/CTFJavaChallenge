package org.jooq.impl;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import org.jooq.Tag;
import org.jooq.tools.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/TagImpl.class */
final class TagImpl extends Record implements Tag {
    private final String id;
    private final String message;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TagImpl(String id, String message) {
        this.id = id;
        this.message = message;
    }

    @Override // java.lang.Record
    public final int hashCode() {
        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, TagImpl.class), TagImpl.class, "id;message", "FIELD:Lorg/jooq/impl/TagImpl;->id:Ljava/lang/String;", "FIELD:Lorg/jooq/impl/TagImpl;->message:Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final boolean equals(Object o) {
        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, TagImpl.class, Object.class), TagImpl.class, "id;message", "FIELD:Lorg/jooq/impl/TagImpl;->id:Ljava/lang/String;", "FIELD:Lorg/jooq/impl/TagImpl;->message:Ljava/lang/String;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
    }

    @Override // org.jooq.Tag
    public String id() {
        return this.id;
    }

    @Override // org.jooq.Tag
    public String message() {
        return this.message;
    }

    @Override // java.lang.Record
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id());
        if (!StringUtils.isBlank(message())) {
            sb.append(" - ").append(message());
        }
        return sb.toString();
    }
}
