package org.h2.expression;

import org.h2.api.ErrorCode;
import org.h2.command.query.Select;
import org.h2.command.query.SelectGroups;
import org.h2.command.query.SelectListColumnResolver;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.expression.condition.Comparison;
import org.h2.index.IndexCondition;
import org.h2.message.DbException;
import org.h2.mode.ModeFunction;
import org.h2.schema.Constant;
import org.h2.schema.Schema;
import org.h2.table.Column;
import org.h2.table.ColumnResolver;
import org.h2.table.Table;
import org.h2.table.TableFilter;
import org.h2.util.ParserUtil;
import org.h2.util.StringUtils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBigint;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueDecfloat;
import org.h2.value.ValueDouble;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNumeric;
import org.h2.value.ValueReal;
import org.h2.value.ValueSmallint;
import org.h2.value.ValueTinyint;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/ExpressionColumn.class */
public final class ExpressionColumn extends Expression {
    private final Database database;
    private final String schemaName;
    private final String tableAlias;
    private final String columnName;
    private final boolean rowId;
    private final boolean quotedName;
    private ColumnResolver columnResolver;
    private int queryLevel;
    private Column column;

    public ExpressionColumn(Database database, Column column) {
        this.database = database;
        this.column = column;
        this.schemaName = null;
        this.tableAlias = null;
        this.columnName = null;
        this.rowId = column.isRowId();
        this.quotedName = true;
    }

    public ExpressionColumn(Database database, String str, String str2, String str3) {
        this(database, str, str2, str3, true);
    }

    public ExpressionColumn(Database database, String str, String str2, String str3, boolean z) {
        this.database = database;
        this.schemaName = str;
        this.tableAlias = str2;
        this.columnName = str3;
        this.rowId = false;
        this.quotedName = z;
    }

    public ExpressionColumn(Database database, String str, String str2) {
        this.database = database;
        this.schemaName = str;
        this.tableAlias = str2;
        this.columnName = Column.ROWID;
        this.rowId = true;
        this.quotedName = true;
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        if (this.schemaName != null) {
            ParserUtil.quoteIdentifier(sb, this.schemaName, i).append('.');
        }
        if (this.tableAlias != null) {
            ParserUtil.quoteIdentifier(sb, this.tableAlias, i).append('.');
        }
        if (this.column != null) {
            if (this.columnResolver != null && this.columnResolver.hasDerivedColumnList()) {
                ParserUtil.quoteIdentifier(sb, this.columnResolver.getColumnName(this.column), i);
            } else {
                this.column.getSQL(sb, i);
            }
        } else if (this.rowId) {
            sb.append(this.columnName);
        } else {
            ParserUtil.quoteIdentifier(sb, this.columnName, i);
        }
        return sb;
    }

    public TableFilter getTableFilter() {
        if (this.columnResolver == null) {
            return null;
        }
        return this.columnResolver.getTableFilter();
    }

    @Override // org.h2.expression.Expression
    public void mapColumns(ColumnResolver columnResolver, int i, int i2) {
        if (this.tableAlias != null && !this.database.equalsIdentifiers(this.tableAlias, columnResolver.getTableAlias())) {
            return;
        }
        if (this.schemaName != null && !this.database.equalsIdentifiers(this.schemaName, columnResolver.getSchemaName())) {
            return;
        }
        if (this.rowId) {
            Column rowIdColumn = columnResolver.getRowIdColumn();
            if (rowIdColumn != null) {
                mapColumn(columnResolver, rowIdColumn, i);
                return;
            }
            return;
        }
        Column findColumn = columnResolver.findColumn(this.columnName);
        if (findColumn != null) {
            mapColumn(columnResolver, findColumn, i);
            return;
        }
        Column[] systemColumns = columnResolver.getSystemColumns();
        for (int i3 = 0; systemColumns != null && i3 < systemColumns.length; i3++) {
            Column column = systemColumns[i3];
            if (this.database.equalsIdentifiers(this.columnName, column.getName())) {
                mapColumn(columnResolver, column, i);
                return;
            }
        }
    }

