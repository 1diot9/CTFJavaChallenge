package org.h2.jdbc;

import java.sql.ParameterMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import org.h2.command.CommandInterface;
import org.h2.expression.ParameterInterface;
import org.h2.message.DbException;
import org.h2.message.Trace;
import org.h2.message.TraceObject;
import org.h2.util.MathUtils;
import org.h2.value.DataType;
import org.h2.value.TypeInfo;
import org.h2.value.ValueToObjectConverter;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/jdbc/JdbcParameterMetaData.class */
public final class JdbcParameterMetaData extends TraceObject implements ParameterMetaData {
    private final JdbcPreparedStatement prep;
    private final int paramCount;
    private final ArrayList<? extends ParameterInterface> parameters;

    /* JADX INFO: Access modifiers changed from: package-private */
    public JdbcParameterMetaData(Trace trace, JdbcPreparedStatement jdbcPreparedStatement, CommandInterface commandInterface, int i) {
        setTrace(trace, 11, i);
        this.prep = jdbcPreparedStatement;
        this.parameters = commandInterface.getParameters();
        this.paramCount = this.parameters.size();
    }

    @Override // java.sql.ParameterMetaData
    public int getParameterCount() throws SQLException {
        try {
            debugCodeCall("getParameterCount");
            checkClosed();
            return this.paramCount;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ParameterMetaData
    public int getParameterMode(int i) throws SQLException {
        try {
            debugCodeCall("getParameterMode", i);
            getParameter(i);
            return 1;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ParameterMetaData
    public int getParameterType(int i) throws SQLException {
        try {
            debugCodeCall("getParameterType", i);
            TypeInfo type = getParameter(i).getType();
            if (type.getValueType() == -1) {
                type = TypeInfo.TYPE_VARCHAR;
            }
            return DataType.convertTypeToSQLType(type);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ParameterMetaData
    public int getPrecision(int i) throws SQLException {
        try {
            debugCodeCall("getPrecision", i);
            TypeInfo type = getParameter(i).getType();
            if (type.getValueType() == -1) {
                return 0;
            }
            return MathUtils.convertLongToInt(type.getPrecision());
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ParameterMetaData
    public int getScale(int i) throws SQLException {
        try {
            debugCodeCall("getScale", i);
            TypeInfo type = getParameter(i).getType();
            if (type.getValueType() == -1) {
                return 0;
            }
            return type.getScale();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ParameterMetaData
    public int isNullable(int i) throws SQLException {
        try {
            debugCodeCall("isNullable", i);
            return getParameter(i).getNullable();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ParameterMetaData
    public boolean isSigned(int i) throws SQLException {
        try {
            debugCodeCall("isSigned", i);
            getParameter(i);
            return true;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ParameterMetaData
    public String getParameterClassName(int i) throws SQLException {
        try {
            debugCodeCall("getParameterClassName", i);
            int valueType = getParameter(i).getType().getValueType();
            if (valueType == -1) {
                valueType = 2;
            }
            return ValueToObjectConverter.getDefaultClass(valueType, true).getName();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ParameterMetaData
    public String getParameterTypeName(int i) throws SQLException {
        try {
            debugCodeCall("getParameterTypeName", i);
            TypeInfo type = getParameter(i).getType();
            if (type.getValueType() == -1) {
                type = TypeInfo.TYPE_VARCHAR;
            }
            return type.getDeclaredTypeName();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    private ParameterInterface getParameter(int i) {
        checkClosed();
        if (i < 1 || i > this.paramCount) {
            throw DbException.getInvalidValueException("param", Integer.valueOf(i));
        }
        return this.parameters.get(i - 1);
    }

    private void checkClosed() {
        this.prep.checkClosed();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.sql.Wrapper
    public <T> T unwrap(Class<T> cls) throws SQLException {
        try {
            if (isWrapperFor(cls)) {
                return this;
            }
            throw DbException.getInvalidValueException("iface", cls);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Wrapper
    public boolean isWrapperFor(Class<?> cls) throws SQLException {
        return cls != null && cls.isAssignableFrom(getClass());
    }

    public String toString() {
        return getTraceObjectName() + ": parameterCount=" + this.paramCount;
    }
}
