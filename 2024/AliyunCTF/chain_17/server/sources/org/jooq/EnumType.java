package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/EnumType.class */
public interface EnumType {
    @NotNull
    String getLiteral();

    @Nullable
    String getName();

    @Nullable
    default Catalog getCatalog() {
        return null;
    }

    @Nullable
    default Schema getSchema() {
        return null;
    }

    /* JADX WARN: Incorrect return type in method signature: <E:Ljava/lang/Enum<TE;>;:Lorg/jooq/EnumType;>(Ljava/lang/Class<TE;>;Ljava/lang/String;)TE; */
    @Nullable
    static Enum lookupLiteral(Class cls, String literal) {
        return (Enum) EnumTypes.lookupLiteral(cls, literal);
    }
}
