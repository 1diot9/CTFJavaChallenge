package org.springframework.javapoet;

import ch.qos.logback.core.CoreConstants;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/javapoet/WildcardTypeName.class */
public final class WildcardTypeName extends TypeName {
    public final List<TypeName> upperBounds;
    public final List<TypeName> lowerBounds;

    @Override // org.springframework.javapoet.TypeName
    public /* bridge */ /* synthetic */ TypeName annotated(List list) {
        return annotated((List<AnnotationSpec>) list);
    }

    private WildcardTypeName(List<TypeName> upperBounds, List<TypeName> lowerBounds) {
        this(upperBounds, lowerBounds, new ArrayList());
    }

    private WildcardTypeName(List<TypeName> upperBounds, List<TypeName> lowerBounds, List<AnnotationSpec> annotations) {
        super(annotations);
        this.upperBounds = Util.immutableList(upperBounds);
        this.lowerBounds = Util.immutableList(lowerBounds);
        Util.checkArgument(this.upperBounds.size() == 1, "unexpected extends bounds: %s", upperBounds);
        Iterator<TypeName> it = this.upperBounds.iterator();
        while (it.hasNext()) {
            TypeName upperBound = it.next();
            Util.checkArgument((upperBound.isPrimitive() || upperBound == VOID) ? false : true, "invalid upper bound: %s", upperBound);
        }
        Iterator<TypeName> it2 = this.lowerBounds.iterator();
        while (it2.hasNext()) {
            TypeName lowerBound = it2.next();
            Util.checkArgument((lowerBound.isPrimitive() || lowerBound == VOID) ? false : true, "invalid lower bound: %s", lowerBound);
        }
    }

    @Override // org.springframework.javapoet.TypeName
    public WildcardTypeName annotated(List<AnnotationSpec> annotations) {
        return new WildcardTypeName(this.upperBounds, this.lowerBounds, concatAnnotations(annotations));
    }

    @Override // org.springframework.javapoet.TypeName
    public TypeName withoutAnnotations() {
        return new WildcardTypeName(this.upperBounds, this.lowerBounds);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.springframework.javapoet.TypeName
    public CodeWriter emit(CodeWriter out) throws IOException {
        if (this.lowerBounds.size() == 1) {
            return out.emit("? super $T", this.lowerBounds.get(0));
        }
        if (this.upperBounds.get(0).equals(TypeName.OBJECT)) {
            return out.emit(CoreConstants.NA);
        }
        return out.emit("? extends $T", this.upperBounds.get(0));
    }

    public static WildcardTypeName subtypeOf(TypeName upperBound) {
        return new WildcardTypeName(Collections.singletonList(upperBound), Collections.emptyList());
    }

    public static WildcardTypeName subtypeOf(Type upperBound) {
        return subtypeOf(TypeName.get(upperBound));
    }

    public static WildcardTypeName supertypeOf(TypeName lowerBound) {
        return new WildcardTypeName(Collections.singletonList(OBJECT), Collections.singletonList(lowerBound));
    }

    public static WildcardTypeName supertypeOf(Type lowerBound) {
        return supertypeOf(TypeName.get(lowerBound));
    }

    public static TypeName get(WildcardType mirror) {
        return get(mirror, new LinkedHashMap());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static TypeName get(WildcardType mirror, Map<TypeParameterElement, TypeVariableName> typeVariables) {
        TypeMirror extendsBound = mirror.getExtendsBound();
        if (extendsBound == null) {
            TypeMirror superBound = mirror.getSuperBound();
            if (superBound == null) {
                return subtypeOf(Object.class);
            }
            return supertypeOf(TypeName.get(superBound, typeVariables));
        }
        return subtypeOf(TypeName.get(extendsBound, typeVariables));
    }

    public static TypeName get(java.lang.reflect.WildcardType wildcardName) {
        return get(wildcardName, (Map<Type, TypeVariableName>) new LinkedHashMap());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static TypeName get(java.lang.reflect.WildcardType wildcardName, Map<Type, TypeVariableName> map) {
        return new WildcardTypeName(list(wildcardName.getUpperBounds(), map), list(wildcardName.getLowerBounds(), map));
    }
}
