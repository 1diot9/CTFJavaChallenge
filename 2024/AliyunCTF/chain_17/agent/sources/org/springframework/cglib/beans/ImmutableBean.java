package org.springframework.cglib.beans;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.Type;
import org.springframework.cglib.core.AbstractClassGenerator;
import org.springframework.cglib.core.ClassEmitter;
import org.springframework.cglib.core.CodeEmitter;
import org.springframework.cglib.core.Constants;
import org.springframework.cglib.core.EmitUtils;
import org.springframework.cglib.core.MethodInfo;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.cglib.core.Signature;
import org.springframework.cglib.core.TypeUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/beans/ImmutableBean.class */
public class ImmutableBean {
    private static final Type ILLEGAL_STATE_EXCEPTION = TypeUtils.parseType("IllegalStateException");
    private static final Signature CSTRUCT_OBJECT = TypeUtils.parseConstructor("Object");
    private static final Class[] OBJECT_CLASSES = {Object.class};
    private static final String FIELD_NAME = "CGLIB$RWBean";

    private ImmutableBean() {
    }

    public static Object create(Object bean) {
        Generator gen = new Generator();
        gen.setBean(bean);
        return gen.create();
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/beans/ImmutableBean$Generator.class */
    public static class Generator extends AbstractClassGenerator {
        private static final AbstractClassGenerator.Source SOURCE = new AbstractClassGenerator.Source(ImmutableBean.class.getName());
        private Object bean;
        private Class target;

        public Generator() {
            super(SOURCE);
        }

        public void setBean(Object bean) {
            this.bean = bean;
            this.target = bean.getClass();
            setContextClass(this.target);
        }

        @Override // org.springframework.cglib.core.AbstractClassGenerator
        protected ClassLoader getDefaultClassLoader() {
            return this.target.getClassLoader();
        }

        @Override // org.springframework.cglib.core.AbstractClassGenerator
        protected ProtectionDomain getProtectionDomain() {
            return ReflectUtils.getProtectionDomain(this.target);
        }

        public Object create() {
            String name = this.target.getName();
            setNamePrefix(name);
            return super.create(name);
        }

        @Override // org.springframework.cglib.core.ClassGenerator
        public void generateClass(ClassVisitor v) {
            Type targetType = Type.getType((Class<?>) this.target);
            ClassEmitter ce = new ClassEmitter(v);
            ce.begin_class(52, 1, getClassName(), targetType, null, Constants.SOURCE_FILE);
            ce.declare_field(18, ImmutableBean.FIELD_NAME, targetType, null);
            CodeEmitter e = ce.begin_method(1, ImmutableBean.CSTRUCT_OBJECT, null);
            e.load_this();
            e.super_invoke_constructor();
            e.load_this();
            e.load_arg(0);
            e.checkcast(targetType);
            e.putfield(ImmutableBean.FIELD_NAME);
            e.return_value();
            e.end_method();
            PropertyDescriptor[] descriptors = ReflectUtils.getBeanProperties(this.target);
            Method[] getters = ReflectUtils.getPropertyMethods(descriptors, true, false);
            Method[] setters = ReflectUtils.getPropertyMethods(descriptors, false, true);
            for (Method getter2 : getters) {
                MethodInfo getter = ReflectUtils.getMethodInfo(getter2);
                CodeEmitter e2 = EmitUtils.begin_method(ce, getter, 1);
                e2.load_this();
                e2.getfield(ImmutableBean.FIELD_NAME);
                e2.invoke(getter);
                e2.return_value();
                e2.end_method();
            }
            for (Method setter2 : setters) {
                MethodInfo setter = ReflectUtils.getMethodInfo(setter2);
                CodeEmitter e3 = EmitUtils.begin_method(ce, setter, 1);
                e3.throw_exception(ImmutableBean.ILLEGAL_STATE_EXCEPTION, "Bean is immutable");
                e3.end_method();
            }
            ce.end_class();
        }

        @Override // org.springframework.cglib.core.AbstractClassGenerator
        protected Object firstInstance(Class type) {
            return ReflectUtils.newInstance(type, ImmutableBean.OBJECT_CLASSES, new Object[]{this.bean});
        }

        @Override // org.springframework.cglib.core.AbstractClassGenerator
        protected Object nextInstance(Object instance) {
            return firstInstance(instance.getClass());
        }
    }
}
