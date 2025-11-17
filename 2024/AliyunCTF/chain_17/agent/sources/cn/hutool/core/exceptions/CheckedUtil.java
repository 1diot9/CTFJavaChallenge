package cn.hutool.core.exceptions;

import cn.hutool.core.lang.func.Func;
import cn.hutool.core.lang.func.Func0;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.lang.func.Supplier1;
import cn.hutool.core.lang.func.VoidFunc;
import cn.hutool.core.lang.func.VoidFunc0;
import cn.hutool.core.lang.func.VoidFunc1;
import java.lang.invoke.SerializedLambda;
import java.util.Objects;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/exceptions/CheckedUtil.class */
public class CheckedUtil {

    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/exceptions/CheckedUtil$Func0Rt.class */
    public interface Func0Rt<R> extends Func0<R> {
        @Override // cn.hutool.core.lang.func.Func0
        R call() throws RuntimeException;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/exceptions/CheckedUtil$Func1Rt.class */
    public interface Func1Rt<P, R> extends Func1<P, R> {
        @Override // cn.hutool.core.lang.func.Func1
        R call(P p) throws RuntimeException;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/exceptions/CheckedUtil$FuncRt.class */
    public interface FuncRt<P, R> extends Func<P, R> {
        @Override // cn.hutool.core.lang.func.Func
        R call(P... pArr) throws RuntimeException;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/exceptions/CheckedUtil$VoidFunc0Rt.class */
    public interface VoidFunc0Rt extends VoidFunc0 {
        @Override // cn.hutool.core.lang.func.VoidFunc0
        void call() throws RuntimeException;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/exceptions/CheckedUtil$VoidFunc1Rt.class */
    public interface VoidFunc1Rt<P> extends VoidFunc1<P> {
        @Override // cn.hutool.core.lang.func.VoidFunc1
        void call(P p) throws RuntimeException;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/exceptions/CheckedUtil$VoidFuncRt.class */
    public interface VoidFuncRt<P> extends VoidFunc<P> {
        @Override // cn.hutool.core.lang.func.VoidFunc
        void call(P... pArr) throws RuntimeException;
    }

