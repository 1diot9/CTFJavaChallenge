package org.jooq.impl;

import org.jooq.Clause;
import org.jooq.Context;
import org.jooq.QueryPartInternal;
import org.jooq.impl.AbstractContext;
import org.jooq.impl.QOM;
import org.jooq.impl.Tools;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ScopeMarker.class */
public enum ScopeMarker {
    TOP_LEVEL_CTE(true, Tools.SimpleDataKey.DATA_TOP_LEVEL_CTE, (ctx, beforeFirst, afterLast, object) -> {
        TopLevelCte cte = (TopLevelCte) object;
        boolean single = cte.size() == 1;
        boolean noWith = afterLast != null && beforeFirst.positions[0] == afterLast.positions[0];
        if (noWith) {
            WithImpl.acceptWithRecursive(ctx, cte.recursive);
            if (single) {
                ctx.formatIndentStart().formatSeparator();
            } else {
                ctx.sql(' ');
            }
        }
        ctx.scopeStart().data(Tools.SimpleDataKey.DATA_TOP_LEVEL_CTE, new TopLevelCte());
        ctx.declareCTE(true).visit(cte).declareCTE(false);
        ctx.scopeEnd();
        if (noWith) {
            if (single) {
                ctx.formatIndentEnd();
            }
        } else if (!Tools.isRendersSeparator(cte)) {
            ctx.sql(',');
        }
        ctx.formatSeparator().sql("");
    });

    final ReplacementRenderer renderer;
    final boolean topLevelOnly;
    final Object key;
    final Marker beforeFirst = new Marker(name() + "_BEFORE");
    final Marker afterLast = new Marker(name() + "_AFTER");

    @FunctionalInterface
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ScopeMarker$ReplacementRenderer.class */
    interface ReplacementRenderer {
        void render(DefaultRenderContext defaultRenderContext, AbstractContext.ScopeStackElement scopeStackElement, AbstractContext.ScopeStackElement scopeStackElement2, ScopeContent scopeContent);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ScopeMarker$ScopeContent.class */
    interface ScopeContent {
        boolean isEmpty();
    }

    ScopeMarker(boolean topLevelOnly, Object key, ReplacementRenderer renderer) {
        this.renderer = renderer;
        this.topLevelOnly = topLevelOnly;
        this.key = key;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ScopeMarker$Marker.class */
    public static class Marker implements QueryPartInternal, QOM.UTransient {
        private final String marker;

        Marker(String marker) {
            this.marker = marker;
        }

        @Override // org.jooq.QueryPartInternal
        public final boolean rendersContent(Context<?> ctx) {
            return false;
        }

        @Override // org.jooq.QueryPartInternal
        public final void accept(Context<?> ctx) {
        }

        @Override // org.jooq.QueryPartInternal
        public final Clause[] clauses(Context<?> ctx) {
            return null;
        }

        @Override // org.jooq.QueryPartInternal
        public final boolean declaresFields() {
            return false;
        }

        @Override // org.jooq.QueryPartInternal
        public final boolean declaresTables() {
            return false;
        }

        @Override // org.jooq.QueryPartInternal
        public final boolean declaresWindows() {
            return false;
        }

        @Override // org.jooq.QueryPartInternal
        public final boolean declaresCTE() {
            return false;
        }

        @Override // org.jooq.QueryPartInternal
        public final boolean generatesCast() {
            return false;
        }

        @Override // org.jooq.QueryPart
        public String toString() {
            return this.marker;
        }
    }
}
