package org.springframework.asm;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/asm/FieldVisitor.class */
public abstract class FieldVisitor {
    protected final int api;
    protected FieldVisitor fv;

    /* JADX INFO: Access modifiers changed from: protected */
    public FieldVisitor(final int api) {
        this(api, null);
    }

    protected FieldVisitor(final int api, final FieldVisitor fieldVisitor) {
        if (api != 589824 && api != 524288 && api != 458752 && api != 393216 && api != 327680 && api != 262144 && api != 17432576) {
            throw new IllegalArgumentException("Unsupported api " + api);
        }
        this.api = api;
        this.fv = fieldVisitor;
    }

    public FieldVisitor getDelegate() {
        return this.fv;
    }

    public AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible) {
        if (this.fv != null) {
            return this.fv.visitAnnotation(descriptor, visible);
        }
        return null;
    }

    public AnnotationVisitor visitTypeAnnotation(final int typeRef, final TypePath typePath, final String descriptor, final boolean visible) {
        if (this.api < 327680) {
            throw new UnsupportedOperationException("This feature requires ASM5");
        }
        if (this.fv != null) {
            return this.fv.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
        }
        return null;
    }

    public void visitAttribute(final Attribute attribute) {
        if (this.fv != null) {
            this.fv.visitAttribute(attribute);
        }
    }

    public void visitEnd() {
        if (this.fv != null) {
            this.fv.visitEnd();
        }
    }
}
