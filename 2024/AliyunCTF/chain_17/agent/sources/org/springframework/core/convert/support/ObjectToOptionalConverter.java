package org.springframework.core.convert.support;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.converter.GenericConverter;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/convert/support/ObjectToOptionalConverter.class */
public final class ObjectToOptionalConverter implements ConditionalGenericConverter {
    private final ConversionService conversionService;

    public ObjectToOptionalConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override // org.springframework.core.convert.converter.GenericConverter
    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        Set<GenericConverter.ConvertiblePair> convertibleTypes = new LinkedHashSet<>(4);
        convertibleTypes.add(new GenericConverter.ConvertiblePair(Collection.class, Optional.class));
        convertibleTypes.add(new GenericConverter.ConvertiblePair(Object[].class, Optional.class));
        convertibleTypes.add(new GenericConverter.ConvertiblePair(Object.class, Optional.class));
        return convertibleTypes;
    }

    @Override // org.springframework.core.convert.converter.ConditionalConverter
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (targetType.getResolvableType().hasGenerics()) {
            return this.conversionService.canConvert(sourceType, new GenericTypeDescriptor(targetType));
        }
        return true;
    }

    /* JADX WARN: Code restructure failed: missing block: B:21:0x005e, code lost:            if (r0.isEmpty() != false) goto L22;     */
    @Override // org.springframework.core.convert.converter.GenericConverter
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public java.lang.Object convert(@org.springframework.lang.Nullable java.lang.Object r8, org.springframework.core.convert.TypeDescriptor r9, org.springframework.core.convert.TypeDescriptor r10) {
        /*
            r7 = this;
            r0 = r8
            if (r0 != 0) goto L8
            java.util.Optional r0 = java.util.Optional.empty()
            return r0
        L8:
            r0 = r8
            boolean r0 = r0 instanceof java.util.Optional
            if (r0 == 0) goto L11
            r0 = r8
            return r0
        L11:
            r0 = r10
            org.springframework.core.ResolvableType r0 = r0.getResolvableType()
            boolean r0 = r0.hasGenerics()
            if (r0 == 0) goto L6b
            r0 = r7
            org.springframework.core.convert.ConversionService r0 = r0.conversionService
            r1 = r8
            r2 = r9
            org.springframework.core.convert.support.ObjectToOptionalConverter$GenericTypeDescriptor r3 = new org.springframework.core.convert.support.ObjectToOptionalConverter$GenericTypeDescriptor
            r4 = r3
            r5 = r10
            r4.<init>(r5)
            java.lang.Object r0 = r0.convert(r1, r2, r3)
            r11 = r0
            r0 = r11
            if (r0 == 0) goto L61
            r0 = r11
            java.lang.Class r0 = r0.getClass()
            boolean r0 = r0.isArray()
            if (r0 == 0) goto L48
            r0 = r11
            int r0 = java.lang.reflect.Array.getLength(r0)
            if (r0 == 0) goto L61
        L48:
            r0 = r11
            boolean r0 = r0 instanceof java.util.Collection
            if (r0 == 0) goto L65
            r0 = r11
            java.util.Collection r0 = (java.util.Collection) r0
            r12 = r0
            r0 = r12
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L65
        L61:
            java.util.Optional r0 = java.util.Optional.empty()
            return r0
        L65:
            r0 = r11
            java.util.Optional r0 = java.util.Optional.of(r0)
            return r0
        L6b:
            r0 = r8
            java.util.Optional r0 = java.util.Optional.of(r0)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.core.convert.support.ObjectToOptionalConverter.convert(java.lang.Object, org.springframework.core.convert.TypeDescriptor, org.springframework.core.convert.TypeDescriptor):java.lang.Object");
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/convert/support/ObjectToOptionalConverter$GenericTypeDescriptor.class */
    private static class GenericTypeDescriptor extends TypeDescriptor {
        public GenericTypeDescriptor(TypeDescriptor typeDescriptor) {
            super(typeDescriptor.getResolvableType().getGeneric(new int[0]), null, typeDescriptor.getAnnotations());
        }
    }
}
