package org.h2.command.dml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import org.h2.command.Prepared;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.message.DbException;
import org.h2.result.LocalResult;
import org.h2.result.ResultInterface;
import org.h2.table.Column;
import org.h2.tools.Csv;
import org.h2.util.Utils;
import org.h2.value.TypeInfo;
import org.h2.value.ValueVarchar;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/dml/Help.class */
public class Help extends Prepared {
    private final String[] conditions;
    private final Expression[] expressions;

    public Help(SessionLocal sessionLocal, String[] strArr) {
        super(sessionLocal);
        this.conditions = strArr;
        Database database = getDatabase();
        this.expressions = new Expression[]{new ExpressionColumn(database, new Column("SECTION", TypeInfo.TYPE_VARCHAR)), new ExpressionColumn(database, new Column("TOPIC", TypeInfo.TYPE_VARCHAR)), new ExpressionColumn(database, new Column("SYNTAX", TypeInfo.TYPE_VARCHAR)), new ExpressionColumn(database, new Column("TEXT", TypeInfo.TYPE_VARCHAR))};
    }

    @Override // org.h2.command.Prepared
    public ResultInterface queryMeta() {
        LocalResult localResult = new LocalResult(this.session, this.expressions, 4, 4);
        localResult.done();
        return localResult;
    }

    @Override // org.h2.command.Prepared
    public ResultInterface query(long j) {
        LocalResult localResult = new LocalResult(this.session, this.expressions, 4, 4);
        try {
            ResultSet table = getTable();
            while (table.next()) {
                String trim = table.getString(2).trim();
                String[] strArr = this.conditions;
                int length = strArr.length;
                int i = 0;
                while (true) {
                    if (i < length) {
                        if (!trim.contains(strArr[i])) {
                            break;
                        }
                        i++;
                    } else {
                        localResult.addRow(ValueVarchar.get(table.getString(1).trim(), this.session), ValueVarchar.get(trim, this.session), ValueVarchar.get(stripAnnotationsFromSyntax(table.getString(3)), this.session), ValueVarchar.get(processHelpText(table.getString(4)), this.session));
                        break;
                    }
                }
            }
            localResult.done();
            return localResult;
        } catch (Exception e) {
            throw DbException.convert(e);
        }
    }

    public static String stripAnnotationsFromSyntax(String str) {
        return str.replaceAll("@c@ ", "").replaceAll("@h2@ ", "").replaceAll("@c@", "").replaceAll("@h2@", "").trim();
    }

    public static String processHelpText(String str) {
        int length = str.length();
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            char charAt = str.charAt(i);
            if (charAt == '.') {
                i++;
                break;
            }
            if (charAt != '\"') {
                i++;
            }
            do {
                i++;
                if (i < length) {
                }
                i++;
            } while (str.charAt(i) != '\"');
            i++;
        }
        return str.substring(0, i).trim();
    }

    public static ResultSet getTable() throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(new ByteArrayInputStream(Utils.getResource("/org/h2/res/help.csv")), StandardCharsets.UTF_8);
        Csv csv = new Csv();
        csv.setLineCommentCharacter('#');
        return csv.read(inputStreamReader, null);
    }

    @Override // org.h2.command.Prepared
    public boolean isQuery() {
        return true;
    }

    @Override // org.h2.command.Prepared
    public boolean isTransactional() {
        return true;
    }

    @Override // org.h2.command.Prepared
    public boolean isReadOnly() {
        return true;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 57;
    }

    @Override // org.h2.command.Prepared
    public boolean isCacheable() {
        return true;
    }
}
