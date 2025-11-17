package com.fasterxml.jackson.databind.jsontype.impl;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedClassResolver;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;
import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/jsontype/impl/StdSubtypeResolver.class */
public class StdSubtypeResolver extends SubtypeResolver implements Serializable {
    private static final long serialVersionUID = 1;
    protected LinkedHashSet<NamedType> _registeredSubtypes;

    public StdSubtypeResolver() {
    }

    protected StdSubtypeResolver(StdSubtypeResolver src) {
        LinkedHashSet<NamedType> reg = src._registeredSubtypes;
        this._registeredSubtypes = reg == null ? null : new LinkedHashSet<>(reg);
    }

    @Override // com.fasterxml.jackson.databind.jsontype.SubtypeResolver
    public SubtypeResolver copy() {
        return new StdSubtypeResolver(this);
    }

    @Override // com.fasterxml.jackson.databind.jsontype.SubtypeResolver
    public void registerSubtypes(NamedType... types) {
        if (this._registeredSubtypes == null) {
            this._registeredSubtypes = new LinkedHashSet<>();
        }
        for (NamedType type : types) {
            this._registeredSubtypes.add(type);
        }
    }

    @Override // com.fasterxml.jackson.databind.jsontype.SubtypeResolver
    public void registerSubtypes(Class<?>... classes) {
        NamedType[] types = new NamedType[classes.length];
        int len = classes.length;
        for (int i = 0; i < len; i++) {
            types[i] = new NamedType(classes[i]);
        }
        registerSubtypes(types);
    }

    @Override // com.fasterxml.jackson.databind.jsontype.SubtypeResolver
    public void registerSubtypes(Collection<Class<?>> subtypes) {
        int len = subtypes.size();
        NamedType[] types = new NamedType[len];
        int i = 0;
        for (Class<?> subtype : subtypes) {
            int i2 = i;
            i++;
            types[i2] = new NamedType(subtype);
        }
        registerSubtypes(types);
    }

    @Override // com.fasterxml.jackson.databind.jsontype.SubtypeResolver
    public Collection<NamedType> collectAndResolveSubtypesByClass(MapperConfig<?> config, AnnotatedMember property, JavaType baseType) {
        Class<?> rawBase;
        Collection<NamedType> st;
        AnnotationIntrospector ai = config.getAnnotationIntrospector();
        if (baseType != null) {
            rawBase = baseType.getRawClass();
        } else if (property != null) {
            rawBase = property.getRawType();
        } else {
            throw new IllegalArgumentException("Both property and base type are nulls");
        }
        HashMap<NamedType, NamedType> collected = new HashMap<>();
        if (this._registeredSubtypes != null) {
            Iterator<NamedType> it = this._registeredSubtypes.iterator();
            while (it.hasNext()) {
                NamedType subtype = it.next();
                if (rawBase.isAssignableFrom(subtype.getType())) {
                    AnnotatedClass curr = AnnotatedClassResolver.resolveWithoutSuperTypes(config, subtype.getType());
                    _collectAndResolve(curr, subtype, config, ai, collected);
                }
            }
        }
        if (property != null && (st = ai.findSubtypes(property)) != null) {
            for (NamedType nt : st) {
                AnnotatedClass ac = AnnotatedClassResolver.resolveWithoutSuperTypes(config, nt.getType());
                _collectAndResolve(ac, nt, config, ai, collected);
            }
        }
        NamedType rootType = new NamedType(rawBase, null);
        AnnotatedClass ac2 = AnnotatedClassResolver.resolveWithoutSuperTypes(config, rawBase);
        _collectAndResolve(ac2, rootType, config, ai, collected);
        return new ArrayList(collected.values());
    }

    @Override // com.fasterxml.jackson.databind.jsontype.SubtypeResolver
    public Collection<NamedType> collectAndResolveSubtypesByClass(MapperConfig<?> config, AnnotatedClass type) {
        AnnotationIntrospector ai = config.getAnnotationIntrospector();
        HashMap<NamedType, NamedType> subtypes = new HashMap<>();
        if (this._registeredSubtypes != null) {
            Class<?> rawBase = type.getRawType();
            Iterator<NamedType> it = this._registeredSubtypes.iterator();
            while (it.hasNext()) {
                NamedType subtype = it.next();
                if (rawBase.isAssignableFrom(subtype.getType())) {
                    AnnotatedClass curr = AnnotatedClassResolver.resolveWithoutSuperTypes(config, subtype.getType());
                    _collectAndResolve(curr, subtype, config, ai, subtypes);
                }
            }
        }
        NamedType rootType = new NamedType(type.getRawType(), null);
        _collectAndResolve(type, rootType, config, ai, subtypes);
        return new ArrayList(subtypes.values());
    }

