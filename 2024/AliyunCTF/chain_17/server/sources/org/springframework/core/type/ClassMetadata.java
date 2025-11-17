package org.springframework.core.type;

import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/type/ClassMetadata.class */
public interface ClassMetadata {
    String getClassName();

    boolean isInterface();

    boolean isAnnotation();

    boolean isAbstract();

    boolean isFinal();

    boolean isIndependent();

    @Nullable
    String getEnclosingClassName();

    @Nullable
    String getSuperClassName();

    String[] getInterfaceNames();

    String[] getMemberClassNames();

    default boolean isConcrete() {
        return (isInterface() || isAbstract()) ? false : true;
    }

    default boolean hasEnclosingClass() {
        return getEnclosingClassName() != null;
    }

    default boolean hasSuperClass() {
        return getSuperClassName() != null;
    }
}
