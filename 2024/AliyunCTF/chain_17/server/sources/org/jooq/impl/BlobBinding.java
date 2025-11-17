package org.jooq.impl;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.Set;
import org.jooq.Binding;
import org.jooq.BindingGetResultSetContext;
import org.jooq.BindingGetSQLInputContext;
import org.jooq.BindingGetStatementContext;
import org.jooq.BindingRegisterContext;
import org.jooq.BindingSQLContext;
import org.jooq.BindingSetSQLOutputContext;
import org.jooq.BindingSetStatementContext;
import org.jooq.Converter;
import org.jooq.Converters;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.impl.R2DBC;
import org.jooq.tools.jdbc.JDBCUtils;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/BlobBinding.class */
public class BlobBinding implements Binding<byte[], byte[]> {
    static final Set<SQLDialect> NO_SUPPORT_LOBS = SQLDialect.supportedBy(SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB);

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.jooq.impl.BlobBinding$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/BlobBinding$1.class */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    @Override // org.jooq.Binding
    public final Converter<byte[], byte[]> converter() {
        return Converters.identity(byte[].class);
    }

    @Override // org.jooq.Binding
    public final void sql(BindingSQLContext<byte[]> ctx) throws SQLException {
        if (ctx.render().paramType() == ParamType.INLINED) {
            ctx.render().visit((Field<?>) DSL.inline(ctx.convert(converter()).value(), SQLDataType.BLOB));
        } else {
            ctx.render().sql(ctx.variable());
        }
    }

    @Override // org.jooq.Binding
    public final void register(BindingRegisterContext<byte[]> ctx) throws SQLException {
        if (!NO_SUPPORT_LOBS.contains(ctx.dialect())) {
            ctx.statement().registerOutParameter(ctx.index(), 2004);
        } else {
            ctx.statement().registerOutParameter(ctx.index(), -2);
        }
    }

    @Override // org.jooq.Binding
    public final void set(BindingSetStatementContext<byte[]> ctx) throws SQLException {
        if (ctx.statement() instanceof R2DBC.R2DBCPreparedStatement) {
            ctx.statement().setBytes(ctx.index(), ctx.value());
            return;
        }
        if (!NO_SUPPORT_LOBS.contains(ctx.dialect())) {
            Blob blob = newBlob(ctx, ctx.value(), ctx.statement().getConnection());
            if (blob == null && ClobBinding.NO_SUPPORT_NULL_LOBS.contains(ctx.dialect())) {
                ctx.statement().setNull(ctx.index(), 2004);
                return;
            } else {
                ctx.statement().setBlob(ctx.index(), blob);
                return;
            }
        }
        ctx.statement().setBytes(ctx.index(), ctx.value());
    }

    @Override // org.jooq.Binding
    public final void set(BindingSetSQLOutputContext<byte[]> ctx) throws SQLException {
        if (!NO_SUPPORT_LOBS.contains(ctx.dialect())) {
            ctx.output().writeBlob(newBlob(ctx, ctx.value(), null));
        } else {
            ctx.output().writeBytes(ctx.value());
        }
    }

    @Override // org.jooq.Binding
    public final void get(BindingGetResultSetContext<byte[]> ctx) throws SQLException {
        byte[] bytes;
        if (ctx.resultSet() instanceof R2DBC.R2DBCResultSet) {
            ctx.value(ctx.resultSet().getBytes(ctx.index()));
            return;
        }
        if (!NO_SUPPORT_LOBS.contains(ctx.dialect())) {
            Blob blob = ctx.resultSet().getBlob(ctx.index());
            if (blob == null) {
                bytes = null;
            } else {
                try {
                    bytes = blob.getBytes(1L, Tools.asInt(blob.length()));
                } finally {
                    JDBCUtils.safeFree(blob);
                }
            }
            ctx.value(bytes);
            return;
        }
        ctx.value(ctx.resultSet().getBytes(ctx.index()));
    }

    @Override // org.jooq.Binding
    public final void get(BindingGetStatementContext<byte[]> ctx) throws SQLException {
        byte[] bytes;
        if (!NO_SUPPORT_LOBS.contains(ctx.dialect())) {
            Blob blob = ctx.statement().getBlob(ctx.index());
            if (blob == null) {
                bytes = null;
            } else {
                try {
                    bytes = blob.getBytes(1L, Tools.asInt(blob.length()));
                } finally {
                    JDBCUtils.safeFree(blob);
                }
            }
            ctx.value(bytes);
            return;
        }
        ctx.value(ctx.statement().getBytes(ctx.index()));
    }

    @Override // org.jooq.Binding
    public final void get(BindingGetSQLInputContext<byte[]> ctx) throws SQLException {
        byte[] bytes;
        if (!NO_SUPPORT_LOBS.contains(ctx.dialect())) {
            Blob blob = ctx.input().readBlob();
            if (blob == null) {
                bytes = null;
            } else {
                try {
                    bytes = blob.getBytes(1L, Tools.asInt(blob.length()));
                } finally {
                    JDBCUtils.safeFree(blob);
                }
            }
            ctx.value(bytes);
            return;
        }
        ctx.value(ctx.input().readBytes());
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x0027, code lost:            r0 = r0.createBlob();        r5.autoFree(r0);        r0.setBytes(1, r6);     */
    /* JADX WARN: Code restructure failed: missing block: B:11:0x003f, code lost:            return r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x0024, code lost:            r0 = org.jooq.impl.DefaultExecuteContext.localConnection();     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x001d, code lost:            if (r7 == null) goto L11;     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x0020, code lost:            r0 = r7;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    static final java.sql.Blob newBlob(org.jooq.ResourceManagingScope r5, byte[] r6, java.sql.Connection r7) throws java.sql.SQLException {
        /*
            r0 = r6
            if (r0 != 0) goto L6
            r0 = 0
            return r0
        L6:
            int[] r0 = org.jooq.impl.BlobBinding.AnonymousClass1.$SwitchMap$org$jooq$SQLDialect
            r1 = r5
            org.jooq.SQLDialect r1 = r1.dialect()
            int r1 = r1.ordinal()
            r0 = r0[r1]
            switch(r0) {
                default: goto L1c;
            }
        L1c:
            r0 = r7
            if (r0 == 0) goto L24
            r0 = r7
            goto L27
        L24:
            java.sql.Connection r0 = org.jooq.impl.DefaultExecuteContext.localConnection()
        L27:
            java.sql.Blob r0 = r0.createBlob()
            r8 = r0
            r0 = r5
            r1 = r8
            java.sql.Blob r0 = r0.autoFree(r1)
            r0 = r8
            r1 = 1
            r2 = r6
            int r0 = r0.setBytes(r1, r2)
            r0 = r8
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jooq.impl.BlobBinding.newBlob(org.jooq.ResourceManagingScope, byte[], java.sql.Connection):java.sql.Blob");
    }
}
