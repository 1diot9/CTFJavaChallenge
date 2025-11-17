package org.h2.schema;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import org.h2.Driver;
import org.h2.api.ErrorCode;
import org.h2.engine.SessionLocal;
import org.h2.expression.Alias;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.jdbc.JdbcConnection;
import org.h2.message.DbException;
import org.h2.result.LocalResult;
import org.h2.result.ResultInterface;
import org.h2.table.Column;
import org.h2.util.JdbcUtils;
import org.h2.util.SourceCompiler;
import org.h2.util.StringUtils;
import org.h2.util.Utils;
import org.h2.value.DataType;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueNull;
import org.h2.value.ValueToObjectConverter;
import org.h2.value.ValueToObjectConverter2;
import org.springframework.util.ClassUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/schema/FunctionAlias.class */
public final class FunctionAlias extends UserDefinedFunction {
    private String methodName;
    private String source;
    private JavaMethod[] javaMethods;
    private boolean deterministic;

    private FunctionAlias(Schema schema, int i, String str) {
        super(schema, i, str, 3);
    }

    public static FunctionAlias newInstance(Schema schema, int i, String str, String str2, boolean z) {
        FunctionAlias functionAlias = new FunctionAlias(schema, i, str);
        int indexOf = str2.indexOf(40);
        int lastIndexOf = str2.lastIndexOf(46, indexOf < 0 ? str2.length() : indexOf);
        if (lastIndexOf < 0) {
            throw DbException.get(ErrorCode.SYNTAX_ERROR_1, str2);
        }
        functionAlias.className = str2.substring(0, lastIndexOf);
        functionAlias.methodName = str2.substring(lastIndexOf + 1);
        functionAlias.init(z);
        return functionAlias;
    }

    public static FunctionAlias newInstanceFromSource(Schema schema, int i, String str, String str2, boolean z) {
        FunctionAlias functionAlias = new FunctionAlias(schema, i, str);
        functionAlias.source = str2;
        functionAlias.init(z);
        return functionAlias;
    }

    private void init(boolean z) {
        try {
            load();
        } catch (DbException e) {
            if (!z) {
                throw e;
            }
        }
    }

    private synchronized void load() {
        if (this.javaMethods != null) {
            return;
        }
        if (this.source != null) {
            loadFromSource();
        } else {
            loadClass();
        }
    }

    private void loadFromSource() {
        SourceCompiler compiler = this.database.getCompiler();
        synchronized (compiler) {
            String str = "org.h2.dynamic." + getName();
            compiler.setSource(str, this.source);
            try {
                this.javaMethods = new JavaMethod[]{new JavaMethod(compiler.getMethod(str), 0)};
            } catch (DbException e) {
                throw e;
            } catch (Exception e2) {
                throw DbException.get(ErrorCode.SYNTAX_ERROR_1, e2, this.source);
            }
        }
    }