    @Override // com.fasterxml.jackson.databind.jsontype.SubtypeResolver
    public Collection<NamedType> collectAndResolveSubtypesByTypeId(MapperConfig<?> config, AnnotatedMember property, JavaType baseType) {
        Collection<NamedType> st;
        AnnotationIntrospector ai = config.getAnnotationIntrospector();
        Class<?> rawBase = baseType.getRawClass();
        Set<Class<?>> typesHandled = new HashSet<>();
        Map<String, NamedType> byName = new LinkedHashMap<>();
        NamedType rootType = new NamedType(rawBase, null);
        AnnotatedClass ac = AnnotatedClassResolver.resolveWithoutSuperTypes(config, rawBase);
        _collectAndResolveByTypeId(ac, rootType, config, typesHandled, byName);
        if (property != null && (st = ai.findSubtypes(property)) != null) {
            for (NamedType nt : st) {
                AnnotatedClass ac2 = AnnotatedClassResolver.resolveWithoutSuperTypes(config, nt.getType());
                _collectAndResolveByTypeId(ac2, nt, config, typesHandled, byName);
            }
        }
        if (this._registeredSubtypes != null) {
            Iterator<NamedType> it = this._registeredSubtypes.iterator();
            while (it.hasNext()) {
                NamedType subtype = it.next();
                if (rawBase.isAssignableFrom(subtype.getType())) {
                    AnnotatedClass curr = AnnotatedClassResolver.resolveWithoutSuperTypes(config, subtype.getType());
                    _collectAndResolveByTypeId(curr, subtype, config, typesHandled, byName);
                }
            }
        }
        return _combineNamedAndUnnamed(rawBase, typesHandled, byName);
    }

    @Override // com.fasterxml.jackson.databind.jsontype.SubtypeResolver
    public Collection<NamedType> collectAndResolveSubtypesByTypeId(MapperConfig<?> config, AnnotatedClass baseType) {
        Class<?> rawBase = baseType.getRawType();
        Set<Class<?>> typesHandled = new HashSet<>();
        Map<String, NamedType> byName = new LinkedHashMap<>();
        NamedType rootType = new NamedType(rawBase, null);
        _collectAndResolveByTypeId(baseType, rootType, config, typesHandled, byName);
        if (this._registeredSubtypes != null) {
            Iterator<NamedType> it = this._registeredSubtypes.iterator();
            while (it.hasNext()) {
                NamedType subtype = it.next();
                if (rawBase.isAssignableFrom(subtype.getType())) {
                    AnnotatedClass curr = AnnotatedClassResolver.resolveWithoutSuperTypes(config, subtype.getType());
                    _collectAndResolveByTypeId(curr, subtype, config, typesHandled, byName);
                }
            }
        }
        return _combineNamedAndUnnamed(rawBase, typesHandled, byName);
    }

    protected void _collectAndResolve(AnnotatedClass annotatedType, NamedType namedType, MapperConfig<?> config, AnnotationIntrospector ai, HashMap<NamedType, NamedType> collectedSubtypes) {
        String name;
        if (!namedType.hasName() && (name = ai.findTypeName(annotatedType)) != null) {
            namedType = new NamedType(namedType.getType(), name);
        }
        NamedType typeOnlyNamedType = new NamedType(namedType.getType());
        if (collectedSubtypes.containsKey(typeOnlyNamedType)) {
            if (namedType.hasName()) {
                NamedType prev = collectedSubtypes.get(typeOnlyNamedType);
                if (!prev.hasName()) {
                    collectedSubtypes.put(typeOnlyNamedType, namedType);
                    return;
                }
                return;
            }
            return;
        }
        collectedSubtypes.put(typeOnlyNamedType, namedType);
        Collection<NamedType> st = ai.findSubtypes(annotatedType);
        if (st != null && !st.isEmpty()) {
            for (NamedType subtype : st) {
                AnnotatedClass subtypeClass = AnnotatedClassResolver.resolveWithoutSuperTypes(config, subtype.getType());
                _collectAndResolve(subtypeClass, subtype, config, ai, collectedSubtypes);
            }
        }
    }

    protected void _collectAndResolveByTypeId(AnnotatedClass annotatedType, NamedType namedType, MapperConfig<?> config, Set<Class<?>> typesHandled, Map<String, NamedType> byName) {
        Collection<NamedType> st;
        String name;
        AnnotationIntrospector ai = config.getAnnotationIntrospector();
        if (!namedType.hasName() && (name = ai.findTypeName(annotatedType)) != null) {
            namedType = new NamedType(namedType.getType(), name);
        }
        if (namedType.hasName()) {
            byName.put(namedType.getName(), namedType);
        }
        if (typesHandled.add(namedType.getType()) && (st = ai.findSubtypes(annotatedType)) != null && !st.isEmpty()) {
            for (NamedType subtype : st) {
                AnnotatedClass subtypeClass = AnnotatedClassResolver.resolveWithoutSuperTypes(config, subtype.getType());
                _collectAndResolveByTypeId(subtypeClass, subtype, config, typesHandled, byName);
            }
        }
    }

    protected Collection<NamedType> _combineNamedAndUnnamed(Class<?> rawBase, Set<Class<?>> typesHandled, Map<String, NamedType> byName) {
        ArrayList<NamedType> result = new ArrayList<>(byName.values());
        for (NamedType t : byName.values()) {
            typesHandled.remove(t.getType());
        }
        for (Class<?> cls : typesHandled) {
            if (cls != rawBase || !Modifier.isAbstract(cls.getModifiers())) {
                result.add(new NamedType(cls));
            }
        }
        return result;
    }
}
