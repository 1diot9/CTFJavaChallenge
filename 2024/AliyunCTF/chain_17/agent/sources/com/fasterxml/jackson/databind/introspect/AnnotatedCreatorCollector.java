package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/introspect/AnnotatedCreatorCollector.class */
public final class AnnotatedCreatorCollector extends CollectorBase {
    private final TypeResolutionContext _typeContext;
    private final boolean _collectAnnotations;
    private AnnotatedConstructor _defaultConstructor;

    AnnotatedCreatorCollector(AnnotationIntrospector intr, TypeResolutionContext tc, boolean collectAnnotations) {
        super(intr);
        this._typeContext = tc;
        this._collectAnnotations = collectAnnotations;
    }

    public static AnnotatedClass.Creators collectCreators(AnnotationIntrospector intr, TypeFactory typeFactory, TypeResolutionContext tc, JavaType type, Class<?> primaryMixIn, boolean collectAnnotations) {
        return new AnnotatedCreatorCollector(intr, tc, collectAnnotations | (primaryMixIn != null)).collect(typeFactory, type, primaryMixIn);
    }

    AnnotatedClass.Creators collect(TypeFactory typeFactory, JavaType type, Class<?> primaryMixIn) {
        List<AnnotatedConstructor> constructors = _findPotentialConstructors(type, primaryMixIn);
        List<AnnotatedMethod> factories = _findPotentialFactories(typeFactory, type, primaryMixIn);
        if (this._collectAnnotations) {
            if (this._defaultConstructor != null && this._intr.hasIgnoreMarker(this._defaultConstructor)) {
                this._defaultConstructor = null;
            }
            int i = constructors.size();
            while (true) {
                i--;
                if (i < 0) {
                    break;
                }
                if (this._intr.hasIgnoreMarker(constructors.get(i))) {
                    constructors.remove(i);
                }
            }
            int i2 = factories.size();
            while (true) {
                i2--;
                if (i2 < 0) {
                    break;
                }
                if (this._intr.hasIgnoreMarker(factories.get(i2))) {
                    factories.remove(i2);
                }
            }
        }
        return new AnnotatedClass.Creators(this._defaultConstructor, constructors, factories);
    }

    private List<AnnotatedConstructor> _findPotentialConstructors(JavaType type, Class<?> primaryMixIn) {
        int ctorCount;
        List<AnnotatedConstructor> result;
        ClassUtil.Ctor defaultCtor = null;
        List<ClassUtil.Ctor> ctors = null;
        if (!type.isEnumType()) {
            ClassUtil.Ctor[] declaredCtors = ClassUtil.getConstructors(type.getRawClass());
            for (ClassUtil.Ctor ctor : declaredCtors) {
                if (isIncludableConstructor(ctor.getConstructor())) {
                    if (ctor.getParamCount() == 0) {
                        defaultCtor = ctor;
                    } else {
                        if (ctors == null) {
                            ctors = new ArrayList<>();
                        }
                        ctors.add(ctor);
                    }
                }
            }
        }
        if (ctors == null) {
            result = Collections.emptyList();
            if (defaultCtor == null) {
                return result;
            }
            ctorCount = 0;
        } else {
            ctorCount = ctors.size();
            result = new ArrayList<>(ctorCount);
            for (int i = 0; i < ctorCount; i++) {
                result.add(null);
            }
        }
        if (primaryMixIn != null) {
            MemberKey[] ctorKeys = null;
            for (ClassUtil.Ctor mixinCtor : ClassUtil.getConstructors(primaryMixIn)) {
                if (mixinCtor.getParamCount() == 0) {
                    if (defaultCtor != null) {
                        this._defaultConstructor = constructDefaultConstructor(defaultCtor, mixinCtor);
                        defaultCtor = null;
                    }
                } else if (ctors != null) {
                    if (ctorKeys == null) {
                        ctorKeys = new MemberKey[ctorCount];
                        for (int i2 = 0; i2 < ctorCount; i2++) {
                            ctorKeys[i2] = new MemberKey(ctors.get(i2).getConstructor());
                        }
                    }
                    MemberKey key = new MemberKey(mixinCtor.getConstructor());
                    int i3 = 0;
                    while (true) {
                        if (i3 >= ctorCount) {
                            break;
                        }
                        if (!key.equals(ctorKeys[i3])) {
                            i3++;
                        } else {
                            result.set(i3, constructNonDefaultConstructor(ctors.get(i3), mixinCtor));
                            break;
                        }
                    }
                }
            }
        }
        if (defaultCtor != null) {
            this._defaultConstructor = constructDefaultConstructor(defaultCtor, null);
        }
        for (int i4 = 0; i4 < ctorCount; i4++) {
            if (result.get(i4) == null) {
                result.set(i4, constructNonDefaultConstructor(ctors.get(i4), null));
            }
        }
        return result;
    }

