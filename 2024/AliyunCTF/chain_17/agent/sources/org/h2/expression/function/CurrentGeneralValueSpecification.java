package org.h2.expression.function;

import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.Operation0;
import org.h2.message.DbException;
import org.h2.util.ParserUtil;
import org.h2.util.StringUtils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueNull;
import org.h2.value.ValueVarchar;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/CurrentGeneralValueSpecification.class */
public final class CurrentGeneralValueSpecification extends Operation0 implements NamedExpression {
    public static final int CURRENT_CATALOG = 0;
    public static final int CURRENT_PATH = 1;
    public static final int CURRENT_ROLE = 2;
    public static final int CURRENT_SCHEMA = 3;
    public static final int CURRENT_USER = 4;
    public static final int SESSION_USER = 5;
    public static final int SYSTEM_USER = 6;
    private static final String[] NAMES = {"CURRENT_CATALOG", "CURRENT_PATH", "CURRENT_ROLE", "CURRENT_SCHEMA", "CURRENT_USER", "SESSION_USER", "SYSTEM_USER"};
    private final int specification;

    public CurrentGeneralValueSpecification(int i) {
        this.specification = i;
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        String name;
        switch (this.specification) {
            case 0:
                name = sessionLocal.getDatabase().getShortName();
                break;
            case 1:
                String[] schemaSearchPath = sessionLocal.getSchemaSearchPath();
                if (schemaSearchPath != null) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < schemaSearchPath.length; i++) {
                        if (i > 0) {
                            sb.append(',');
                        }
                        ParserUtil.quoteIdentifier(sb, schemaSearchPath[i], 0);
                    }
                    name = sb.toString();
                    break;
                } else {
                    name = "";
                    break;
                }
            case 2:
                Database database = sessionLocal.getDatabase();
                name = database.getPublicRole().getName();
                if (database.getSettings().databaseToLower) {
                    name = StringUtils.toLowerEnglish(name);
                    break;
                }
                break;
            case 3:
                name = sessionLocal.getCurrentSchemaName();
                break;
            case 4:
            case 5:
            case 6:
                name = sessionLocal.getUser().getName();
                if (sessionLocal.getDatabase().getSettings().databaseToLower) {
                    name = StringUtils.toLowerEnglish(name);
                    break;
                }
                break;
            default:
                throw DbException.getInternalError("specification=" + this.specification);
        }
        return name != null ? ValueVarchar.get(name, sessionLocal) : ValueNull.INSTANCE;
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        return sb.append(getName());
    }

    @Override // org.h2.expression.Expression
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        switch (expressionVisitor.getType()) {
            case 2:
                return false;
            default:
                return true;
        }
    }

    @Override // org.h2.expression.Expression, org.h2.value.Typed
    public TypeInfo getType() {
        return TypeInfo.TYPE_VARCHAR;
    }

    @Override // org.h2.expression.Expression
    public int getCost() {
        return 1;
    }

    @Override // org.h2.expression.function.NamedExpression
    public String getName() {
        return NAMES[this.specification];
    }
}