    private void mapColumn(ColumnResolver columnResolver, Column column, int i) {
        if (this.columnResolver == null) {
            this.queryLevel = i;
            this.column = column;
            this.columnResolver = columnResolver;
        } else if (this.queryLevel == i && this.columnResolver != columnResolver && !(columnResolver instanceof SelectListColumnResolver)) {
            throw DbException.get(ErrorCode.AMBIGUOUS_COLUMN_NAME_1, this.columnName);
        }
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        Constant findConstant;
        if (this.columnResolver == null) {
            Schema findSchema = sessionLocal.getDatabase().findSchema(this.tableAlias == null ? sessionLocal.getCurrentSchemaName() : this.tableAlias);
            if (findSchema != null && (findConstant = findSchema.findConstant(this.columnName)) != null) {
                return findConstant.getValue();
            }
            return optimizeOther();
        }
        return this.columnResolver.optimize(this, this.column);
    }

    private Expression optimizeOther() {
        Expression compatibilityDateTimeValueFunction;
        if (this.tableAlias == null && !this.quotedName && (compatibilityDateTimeValueFunction = ModeFunction.getCompatibilityDateTimeValueFunction(this.database, StringUtils.toUpperEnglish(this.columnName), -1)) != null) {
            return compatibilityDateTimeValueFunction;
        }
        throw getColumnException(ErrorCode.COLUMN_NOT_FOUND_1);
    }

    public DbException getColumnException(int i) {
        String str = this.columnName;
        if (this.tableAlias != null) {
            if (this.schemaName != null) {
                str = this.schemaName + '.' + this.tableAlias + '.' + str;
            } else {
                str = this.tableAlias + '.' + str;
            }
        }
        return DbException.get(i, str);
    }

    @Override // org.h2.expression.Expression
    public void updateAggregate(SessionLocal sessionLocal, int i) {
        SelectGroups groupDataIfCurrent;
        Select select = this.columnResolver.getSelect();
        if (select == null) {
            throw DbException.get(ErrorCode.MUST_GROUP_BY_COLUMN_1, getTraceSQL());
        }
        if (i == 0 || (groupDataIfCurrent = select.getGroupDataIfCurrent(false)) == null) {
            return;
        }
        Value value = (Value) groupDataIfCurrent.getCurrentGroupExprData(this);
        if (value == null) {
            groupDataIfCurrent.setCurrentGroupExprData(this, this.columnResolver.getValue(this.column));
        } else if (!select.isGroupWindowStage2() && !sessionLocal.areEqual(this.columnResolver.getValue(this.column), value)) {
            throw DbException.get(ErrorCode.MUST_GROUP_BY_COLUMN_1, getTraceSQL());
        }
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        SelectGroups groupDataIfCurrent;
        Select select = this.columnResolver.getSelect();
        if (select != null && (groupDataIfCurrent = select.getGroupDataIfCurrent(false)) != null) {
            Value value = (Value) groupDataIfCurrent.getCurrentGroupExprData(this);
            if (value != null) {
                return value;
            }
            if (select.isGroupWindowStage2()) {
                throw DbException.get(ErrorCode.MUST_GROUP_BY_COLUMN_1, getTraceSQL());
            }
        }
        Value value2 = this.columnResolver.getValue(this.column);
        if (value2 == null) {
            if (select == null) {
                throw DbException.get(ErrorCode.NULL_NOT_ALLOWED, getTraceSQL());
            }
            throw DbException.get(ErrorCode.MUST_GROUP_BY_COLUMN_1, getTraceSQL());
        }
        return value2;
    }

    @Override // org.h2.expression.Expression, org.h2.value.Typed
    public TypeInfo getType() {
        return this.column != null ? this.column.getType() : this.rowId ? TypeInfo.TYPE_BIGINT : TypeInfo.TYPE_UNKNOWN;
    }

    @Override // org.h2.expression.Expression
    public void setEvaluatable(TableFilter tableFilter, boolean z) {
    }

    public Column getColumn() {
        return this.column;
    }

    public String getOriginalColumnName() {
        return this.columnName;
    }

    public String getOriginalTableAliasName() {
        return this.tableAlias;
    }

    @Override // org.h2.expression.Expression
    public String getColumnName(SessionLocal sessionLocal, int i) {
        if (this.column != null) {
            if (this.columnResolver != null) {
                return this.columnResolver.getColumnName(this.column);
            }
            return this.column.getName();
        }
        return this.columnName;
    }

    @Override // org.h2.expression.Expression
    public String getSchemaName() {
        Table table = this.column.getTable();
        if (table == null) {
            return null;
        }
        return table.getSchema().getName();
    }

