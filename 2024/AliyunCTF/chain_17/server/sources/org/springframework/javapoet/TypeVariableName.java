package org.springframework.javapoet;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/javapoet/TypeVariableName.class */
public final class TypeVariableName extends TypeName {
    public final String name;
    public final List<TypeName> bounds;

    @Override // org.springframework.javapoet.TypeName
    public /* bridge */ /* synthetic */ TypeName annotated(List list) {
        return annotated((List<AnnotationSpec>) list);
    }

    private TypeVariableName(String name, List<TypeName> bounds) {
        this(name, bounds, new ArrayList());
    }

    private TypeVariableName(String name, List<TypeName> bounds, List<AnnotationSpec> annotations) {
        super(annotations);
        this.name = (String) Util.checkNotNull(name, "name == null", new Object[0]);
        this.bounds = bounds;
        Iterator<TypeName> it = this.bounds.iterator();
        while (it.hasNext()) {
            TypeName bound = it.next();
            Util.checkArgument((bound.isPrimitive() || bound == VOID) ? false : true, "invalid bound: %s", bound);
        }
    }

    @Override // org.springframework.javapoet.TypeName
    public TypeVariableName annotated(List<AnnotationSpec> annotations) {
        return new TypeVariableName(this.name, this.bounds, annotations);
    }

    @Override // org.springframework.javapoet.TypeName
    public TypeName withoutAnnotations() {
        return new TypeVariableName(this.name, this.bounds);
    }

    public TypeVariableName withBounds(Type... bounds) {
        return withBounds(TypeName.list(bounds));
    }

    public TypeVariableName withBounds(TypeName... bounds) {
        return withBounds(Arrays.asList(bounds));
    }

    public TypeVariableName withBounds(List<? extends TypeName> bounds) {
        ArrayList<TypeName> newBounds = new ArrayList<>();
        newBounds.addAll(this.bounds);
        newBounds.addAll(bounds);
        return new TypeVariableName(this.name, newBounds, this.annotations);
    }

    private static TypeVariableName of(String name, List<TypeName> bounds) {
        List<TypeName> boundsNoObject = new ArrayList<>(bounds);
        boundsNoObject.remove(OBJECT);
        return new TypeVariableName(name, Collections.unmodifiableList(boundsNoObject));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.springframework.javapoet.TypeName
    public CodeWriter emit(CodeWriter out) throws IOException {
        emitAnnotations(out);
        return out.emitAndIndent(this.name);
    }

    public static TypeVariableName get(String name) {
        return of(name, Collections.emptyList());
    }

    public static TypeVariableName get(String name, TypeName... bounds) {
        return of(name, Arrays.asList(bounds));
    }

    public static TypeVariableName get(String name, Type... bounds) {
        return of(name, TypeName.list(bounds));
    }

    public static TypeVariableName get(TypeVariable mirror) {
        return get(mirror.asElement());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static TypeVariableName get(TypeVariable mirror, Map<TypeParameterElement, TypeVariableName> typeVariables) {
        TypeParameterElement element = (TypeParameterElement) mirror.asElement();
        TypeVariableName typeVariableName = typeVariables.get(element);
        if (typeVariableName == null) {
            List<TypeName> bounds = new ArrayList<>();
            List<TypeName> visibleBounds = Collections.unmodifiableList(bounds);
            typeVariableName = new TypeVariableName(element.getSimpleName().toString(), visibleBounds);
            typeVariables.put(element, typeVariableName);
            for (TypeMirror typeMirror : element.getBounds()) {
                bounds.add(TypeName.get(typeMirror, typeVariables));
            }
            bounds.remove(OBJECT);
        }
        return typeVariableName;
    }

    public static TypeVariableName get(TypeParameterElement element) {
        String name = element.getSimpleName().toString();
        List<? extends TypeMirror> boundsMirrors = element.getBounds();
        List<TypeName> boundsTypeNames = new ArrayList<>();
        for (TypeMirror typeMirror : boundsMirrors) {
            boundsTypeNames.add(TypeName.get(typeMirror));
        }
        return of(name, boundsTypeNames);
    }

    public static TypeVariableName get(java.lang.reflect.TypeVariable<?> type) {
        return get(type, (Map<Type, TypeVariableName>) new LinkedHashMap());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static TypeVariableName get(java.lang.reflect.TypeVariable<?> type, Map<Type, TypeVariableName> map) {
        TypeVariableName result = map.get(type);
        if (result == null) {
            List<TypeName> bounds = new ArrayList<>();
            List<TypeName> visibleBounds = Collections.unmodifiableList(bounds);
            result = new TypeVariableName(type.getName(), visibleBounds);
            map.put(type, result);
            for (Type bound : type.getBounds()) {
                bounds.add(TypeName.get(bound, map));
            }
            bounds.remove(OBJECT);
        }
        return result;
    }
}
