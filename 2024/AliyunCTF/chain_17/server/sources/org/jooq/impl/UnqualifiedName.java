package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Name;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.conf.SettingsTools;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/UnqualifiedName.class */
public final class UnqualifiedName extends AbstractName {
    final String name;
    final Name.Quoted quoted;

    /* JADX INFO: Access modifiers changed from: package-private */
    public UnqualifiedName(String name) {
        this(name, Name.Quoted.DEFAULT);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public UnqualifiedName(String name, Name.Quoted quoted) {
        this.name = name;
        this.quoted = quoted;
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        RenderQuotedNames q = SettingsTools.getRenderQuotedNames(ctx.settings());
        boolean previous = ctx.quote();
        boolean current = this.quoted != Name.Quoted.SYSTEM && (q == RenderQuotedNames.ALWAYS || ((q == RenderQuotedNames.EXPLICIT_DEFAULT_QUOTED && (this.quoted == Name.Quoted.DEFAULT || this.quoted == Name.Quoted.QUOTED)) || (q == RenderQuotedNames.EXPLICIT_DEFAULT_UNQUOTED && this.quoted == Name.Quoted.QUOTED)));
        ctx.quote(current);
        ctx.literal(this.name);
        ctx.quote(previous);
    }

    @Override // org.jooq.Name
    public final String first() {
        return this.name;
    }

    @Override // org.jooq.Name
    public final String last() {
        return this.name;
    }

    @Override // org.jooq.Name
    public final boolean empty() {
        return StringUtils.isEmpty(this.name);
    }

    @Override // org.jooq.Name
    public final boolean qualified() {
        return false;
    }

    @Override // org.jooq.Name
    public final boolean qualifierQualified() {
        return false;
    }

    @Override // org.jooq.Name
    public final Name qualifier() {
        return null;
    }

    @Override // org.jooq.Name
    public final Name unqualifiedName() {
        return this;
    }

    @Override // org.jooq.Name
    public final Name.Quoted quoted() {
        return this.quoted;
    }

    @Override // org.jooq.Name
    public final Name quotedName() {
        return new UnqualifiedName(this.name, Name.Quoted.QUOTED);
    }

    @Override // org.jooq.Name
    public final Name unquotedName() {
        return new UnqualifiedName(this.name, Name.Quoted.UNQUOTED);
    }

    @Override // org.jooq.Name
    public final String[] getName() {
        return new String[]{this.name};
    }

    @Override // org.jooq.Name
    public final Name[] parts() {
        return new Name[]{this};
    }

    @Override // org.jooq.impl.AbstractName, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public int hashCode() {
        if (this.name == null) {
            return 0;
        }
        return 31 + this.name.hashCode();
    }
}
