package org.h2.table;

import java.util.Objects;
import org.h2.api.ErrorCode;
import org.h2.command.ParserBase;
import org.h2.command.ddl.SequenceOptions;
import org.h2.engine.CastDataProvider;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.ValueExpression;
import org.h2.message.DbException;
import org.h2.result.Row;
import org.h2.schema.Domain;
import org.h2.schema.Schema;
import org.h2.schema.Sequence;
import org.h2.util.HasSQL;
import org.h2.util.ParserUtil;
import org.h2.util.StringUtils;
import org.h2.value.TypeInfo;
import org.h2.value.Typed;
import org.h2.value.Value;
import org.h2.value.ValueNull;
import org.h2.value.ValueUuid;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/table/Column.class */
public final class Column implements HasSQL, Typed, ColumnTemplate {
    public static final String ROWID = "_ROWID_";
    public static final int NOT_NULLABLE = 0;
    public static final int NULLABLE = 1;
    public static final int NULLABLE_UNKNOWN = 2;
    private TypeInfo type;
    private Table table;
    private String name;
    private int columnId;
    private Expression defaultExpression;
    private Expression onUpdateExpression;
    private SequenceOptions identityOptions;
    private boolean defaultOnNull;
    private Sequence sequence;
    private boolean isGeneratedAlways;
    private GeneratedColumnResolver generatedTableFilter;
    private int selectivity;
    private String comment;
    private boolean primaryKey;
    private boolean rowId;
    private Domain domain;
    private boolean nullable = true;
    private boolean visible = true;

    public static StringBuilder writeColumns(StringBuilder sb, Column[] columnArr, int i) {
        int length = columnArr.length;
        for (int i2 = 0; i2 < length; i2++) {
            if (i2 > 0) {
                sb.append(", ");
            }
            columnArr[i2].getSQL(sb, i);
        }
        return sb;
    }

    public static StringBuilder writeColumns(StringBuilder sb, Column[] columnArr, String str, String str2, int i) {
        int length = columnArr.length;
        for (int i2 = 0; i2 < length; i2++) {
            if (i2 > 0) {
                sb.append(str);
            }
            columnArr[i2].getSQL(sb, i).append(str2);
        }
        return sb;
    }

    public Column(String str, TypeInfo typeInfo) {
        this.name = str;
        this.type = typeInfo;
    }

