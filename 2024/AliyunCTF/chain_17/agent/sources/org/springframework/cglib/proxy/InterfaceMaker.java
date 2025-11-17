package org.springframework.cglib.proxy;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.Type;
import org.springframework.cglib.core.AbstractClassGenerator;
import org.springframework.cglib.core.ClassEmitter;
import org.springframework.cglib.core.Constants;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.cglib.core.Signature;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/proxy/InterfaceMaker.class */
public class InterfaceMaker extends AbstractClassGenerator {
    private static final AbstractClassGenerator.Source SOURCE = new AbstractClassGenerator.Source(InterfaceMaker.class.getName());
    private Map signatures;

    public InterfaceMaker() {
        super(SOURCE);
        this.signatures = new HashMap();
    }

    public void add(Signature sig, Type[] exceptions) {
        this.signatures.put(sig, exceptions);
    }

    public void add(Method method) {
        add(ReflectUtils.getSignature(method), ReflectUtils.getExceptionTypes(method));
    }

    public void add(Class clazz) {
        Method[] methods = clazz.getMethods();
        for (Method m : methods) {
            if (!m.getDeclaringClass().getName().equals("java.lang.Object")) {
                add(m);
            }
        }
    }

    public Class create() {
        setUseCache(false);
        return (Class) super.create(this);
    }

    @Override // org.springframework.cglib.core.AbstractClassGenerator
    protected ClassLoader getDefaultClassLoader() {
        return null;
    }

    @Override // org.springframework.cglib.core.AbstractClassGenerator
    protected Object firstInstance(Class type) {
        return type;
    }

    @Override // org.springframework.cglib.core.AbstractClassGenerator
    protected Object nextInstance(Object instance) {
        throw new IllegalStateException("InterfaceMaker does not cache");
    }

    @Override // org.springframework.cglib.core.ClassGenerator
    public void generateClass(ClassVisitor v) throws Exception {
        ClassEmitter ce = new ClassEmitter(v);
        ce.begin_class(52, 1537, getClassName(), null, null, Constants.SOURCE_FILE);
        for (Signature sig : this.signatures.keySet()) {
            Type[] exceptions = (Type[]) this.signatures.get(sig);
            ce.begin_method(1025, sig, exceptions).end_method();
        }
        ce.end_class();
    }
}
