package org.springframework.cglib.beans;

import java.beans.PropertyDescriptor;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.Type;
import org.springframework.cglib.core.AbstractClassGenerator;
import org.springframework.cglib.core.ClassEmitter;
import org.springframework.cglib.core.Constants;
import org.springframework.cglib.core.EmitUtils;
import org.springframework.cglib.core.KeyFactory;
import org.springframework.cglib.core.ReflectUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/beans/BeanGenerator.class */
public class BeanGenerator extends AbstractClassGenerator {
    private static final AbstractClassGenerator.Source SOURCE = new AbstractClassGenerator.Source(BeanGenerator.class.getName());
    private static final BeanGeneratorKey KEY_FACTORY = (BeanGeneratorKey) KeyFactory.create(BeanGeneratorKey.class);
    private Class superclass;
    private Map props;
    private boolean classOnly;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/beans/BeanGenerator$BeanGeneratorKey.class */
    public interface BeanGeneratorKey {
        Object newInstance(String superclass, Map props);
    }

    public BeanGenerator() {
        super(SOURCE);
        this.props = new HashMap();
    }

    public void setSuperclass(Class superclass) {
        if (superclass != null && superclass.equals(Object.class)) {
            superclass = null;
        }
        this.superclass = superclass;
        setContextClass(superclass);
    }

    public void addProperty(String name, Class type) {
        if (this.props.containsKey(name)) {
            throw new IllegalArgumentException("Duplicate property name \"" + name + "\"");
        }
        this.props.put(name, Type.getType((Class<?>) type));
    }

    @Override // org.springframework.cglib.core.AbstractClassGenerator
    protected ClassLoader getDefaultClassLoader() {
        if (this.superclass != null) {
            return this.superclass.getClassLoader();
        }
        return null;
    }

    @Override // org.springframework.cglib.core.AbstractClassGenerator
    protected ProtectionDomain getProtectionDomain() {
        return ReflectUtils.getProtectionDomain(this.superclass);
    }

    public Object create() {
        this.classOnly = false;
        return createHelper();
    }

    public Object createClass() {
        this.classOnly = true;
        return createHelper();
    }

    private Object createHelper() {
        if (this.superclass != null) {
            setNamePrefix(this.superclass.getName());
        }
        String superName = this.superclass != null ? this.superclass.getName() : "java.lang.Object";
        Object key = KEY_FACTORY.newInstance(superName, this.props);
        return super.create(key);
    }

    @Override // org.springframework.cglib.core.ClassGenerator
    public void generateClass(ClassVisitor v) throws Exception {
        int size = this.props.size();
        String[] names = (String[]) this.props.keySet().toArray(new String[size]);
        Type[] types = new Type[size];
        for (int i = 0; i < size; i++) {
            types[i] = (Type) this.props.get(names[i]);
        }
        ClassEmitter ce = new ClassEmitter(v);
        ce.begin_class(52, 1, getClassName(), this.superclass != null ? Type.getType((Class<?>) this.superclass) : Constants.TYPE_OBJECT, null, null);
        EmitUtils.null_constructor(ce);
        EmitUtils.add_properties(ce, names, types);
        ce.end_class();
    }

    @Override // org.springframework.cglib.core.AbstractClassGenerator
    protected Object firstInstance(Class type) {
        if (this.classOnly) {
            return type;
        }
        return ReflectUtils.newInstance(type);
    }

    @Override // org.springframework.cglib.core.AbstractClassGenerator
    protected Object nextInstance(Object instance) {
        Class<?> cls;
        if (instance instanceof Class) {
            Class<?> clazz = (Class) instance;
            cls = clazz;
        } else {
            cls = instance.getClass();
        }
        Class<?> protoclass = cls;
        if (this.classOnly) {
            return protoclass;
        }
        return ReflectUtils.newInstance(protoclass);
    }

    public static void addProperties(BeanGenerator gen, Map props) {
        for (String name : props.keySet()) {
            gen.addProperty(name, (Class) props.get(name));
        }
    }

    public static void addProperties(BeanGenerator gen, Class type) {
        addProperties(gen, ReflectUtils.getBeanProperties(type));
    }

    public static void addProperties(BeanGenerator gen, PropertyDescriptor[] descriptors) {
        for (PropertyDescriptor descriptor : descriptors) {
            gen.addProperty(descriptor.getName(), descriptor.getPropertyType());
        }
    }
}