    private List<AnnotatedMethod> _findPotentialFactories(TypeFactory typeFactory, JavaType type, Class<?> primaryMixIn) {
        List<Method> candidates = null;
        for (Method m : ClassUtil.getClassMethods(type.getRawClass())) {
            if (_isIncludableFactoryMethod(m)) {
                if (candidates == null) {
                    candidates = new ArrayList<>();
                }
                candidates.add(m);
            }
        }
        if (candidates == null) {
            return Collections.emptyList();
        }
        TypeResolutionContext initialTypeResCtxt = this._typeContext;
        int factoryCount = candidates.size();
        List<AnnotatedMethod> result = new ArrayList<>(factoryCount);
        for (int i = 0; i < factoryCount; i++) {
            result.add(null);
        }
        if (primaryMixIn != null) {
            MemberKey[] methodKeys = null;
            for (Method mixinFactory : primaryMixIn.getDeclaredMethods()) {
                if (_isIncludableFactoryMethod(mixinFactory)) {
                    if (methodKeys == null) {
                        methodKeys = new MemberKey[factoryCount];
                        for (int i2 = 0; i2 < factoryCount; i2++) {
                            methodKeys[i2] = new MemberKey(candidates.get(i2));
                        }
                    }
                    MemberKey key = new MemberKey(mixinFactory);
                    int i3 = 0;
                    while (true) {
                        if (i3 >= factoryCount) {
                            break;
                        }
                        if (!key.equals(methodKeys[i3])) {
                            i3++;
                        } else {
                            result.set(i3, constructFactoryCreator(candidates.get(i3), initialTypeResCtxt, mixinFactory));
                            break;
                        }
                    }
                }
            }
        }
        for (int i4 = 0; i4 < factoryCount; i4++) {
            AnnotatedMethod factory = result.get(i4);
            if (factory == null) {
                Method candidate = candidates.get(i4);
                TypeResolutionContext typeResCtxt = MethodGenericTypeResolver.narrowMethodTypeParameters(candidate, type, typeFactory, initialTypeResCtxt);
                result.set(i4, constructFactoryCreator(candidate, typeResCtxt, null));
            }
        }
        return result;
    }

    private static boolean _isIncludableFactoryMethod(Method m) {
        return Modifier.isStatic(m.getModifiers()) && !m.isSynthetic();
    }

