package org.springframework.cglib.beans;

import java.beans.PropertyDescriptor;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.Type;
import org.springframework.cglib.core.AbstractClassGenerator;
import org.springframework.cglib.core.ClassEmitter;
import org.springframework.cglib.core.CodeEmitter;
import org.springframework.cglib.core.Constants;
import org.springframework.cglib.core.Converter;
import org.springframework.cglib.core.EmitUtils;
import org.springframework.cglib.core.KeyFactory;
import org.springframework.cglib.core.Local;
import org.springframework.cglib.core.MethodInfo;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.cglib.core.Signature;
import org.springframework.cglib.core.TypeUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/beans/BeanCopier.class */
public abstract class BeanCopier {
    private static final BeanCopierKey KEY_FACTORY = (BeanCopierKey) KeyFactory.create(BeanCopierKey.class);
    private static final Type CONVERTER = TypeUtils.parseType("org.springframework.cglib.core.Converter");
    private static final Type BEAN_COPIER = TypeUtils.parseType("org.springframework.cglib.beans.BeanCopier");
    private static final Signature COPY = new Signature("copy", Type.VOID_TYPE, new Type[]{Constants.TYPE_OBJECT, Constants.TYPE_OBJECT, CONVERTER});
    private static final Signature CONVERT = TypeUtils.parseSignature("Object convert(Object, Class, Object)");

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/beans/BeanCopier$BeanCopierKey.class */
    public interface BeanCopierKey {
        Object newInstance(String source, String target, boolean useConverter);
    }

    public abstract void copy(Object from, Object to, Converter converter);

    public static BeanCopier create(Class source, Class target, boolean useConverter) {
        Generator gen = new Generator();
        gen.setSource(source);
        gen.setTarget(target);
        gen.setUseConverter(useConverter);
        return gen.create();
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/beans/BeanCopier$Generator.class */
    public static class Generator extends AbstractClassGenerator {
        private static final AbstractClassGenerator.Source SOURCE = new AbstractClassGenerator.Source(BeanCopier.class.getName());
        private Class source;
        private Class target;
        private boolean useConverter;

        public Generator() {
            super(SOURCE);
        }

        public void setSource(Class source) {
            this.source = source;
            setContextClass(source);
            setNamePrefix(source.getName());
        }

        public void setTarget(Class target) {
            this.target = target;
            setContextClass(target);
            setNamePrefix(target.getName());
        }

        public void setUseConverter(boolean useConverter) {
            this.useConverter = useConverter;
        }

        @Override // org.springframework.cglib.core.AbstractClassGenerator
        protected ClassLoader getDefaultClassLoader() {
            return this.source.getClassLoader();
        }

        @Override // org.springframework.cglib.core.AbstractClassGenerator
        protected ProtectionDomain getProtectionDomain() {
            return ReflectUtils.getProtectionDomain(this.source);
        }

        public BeanCopier create() {
            Object key = BeanCopier.KEY_FACTORY.newInstance(this.source.getName(), this.target.getName(), this.useConverter);
            return (BeanCopier) super.create(key);
        }

        @Override // org.springframework.cglib.core.ClassGenerator
        public void generateClass(ClassVisitor v) {
            Type sourceType = Type.getType((Class<?>) this.source);
            Type targetType = Type.getType((Class<?>) this.target);
            ClassEmitter ce = new ClassEmitter(v);
            ce.begin_class(52, 1, getClassName(), BeanCopier.BEAN_COPIER, null, Constants.SOURCE_FILE);
            EmitUtils.null_constructor(ce);
            CodeEmitter e = ce.begin_method(1, BeanCopier.COPY, null);
            PropertyDescriptor[] getters = ReflectUtils.getBeanGetters(this.source);
            PropertyDescriptor[] setters = ReflectUtils.getBeanSetters(this.target);
            Map names = new HashMap();
            for (PropertyDescriptor getter : getters) {
                names.put(getter.getName(), getter);
            }
            Local targetLocal = e.make_local();
            Local sourceLocal = e.make_local();
            if (this.useConverter) {
                e.load_arg(1);
                e.checkcast(targetType);
                e.store_local(targetLocal);
                e.load_arg(0);
                e.checkcast(sourceType);
                e.store_local(sourceLocal);
            } else {
                e.load_arg(1);
                e.checkcast(targetType);
                e.load_arg(0);
                e.checkcast(sourceType);
            }
            for (PropertyDescriptor setter : setters) {
                PropertyDescriptor getter2 = (PropertyDescriptor) names.get(setter.getName());
                if (getter2 != null) {
                    MethodInfo read = ReflectUtils.getMethodInfo(getter2.getReadMethod());
                    MethodInfo write = ReflectUtils.getMethodInfo(setter.getWriteMethod());
                    if (this.useConverter) {
                        Type setterType = write.getSignature().getArgumentTypes()[0];
                        e.load_local(targetLocal);
                        e.load_arg(2);
                        e.load_local(sourceLocal);
                        e.invoke(read);
                        e.box(read.getSignature().getReturnType());
                        EmitUtils.load_class(e, setterType);
                        e.push(write.getSignature().getName());
                        e.invoke_interface(BeanCopier.CONVERTER, BeanCopier.CONVERT);
                        e.unbox_or_zero(setterType);
                        e.invoke(write);
                    } else if (compatible(getter2, setter)) {
                        e.dup2();
                        e.invoke(read);
                        e.invoke(write);
                    }
                }
            }
            e.return_value();
            e.end_method();
            ce.end_class();
        }

        private static boolean compatible(PropertyDescriptor getter, PropertyDescriptor setter) {
            return setter.getPropertyType().isAssignableFrom(getter.getPropertyType());
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
