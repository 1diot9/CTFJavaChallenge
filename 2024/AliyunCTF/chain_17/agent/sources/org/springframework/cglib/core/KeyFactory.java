package org.springframework.cglib.core;

import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.Collections;
import java.util.List;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.Label;
import org.springframework.asm.Opcodes;
import org.springframework.asm.Type;
import org.springframework.cglib.core.AbstractClassGenerator;
import org.springframework.cglib.core.internal.CustomizerRegistry;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/core/KeyFactory.class */
public abstract class KeyFactory {
    private static final Signature GET_NAME = TypeUtils.parseSignature("String getName()");
    private static final Signature GET_CLASS = TypeUtils.parseSignature("Class getClass()");
    private static final Signature HASH_CODE = TypeUtils.parseSignature("int hashCode()");
    private static final Signature EQUALS = TypeUtils.parseSignature("boolean equals(Object)");
    private static final Signature TO_STRING = TypeUtils.parseSignature("String toString()");
    private static final Signature APPEND_STRING = TypeUtils.parseSignature("StringBuffer append(String)");
    private static final Type KEY_FACTORY = TypeUtils.parseType("org.springframework.cglib.core.KeyFactory");
    private static final Signature GET_SORT = TypeUtils.parseSignature("int getSort()");
    private static final int[] PRIMES = {11, 73, Opcodes.PUTSTATIC, 331, 521, 787, 1213, 1823, 2609, 3691, 5189, 7247, 10037, 13931, 19289, 26627, 36683, 50441, 69403, 95401, 131129, 180179, 247501, 340057, 467063, 641371, 880603, 1209107, 1660097, 2279161, 3129011, 4295723, 5897291, 8095873, 11114263, 15257791, 20946017, 28754629, 39474179, 54189869, 74391461, 102123817, 140194277, 192456917, 264202273, 362693231, 497900099, 683510293, 938313161, 1288102441, 1768288259};
    public static final Customizer CLASS_BY_NAME = (e, type) -> {
        if (type.equals(Constants.TYPE_CLASS)) {
            e.invoke_virtual(Constants.TYPE_CLASS, GET_NAME);
        }
    };
    public static final FieldTypeCustomizer STORE_CLASS_AS_STRING = new FieldTypeCustomizer() { // from class: org.springframework.cglib.core.KeyFactory.1
        @Override // org.springframework.cglib.core.FieldTypeCustomizer
        public void customize(CodeEmitter e, int index, Type type) {
            if (type.equals(Constants.TYPE_CLASS)) {
                e.invoke_virtual(Constants.TYPE_CLASS, KeyFactory.GET_NAME);
            }
        }

        @Override // org.springframework.cglib.core.FieldTypeCustomizer
        public Type getOutType(int index, Type type) {
            if (type.equals(Constants.TYPE_CLASS)) {
                return Constants.TYPE_STRING;
            }
            return type;
        }
    };
    public static final HashCodeCustomizer HASH_ASM_TYPE = (e, type) -> {
        if (Constants.TYPE_TYPE.equals(type)) {
            e.invoke_virtual(type, GET_SORT);
            return true;
        }
        return false;
    };

    @Deprecated
    public static final Customizer OBJECT_BY_CLASS = (e, type) -> {
        e.invoke_virtual(Constants.TYPE_OBJECT, GET_CLASS);
    };

    protected KeyFactory() {
    }

    public static KeyFactory create(Class keyInterface) {
        return create(keyInterface, null);
    }

    public static KeyFactory create(Class keyInterface, Customizer customizer) {
        return create(keyInterface.getClassLoader(), keyInterface, customizer);
    }

    public static KeyFactory create(Class keyInterface, KeyFactoryCustomizer first, List<KeyFactoryCustomizer> next) {
        return create(keyInterface.getClassLoader(), keyInterface, first, next);
    }

    public static KeyFactory create(ClassLoader loader, Class keyInterface, Customizer customizer) {
        return create(loader, keyInterface, customizer, Collections.emptyList());
    }

