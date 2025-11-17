package io.r2dbc.spi;

import io.r2dbc.spi.Parameter;
import io.r2dbc.spi.Type;
import org.springframework.beans.factory.support.AbstractBeanDefinition;

/* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/Parameters.class */
public final class Parameters {
    private Parameters() {
    }

    public static Parameter in(Type type) {
        Assert.requireNonNull(type, "Type must not be null");
        return in(type, null);
    }

    public static Parameter in(Class<?> type) {
        Assert.requireNonNull(type, "Type must not be null");
        return in(new DefaultInferredType(type), null);
    }

    public static Parameter in(Object value) {
        Assert.requireNonNull(value, "Value must not be null");
        return in(new DefaultInferredType(value.getClass()), value);
    }

    public static Parameter in(Type type, @Nullable Object value) {
        Assert.requireNonNull(type, "Type must not be null");
        return new InParameter(type, value);
    }

    public static Parameter out(Type type) {
        Assert.requireNonNull(type, "Type must not be null");
        return new OutParameter(type);
    }

    public static Parameter out(Class<?> type) {
        Assert.requireNonNull(type, "Type must not be null");
        return out(new DefaultInferredType(type));
    }

    public static Parameter inOut(Type type) {
        Assert.requireNonNull(type, "Type must not be null");
        return inOut(type, null);
    }

    public static Parameter inOut(Class<?> type) {
        Assert.requireNonNull(type, "Type must not be null");
        return inOut(new DefaultInferredType(type), null);
    }

    public static Parameter inOut(Object value) {
        Assert.requireNonNull(value, "Value must not be null");
        return inOut(new DefaultInferredType(value.getClass()), value);
    }

    public static Parameter inOut(Type type, @Nullable Object value) {
        Assert.requireNonNull(type, "Type must not be null");
        return new InOutParameter(type, value);
    }

    /* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/Parameters$DefaultParameter.class */
    private static class DefaultParameter implements Parameter {
        private final Type type;

        @Nullable
        private final Object value;

        public DefaultParameter(Type type, @Nullable Object value) {
            this.type = type;
            this.value = value;
        }

        @Override // io.r2dbc.spi.Parameter
        public Type getType() {
            return this.type;
        }

        @Override // io.r2dbc.spi.Parameter
        public Object getValue() {
            return this.value;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Parameter)) {
                return false;
            }
            Parameter that = (Parameter) o;
            if (getType().equals(that.getType())) {
                return getValue() != null ? getValue().equals(that.getValue()) : that.getValue() == null;
            }
            return false;
        }

        public int hashCode() {
            int result = getType().hashCode();
            return (31 * result) + (getValue() != null ? getValue().hashCode() : 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/Parameters$InParameter.class */
    public static class InParameter extends DefaultParameter implements Parameter.In {
        public InParameter(Type type, @Nullable Object value) {
            super(type, value);
        }

        public String toString() {
            return "In{" + getType() + '}';
        }

        @Override // io.r2dbc.spi.Parameters.DefaultParameter
        public boolean equals(Object o) {
            return (o instanceof Parameter.In) && !(o instanceof Parameter.Out) && super.equals(o);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/Parameters$OutParameter.class */
    public static class OutParameter extends DefaultParameter implements Parameter.Out {
        public OutParameter(Type type) {
            super(type, null);
        }

        public String toString() {
            return "Out{" + getType() + '}';
        }

        @Override // io.r2dbc.spi.Parameters.DefaultParameter
        public boolean equals(Object o) {
            return (o instanceof Parameter.Out) && !(o instanceof Parameter.In) && super.equals(o);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/Parameters$InOutParameter.class */
    public static class InOutParameter extends DefaultParameter implements Parameter.In, Parameter.Out {
        public InOutParameter(Type type, @Nullable Object value) {
            super(type, value);
        }

        public String toString() {
            return "InOut{" + getType() + '}';
        }

        @Override // io.r2dbc.spi.Parameters.DefaultParameter
        public boolean equals(Object o) {
            return (o instanceof Parameter.In) && (o instanceof Parameter.Out) && super.equals(o);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/Parameters$DefaultInferredType.class */
    private static class DefaultInferredType implements Type.InferredType, Type {
        private final Class<?> javaType;

        DefaultInferredType(Class<?> javaType) {
            this.javaType = javaType;
        }

        @Override // io.r2dbc.spi.Type
        public Class<?> getJavaType() {
            return this.javaType;
        }

        @Override // io.r2dbc.spi.Type
        public String getName() {
            return AbstractBeanDefinition.INFER_METHOD;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Type.InferredType)) {
                return false;
            }
            Type.InferredType that = (Type.InferredType) o;
            return getJavaType().equals(that.getJavaType());
        }

        public int hashCode() {
            return getJavaType().hashCode();
        }

        public String toString() {
            return "Inferred: " + getJavaType().getName();
        }
    }
}
