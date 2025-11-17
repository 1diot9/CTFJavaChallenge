package org.jooq.impl;

import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.XML;
import org.jooq.XMLQueryPassingStep;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/XMLQuery.class */
public final class XMLQuery extends AbstractField<XML> implements XMLQueryPassingStep, QOM.XMLQuery {
    private final Field<String> xpath;
    private final Field<XML> passing;
    private final QOM.XMLPassingMechanism passingMechanism;

    /* JADX INFO: Access modifiers changed from: package-private */
    public XMLQuery(Field<String> xpath) {
        this(xpath, null, null);
    }

    private XMLQuery(Field<String> xpath, Field<XML> passing, QOM.XMLPassingMechanism passingMechanism) {
        super(Names.N_XMLQUERY, SQLDataType.XML);
        this.xpath = xpath;
        this.passing = passing;
        this.passingMechanism = passingMechanism;
    }

    @Override // org.jooq.XMLQueryPassingStep
    public final Field<XML> passing(XML xml) {
        return passing(Tools.field(xml));
    }

    @Override // org.jooq.XMLQueryPassingStep
    public final Field<XML> passing(Field<XML> xml) {
        return new XMLQuery(this.xpath, xml, null);
    }

    @Override // org.jooq.XMLQueryPassingStep
    public final Field<XML> passingByRef(XML xml) {
        return passingByRef(Tools.field(xml));
    }

    @Override // org.jooq.XMLQueryPassingStep
    public final Field<XML> passingByRef(Field<XML> xml) {
        return new XMLQuery(this.xpath, xml, QOM.XMLPassingMechanism.BY_REF);
    }

    /* JADX WARN: Type inference failed for: r0v13, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case POSTGRES:
                Field<XML> x = DSL.field(DSL.name("x"), SQLDataType.XML);
                ctx.sql('(').visit(DSL.select(DSL.xmlagg(x)).from(DSL.unnest((Field<?>) DSL.field("xpath({0}, {1})", (DataType) SQLDataType.XML.getArrayDataType(), this.xpath, this.passing)).as("t", x.getName()))).sql(')');
                return;
            default:
                ctx.visit(Names.N_XMLQUERY).sqlIndentStart('(');
                XMLTable.acceptXPath(ctx, this.xpath);
                XMLTable.acceptPassing(ctx, this.passing, this.passingMechanism);
                ctx.sqlIndentEnd(')');
                return;
        }
    }

    @Override // org.jooq.impl.QOM.XMLQuery
    public final Field<String> $xpath() {
        return this.xpath;
    }

    @Override // org.jooq.impl.QOM.XMLQuery
    public final Field<XML> $passing() {
        return this.passing;
    }

    @Override // org.jooq.impl.QOM.XMLQuery
    public final QOM.XMLPassingMechanism $passingMechanism() {
        return this.passingMechanism;
    }
}
