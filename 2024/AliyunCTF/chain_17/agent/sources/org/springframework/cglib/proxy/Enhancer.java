package org.springframework.cglib.proxy;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.runtime.ObjectMethods;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.Label;
import org.springframework.asm.Type;
import org.springframework.cglib.core.AbstractClassGenerator;
import org.springframework.cglib.core.ClassEmitter;
import org.springframework.cglib.core.CodeEmitter;
import org.springframework.cglib.core.CodeGenerationException;
import org.springframework.cglib.core.CollectionUtils;
import org.springframework.cglib.core.Constants;
import org.springframework.cglib.core.DuplicatesPredicate;
import org.springframework.cglib.core.EmitUtils;
import org.springframework.cglib.core.Local;
import org.springframework.cglib.core.MethodInfo;
import org.springframework.cglib.core.MethodInfoTransformer;
import org.springframework.cglib.core.MethodWrapper;
import org.springframework.cglib.core.ObjectSwitchCallback;
import org.springframework.cglib.core.ProcessSwitchCallback;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.cglib.core.RejectModifierPredicate;
import org.springframework.cglib.core.Signature;
import org.springframework.cglib.core.TypeUtils;
import org.springframework.cglib.core.VisibilityPredicate;
import org.springframework.cglib.core.WeakCacheKey;
import org.springframework.cglib.proxy.CallbackGenerator;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/proxy/Enhancer.class */
public class Enhancer extends AbstractClassGenerator {
    private static final String BOUND_FIELD = "CGLIB$BOUND";
    private static final String FACTORY_DATA_FIELD = "CGLIB$FACTORY_DATA";
    private static final String THREAD_CALLBACKS_FIELD = "CGLIB$THREAD_CALLBACKS";
    private static final String STATIC_CALLBACKS_FIELD = "CGLIB$STATIC_CALLBACKS";
    private static final String CONSTRUCTED_FIELD = "CGLIB$CONSTRUCTED";
    private static final String CALLBACK_FILTER_FIELD = "CGLIB$CALLBACK_FILTER";
    private EnhancerFactoryData currentData;
    private Object currentKey;
    private Class[] interfaces;
    private CallbackFilter filter;
    private Callback[] callbacks;
    private Type[] callbackTypes;
    private boolean validateCallbackTypes;
    private boolean classOnly;
    private Class superclass;
    private Class[] argumentTypes;
    private Object[] arguments;
    private boolean useFactory;
    private Long serialVersionUID;
    private boolean interceptDuringConstruction;
    private static final CallbackFilter ALL_ZERO = method -> {
        return 0;
    };
    private static final AbstractClassGenerator.Source SOURCE = new AbstractClassGenerator.Source(Enhancer.class.getName());
    private static final Type OBJECT_TYPE = TypeUtils.parseType("Object");
    private static final Type FACTORY = TypeUtils.parseType("org.springframework.cglib.proxy.Factory");
    private static final Type ILLEGAL_STATE_EXCEPTION = TypeUtils.parseType("IllegalStateException");
    private static final Type ILLEGAL_ARGUMENT_EXCEPTION = TypeUtils.parseType("IllegalArgumentException");
    private static final Type THREAD_LOCAL = TypeUtils.parseType("ThreadLocal");
    private static final Type CALLBACK = TypeUtils.parseType("org.springframework.cglib.proxy.Callback");
    private static final Type CALLBACK_ARRAY = Type.getType((Class<?>) Callback[].class);
    private static final Signature CSTRUCT_NULL = TypeUtils.parseConstructor("");
    private static final String SET_THREAD_CALLBACKS_NAME = "CGLIB$SET_THREAD_CALLBACKS";
    private static final Signature SET_THREAD_CALLBACKS = new Signature(SET_THREAD_CALLBACKS_NAME, Type.VOID_TYPE, new Type[]{CALLBACK_ARRAY});
    private static final String SET_STATIC_CALLBACKS_NAME = "CGLIB$SET_STATIC_CALLBACKS";
    private static final Signature SET_STATIC_CALLBACKS = new Signature(SET_STATIC_CALLBACKS_NAME, Type.VOID_TYPE, new Type[]{CALLBACK_ARRAY});
    private static final Signature NEW_INSTANCE = new Signature("newInstance", Constants.TYPE_OBJECT, new Type[]{CALLBACK_ARRAY});
    private static final Signature MULTIARG_NEW_INSTANCE = new Signature("newInstance", Constants.TYPE_OBJECT, new Type[]{Constants.TYPE_CLASS_ARRAY, Constants.TYPE_OBJECT_ARRAY, CALLBACK_ARRAY});
    private static final Signature SINGLE_NEW_INSTANCE = new Signature("newInstance", Constants.TYPE_OBJECT, new Type[]{CALLBACK});
    private static final Signature SET_CALLBACK = new Signature("setCallback", Type.VOID_TYPE, new Type[]{Type.INT_TYPE, CALLBACK});
    private static final Signature GET_CALLBACK = new Signature("getCallback", CALLBACK, new Type[]{Type.INT_TYPE});
    private static final Signature SET_CALLBACKS = new Signature("setCallbacks", Type.VOID_TYPE, new Type[]{CALLBACK_ARRAY});
    private static final Signature GET_CALLBACKS = new Signature("getCallbacks", CALLBACK_ARRAY, new Type[0]);
    private static final Signature THREAD_LOCAL_GET = TypeUtils.parseSignature("Object get()");
    private static final Signature THREAD_LOCAL_SET = TypeUtils.parseSignature("void set(Object)");
    private static final Signature BIND_CALLBACKS = TypeUtils.parseSignature("void CGLIB$BIND_CALLBACKS(Object)");

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/proxy/Enhancer$EnhancerKey.class */
    public static final class EnhancerKey extends Record {
        private final String type;
        private final List<String> interfaces;
        private final WeakCacheKey<CallbackFilter> filter;
        private final List<Type> callbackTypes;
        private final boolean useFactory;
        private final boolean interceptDuringConstruction;
        private final Long serialVersionUID;