    public static KeyFactory create(ClassLoader loader, Class keyInterface, KeyFactoryCustomizer customizer, List<KeyFactoryCustomizer> next) {
        Generator gen = new Generator();
        gen.setInterface(keyInterface);
        gen.setContextClass(keyInterface);
        if (customizer != null) {
            gen.addCustomizer(customizer);
        }
        if (next != null && !next.isEmpty()) {
            for (KeyFactoryCustomizer keyFactoryCustomizer : next) {
                gen.addCustomizer(keyFactoryCustomizer);
            }
        }
        gen.setClassLoader(loader);
        return gen.create();
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/core/KeyFactory$Generator.class */
    public static class Generator extends AbstractClassGenerator {
        private static final AbstractClassGenerator.Source SOURCE = new AbstractClassGenerator.Source(KeyFactory.class.getName());
        private static final Class[] KNOWN_CUSTOMIZER_TYPES = {Customizer.class, FieldTypeCustomizer.class};
        private Class keyInterface;
        private CustomizerRegistry customizers;
        private int constant;
        private int multiplier;

        public Generator() {
            super(SOURCE);
            this.customizers = new CustomizerRegistry(KNOWN_CUSTOMIZER_TYPES);
        }

        @Override // org.springframework.cglib.core.AbstractClassGenerator
        protected ClassLoader getDefaultClassLoader() {
            return this.keyInterface.getClassLoader();
        }

        @Override // org.springframework.cglib.core.AbstractClassGenerator
        protected ProtectionDomain getProtectionDomain() {
            return ReflectUtils.getProtectionDomain(this.keyInterface);
        }

        @Deprecated
        public void setCustomizer(Customizer customizer) {
            this.customizers = CustomizerRegistry.singleton(customizer);
        }

        public void addCustomizer(KeyFactoryCustomizer customizer) {
            this.customizers.add(customizer);
        }

        public <T> List<T> getCustomizers(Class<T> klass) {
            return this.customizers.get(klass);
        }

        public void setInterface(Class keyInterface) {
            this.keyInterface = keyInterface;
        }

        public KeyFactory create() {
            setNamePrefix(this.keyInterface.getName());
            return (KeyFactory) super.create(this.keyInterface.getName());
        }

        public void setHashConstant(int constant) {
            this.constant = constant;
        }

        public void setHashMultiplier(int multiplier) {
            this.multiplier = multiplier;
        }

        @Override // org.springframework.cglib.core.AbstractClassGenerator
        protected Object firstInstance(Class type) {
            return ReflectUtils.newInstance(type);
        }

        @Override // org.springframework.cglib.core.AbstractClassGenerator
        protected Object nextInstance(Object instance) {
            return instance;
        }

        @Override // org.springframework.cglib.core.ClassGenerator
        public void generateClass(ClassVisitor v) {
            ClassEmitter ce = new ClassEmitter(v);
            Method newInstance = ReflectUtils.findNewInstance(this.keyInterface);
            if (!newInstance.getReturnType().equals(Object.class)) {
                throw new IllegalArgumentException("newInstance method must return Object");
            }
            Type[] parameterTypes = TypeUtils.getTypes(newInstance.getParameterTypes());
            ce.begin_class(52, 1, getClassName(), KeyFactory.KEY_FACTORY, new Type[]{Type.getType((Class<?>) this.keyInterface)}, Constants.SOURCE_FILE);
            EmitUtils.null_constructor(ce);
            EmitUtils.factory_method(ce, ReflectUtils.getSignature(newInstance));
            int seed = 0;
            CodeEmitter e = ce.begin_method(1, TypeUtils.parseConstructor(parameterTypes), null);
            e.load_this();
            e.super_invoke_constructor();
            e.load_this();
            List<FieldTypeCustomizer> fieldTypeCustomizers = getCustomizers(FieldTypeCustomizer.class);
            for (int i = 0; i < parameterTypes.length; i++) {
                Type parameterType = parameterTypes[i];
                Type fieldType = parameterType;
                for (FieldTypeCustomizer customizer : fieldTypeCustomizers) {
                    fieldType = customizer.getOutType(i, fieldType);
                }
                seed += fieldType.hashCode();
                ce.declare_field(18, getFieldName(i), fieldType, null);
                e.dup();
                e.load_arg(i);
                for (FieldTypeCustomizer customizer2 : fieldTypeCustomizers) {
                    customizer2.customize(e, i, parameterType);
                }
                e.putfield(getFieldName(i));
            }
            e.return_value();
            e.end_method();
            CodeEmitter e2 = ce.begin_method(1, KeyFactory.HASH_CODE, null);
            int hc = this.constant != 0 ? this.constant : KeyFactory.PRIMES[Math.abs(seed) % KeyFactory.PRIMES.length];
            int hm = this.multiplier != 0 ? this.multiplier : KeyFactory.PRIMES[Math.abs(seed * 13) % KeyFactory.PRIMES.length];
            e2.push(hc);
            for (int i2 = 0; i2 < parameterTypes.length; i2++) {
                e2.load_this();
                e2.getfield(getFieldName(i2));
                EmitUtils.hash_code(e2, parameterTypes[i2], hm, this.customizers);
            }
            e2.return_value();
            e2.end_method();
            CodeEmitter e3 = ce.begin_method(1, KeyFactory.EQUALS, null);
            Label fail = e3.make_label();
            e3.load_arg(0);
            e3.instance_of_this();
            e3.if_jump(153, fail);
            for (int i3 = 0; i3 < parameterTypes.length; i3++) {
                e3.load_this();
                e3.getfield(getFieldName(i3));
                e3.load_arg(0);
                e3.checkcast_this();
                e3.getfield(getFieldName(i3));
                EmitUtils.not_equals(e3, parameterTypes[i3], fail, this.customizers);
            }
            e3.push(1);
            e3.return_value();
            e3.mark(fail);
            e3.push(0);
            e3.return_value();
            e3.end_method();
            CodeEmitter e4 = ce.begin_method(1, KeyFactory.TO_STRING, null);
            e4.new_instance(Constants.TYPE_STRING_BUFFER);
            e4.dup();
            e4.invoke_constructor(Constants.TYPE_STRING_BUFFER);
            for (int i4 = 0; i4 < parameterTypes.length; i4++) {
                if (i4 > 0) {
                    e4.push(", ");
                    e4.invoke_virtual(Constants.TYPE_STRING_BUFFER, KeyFactory.APPEND_STRING);
                }
                e4.load_this();
                e4.getfield(getFieldName(i4));
                EmitUtils.append_string(e4, parameterTypes[i4], EmitUtils.DEFAULT_DELIMITERS, this.customizers);
            }
            e4.invoke_virtual(Constants.TYPE_STRING_BUFFER, KeyFactory.TO_STRING);
            e4.return_value();
            e4.end_method();
            ce.end_class();
        }

        private String getFieldName(int arg) {
            return "FIELD_" + arg;
        }
    }
}
