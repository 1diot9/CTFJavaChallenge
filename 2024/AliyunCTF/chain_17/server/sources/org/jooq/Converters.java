package org.jooq;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jooq.exception.DataTypeException;
import org.jooq.impl.AbstractContextConverter;
import org.jooq.impl.IdentityConverter;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Converters.class */
public final class Converters<T, U> extends AbstractContextConverter<T, U> {
    final ContextConverter[] chain;

    private static /* synthetic */ Object $deserializeLambda$(SerializedLambda lambda) {
        String implMethodName = lambda.getImplMethodName();
        boolean z = -1;
        switch (implMethodName.hashCode()) {
            case -981644366:
                if (implMethodName.equals("lambda$nullable$ea655ee9$1")) {
                    z = true;
                    break;
                }
                break;
            case -902848317:
                if (implMethodName.equals("lambda$nullable$66be5c54$1")) {
                    z = false;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("java/util/function/BiFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("org/jooq/Converters") && lambda.getImplMethodSignature().equals("(Ljava/util/function/BiFunction;Ljava/lang/Object;Lorg/jooq/ConverterContext;)Ljava/lang/Object;")) {
                    BiFunction biFunction = (BiFunction) lambda.getCapturedArg(0);
                    return (t, x) -> {
                        if (t == null) {
                            return null;
                        }
                        return biFunction.apply(t, x);
                    };
                }
                break;
            case true:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("java/util/function/Function") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("org/jooq/Converters") && lambda.getImplMethodSignature().equals("(Ljava/util/function/Function;Ljava/lang/Object;)Ljava/lang/Object;")) {
                    Function function = (Function) lambda.getCapturedArg(0);
                    return t2 -> {
                        if (t2 == null) {
                            return null;
                        }
                        return function.apply(t2);
                    };
                }
                break;
        }
        throw new IllegalArgumentException("Invalid lambda deserialization");
    }

    @NotNull
    public static <T> ContextConverter<T, T> identity(Class<T> type) {
        return new IdentityConverter(type);
    }

    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    public static <T, U> ContextConverter<T, U> of() {
        return new Converters(new ContextConverter[0]);
    }

    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    public static <T, U> ContextConverter<T, U> of(Converter<T, U> converter) {
        return new Converters(ContextConverter.scoped(converter));
    }

    @NotNull
    public static <T, X1, U> ContextConverter<T, U> of(Converter<T, ? extends X1> c1, Converter<? super X1, U> c2) {
        return new Converters(ContextConverter.scoped(c1), ContextConverter.scoped(c2));
    }

    @NotNull
    public static <T, X1, X2, U> ContextConverter<T, U> of(Converter<T, ? extends X1> c1, Converter<? super X1, ? extends X2> c2, Converter<? super X2, U> c3) {
        return new Converters(ContextConverter.scoped(c1), ContextConverter.scoped(c2), ContextConverter.scoped(c3));
    }

    @NotNull
    public static <T, X1, X2, X3, U> ContextConverter<T, U> of(Converter<T, ? extends X1> c1, Converter<? super X1, ? extends X2> c2, Converter<? super X2, ? extends X3> c3, Converter<? super X3, U> c4) {
        return new Converters(ContextConverter.scoped(c1), ContextConverter.scoped(c2), ContextConverter.scoped(c3), ContextConverter.scoped(c4));
    }

    public static <T, U> Converter<U, T> inverse(Converter<T, U> converter) {
        return inverse(ContextConverter.scoped(converter));
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <T, U> ContextConverter<U, T> inverse(ContextConverter<T, U> contextConverter) {
        if (contextConverter instanceof IdentityConverter) {
            return contextConverter;
        }
        Class type = contextConverter.toType();
        Class fromType = contextConverter.fromType();
        Objects.requireNonNull(contextConverter);
        BiFunction biFunction = contextConverter::to;
        Objects.requireNonNull(contextConverter);
        return ContextConverter.of(type, fromType, biFunction, contextConverter::from);
    }

    public static <T, U> Converter<T[], U[]> forArrays(Converter<T, U> converter) {
        return forArrays(ContextConverter.scoped(converter));
    }

    public static <T, U> ContextConverter<T[], U[]> forArrays(ContextConverter<T, U> converter) {
        if (converter instanceof ArrayComponentConverter) {
            ArrayComponentConverter<T, U> a = (ArrayComponentConverter) converter;
            return a.converter;
        }
        return new ArrayConverter(converter);
    }

    public static <T, U> Converter<T, U> forArrayComponents(Converter<T[], U[]> converter) {
        return forArrayComponents(ContextConverter.scoped(converter));
    }

    public static <T, U> Converter<T, U> forArrayComponents(ContextConverter<T[], U[]> converter) {
        if (converter instanceof ArrayConverter) {
            ArrayConverter<T, U> a = (ArrayConverter) converter;
            return a.converter;
        }
        return new ArrayComponentConverter(converter);
    }

    Converters(ContextConverter... chain) {
        super(chain[0].fromType(), chain[chain.length - 1].toType());
        this.chain = chain;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ContextConverter
    public final U from(T t, ConverterContext converterContext) {
        T t2 = t;
        for (int i = 0; i < this.chain.length; i++) {
            t2 = this.chain[i].from(t2, converterContext);
        }
        return (U) t2;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ContextConverter
    public final T to(U u, ConverterContext converterContext) {
        U u2 = u;
        for (int length = this.chain.length - 1; length >= 0; length--) {
            u2 = this.chain[length].to(u2, converterContext);
        }
        return (T) u2;
    }

    @Override // org.jooq.impl.AbstractContextConverter, org.jooq.impl.AbstractConverter
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Converters [ ");
        sb.append(fromType().getName());
        for (Converter<?, ?> converter : this.chain) {
            sb.append(" -> ");
            sb.append(converter.toType().getName());
        }
        sb.append(" ]");
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T, U> Function<T, U> nullable(Function<? super T, ? extends U> f) {
        if (f instanceof Serializable) {
            return (Function) ((Serializable) t2 -> {
                if (t2 == null) {
                    return null;
                }
                return f.apply(t2);
            });
        }
        return t -> {
            if (t == null) {
                return null;
            }
            return f.apply(t);
        };
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T, U> BiFunction<T, ConverterContext, U> nullable(BiFunction<? super T, ? super ConverterContext, ? extends U> f) {
        if (f instanceof Serializable) {
            return (BiFunction) ((Serializable) (t, x) -> {
                if (t == null) {
                    return null;
                }
                return f.apply(t, x);
            });
        }
        return (t2, x2) -> {
            if (t2 == null) {
                return null;
            }
            return f.apply(t2, x2);
        };
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T, U> Function<T, U> notImplemented() {
        return t -> {
            throw new DataTypeException("Conversion function not implemented");
        };
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T, U> BiFunction<T, ConverterContext, U> notImplementedBiFunction() {
        return (t, x) -> {
            throw new DataTypeException("Conversion function not implemented");
        };
    }

    @ApiStatus.Internal
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Converters$UnknownType.class */
    public static final class UnknownType {
        private UnknownType() {
        }
    }
}
