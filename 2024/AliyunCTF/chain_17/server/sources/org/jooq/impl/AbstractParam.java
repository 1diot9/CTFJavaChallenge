package org.jooq.impl;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import java.lang.invoke.SerializedLambda;
import java.util.Arrays;
import java.util.Set;
import org.jooq.Clause;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Name;
import org.jooq.Param;
import org.jooq.ParamMode;
import org.jooq.QualifiedRecord;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.impl.DefaultBinding;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractParam.class */
public abstract class AbstractParam<T> extends AbstractParamX<T> implements SimpleQueryPart {
    private static Set<SQLDialect> NO_SUPPORT_ARRAY_BINDS = SQLDialect.supportedBy(SQLDialect.TRINO);
    private static Set<SQLDialect> NO_SUPPORT_INTERVAL_BINDS = SQLDialect.supportedBy(SQLDialect.TRINO);
    private static final Clause[] CLAUSES = {Clause.FIELD, Clause.FIELD_VALUE};
    private final String paramName;
    T value;
    private boolean inline;

    private static /* synthetic */ Object $deserializeLambda$(SerializedLambda lambda) {
        String implMethodName = lambda.getImplMethodName();
        boolean z = -1;
        switch (implMethodName.hashCode()) {
            case 1049447520:
                if (implMethodName.equals("lambda$name$a45a22a9$1")) {
                    z = false;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("org/jooq/impl/LazySupplier") && lambda.getFunctionalInterfaceMethodName().equals(BeanUtil.PREFIX_GETTER_GET) && lambda.getFunctionalInterfaceMethodSignature().equals("()Ljava/lang/Object;") && lambda.getImplClass().equals("org/jooq/impl/AbstractParam") && lambda.getImplMethodSignature().equals("(Ljava/lang/String;Ljava/lang/Object;)Lorg/jooq/Name;")) {
                    String str = (String) lambda.getCapturedArg(0);
                    Object capturedArg = lambda.getCapturedArg(1);
                    return () -> {
                        String name;
                        if (str != null) {
                            name = str;
                        } else if (capturedArg instanceof QualifiedRecord) {
                            QualifiedRecord<?> q = (QualifiedRecord) capturedArg;
                            name = q.getQualifier().getName();
                        } else {
                            name = name(capturedArg);
                        }
                        return DSL.name(name);
                    };
                }
                break;
        }
        throw new IllegalArgumentException("Invalid lambda deserialization");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractParam(T value, DataType<T> type) {
        this(value, type, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractParam(T value, DataType<T> type, String paramName) {
        super(name(value, paramName), type);
        this.paramName = paramName;
        this.value = value;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean isPossiblyNullable() {
        return !this.inline || this.value == null;
    }

    static final Name name(Object value, String paramName) {
        return new LazyName(() -> {
            String name;
            if (paramName != null) {
                name = paramName;
            } else if (value instanceof QualifiedRecord) {
                QualifiedRecord<?> q = (QualifiedRecord) value;
                name = q.getQualifier().getName();
            } else {
                name = name(value);
            }
            return DSL.name(name);
        });
    }

    private static final String name(Object value) {
        if (value instanceof byte[]) {
            byte[] b = (byte[]) value;
            return "b_" + Internal.hash0(Integer.valueOf(Arrays.hashCode(Arrays.copyOf(b, 16))));
        }
        if (value instanceof Object[]) {
            Object[] o = (Object[]) value;
            return "a_" + Internal.hash0(Integer.valueOf(Arrays.hashCode(Arrays.copyOf(o, 16))));
        }
        return String.valueOf(value);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        return (getBinding() instanceof DefaultBinding.InternalBinding) && positive(this.value);
    }

    private static boolean positive(Object value) {
        if (!(value instanceof Number)) {
            return false;
        }
        Number n = (Number) value;
        return n.doubleValue() >= 0.0d;
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractParamX
    public final void setConverted0(Object value) {
        this.value = getDataType().convert(value);
    }

    @Override // org.jooq.Param
    public final T getValue() {
        return this.value;
    }

    @Override // org.jooq.Param
    public final String getParamName() {
        return this.paramName;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractParamX
    public final void setInline0(boolean inline) {
        this.inline = inline;
    }

    @Override // org.jooq.Param
    public final boolean isInline() {
        return this.inline;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isInline(Context<?> ctx) {
        return isInline() || ctx.paramType() == ParamType.INLINED || (ctx.paramType() == ParamType.NAMED_OR_INLINED && StringUtils.isBlank(this.paramName)) || ((NO_SUPPORT_ARRAY_BINDS.contains(ctx.dialect()) && getDataType().isArray()) || (NO_SUPPORT_INTERVAL_BINDS.contains(ctx.dialect()) && getDataType().isInterval()));
    }

    @Override // org.jooq.Param
    public final ParamType getParamType() {
        if (this.inline) {
            return ParamType.INLINED;
        }
        if (StringUtils.isBlank(this.paramName)) {
            return ParamType.INDEXED;
        }
        return ParamType.NAMED;
    }

    @Override // org.jooq.Param
    public final ParamMode getParamMode() {
        return ParamMode.IN;
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public void accept(Context<?> ctx) {
    }

    @Override // org.jooq.Param
    public final T $value() {
        return this.value;
    }

    @Override // org.jooq.Param
    public final boolean $inline() {
        return this.inline;
    }

    @Override // org.jooq.Param
    public Param<T> $value(T newValue) {
        throw new QOM.NotYetImplementedException();
    }

    @Override // org.jooq.Param
    public Param<T> $inline(boolean inline) {
        throw new QOM.NotYetImplementedException();
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Param) {
            Object value = ((Param) obj).getValue();
            if (this.value == null) {
                return value == null;
            }
            if ((this.value instanceof byte[]) && (value instanceof byte[])) {
                return Arrays.equals((byte[]) this.value, (byte[]) value);
            }
            if ((this.value instanceof Object[]) && (value instanceof Object[])) {
                return Arrays.equals((Object[]) this.value, (Object[]) value);
            }
            return this.value.equals(value);
        }
        return false;
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public int hashCode() {
        if (this.value == null) {
            return 0;
        }
        T t = this.value;
        if (t instanceof byte[]) {
            return Arrays.hashCode((byte[]) t);
        }
        T t2 = this.value;
        if (t2 instanceof Object[]) {
            return Arrays.hashCode((Object[]) t2);
        }
        return this.value.hashCode();
    }
}
