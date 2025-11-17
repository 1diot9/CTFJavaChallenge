package org.jooq.impl;

import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.TableOptions;
import org.jooq.XML;
import org.jooq.XMLTableColumnPathStep;
import org.jooq.XMLTableColumnsFirstStep;
import org.jooq.XMLTablePassingStep;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/XMLTable.class */
public final class XMLTable extends AbstractTable<Record> implements XMLTablePassingStep, XMLTableColumnPathStep, QOM.UNotYetImplemented {
    private final Field<String> xpath;
    private final Field<XML> passing;
    private final QOM.XMLPassingMechanism passingMechanism;
    private final QueryPartList<XMLTableColumn> columns;
    private final boolean hasOrdinality;
    private transient FieldsImpl<Record> fields;

    /* renamed from: org.jooq.impl.XMLTable$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/XMLTable$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    @Override // org.jooq.XMLTablePassingStep
    public /* bridge */ /* synthetic */ XMLTableColumnsFirstStep passingByValue(Field field) {
        return passingByValue((Field<XML>) field);
    }

    @Override // org.jooq.XMLTablePassingStep
    public /* bridge */ /* synthetic */ XMLTableColumnsFirstStep passingByRef(Field field) {
        return passingByRef((Field<XML>) field);
    }

    @Override // org.jooq.XMLTablePassingStep
    public /* bridge */ /* synthetic */ XMLTableColumnsFirstStep passing(Field field) {
        return passing((Field<XML>) field);
    }

    @Override // org.jooq.XMLTableColumnsFirstStep
    public /* bridge */ /* synthetic */ XMLTableColumnPathStep column(Field field, DataType dataType) {
        return column((Field<?>) field, (DataType<?>) dataType);
    }

    @Override // org.jooq.XMLTableColumnsFirstStep
    public /* bridge */ /* synthetic */ XMLTableColumnPathStep column(Name name, DataType dataType) {
        return column(name, (DataType<?>) dataType);
    }

    @Override // org.jooq.XMLTableColumnsFirstStep
    public /* bridge */ /* synthetic */ XMLTableColumnPathStep column(String str, DataType dataType) {
        return column(str, (DataType<?>) dataType);
    }

