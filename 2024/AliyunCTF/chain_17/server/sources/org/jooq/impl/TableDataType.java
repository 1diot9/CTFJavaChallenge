package org.jooq.impl;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import java.lang.invoke.SerializedLambda;
import java.util.Objects;
import java.util.function.Supplier;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.Table;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/TableDataType.class */
public final class TableDataType<R extends Record> extends DefaultDataType<R> {
    final Table<R> table;

    private static /* synthetic */ Object $deserializeLambda$(SerializedLambda lambda) {
        String implMethodName = lambda.getImplMethodName();
        boolean z = -1;
        switch (implMethodName.hashCode()) {
            case -1499469357:
                if (implMethodName.equals("lambda$new$fd1dc7bb$1")) {
                    z = false;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("org/jooq/impl/LazySupplier") && lambda.getFunctionalInterfaceMethodName().equals(BeanUtil.PREFIX_GETTER_GET) && lambda.getFunctionalInterfaceMethodSignature().equals("()Ljava/lang/Object;") && lambda.getImplClass().equals("org/jooq/impl/TableDataType") && lambda.getImplMethodSignature().equals("(Lorg/jooq/Table;)Lorg/jooq/Name;")) {
                    Table table = (Table) lambda.getCapturedArg(0);
                    return () -> {
                        Objects.requireNonNull(table);
                        Supplier supplier = table::getSchema;
                        Objects.requireNonNull(table);
                        return lazyName(supplier, table::getUnqualifiedName);
                    };
                }
                break;
        }
        throw new IllegalArgumentException("Invalid lambda deserialization");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TableDataType(Table<R> table) {
        super(SQLDialect.DEFAULT, table.getRecordType(), new LazyName(() -> {
            Objects.requireNonNull(table);
            Supplier supplier = table::getSchema;
            Objects.requireNonNull(table);
            return lazyName(supplier, table::getUnqualifiedName);
        }));
        this.table = table;
    }
}