        private EnhancerKey(String type, List<String> interfaces, WeakCacheKey<CallbackFilter> filter, List<Type> callbackTypes, boolean useFactory, boolean interceptDuringConstruction, Long serialVersionUID) {
            this.type = type;
            this.interfaces = interfaces;
            this.filter = filter;
            this.callbackTypes = callbackTypes;
            this.useFactory = useFactory;
            this.interceptDuringConstruction = interceptDuringConstruction;
            this.serialVersionUID = serialVersionUID;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, EnhancerKey.class), EnhancerKey.class, "type;interfaces;filter;callbackTypes;useFactory;interceptDuringConstruction;serialVersionUID", "FIELD:Lorg/springframework/cglib/proxy/Enhancer$EnhancerKey;->type:Ljava/lang/String;", "FIELD:Lorg/springframework/cglib/proxy/Enhancer$EnhancerKey;->interfaces:Ljava/util/List;", "FIELD:Lorg/springframework/cglib/proxy/Enhancer$EnhancerKey;->filter:Lorg/springframework/cglib/core/WeakCacheKey;", "FIELD:Lorg/springframework/cglib/proxy/Enhancer$EnhancerKey;->callbackTypes:Ljava/util/List;", "FIELD:Lorg/springframework/cglib/proxy/Enhancer$EnhancerKey;->useFactory:Z", "FIELD:Lorg/springframework/cglib/proxy/Enhancer$EnhancerKey;->interceptDuringConstruction:Z", "FIELD:Lorg/springframework/cglib/proxy/Enhancer$EnhancerKey;->serialVersionUID:Ljava/lang/Long;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, EnhancerKey.class), EnhancerKey.class, "type;interfaces;filter;callbackTypes;useFactory;interceptDuringConstruction;serialVersionUID", "FIELD:Lorg/springframework/cglib/proxy/Enhancer$EnhancerKey;->type:Ljava/lang/String;", "FIELD:Lorg/springframework/cglib/proxy/Enhancer$EnhancerKey;->interfaces:Ljava/util/List;", "FIELD:Lorg/springframework/cglib/proxy/Enhancer$EnhancerKey;->filter:Lorg/springframework/cglib/core/WeakCacheKey;", "FIELD:Lorg/springframework/cglib/proxy/Enhancer$EnhancerKey;->callbackTypes:Ljava/util/List;", "FIELD:Lorg/springframework/cglib/proxy/Enhancer$EnhancerKey;->useFactory:Z", "FIELD:Lorg/springframework/cglib/proxy/Enhancer$EnhancerKey;->interceptDuringConstruction:Z", "FIELD:Lorg/springframework/cglib/proxy/Enhancer$EnhancerKey;->serialVersionUID:Ljava/lang/Long;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, EnhancerKey.class, Object.class), EnhancerKey.class, "type;interfaces;filter;callbackTypes;useFactory;interceptDuringConstruction;serialVersionUID", "FIELD:Lorg/springframework/cglib/proxy/Enhancer$EnhancerKey;->type:Ljava/lang/String;", "FIELD:Lorg/springframework/cglib/proxy/Enhancer$EnhancerKey;->interfaces:Ljava/util/List;", "FIELD:Lorg/springframework/cglib/proxy/Enhancer$EnhancerKey;->filter:Lorg/springframework/cglib/core/WeakCacheKey;", "FIELD:Lorg/springframework/cglib/proxy/Enhancer$EnhancerKey;->callbackTypes:Ljava/util/List;", "FIELD:Lorg/springframework/cglib/proxy/Enhancer$EnhancerKey;->useFactory:Z", "FIELD:Lorg/springframework/cglib/proxy/Enhancer$EnhancerKey;->interceptDuringConstruction:Z", "FIELD:Lorg/springframework/cglib/proxy/Enhancer$EnhancerKey;->serialVersionUID:Ljava/lang/Long;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public String type() {
            return this.type;
        }

        public List<String> interfaces() {
            return this.interfaces;
        }

        public WeakCacheKey<CallbackFilter> filter() {
            return this.filter;
        }

        public List<Type> callbackTypes() {
            return this.callbackTypes;
        }

        public boolean useFactory() {
            return this.useFactory;
        }

        public boolean interceptDuringConstruction() {
            return this.interceptDuringConstruction;
        }

        public Long serialVersionUID() {
            return this.serialVersionUID;
        }
    }

    public Enhancer() {
        super(SOURCE);
        this.useFactory = true;
        this.interceptDuringConstruction = true;
    }

    public void setSuperclass(Class superclass) {
        if (superclass != null && superclass.isInterface()) {
            setInterfaces(new Class[]{superclass});
            setContextClass(superclass);
        } else if (superclass != null && superclass.equals(Object.class)) {
            this.superclass = null;
        } else {
            this.superclass = superclass;
            setContextClass(superclass);
        }
    }

    public void setInterfaces(Class[] interfaces) {
        this.interfaces = interfaces;
    }

    public void setCallbackFilter(CallbackFilter filter) {
        this.filter = filter;
    }

    public void setCallback(final Callback callback) {
        setCallbacks(new Callback[]{callback});
    }

    public void setCallbacks(Callback[] callbacks) {
        if (callbacks != null && callbacks.length == 0) {
            throw new IllegalArgumentException("Array cannot be empty");
        }
        this.callbacks = callbacks;
    }

    public void setUseFactory(boolean useFactory) {
        this.useFactory = useFactory;
    }

    public void setInterceptDuringConstruction(boolean interceptDuringConstruction) {
        this.interceptDuringConstruction = interceptDuringConstruction;
    }

    public void setCallbackType(Class callbackType) {
        setCallbackTypes(new Class[]{callbackType});
    }

    public void setCallbackTypes(Class[] callbackTypes) {
        if (callbackTypes != null && callbackTypes.length == 0) {
            throw new IllegalArgumentException("Array cannot be empty");
        }
        this.callbackTypes = CallbackInfo.determineTypes(callbackTypes);
    }

    public Object create() {
        this.classOnly = false;
        this.argumentTypes = null;
        return createHelper();
    }

    public Object create(Class[] argumentTypes, Object[] arguments) {
        this.classOnly = false;
        if (argumentTypes == null || arguments == null || argumentTypes.length != arguments.length) {
            throw new IllegalArgumentException("Arguments must be non-null and of equal length");
        }
        this.argumentTypes = argumentTypes;
        this.arguments = arguments;
        return createHelper();
    }

    public Class createClass() {
        this.classOnly = true;
        return (Class) createHelper();
    }

    public void setSerialVersionUID(Long sUID) {
        this.serialVersionUID = sUID;
    }

    private void preValidate() {
        if (this.callbackTypes == null) {
            this.callbackTypes = CallbackInfo.determineTypes(this.callbacks, false);
            this.validateCallbackTypes = true;
        }
        if (this.filter == null) {
            if (this.callbackTypes.length > 1) {
                throw new IllegalStateException("Multiple callback types possible but no filter specified");
            }
            this.filter = ALL_ZERO;
        }
    }

    private void validate() {
        if (this.classOnly ^ (this.callbacks == null)) {
            if (this.classOnly) {
                throw new IllegalStateException("createClass does not accept callbacks");
            }
            throw new IllegalStateException("Callbacks are required");
        }
        if (this.classOnly && this.callbackTypes == null) {
            throw new IllegalStateException("Callback types are required");
        }
        if (this.validateCallbackTypes) {
            this.callbackTypes = null;
        }
        if (this.callbacks != null && this.callbackTypes != null) {
            if (this.callbacks.length != this.callbackTypes.length) {
                throw new IllegalStateException("Lengths of callback and callback types array must be the same");
            }
            Type[] check = CallbackInfo.determineTypes(this.callbacks);
            for (int i = 0; i < check.length; i++) {
                if (!check[i].equals(this.callbackTypes[i])) {
                    throw new IllegalStateException("Callback " + check[i] + " is not assignable to " + this.callbackTypes[i]);
                }
            }
        } else if (this.callbacks != null) {
            this.callbackTypes = CallbackInfo.determineTypes(this.callbacks);
        }
        if (this.interfaces != null) {
            for (Class element : this.interfaces) {
                if (element == null) {
                    throw new IllegalStateException("Interfaces cannot be null");
                }
                if (!element.isInterface()) {
                    throw new IllegalStateException(element + " is not an interface");
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/proxy/Enhancer$EnhancerFactoryData.class */
    public static class EnhancerFactoryData {
        public final Class generatedClass;
        private final Method setThreadCallbacks;
        private final Class[] primaryConstructorArgTypes;
        private final Constructor primaryConstructor;

        public EnhancerFactoryData(Class generatedClass, Class[] primaryConstructorArgTypes, boolean classOnly) {
            this.generatedClass = generatedClass;
            try {
                this.setThreadCallbacks = Enhancer.getCallbacksSetter(generatedClass, Enhancer.SET_THREAD_CALLBACKS_NAME);
                if (classOnly) {
                    this.primaryConstructorArgTypes = null;
                    this.primaryConstructor = null;
                } else {
                    this.primaryConstructorArgTypes = primaryConstructorArgTypes;
                    this.primaryConstructor = ReflectUtils.getConstructor(generatedClass, primaryConstructorArgTypes);
                }
            } catch (NoSuchMethodException e) {
                throw new CodeGenerationException(e);
            }
        }

        public Object newInstance(Class[] argumentTypes, Object[] arguments, Callback[] callbacks) {
            setThreadCallbacks(callbacks);
            try {
                if (this.primaryConstructorArgTypes == argumentTypes || Arrays.equals(this.primaryConstructorArgTypes, argumentTypes)) {
                    Object newInstance = ReflectUtils.newInstance(this.primaryConstructor, arguments);
                    setThreadCallbacks(null);
                    return newInstance;
                }
                Object newInstance2 = ReflectUtils.newInstance(this.generatedClass, argumentTypes, arguments);
                setThreadCallbacks(null);
                return newInstance2;
            } catch (Throwable th) {
                setThreadCallbacks(null);
                throw th;
            }
        }

        private void setThreadCallbacks(Callback[] callbacks) {
            try {
                this.setThreadCallbacks.invoke(this.generatedClass, callbacks);
            } catch (IllegalAccessException e) {
                throw new CodeGenerationException(e);
            } catch (InvocationTargetException e2) {
                throw new CodeGenerationException(e2.getTargetException());
            }
        }
    }

    private Object createHelper() {
        preValidate();
        Object key = new EnhancerKey(this.superclass != null ? this.superclass.getName() : null, this.interfaces != null ? Arrays.asList(ReflectUtils.getNames(this.interfaces)) : null, this.filter == ALL_ZERO ? null : new WeakCacheKey(this.filter), Arrays.asList(this.callbackTypes), this.useFactory, this.interceptDuringConstruction, this.serialVersionUID);
        this.currentKey = key;
        Object result = super.create(key);
        return result;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.cglib.core.AbstractClassGenerator
    public Class generate(AbstractClassGenerator.ClassLoaderData data) {
        validate();
        if (this.superclass != null) {
            setNamePrefix(this.superclass.getName());
        } else if (this.interfaces != null) {
            setNamePrefix(this.interfaces[ReflectUtils.findPackageProtected(this.interfaces)].getName());
        }
        return super.generate(data);
    }

    @Override // org.springframework.cglib.core.AbstractClassGenerator
    protected ClassLoader getDefaultClassLoader() {
        if (this.superclass != null) {
            return this.superclass.getClassLoader();
        }
        if (this.interfaces != null) {
            return this.interfaces[0].getClassLoader();
        }
        return null;
    }

    @Override // org.springframework.cglib.core.AbstractClassGenerator
    protected ProtectionDomain getProtectionDomain() {
        if (this.superclass != null) {
            return ReflectUtils.getProtectionDomain(this.superclass);
        }
        if (this.interfaces != null) {
            return ReflectUtils.getProtectionDomain(this.interfaces[0]);
        }
        return null;
    }

    private Signature rename(Signature sig, int index) {
        return new Signature("CGLIB$" + sig.getName() + "$" + index, sig.getDescriptor());
    }

    public static void getMethods(Class superclass, Class[] interfaces, List methods) {
        getMethods(superclass, interfaces, methods, null, null);
    }

    private static void getMethods(Class superclass, Class[] interfaces, List methods, List interfaceMethods, Set forcePublic) {
        ReflectUtils.addAllMethods(superclass, methods);
        List target = interfaceMethods != null ? interfaceMethods : methods;
        if (interfaces != null) {
            for (Class element : interfaces) {
                if (element != Factory.class) {
                    ReflectUtils.addAllMethods(element, target);
                }
            }
        }
        if (interfaceMethods != null) {
            if (forcePublic != null) {
                forcePublic.addAll(MethodWrapper.createSet(interfaceMethods));
            }
            methods.addAll(interfaceMethods);
        }
        CollectionUtils.filter(methods, new RejectModifierPredicate(8));
        CollectionUtils.filter(methods, new VisibilityPredicate(superclass, true));
        CollectionUtils.filter(methods, new DuplicatesPredicate());
        CollectionUtils.filter(methods, new RejectModifierPredicate(16));
    }

    @Override // org.springframework.cglib.core.ClassGenerator
    public void generateClass(ClassVisitor v) throws Exception {
        Type[] types;
        Class sc = this.superclass == null ? Object.class : this.superclass;
        if (TypeUtils.isFinal(sc.getModifiers())) {
            throw new IllegalArgumentException("Cannot subclass final class " + sc.getName());
        }
        List constructors = new ArrayList(Arrays.asList(sc.getDeclaredConstructors()));
        filterConstructors(sc, constructors);
        List actualMethods = new ArrayList();
        List interfaceMethods = new ArrayList();
        Set forcePublic = new HashSet();
        getMethods(sc, this.interfaces, actualMethods, interfaceMethods, forcePublic);
        List methods = CollectionUtils.transform(actualMethods, value -> {
            Method method = (Method) value;
            int modifiers = 16 | (method.getModifiers() & (-1025) & (-257) & (-33));
            if (forcePublic.contains(MethodWrapper.create(method))) {
                modifiers = (modifiers & (-5)) | 1;
            }
            return ReflectUtils.getMethodInfo(method, modifiers);
        });
        ClassEmitter e = new ClassEmitter(v);
        if (this.currentData == null) {
            String className = getClassName();
            Type type = Type.getType((Class<?>) sc);
            if (this.useFactory) {
                types = TypeUtils.add(TypeUtils.getTypes(this.interfaces), FACTORY);
            } else {
                types = TypeUtils.getTypes(this.interfaces);
            }
            e.begin_class(52, 1, className, type, types, Constants.SOURCE_FILE);
        } else {
            e.begin_class(52, 1, getClassName(), null, new Type[]{FACTORY}, Constants.SOURCE_FILE);
        }
        List constructorInfo = CollectionUtils.transform(constructors, MethodInfoTransformer.getInstance());
        e.declare_field(2, BOUND_FIELD, Type.BOOLEAN_TYPE, null);
        e.declare_field(9, FACTORY_DATA_FIELD, OBJECT_TYPE, null);
        if (!this.interceptDuringConstruction) {
            e.declare_field(2, CONSTRUCTED_FIELD, Type.BOOLEAN_TYPE, null);
        }
        e.declare_field(26, THREAD_CALLBACKS_FIELD, THREAD_LOCAL, null);
        e.declare_field(26, STATIC_CALLBACKS_FIELD, CALLBACK_ARRAY, null);
        if (this.serialVersionUID != null) {
            e.declare_field(26, Constants.SUID_FIELD_NAME, Type.LONG_TYPE, this.serialVersionUID);
        }
        for (int i = 0; i < this.callbackTypes.length; i++) {
            e.declare_field(2, getCallbackField(i), this.callbackTypes[i], null);
        }
        e.declare_field(10, CALLBACK_FILTER_FIELD, OBJECT_TYPE, null);
        if (this.currentData == null) {
            emitMethods(e, methods, actualMethods);
            emitConstructors(e, constructorInfo);
        } else {
            emitDefaultConstructor(e);
        }
        emitSetThreadCallbacks(e);
        emitSetStaticCallbacks(e);
        emitBindCallbacks(e);
        if (this.useFactory || this.currentData != null) {
            int[] keys = getCallbackKeys();
            emitNewInstanceCallbacks(e);
            emitNewInstanceCallback(e);
            emitNewInstanceMultiarg(e, constructorInfo);
            emitGetCallback(e, keys);
            emitSetCallback(e, keys);
            emitGetCallbacks(e);
            emitSetCallbacks(e);
        }
        e.end_class();
    }

    protected void filterConstructors(Class sc, List constructors) {
        CollectionUtils.filter(constructors, new VisibilityPredicate(sc, true));
        if (constructors.size() == 0) {
            throw new IllegalArgumentException("No visible constructors in " + sc);
        }
    }

    @Override // org.springframework.cglib.core.AbstractClassGenerator
    protected Object firstInstance(Class type) throws Exception {
        if (this.classOnly) {
            return type;
        }
        return createUsingReflection(type);
    }

    @Override // org.springframework.cglib.core.AbstractClassGenerator
    protected Object nextInstance(Object instance) {
        EnhancerFactoryData data = (EnhancerFactoryData) instance;
        if (this.classOnly) {
            return data.generatedClass;
        }
        Class[] argumentTypes = this.argumentTypes;
        Object[] arguments = this.arguments;
        if (argumentTypes == null) {
            argumentTypes = Constants.EMPTY_CLASS_ARRAY;
            arguments = null;
        }
        return data.newInstance(argumentTypes, arguments, this.callbacks);
    }

    @Override // org.springframework.cglib.core.AbstractClassGenerator
    protected Object wrapCachedClass(Class klass) {
        Class[] argumentTypes = this.argumentTypes;
        if (argumentTypes == null) {
            argumentTypes = Constants.EMPTY_CLASS_ARRAY;
        }
        EnhancerFactoryData factoryData = new EnhancerFactoryData(klass, argumentTypes, this.classOnly);
        try {
            Field factoryDataField = klass.getField(FACTORY_DATA_FIELD);
            factoryDataField.set(null, factoryData);
            Field callbackFilterField = klass.getDeclaredField(CALLBACK_FILTER_FIELD);
            callbackFilterField.setAccessible(true);
            callbackFilterField.set(null, this.filter);
            return new WeakReference(factoryData);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new CodeGenerationException(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.cglib.core.AbstractClassGenerator
    public Object unwrapCachedValue(Object cached) {
        if (this.currentKey instanceof EnhancerKey) {
            EnhancerFactoryData data = (EnhancerFactoryData) ((WeakReference) cached).get();
            return data;
        }
        return super.unwrapCachedValue(cached);
    }

    public static void registerCallbacks(Class generatedClass, Callback[] callbacks) {
        setThreadCallbacks(generatedClass, callbacks);
    }

    public static void registerStaticCallbacks(Class generatedClass, Callback[] callbacks) {
        setCallbacksHelper(generatedClass, callbacks, SET_STATIC_CALLBACKS_NAME);
    }

    public static boolean isEnhanced(Class type) {
        try {
            getCallbacksSetter(type, SET_THREAD_CALLBACKS_NAME);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    private static void setThreadCallbacks(Class type, Callback[] callbacks) {
        setCallbacksHelper(type, callbacks, SET_THREAD_CALLBACKS_NAME);
    }

    private static void setCallbacksHelper(Class type, Callback[] callbacks, String methodName) {
        try {
            Method setter = getCallbacksSetter(type, methodName);
            setter.invoke(null, callbacks);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new CodeGenerationException(e);
        } catch (NoSuchMethodException e2) {
            throw new IllegalArgumentException(type + " is not an enhanced class");
        }
    }

    private static Method getCallbacksSetter(Class type, String methodName) throws NoSuchMethodException {
        return type.getDeclaredMethod(methodName, Callback[].class);
    }

    private Object createUsingReflection(Class type) {
        setThreadCallbacks(type, this.callbacks);
        try {
            if (this.argumentTypes != null) {
                return ReflectUtils.newInstance(type, this.argumentTypes, this.arguments);
            }
            return ReflectUtils.newInstance(type);
        } finally {
            setThreadCallbacks(type, null);
        }
    }

    public static Object create(Class type, Callback callback) {
        Enhancer e = new Enhancer();
        e.setSuperclass(type);
        e.setCallback(callback);
        return e.create();
    }

    public static Object create(Class superclass, Class[] interfaces, Callback callback) {
        Enhancer e = new Enhancer();
        e.setSuperclass(superclass);
        e.setInterfaces(interfaces);
        e.setCallback(callback);
        return e.create();
    }

    public static Object create(Class superclass, Class[] interfaces, CallbackFilter filter, Callback[] callbacks) {
        Enhancer e = new Enhancer();
        e.setSuperclass(superclass);
        e.setInterfaces(interfaces);
        e.setCallbackFilter(filter);
        e.setCallbacks(callbacks);
        return e.create();
    }

    private void emitDefaultConstructor(ClassEmitter ce) {
        try {
            Constructor<Object> declaredConstructor = Object.class.getDeclaredConstructor(new Class[0]);
            MethodInfo constructor = (MethodInfo) MethodInfoTransformer.getInstance().transform(declaredConstructor);
            CodeEmitter e = EmitUtils.begin_method(ce, constructor, 1);
            e.load_this();
            e.dup();
            Signature sig = constructor.getSignature();
            e.super_invoke_constructor(sig);
            e.return_value();
            e.end_method();
        } catch (NoSuchMethodException e2) {
            throw new IllegalStateException("Object should have default constructor ", e2);
        }
    }

    private void emitConstructors(ClassEmitter ce, List constructors) {
        boolean seenNull = false;
        Iterator it = constructors.iterator();
        while (it.hasNext()) {
            MethodInfo constructor = (MethodInfo) it.next();
            if (this.currentData == null || "()V".equals(constructor.getSignature().getDescriptor())) {
                CodeEmitter e = EmitUtils.begin_method(ce, constructor, 1);
                e.load_this();
                e.dup();
                e.load_args();
                Signature sig = constructor.getSignature();
                seenNull = seenNull || sig.getDescriptor().equals("()V");
                e.super_invoke_constructor(sig);
                if (this.currentData == null) {
                    e.invoke_static_this(BIND_CALLBACKS);
                    if (!this.interceptDuringConstruction) {
                        e.load_this();
                        e.push(1);
                        e.putfield(CONSTRUCTED_FIELD);
                    }
                }
                e.return_value();
                e.end_method();
            }
        }
        if (!this.classOnly && !seenNull && this.arguments == null) {
            throw new IllegalArgumentException("Superclass has no null constructors but no arguments were given");
        }
    }

    private int[] getCallbackKeys() {
        int[] keys = new int[this.callbackTypes.length];
        for (int i = 0; i < this.callbackTypes.length; i++) {
            keys[i] = i;
        }
        return keys;
    }

    private void emitGetCallback(ClassEmitter ce, int[] keys) {
        final CodeEmitter e = ce.begin_method(1, GET_CALLBACK, null);
        e.load_this();
        e.invoke_static_this(BIND_CALLBACKS);
        e.load_this();
        e.load_arg(0);
        e.process_switch(keys, new ProcessSwitchCallback() { // from class: org.springframework.cglib.proxy.Enhancer.1
            @Override // org.springframework.cglib.core.ProcessSwitchCallback
            public void processCase(int key, Label end) {
                e.getfield(Enhancer.getCallbackField(key));
                e.goTo(end);
            }

            @Override // org.springframework.cglib.core.ProcessSwitchCallback
            public void processDefault() {
                e.pop();
                e.aconst_null();
            }
        });
        e.return_value();
        e.end_method();
    }

    private void emitSetCallback(ClassEmitter ce, int[] keys) {
        final CodeEmitter e = ce.begin_method(1, SET_CALLBACK, null);
        e.load_arg(0);
        e.process_switch(keys, new ProcessSwitchCallback() { // from class: org.springframework.cglib.proxy.Enhancer.2
            @Override // org.springframework.cglib.core.ProcessSwitchCallback
            public void processCase(int key, Label end) {
                e.load_this();
                e.load_arg(1);
                e.checkcast(Enhancer.this.callbackTypes[key]);
                e.putfield(Enhancer.getCallbackField(key));
                e.goTo(end);
            }

            @Override // org.springframework.cglib.core.ProcessSwitchCallback
            public void processDefault() {
            }
        });
        e.return_value();
        e.end_method();
    }

    private void emitSetCallbacks(ClassEmitter ce) {
        CodeEmitter e = ce.begin_method(1, SET_CALLBACKS, null);
        e.load_this();
        e.load_arg(0);
        for (int i = 0; i < this.callbackTypes.length; i++) {
            e.dup2();
            e.aaload(i);
            e.checkcast(this.callbackTypes[i]);
            e.putfield(getCallbackField(i));
        }
        e.return_value();
        e.end_method();
    }

    private void emitGetCallbacks(ClassEmitter ce) {
        CodeEmitter e = ce.begin_method(1, GET_CALLBACKS, null);
        e.load_this();
        e.invoke_static_this(BIND_CALLBACKS);
        e.load_this();
        e.push(this.callbackTypes.length);
        e.newarray(CALLBACK);
        for (int i = 0; i < this.callbackTypes.length; i++) {
            e.dup();
            e.push(i);
            e.load_this();
            e.getfield(getCallbackField(i));
            e.aastore();
        }
        e.return_value();
        e.end_method();
    }

    private void emitNewInstanceCallbacks(ClassEmitter ce) {
        CodeEmitter e = ce.begin_method(1, NEW_INSTANCE, null);
        Type thisType = getThisType(e);
        e.load_arg(0);
        e.invoke_static(thisType, SET_THREAD_CALLBACKS, false);
        emitCommonNewInstance(e);
    }

    private Type getThisType(CodeEmitter e) {
        if (this.currentData == null) {
            return e.getClassEmitter().getClassType();
        }
        return Type.getType((Class<?>) this.currentData.generatedClass);
    }

    private void emitCommonNewInstance(CodeEmitter e) {
        Type thisType = getThisType(e);
        e.new_instance(thisType);
        e.dup();
        e.invoke_constructor(thisType);
        e.aconst_null();
        e.invoke_static(thisType, SET_THREAD_CALLBACKS, false);
        e.return_value();
        e.end_method();
    }

    private void emitNewInstanceCallback(ClassEmitter ce) {
        CodeEmitter e = ce.begin_method(1, SINGLE_NEW_INSTANCE, null);
        switch (this.callbackTypes.length) {
            case 0:
                break;
            case 1:
                e.push(1);
                e.newarray(CALLBACK);
                e.dup();
                e.push(0);
                e.load_arg(0);
                e.aastore();
                e.invoke_static(getThisType(e), SET_THREAD_CALLBACKS, false);
                break;
            default:
                e.throw_exception(ILLEGAL_STATE_EXCEPTION, "More than one callback object required");
                break;
        }
        emitCommonNewInstance(e);
    }

    private void emitNewInstanceMultiarg(ClassEmitter ce, List constructors) {
        final CodeEmitter e = ce.begin_method(1, MULTIARG_NEW_INSTANCE, null);
        final Type thisType = getThisType(e);
        e.load_arg(2);
        e.invoke_static(thisType, SET_THREAD_CALLBACKS, false);
        e.new_instance(thisType);
        e.dup();
        e.load_arg(0);
        EmitUtils.constructor_switch(e, constructors, new ObjectSwitchCallback() { // from class: org.springframework.cglib.proxy.Enhancer.3
            @Override // org.springframework.cglib.core.ObjectSwitchCallback
            public void processCase(Object key, Label end) {
                MethodInfo constructor = (MethodInfo) key;
                Type[] types = constructor.getSignature().getArgumentTypes();
                for (int i = 0; i < types.length; i++) {
                    e.load_arg(1);
                    e.push(i);
                    e.aaload();
                    e.unbox(types[i]);
                }
                e.invoke_constructor(thisType, constructor.getSignature());
                e.goTo(end);
            }

            @Override // org.springframework.cglib.core.ObjectSwitchCallback
            public void processDefault() {
                e.throw_exception(Enhancer.ILLEGAL_ARGUMENT_EXCEPTION, "Constructor not found");
            }
        });
        e.aconst_null();
        e.invoke_static(thisType, SET_THREAD_CALLBACKS, false);
        e.return_value();
        e.end_method();
    }

    private void emitMethods(final ClassEmitter ce, List methods, List actualMethods) {
        CallbackGenerator[] generators = CallbackInfo.getGenerators(this.callbackTypes);
        Map groups = new HashMap();
        final Map indexes = new HashMap();
        final Map originalModifiers = new HashMap();
        final Map positions = CollectionUtils.getIndexMap(methods);
        Map declToBridge = new HashMap();
        Iterator it1 = methods.iterator();
        Iterator it2 = actualMethods != null ? actualMethods.iterator() : null;
        while (it1.hasNext()) {
            MethodInfo method = (MethodInfo) it1.next();
            Method actualMethod = it2 != null ? (Method) it2.next() : null;
            int index = this.filter.accept(actualMethod);
            if (index >= this.callbackTypes.length) {
                throw new IllegalArgumentException("Callback filter returned an index that is too large: " + index);
            }
            originalModifiers.put(method, Integer.valueOf(actualMethod != null ? actualMethod.getModifiers() : method.getModifiers()));
            indexes.put(method, Integer.valueOf(index));
            List group = (List) groups.get(generators[index]);
            if (group == null) {
                CallbackGenerator callbackGenerator = generators[index];
                ArrayList arrayList = new ArrayList(methods.size());
                group = arrayList;
                groups.put(callbackGenerator, arrayList);
            }
            group.add(method);
            if (TypeUtils.isBridge(actualMethod.getModifiers())) {
                Set bridges = (Set) declToBridge.get(actualMethod.getDeclaringClass());
                if (bridges == null) {
                    bridges = new HashSet();
                    declToBridge.put(actualMethod.getDeclaringClass(), bridges);
                }
                bridges.add(method.getSignature());
            }
        }
        final Map bridgeToTarget = new BridgeMethodResolver(declToBridge, getClassLoader()).resolveAll();
        Set seenGen = new HashSet();
        CodeEmitter se = ce.getStaticHook();
        se.new_instance(THREAD_LOCAL);
        se.dup();
        se.invoke_constructor(THREAD_LOCAL, CSTRUCT_NULL);
        se.putfield(THREAD_CALLBACKS_FIELD);
        CallbackGenerator.Context context = new CallbackGenerator.Context() { // from class: org.springframework.cglib.proxy.Enhancer.4
            @Override // org.springframework.cglib.proxy.CallbackGenerator.Context
            public ClassLoader getClassLoader() {
                return Enhancer.this.getClassLoader();
            }

            @Override // org.springframework.cglib.proxy.CallbackGenerator.Context
            public int getOriginalModifiers(MethodInfo method2) {
                return ((Integer) originalModifiers.get(method2)).intValue();
            }

            @Override // org.springframework.cglib.proxy.CallbackGenerator.Context
            public int getIndex(MethodInfo method2) {
                return ((Integer) indexes.get(method2)).intValue();
            }

            @Override // org.springframework.cglib.proxy.CallbackGenerator.Context
            public void emitCallback(CodeEmitter e, int index2) {
                Enhancer.this.emitCurrentCallback(e, index2);
            }

            @Override // org.springframework.cglib.proxy.CallbackGenerator.Context
            public Signature getImplSignature(MethodInfo method2) {
                return Enhancer.this.rename(method2.getSignature(), ((Integer) positions.get(method2)).intValue());
            }

            @Override // org.springframework.cglib.proxy.CallbackGenerator.Context
            public void emitLoadArgsAndInvoke(CodeEmitter e, MethodInfo method2) {
                Signature bridgeTarget = (Signature) bridgeToTarget.get(method2.getSignature());
                if (bridgeTarget != null) {
                    for (int i = 0; i < bridgeTarget.getArgumentTypes().length; i++) {
                        e.load_arg(i);
                        Type target = bridgeTarget.getArgumentTypes()[i];
                        if (!target.equals(method2.getSignature().getArgumentTypes()[i])) {
                            e.checkcast(target);
                        }
                    }
                    e.invoke_virtual_this(bridgeTarget);
                    Type retType = method2.getSignature().getReturnType();
                    if (!retType.equals(bridgeTarget.getReturnType())) {
                        e.checkcast(retType);
                        return;
                    }
                    return;
                }
                e.load_args();
                e.super_invoke(method2.getSignature());
            }

            @Override // org.springframework.cglib.proxy.CallbackGenerator.Context
            public CodeEmitter beginMethod(ClassEmitter ce2, MethodInfo method2) {
                CodeEmitter e = EmitUtils.begin_method(ce2, method2);
                if (!Enhancer.this.interceptDuringConstruction && !TypeUtils.isAbstract(method2.getModifiers())) {
                    Label constructed = e.make_label();
                    e.load_this();
                    e.getfield(Enhancer.CONSTRUCTED_FIELD);
                    e.if_jump(154, constructed);
                    e.load_this();
                    e.load_args();
                    e.super_invoke();
                    e.return_value();
                    e.mark(constructed);
                }
                return e;
            }
        };
        for (int i = 0; i < this.callbackTypes.length; i++) {
            CallbackGenerator gen = generators[i];
            if (!seenGen.contains(gen)) {
                seenGen.add(gen);
                List fmethods = (List) groups.get(gen);
                if (fmethods != null) {
                    try {
                        gen.generate(ce, context, fmethods);
                        gen.generateStatic(se, context, fmethods);
                    } catch (RuntimeException x) {
                        throw x;
                    } catch (Exception x2) {
                        throw new CodeGenerationException(x2);
                    }
                } else {
                    continue;
                }
            }
        }
        se.return_value();
        se.end_method();
    }

    private void emitSetThreadCallbacks(ClassEmitter ce) {
        CodeEmitter e = ce.begin_method(9, SET_THREAD_CALLBACKS, null);
        e.getfield(THREAD_CALLBACKS_FIELD);
        e.load_arg(0);
        e.invoke_virtual(THREAD_LOCAL, THREAD_LOCAL_SET);
        e.return_value();
        e.end_method();
    }

    private void emitSetStaticCallbacks(ClassEmitter ce) {
        CodeEmitter e = ce.begin_method(9, SET_STATIC_CALLBACKS, null);
        e.load_arg(0);
        e.putfield(STATIC_CALLBACKS_FIELD);
        e.return_value();
        e.end_method();
    }

    private void emitCurrentCallback(CodeEmitter e, int index) {
        e.load_this();
        e.getfield(getCallbackField(index));
        e.dup();
        Label end = e.make_label();
        e.ifnonnull(end);
        e.pop();
        e.load_this();
        e.invoke_static_this(BIND_CALLBACKS);
        e.load_this();
        e.getfield(getCallbackField(index));
        e.mark(end);
    }

    private void emitBindCallbacks(ClassEmitter ce) {
        CodeEmitter e = ce.begin_method(26, BIND_CALLBACKS, null);
        Local me = e.make_local();
        e.load_arg(0);
        e.checkcast_this();
        e.store_local(me);
        Label end = e.make_label();
        e.load_local(me);
        e.getfield(BOUND_FIELD);
        e.if_jump(154, end);
        e.load_local(me);
        e.push(1);
        e.putfield(BOUND_FIELD);
        e.getfield(THREAD_CALLBACKS_FIELD);
        e.invoke_virtual(THREAD_LOCAL, THREAD_LOCAL_GET);
        e.dup();
        Label found_callback = e.make_label();
        e.ifnonnull(found_callback);
        e.pop();
        e.getfield(STATIC_CALLBACKS_FIELD);
        e.dup();
        e.ifnonnull(found_callback);
        e.pop();
        e.goTo(end);
        e.mark(found_callback);
        e.checkcast(CALLBACK_ARRAY);
        e.load_local(me);
        e.swap();
        for (int i = this.callbackTypes.length - 1; i >= 0; i--) {
            if (i != 0) {
                e.dup2();
            }
            e.aaload(i);
            e.checkcast(this.callbackTypes[i]);
            e.putfield(getCallbackField(i));
        }
        e.mark(end);
        e.return_value();
        e.end_method();
    }

    private static String getCallbackField(int index) {
        return "CGLIB$CALLBACK_" + index;
    }
}
