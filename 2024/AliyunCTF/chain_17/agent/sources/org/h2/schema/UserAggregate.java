package org.h2.schema;

import java.sql.Connection;
import java.sql.SQLException;
import org.h2.api.Aggregate;
import org.h2.api.AggregateFunction;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.util.JdbcUtils;
import org.h2.util.StringUtils;
import org.h2.value.DataType;
import org.h2.value.TypeInfo;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/schema/UserAggregate.class */
public final class UserAggregate extends UserDefinedFunction {
    private Class<?> javaClass;

    public UserAggregate(Schema schema, int i, String str, String str2, boolean z) {
        super(schema, i, str, 3);
        this.className = str2;
        if (!z) {
            getInstance();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v13, types: [org.h2.api.Aggregate] */
    public Aggregate getInstance() {
        AggregateWrapper aggregateWrapper;
        if (this.javaClass == null) {
            this.javaClass = JdbcUtils.loadUserClass(this.className);
        }
        try {
            Object newInstance = this.javaClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            if (newInstance instanceof Aggregate) {
                aggregateWrapper = (Aggregate) newInstance;
            } else {
                aggregateWrapper = new AggregateWrapper((AggregateFunction) newInstance);
            }
            return aggregateWrapper;
        } catch (Exception e) {
            throw DbException.convert(e);
        }
    }

    @Override // org.h2.engine.DbObject
    public String getDropSQL() {
        return getSQL(new StringBuilder("DROP AGGREGATE IF EXISTS "), 0).toString();
    }

    @Override // org.h2.engine.DbObject
    public String getCreateSQL() {
        StringBuilder sb = new StringBuilder("CREATE FORCE AGGREGATE ");
        getSQL(sb, 0).append(" FOR ");
        return StringUtils.quoteStringSQL(sb, this.className).toString();
    }

    @Override // org.h2.engine.DbObject
    public int getType() {
        return 14;
    }

    @Override // org.h2.engine.DbObject
    public synchronized void removeChildrenAndResources(SessionLocal sessionLocal) {
        this.database.removeMeta(sessionLocal, getId());
        this.className = null;
        this.javaClass = null;
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/schema/UserAggregate$AggregateWrapper.class */
    public static class AggregateWrapper implements Aggregate {
        private final AggregateFunction aggregateFunction;

        AggregateWrapper(AggregateFunction aggregateFunction) {
            this.aggregateFunction = aggregateFunction;
        }

        @Override // org.h2.api.Aggregate
        public void init(Connection connection) throws SQLException {
            this.aggregateFunction.init(connection);
        }

        @Override // org.h2.api.Aggregate
        public int getInternalType(int[] iArr) throws SQLException {
            int[] iArr2 = new int[iArr.length];
            for (int i = 0; i < iArr.length; i++) {
                iArr2[i] = DataType.convertTypeToSQLType(TypeInfo.getTypeInfo(iArr[i]));
            }
            return DataType.convertSQLTypeToValueType(this.aggregateFunction.getType(iArr2));
        }

        @Override // org.h2.api.Aggregate
        public void add(Object obj) throws SQLException {
            this.aggregateFunction.add(obj);
        }

        @Override // org.h2.api.Aggregate
        public Object getResult() throws SQLException {
            return this.aggregateFunction.getResult();
        }
    }
}