    @Override // org.h2.expression.Expression
    public String getTableName() {
        Table table = this.column.getTable();
        if (table == null) {
            return null;
        }
        return table.getName();
    }

    @Override // org.h2.expression.Expression
    public String getAlias(SessionLocal sessionLocal, int i) {
        if (this.column != null) {
            if (this.columnResolver != null) {
                return this.columnResolver.getColumnName(this.column);
            }
            return this.column.getName();
        }
        if (this.tableAlias != null) {
            return this.tableAlias + '.' + this.columnName;
        }
        return this.columnName;
    }

    @Override // org.h2.expression.Expression
    public String getColumnNameForView(SessionLocal sessionLocal, int i) {
        return getAlias(sessionLocal, i);
    }

    @Override // org.h2.expression.Expression
    public boolean isIdentity() {
        return this.column.isIdentity();
    }

    @Override // org.h2.expression.Expression
    public int getNullable() {
        return this.column.isNullable() ? 1 : 0;
    }

    @Override // org.h2.expression.Expression
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        switch (expressionVisitor.getType()) {
            case 0:
                return this.queryLevel < expressionVisitor.getQueryLevel();
            case 1:
                return false;
            case 2:
            case 5:
            case 8:
            default:
                return true;
            case 3:
                if (expressionVisitor.getQueryLevel() < this.queryLevel) {
                    return true;
                }
                if (getTableFilter() == null) {
                    return false;
                }
                return getTableFilter().isEvaluatable();
            case 4:
                expressionVisitor.addDataModificationId(this.column.getTable().getMaxDataModificationId());
                return true;
            case 6:
                return this.columnResolver != expressionVisitor.getResolver();
            case 7:
                if (this.column != null) {
                    expressionVisitor.addDependency(this.column.getTable());
                    return true;
                }
                return true;
            case 9:
                if (this.column == null) {
                    throw DbException.get(ErrorCode.COLUMN_NOT_FOUND_1, getTraceSQL());
                }
                expressionVisitor.addColumn1(this.column);
                return true;
            case 10:
                if (this.column == null) {
                    throw DbException.get(ErrorCode.COLUMN_NOT_FOUND_1, getTraceSQL());
                }
                expressionVisitor.addColumn2(this.column);
                return true;
            case 11:
                if (this.column == null) {
                    throw DbException.get(ErrorCode.COLUMN_NOT_FOUND_1, getTraceSQL());
                }
                if (expressionVisitor.getColumnResolvers().contains(this.columnResolver)) {
                    if (expressionVisitor.getQueryLevel() <= 0) {
                        return this.queryLevel > 0;
                    }
                    if (this.queryLevel > 0) {
                        this.queryLevel--;
                        return true;
                    }
                    throw DbException.getInternalError("queryLevel=0");
                }
                return true;
        }
    }

    @Override // org.h2.expression.Expression
    public int getCost() {
        return 2;
    }

    @Override // org.h2.expression.Expression
    public void createIndexConditions(SessionLocal sessionLocal, TableFilter tableFilter) {
        if (tableFilter == getTableFilter() && this.column.getType().getValueType() == 8) {
            tableFilter.addIndexCondition(IndexCondition.get(0, this, ValueExpression.TRUE));
        }
    }

    @Override // org.h2.expression.Expression
    public Expression getNotIfPossible(SessionLocal sessionLocal) {
        Value value;
        Expression optimize = optimize(sessionLocal);
        if (optimize != this) {
            return optimize.getNotIfPossible(sessionLocal);
        }
        switch (this.column.getType().getValueType()) {
            case 8:
                value = ValueBoolean.FALSE;
                break;
            case 9:
                value = ValueTinyint.get((byte) 0);
                break;
            case 10:
                value = ValueSmallint.get((short) 0);
                break;
            case 11:
                value = ValueInteger.get(0);
                break;
            case 12:
                value = ValueBigint.get(0L);
                break;
            case 13:
                value = ValueNumeric.ZERO;
                break;
            case 14:
                value = ValueReal.ZERO;
                break;
            case 15:
                value = ValueDouble.ZERO;
                break;
            case 16:
                value = ValueDecfloat.ZERO;
                break;
            default:
                return null;
        }
        return new Comparison(0, this, ValueExpression.get(value), false);
    }
}
