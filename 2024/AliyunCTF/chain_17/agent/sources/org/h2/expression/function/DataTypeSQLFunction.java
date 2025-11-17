package org.h2.expression.function;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.message.DbException;
import org.h2.schema.Constant;
import org.h2.schema.Domain;
import org.h2.schema.FunctionAlias;
import org.h2.schema.Schema;
import org.h2.table.Column;
import org.h2.table.Table;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueNull;
import org.h2.value.ValueToObjectConverter2;
import org.h2.value.ValueVarchar;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/DataTypeSQLFunction.class */
public final class DataTypeSQLFunction extends FunctionN {
    public DataTypeSQLFunction(Expression expression, Expression expression2, Expression expression3, Expression expression4) {
        super(new Expression[]{expression, expression2, expression3, expression4});
    }

    @Override // org.h2.expression.function.FunctionN
    public Value getValue(SessionLocal sessionLocal, Value value, Value value2, Value value3) {
        TypeInfo type;
        Schema findSchema = sessionLocal.getDatabase().findSchema(value.getString());
        if (findSchema == null) {
            return ValueNull.INSTANCE;
        }
        String string = value2.getString();
        String string2 = value3.getString();
        String string3 = this.args[3].getValue(sessionLocal).getString();
        if (string3 == null) {
            return ValueNull.INSTANCE;
        }
        boolean z = -1;
        switch (string2.hashCode()) {
            case 79578030:
                if (string2.equals("TABLE")) {
                    z = 3;
                    break;
                }
                break;
            case 214815652:
                if (string2.equals("CONSTANT")) {
                    z = false;
                    break;
                }
                break;
            case 2022099140:
                if (string2.equals("DOMAIN")) {
                    z = true;
                    break;
                }
                break;
            case 2103635108:
                if (string2.equals("ROUTINE")) {
                    z = 2;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                Constant findConstant = findSchema.findConstant(string);
                if (findConstant == null || !string3.equals("TYPE")) {
                    return ValueNull.INSTANCE;
                }
                type = findConstant.getValue().getType();
                break;
                break;
            case true:
                Domain findDomain = findSchema.findDomain(string);
                if (findDomain == null || !string3.equals("TYPE")) {
                    return ValueNull.INSTANCE;
                }
                type = findDomain.getDataType();
                break;
                break;
            case true:
                int lastIndexOf = string.lastIndexOf(95);
                if (lastIndexOf < 0) {
                    return ValueNull.INSTANCE;
                }
                FunctionAlias findFunction = findSchema.findFunction(string.substring(0, lastIndexOf));
                if (findFunction == null) {
                    return ValueNull.INSTANCE;
                }
                try {
                    int parseInt = Integer.parseInt(string.substring(lastIndexOf + 1));
                    try {
                        FunctionAlias.JavaMethod[] javaMethods = findFunction.getJavaMethods();
                        if (parseInt < 1 || parseInt > javaMethods.length) {
                            return ValueNull.INSTANCE;
                        }
                        FunctionAlias.JavaMethod javaMethod = javaMethods[parseInt - 1];
                        if (string3.equals("RESULT")) {
                            type = javaMethod.getDataType();
                            break;
                        } else {
                            try {
                                int parseInt2 = Integer.parseInt(string3);
                                if (parseInt2 < 1) {
                                    return ValueNull.INSTANCE;
                                }
                                if (!javaMethod.hasConnectionParam()) {
                                    parseInt2--;
                                }
                                Class<?>[] columnClasses = javaMethod.getColumnClasses();
                                if (parseInt2 >= columnClasses.length) {
                                    return ValueNull.INSTANCE;
                                }
                                type = ValueToObjectConverter2.classToType(columnClasses[parseInt2]);
                                break;
                            } catch (NumberFormatException e) {
                                return ValueNull.INSTANCE;
                            }
                        }
                    } catch (DbException e2) {
                        return ValueNull.INSTANCE;
                    }
                } catch (NumberFormatException e3) {
                    return ValueNull.INSTANCE;
                }
            case true:
                Table findTableOrView = findSchema.findTableOrView(sessionLocal, string);
                if (findTableOrView == null) {
                    return ValueNull.INSTANCE;
                }
                try {
                    int parseInt3 = Integer.parseInt(string3);
                    Column[] columns = findTableOrView.getColumns();
                    if (parseInt3 < 1 || parseInt3 > columns.length) {
                        return ValueNull.INSTANCE;
                    }
                    type = columns[parseInt3 - 1].getType();
                    break;
                } catch (NumberFormatException e4) {
                    return ValueNull.INSTANCE;
                }
                break;
            default:
                return ValueNull.INSTANCE;
        }
        return ValueVarchar.get(type.getSQL(0));
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        optimizeArguments(sessionLocal, false);
        this.type = TypeInfo.TYPE_VARCHAR;
        return this;
    }

    @Override // org.h2.expression.function.NamedExpression
    public String getName() {
        return "DATA_TYPE_SQL";
    }

    @Override // org.h2.expression.OperationN, org.h2.expression.Expression
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        switch (expressionVisitor.getType()) {
            case 2:
                return false;
            default:
                return true;
        }
    }
}
