package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
import com.fasterxml.jackson.databind.introspect.TypeResolutionContext;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jmx.export.naming.IdentityNamingStrategy;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/introspect/AnnotatedMethodCollector.class */
public class AnnotatedMethodCollector extends CollectorBase {
    private final ClassIntrospector.MixInResolver _mixInResolver;
    private final boolean _collectAnnotations;

    AnnotatedMethodCollector(AnnotationIntrospector intr, ClassIntrospector.MixInResolver mixins, boolean collectAnnotations) {
        super(intr);
        this._mixInResolver = intr == null ? null : mixins;
        this._collectAnnotations = collectAnnotations;
    }

    public static AnnotatedMethodMap collectMethods(AnnotationIntrospector intr, TypeResolutionContext tc, ClassIntrospector.MixInResolver mixins, TypeFactory types, JavaType type, List<JavaType> superTypes, Class<?> primaryMixIn, boolean collectAnnotations) {
        return new AnnotatedMethodCollector(intr, mixins, collectAnnotations).collect(types, tc, type, superTypes, primaryMixIn);
    }

    AnnotatedMethodMap collect(TypeFactory typeFactory, TypeResolutionContext tc, JavaType mainType, List<JavaType> superTypes, Class<?> primaryMixIn) {
        Class<?> mixin;
        Map<MemberKey, MethodBuilder> methods = new LinkedHashMap<>();
        _addMemberMethods(tc, mainType.getRawClass(), methods, primaryMixIn);
        for (JavaType type : superTypes) {
            Class<?> mixin2 = this._mixInResolver == null ? null : this._mixInResolver.findMixInClassFor(type.getRawClass());
            _addMemberMethods(new TypeResolutionContext.Basic(typeFactory, type.getBindings()), type.getRawClass(), methods, mixin2);
        }
        boolean checkJavaLangObject = false;
        if (this._mixInResolver != null && (mixin = this._mixInResolver.findMixInClassFor(Object.class)) != null) {
            _addMethodMixIns(tc, mainType.getRawClass(), methods, mixin);
            checkJavaLangObject = true;
        }
        if (checkJavaLangObject && this._intr != null && !methods.isEmpty()) {
            for (Map.Entry<MemberKey, MethodBuilder> entry : methods.entrySet()) {
                MemberKey k = entry.getKey();
                if (IdentityNamingStrategy.HASH_CODE_KEY.equals(k.getName()) && 0 == k.argCount()) {
                    try {
                        Method m = Object.class.getDeclaredMethod(k.getName(), new Class[0]);
                        if (m != null) {
                            MethodBuilder b = entry.getValue();
                            b.annotations = collectDefaultAnnotations(b.annotations, m.getDeclaredAnnotations());
                            b.method = m;
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }
        if (methods.isEmpty()) {
            return new AnnotatedMethodMap();
        }
        Map<MemberKey, AnnotatedMethod> actual = new LinkedHashMap<>(methods.size());
        for (Map.Entry<MemberKey, MethodBuilder> entry2 : methods.entrySet()) {
            AnnotatedMethod am = entry2.getValue().build();
            if (am != null) {
                actual.put(entry2.getKey(), am);
            }
        }
        return new AnnotatedMethodMap(actual);
    }

    private void _addMemberMethods(TypeResolutionContext tc, Class<?> cls, Map<MemberKey, MethodBuilder> methods, Class<?> mixInCls) {
        if (mixInCls != null) {
            _addMethodMixIns(tc, cls, methods, mixInCls);
        }
        if (cls == null) {
            return;
        }
        for (Method m : ClassUtil.getClassMethods(cls)) {
            if (_isIncludableMemberMethod(m)) {
                MemberKey key = new MemberKey(m);
                MethodBuilder b = methods.get(key);
                if (b == null) {
                    AnnotationCollector c = this._intr == null ? AnnotationCollector.emptyCollector() : collectAnnotations(m.getDeclaredAnnotations());
                    methods.put(key, new MethodBuilder(tc, m, c));
                } else {
                    if (this._collectAnnotations) {
                        b.annotations = collectDefaultAnnotations(b.annotations, m.getDeclaredAnnotations());
                    }
                    Method old = b.method;
                    if (old == null) {
                        b.method = m;
                    } else if (Modifier.isAbstract(old.getModifiers()) && !Modifier.isAbstract(m.getModifiers())) {
                        b.method = m;
                        b.typeContext = tc;
                    }
                }
            }
        }
    }

    protected void _addMethodMixIns(TypeResolutionContext tc, Class<?> targetClass, Map<MemberKey, MethodBuilder> methods, Class<?> mixInCls) {
        if (this._intr == null) {
            return;
        }
        for (Class<?> mixin : ClassUtil.findRawSuperTypes(mixInCls, targetClass, true)) {
            for (Method m : mixin.getDeclaredMethods()) {
                if (_isIncludableMemberMethod(m)) {
                    MemberKey key = new MemberKey(m);
                    MethodBuilder b = methods.get(key);
                    Annotation[] anns = m.getDeclaredAnnotations();
                    if (b == null) {
                        methods.put(key, new MethodBuilder(tc, null, collectAnnotations(anns)));
                    } else {
                        b.annotations = collectDefaultAnnotations(b.annotations, anns);
                    }
                }
            }
        }
    }

    private static boolean _isIncludableMemberMethod(Method m) {
        return (Modifier.isStatic(m.getModifiers()) || m.isSynthetic() || m.isBridge() || m.getParameterCount() > 2) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/introspect/AnnotatedMethodCollector$MethodBuilder.class */
    public static final class MethodBuilder {
        public TypeResolutionContext typeContext;
        public Method method;
        public AnnotationCollector annotations;

        public MethodBuilder(TypeResolutionContext tc, Method m, AnnotationCollector ann) {
            this.typeContext = tc;
            this.method = m;
            this.annotations = ann;
        }

        public AnnotatedMethod build() {
            if (this.method == null) {
                return null;
            }
            return new AnnotatedMethod(this.typeContext, this.method, this.annotations.asAnnotationMap(), null);
        }
    }
}