    private static /* synthetic */ Object $deserializeLambda$(SerializedLambda lambda) {
        String implMethodName = lambda.getImplMethodName();
        boolean z = -1;
        switch (implMethodName.hashCode()) {
            case -1766956304:
                if (implMethodName.equals("lambda$uncheck$a3c5d001$1")) {
                    z = 6;
                    break;
                }
                break;
            case -706715036:
                if (implMethodName.equals("lambda$uncheck$5732f3b9$1")) {
                    z = false;
                    break;
                }
                break;
            case 55743573:
                if (implMethodName.equals("lambda$uncheck$2300d7df$1")) {
                    z = 2;
                    break;
                }
                break;
            case 280508133:
                if (implMethodName.equals("lambda$uncheck$5b7ace8e$1")) {
                    z = 5;
                    break;
                }
                break;
            case 419182489:
                if (implMethodName.equals("lambda$uncheck$e9066ec4$1")) {
                    z = true;
                    break;
                }
                break;
            case 1499146451:
                if (implMethodName.equals("lambda$uncheck$ad313ebc$1")) {
                    z = 3;
                    break;
                }
                break;
            case 2106418934:
                if (implMethodName.equals("lambda$uncheck$6c25eb8b$1")) {
                    z = 4;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/exceptions/CheckedUtil$Func1Rt") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/core/exceptions/CheckedUtil") && lambda.getImplMethodSignature().equals("(Lcn/hutool/core/lang/func/Func1;Lcn/hutool/core/lang/func/Supplier1;Ljava/lang/Object;)Ljava/lang/Object;")) {
                    Func1 func1 = (Func1) lambda.getCapturedArg(0);
                    Supplier1 supplier1 = (Supplier1) lambda.getCapturedArg(1);
                    return t -> {
                        try {
                            return func1.call(t);
                        } catch (Exception e) {
                            if (supplier1 == null) {
                                throw new RuntimeException(e);
                            }
                            throw ((RuntimeException) supplier1.get(e));
                        }
                    };
                }
                break;
            case true:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/exceptions/CheckedUtil$Func0Rt") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("()Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/core/exceptions/CheckedUtil") && lambda.getImplMethodSignature().equals("(Lcn/hutool/core/lang/func/Func0;Lcn/hutool/core/lang/func/Supplier1;)Ljava/lang/Object;")) {
                    Func0 func0 = (Func0) lambda.getCapturedArg(0);
                    Supplier1 supplier12 = (Supplier1) lambda.getCapturedArg(1);
                    return () -> {
                        try {
                            return func0.call();
                        } catch (Exception e) {
                            if (supplier12 == null) {
                                throw new RuntimeException(e);
                            }
                            throw ((RuntimeException) supplier12.get(e));
                        }
                    };
                }
                break;
            case true:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/exceptions/CheckedUtil$VoidFunc0Rt") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("()V") && lambda.getImplClass().equals("cn/hutool/core/exceptions/CheckedUtil") && lambda.getImplMethodSignature().equals("(Lcn/hutool/core/lang/func/VoidFunc0;Lcn/hutool/core/lang/func/Supplier1;)V")) {
                    VoidFunc0 voidFunc0 = (VoidFunc0) lambda.getCapturedArg(0);
                    Supplier1 supplier13 = (Supplier1) lambda.getCapturedArg(1);
                    return () -> {
                        try {
                            voidFunc0.call();
                        } catch (Exception e) {
                            if (supplier13 == null) {
                                throw new RuntimeException(e);
                            }
                            throw ((RuntimeException) supplier13.get(e));
                        }
                    };
                }
                break;
            case true:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/exceptions/CheckedUtil$VoidFunc1Rt") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)V") && lambda.getImplClass().equals("cn/hutool/core/exceptions/CheckedUtil") && lambda.getImplMethodSignature().equals("(Lcn/hutool/core/lang/func/VoidFunc1;Lcn/hutool/core/lang/func/Supplier1;Ljava/lang/Object;)V")) {
                    VoidFunc1 voidFunc1 = (VoidFunc1) lambda.getCapturedArg(0);
                    Supplier1 supplier14 = (Supplier1) lambda.getCapturedArg(1);
                    return t2 -> {
                        try {
                            voidFunc1.call(t2);
                        } catch (Exception e) {
                            if (supplier14 == null) {
                                throw new RuntimeException(e);
                            }
                            throw ((RuntimeException) supplier14.get(e));
                        }
                    };
                }
                break;
            case true:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/exceptions/CheckedUtil$FuncRt") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("([Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/core/exceptions/CheckedUtil") && lambda.getImplMethodSignature().equals("(Lcn/hutool/core/lang/func/Func;Lcn/hutool/core/lang/func/Supplier1;[Ljava/lang/Object;)Ljava/lang/Object;")) {
                    Func func = (Func) lambda.getCapturedArg(0);
                    Supplier1 supplier15 = (Supplier1) lambda.getCapturedArg(1);
                    return t3 -> {
                        try {
                            return func.call(t3);
                        } catch (Exception e) {
                            if (supplier15 == null) {
                                throw new RuntimeException(e);
                            }
                            throw ((RuntimeException) supplier15.get(e));
                        }
                    };
                }
                break;
            case true:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/exceptions/CheckedUtil$VoidFuncRt") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("([Ljava/lang/Object;)V") && lambda.getImplClass().equals("cn/hutool/core/exceptions/CheckedUtil") && lambda.getImplMethodSignature().equals("(Lcn/hutool/core/lang/func/VoidFunc;Lcn/hutool/core/lang/func/Supplier1;[Ljava/lang/Object;)V")) {
                    VoidFunc voidFunc = (VoidFunc) lambda.getCapturedArg(0);
                    Supplier1 supplier16 = (Supplier1) lambda.getCapturedArg(1);
                    return t4 -> {
                        try {
                            voidFunc.call(t4);
                        } catch (Exception e) {
                            if (supplier16 == null) {
                                throw new RuntimeException(e);
                            }
                            throw ((RuntimeException) supplier16.get(e));
                        }
                    };
                }
                break;
            case true:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/exceptions/CheckedUtil$VoidFunc0Rt") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("()V") && lambda.getImplClass().equals("cn/hutool/core/exceptions/CheckedUtil") && lambda.getImplMethodSignature().equals("(Lcn/hutool/core/lang/func/VoidFunc0;Ljava/lang/RuntimeException;)V")) {
                    VoidFunc0 voidFunc02 = (VoidFunc0) lambda.getCapturedArg(0);
                    RuntimeException runtimeException = (RuntimeException) lambda.getCapturedArg(1);
                    return () -> {
                        try {
                            voidFunc02.call();
                        } catch (Exception e) {
                            if (runtimeException == null) {
                                throw new RuntimeException(e);
                            }
                            runtimeException.initCause(e);
                            throw runtimeException;
                        }
                    };
                }
                break;
        }
        throw new IllegalArgumentException("Invalid lambda deserialization");
    }

