package org.springframework.cglib.transform.impl;

import org.springframework.asm.Type;
import org.springframework.cglib.core.CodeEmitter;
import org.springframework.cglib.core.Constants;
import org.springframework.cglib.core.Signature;
import org.springframework.cglib.core.TypeUtils;
import org.springframework.cglib.transform.ClassEmitterTransformer;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/transform/impl/AccessFieldTransformer.class */
public class AccessFieldTransformer extends ClassEmitterTransformer {
    private Callback callback;

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/transform/impl/AccessFieldTransformer$Callback.class */
    public interface Callback {
        String getPropertyName(Type owner, String fieldName);
    }

    public AccessFieldTransformer(Callback callback) {
        this.callback = callback;
    }

    @Override // org.springframework.cglib.core.ClassEmitter
    public void declare_field(int access, final String name, Type type, Object value) {
        super.declare_field(access, name, type, value);
        String property = TypeUtils.upperFirst(this.callback.getPropertyName(getClassType(), name));
        if (property != null) {
            CodeEmitter e = begin_method(1, new Signature("get" + property, type, Constants.TYPES_EMPTY), null);
            e.load_this();
            e.getfield(name);
            e.return_value();
            e.end_method();
            CodeEmitter e2 = begin_method(1, new Signature("set" + property, Type.VOID_TYPE, new Type[]{type}), null);
            e2.load_this();
            e2.load_arg(0);
            e2.putfield(name);
            e2.return_value();
            e2.end_method();
        }
    }
}
