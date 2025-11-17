package org.springframework.asm;

import org.apache.tomcat.util.bcel.Const;
import org.jooq.types.UShort;
import org.springframework.asm.Attribute;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/asm/ClassWriter.class */
public class ClassWriter extends ClassVisitor {
    public static final int COMPUTE_MAXS = 1;
    public static final int COMPUTE_FRAMES = 2;
    private final int flags;
    private int version;
    private final SymbolTable symbolTable;
    private int accessFlags;
    private int thisClass;
    private int superClass;
    private int interfaceCount;
    private int[] interfaces;
    private FieldWriter firstField;
    private FieldWriter lastField;
    private MethodWriter firstMethod;
    private MethodWriter lastMethod;
    private int numberOfInnerClasses;
    private ByteVector innerClasses;
    private int enclosingClassIndex;
    private int enclosingMethodIndex;
    private int signatureIndex;
    private int sourceFileIndex;
    private ByteVector debugExtension;
    private AnnotationWriter lastRuntimeVisibleAnnotation;
    private AnnotationWriter lastRuntimeInvisibleAnnotation;
    private AnnotationWriter lastRuntimeVisibleTypeAnnotation;
    private AnnotationWriter lastRuntimeInvisibleTypeAnnotation;
    private ModuleWriter moduleWriter;
    private int nestHostClassIndex;
    private int numberOfNestMemberClasses;
    private ByteVector nestMemberClasses;
    private int numberOfPermittedSubclasses;
    private ByteVector permittedSubclasses;
    private RecordComponentWriter firstRecordComponent;
    private RecordComponentWriter lastRecordComponent;
    private Attribute firstAttribute;
    private int compute;

    public ClassWriter(final int flags) {
        this(null, flags);
    }

    public ClassWriter(final ClassReader classReader, final int flags) {
        super(Opcodes.ASM9);
        this.flags = flags;
        this.symbolTable = classReader == null ? new SymbolTable(this) : new SymbolTable(this, classReader);
        if ((flags & 2) != 0) {
            this.compute = 4;
        } else if ((flags & 1) != 0) {
            this.compute = 1;
        } else {
            this.compute = 0;
        }
    }

    public boolean hasFlags(final int flags) {
        return (this.flags & flags) == flags;
    }