    public static <P, R> FuncRt<P, R> uncheck(Func<P, R> expression) {
        return uncheck(expression, (Supplier1<RuntimeException, Exception>) (v1) -> {
            return new RuntimeException(v1);
        });
    }

    public static <R> Func0Rt<R> uncheck(Func0<R> expression) {
        return uncheck(expression, (Supplier1<RuntimeException, Exception>) (v1) -> {
            return new RuntimeException(v1);
        });
    }

    public static <P, R> Func1Rt<P, R> uncheck(Func1<P, R> expression) {
        return uncheck(expression, (Supplier1<RuntimeException, Exception>) (v1) -> {
            return new RuntimeException(v1);
        });
    }

    public static <P> VoidFuncRt<P> uncheck(VoidFunc<P> expression) {
        return uncheck(expression, (Supplier1<RuntimeException, Exception>) (v1) -> {
            return new RuntimeException(v1);
        });
    }

    public static VoidFunc0Rt uncheck(VoidFunc0 expression) {
        return uncheck(expression, (Supplier1<RuntimeException, Exception>) (v1) -> {
            return new RuntimeException(v1);
        });
    }

    public static <P> VoidFunc1Rt<P> uncheck(VoidFunc1<P> expression) {
        return uncheck(expression, (Supplier1<RuntimeException, Exception>) (v1) -> {
            return new RuntimeException(v1);
        });
    }

    public static <P, R> FuncRt<P, R> uncheck(Func<P, R> expression, Supplier1<RuntimeException, Exception> rteSupplier) {
        Objects.requireNonNull(expression, "expression can not be null");
        return t3 -> {
            try {
                return expression.call(t3);
            } catch (Exception e) {
                if (rteSupplier == null) {
                    throw new RuntimeException(e);
                }
                throw ((RuntimeException) rteSupplier.get(e));
            }
        };
    }

    public static <R> Func0Rt<R> uncheck(Func0<R> expression, Supplier1<RuntimeException, Exception> rteSupplier) {
        Objects.requireNonNull(expression, "expression can not be null");
        return () -> {
            try {
                return expression.call();
            } catch (Exception e) {
                if (rteSupplier == null) {
                    throw new RuntimeException(e);
                }
                throw ((RuntimeException) rteSupplier.get(e));
            }
        };
    }

    public static <P, R> Func1Rt<P, R> uncheck(Func1<P, R> expression, Supplier1<RuntimeException, Exception> rteSupplier) {
        Objects.requireNonNull(expression, "expression can not be null");
        return t -> {
            try {
                return expression.call(t);
            } catch (Exception e) {
                if (rteSupplier == null) {
                    throw new RuntimeException(e);
                }
                throw ((RuntimeException) rteSupplier.get(e));
            }
        };
    }

    public static <P> VoidFuncRt<P> uncheck(VoidFunc<P> expression, Supplier1<RuntimeException, Exception> rteSupplier) {
        Objects.requireNonNull(expression, "expression can not be null");
        return t4 -> {
            try {
                expression.call(t4);
            } catch (Exception e) {
                if (rteSupplier == null) {
                    throw new RuntimeException(e);
                }
                throw ((RuntimeException) rteSupplier.get(e));
            }
        };
    }

    public static VoidFunc0Rt uncheck(VoidFunc0 expression, RuntimeException rte) {
        Objects.requireNonNull(expression, "expression can not be null");
        return () -> {
            try {
                expression.call();
            } catch (Exception e) {
                if (rte == null) {
                    throw new RuntimeException(e);
                }
                rte.initCause(e);
                throw rte;
            }
        };
    }

    public static VoidFunc0Rt uncheck(VoidFunc0 expression, Supplier1<RuntimeException, Exception> rteSupplier) {
        Objects.requireNonNull(expression, "expression can not be null");
        return () -> {
            try {
                expression.call();
            } catch (Exception e) {
                if (rteSupplier == null) {
                    throw new RuntimeException(e);
                }
                throw ((RuntimeException) rteSupplier.get(e));
            }
        };
    }

    public static <P> VoidFunc1Rt<P> uncheck(VoidFunc1<P> expression, Supplier1<RuntimeException, Exception> rteSupplier) {
        Objects.requireNonNull(expression, "expression can not be null");
        return t2 -> {
            try {
                expression.call(t2);
            } catch (Exception e) {
                if (rteSupplier == null) {
                    throw new RuntimeException(e);
                }
                throw ((RuntimeException) rteSupplier.get(e));
            }
        };
    }
}
