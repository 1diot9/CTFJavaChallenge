package org.springframework.cglib.proxy;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.Type;
import org.springframework.cglib.core.ClassEmitter;
import org.springframework.cglib.core.CodeEmitter;
import org.springframework.cglib.core.Constants;
import org.springframework.cglib.core.EmitUtils;
import org.springframework.cglib.core.MethodInfo;
import org.springframework.cglib.core.MethodWrapper;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.cglib.core.Signature;
import org.springframework.cglib.core.TypeUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/proxy/MixinEmitter.class */
class MixinEmitter extends ClassEmitter {
    private static final String FIELD_NAME = "CGLIB$DELEGATES";
    private static final Signature CSTRUCT_OBJECT_ARRAY = TypeUtils.parseConstructor("Object[]");
    private static final Type MIXIN = TypeUtils.parseType("org.springframework.cglib.proxy.Mixin");
    private static final Signature NEW_INSTANCE = new Signature("newInstance", MIXIN, new Type[]{Constants.TYPE_OBJECT_ARRAY});

    public MixinEmitter(ClassVisitor v, String className, Class[] classes, int[] route) {
        super(v);
        begin_class(52, 1, className, MIXIN, TypeUtils.getTypes(getInterfaces(classes)), Constants.SOURCE_FILE);
        EmitUtils.null_constructor(this);
        EmitUtils.factory_method(this, NEW_INSTANCE);
        declare_field(2, FIELD_NAME, Constants.TYPE_OBJECT_ARRAY, null);
        CodeEmitter e = begin_method(1, CSTRUCT_OBJECT_ARRAY, null);
        e.load_this();
        e.super_invoke_constructor();
        e.load_this();
        e.load_arg(0);
        e.putfield(FIELD_NAME);
        e.return_value();
        e.end_method();
        Set unique = new HashSet();
        for (int i = 0; i < classes.length; i++) {
            Method[] methods = getMethods(classes[i]);
            for (Method method : methods) {
                if (unique.add(MethodWrapper.create(method))) {
                    MethodInfo methodInfo = ReflectUtils.getMethodInfo(method);
                    int modifiers = 1;
                    CodeEmitter e2 = EmitUtils.begin_method(this, methodInfo, (methodInfo.getModifiers() & 128) == 128 ? 1 | 128 : modifiers);
                    e2.load_this();
                    e2.getfield(FIELD_NAME);
                    e2.aaload(route != null ? route[i] : i);
                    e2.checkcast(methodInfo.getClassInfo().getType());
                    e2.load_args();
                    e2.invoke(methodInfo);
                    e2.return_value();
                    e2.end_method();
                }
            }
        }
        end_class();
    }

    protected Class[] getInterfaces(Class[] classes) {
        return classes;
    }

    protected Method[] getMethods(Class type) {
        return type.getMethods();
    }
}