    protected AnnotatedConstructor constructDefaultConstructor(ClassUtil.Ctor ctor, ClassUtil.Ctor mixin) {
        return new AnnotatedConstructor(this._typeContext, ctor.getConstructor(), collectAnnotations(ctor, mixin), NO_ANNOTATION_MAPS);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v23, types: [java.lang.annotation.Annotation[]] */
    /* JADX WARN: Type inference failed for: r0v35, types: [java.lang.annotation.Annotation[]] */
    protected AnnotatedConstructor constructNonDefaultConstructor(ClassUtil.Ctor ctor, ClassUtil.Ctor mixin) {
        AnnotationMap[] resolvedAnnotations;
        int paramCount = ctor.getParamCount();
        if (this._intr == null) {
            return new AnnotatedConstructor(this._typeContext, ctor.getConstructor(), _emptyAnnotationMap(), _emptyAnnotationMaps(paramCount));
        }
        if (paramCount == 0) {
            return new AnnotatedConstructor(this._typeContext, ctor.getConstructor(), collectAnnotations(ctor, mixin), NO_ANNOTATION_MAPS);
        }
        Annotation[][] paramAnns = ctor.getParameterAnnotations();
        if (paramCount != paramAnns.length) {
            resolvedAnnotations = null;
            Class<?> dc = ctor.getDeclaringClass();
            if (ClassUtil.isEnumType(dc) && paramCount == paramAnns.length + 2) {
                paramAnns = new Annotation[paramAnns.length + 2];
                System.arraycopy(paramAnns, 0, paramAnns, 2, paramAnns.length);
                resolvedAnnotations = collectAnnotations(paramAnns, (Annotation[][]) null);
            } else if (dc.isMemberClass() && paramCount == paramAnns.length + 1) {
                paramAnns = new Annotation[paramAnns.length + 1];
                System.arraycopy(paramAnns, 0, paramAnns, 1, paramAnns.length);
                paramAnns[0] = NO_ANNOTATIONS;
                resolvedAnnotations = collectAnnotations(paramAnns, (Annotation[][]) null);
            }
            if (resolvedAnnotations == null) {
                throw new IllegalStateException(String.format("Internal error: constructor for %s has mismatch: %d parameters; %d sets of annotations", ctor.getDeclaringClass().getName(), Integer.valueOf(paramCount), Integer.valueOf(paramAnns.length)));
            }
        } else {
            resolvedAnnotations = collectAnnotations(paramAnns, mixin == null ? (Annotation[][]) null : mixin.getParameterAnnotations());
        }
        return new AnnotatedConstructor(this._typeContext, ctor.getConstructor(), collectAnnotations(ctor, mixin), resolvedAnnotations);
    }

    protected AnnotatedMethod constructFactoryCreator(Method m, TypeResolutionContext typeResCtxt, Method mixin) {
        int paramCount = m.getParameterCount();
        if (this._intr == null) {
            return new AnnotatedMethod(typeResCtxt, m, _emptyAnnotationMap(), _emptyAnnotationMaps(paramCount));
        }
        if (paramCount == 0) {
            return new AnnotatedMethod(typeResCtxt, m, collectAnnotations(m, mixin), NO_ANNOTATION_MAPS);
        }
        return new AnnotatedMethod(typeResCtxt, m, collectAnnotations(m, mixin), collectAnnotations(m.getParameterAnnotations(), mixin == null ? (Annotation[][]) null : mixin.getParameterAnnotations()));
    }

    private AnnotationMap[] collectAnnotations(Annotation[][] mainAnns, Annotation[][] mixinAnns) {
        if (this._collectAnnotations) {
            int count = mainAnns.length;
            AnnotationMap[] result = new AnnotationMap[count];
            for (int i = 0; i < count; i++) {
                AnnotationCollector c = collectAnnotations(AnnotationCollector.emptyCollector(), mainAnns[i]);
                if (mixinAnns != null) {
                    c = collectAnnotations(c, mixinAnns[i]);
                }
                result[i] = c.asAnnotationMap();
            }
            return result;
        }
        return NO_ANNOTATION_MAPS;
    }

    private AnnotationMap collectAnnotations(ClassUtil.Ctor main, ClassUtil.Ctor mixin) {
        if (this._collectAnnotations) {
            AnnotationCollector c = collectAnnotations(main.getDeclaredAnnotations());
            if (mixin != null) {
                c = collectAnnotations(c, mixin.getDeclaredAnnotations());
            }
            return c.asAnnotationMap();
        }
        return _emptyAnnotationMap();
    }

    private final AnnotationMap collectAnnotations(AnnotatedElement main, AnnotatedElement mixin) {
        AnnotationCollector c = collectAnnotations(main.getDeclaredAnnotations());
        if (mixin != null) {
            c = collectAnnotations(c, mixin.getDeclaredAnnotations());
        }
        return c.asAnnotationMap();
    }

    private static boolean isIncludableConstructor(Constructor<?> c) {
        return !c.isSynthetic();
    }
}
