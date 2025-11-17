package org.jooq.impl;

import org.jooq.CharacterSet;
import org.jooq.Collation;
import org.jooq.Comment;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Generator;
import org.jooq.Name;
import org.jooq.Nullability;
import org.jooq.impl.QOM;
import org.jooq.tools.JooqLogger;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractDataTypeX.class */
public abstract class AbstractDataTypeX<T> extends AbstractDataType<T> {
    private static final JooqLogger logGeneratedAlwaysAs = JooqLogger.getLogger(AbstractDataTypeX.class, "generateAlwaysAs", 1);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract AbstractDataTypeX<T> construct(Integer num, Integer num2, Integer num3, Nullability nullability, boolean z, Generator<?, ?, T> generator, QOM.GenerationOption generationOption, QOM.GenerationLocation generationLocation, Collation collation, CharacterSet characterSet, boolean z2, Field<T> field);

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractDataTypeX(Name name, Comment comment) {
        super(name, comment);
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final DataType<T> nullability(Nullability n) {
        return construct(precision0(), scale0(), length0(), n, readonly(), generatedAlwaysAsGenerator(), generationOption(), generationLocation(), collation(), characterSet(), !n.nullable() && identity(), defaultValue());
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final DataType<T> readonly(boolean r) {
        if (r && !Tools.CONFIG.get().commercial()) {
            logGeneratedAlwaysAs.info("Readonly columns", "Readonly columns are a commercial only jOOQ feature. If you wish to profit from this feature, please upgrade to the jOOQ Professional Edition");
        }
        return construct(precision0(), scale0(), length0(), nullability(), r, generatedAlwaysAsGenerator(), generationOption(), generationLocation(), collation(), characterSet(), identity(), defaultValue());
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final DataType<T> generatedAlwaysAs(Generator<?, ?, T> g) {
        if (g != null && !Tools.CONFIG.get().commercial()) {
            logGeneratedAlwaysAs.info("Computed columns", "Computed columns are a commercial only jOOQ feature. If you wish to profit from this feature, please upgrade to the jOOQ Professional Edition");
        }
        return construct(precision0(), scale0(), length0(), nullability(), g != null ? true : readonly(), g, generationOption(), generationLocation(), collation(), characterSet(), identity(), g != null ? null : defaultValue());
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final DataType<T> generationOption(QOM.GenerationOption g) {
        if (g != null && !Tools.CONFIG.get().commercial()) {
            logGeneratedAlwaysAs.info("Computed columns", "Computed columns are a commercial only jOOQ feature. If you wish to profit from this feature, please upgrade to the jOOQ Professional Edition");
        }
        return construct(precision0(), scale0(), length0(), nullability(), readonly(), generatedAlwaysAsGenerator(), g, generationLocation(), collation(), characterSet(), identity(), defaultValue());
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final DataType<T> generationLocation(QOM.GenerationLocation g) {
        if (g != null && !Tools.CONFIG.get().commercial()) {
            logGeneratedAlwaysAs.info("Computed columns", "Computed columns are a commercial only jOOQ feature. If you wish to profit from this feature, please upgrade to the jOOQ Professional Edition");
        }
        return construct(precision0(), scale0(), length0(), nullability(), readonly(), generatedAlwaysAsGenerator(), generationOption(), g, collation(), characterSet(), identity(), defaultValue());
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final DataType<T> collation(Collation c) {
        return construct(precision0(), scale0(), length0(), nullability(), readonly(), generatedAlwaysAsGenerator(), generationOption(), generationLocation(), c, characterSet(), identity(), defaultValue());
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final DataType<T> characterSet(CharacterSet c) {
        return construct(precision0(), scale0(), length0(), nullability(), readonly(), generatedAlwaysAsGenerator(), generationOption(), generationLocation(), collation(), c, identity(), defaultValue());
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final DataType<T> identity(boolean i) {
        return construct(precision0(), scale0(), length0(), i ? Nullability.NOT_NULL : nullability(), readonly(), generatedAlwaysAsGenerator(), generationOption(), generationLocation(), collation(), characterSet(), i, defaultValue());
    }

    @Override // org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final DataType<T> default_(Field<T> d) {
        return construct(precision0(), scale0(), length0(), nullability(), readonly(), d != null ? null : generatedAlwaysAsGenerator(), generationOption(), generationLocation(), collation(), characterSet(), identity(), d);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractDataType
    public final AbstractDataTypeX<T> precision1(Integer p, Integer s) {
        return construct(p, s, length0(), nullability(), readonly(), generatedAlwaysAsGenerator(), generationOption(), generationLocation(), collation(), characterSet(), identity(), defaultValue());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractDataType
    public final AbstractDataTypeX<T> scale1(Integer s) {
        return construct(precision0(), s, length0(), nullability(), readonly(), generatedAlwaysAsGenerator(), generationOption(), generationLocation(), collation(), characterSet(), identity(), defaultValue());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractDataType
    public final AbstractDataTypeX<T> length1(Integer l) {
        return construct(precision0(), scale0(), l, nullability(), readonly(), generatedAlwaysAsGenerator(), generationOption(), generationLocation(), collation(), characterSet(), identity(), defaultValue());
    }
}