    @Override // org.springframework.asm.ClassVisitor
    public final void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        this.version = version;
        this.accessFlags = access;
        this.thisClass = this.symbolTable.setMajorVersionAndClassName(version & UShort.MAX_VALUE, name);
        if (signature != null) {
            this.signatureIndex = this.symbolTable.addConstantUtf8(signature);
        }
        this.superClass = superName == null ? 0 : this.symbolTable.addConstantClass(superName).index;
        if (interfaces != null && interfaces.length > 0) {
            this.interfaceCount = interfaces.length;
            this.interfaces = new int[this.interfaceCount];
            for (int i = 0; i < this.interfaceCount; i++) {
                this.interfaces[i] = this.symbolTable.addConstantClass(interfaces[i]).index;
            }
        }
        if (this.compute == 1 && (version & UShort.MAX_VALUE) >= 51) {
            this.compute = 2;
        }
    }

    @Override // org.springframework.asm.ClassVisitor
    public final void visitSource(final String file, final String debug) {
        if (file != null) {
            this.sourceFileIndex = this.symbolTable.addConstantUtf8(file);
        }
        if (debug != null) {
            this.debugExtension = new ByteVector().encodeUtf8(debug, 0, Integer.MAX_VALUE);
        }
    }

    @Override // org.springframework.asm.ClassVisitor
    public final ModuleVisitor visitModule(final String name, final int access, final String version) {
        ModuleWriter moduleWriter = new ModuleWriter(this.symbolTable, this.symbolTable.addConstantModule(name).index, access, version == null ? 0 : this.symbolTable.addConstantUtf8(version));
        this.moduleWriter = moduleWriter;
        return moduleWriter;
    }

    @Override // org.springframework.asm.ClassVisitor
    public final void visitNestHost(final String nestHost) {
        this.nestHostClassIndex = this.symbolTable.addConstantClass(nestHost).index;
    }

    @Override // org.springframework.asm.ClassVisitor
    public final void visitOuterClass(final String owner, final String name, final String descriptor) {
        this.enclosingClassIndex = this.symbolTable.addConstantClass(owner).index;
        if (name != null && descriptor != null) {
            this.enclosingMethodIndex = this.symbolTable.addConstantNameAndType(name, descriptor);
        }
    }

    @Override // org.springframework.asm.ClassVisitor
    public final AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible) {
        if (visible) {
            AnnotationWriter create = AnnotationWriter.create(this.symbolTable, descriptor, this.lastRuntimeVisibleAnnotation);
            this.lastRuntimeVisibleAnnotation = create;
            return create;
        }
        AnnotationWriter create2 = AnnotationWriter.create(this.symbolTable, descriptor, this.lastRuntimeInvisibleAnnotation);
        this.lastRuntimeInvisibleAnnotation = create2;
        return create2;
    }

    @Override // org.springframework.asm.ClassVisitor
    public final AnnotationVisitor visitTypeAnnotation(final int typeRef, final TypePath typePath, final String descriptor, final boolean visible) {
        if (visible) {
            AnnotationWriter create = AnnotationWriter.create(this.symbolTable, typeRef, typePath, descriptor, this.lastRuntimeVisibleTypeAnnotation);
            this.lastRuntimeVisibleTypeAnnotation = create;
            return create;
        }
        AnnotationWriter create2 = AnnotationWriter.create(this.symbolTable, typeRef, typePath, descriptor, this.lastRuntimeInvisibleTypeAnnotation);
        this.lastRuntimeInvisibleTypeAnnotation = create2;
        return create2;
    }

    @Override // org.springframework.asm.ClassVisitor
    public final void visitAttribute(final Attribute attribute) {
        attribute.nextAttribute = this.firstAttribute;
        this.firstAttribute = attribute;
    }

    @Override // org.springframework.asm.ClassVisitor
    public final void visitNestMember(final String nestMember) {
        if (this.nestMemberClasses == null) {
            this.nestMemberClasses = new ByteVector();
        }
        this.numberOfNestMemberClasses++;
        this.nestMemberClasses.putShort(this.symbolTable.addConstantClass(nestMember).index);
    }

    @Override // org.springframework.asm.ClassVisitor
    public final void visitPermittedSubclass(final String permittedSubclass) {
        if (this.permittedSubclasses == null) {
            this.permittedSubclasses = new ByteVector();
        }
        this.numberOfPermittedSubclasses++;
        this.permittedSubclasses.putShort(this.symbolTable.addConstantClass(permittedSubclass).index);
    }

    @Override // org.springframework.asm.ClassVisitor
    public final void visitInnerClass(final String name, final String outerName, final String innerName, final int access) {
        if (this.innerClasses == null) {
            this.innerClasses = new ByteVector();
        }
        Symbol nameSymbol = this.symbolTable.addConstantClass(name);
        if (nameSymbol.info == 0) {
            this.numberOfInnerClasses++;
            this.innerClasses.putShort(nameSymbol.index);
            this.innerClasses.putShort(outerName == null ? 0 : this.symbolTable.addConstantClass(outerName).index);
            this.innerClasses.putShort(innerName == null ? 0 : this.symbolTable.addConstantUtf8(innerName));
            this.innerClasses.putShort(access);
            nameSymbol.info = this.numberOfInnerClasses;
        }
    }

    @Override // org.springframework.asm.ClassVisitor
    public final RecordComponentVisitor visitRecordComponent(final String name, final String descriptor, final String signature) {
        RecordComponentWriter recordComponentWriter = new RecordComponentWriter(this.symbolTable, name, descriptor, signature);
        if (this.firstRecordComponent == null) {
            this.firstRecordComponent = recordComponentWriter;
        } else {
            this.lastRecordComponent.delegate = recordComponentWriter;
        }
        this.lastRecordComponent = recordComponentWriter;
        return recordComponentWriter;
    }

    @Override // org.springframework.asm.ClassVisitor
    public final FieldVisitor visitField(final int access, final String name, final String descriptor, final String signature, final Object value) {
        FieldWriter fieldWriter = new FieldWriter(this.symbolTable, access, name, descriptor, signature, value);
        if (this.firstField == null) {
            this.firstField = fieldWriter;
        } else {
            this.lastField.fv = fieldWriter;
        }
        this.lastField = fieldWriter;
        return fieldWriter;
    }

    @Override // org.springframework.asm.ClassVisitor
    public final MethodVisitor visitMethod(final int access, final String name, final String descriptor, final String signature, final String[] exceptions) {
        MethodWriter methodWriter = new MethodWriter(this.symbolTable, access, name, descriptor, signature, exceptions, this.compute);
        if (this.firstMethod == null) {
            this.firstMethod = methodWriter;
        } else {
            this.lastMethod.mv = methodWriter;
        }
        this.lastMethod = methodWriter;
        return methodWriter;
    }

    @Override // org.springframework.asm.ClassVisitor
    public final void visitEnd() {
    }

    public byte[] toByteArray() {
        int size = 24 + (2 * this.interfaceCount);
        int fieldsCount = 0;
        FieldWriter fieldWriter = this.firstField;
        while (true) {
            FieldWriter fieldWriter2 = fieldWriter;
            if (fieldWriter2 == null) {
                break;
            }
            fieldsCount++;
            size += fieldWriter2.computeFieldInfoSize();
            fieldWriter = (FieldWriter) fieldWriter2.fv;
        }
        int methodsCount = 0;
        MethodWriter methodWriter = this.firstMethod;
        while (true) {
            MethodWriter methodWriter2 = methodWriter;
            if (methodWriter2 == null) {
                break;
            }
            methodsCount++;
            size += methodWriter2.computeMethodInfoSize();
            methodWriter = (MethodWriter) methodWriter2.mv;
        }
        int attributesCount = 0;
        if (this.innerClasses != null) {
            attributesCount = 0 + 1;
            size += 8 + this.innerClasses.length;
            this.symbolTable.addConstantUtf8("InnerClasses");
        }
        if (this.enclosingClassIndex != 0) {
            attributesCount++;
            size += 10;
            this.symbolTable.addConstantUtf8("EnclosingMethod");
        }
        if ((this.accessFlags & Opcodes.ACC_SYNTHETIC) != 0 && (this.version & UShort.MAX_VALUE) < 49) {
            attributesCount++;
            size += 6;
            this.symbolTable.addConstantUtf8("Synthetic");
        }
        if (this.signatureIndex != 0) {
            attributesCount++;
            size += 8;
            this.symbolTable.addConstantUtf8("Signature");
        }
        if (this.sourceFileIndex != 0) {
            attributesCount++;
            size += 8;
            this.symbolTable.addConstantUtf8("SourceFile");
        }
        if (this.debugExtension != null) {
            attributesCount++;
            size += 6 + this.debugExtension.length;
            this.symbolTable.addConstantUtf8("SourceDebugExtension");
        }
        if ((this.accessFlags & 131072) != 0) {
            attributesCount++;
            size += 6;
            this.symbolTable.addConstantUtf8("Deprecated");
        }
        if (this.lastRuntimeVisibleAnnotation != null) {
            attributesCount++;
            size += this.lastRuntimeVisibleAnnotation.computeAnnotationsSize("RuntimeVisibleAnnotations");
        }
        if (this.lastRuntimeInvisibleAnnotation != null) {
            attributesCount++;
            size += this.lastRuntimeInvisibleAnnotation.computeAnnotationsSize("RuntimeInvisibleAnnotations");
        }
        if (this.lastRuntimeVisibleTypeAnnotation != null) {
            attributesCount++;
            size += this.lastRuntimeVisibleTypeAnnotation.computeAnnotationsSize("RuntimeVisibleTypeAnnotations");
        }
        if (this.lastRuntimeInvisibleTypeAnnotation != null) {
            attributesCount++;
            size += this.lastRuntimeInvisibleTypeAnnotation.computeAnnotationsSize("RuntimeInvisibleTypeAnnotations");
        }
        if (this.symbolTable.computeBootstrapMethodsSize() > 0) {
            attributesCount++;
            size += this.symbolTable.computeBootstrapMethodsSize();
        }
        if (this.moduleWriter != null) {
            attributesCount += this.moduleWriter.getAttributeCount();
            size += this.moduleWriter.computeAttributesSize();
        }
        if (this.nestHostClassIndex != 0) {
            attributesCount++;
            size += 8;
            this.symbolTable.addConstantUtf8("NestHost");
        }
        if (this.nestMemberClasses != null) {
            attributesCount++;
            size += 8 + this.nestMemberClasses.length;
            this.symbolTable.addConstantUtf8("NestMembers");
        }
        if (this.permittedSubclasses != null) {
            attributesCount++;
            size += 8 + this.permittedSubclasses.length;
            this.symbolTable.addConstantUtf8("PermittedSubclasses");
        }
        int recordComponentCount = 0;
        int recordSize = 0;
        if ((this.accessFlags & 65536) != 0 || this.firstRecordComponent != null) {
            RecordComponentWriter recordComponentWriter = this.firstRecordComponent;
            while (true) {
                RecordComponentWriter recordComponentWriter2 = recordComponentWriter;
                if (recordComponentWriter2 == null) {
                    break;
                }
                recordComponentCount++;
                recordSize += recordComponentWriter2.computeRecordComponentInfoSize();
                recordComponentWriter = (RecordComponentWriter) recordComponentWriter2.delegate;
            }
            attributesCount++;
            size += 8 + recordSize;
            this.symbolTable.addConstantUtf8("Record");
        }
        if (this.firstAttribute != null) {
            attributesCount += this.firstAttribute.getAttributeCount();
            size += this.firstAttribute.computeAttributesSize(this.symbolTable);
        }
        int size2 = size + this.symbolTable.getConstantPoolLength();
        int constantPoolCount = this.symbolTable.getConstantPoolCount();
        if (constantPoolCount > 65535) {
            throw new ClassTooLargeException(this.symbolTable.getClassName(), constantPoolCount);
        }
        ByteVector result = new ByteVector(size2);
        result.putInt(Const.JVM_CLASSFILE_MAGIC).putInt(this.version);
        this.symbolTable.putConstantPool(result);
        int mask = (this.version & UShort.MAX_VALUE) < 49 ? Opcodes.ACC_SYNTHETIC : 0;
        result.putShort(this.accessFlags & (mask ^ (-1))).putShort(this.thisClass).putShort(this.superClass);
        result.putShort(this.interfaceCount);
        for (int i = 0; i < this.interfaceCount; i++) {
            result.putShort(this.interfaces[i]);
        }
        result.putShort(fieldsCount);
        FieldWriter fieldWriter3 = this.firstField;
        while (true) {
            FieldWriter fieldWriter4 = fieldWriter3;
            if (fieldWriter4 == null) {
                break;
            }
            fieldWriter4.putFieldInfo(result);
            fieldWriter3 = (FieldWriter) fieldWriter4.fv;
        }
        result.putShort(methodsCount);
        boolean hasFrames = false;
        boolean hasAsmInstructions = false;
        MethodWriter methodWriter3 = this.firstMethod;
        while (true) {
            MethodWriter methodWriter4 = methodWriter3;
            if (methodWriter4 == null) {
                break;
            }
            hasFrames |= methodWriter4.hasFrames();
            hasAsmInstructions |= methodWriter4.hasAsmInstructions();
            methodWriter4.putMethodInfo(result);
            methodWriter3 = (MethodWriter) methodWriter4.mv;
        }
        result.putShort(attributesCount);
        if (this.innerClasses != null) {
            result.putShort(this.symbolTable.addConstantUtf8("InnerClasses")).putInt(this.innerClasses.length + 2).putShort(this.numberOfInnerClasses).putByteArray(this.innerClasses.data, 0, this.innerClasses.length);
        }
        if (this.enclosingClassIndex != 0) {
            result.putShort(this.symbolTable.addConstantUtf8("EnclosingMethod")).putInt(4).putShort(this.enclosingClassIndex).putShort(this.enclosingMethodIndex);
        }
        if ((this.accessFlags & Opcodes.ACC_SYNTHETIC) != 0 && (this.version & UShort.MAX_VALUE) < 49) {
            result.putShort(this.symbolTable.addConstantUtf8("Synthetic")).putInt(0);
        }
        if (this.signatureIndex != 0) {
            result.putShort(this.symbolTable.addConstantUtf8("Signature")).putInt(2).putShort(this.signatureIndex);
        }
        if (this.sourceFileIndex != 0) {
            result.putShort(this.symbolTable.addConstantUtf8("SourceFile")).putInt(2).putShort(this.sourceFileIndex);
        }
        if (this.debugExtension != null) {
            int length = this.debugExtension.length;
            result.putShort(this.symbolTable.addConstantUtf8("SourceDebugExtension")).putInt(length).putByteArray(this.debugExtension.data, 0, length);
        }
        if ((this.accessFlags & 131072) != 0) {
            result.putShort(this.symbolTable.addConstantUtf8("Deprecated")).putInt(0);
        }
        AnnotationWriter.putAnnotations(this.symbolTable, this.lastRuntimeVisibleAnnotation, this.lastRuntimeInvisibleAnnotation, this.lastRuntimeVisibleTypeAnnotation, this.lastRuntimeInvisibleTypeAnnotation, result);
        this.symbolTable.putBootstrapMethods(result);
        if (this.moduleWriter != null) {
            this.moduleWriter.putAttributes(result);
        }
        if (this.nestHostClassIndex != 0) {
            result.putShort(this.symbolTable.addConstantUtf8("NestHost")).putInt(2).putShort(this.nestHostClassIndex);
        }
        if (this.nestMemberClasses != null) {
            result.putShort(this.symbolTable.addConstantUtf8("NestMembers")).putInt(this.nestMemberClasses.length + 2).putShort(this.numberOfNestMemberClasses).putByteArray(this.nestMemberClasses.data, 0, this.nestMemberClasses.length);
        }
        if (this.permittedSubclasses != null) {
            result.putShort(this.symbolTable.addConstantUtf8("PermittedSubclasses")).putInt(this.permittedSubclasses.length + 2).putShort(this.numberOfPermittedSubclasses).putByteArray(this.permittedSubclasses.data, 0, this.permittedSubclasses.length);
        }
        if ((this.accessFlags & 65536) != 0 || this.firstRecordComponent != null) {
            result.putShort(this.symbolTable.addConstantUtf8("Record")).putInt(recordSize + 2).putShort(recordComponentCount);
            RecordComponentWriter recordComponentWriter3 = this.firstRecordComponent;
            while (true) {
                RecordComponentWriter recordComponentWriter4 = recordComponentWriter3;
                if (recordComponentWriter4 == null) {
                    break;
                }
                recordComponentWriter4.putRecordComponentInfo(result);
                recordComponentWriter3 = (RecordComponentWriter) recordComponentWriter4.delegate;
            }
        }
        if (this.firstAttribute != null) {
            this.firstAttribute.putAttributes(this.symbolTable, result);
        }
        if (hasAsmInstructions) {
            return replaceAsmInstructions(result.data, hasFrames);
        }
        return result.data;
    }

    private byte[] replaceAsmInstructions(final byte[] classFile, final boolean hasFrames) {
        Attribute[] attributes = getAttributePrototypes();
        this.firstField = null;
        this.lastField = null;
        this.firstMethod = null;
        this.lastMethod = null;
        this.lastRuntimeVisibleAnnotation = null;
        this.lastRuntimeInvisibleAnnotation = null;
        this.lastRuntimeVisibleTypeAnnotation = null;
        this.lastRuntimeInvisibleTypeAnnotation = null;
        this.moduleWriter = null;
        this.nestHostClassIndex = 0;
        this.numberOfNestMemberClasses = 0;
        this.nestMemberClasses = null;
        this.numberOfPermittedSubclasses = 0;
        this.permittedSubclasses = null;
        this.firstRecordComponent = null;
        this.lastRecordComponent = null;
        this.firstAttribute = null;
        this.compute = hasFrames ? 3 : 0;
        new ClassReader(classFile, 0, false).accept(this, attributes, (hasFrames ? 8 : 0) | 256);
        return toByteArray();
    }

    private Attribute[] getAttributePrototypes() {
        Attribute.Set attributePrototypes = new Attribute.Set();
        attributePrototypes.addAttributes(this.firstAttribute);
        FieldWriter fieldWriter = this.firstField;
        while (true) {
            FieldWriter fieldWriter2 = fieldWriter;
            if (fieldWriter2 == null) {
                break;
            }
            fieldWriter2.collectAttributePrototypes(attributePrototypes);
            fieldWriter = (FieldWriter) fieldWriter2.fv;
        }
        MethodWriter methodWriter = this.firstMethod;
        while (true) {
            MethodWriter methodWriter2 = methodWriter;
            if (methodWriter2 == null) {
                break;
            }
            methodWriter2.collectAttributePrototypes(attributePrototypes);
            methodWriter = (MethodWriter) methodWriter2.mv;
        }
        RecordComponentWriter recordComponentWriter = this.firstRecordComponent;
        while (true) {
            RecordComponentWriter recordComponentWriter2 = recordComponentWriter;
            if (recordComponentWriter2 != null) {
                recordComponentWriter2.collectAttributePrototypes(attributePrototypes);
                recordComponentWriter = (RecordComponentWriter) recordComponentWriter2.delegate;
            } else {
                return attributePrototypes.toArray();
            }
        }
    }

    public int newConst(final Object value) {
        return this.symbolTable.addConstant(value).index;
    }

    public int newUTF8(final String value) {
        return this.symbolTable.addConstantUtf8(value);
    }

    public int newClass(final String value) {
        return this.symbolTable.addConstantClass(value).index;
    }

    public int newMethodType(final String methodDescriptor) {
        return this.symbolTable.addConstantMethodType(methodDescriptor).index;
    }

    public int newModule(final String moduleName) {
        return this.symbolTable.addConstantModule(moduleName).index;
    }

    public int newPackage(final String packageName) {
        return this.symbolTable.addConstantPackage(packageName).index;
    }

    @Deprecated
    public int newHandle(final int tag, final String owner, final String name, final String descriptor) {
        return newHandle(tag, owner, name, descriptor, tag == 9);
    }

    public int newHandle(final int tag, final String owner, final String name, final String descriptor, final boolean isInterface) {
        return this.symbolTable.addConstantMethodHandle(tag, owner, name, descriptor, isInterface).index;
    }

    public int newConstantDynamic(final String name, final String descriptor, final Handle bootstrapMethodHandle, final Object... bootstrapMethodArguments) {
        return this.symbolTable.addConstantDynamic(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments).index;
    }

    public int newInvokeDynamic(final String name, final String descriptor, final Handle bootstrapMethodHandle, final Object... bootstrapMethodArguments) {
        return this.symbolTable.addConstantInvokeDynamic(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments).index;
    }

    public int newField(final String owner, final String name, final String descriptor) {
        return this.symbolTable.addConstantFieldref(owner, name, descriptor).index;
    }

    public int newMethod(final String owner, final String name, final String descriptor, final boolean isInterface) {
        return this.symbolTable.addConstantMethodref(owner, name, descriptor, isInterface).index;
    }

    public int newNameType(final String name, final String descriptor) {
        return this.symbolTable.addConstantNameAndType(name, descriptor);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getCommonSuperClass(final String type1, final String type2) {
        ClassLoader classLoader = getClassLoader();
        try {
            Class<?> class1 = Class.forName(type1.replace('/', '.'), false, classLoader);
            try {
                Class<?> class2 = Class.forName(type2.replace('/', '.'), false, classLoader);
                if (class1.isAssignableFrom(class2)) {
                    return type1;
                }
                if (class2.isAssignableFrom(class1)) {
                    return type2;
                }
                if (class1.isInterface() || class2.isInterface()) {
                    return "java/lang/Object";
                }
                do {
                    class1 = class1.getSuperclass();
                } while (!class1.isAssignableFrom(class2));
                return class1.getName().replace('.', '/');
            } catch (ClassNotFoundException e) {
                throw new TypeNotPresentException(type2, e);
            }
        } catch (ClassNotFoundException e2) {
            throw new TypeNotPresentException(type1, e2);
        }
    }

    protected ClassLoader getClassLoader() {
        ClassLoader classLoader = null;
        try {
            classLoader = Thread.currentThread().getContextClassLoader();
        } catch (Throwable th) {
        }
        return classLoader != null ? classLoader : getClass().getClassLoader();
    }
}