    @Override // org.jooq.XMLTableColumnsFirstStep
    public /* bridge */ /* synthetic */ XMLTableColumnPathStep column(Field field) {
        return column((Field<?>) field);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public XMLTable(Field<String> xpath) {
        this(xpath, null, null, null, false);
    }

    private XMLTable(Field<String> xpath, Field<XML> passing, QOM.XMLPassingMechanism passingMechanism, QueryPartList<XMLTableColumn> columns, boolean hasOrdinality) {
        super(TableOptions.expression(), Names.N_XMLTABLE);
        this.xpath = xpath;
        this.passing = passing;
        this.passingMechanism = passingMechanism;
        this.columns = columns == null ? new QueryPartList<>() : columns;
        this.hasOrdinality = hasOrdinality;
    }

    @Override // org.jooq.XMLTablePassingStep
    public final XMLTable passing(XML xml) {
        return passing((Field<XML>) Tools.field(xml));
    }

    @Override // org.jooq.XMLTablePassingStep
    public final XMLTable passing(Field<XML> xml) {
        return new XMLTable(this.xpath, xml, null, this.columns, this.hasOrdinality);
    }

    @Override // org.jooq.XMLTablePassingStep
    public final XMLTable passingByRef(XML xml) {
        return passingByRef((Field<XML>) Tools.field(xml));
    }

    @Override // org.jooq.XMLTablePassingStep
    public final XMLTable passingByRef(Field<XML> xml) {
        return new XMLTable(this.xpath, xml, QOM.XMLPassingMechanism.BY_REF, this.columns, this.hasOrdinality);
    }

    @Override // org.jooq.XMLTablePassingStep
    public final XMLTable passingByValue(XML xml) {
        return passingByRef((Field<XML>) Tools.field(xml));
    }

    @Override // org.jooq.XMLTablePassingStep
    public final XMLTable passingByValue(Field<XML> xml) {
        return new XMLTable(this.xpath, xml, QOM.XMLPassingMechanism.BY_VALUE, this.columns, this.hasOrdinality);
    }

    @Override // org.jooq.XMLTableColumnsFirstStep
    public final XMLTable column(String name) {
        return column(DSL.name(name));
    }

    @Override // org.jooq.XMLTableColumnsFirstStep
    public final XMLTable column(Name name) {
        return column(DSL.field(name));
    }

    @Override // org.jooq.XMLTableColumnsFirstStep
    public final XMLTable column(Field<?> name) {
        return column(name, name.getDataType());
    }

    @Override // org.jooq.XMLTableColumnsFirstStep
    public final XMLTable column(String name, DataType<?> type) {
        return column(DSL.name(name), type);
    }

    @Override // org.jooq.XMLTableColumnsFirstStep
    public final XMLTable column(Name name, DataType<?> type) {
        return column(DSL.field(name), type);
    }

    @Override // org.jooq.XMLTableColumnsFirstStep
    public final XMLTable column(Field<?> name, DataType<?> type) {
        QueryPartList<XMLTableColumn> c = new QueryPartList<>(this.columns);
        c.add((QueryPartList<XMLTableColumn>) new XMLTableColumn(name, type, false, null));
        return new XMLTable(this.xpath, this.passing, this.passingMechanism, c, this.hasOrdinality);
    }

    @Override // org.jooq.XMLTableColumnForOrdinalityStep
    public final XMLTable forOrdinality() {
        return path0(true, null);
    }

    @Override // org.jooq.XMLTableColumnPathStep
    public final XMLTable path(String path) {
        return path0(false, path);
    }

    private final XMLTable path0(boolean forOrdinality, String path) {
        QueryPartList<XMLTableColumn> c = new QueryPartList<>(this.columns);
        int i = c.size() - 1;
        XMLTableColumn last = c.get(i);
        c.set(i, (int) new XMLTableColumn(last.field, last.type, forOrdinality, path));
        return new XMLTable(this.xpath, this.passing, this.passingMechanism, c, this.hasOrdinality || forOrdinality);
    }

    @Override // org.jooq.RecordQualifier
    public final Class<? extends Record> getRecordType() {
        return RecordImplN.class;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractTable
    public final FieldsImpl<Record> fields0() {
        if (this.fields == null) {
            this.fields = new FieldsImpl<>(Tools.map(this.columns, c -> {
                return c.field.getDataType() == c.type ? c.field : DSL.field(c.field.getQualifiedName(), c.type);
            }));
        }
        return this.fields;
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                acceptStandard(ctx);
                return;
        }
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v7, types: [org.jooq.Context] */
    private final void acceptStandard(Context<?> ctx) {
        ctx.visit(Keywords.K_XMLTABLE).sqlIndentStart('(');
        acceptXPath(ctx, this.xpath);
        if (this.passing != null) {
            acceptPassing(ctx, this.passing, this.passingMechanism);
        }
        ctx.formatSeparator().visit(Keywords.K_COLUMNS).separatorRequired(true).visit(this.columns);
        ctx.sqlIndentEnd(')');
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void acceptXPath(Context<?> ctx, Field<String> xpath) {
        ctx.visit((Field<?>) xpath);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v11, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v14, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v6, types: [org.jooq.Context] */
    public static final void acceptPassing(Context<?> ctx, Field<XML> passing, QOM.XMLPassingMechanism passingMechanism) {
        ctx.formatSeparator().visit(Keywords.K_PASSING);
        if (passingMechanism == QOM.XMLPassingMechanism.BY_REF) {
            ctx.sql(' ').visit(Keywords.K_BY).sql(' ').visit(Keywords.K_REF);
        } else if (passingMechanism == QOM.XMLPassingMechanism.BY_VALUE) {
            ctx.sql(' ').visit(Keywords.K_BY).sql(' ').visit(Keywords.K_VALUE);
        }
        ctx.sql(' ').visit(passing);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/XMLTable$XMLTableColumn.class */
    public static class XMLTableColumn extends AbstractQueryPart implements QOM.UNotYetImplemented {
        final Field<?> field;
        final DataType<?> type;
        final boolean forOrdinality;
        final String path;

        XMLTableColumn(Field<?> field, DataType<?> type, boolean forOrdinality, String path) {
            this.field = field;
            this.type = type;
            this.forOrdinality = forOrdinality;
            this.path = path;
        }

        /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
        /* JADX WARN: Type inference failed for: r0v14, types: [org.jooq.Context] */
        /* JADX WARN: Type inference failed for: r0v9, types: [org.jooq.Context] */
        @Override // org.jooq.QueryPartInternal
        public final void accept(Context<?> ctx) {
            ctx.qualify(false, c -> {
                c.visit(this.field);
            }).sql(' ');
            if (this.forOrdinality) {
                ctx.visit(Keywords.K_FOR).sql(' ').visit(Keywords.K_ORDINALITY);
            } else {
                Tools.toSQLDDLTypeDeclaration(ctx, this.type);
            }
            if (this.path != null) {
                ctx.sql(' ').visit(Keywords.K_PATH).sql(' ').visit((Field<?>) DSL.inline(this.path));
            }
        }
    }
}
