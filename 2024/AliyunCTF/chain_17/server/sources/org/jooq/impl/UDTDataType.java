package org.jooq.impl;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import java.lang.invoke.SerializedLambda;
import java.util.Objects;
import java.util.function.Supplier;
import org.jooq.Record;
import org.jooq.Row;
import org.jooq.SQLDialect;
import org.jooq.UDT;
import org.jooq.UDTRecord;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/UDTDataType.class */
public final class UDTDataType<R extends UDTRecord<R>> extends DefaultDataType<R> {
    final UDT<R> udt;

    private static /* synthetic */ Object $deserializeLambda$(SerializedLambda lambda) {
        String implMethodName = lambda.getImplMethodName();
        boolean z = -1;
        switch (implMethodName.hashCode()) {
            case 1718632305:
                if (implMethodName.equals("lambda$new$db692576$1")) {
                    z = false;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("org/jooq/impl/LazySupplier") && lambda.getFunctionalInterfaceMethodName().equals(BeanUtil.PREFIX_GETTER_GET) && lambda.getFunctionalInterfaceMethodSignature().equals("()Ljava/lang/Object;") && lambda.getImplClass().equals("org/jooq/impl/UDTDataType") && lambda.getImplMethodSignature().equals("(Lorg/jooq/UDT;)Lorg/jooq/Name;")) {
                    UDT udt = (UDT) lambda.getCapturedArg(0);
                    return () -> {
                        Objects.requireNonNull(udt);
                        Supplier supplier = udt::getSchema;
                        Objects.requireNonNull(udt);
                        return lazyName(supplier, udt::getUnqualifiedName);
                    };
                }
                break;
        }
        throw new IllegalArgumentException("Invalid lambda deserialization");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public UDTDataType(UDT<R> udt) {
        super(SQLDialect.DEFAULT, udt.getRecordType(), new LazyName(() -> {
            Objects.requireNonNull(udt);
            Supplier supplier = udt::getSchema;
            Objects.requireNonNull(udt);
            return lazyName(supplier, udt::getUnqualifiedName);
        }));
        this.udt = udt;
    }

    @Override // org.jooq.impl.DefaultDataType, org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final Row getRow() {
        return this.udt.fieldsRow();
    }

    @Override // org.jooq.impl.DefaultDataType, org.jooq.impl.AbstractDataType, org.jooq.DataType
    public final Class<? extends Record> getRecordType() {
        return this.udt.getRecordType();
    }
}
