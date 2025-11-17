package org.h2.expression.function.table;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.h2.api.ErrorCode;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.jdbc.JdbcConnection;
import org.h2.message.DbException;
import org.h2.result.ResultInterface;
import org.h2.result.SimpleResult;
import org.h2.util.JdbcUtils;
import org.h2.util.StringUtils;
import org.h2.value.TypeInfo;
import org.h2.value.ValueVarchar;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/table/LinkSchemaFunction.class */
public final class LinkSchemaFunction extends TableFunction {
    public LinkSchemaFunction() {
        super(new Expression[6]);
    }

    @Override // org.h2.expression.function.table.TableFunction
    public ResultInterface getValue(SessionLocal sessionLocal) {
        sessionLocal.getUser().checkAdmin();
        String value = getValue(sessionLocal, 0);
        String value2 = getValue(sessionLocal, 1);
        String value3 = getValue(sessionLocal, 2);
        String value4 = getValue(sessionLocal, 3);
        String value5 = getValue(sessionLocal, 4);
        String value6 = getValue(sessionLocal, 5);
        if (value == null || value2 == null || value3 == null || value4 == null || value5 == null || value6 == null) {
            return getValueTemplate(sessionLocal);
        }
        JdbcConnection createConnection = sessionLocal.createConnection(false);
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        SimpleResult simpleResult = new SimpleResult();
        simpleResult.addColumn("TABLE_NAME", TypeInfo.TYPE_VARCHAR);
        try {
            try {
                connection = JdbcUtils.getConnection(value2, value3, value4, value5);
                statement = createConnection.createStatement();
                statement.execute(StringUtils.quoteIdentifier(new StringBuilder("CREATE SCHEMA IF NOT EXISTS "), value).toString());
                if (value3.startsWith("jdbc:postgresql:")) {
                    resultSet = connection.getMetaData().getTables(null, value6, null, new String[]{"TABLE", "LINKED TABLE", "VIEW", "EXTERNAL"});
                } else {
                    resultSet = connection.getMetaData().getTables(null, value6, null, null);
                }
                while (resultSet.next()) {
                    String string = resultSet.getString("TABLE_NAME");
                    StringBuilder sb = new StringBuilder();
                    sb.append("DROP TABLE IF EXISTS ");
                    StringUtils.quoteIdentifier(sb, value).append('.');
                    StringUtils.quoteIdentifier(sb, string);
                    statement.execute(sb.toString());
                    sb.setLength(0);
                    sb.append("CREATE LINKED TABLE ");
                    StringUtils.quoteIdentifier(sb, value).append('.');
                    StringUtils.quoteIdentifier(sb, string).append('(');
                    StringUtils.quoteStringSQL(sb, value2).append(", ");
                    StringUtils.quoteStringSQL(sb, value3).append(", ");
                    StringUtils.quoteStringSQL(sb, value4).append(", ");
                    StringUtils.quoteStringSQL(sb, value5).append(", ");
                    StringUtils.quoteStringSQL(sb, value6).append(", ");
                    StringUtils.quoteStringSQL(sb, string).append(')');
                    statement.execute(sb.toString());
                    simpleResult.addRow(ValueVarchar.get(string, sessionLocal));
                }
                JdbcUtils.closeSilently(resultSet);
                JdbcUtils.closeSilently(connection);
                JdbcUtils.closeSilently(statement);
                return simpleResult;
            } catch (SQLException e) {
                simpleResult.close();
                throw DbException.convert(e);
            }
        } catch (Throwable th) {
            JdbcUtils.closeSilently(resultSet);
            JdbcUtils.closeSilently(connection);
            JdbcUtils.closeSilently(statement);
            throw th;
        }
    }

    private String getValue(SessionLocal sessionLocal, int i) {
        return this.args[i].getValue(sessionLocal).getString();
    }

    @Override // org.h2.expression.function.table.TableFunction
    public void optimize(SessionLocal sessionLocal) {
        super.optimize(sessionLocal);
        if (this.args.length != 6) {
            throw DbException.get(ErrorCode.INVALID_PARAMETER_COUNT_2, getName(), "6");
        }
    }

    @Override // org.h2.expression.function.table.TableFunction
    public ResultInterface getValueTemplate(SessionLocal sessionLocal) {
        SimpleResult simpleResult = new SimpleResult();
        simpleResult.addColumn("TABLE_NAME", TypeInfo.TYPE_VARCHAR);
        return simpleResult;
    }

    @Override // org.h2.expression.function.NamedExpression
    public String getName() {
        return "LINK_SCHEMA";
    }

    @Override // org.h2.expression.function.table.TableFunction
    public boolean isDeterministic() {
        return false;
    }
}
