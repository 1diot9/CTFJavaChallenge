package org.h2.expression.function;

import java.sql.SQLException;
import org.h2.api.ErrorCode;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.jdbc.JdbcConnection;
import org.h2.message.DbException;
import org.h2.tools.Csv;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueInteger;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/CSVWriteFunction.class */
public final class CSVWriteFunction extends FunctionN {
    public CSVWriteFunction() {
        super(new Expression[4]);
    }

    @Override // org.h2.expression.function.FunctionN, org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        String str;
        sessionLocal.getUser().checkAdmin();
        JdbcConnection createConnection = sessionLocal.createConnection(false);
        Csv csv = new Csv();
        String value = getValue(sessionLocal, 2);
        if (value != null && value.indexOf(61) >= 0) {
            str = csv.setOptions(value);
        } else {
            str = value;
            String value2 = getValue(sessionLocal, 3);
            String value3 = getValue(sessionLocal, 4);
            String value4 = getValue(sessionLocal, 5);
            String value5 = getValue(sessionLocal, 6);
            String value6 = getValue(sessionLocal, 7);
            setCsvDelimiterEscape(csv, value2, value3, value4);
            csv.setNullString(value5);
            if (value6 != null) {
                csv.setLineSeparator(value6);
            }
        }
        try {
            return ValueInteger.get(csv.write(createConnection, this.args[0].getValue(sessionLocal).getString(), this.args[1].getValue(sessionLocal).getString(), str));
        } catch (SQLException e) {
            throw DbException.convert(e);
        }
    }

    private String getValue(SessionLocal sessionLocal, int i) {
        if (i < this.args.length) {
            return this.args[i].getValue(sessionLocal).getString();
        }
        return null;
    }

    public static void setCsvDelimiterEscape(Csv csv, String str, String str2, String str3) {
        if (str != null) {
            csv.setFieldSeparatorWrite(str);
            if (!str.isEmpty()) {
                csv.setFieldSeparatorRead(str.charAt(0));
            }
        }
        if (str2 != null) {
            csv.setFieldDelimiter(str2.isEmpty() ? (char) 0 : str2.charAt(0));
        }
        if (str3 != null) {
            csv.setEscapeCharacter(str3.isEmpty() ? (char) 0 : str3.charAt(0));
        }
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        optimizeArguments(sessionLocal, false);
        int length = this.args.length;
        if (length < 2 || length > 8) {
            throw DbException.get(ErrorCode.INVALID_PARAMETER_COUNT_2, getName(), "2..8");
        }
        this.type = TypeInfo.TYPE_INTEGER;
        return this;
    }

    @Override // org.h2.expression.function.NamedExpression
    public String getName() {
        return "CSVWRITE";
    }

    @Override // org.h2.expression.OperationN, org.h2.expression.Expression
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        if (!super.isEverything(expressionVisitor)) {
            return false;
        }
        switch (expressionVisitor.getType()) {
            case 2:
            case 5:
            case 8:
                return false;
            default:
                return true;
        }
    }
}
