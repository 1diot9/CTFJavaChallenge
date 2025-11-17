package org.jooq;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/RenderContext.class */
public interface RenderContext extends Context<RenderContext> {

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/RenderContext$CastMode.class */
    public enum CastMode {
        ALWAYS,
        NEVER,
        DEFAULT
    }
}
