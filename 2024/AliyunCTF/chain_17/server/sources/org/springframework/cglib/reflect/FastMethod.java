package org.springframework.cglib.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.springframework.asm.Type;
import org.springframework.cglib.core.Signature;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/reflect/FastMethod.class */
public class FastMethod extends FastMember {
    /* JADX INFO: Access modifiers changed from: package-private */
    public FastMethod(FastClass fc, Method method) {
        super(fc, method, helper(fc, method));
    }

    private static int helper(FastClass fc, Method method) {
        int index = fc.getIndex(new Signature(method.getName(), Type.getMethodDescriptor(method)));
        if (index < 0) {
            Class[] types = method.getParameterTypes();
            System.err.println("hash=" + method.getName().hashCode() + " size=" + types.length);
            for (int i = 0; i < types.length; i++) {
                System.err.println("  types[" + i + "]=" + types[i].getName());
            }
            throw new IllegalArgumentException("Cannot find method " + method);
        }
        return index;
    }

    public Class getReturnType() {
        return ((Method) this.member).getReturnType();
    }

    @Override // org.springframework.cglib.reflect.FastMember
    public Class[] getParameterTypes() {
        return ((Method) this.member).getParameterTypes();
    }

    @Override // org.springframework.cglib.reflect.FastMember
    public Class[] getExceptionTypes() {
        return ((Method) this.member).getExceptionTypes();
    }

    public Object invoke(Object obj, Object[] args) throws InvocationTargetException {
        return this.fc.invoke(this.index, obj, args);
    }

    public Method getJavaMethod() {
        return (Method) this.member;
    }
}