    private void loadClass() {
        Method[] methods = JdbcUtils.loadUserClass(this.className).getMethods();
        ArrayList arrayList = new ArrayList(1);
        int length = methods.length;
        for (int i = 0; i < length; i++) {
            Method method = methods[i];
            if (Modifier.isStatic(method.getModifiers()) && (method.getName().equals(this.methodName) || getMethodSignature(method).equals(this.methodName))) {
                JavaMethod javaMethod = new JavaMethod(method, i);
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    JavaMethod javaMethod2 = (JavaMethod) it.next();
                    if (javaMethod2.getParameterCount() == javaMethod.getParameterCount()) {
                        throw DbException.get(ErrorCode.METHODS_MUST_HAVE_DIFFERENT_PARAMETER_COUNTS_2, javaMethod2.toString(), javaMethod.toString());
                    }
                }
                arrayList.add(javaMethod);
            }
        }
        if (arrayList.isEmpty()) {
            throw DbException.get(ErrorCode.PUBLIC_STATIC_JAVA_METHOD_NOT_FOUND_1, this.methodName + " (" + this.className + ")");
        }
        this.javaMethods = (JavaMethod[]) arrayList.toArray(new JavaMethod[0]);
        Arrays.sort(this.javaMethods);
    }

    private static String getMethodSignature(Method method) {
        StringBuilder sb = new StringBuilder(method.getName());
        sb.append('(');
        Class<?>[] parameterTypes = method.getParameterTypes();
        int length = parameterTypes.length;
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            Class<?> cls = parameterTypes[i];
            if (cls.isArray()) {
                sb.append(cls.getComponentType().getName()).append(ClassUtils.ARRAY_SUFFIX);
            } else {
                sb.append(cls.getName());
            }
        }
        return sb.append(')').toString();
    }

    @Override // org.h2.engine.DbObject
    public String getDropSQL() {
        return getSQL(new StringBuilder("DROP ALIAS IF EXISTS "), 0).toString();
    }

    @Override // org.h2.engine.DbObject
    public String getCreateSQL() {
        StringBuilder sb = new StringBuilder("CREATE FORCE ALIAS ");
        getSQL(sb, 0);
        if (this.deterministic) {
            sb.append(" DETERMINISTIC");
        }
        if (this.source != null) {
            StringUtils.quoteStringSQL(sb.append(" AS "), this.source);
        } else {
            StringUtils.quoteStringSQL(sb.append(" FOR "), this.className + '.' + this.methodName);
        }
        return sb.toString();
    }

    @Override // org.h2.engine.DbObject
    public int getType() {
        return 9;
    }

    @Override // org.h2.engine.DbObject
    public synchronized void removeChildrenAndResources(SessionLocal sessionLocal) {
        this.database.removeMeta(sessionLocal, getId());
        this.className = null;
        this.methodName = null;
        this.javaMethods = null;
        invalidate();
    }

    public JavaMethod findJavaMethod(Expression[] expressionArr) {
        load();
        int length = expressionArr.length;
        for (JavaMethod javaMethod : this.javaMethods) {
            int parameterCount = javaMethod.getParameterCount();
            if (parameterCount == length || (javaMethod.isVarArgs() && parameterCount <= length + 1)) {
                return javaMethod;
            }
        }
        throw DbException.get(ErrorCode.METHOD_NOT_FOUND_1, getName() + " (" + this.className + ", parameter count: " + length + ")");
    }

    public String getJavaMethodName() {
        return this.methodName;
    }

    public JavaMethod[] getJavaMethods() {
        load();
        return this.javaMethods;
    }

    public void setDeterministic(boolean z) {
        this.deterministic = z;
    }

    public boolean isDeterministic() {
        return this.deterministic;
    }

    public String getSource() {
        return this.source;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/schema/FunctionAlias$JavaMethod.class */
    public static class JavaMethod implements Comparable<JavaMethod> {
        private final int id;
        private final Method method;
        private final TypeInfo dataType;
        private boolean hasConnectionParam;
        private boolean varArgs;
        private Class<?> varArgClass;
        private int paramCount;

        JavaMethod(Method method, int i) {
            this.method = method;
            this.id = i;
            Class<?>[] parameterTypes = method.getParameterTypes();
            this.paramCount = parameterTypes.length;
            if (this.paramCount > 0 && Connection.class.isAssignableFrom(parameterTypes[0])) {
                this.hasConnectionParam = true;
                this.paramCount--;
            }
            if (this.paramCount > 0) {
                Class<?> cls = parameterTypes[parameterTypes.length - 1];
                if (cls.isArray() && method.isVarArgs()) {
                    this.varArgs = true;
                    this.varArgClass = cls.getComponentType();
                }
            }
            Class<?> returnType = method.getReturnType();
            this.dataType = ResultSet.class.isAssignableFrom(returnType) ? null : ValueToObjectConverter2.classToType(returnType);
        }

        public String toString() {
            return this.method.toString();
        }

        public boolean hasConnectionParam() {
            return this.hasConnectionParam;
        }

        public Value getValue(SessionLocal sessionLocal, Expression[] expressionArr, boolean z) {
            Object execute = execute(sessionLocal, expressionArr, z);
            if (Value.class.isAssignableFrom(this.method.getReturnType())) {
                return (Value) execute;
            }
            return ValueToObjectConverter.objectToValue(sessionLocal, execute, this.dataType.getValueType()).convertTo(this.dataType, sessionLocal);
        }

        public ResultInterface getTableValue(SessionLocal sessionLocal, Expression[] expressionArr, boolean z) {
            Object execute = execute(sessionLocal, expressionArr, z);
            if (execute == null) {
                throw DbException.get(ErrorCode.FUNCTION_MUST_RETURN_RESULT_SET_1, this.method.getName());
            }
            if (ResultInterface.class.isAssignableFrom(this.method.getReturnType())) {
                return (ResultInterface) execute;
            }
            return resultSetToResult(sessionLocal, (ResultSet) execute, z ? 0 : Integer.MAX_VALUE);
        }

        public static ResultInterface resultSetToResult(SessionLocal sessionLocal, ResultSet resultSet, int i) {
            TypeInfo typeInfo;
            Throwable th = null;
            try {
                try {
                    try {
                        ResultSetMetaData metaData = resultSet.getMetaData();
                        int columnCount = metaData.getColumnCount();
                        Expression[] expressionArr = new Expression[columnCount];
                        for (int i2 = 0; i2 < columnCount; i2++) {
                            String columnLabel = metaData.getColumnLabel(i2 + 1);
                            String columnName = metaData.getColumnName(i2 + 1);
                            String columnTypeName = metaData.getColumnTypeName(i2 + 1);
                            int convertSQLTypeToValueType = DataType.convertSQLTypeToValueType(metaData.getColumnType(i2 + 1), columnTypeName);
                            int precision = metaData.getPrecision(i2 + 1);
                            int scale = metaData.getScale(i2 + 1);
                            if (convertSQLTypeToValueType == 40 && columnTypeName.endsWith(" ARRAY")) {
                                typeInfo = TypeInfo.getTypeInfo(40, -1L, 0, TypeInfo.getTypeInfo(DataType.getTypeByName(columnTypeName.substring(0, columnTypeName.length() - 6), sessionLocal.getMode()).type));
                            } else {
                                typeInfo = TypeInfo.getTypeInfo(convertSQLTypeToValueType, precision, scale, null);
                            }
                            Expression expressionColumn = new ExpressionColumn(sessionLocal.getDatabase(), new Column(columnName, typeInfo));
                            if (!columnLabel.equals(columnName)) {
                                expressionColumn = new Alias(expressionColumn, columnLabel, false);
                            }
                            expressionArr[i2] = expressionColumn;
                        }
                        LocalResult localResult = new LocalResult(sessionLocal, expressionArr, columnCount, columnCount);
                        for (int i3 = 0; i3 < i && resultSet.next(); i3++) {
                            Value[] valueArr = new Value[columnCount];
                            for (int i4 = 0; i4 < columnCount; i4++) {
                                valueArr[i4] = ValueToObjectConverter.objectToValue(sessionLocal, resultSet.getObject(i4 + 1), expressionArr[i4].getType().getValueType());
                            }
                            localResult.addRow(valueArr);
                        }
                        localResult.done();
                        if (resultSet != null) {
                            if (0 != 0) {
                                try {
                                    resultSet.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            } else {
                                resultSet.close();
                            }
                        }
                        return localResult;
                    } finally {
                    }
                } finally {
                }
            } catch (SQLException e) {
                throw DbException.convert(e);
            }
        }

        private Object execute(SessionLocal sessionLocal, Expression[] expressionArr, boolean z) {
            Class<?> cls;
            Object valueToObject;
            Class<?>[] parameterTypes = this.method.getParameterTypes();
            Object[] objArr = new Object[parameterTypes.length];
            int i = 0;
            JdbcConnection createConnection = sessionLocal.createConnection(z);
            if (this.hasConnectionParam && objArr.length > 0) {
                i = 0 + 1;
                objArr[0] = createConnection;
            }
            Object obj = null;
            if (this.varArgs) {
                obj = Array.newInstance(this.varArgClass, (expressionArr.length - objArr.length) + 1 + (this.hasConnectionParam ? 1 : 0));
                objArr[objArr.length - 1] = obj;
            }
            int i2 = 0;
            int length = expressionArr.length;
            while (i2 < length) {
                boolean z2 = this.varArgs && i >= parameterTypes.length - 1;
                if (z2) {
                    cls = this.varArgClass;
                } else {
                    cls = parameterTypes[i];
                }
                Value value = expressionArr[i2].getValue(sessionLocal);
                if (Value.class.isAssignableFrom(cls)) {
                    valueToObject = value;
                } else {
                    boolean isPrimitive = cls.isPrimitive();
                    if (value == ValueNull.INSTANCE) {
                        if (isPrimitive) {
                            if (z) {
                                valueToObject = DataType.getDefaultForPrimitiveType(cls);
                            } else {
                                return null;
                            }
                        } else {
                            valueToObject = null;
                        }
                    } else {
                        valueToObject = ValueToObjectConverter.valueToObject(isPrimitive ? Utils.getNonPrimitiveClass(cls) : cls, value, createConnection);
                    }
                }
                if (z2) {
                    Array.set(obj, (i - objArr.length) + 1, valueToObject);
                } else {
                    objArr[i] = valueToObject;
                }
                i2++;
                i++;
            }
            boolean autoCommit = sessionLocal.getAutoCommit();
            Value lastIdentity = sessionLocal.getLastIdentity();
            boolean z3 = sessionLocal.getDatabase().getSettings().defaultConnection;
            try {
                sessionLocal.setAutoCommit(false);
                if (z3) {
                    try {
                        Driver.setDefaultConnection(sessionLocal.createConnection(z));
                    } catch (InvocationTargetException e) {
                        StringBuilder append = new StringBuilder(this.method.getName()).append('(');
                        int length2 = objArr.length;
                        for (int i3 = 0; i3 < length2; i3++) {
                            if (i3 > 0) {
                                append.append(", ");
                            }
                            append.append(objArr[i3]);
                        }
                        append.append(')');
                        throw DbException.convertInvocation(e, append.toString());
                    } catch (Exception e2) {
                        throw DbException.convert(e2);
                    }
                }
                Object invoke = this.method.invoke(null, objArr);
                if (invoke == null) {
                    return null;
                }
                sessionLocal.setLastIdentity(lastIdentity);
                sessionLocal.setAutoCommit(autoCommit);
                if (z3) {
                    Driver.setDefaultConnection(null);
                }
                return invoke;
            } finally {
                sessionLocal.setLastIdentity(lastIdentity);
                sessionLocal.setAutoCommit(autoCommit);
                if (z3) {
                    Driver.setDefaultConnection(null);
                }
            }
        }

        public Class<?>[] getColumnClasses() {
            return this.method.getParameterTypes();
        }

        public TypeInfo getDataType() {
            return this.dataType;
        }

        public int getParameterCount() {
            return this.paramCount;
        }

        public boolean isVarArgs() {
            return this.varArgs;
        }

        @Override // java.lang.Comparable
        public int compareTo(JavaMethod javaMethod) {
            if (this.varArgs != javaMethod.varArgs) {
                return this.varArgs ? 1 : -1;
            }
            if (this.paramCount != javaMethod.paramCount) {
                return this.paramCount - javaMethod.paramCount;
            }
            if (this.hasConnectionParam != javaMethod.hasConnectionParam) {
                return this.hasConnectionParam ? 1 : -1;
            }
            return this.id - javaMethod.id;
        }
    }
}
