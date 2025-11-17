package org.jooq.impl;

import org.jooq.Field;
import org.jooq.Name;
import org.jooq.SQLDialect;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractLeadLag.class */
abstract class AbstractLeadLag<T> extends AbstractWindowFunction<T> {
    private final Field<T> field;
    private final Field<Integer> offset;
    private final Field<T> defaultValue;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.jooq.impl.AbstractLeadLag$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractLeadLag$1.class */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractLeadLag(Name name, Field<T> field, Field<Integer> offset, Field<T> defaultValue) {
        super(name, field.getDataType().null_());
        this.field = field;
        this.offset = offset;
        this.defaultValue = defaultValue;
    }

    /* JADX WARN: Code restructure failed: missing block: B:8:0x0028, code lost:            accept0(r4);     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x002d, code lost:            return;     */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void accept(org.jooq.Context<?> r4) {
        /*
            r3 = this;
            r0 = r3
            org.jooq.Field<T> r0 = r0.defaultValue
            if (r0 != 0) goto Lf
            r0 = r3
            r1 = r4
            r0.accept0(r1)
            goto L2d
        Lf:
            int[] r0 = org.jooq.impl.AbstractLeadLag.AnonymousClass1.$SwitchMap$org$jooq$SQLDialect
            r1 = r4
            org.jooq.SQLDialect r1 = r1.family()
            int r1 = r1.ordinal()
            r0 = r0[r1]
            switch(r0) {
                default: goto L28;
            }
        L28:
            r0 = r3
            r1 = r4
            r0.accept0(r1)
        L2d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jooq.impl.AbstractLeadLag.accept(org.jooq.Context):void");
    }

    /* JADX WARN: Code restructure failed: missing block: B:12:0x0080, code lost:            r4.sql(", ").visit(r3.defaultValue);     */
    /* JADX WARN: Code restructure failed: missing block: B:7:0x0050, code lost:            r4.sql(", ").visit(r3.offset);     */
    /* JADX WARN: Type inference failed for: r0v17, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v22, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private final void accept0(org.jooq.Context<?> r4) {
        /*
            r3 = this;
            int[] r0 = org.jooq.impl.AbstractLeadLag.AnonymousClass1.$SwitchMap$org$jooq$SQLDialect
            r1 = r4
            org.jooq.SQLDialect r1 = r1.family()
            int r1 = r1.ordinal()
            r0 = r0[r1]
            switch(r0) {
                default: goto L18;
            }
        L18:
            r0 = r4
            r1 = r3
            org.jooq.Name r1 = r1.getUnqualifiedName()
            org.jooq.Context r0 = r0.visit(r1)
            r1 = 40
            org.jooq.Context r0 = r0.sql(r1)
            r1 = r3
            org.jooq.Field<T> r1 = r1.field
            org.jooq.Context r0 = r0.visit(r1)
            r0 = r3
            org.jooq.Field<java.lang.Integer> r0 = r0.offset
            if (r0 == 0) goto L62
            int[] r0 = org.jooq.impl.AbstractLeadLag.AnonymousClass1.$SwitchMap$org$jooq$SQLDialect
            r1 = r4
            org.jooq.SQLDialect r1 = r1.family()
            int r1 = r1.ordinal()
            r0 = r0[r1]
            switch(r0) {
                default: goto L50;
            }
        L50:
            r0 = r4
            java.lang.String r1 = ", "
            org.jooq.Context r0 = r0.sql(r1)
            r1 = r3
            org.jooq.Field<java.lang.Integer> r1 = r1.offset
            org.jooq.Context r0 = r0.visit(r1)
        L62:
            r0 = r3
            org.jooq.Field<T> r0 = r0.defaultValue
            if (r0 == 0) goto L92
            int[] r0 = org.jooq.impl.AbstractLeadLag.AnonymousClass1.$SwitchMap$org$jooq$SQLDialect
            r1 = r4
            org.jooq.SQLDialect r1 = r1.family()
            int r1 = r1.ordinal()
            r0 = r0[r1]
            switch(r0) {
                default: goto L80;
            }
        L80:
            r0 = r4
            java.lang.String r1 = ", "
            org.jooq.Context r0 = r0.sql(r1)
            r1 = r3
            org.jooq.Field<T> r1 = r1.defaultValue
            org.jooq.Context r0 = r0.visit(r1)
        L92:
            r0 = r4
            r1 = 41
            org.jooq.Context r0 = r0.sql(r1)
            r0 = r3
            r1 = r4
            r0.acceptNullTreatment(r1)
            r0 = r3
            r1 = r4
            r0.acceptOverClause(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jooq.impl.AbstractLeadLag.accept0(org.jooq.Context):void");
    }

    public final Field<T> $field() {
        return this.field;
    }

    public final Field<Integer> $offset() {
        return this.offset;
    }

    public final Field<T> $defaultValue() {
        return this.defaultValue;
    }
}
