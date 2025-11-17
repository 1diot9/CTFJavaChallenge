package org.jooq.impl;

import org.jooq.Condition;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.XML;
import org.jooq.XMLExistsPassingStep;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/XMLExists.class */
public final class XMLExists extends AbstractCondition implements XMLExistsPassingStep, QOM.XMLExists, QOM.UNotYetImplemented {
    private final Field<String> xpath;
    private final Field<XML> passing;
    private final QOM.XMLPassingMechanism passingMechanism;

    /* renamed from: org.jooq.impl.XMLExists$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/XMLExists$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public XMLExists(Field<String> xpath) {
        this(xpath, null, null);
    }

    private XMLExists(Field<String> xpath, Field<XML> passing, QOM.XMLPassingMechanism passingMechanism) {
        this.xpath = xpath;
        this.passing = passing;
        this.passingMechanism = passingMechanism;
    }

    @Override // org.jooq.XMLExistsPassingStep
    public final Condition passing(XML xml) {
        return passing(Tools.field(xml));
    }

    @Override // org.jooq.XMLExistsPassingStep
    public final Condition passing(Field<XML> xml) {
        return new XMLExists(this.xpath, xml, null);
    }

    @Override // org.jooq.XMLExistsPassingStep
    public final Condition passingByRef(XML xml) {
        return passingByRef(Tools.field(xml));
    }

    @Override // org.jooq.XMLExistsPassingStep
    public final Condition passingByRef(Field<XML> xml) {
        return new XMLExists(this.xpath, xml, QOM.XMLPassingMechanism.BY_REF);
    }

    @Override // org.jooq.XMLExistsPassingStep
    public final Condition passingByValue(XML xml) {
        return passingByRef(Tools.field(xml));
    }

    @Override // org.jooq.XMLExistsPassingStep
    public final Condition passingByValue(Field<XML> xml) {
        return new XMLExists(this.xpath, xml, QOM.XMLPassingMechanism.BY_VALUE);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        return true;
    }

    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                ctx.visit(Keywords.K_XMLEXISTS).sqlIndentStart('(');
                XMLTable.acceptXPath(ctx, this.xpath);
                XMLTable.acceptPassing(ctx, this.passing, this.passingMechanism);
                ctx.sqlIndentEnd(')');
                return;
        }
    }

    @Override // org.jooq.impl.QOM.XMLExists
    public final Field<String> $xpath() {
        return this.xpath;
    }

    @Override // org.jooq.impl.QOM.XMLExists
    public final Field<XML> $passing() {
        return this.passing;
    }

    @Override // org.jooq.impl.QOM.XMLExists
    public final QOM.XMLPassingMechanism $passingMechanism() {
        return this.passingMechanism;
    }
}
