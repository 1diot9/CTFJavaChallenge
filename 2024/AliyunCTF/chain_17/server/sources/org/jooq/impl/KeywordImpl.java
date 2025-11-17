package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Keyword;
import org.jooq.conf.RenderKeywordCase;
import org.jooq.conf.SettingsTools;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/KeywordImpl.class */
public final class KeywordImpl extends AbstractQueryPart implements Keyword, QOM.UTransient {
    private final String asIs;
    private String lower;
    private String upper;
    private String pascal;

    /* JADX INFO: Access modifiers changed from: package-private */
    public KeywordImpl(String keyword) {
        this.asIs = keyword;
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (ctx.separatorRequired()) {
            ctx.sql(' ');
        }
        ctx.sql(render(ctx), true);
    }

    private String render(Context<?> ctx) {
        RenderKeywordCase style = SettingsTools.getRenderKeywordCase(ctx.settings());
        switch (style) {
            case AS_IS:
                return this.asIs;
            case LOWER:
                if (this.lower != null) {
                    return this.lower;
                }
                String lowerCase = this.asIs.toLowerCase();
                this.lower = lowerCase;
                return lowerCase;
            case UPPER:
                if (this.upper != null) {
                    return this.upper;
                }
                String upperCase = this.asIs.toUpperCase();
                this.upper = upperCase;
                return upperCase;
            case PASCAL:
                if (this.pascal != null) {
                    return this.pascal;
                }
                String pascal = pascal(this.asIs);
                this.pascal = pascal;
                return pascal;
            default:
                throw new UnsupportedOperationException("Unsupported style: " + String.valueOf(style));
        }
    }

    private static final String pascal(String keyword) {
        int next;
        if (keyword.isEmpty()) {
            return keyword;
        }
        if (keyword.indexOf(32) >= 0) {
            StringBuilder sb = new StringBuilder();
            int prev = 0;
            do {
                next = keyword.indexOf(32, prev);
                if (prev > 0) {
                    sb.append(' ');
                }
                sb.append(Character.toUpperCase(keyword.charAt(prev)));
                sb.append(keyword.substring(prev + 1, next == -1 ? keyword.length() : next).toLowerCase());
                prev = next + 1;
            } while (next != -1);
            return sb.toString();
        }
        return Character.toUpperCase(keyword.charAt(0)) + keyword.substring(1).toLowerCase();
    }
}
