package org.springframework.cglib.reflect;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.Type;
import org.springframework.cglib.core.AbstractClassGenerator;
import org.springframework.cglib.core.ClassEmitter;
import org.springframework.cglib.core.CodeEmitter;
import org.springframework.cglib.core.Constants;
import org.springframework.cglib.core.EmitUtils;
import org.springframework.cglib.core.Local;
import org.springframework.cglib.core.MethodInfo;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.cglib.core.Signature;
import org.springframework.cglib.core.TypeUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/reflect/MulticastDelegate.class */
public abstract class MulticastDelegate implements Cloneable {
    protected Object[] targets = new Object[0];

    public abstract MulticastDelegate add(Object target);

    public abstract MulticastDelegate newInstance();

    protected MulticastDelegate() {
    }

    public List getTargets() {
        return new ArrayList(Arrays.asList(this.targets));
    }

    protected MulticastDelegate addHelper(Object target) {
        MulticastDelegate copy = newInstance();
        copy.targets = new Object[this.targets.length + 1];
        System.arraycopy(this.targets, 0, copy.targets, 0, this.targets.length);
        copy.targets[this.targets.length] = target;
        return copy;
    }

    public MulticastDelegate remove(Object target) {
        for (int i = this.targets.length - 1; i >= 0; i--) {
            if (this.targets[i].equals(target)) {
                MulticastDelegate copy = newInstance();
                copy.targets = new Object[this.targets.length - 1];
                System.arraycopy(this.targets, 0, copy.targets, 0, i);
                System.arraycopy(this.targets, i + 1, copy.targets, i, (this.targets.length - i) - 1);
                return copy;
            }
        }
        return this;
    }

    public static MulticastDelegate create(Class iface) {
        Generator gen = new Generator();
        gen.setInterface(iface);
        return gen.create();
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/reflect/MulticastDelegate$Generator.class */
    public static class Generator extends AbstractClassGenerator {
        private static final AbstractClassGenerator.Source SOURCE = new AbstractClassGenerator.Source(MulticastDelegate.class.getName());
        private static final Type MULTICAST_DELEGATE = TypeUtils.parseType("org.springframework.cglib.reflect.MulticastDelegate");
        private static final Signature NEW_INSTANCE = new Signature("newInstance", MULTICAST_DELEGATE, new Type[0]);
        private static final Signature ADD_DELEGATE = new Signature(BeanUtil.PREFIX_ADDER, MULTICAST_DELEGATE, new Type[]{Constants.TYPE_OBJECT});
        private static final Signature ADD_HELPER = new Signature("addHelper", MULTICAST_DELEGATE, new Type[]{Constants.TYPE_OBJECT});
        private Class iface;

        public Generator() {
            super(SOURCE);
        }

        @Override // org.springframework.cglib.core.AbstractClassGenerator
        protected ClassLoader getDefaultClassLoader() {
            return this.iface.getClassLoader();
        }

        @Override // org.springframework.cglib.core.AbstractClassGenerator
        protected ProtectionDomain getProtectionDomain() {
            return ReflectUtils.getProtectionDomain(this.iface);
        }

        public void setInterface(Class iface) {
            this.iface = iface;
        }

        public MulticastDelegate create() {
            setNamePrefix(MulticastDelegate.class.getName());
            return (MulticastDelegate) super.create(this.iface.getName());
        }

        @Override // org.springframework.cglib.core.ClassGenerator
        public void generateClass(ClassVisitor cv) {
            MethodInfo method = ReflectUtils.getMethodInfo(ReflectUtils.findInterfaceMethod(this.iface));
            ClassEmitter ce = new ClassEmitter(cv);
            ce.begin_class(52, 1, getClassName(), MULTICAST_DELEGATE, new Type[]{Type.getType((Class<?>) this.iface)}, Constants.SOURCE_FILE);
            EmitUtils.null_constructor(ce);
            emitProxy(ce, method);
            CodeEmitter e = ce.begin_method(1, NEW_INSTANCE, null);
            e.new_instance_this();
            e.dup();
            e.invoke_constructor_this();
            e.return_value();
            e.end_method();
            CodeEmitter e2 = ce.begin_method(1, ADD_DELEGATE, null);
            e2.load_this();
            e2.load_arg(0);
            e2.checkcast(Type.getType((Class<?>) this.iface));
            e2.invoke_virtual_this(ADD_HELPER);
            e2.return_value();
            e2.end_method();
            ce.end_class();
        }

        private void emitProxy(ClassEmitter ce, final MethodInfo method) {
            int modifiers = 1;
            if ((method.getModifiers() & 128) == 128) {
                modifiers = 1 | 128;
            }
            CodeEmitter e = EmitUtils.begin_method(ce, method, modifiers);
            Type returnType = method.getSignature().getReturnType();
            boolean returns = returnType != Type.VOID_TYPE;
            Local result = null;
            if (returns) {
                result = e.make_local(returnType);
                e.zero_or_null(returnType);
                e.store_local(result);
            }
            e.load_this();
            e.super_getfield("targets", Constants.TYPE_OBJECT_ARRAY);
            Local result2 = result;
            EmitUtils.process_array(e, Constants.TYPE_OBJECT_ARRAY, type -> {
                e.checkcast(Type.getType((Class<?>) this.iface));
                e.load_args();
                e.invoke(method);
                if (returns) {
                    e.store_local(result2);
                }
            });
            if (returns) {
                e.load_local(result);
            }
            e.return_value();
            e.end_method();
        }

        @Override // org.springframework.cglib.core.AbstractClassGenerator
        protected Object firstInstance(Class type) {
            return ((MulticastDelegate) ReflectUtils.newInstance(type)).newInstance();
        }

        @Override // org.springframework.cglib.core.AbstractClassGenerator
        protected Object nextInstance(Object instance) {
            return ((MulticastDelegate) instance).newInstance();
        }
    }
}