    public Column(String str, TypeInfo typeInfo, Table table, int i) {
        this.name = str;
        this.type = typeInfo;
        this.table = table;
        this.columnId = i;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Column)) {
            return false;
        }
        Column column = (Column) obj;
        if (this.table == null || column.table == null || this.name == null || column.name == null || this.table != column.table) {
            return false;
        }
        return this.name.equals(column.name);
    }

    public int hashCode() {
        if (this.table == null || this.name == null) {
            return 0;
        }
        return this.table.getId() ^ this.name.hashCode();
    }

    public Column getClone() {
        Column column = new Column(this.name, this.type);
        column.copy(this);
        return column;
    }

    public Value convert(CastDataProvider castDataProvider, Value value) {
        try {
            return value.convertTo(this.type, castDataProvider, this);
        } catch (DbException e) {
            e = e;
            if (e.getErrorCode() == 22018) {
                e = getDataConversionError(value, e);
            }
            throw e;
        }
    }

    public boolean isIdentity() {
        return (this.sequence == null && this.identityOptions == null) ? false : true;
    }

    public boolean isGenerated() {
        return this.isGeneratedAlways && this.defaultExpression != null;
    }

    public boolean isGeneratedAlways() {
        return this.isGeneratedAlways;
    }

    public void setGeneratedExpression(Expression expression) {
        this.isGeneratedAlways = true;
        this.defaultExpression = expression;
    }

    public void setTable(Table table, int i) {
        this.table = table;
        this.columnId = i;
    }

    public Table getTable() {
        return this.table;
    }

    @Override // org.h2.table.ColumnTemplate
    public void setDefaultExpression(SessionLocal sessionLocal, Expression expression) {
        if (expression != null) {
            expression = expression.optimize(sessionLocal);
            if (expression.isConstant()) {
                expression = ValueExpression.get(expression.getValue(sessionLocal));
            }
        }
        this.defaultExpression = expression;
        this.isGeneratedAlways = false;
    }

    @Override // org.h2.table.ColumnTemplate
    public void setOnUpdateExpression(SessionLocal sessionLocal, Expression expression) {
        if (expression != null) {
            expression = expression.optimize(sessionLocal);
            if (expression.isConstant()) {
                expression = ValueExpression.get(expression.getValue(sessionLocal));
            }
        }
        this.onUpdateExpression = expression;
    }

    public int getColumnId() {
        return this.columnId;
    }

    @Override // org.h2.util.HasSQL
    public String getSQL(int i) {
        return this.rowId ? this.name : ParserBase.quoteIdentifier(this.name, i);
    }

    @Override // org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        return this.rowId ? sb.append(this.name) : ParserUtil.quoteIdentifier(sb, this.name, i);
    }

    public StringBuilder getSQLWithTable(StringBuilder sb, int i) {
        return getSQL(this.table.getSQL(sb, i).append('.'), i);
    }

    public String getName() {
        return this.name;
    }

    @Override // org.h2.value.Typed
    public TypeInfo getType() {
        return this.type;
    }

    public void setNullable(boolean z) {
        this.nullable = z;
    }

    public boolean getVisible() {
        return this.visible;
    }

    public void setVisible(boolean z) {
        this.visible = z;
    }

    @Override // org.h2.table.ColumnTemplate
    public Domain getDomain() {
        return this.domain;
    }

    @Override // org.h2.table.ColumnTemplate
    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public boolean isRowId() {
        return this.rowId;
    }

    public void setRowId(boolean z) {
        this.rowId = z;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Removed duplicated region for block: B:10:0x0069  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public org.h2.value.Value validateConvertUpdateSequence(org.h2.engine.SessionLocal r6, org.h2.value.Value r7, org.h2.result.Row r8) {
        /*
            r5 = this;
            r0 = r7
            if (r0 != 0) goto L1f
            r0 = r5
            org.h2.schema.Sequence r0 = r0.sequence
            if (r0 == 0) goto L18
            r0 = r6
            r1 = r5
            org.h2.schema.Sequence r1 = r1.sequence
            r2 = 0
            org.h2.value.Value r0 = r0.getNextValueFor(r1, r2)
            r7 = r0
            goto L38
        L18:
            r0 = r5
            r1 = r6
            r2 = r8
            org.h2.value.Value r0 = r0.getDefaultOrGenerated(r1, r2)
            r7 = r0
        L1f:
            r0 = r7
            org.h2.value.ValueNull r1 = org.h2.value.ValueNull.INSTANCE
            if (r0 != r1) goto L38
            r0 = r5
            boolean r0 = r0.nullable
            if (r0 != 0) goto L38
            r0 = 23502(0x5bce, float:3.2933E-41)
            r1 = r5
            java.lang.String r1 = r1.name
            org.h2.message.DbException r0 = org.h2.message.DbException.get(r0, r1)
            throw r0
        L38:
            r0 = r7
            r1 = r5
            org.h2.value.TypeInfo r1 = r1.type     // Catch: org.h2.message.DbException -> L49
            r2 = r6
            r3 = r5
            java.lang.String r3 = r3.name     // Catch: org.h2.message.DbException -> L49
            org.h2.value.Value r0 = r0.convertForAssignTo(r1, r2, r3)     // Catch: org.h2.message.DbException -> L49
            r7 = r0
            goto L62
        L49:
            r9 = move-exception
            r0 = r9
            int r0 = r0.getErrorCode()
            r1 = 22018(0x5602, float:3.0854E-41)
            if (r0 != r1) goto L5f
            r0 = r5
            r1 = r7
            r2 = r9
            org.h2.message.DbException r0 = r0.getDataConversionError(r1, r2)
            r9 = r0
        L5f:
            r0 = r9
            throw r0
        L62:
            r0 = r5
            org.h2.schema.Domain r0 = r0.domain
            if (r0 == 0) goto L72
            r0 = r5
            org.h2.schema.Domain r0 = r0.domain
            r1 = r6
            r2 = r7
            r0.checkConstraints(r1, r2)
        L72:
            r0 = r5
            org.h2.schema.Sequence r0 = r0.sequence
            if (r0 == 0) goto L8c
            r0 = r6
            org.h2.engine.Mode r0 = r0.getMode()
            boolean r0 = r0.updateSequenceOnManualIdentityInsertion
            if (r0 == 0) goto L8c
            r0 = r5
            r1 = r6
            r2 = r7
            long r2 = r2.getLong()
            r0.updateSequenceIfRequired(r1, r2)
        L8c:
            r0 = r7
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.table.Column.validateConvertUpdateSequence(org.h2.engine.SessionLocal, org.h2.value.Value, org.h2.result.Row):org.h2.value.Value");
    }

    private Value getDefaultOrGenerated(SessionLocal sessionLocal, Row row) {
        Value value;
        Expression effectiveDefaultExpression = getEffectiveDefaultExpression();
        if (effectiveDefaultExpression == null) {
            value = ValueNull.INSTANCE;
        } else if (this.isGeneratedAlways) {
            synchronized (this) {
                this.generatedTableFilter.set(row);
                try {
                    value = effectiveDefaultExpression.getValue(sessionLocal);
                    this.generatedTableFilter.set(null);
                } catch (Throwable th) {
                    this.generatedTableFilter.set(null);
                    throw th;
                }
            }
        } else {
            value = effectiveDefaultExpression.getValue(sessionLocal);
        }
        return value;
    }

    private DbException getDataConversionError(Value value, DbException dbException) {
        StringBuilder append = new StringBuilder().append(value.getTraceSQL()).append(" (");
        if (this.table != null) {
            append.append(this.table.getName()).append(": ");
        }
        append.append(getCreateSQL()).append(')');
        return DbException.get(ErrorCode.DATA_CONVERSION_ERROR_1, dbException, append.toString());
    }

    private void updateSequenceIfRequired(SessionLocal sessionLocal, long j) {
        if (this.sequence.getCycle() == Sequence.Cycle.EXHAUSTED) {
            return;
        }
        long currentValue = this.sequence.getCurrentValue();
        long increment = this.sequence.getIncrement();
        if (increment > 0) {
            if (j < currentValue) {
                return;
            }
        } else if (j > currentValue) {
            return;
        }
        try {
            this.sequence.modify(Long.valueOf(j + increment), null, null, null, null, null, null);
            this.sequence.flush(sessionLocal);
        } catch (DbException e) {
            if (e.getErrorCode() == 90009) {
            } else {
                throw e;
            }
        }
    }

    public void initializeSequence(SessionLocal sessionLocal, Schema schema, int i, boolean z) {
        String str;
        if (this.identityOptions == null) {
            throw DbException.getInternalError();
        }
        do {
            str = "SYSTEM_SEQUENCE_" + StringUtils.toUpperEnglish(ValueUuid.getNewRandom().getString().replace('-', '_'));
        } while (schema.findSequence(str) != null);
        this.identityOptions.setDataType(this.type);
        Sequence sequence = new Sequence(sessionLocal, schema, i, str, this.identityOptions, true);
        sequence.setTemporary(z);
        sessionLocal.getDatabase().addSchemaObject(sessionLocal, sequence);
        setSequence(sequence, this.isGeneratedAlways);
    }

    @Override // org.h2.table.ColumnTemplate
    public void prepareExpressions(SessionLocal sessionLocal) {
        if (this.defaultExpression != null) {
            if (this.isGeneratedAlways) {
                this.generatedTableFilter = new GeneratedColumnResolver(this.table);
                this.defaultExpression.mapColumns(this.generatedTableFilter, 0, 0);
            }
            this.defaultExpression = this.defaultExpression.optimize(sessionLocal);
        }
        if (this.onUpdateExpression != null) {
            this.onUpdateExpression = this.onUpdateExpression.optimize(sessionLocal);
        }
        if (this.domain != null) {
            this.domain.prepareExpressions(sessionLocal);
        }
    }

    public String getCreateSQLWithoutName() {
        return getCreateSQL(new StringBuilder(), false);
    }

    public String getCreateSQL() {
        return getCreateSQL(false);
    }

    public String getCreateSQL(boolean z) {
        StringBuilder sb = new StringBuilder();
        if (this.name != null) {
            ParserUtil.quoteIdentifier(sb, this.name, 0).append(' ');
        }
        return getCreateSQL(sb, z);
    }

    private String getCreateSQL(StringBuilder sb, boolean z) {
        if (this.domain != null) {
            this.domain.getSQL(sb, 0);
        } else {
            this.type.getSQL(sb, 0);
        }
        if (!this.visible) {
            sb.append(" INVISIBLE ");
        }
        if (this.sequence != null) {
            sb.append(" GENERATED ").append(this.isGeneratedAlways ? "ALWAYS" : "BY DEFAULT").append(" AS IDENTITY");
            if (!z) {
                this.sequence.getSequenceOptionsSQL(sb.append('(')).append(')');
            }
        } else if (this.defaultExpression != null) {
            if (this.isGeneratedAlways) {
                this.defaultExpression.getEnclosedSQL(sb.append(" GENERATED ALWAYS AS "), 0);
            } else {
                this.defaultExpression.getUnenclosedSQL(sb.append(" DEFAULT "), 0);
            }
        }
        if (this.onUpdateExpression != null) {
            this.onUpdateExpression.getUnenclosedSQL(sb.append(" ON UPDATE "), 0);
        }
        if (this.defaultOnNull) {
            sb.append(" DEFAULT ON NULL");
        }
        if (z && this.sequence != null) {
            this.sequence.getSQL(sb.append(" SEQUENCE "), 0);
        }
        if (this.selectivity != 0) {
            sb.append(" SELECTIVITY ").append(this.selectivity);
        }
        if (this.comment != null) {
            StringUtils.quoteStringSQL(sb.append(" COMMENT "), this.comment);
        }
        if (!this.nullable) {
            sb.append(" NOT NULL");
        }
        return sb.toString();
    }

    public boolean isNullable() {
        return this.nullable;
    }

    @Override // org.h2.table.ColumnTemplate
    public Expression getDefaultExpression() {
        return this.defaultExpression;
    }

    @Override // org.h2.table.ColumnTemplate
    public Expression getEffectiveDefaultExpression() {
        if (this.sequence != null) {
            return null;
        }
        if (this.defaultExpression != null) {
            return this.defaultExpression;
        }
        if (this.domain != null) {
            return this.domain.getEffectiveDefaultExpression();
        }
        return null;
    }

    @Override // org.h2.table.ColumnTemplate
    public Expression getOnUpdateExpression() {
        return this.onUpdateExpression;
    }

    @Override // org.h2.table.ColumnTemplate
    public Expression getEffectiveOnUpdateExpression() {
        if (this.sequence != null || this.isGeneratedAlways) {
            return null;
        }
        if (this.onUpdateExpression != null) {
            return this.onUpdateExpression;
        }
        if (this.domain != null) {
            return this.domain.getEffectiveOnUpdateExpression();
        }
        return null;
    }

    public boolean hasIdentityOptions() {
        return this.identityOptions != null;
    }

    public void setIdentityOptions(SequenceOptions sequenceOptions, boolean z) {
        this.identityOptions = sequenceOptions;
        this.isGeneratedAlways = z;
        removeNonIdentityProperties();
    }

    private void removeNonIdentityProperties() {
        this.nullable = false;
        this.defaultExpression = null;
        this.onUpdateExpression = null;
    }

    public SequenceOptions getIdentityOptions() {
        return this.identityOptions;
    }

    public void setDefaultOnNull(boolean z) {
        this.defaultOnNull = z;
    }

    public boolean isDefaultOnNull() {
        return this.defaultOnNull;
    }

    public void rename(String str) {
        this.name = str;
    }

    public void setSequence(Sequence sequence, boolean z) {
        this.sequence = sequence;
        this.isGeneratedAlways = z;
        this.identityOptions = null;
        if (sequence != null) {
            removeNonIdentityProperties();
            if (sequence.getDatabase().getMode().identityColumnsHaveDefaultOnNull) {
                this.defaultOnNull = true;
            }
        }
    }

    public Sequence getSequence() {
        return this.sequence;
    }

    public int getSelectivity() {
        if (this.selectivity == 0) {
            return 50;
        }
        return this.selectivity;
    }

    public void setSelectivity(int i) {
        this.selectivity = i < 0 ? 0 : i > 100 ? 100 : i;
    }

    @Override // org.h2.table.ColumnTemplate
    public String getDefaultSQL() {
        if (this.defaultExpression == null) {
            return null;
        }
        return this.defaultExpression.getUnenclosedSQL(new StringBuilder(), 0).toString();
    }

    @Override // org.h2.table.ColumnTemplate
    public String getOnUpdateSQL() {
        if (this.onUpdateExpression == null) {
            return null;
        }
        return this.onUpdateExpression.getUnenclosedSQL(new StringBuilder(), 0).toString();
    }

    public void setComment(String str) {
        this.comment = (str == null || str.isEmpty()) ? null : str;
    }

    public String getComment() {
        return this.comment;
    }

    public void setPrimaryKey(boolean z) {
        this.primaryKey = z;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        if (expressionVisitor.getType() == 7 && this.sequence != null) {
            expressionVisitor.getDependencies().add(this.sequence);
        }
        Expression effectiveDefaultExpression = getEffectiveDefaultExpression();
        if (effectiveDefaultExpression != null && !effectiveDefaultExpression.isEverything(expressionVisitor)) {
            return false;
        }
        Expression effectiveOnUpdateExpression = getEffectiveOnUpdateExpression();
        if (effectiveOnUpdateExpression != null && !effectiveOnUpdateExpression.isEverything(expressionVisitor)) {
            return false;
        }
        return true;
    }

    public boolean isPrimaryKey() {
        return this.primaryKey;
    }

    public String toString() {
        return this.name;
    }

    public boolean isWideningConversion(Column column) {
        TypeInfo typeInfo = column.type;
        int valueType = this.type.getValueType();
        if (valueType != typeInfo.getValueType()) {
            return false;
        }
        long precision = this.type.getPrecision();
        long precision2 = typeInfo.getPrecision();
        if (precision > precision2) {
            return false;
        }
        if ((precision < precision2 && (valueType == 1 || valueType == 5)) || this.type.getScale() != typeInfo.getScale() || !Objects.equals(this.type.getExtTypeInfo(), typeInfo.getExtTypeInfo())) {
            return false;
        }
        if ((this.nullable && !column.nullable) || this.primaryKey != column.primaryKey || this.identityOptions != null || column.identityOptions != null || this.domain != column.domain || this.defaultExpression != null || column.defaultExpression != null || this.isGeneratedAlways || column.isGeneratedAlways || this.onUpdateExpression != null || column.onUpdateExpression != null) {
            return false;
        }
        return true;
    }

    public void copy(Column column) {
        this.name = column.name;
        this.type = column.type;
        this.domain = column.domain;
        this.nullable = column.nullable;
        this.defaultExpression = column.defaultExpression;
        this.onUpdateExpression = column.onUpdateExpression;
        this.defaultOnNull = column.defaultOnNull;
        this.sequence = column.sequence;
        this.comment = column.comment;
        this.generatedTableFilter = column.generatedTableFilter;
        this.isGeneratedAlways = column.isGeneratedAlways;
        this.selectivity = column.selectivity;
        this.primaryKey = column.primaryKey;
        this.visible = column.visible;
    }
}
