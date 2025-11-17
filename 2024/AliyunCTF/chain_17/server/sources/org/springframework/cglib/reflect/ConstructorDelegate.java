package org.springframework.cglib.reflect;

import java.lang.reflect.Constructor;
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
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.cglib.core.TypeUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/reflect/ConstructorDelegate.class */
public abstract class ConstructorDelegate {
    private static final ConstructorKey KEY_FACTORY = (ConstructorKey) KeyFactory.create(ConstructorKey.class, KeyFactory.CLASS_BY_NAME);

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/reflect/ConstructorDelegate$ConstructorKey.class */
    public interface ConstructorKey {
        Object newInstance(String declaring, String iface);
    }

    protected ConstructorDelegate() {
    }

    public static ConstructorDelegate create(Class targetClass, Class iface) {
        Generator gen = new Generator();
        gen.setTargetClass(targetClass);
        gen.setInterface(iface);
        return gen.create();
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/reflect/ConstructorDelegate$Generator.class */
    public static class Generator extends AbstractClassGenerator {
        private static final AbstractClassGenerator.Source SOURCE = new AbstractClassGenerator.Source(ConstructorDelegate.class.getName());
        private static final Type CONSTRUCTOR_DELEGATE = TypeUtils.parseType("org.springframework.cglib.reflect.ConstructorDelegate");
        private Class iface;
        private Class targetClass;

        public Generator() {
            super(SOURCE);
        }

        public void setInterface(Class iface) {
            this.iface = iface;
        }

        public void setTargetClass(Class targetClass) {
            this.targetClass = targetClass;
        }

        public ConstructorDelegate create() {
            setNamePrefix(this.targetClass.getName());
            Object key = ConstructorDelegate.KEY_FACTORY.newInstance(this.iface.getName(), this.targetClass.getName());
            return (ConstructorDelegate) super.create(key);
        }

        @Override // org.springframework.cglib.core.AbstractClassGenerator
        protected ClassLoader getDefaultClassLoader() {
            return this.targetClass.getClassLoader();
        }

        @Override // org.springframework.cglib.core.AbstractClassGenerator
        protected ProtectionDomain getProtectionDomain() {
            return ReflectUtils.getProtectionDomain(this.targetClass);
        }

        @Override // org.springframework.cglib.core.ClassGenerator
        public void generateClass(ClassVisitor v) {
            setNamePrefix(this.targetClass.getName());
            Method newInstance = ReflectUtils.findNewInstance(this.iface);
            if (!newInstance.getReturnType().isAssignableFrom(this.targetClass)) {
                throw new IllegalArgumentException("incompatible return type");
            }
            try {
                Constructor constructor = this.targetClass.getDeclaredConstructor(newInstance.getParameterTypes());
                ClassEmitter ce = new ClassEmitter(v);
                ce.begin_class(52, 1, getClassName(), CONSTRUCTOR_DELEGATE, new Type[]{Type.getType((Class<?>) this.iface)}, Constants.SOURCE_FILE);
                Type declaring = Type.getType((Class<?>) constructor.getDeclaringClass());
                EmitUtils.null_constructor(ce);
                CodeEmitter e = ce.begin_method(1, ReflectUtils.getSignature(newInstance), ReflectUtils.getExceptionTypes(newInstance));
                e.new_instance(declaring);
                e.dup();
                e.load_args();
                e.invoke_constructor(declaring, ReflectUtils.getSignature(constructor));
                e.return_value();
                e.end_method();
                ce.end_class();
            } catch (NoSuchMethodException e2) {
                throw new IllegalArgumentException("interface does not match any known constructor");
            }
        }

        @Override // org.springframework.cglib.core.AbstractClassGenerator
        protected Object firstInstance(Class type) {
            return ReflectUtils.newInstance(type);
        }

        @Override // org.springframework.cglib.core.AbstractClassGenerator
        protected Object nextInstance(Object instance) {
            return instance;
        }
    }
}
