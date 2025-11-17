package org.springframework.cglib.transform.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.springframework.asm.Type;
import org.springframework.cglib.core.CodeEmitter;
import org.springframework.cglib.core.CodeGenerationException;
import org.springframework.cglib.core.Constants;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.cglib.core.Signature;
import org.springframework.cglib.core.TypeUtils;
import org.springframework.cglib.transform.ClassEmitterTransformer;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/transform/impl/AddDelegateTransformer.class */
public class AddDelegateTransformer extends ClassEmitterTransformer {
    private static final String DELEGATE = "$CGLIB_DELEGATE";
    private static final Signature CSTRUCT_OBJECT = TypeUtils.parseSignature("void <init>(Object)");
    private Class[] delegateIf;
    private Class delegateImpl;
    private Type delegateType;

    public AddDelegateTransformer(Class[] delegateIf, Class delegateImpl) {
        try {
            delegateImpl.getConstructor(Object.class);
            this.delegateIf = delegateIf;
            this.delegateImpl = delegateImpl;
            this.delegateType = Type.getType((Class<?>) delegateImpl);
        } catch (NoSuchMethodException e) {
            throw new CodeGenerationException(e);
        }
    }

    @Override // org.springframework.cglib.core.ClassEmitter
    public void begin_class(int version, int access, String className, Type superType, Type[] interfaces, String sourceFile) {
        if (!TypeUtils.isInterface(access)) {
            Type[] all = TypeUtils.add(interfaces, TypeUtils.getTypes(this.delegateIf));
            super.begin_class(version, access, className, superType, all, sourceFile);
            declare_field(130, DELEGATE, this.delegateType, null);
            for (Class element : this.delegateIf) {
                Method[] methods = element.getMethods();
                for (Method method : methods) {
                    if (Modifier.isAbstract(method.getModifiers())) {
                        addDelegate(method);
                    }
                }
            }
            return;
        }
        super.begin_class(version, access, className, superType, interfaces, sourceFile);
    }

    @Override // org.springframework.cglib.core.ClassEmitter
    public CodeEmitter begin_method(int access, Signature sig, Type[] exceptions) {
        CodeEmitter e = super.begin_method(access, sig, exceptions);
        if (sig.getName().equals(Constants.CONSTRUCTOR_NAME)) {
            return new CodeEmitter(e) { // from class: org.springframework.cglib.transform.impl.AddDelegateTransformer.1
                private boolean transformInit = true;

                @Override // org.springframework.asm.MethodVisitor
                public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                    super.visitMethodInsn(opcode, owner, name, desc, itf);
                    if (this.transformInit && opcode == 183) {
                        load_this();
                        new_instance(AddDelegateTransformer.this.delegateType);
                        dup();
                        load_this();
                        invoke_constructor(AddDelegateTransformer.this.delegateType, AddDelegateTransformer.CSTRUCT_OBJECT);
                        putfield(AddDelegateTransformer.DELEGATE);
                        this.transformInit = false;
                    }
                }
            };
        }
        return e;
    }

    private void addDelegate(Method m) {
        try {
            Method delegate = this.delegateImpl.getMethod(m.getName(), m.getParameterTypes());
            if (!delegate.getReturnType().getName().equals(m.getReturnType().getName())) {
                throw new IllegalArgumentException("Invalid delegate signature " + delegate);
            }
            Signature sig = ReflectUtils.getSignature(m);
            Type[] exceptions = TypeUtils.getTypes(m.getExceptionTypes());
            CodeEmitter e = super.begin_method(1, sig, exceptions);
            e.load_this();
            e.getfield(DELEGATE);
            e.load_args();
            e.invoke_virtual(this.delegateType, sig);
            e.return_value();
            e.end_method();
        } catch (NoSuchMethodException e2) {
            throw new CodeGenerationException(e2);
        }
    }
}
