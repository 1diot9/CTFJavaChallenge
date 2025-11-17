package org.springframework.cglib.reflect;

import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.Type;
import org.springframework.cglib.core.AbstractClassGenerator;
import org.springframework.cglib.core.ClassEmitter;
import org.springframework.cglib.core.CodeEmitter;
import org.springframework.cglib.core.Constants;
import org.springframework.cglib.core.EmitUtils;
import org.springframework.cglib.core.KeyFactory;
import org.springframework.cglib.core.MethodInfo;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.cglib.core.Signature;
import org.springframework.cglib.core.TypeUtils;
import org.springframework.validation.DataBinder;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/reflect/MethodDelegate.class */
public abstract class MethodDelegate {
    private static final MethodDelegateKey KEY_FACTORY = (MethodDelegateKey) KeyFactory.create(MethodDelegateKey.class, KeyFactory.CLASS_BY_NAME);
    protected Object target;
    protected String eqMethod;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/reflect/MethodDelegate$MethodDelegateKey.class */
    public interface MethodDelegateKey {
        Object newInstance(Class delegateClass, String methodName, Class iface);
    }

    public abstract MethodDelegate newInstance(Object target);

    public static MethodDelegate createStatic(Class targetClass, String methodName, Class iface) {
        Generator gen = new Generator();
        gen.setTargetClass(targetClass);
        gen.setMethodName(methodName);
        gen.setInterface(iface);
        return gen.create();
    }

    public static MethodDelegate create(Object target, String methodName, Class iface) {
        Generator gen = new Generator();
        gen.setTarget(target);
        gen.setMethodName(methodName);
        gen.setInterface(iface);
        return gen.create();
    }

    public boolean equals(Object obj) {
        MethodDelegate other = (MethodDelegate) obj;
        return other != null && this.target == other.target && this.eqMethod.equals(other.eqMethod);
    }

    public int hashCode() {
        return this.target.hashCode() ^ this.eqMethod.hashCode();
    }

    public Object getTarget() {
        return this.target;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/reflect/MethodDelegate$Generator.class */
    public static class Generator extends AbstractClassGenerator {
        private static final AbstractClassGenerator.Source SOURCE = new AbstractClassGenerator.Source(MethodDelegate.class.getName());
        private static final Type METHOD_DELEGATE = TypeUtils.parseType("org.springframework.cglib.reflect.MethodDelegate");
        private static final Signature NEW_INSTANCE = new Signature("newInstance", METHOD_DELEGATE, new Type[]{Constants.TYPE_OBJECT});
        private Object target;
        private Class targetClass;
        private String methodName;
        private Class iface;

        public Generator() {
            super(SOURCE);
        }

        public void setTarget(Object target) {
            this.target = target;
            this.targetClass = target.getClass();
        }

        public void setTargetClass(Class targetClass) {
            this.targetClass = targetClass;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public void setInterface(Class iface) {
            this.iface = iface;
        }

        @Override // org.springframework.cglib.core.AbstractClassGenerator
        protected ClassLoader getDefaultClassLoader() {
            return this.targetClass.getClassLoader();
        }

        @Override // org.springframework.cglib.core.AbstractClassGenerator
        protected ProtectionDomain getProtectionDomain() {
            return ReflectUtils.getProtectionDomain(this.targetClass);
        }

        public MethodDelegate create() {
            setNamePrefix(this.targetClass.getName());
            Object key = MethodDelegate.KEY_FACTORY.newInstance(this.targetClass, this.methodName, this.iface);
            return (MethodDelegate) super.create(key);
        }

        @Override // org.springframework.cglib.core.AbstractClassGenerator
        protected Object firstInstance(Class type) {
            return ((MethodDelegate) ReflectUtils.newInstance(type)).newInstance(this.target);
        }

        @Override // org.springframework.cglib.core.AbstractClassGenerator
        protected Object nextInstance(Object instance) {
            return ((MethodDelegate) instance).newInstance(this.target);
        }

        @Override // org.springframework.cglib.core.ClassGenerator
        public void generateClass(ClassVisitor v) throws NoSuchMethodException {
            Method proxy = ReflectUtils.findInterfaceMethod(this.iface);
            Method method = this.targetClass.getMethod(this.methodName, proxy.getParameterTypes());
            if (!proxy.getReturnType().isAssignableFrom(method.getReturnType())) {
                throw new IllegalArgumentException("incompatible return types");
            }
            MethodInfo methodInfo = ReflectUtils.getMethodInfo(method);
            boolean isStatic = TypeUtils.isStatic(methodInfo.getModifiers());
            if ((this.target == null) ^ isStatic) {
                throw new IllegalArgumentException("Static method " + (isStatic ? "not " : "") + "expected");
            }
            ClassEmitter ce = new ClassEmitter(v);
            ce.begin_class(52, 1, getClassName(), METHOD_DELEGATE, new Type[]{Type.getType((Class<?>) this.iface)}, Constants.SOURCE_FILE);
            ce.declare_field(26, "eqMethod", Constants.TYPE_STRING, null);
            EmitUtils.null_constructor(ce);
            MethodInfo proxied = ReflectUtils.getMethodInfo(this.iface.getDeclaredMethods()[0]);
            int modifiers = 1;
            if ((proxied.getModifiers() & 128) == 128) {
                modifiers = 1 | 128;
            }
            CodeEmitter e = EmitUtils.begin_method(ce, proxied, modifiers);
            e.load_this();
            e.super_getfield(DataBinder.DEFAULT_OBJECT_NAME, Constants.TYPE_OBJECT);
            e.checkcast(methodInfo.getClassInfo().getType());
            e.load_args();
            e.invoke(methodInfo);
            e.return_value();
            e.end_method();
            CodeEmitter e2 = ce.begin_method(1, NEW_INSTANCE, null);
            e2.new_instance_this();
            e2.dup();
            e2.dup2();
            e2.invoke_constructor_this();
            e2.getfield("eqMethod");
            e2.super_putfield("eqMethod", Constants.TYPE_STRING);
            e2.load_arg(0);
            e2.super_putfield(DataBinder.DEFAULT_OBJECT_NAME, Constants.TYPE_OBJECT);
            e2.return_value();
            e2.end_method();
            CodeEmitter e3 = ce.begin_static();
            e3.push(methodInfo.getSignature().toString());
            e3.putfield("eqMethod");
            e3.return_value();
            e3.end_method();
            ce.end_class();
        }
    }
}
