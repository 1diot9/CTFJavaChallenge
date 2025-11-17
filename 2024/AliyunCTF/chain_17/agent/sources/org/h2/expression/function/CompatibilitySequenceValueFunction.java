package org.h2.expression.function;

import org.h2.command.Parser;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.expression.ExpressionVisitor;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.schema.Sequence;
import org.h2.util.StringUtils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/CompatibilitySequenceValueFunction.class */
public final class CompatibilitySequenceValueFunction extends Function1_2 {
    private final boolean current;

    public CompatibilitySequenceValueFunction(Expression expression, Expression expression2, boolean z) {
        super(expression, expression2);
        this.current = z;
    }

    @Override // org.h2.expression.function.Function1_2
    public Value getValue(SessionLocal sessionLocal, Value value, Value value2) {
        String string;
        String string2;
        if (value2 == null) {
            Parser parser = new Parser(sessionLocal);
            String string3 = value.getString();
            Expression parseExpression = parser.parseExpression(string3);
            if (parseExpression instanceof ExpressionColumn) {
                ExpressionColumn expressionColumn = (ExpressionColumn) parseExpression;
                string = expressionColumn.getOriginalTableAliasName();
                if (string == null) {
                    string = sessionLocal.getCurrentSchemaName();
                    string2 = string3;
                } else {
                    string2 = expressionColumn.getColumnName(sessionLocal, -1);
                }
            } else {
                throw DbException.getSyntaxError(string3, 1);
            }
        } else {
            string = value.getString();
            string2 = value2.getString();
        }
        Database database = sessionLocal.getDatabase();
        Schema findSchema = database.findSchema(string);
        if (findSchema == null) {
            findSchema = database.getSchema(StringUtils.toUpperEnglish(string));
        }
        Sequence findSequence = findSchema.findSequence(string2);
        if (findSequence == null) {
            findSequence = findSchema.getSequence(StringUtils.toUpperEnglish(string2));
        }
        return (this.current ? sessionLocal.getCurrentValueFor(findSequence) : sessionLocal.getNextValueFor(findSequence, null)).convertTo(this.type);
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        this.left = this.left.optimize(sessionLocal);
        if (this.right != null) {
            this.right = this.right.optimize(sessionLocal);
        }
        this.type = sessionLocal.getMode().decimalSequences ? TypeInfo.TYPE_NUMERIC_BIGINT : TypeInfo.TYPE_BIGINT;
        return this;
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0004. Please report as an issue. */
    @Override // org.h2.expression.Operation1_2, org.h2.expression.Expression
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        switch (expressionVisitor.getType()) {
            case 0:
            case 2:
            case 8:
                return false;
            case 5:
                if (!this.current) {
                    return false;
                }
            case 1:
            case 3:
            case 4:
            case 6:
            case 7:
            default:
                return super.isEverything(expressionVisitor);
        }
    }

    @Override // org.h2.expression.function.NamedExpression
    public String getName() {
        return this.current ? "CURRVAL" : "NEXTVAL";
    }
}
