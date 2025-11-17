package org.jooq;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/LoaderFieldMapper.class */
public interface LoaderFieldMapper {

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/LoaderFieldMapper$LoaderFieldContext.class */
    public interface LoaderFieldContext {
        Field<?> field();

        int index();
    }

    Field<?> map(LoaderFieldContext loaderFieldContext);
}
