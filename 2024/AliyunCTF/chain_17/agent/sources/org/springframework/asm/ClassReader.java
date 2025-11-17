package org.springframework.asm;

import com.alibaba.com.caucho.hessian.io.Hessian2Constants;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.catalina.servlets.WebdavStatus;
import org.apache.tomcat.util.bcel.Const;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/asm/ClassReader.class */
public class ClassReader {
    public static final int SKIP_CODE = 1;
    public static final int SKIP_DEBUG = 2;
    public static final int SKIP_FRAMES = 4;
    public static final int EXPAND_FRAMES = 8;
    static final int EXPAND_ASM_INSNS = 256;
    private static final int MAX_BUFFER_SIZE = 1048576;
    private static final int INPUT_STREAM_DATA_CHUNK_SIZE = 4096;

    @Deprecated
    public final byte[] b;
    public final int header;
    final byte[] classFileBuffer;
    private final int[] cpInfoOffsets;
    private final String[] constantUtf8Values;
    private final ConstantDynamic[] constantDynamicValues;
    private final int[] bootstrapMethodOffsets;
    private final int maxStringLength;

    public ClassReader(final byte[] classFile) {
        this(classFile, 0, classFile.length);
    }

    public ClassReader(final byte[] classFileBuffer, final int classFileOffset, final int classFileLength) {
        this(classFileBuffer, classFileOffset, true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ClassReader(final byte[] classFileBuffer, final int classFileOffset, final boolean checkClassVersion) {
        int cpInfoSize;
        this.classFileBuffer = classFileBuffer;
        this.b = classFileBuffer;
        if (checkClassVersion && readShort(classFileOffset + 6) > 67) {
            throw new IllegalArgumentException("Unsupported class file major version " + readShort(classFileOffset + 6));
        }
        int constantPoolCount = readUnsignedShort(classFileOffset + 8);
        this.cpInfoOffsets = new int[constantPoolCount];
        this.constantUtf8Values = new String[constantPoolCount];
        int currentCpInfoIndex = 1;
        int currentCpInfoOffset = classFileOffset + 10;
        int currentMaxStringLength = 0;
        boolean hasBootstrapMethods = false;
        boolean hasConstantDynamic = false;
        while (currentCpInfoIndex < constantPoolCount) {
            int i = currentCpInfoIndex;
            currentCpInfoIndex++;
            this.cpInfoOffsets[i] = currentCpInfoOffset + 1;
            switch (classFileBuffer[currentCpInfoOffset]) {
                case 1:
                    cpInfoSize = 3 + readUnsignedShort(currentCpInfoOffset + 1);
                    if (cpInfoSize <= currentMaxStringLength) {
                        break;
                    } else {
                        currentMaxStringLength = cpInfoSize;
                        break;
                    }
                case 2:
                case 13:
                case 14:
                default:
                    throw new IllegalArgumentException();
                case 3:
                case 4:
                case 9:
                case 10:
                case 11:
                case 12:
                    cpInfoSize = 5;
                    break;
                case 5:
                case 6:
                    cpInfoSize = 9;
                    currentCpInfoIndex++;
                    break;
                case 7:
                case 8:
                case 16:
                case 19:
                case 20:
                    cpInfoSize = 3;
                    break;
                case 15:
                    cpInfoSize = 4;
                    break;
                case 17:
                    cpInfoSize = 5;
                    hasBootstrapMethods = true;
                    hasConstantDynamic = true;
                    break;
                case 18:
                    cpInfoSize = 5;
                    hasBootstrapMethods = true;
                    break;
            }
            currentCpInfoOffset += cpInfoSize;
        }
        this.maxStringLength = currentMaxStringLength;
        this.header = currentCpInfoOffset;
        this.constantDynamicValues = hasConstantDynamic ? new ConstantDynamic[constantPoolCount] : null;
        this.bootstrapMethodOffsets = hasBootstrapMethods ? readBootstrapMethodsAttribute(currentMaxStringLength) : null;
    }

    public ClassReader(final InputStream inputStream) throws IOException {
        this(readStream(inputStream, false));
    }

    public ClassReader(final String className) throws IOException {
        this(readStream(ClassLoader.getSystemResourceAsStream(className.replace('.', '/') + ".class"), true));
    }

    private static byte[] readStream(final InputStream inputStream, final boolean close) throws IOException {
        if (inputStream == null) {
            throw new IOException("Class not found");
        }
        int bufferSize = computeBufferSize(inputStream);
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                byte[] data = new byte[bufferSize];
                int readCount = 0;
                while (true) {
                    int bytesRead = inputStream.read(data, 0, bufferSize);
                    if (bytesRead == -1) {
                        break;
                    }
                    outputStream.write(data, 0, bytesRead);
                    readCount++;
                }
                outputStream.flush();
                if (readCount == 1) {
                }
                byte[] byteArray = outputStream.toByteArray();
                outputStream.close();
                if (close) {
                    inputStream.close();
                }
                return byteArray;
            } finally {
            }
        } catch (Throwable th) {
            if (close) {
                inputStream.close();
            }
            throw th;
        }
    }

    private static int computeBufferSize(final InputStream inputStream) throws IOException {
        int expectedLength = inputStream.available();
        if (expectedLength < 256) {
            return 4096;
        }
        return Math.min(expectedLength, 1048576);
    }

    public int getAccess() {
        return readUnsignedShort(this.header);
    }

    public String getClassName() {
        return readClass(this.header + 2, new char[this.maxStringLength]);
    }

    public String getSuperName() {
        return readClass(this.header + 4, new char[this.maxStringLength]);
    }

    public String[] getInterfaces() {
        int currentOffset = this.header + 6;
        int interfacesCount = readUnsignedShort(currentOffset);
        String[] interfaces = new String[interfacesCount];
        if (interfacesCount > 0) {
            char[] charBuffer = new char[this.maxStringLength];
            for (int i = 0; i < interfacesCount; i++) {
                currentOffset += 2;
                interfaces[i] = readClass(currentOffset, charBuffer);
            }
        }
        return interfaces;
    }

    public void accept(final ClassVisitor classVisitor, final int parsingOptions) {
        accept(classVisitor, new Attribute[0], parsingOptions);
    }

    public void accept(final ClassVisitor classVisitor, final Attribute[] attributePrototypes, final int parsingOptions) {
        Context context = new Context();
        context.attributePrototypes = attributePrototypes;
        context.parsingOptions = parsingOptions;
        context.charBuffer = new char[this.maxStringLength];
        char[] charBuffer = context.charBuffer;
        int currentOffset = this.header;
        int accessFlags = readUnsignedShort(currentOffset);
        String thisClass = readClass(currentOffset + 2, charBuffer);
        String superClass = readClass(currentOffset + 4, charBuffer);
        String[] interfaces = new String[readUnsignedShort(currentOffset + 6)];
        int currentOffset2 = currentOffset + 8;
        for (int i = 0; i < interfaces.length; i++) {
            interfaces[i] = readClass(currentOffset2, charBuffer);
            currentOffset2 += 2;
        }
        int innerClassesOffset = 0;
        int enclosingMethodOffset = 0;
        String signature = null;
        String sourceFile = null;
        String sourceDebugExtension = null;
        int runtimeVisibleAnnotationsOffset = 0;
        int runtimeInvisibleAnnotationsOffset = 0;
        int runtimeVisibleTypeAnnotationsOffset = 0;
        int runtimeInvisibleTypeAnnotationsOffset = 0;
        int moduleOffset = 0;
        int modulePackagesOffset = 0;
        String moduleMainClass = null;
        String nestHostClass = null;
        int nestMembersOffset = 0;
        int permittedSubclassesOffset = 0;
        int recordOffset = 0;
        Attribute attributes = null;
        int currentAttributeOffset = getFirstAttributeOffset();
        for (int i2 = readUnsignedShort(currentAttributeOffset - 2); i2 > 0; i2--) {
            String attributeName = readUTF8(currentAttributeOffset, charBuffer);
            int attributeLength = readInt(currentAttributeOffset + 2);
            int currentAttributeOffset2 = currentAttributeOffset + 6;
            if ("SourceFile".equals(attributeName)) {
                sourceFile = readUTF8(currentAttributeOffset2, charBuffer);
            } else if ("InnerClasses".equals(attributeName)) {
                innerClassesOffset = currentAttributeOffset2;
            } else if ("EnclosingMethod".equals(attributeName)) {
                enclosingMethodOffset = currentAttributeOffset2;
            } else if ("NestHost".equals(attributeName)) {
                nestHostClass = readClass(currentAttributeOffset2, charBuffer);
            } else if ("NestMembers".equals(attributeName)) {
                nestMembersOffset = currentAttributeOffset2;
            } else if ("PermittedSubclasses".equals(attributeName)) {
                permittedSubclassesOffset = currentAttributeOffset2;
            } else if ("Signature".equals(attributeName)) {
                signature = readUTF8(currentAttributeOffset2, charBuffer);
            } else if ("RuntimeVisibleAnnotations".equals(attributeName)) {
                runtimeVisibleAnnotationsOffset = currentAttributeOffset2;
            } else if ("RuntimeVisibleTypeAnnotations".equals(attributeName)) {
                runtimeVisibleTypeAnnotationsOffset = currentAttributeOffset2;
            } else if ("Deprecated".equals(attributeName)) {
                accessFlags |= 131072;
            } else if ("Synthetic".equals(attributeName)) {
                accessFlags |= 4096;
            } else if ("SourceDebugExtension".equals(attributeName)) {
                if (attributeLength > this.classFileBuffer.length - currentAttributeOffset2) {
                    throw new IllegalArgumentException();
                }
                sourceDebugExtension = readUtf(currentAttributeOffset2, attributeLength, new char[attributeLength]);
            } else if ("RuntimeInvisibleAnnotations".equals(attributeName)) {
                runtimeInvisibleAnnotationsOffset = currentAttributeOffset2;
            } else if ("RuntimeInvisibleTypeAnnotations".equals(attributeName)) {
                runtimeInvisibleTypeAnnotationsOffset = currentAttributeOffset2;
            } else if ("Record".equals(attributeName)) {
                recordOffset = currentAttributeOffset2;
                accessFlags |= 65536;
            } else if ("Module".equals(attributeName)) {
                moduleOffset = currentAttributeOffset2;
            } else if ("ModuleMainClass".equals(attributeName)) {
                moduleMainClass = readClass(currentAttributeOffset2, charBuffer);
            } else if ("ModulePackages".equals(attributeName)) {
                modulePackagesOffset = currentAttributeOffset2;
            } else if (!"BootstrapMethods".equals(attributeName)) {
                Attribute attribute = readAttribute(attributePrototypes, attributeName, currentAttributeOffset2, attributeLength, charBuffer, -1, null);
                attribute.nextAttribute = attributes;
                attributes = attribute;
            }
            currentAttributeOffset = currentAttributeOffset2 + attributeLength;
        }
        classVisitor.visit(readInt(this.cpInfoOffsets[1] - 7), accessFlags, thisClass, signature, superClass, interfaces);
        if ((parsingOptions & 2) == 0 && (sourceFile != null || sourceDebugExtension != null)) {
            classVisitor.visitSource(sourceFile, sourceDebugExtension);
        }
        if (moduleOffset != 0) {
            readModuleAttributes(classVisitor, context, moduleOffset, modulePackagesOffset, moduleMainClass);
        }
        if (nestHostClass != null) {
            classVisitor.visitNestHost(nestHostClass);
        }
        if (enclosingMethodOffset != 0) {
            String className = readClass(enclosingMethodOffset, charBuffer);
            int methodIndex = readUnsignedShort(enclosingMethodOffset + 2);
            String name = methodIndex == 0 ? null : readUTF8(this.cpInfoOffsets[methodIndex], charBuffer);
            String type = methodIndex == 0 ? null : readUTF8(this.cpInfoOffsets[methodIndex] + 2, charBuffer);
            classVisitor.visitOuterClass(className, name, type);
        }
        if (runtimeVisibleAnnotationsOffset != 0) {
            int numAnnotations = readUnsignedShort(runtimeVisibleAnnotationsOffset);
            int i3 = runtimeVisibleAnnotationsOffset + 2;
            while (true) {
                int currentAnnotationOffset = i3;
                int i4 = numAnnotations;
                numAnnotations--;
                if (i4 <= 0) {
                    break;
                }
                String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
                i3 = readElementValues(classVisitor.visitAnnotation(annotationDescriptor, true), currentAnnotationOffset + 2, true, charBuffer);
            }
        }
        if (runtimeInvisibleAnnotationsOffset != 0) {
            int numAnnotations2 = readUnsignedShort(runtimeInvisibleAnnotationsOffset);
            int i5 = runtimeInvisibleAnnotationsOffset + 2;
            while (true) {
                int currentAnnotationOffset2 = i5;
                int i6 = numAnnotations2;
                numAnnotations2--;
                if (i6 <= 0) {
                    break;
                }
                String annotationDescriptor2 = readUTF8(currentAnnotationOffset2, charBuffer);
                i5 = readElementValues(classVisitor.visitAnnotation(annotationDescriptor2, false), currentAnnotationOffset2 + 2, true, charBuffer);
            }
        }
        if (runtimeVisibleTypeAnnotationsOffset != 0) {
            int numAnnotations3 = readUnsignedShort(runtimeVisibleTypeAnnotationsOffset);
            int i7 = runtimeVisibleTypeAnnotationsOffset + 2;
            while (true) {
                int currentAnnotationOffset3 = i7;
                int i8 = numAnnotations3;
                numAnnotations3--;
                if (i8 <= 0) {
                    break;
                }
                int currentAnnotationOffset4 = readTypeAnnotationTarget(context, currentAnnotationOffset3);
                String annotationDescriptor3 = readUTF8(currentAnnotationOffset4, charBuffer);
                i7 = readElementValues(classVisitor.visitTypeAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, annotationDescriptor3, true), currentAnnotationOffset4 + 2, true, charBuffer);
            }
        }
        if (runtimeInvisibleTypeAnnotationsOffset != 0) {
            int numAnnotations4 = readUnsignedShort(runtimeInvisibleTypeAnnotationsOffset);
            int i9 = runtimeInvisibleTypeAnnotationsOffset + 2;
            while (true) {
                int currentAnnotationOffset5 = i9;
                int i10 = numAnnotations4;
                numAnnotations4--;
                if (i10 <= 0) {
                    break;
                }
                int currentAnnotationOffset6 = readTypeAnnotationTarget(context, currentAnnotationOffset5);
                String annotationDescriptor4 = readUTF8(currentAnnotationOffset6, charBuffer);
                i9 = readElementValues(classVisitor.visitTypeAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, annotationDescriptor4, false), currentAnnotationOffset6 + 2, true, charBuffer);
            }
        }
        while (attributes != null) {
            Attribute nextAttribute = attributes.nextAttribute;
            attributes.nextAttribute = null;
            classVisitor.visitAttribute(attributes);
            attributes = nextAttribute;
        }
        if (nestMembersOffset != 0) {
            int numberOfNestMembers = readUnsignedShort(nestMembersOffset);
            int currentNestMemberOffset = nestMembersOffset + 2;
            while (true) {
                int i11 = numberOfNestMembers;
                numberOfNestMembers--;
                if (i11 <= 0) {
                    break;
                }
                classVisitor.visitNestMember(readClass(currentNestMemberOffset, charBuffer));
                currentNestMemberOffset += 2;
            }
        }
        if (permittedSubclassesOffset != 0) {
            int numberOfPermittedSubclasses = readUnsignedShort(permittedSubclassesOffset);
            int currentPermittedSubclassesOffset = permittedSubclassesOffset + 2;
            while (true) {
                int i12 = numberOfPermittedSubclasses;
                numberOfPermittedSubclasses--;
                if (i12 <= 0) {
                    break;
                }
                classVisitor.visitPermittedSubclass(readClass(currentPermittedSubclassesOffset, charBuffer));
                currentPermittedSubclassesOffset += 2;
            }
        }
        if (innerClassesOffset != 0) {
            int numberOfClasses = readUnsignedShort(innerClassesOffset);
            int currentClassesOffset = innerClassesOffset + 2;
            while (true) {
                int i13 = numberOfClasses;
                numberOfClasses--;
                if (i13 <= 0) {
                    break;
                }
                classVisitor.visitInnerClass(readClass(currentClassesOffset, charBuffer), readClass(currentClassesOffset + 2, charBuffer), readUTF8(currentClassesOffset + 4, charBuffer), readUnsignedShort(currentClassesOffset + 6));
                currentClassesOffset += 8;
            }
        }
        if (recordOffset != 0) {
            int recordComponentsCount = readUnsignedShort(recordOffset);
            int recordOffset2 = recordOffset + 2;
            while (true) {
                int i14 = recordComponentsCount;
                recordComponentsCount--;
                if (i14 <= 0) {
                    break;
                } else {
                    recordOffset2 = readRecordComponent(classVisitor, context, recordOffset2);
                }
            }
        }
        int fieldsCount = readUnsignedShort(currentOffset2);
        int currentOffset3 = currentOffset2 + 2;
        while (true) {
            int i15 = fieldsCount;
            fieldsCount--;
            if (i15 <= 0) {
                break;
            } else {
                currentOffset3 = readField(classVisitor, context, currentOffset3);
            }
        }
        int methodsCount = readUnsignedShort(currentOffset3);
        int currentOffset4 = currentOffset3 + 2;
        while (true) {
            int i16 = methodsCount;
            methodsCount--;
            if (i16 > 0) {
                currentOffset4 = readMethod(classVisitor, context, currentOffset4);
            } else {
                classVisitor.visitEnd();
                return;
            }
        }
    }

    private void readModuleAttributes(final ClassVisitor classVisitor, final Context context, final int moduleOffset, final int modulePackagesOffset, final String moduleMainClass) {
        char[] buffer = context.charBuffer;
        String moduleName = readModule(moduleOffset, buffer);
        int moduleFlags = readUnsignedShort(moduleOffset + 2);
        String moduleVersion = readUTF8(moduleOffset + 4, buffer);
        int currentOffset = moduleOffset + 6;
        ModuleVisitor moduleVisitor = classVisitor.visitModule(moduleName, moduleFlags, moduleVersion);
        if (moduleVisitor == null) {
            return;
        }
        if (moduleMainClass != null) {
            moduleVisitor.visitMainClass(moduleMainClass);
        }
        if (modulePackagesOffset != 0) {
            int packageCount = readUnsignedShort(modulePackagesOffset);
            int currentPackageOffset = modulePackagesOffset + 2;
            while (true) {
                int i = packageCount;
                packageCount--;
                if (i <= 0) {
                    break;
                }
                moduleVisitor.visitPackage(readPackage(currentPackageOffset, buffer));
                currentPackageOffset += 2;
            }
        }
        int requiresCount = readUnsignedShort(currentOffset);
        int currentOffset2 = currentOffset + 2;
        while (true) {
            int i2 = requiresCount;
            requiresCount--;
            if (i2 <= 0) {
                break;
            }
            String requires = readModule(currentOffset2, buffer);
            int requiresFlags = readUnsignedShort(currentOffset2 + 2);
            String requiresVersion = readUTF8(currentOffset2 + 4, buffer);
            currentOffset2 += 6;
            moduleVisitor.visitRequire(requires, requiresFlags, requiresVersion);
        }
        int exportsCount = readUnsignedShort(currentOffset2);
        int currentOffset3 = currentOffset2 + 2;
        while (true) {
            int i3 = exportsCount;
            exportsCount--;
            if (i3 <= 0) {
                break;
            }
            String exports = readPackage(currentOffset3, buffer);
            int exportsFlags = readUnsignedShort(currentOffset3 + 2);
            int exportsToCount = readUnsignedShort(currentOffset3 + 4);
            currentOffset3 += 6;
            String[] exportsTo = null;
            if (exportsToCount != 0) {
                exportsTo = new String[exportsToCount];
                for (int i4 = 0; i4 < exportsToCount; i4++) {
                    exportsTo[i4] = readModule(currentOffset3, buffer);
                    currentOffset3 += 2;
                }
            }
            moduleVisitor.visitExport(exports, exportsFlags, exportsTo);
        }
        int opensCount = readUnsignedShort(currentOffset3);
        int currentOffset4 = currentOffset3 + 2;
        while (true) {
            int i5 = opensCount;
            opensCount--;
            if (i5 <= 0) {
                break;
            }
            String opens = readPackage(currentOffset4, buffer);
            int opensFlags = readUnsignedShort(currentOffset4 + 2);
            int opensToCount = readUnsignedShort(currentOffset4 + 4);
            currentOffset4 += 6;
            String[] opensTo = null;
            if (opensToCount != 0) {
                opensTo = new String[opensToCount];
                for (int i6 = 0; i6 < opensToCount; i6++) {
                    opensTo[i6] = readModule(currentOffset4, buffer);
                    currentOffset4 += 2;
                }
            }
            moduleVisitor.visitOpen(opens, opensFlags, opensTo);
        }
        int usesCount = readUnsignedShort(currentOffset4);
        while (true) {
            currentOffset4 += 2;
            int i7 = usesCount;
            usesCount--;
            if (i7 <= 0) {
                break;
            } else {
                moduleVisitor.visitUse(readClass(currentOffset4, buffer));
            }
        }
        int providesCount = readUnsignedShort(currentOffset4);
        int currentOffset5 = currentOffset4 + 2;
        while (true) {
            int i8 = providesCount;
            providesCount--;
            if (i8 > 0) {
                String provides = readClass(currentOffset5, buffer);
                int providesWithCount = readUnsignedShort(currentOffset5 + 2);
                currentOffset5 += 4;
                String[] providesWith = new String[providesWithCount];
                for (int i9 = 0; i9 < providesWithCount; i9++) {
                    providesWith[i9] = readClass(currentOffset5, buffer);
                    currentOffset5 += 2;
                }
                moduleVisitor.visitProvide(provides, providesWith);
            } else {
                moduleVisitor.visitEnd();
                return;
            }
        }
    }

    private int readRecordComponent(final ClassVisitor classVisitor, final Context context, final int recordComponentOffset) {
        char[] charBuffer = context.charBuffer;
        String name = readUTF8(recordComponentOffset, charBuffer);
        String descriptor = readUTF8(recordComponentOffset + 2, charBuffer);
        int currentOffset = recordComponentOffset + 4;
        String signature = null;
        int runtimeVisibleAnnotationsOffset = 0;
        int runtimeInvisibleAnnotationsOffset = 0;
        int runtimeVisibleTypeAnnotationsOffset = 0;
        int runtimeInvisibleTypeAnnotationsOffset = 0;
        Attribute attributes = null;
        int attributesCount = readUnsignedShort(currentOffset);
        int currentOffset2 = currentOffset + 2;
        while (true) {
            int i = attributesCount;
            attributesCount--;
            if (i <= 0) {
                break;
            }
            String attributeName = readUTF8(currentOffset2, charBuffer);
            int attributeLength = readInt(currentOffset2 + 2);
            int currentOffset3 = currentOffset2 + 6;
            if ("Signature".equals(attributeName)) {
                signature = readUTF8(currentOffset3, charBuffer);
            } else if ("RuntimeVisibleAnnotations".equals(attributeName)) {
                runtimeVisibleAnnotationsOffset = currentOffset3;
            } else if ("RuntimeVisibleTypeAnnotations".equals(attributeName)) {
                runtimeVisibleTypeAnnotationsOffset = currentOffset3;
            } else if ("RuntimeInvisibleAnnotations".equals(attributeName)) {
                runtimeInvisibleAnnotationsOffset = currentOffset3;
            } else if ("RuntimeInvisibleTypeAnnotations".equals(attributeName)) {
                runtimeInvisibleTypeAnnotationsOffset = currentOffset3;
            } else {
                Attribute attribute = readAttribute(context.attributePrototypes, attributeName, currentOffset3, attributeLength, charBuffer, -1, null);
                attribute.nextAttribute = attributes;
                attributes = attribute;
            }
            currentOffset2 = currentOffset3 + attributeLength;
        }
        RecordComponentVisitor recordComponentVisitor = classVisitor.visitRecordComponent(name, descriptor, signature);
        if (recordComponentVisitor == null) {
            return currentOffset2;
        }
        if (runtimeVisibleAnnotationsOffset != 0) {
            int numAnnotations = readUnsignedShort(runtimeVisibleAnnotationsOffset);
            int i2 = runtimeVisibleAnnotationsOffset + 2;
            while (true) {
                int currentAnnotationOffset = i2;
                int i3 = numAnnotations;
                numAnnotations--;
                if (i3 <= 0) {
                    break;
                }
                String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
                i2 = readElementValues(recordComponentVisitor.visitAnnotation(annotationDescriptor, true), currentAnnotationOffset + 2, true, charBuffer);
            }
        }
        if (runtimeInvisibleAnnotationsOffset != 0) {
            int numAnnotations2 = readUnsignedShort(runtimeInvisibleAnnotationsOffset);
            int i4 = runtimeInvisibleAnnotationsOffset + 2;
            while (true) {
                int currentAnnotationOffset2 = i4;
                int i5 = numAnnotations2;
                numAnnotations2--;
                if (i5 <= 0) {
                    break;
                }
                String annotationDescriptor2 = readUTF8(currentAnnotationOffset2, charBuffer);
                i4 = readElementValues(recordComponentVisitor.visitAnnotation(annotationDescriptor2, false), currentAnnotationOffset2 + 2, true, charBuffer);
            }
        }
        if (runtimeVisibleTypeAnnotationsOffset != 0) {
            int numAnnotations3 = readUnsignedShort(runtimeVisibleTypeAnnotationsOffset);
            int i6 = runtimeVisibleTypeAnnotationsOffset + 2;
            while (true) {
                int currentAnnotationOffset3 = i6;
                int i7 = numAnnotations3;
                numAnnotations3--;
                if (i7 <= 0) {
                    break;
                }
                int currentAnnotationOffset4 = readTypeAnnotationTarget(context, currentAnnotationOffset3);
                String annotationDescriptor3 = readUTF8(currentAnnotationOffset4, charBuffer);
                i6 = readElementValues(recordComponentVisitor.visitTypeAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, annotationDescriptor3, true), currentAnnotationOffset4 + 2, true, charBuffer);
            }
        }
        if (runtimeInvisibleTypeAnnotationsOffset != 0) {
            int numAnnotations4 = readUnsignedShort(runtimeInvisibleTypeAnnotationsOffset);
            int i8 = runtimeInvisibleTypeAnnotationsOffset + 2;
            while (true) {
                int currentAnnotationOffset5 = i8;
                int i9 = numAnnotations4;
                numAnnotations4--;
                if (i9 <= 0) {
                    break;
                }
                int currentAnnotationOffset6 = readTypeAnnotationTarget(context, currentAnnotationOffset5);
                String annotationDescriptor4 = readUTF8(currentAnnotationOffset6, charBuffer);
                i8 = readElementValues(recordComponentVisitor.visitTypeAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, annotationDescriptor4, false), currentAnnotationOffset6 + 2, true, charBuffer);
            }
        }
        while (attributes != null) {
            Attribute nextAttribute = attributes.nextAttribute;
            attributes.nextAttribute = null;
            recordComponentVisitor.visitAttribute(attributes);
            attributes = nextAttribute;
        }
        recordComponentVisitor.visitEnd();
        return currentOffset2;
    }

    private int readField(final ClassVisitor classVisitor, final Context context, final int fieldInfoOffset) {
        char[] charBuffer = context.charBuffer;
        int accessFlags = readUnsignedShort(fieldInfoOffset);
        String name = readUTF8(fieldInfoOffset + 2, charBuffer);
        String descriptor = readUTF8(fieldInfoOffset + 4, charBuffer);
        int currentOffset = fieldInfoOffset + 6;
        Object constantValue = null;
        String signature = null;
        int runtimeVisibleAnnotationsOffset = 0;
        int runtimeInvisibleAnnotationsOffset = 0;
        int runtimeVisibleTypeAnnotationsOffset = 0;
        int runtimeInvisibleTypeAnnotationsOffset = 0;
        Attribute attributes = null;
        int attributesCount = readUnsignedShort(currentOffset);
        int currentOffset2 = currentOffset + 2;
        while (true) {
            int i = attributesCount;
            attributesCount--;
            if (i <= 0) {
                break;
            }
            String attributeName = readUTF8(currentOffset2, charBuffer);
            int attributeLength = readInt(currentOffset2 + 2);
            int currentOffset3 = currentOffset2 + 6;
            if ("ConstantValue".equals(attributeName)) {
                int constantvalueIndex = readUnsignedShort(currentOffset3);
                constantValue = constantvalueIndex == 0 ? null : readConst(constantvalueIndex, charBuffer);
            } else if ("Signature".equals(attributeName)) {
                signature = readUTF8(currentOffset3, charBuffer);
            } else if ("Deprecated".equals(attributeName)) {
                accessFlags |= 131072;
            } else if ("Synthetic".equals(attributeName)) {
                accessFlags |= 4096;
            } else if ("RuntimeVisibleAnnotations".equals(attributeName)) {
                runtimeVisibleAnnotationsOffset = currentOffset3;
            } else if ("RuntimeVisibleTypeAnnotations".equals(attributeName)) {
                runtimeVisibleTypeAnnotationsOffset = currentOffset3;
            } else if ("RuntimeInvisibleAnnotations".equals(attributeName)) {
                runtimeInvisibleAnnotationsOffset = currentOffset3;
            } else if ("RuntimeInvisibleTypeAnnotations".equals(attributeName)) {
                runtimeInvisibleTypeAnnotationsOffset = currentOffset3;
            } else {
                Attribute attribute = readAttribute(context.attributePrototypes, attributeName, currentOffset3, attributeLength, charBuffer, -1, null);
                attribute.nextAttribute = attributes;
                attributes = attribute;
            }
            currentOffset2 = currentOffset3 + attributeLength;
        }
        FieldVisitor fieldVisitor = classVisitor.visitField(accessFlags, name, descriptor, signature, constantValue);
        if (fieldVisitor == null) {
            return currentOffset2;
        }
        if (runtimeVisibleAnnotationsOffset != 0) {
            int numAnnotations = readUnsignedShort(runtimeVisibleAnnotationsOffset);
            int i2 = runtimeVisibleAnnotationsOffset + 2;
            while (true) {
                int currentAnnotationOffset = i2;
                int i3 = numAnnotations;
                numAnnotations--;
                if (i3 <= 0) {
                    break;
                }
                String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
                i2 = readElementValues(fieldVisitor.visitAnnotation(annotationDescriptor, true), currentAnnotationOffset + 2, true, charBuffer);
            }
        }
        if (runtimeInvisibleAnnotationsOffset != 0) {
            int numAnnotations2 = readUnsignedShort(runtimeInvisibleAnnotationsOffset);
            int i4 = runtimeInvisibleAnnotationsOffset + 2;
            while (true) {
                int currentAnnotationOffset2 = i4;
                int i5 = numAnnotations2;
                numAnnotations2--;
                if (i5 <= 0) {
                    break;
                }
                String annotationDescriptor2 = readUTF8(currentAnnotationOffset2, charBuffer);
                i4 = readElementValues(fieldVisitor.visitAnnotation(annotationDescriptor2, false), currentAnnotationOffset2 + 2, true, charBuffer);
            }
        }
        if (runtimeVisibleTypeAnnotationsOffset != 0) {
            int numAnnotations3 = readUnsignedShort(runtimeVisibleTypeAnnotationsOffset);
            int i6 = runtimeVisibleTypeAnnotationsOffset + 2;
            while (true) {
                int currentAnnotationOffset3 = i6;
                int i7 = numAnnotations3;
                numAnnotations3--;
                if (i7 <= 0) {
                    break;
                }
                int currentAnnotationOffset4 = readTypeAnnotationTarget(context, currentAnnotationOffset3);
                String annotationDescriptor3 = readUTF8(currentAnnotationOffset4, charBuffer);
                i6 = readElementValues(fieldVisitor.visitTypeAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, annotationDescriptor3, true), currentAnnotationOffset4 + 2, true, charBuffer);
            }
        }
        if (runtimeInvisibleTypeAnnotationsOffset != 0) {
            int numAnnotations4 = readUnsignedShort(runtimeInvisibleTypeAnnotationsOffset);
            int i8 = runtimeInvisibleTypeAnnotationsOffset + 2;
            while (true) {
                int currentAnnotationOffset5 = i8;
                int i9 = numAnnotations4;
                numAnnotations4--;
                if (i9 <= 0) {
                    break;
                }
                int currentAnnotationOffset6 = readTypeAnnotationTarget(context, currentAnnotationOffset5);
                String annotationDescriptor4 = readUTF8(currentAnnotationOffset6, charBuffer);
                i8 = readElementValues(fieldVisitor.visitTypeAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, annotationDescriptor4, false), currentAnnotationOffset6 + 2, true, charBuffer);
            }
        }
        while (attributes != null) {
            Attribute nextAttribute = attributes.nextAttribute;
            attributes.nextAttribute = null;
            fieldVisitor.visitAttribute(attributes);
            attributes = nextAttribute;
        }
        fieldVisitor.visitEnd();
        return currentOffset2;
    }

    private int readMethod(final ClassVisitor classVisitor, final Context context, final int methodInfoOffset) {
        char[] charBuffer = context.charBuffer;
        context.currentMethodAccessFlags = readUnsignedShort(methodInfoOffset);
        context.currentMethodName = readUTF8(methodInfoOffset + 2, charBuffer);
        context.currentMethodDescriptor = readUTF8(methodInfoOffset + 4, charBuffer);
        int currentOffset = methodInfoOffset + 6;
        int codeOffset = 0;
        int exceptionsOffset = 0;
        String[] exceptions = null;
        boolean synthetic = false;
        int signatureIndex = 0;
        int runtimeVisibleAnnotationsOffset = 0;
        int runtimeInvisibleAnnotationsOffset = 0;
        int runtimeVisibleParameterAnnotationsOffset = 0;
        int runtimeInvisibleParameterAnnotationsOffset = 0;
        int runtimeVisibleTypeAnnotationsOffset = 0;
        int runtimeInvisibleTypeAnnotationsOffset = 0;
        int annotationDefaultOffset = 0;
        int methodParametersOffset = 0;
        Attribute attributes = null;
        int attributesCount = readUnsignedShort(currentOffset);
        int currentOffset2 = currentOffset + 2;
        while (true) {
            int i = attributesCount;
            attributesCount--;
            if (i <= 0) {
                break;
            }
            String attributeName = readUTF8(currentOffset2, charBuffer);
            int attributeLength = readInt(currentOffset2 + 2);
            int currentOffset3 = currentOffset2 + 6;
            if ("Code".equals(attributeName)) {
                if ((context.parsingOptions & 1) == 0) {
                    codeOffset = currentOffset3;
                }
            } else if ("Exceptions".equals(attributeName)) {
                exceptionsOffset = currentOffset3;
                exceptions = new String[readUnsignedShort(exceptionsOffset)];
                int currentExceptionOffset = exceptionsOffset + 2;
                for (int i2 = 0; i2 < exceptions.length; i2++) {
                    exceptions[i2] = readClass(currentExceptionOffset, charBuffer);
                    currentExceptionOffset += 2;
                }
            } else if ("Signature".equals(attributeName)) {
                signatureIndex = readUnsignedShort(currentOffset3);
            } else if ("Deprecated".equals(attributeName)) {
                context.currentMethodAccessFlags |= 131072;
            } else if ("RuntimeVisibleAnnotations".equals(attributeName)) {
                runtimeVisibleAnnotationsOffset = currentOffset3;
            } else if ("RuntimeVisibleTypeAnnotations".equals(attributeName)) {
                runtimeVisibleTypeAnnotationsOffset = currentOffset3;
            } else if ("AnnotationDefault".equals(attributeName)) {
                annotationDefaultOffset = currentOffset3;
            } else if ("Synthetic".equals(attributeName)) {
                synthetic = true;
                context.currentMethodAccessFlags |= 4096;
            } else if ("RuntimeInvisibleAnnotations".equals(attributeName)) {
                runtimeInvisibleAnnotationsOffset = currentOffset3;
            } else if ("RuntimeInvisibleTypeAnnotations".equals(attributeName)) {
                runtimeInvisibleTypeAnnotationsOffset = currentOffset3;
            } else if ("RuntimeVisibleParameterAnnotations".equals(attributeName)) {
                runtimeVisibleParameterAnnotationsOffset = currentOffset3;
            } else if ("RuntimeInvisibleParameterAnnotations".equals(attributeName)) {
                runtimeInvisibleParameterAnnotationsOffset = currentOffset3;
            } else if ("MethodParameters".equals(attributeName)) {
                methodParametersOffset = currentOffset3;
            } else {
                Attribute attribute = readAttribute(context.attributePrototypes, attributeName, currentOffset3, attributeLength, charBuffer, -1, null);
                attribute.nextAttribute = attributes;
                attributes = attribute;
            }
            currentOffset2 = currentOffset3 + attributeLength;
        }
        MethodVisitor methodVisitor = classVisitor.visitMethod(context.currentMethodAccessFlags, context.currentMethodName, context.currentMethodDescriptor, signatureIndex == 0 ? null : readUtf(signatureIndex, charBuffer), exceptions);
        if (methodVisitor == null) {
            return currentOffset2;
        }
        if (methodVisitor instanceof MethodWriter) {
            MethodWriter methodWriter = (MethodWriter) methodVisitor;
            if (methodWriter.canCopyMethodAttributes(this, synthetic, (context.currentMethodAccessFlags & 131072) != 0, readUnsignedShort(methodInfoOffset + 4), signatureIndex, exceptionsOffset)) {
                methodWriter.setMethodAttributesSource(methodInfoOffset, currentOffset2 - methodInfoOffset);
                return currentOffset2;
            }
        }
        if (methodParametersOffset != 0 && (context.parsingOptions & 2) == 0) {
            int parametersCount = readByte(methodParametersOffset);
            int currentParameterOffset = methodParametersOffset + 1;
            while (true) {
                int i3 = parametersCount;
                parametersCount--;
                if (i3 <= 0) {
                    break;
                }
                methodVisitor.visitParameter(readUTF8(currentParameterOffset, charBuffer), readUnsignedShort(currentParameterOffset + 2));
                currentParameterOffset += 4;
            }
        }
        if (annotationDefaultOffset != 0) {
            AnnotationVisitor annotationVisitor = methodVisitor.visitAnnotationDefault();
            readElementValue(annotationVisitor, annotationDefaultOffset, null, charBuffer);
            if (annotationVisitor != null) {
                annotationVisitor.visitEnd();
            }
        }
        if (runtimeVisibleAnnotationsOffset != 0) {
            int numAnnotations = readUnsignedShort(runtimeVisibleAnnotationsOffset);
            int i4 = runtimeVisibleAnnotationsOffset + 2;
            while (true) {
                int currentAnnotationOffset = i4;
                int i5 = numAnnotations;
                numAnnotations--;
                if (i5 <= 0) {
                    break;
                }
                String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
                i4 = readElementValues(methodVisitor.visitAnnotation(annotationDescriptor, true), currentAnnotationOffset + 2, true, charBuffer);
            }
        }
        if (runtimeInvisibleAnnotationsOffset != 0) {
            int numAnnotations2 = readUnsignedShort(runtimeInvisibleAnnotationsOffset);
            int i6 = runtimeInvisibleAnnotationsOffset + 2;
            while (true) {
                int currentAnnotationOffset2 = i6;
                int i7 = numAnnotations2;
                numAnnotations2--;
                if (i7 <= 0) {
                    break;
                }
                String annotationDescriptor2 = readUTF8(currentAnnotationOffset2, charBuffer);
                i6 = readElementValues(methodVisitor.visitAnnotation(annotationDescriptor2, false), currentAnnotationOffset2 + 2, true, charBuffer);
            }
        }
        if (runtimeVisibleTypeAnnotationsOffset != 0) {
            int numAnnotations3 = readUnsignedShort(runtimeVisibleTypeAnnotationsOffset);
            int i8 = runtimeVisibleTypeAnnotationsOffset + 2;
            while (true) {
                int currentAnnotationOffset3 = i8;
                int i9 = numAnnotations3;
                numAnnotations3--;
                if (i9 <= 0) {
                    break;
                }
                int currentAnnotationOffset4 = readTypeAnnotationTarget(context, currentAnnotationOffset3);
                String annotationDescriptor3 = readUTF8(currentAnnotationOffset4, charBuffer);
                i8 = readElementValues(methodVisitor.visitTypeAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, annotationDescriptor3, true), currentAnnotationOffset4 + 2, true, charBuffer);
            }
        }
        if (runtimeInvisibleTypeAnnotationsOffset != 0) {
            int numAnnotations4 = readUnsignedShort(runtimeInvisibleTypeAnnotationsOffset);
            int i10 = runtimeInvisibleTypeAnnotationsOffset + 2;
            while (true) {
                int currentAnnotationOffset5 = i10;
                int i11 = numAnnotations4;
                numAnnotations4--;
                if (i11 <= 0) {
                    break;
                }
                int currentAnnotationOffset6 = readTypeAnnotationTarget(context, currentAnnotationOffset5);
                String annotationDescriptor4 = readUTF8(currentAnnotationOffset6, charBuffer);
                i10 = readElementValues(methodVisitor.visitTypeAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, annotationDescriptor4, false), currentAnnotationOffset6 + 2, true, charBuffer);
            }
        }
        if (runtimeVisibleParameterAnnotationsOffset != 0) {
            readParameterAnnotations(methodVisitor, context, runtimeVisibleParameterAnnotationsOffset, true);
        }
        if (runtimeInvisibleParameterAnnotationsOffset != 0) {
            readParameterAnnotations(methodVisitor, context, runtimeInvisibleParameterAnnotationsOffset, false);
        }
        while (attributes != null) {
            Attribute nextAttribute = attributes.nextAttribute;
            attributes.nextAttribute = null;
            methodVisitor.visitAttribute(attributes);
            attributes = nextAttribute;
        }
        if (codeOffset != 0) {
            methodVisitor.visitCode();
            readCode(methodVisitor, context, codeOffset);
        }
        methodVisitor.visitEnd();
        return currentOffset2;
    }

    private void readCode(final MethodVisitor methodVisitor, final Context context, final int codeOffset) {
        int i;
        int potentialBytecodeOffset;
        byte[] classBuffer = this.classFileBuffer;
        char[] charBuffer = context.charBuffer;
        int maxStack = readUnsignedShort(codeOffset);
        int maxLocals = readUnsignedShort(codeOffset + 2);
        int codeLength = readInt(codeOffset + 4);
        int currentOffset = codeOffset + 8;
        if (codeLength > this.classFileBuffer.length - currentOffset) {
            throw new IllegalArgumentException();
        }
        int bytecodeEndOffset = currentOffset + codeLength;
        Label[] labels = new Label[codeLength + 1];
        context.currentMethodLabels = labels;
        while (currentOffset < bytecodeEndOffset) {
            int bytecodeOffset = currentOffset - currentOffset;
            switch (classBuffer[currentOffset] & Const.MAX_ARRAY_DIMENSIONS) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                case 26:
                case 27:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 59:
                case 60:
                case 61:
                case 62:
                case 63:
                case 64:
                case 65:
                case 66:
                case 67:
                case 68:
                case 69:
                case 70:
                case 71:
                case 72:
                case 73:
                case 74:
                case 75:
                case 76:
                case 77:
                case 78:
                case 79:
                case 80:
                case 81:
                case 82:
                case 83:
                case 84:
                case 85:
                case 86:
                case 87:
                case 88:
                case 89:
                case 90:
                case 91:
                case 92:
                case 93:
                case 94:
                case 95:
                case 96:
                case 97:
                case 98:
                case 99:
                case 100:
                case 101:
                case 102:
                case 103:
                case 104:
                case 105:
                case 106:
                case Opcodes.DMUL /* 107 */:
                case 108:
                case Opcodes.LDIV /* 109 */:
                case Opcodes.FDIV /* 110 */:
                case Opcodes.DDIV /* 111 */:
                case 112:
                case Opcodes.LREM /* 113 */:
                case Opcodes.FREM /* 114 */:
                case 115:
                case 116:
                case Opcodes.LNEG /* 117 */:
                case Opcodes.FNEG /* 118 */:
                case Opcodes.DNEG /* 119 */:
                case 120:
                case Opcodes.LSHL /* 121 */:
                case 122:
                case 123:
                case 124:
                case 125:
                case 126:
                case 127:
                case 128:
                case Opcodes.LOR /* 129 */:
                case 130:
                case Opcodes.LXOR /* 131 */:
                case Opcodes.I2L /* 133 */:
                case Opcodes.I2F /* 134 */:
                case Opcodes.I2D /* 135 */:
                case 136:
                case Opcodes.L2F /* 137 */:
                case Opcodes.L2D /* 138 */:
                case Opcodes.F2I /* 139 */:
                case Opcodes.F2L /* 140 */:
                case Opcodes.F2D /* 141 */:
                case Opcodes.D2I /* 142 */:
                case Opcodes.D2L /* 143 */:
                case 144:
                case Opcodes.I2B /* 145 */:
                case Opcodes.I2C /* 146 */:
                case Opcodes.I2S /* 147 */:
                case Opcodes.LCMP /* 148 */:
                case Opcodes.FCMPL /* 149 */:
                case 150:
                case Opcodes.DCMPL /* 151 */:
                case 152:
                case Opcodes.IRETURN /* 172 */:
                case Opcodes.LRETURN /* 173 */:
                case Opcodes.FRETURN /* 174 */:
                case Opcodes.DRETURN /* 175 */:
                case 176:
                case Opcodes.RETURN /* 177 */:
                case Opcodes.ARRAYLENGTH /* 190 */:
                case Opcodes.ATHROW /* 191 */:
                case Opcodes.MONITORENTER /* 194 */:
                case Opcodes.MONITOREXIT /* 195 */:
                    currentOffset++;
                    break;
                case 16:
                case 18:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 54:
                case 55:
                case 56:
                case 57:
                case 58:
                case Opcodes.RET /* 169 */:
                case Opcodes.NEWARRAY /* 188 */:
                    currentOffset += 2;
                    break;
                case 17:
                case 19:
                case 20:
                case Opcodes.IINC /* 132 */:
                case Opcodes.GETSTATIC /* 178 */:
                case Opcodes.PUTSTATIC /* 179 */:
                case Opcodes.GETFIELD /* 180 */:
                case Opcodes.PUTFIELD /* 181 */:
                case Opcodes.INVOKEVIRTUAL /* 182 */:
                case Opcodes.INVOKESPECIAL /* 183 */:
                case 184:
                case Opcodes.NEW /* 187 */:
                case Opcodes.ANEWARRAY /* 189 */:
                case Opcodes.CHECKCAST /* 192 */:
                case Opcodes.INSTANCEOF /* 193 */:
                    currentOffset += 3;
                    break;
                case 153:
                case 154:
                case 155:
                case 156:
                case 157:
                case 158:
                case Opcodes.IF_ICMPEQ /* 159 */:
                case 160:
                case Opcodes.IF_ICMPLT /* 161 */:
                case Opcodes.IF_ICMPGE /* 162 */:
                case Opcodes.IF_ICMPGT /* 163 */:
                case Opcodes.IF_ICMPLE /* 164 */:
                case Opcodes.IF_ACMPEQ /* 165 */:
                case Opcodes.IF_ACMPNE /* 166 */:
                case Opcodes.GOTO /* 167 */:
                case 168:
                case Opcodes.IFNULL /* 198 */:
                case Opcodes.IFNONNULL /* 199 */:
                    createLabel(bytecodeOffset + readShort(currentOffset + 1), labels);
                    currentOffset += 3;
                    break;
                case Opcodes.TABLESWITCH /* 170 */:
                    int currentOffset2 = currentOffset + (4 - (bytecodeOffset & 3));
                    createLabel(bytecodeOffset + readInt(currentOffset2), labels);
                    int numTableEntries = (readInt(currentOffset2 + 8) - readInt(currentOffset2 + 4)) + 1;
                    currentOffset = currentOffset2 + 12;
                    while (true) {
                        int i2 = numTableEntries;
                        numTableEntries--;
                        if (i2 > 0) {
                            createLabel(bytecodeOffset + readInt(currentOffset), labels);
                            currentOffset += 4;
                        }
                    }
                    break;
                case Opcodes.LOOKUPSWITCH /* 171 */:
                    currentOffset += 4 - (bytecodeOffset & 3);
                    createLabel(bytecodeOffset + readInt(currentOffset), labels);
                    int numSwitchCases = readInt(currentOffset + 4);
                    while (true) {
                        currentOffset += 8;
                        int i3 = numSwitchCases;
                        numSwitchCases--;
                        if (i3 > 0) {
                            createLabel(bytecodeOffset + readInt(currentOffset + 4), labels);
                        }
                    }
                    break;
                case Opcodes.INVOKEINTERFACE /* 185 */:
                case Opcodes.INVOKEDYNAMIC /* 186 */:
                    currentOffset += 5;
                    break;
                case 196:
                    switch (classBuffer[currentOffset + 1] & 255) {
                        case 21:
                        case 22:
                        case 23:
                        case 24:
                        case 25:
                        case 54:
                        case 55:
                        case 56:
                        case 57:
                        case 58:
                        case Opcodes.RET /* 169 */:
                            currentOffset += 4;
                            break;
                        case Opcodes.IINC /* 132 */:
                            currentOffset += 6;
                            break;
                        default:
                            throw new IllegalArgumentException();
                    }
                case Opcodes.MULTIANEWARRAY /* 197 */:
                    currentOffset += 4;
                    break;
                case 200:
                case 201:
                case 220:
                    createLabel(bytecodeOffset + readInt(currentOffset + 1), labels);
                    currentOffset += 5;
                    break;
                case 202:
                case 203:
                case 204:
                case 205:
                case 206:
                case WebdavStatus.SC_MULTI_STATUS /* 207 */:
                case 208:
                case 209:
                case 210:
                case 211:
                case Hessian2Constants.BC_INT_SHORT_ZERO /* 212 */:
                case 213:
                case 214:
                case 215:
                case 216:
                case 217:
                case 218:
                case 219:
                    createLabel(bytecodeOffset + readUnsignedShort(currentOffset + 1), labels);
                    currentOffset += 3;
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }
        int exceptionTableLength = readUnsignedShort(currentOffset);
        int currentOffset3 = currentOffset + 2;
        while (true) {
            int i4 = exceptionTableLength;
            exceptionTableLength--;
            if (i4 > 0) {
                Label start = createLabel(readUnsignedShort(currentOffset3), labels);
                Label end = createLabel(readUnsignedShort(currentOffset3 + 2), labels);
                Label handler = createLabel(readUnsignedShort(currentOffset3 + 4), labels);
                String catchType = readUTF8(this.cpInfoOffsets[readUnsignedShort(currentOffset3 + 6)], charBuffer);
                currentOffset3 += 8;
                methodVisitor.visitTryCatchBlock(start, end, handler, catchType);
            } else {
                int stackMapFrameOffset = 0;
                int stackMapTableEndOffset = 0;
                boolean compressedFrames = true;
                int localVariableTableOffset = 0;
                int localVariableTypeTableOffset = 0;
                int[] visibleTypeAnnotationOffsets = null;
                int[] invisibleTypeAnnotationOffsets = null;
                Attribute attributes = null;
                int attributesCount = readUnsignedShort(currentOffset3);
                int currentOffset4 = currentOffset3 + 2;
                while (true) {
                    int i5 = attributesCount;
                    attributesCount--;
                    if (i5 > 0) {
                        String attributeName = readUTF8(currentOffset4, charBuffer);
                        int attributeLength = readInt(currentOffset4 + 2);
                        int currentOffset5 = currentOffset4 + 6;
                        if ("LocalVariableTable".equals(attributeName)) {
                            if ((context.parsingOptions & 2) == 0) {
                                localVariableTableOffset = currentOffset5;
                                int localVariableTableLength = readUnsignedShort(currentOffset5);
                                int currentLocalVariableTableOffset = currentOffset5 + 2;
                                while (true) {
                                    int i6 = localVariableTableLength;
                                    localVariableTableLength--;
                                    if (i6 > 0) {
                                        int startPc = readUnsignedShort(currentLocalVariableTableOffset);
                                        createDebugLabel(startPc, labels);
                                        int length = readUnsignedShort(currentLocalVariableTableOffset + 2);
                                        createDebugLabel(startPc + length, labels);
                                        currentLocalVariableTableOffset += 10;
                                    }
                                }
                            }
                        } else if ("LocalVariableTypeTable".equals(attributeName)) {
                            localVariableTypeTableOffset = currentOffset5;
                        } else if ("LineNumberTable".equals(attributeName)) {
                            if ((context.parsingOptions & 2) == 0) {
                                int lineNumberTableLength = readUnsignedShort(currentOffset5);
                                int currentLineNumberTableOffset = currentOffset5 + 2;
                                while (true) {
                                    int i7 = lineNumberTableLength;
                                    lineNumberTableLength--;
                                    if (i7 > 0) {
                                        int startPc2 = readUnsignedShort(currentLineNumberTableOffset);
                                        int lineNumber = readUnsignedShort(currentLineNumberTableOffset + 2);
                                        currentLineNumberTableOffset += 4;
                                        createDebugLabel(startPc2, labels);
                                        labels[startPc2].addLineNumber(lineNumber);
                                    }
                                }
                            }
                        } else if ("RuntimeVisibleTypeAnnotations".equals(attributeName)) {
                            visibleTypeAnnotationOffsets = readTypeAnnotations(methodVisitor, context, currentOffset5, true);
                        } else if ("RuntimeInvisibleTypeAnnotations".equals(attributeName)) {
                            invisibleTypeAnnotationOffsets = readTypeAnnotations(methodVisitor, context, currentOffset5, false);
                        } else if ("StackMapTable".equals(attributeName)) {
                            if ((context.parsingOptions & 4) == 0) {
                                stackMapFrameOffset = currentOffset5 + 2;
                                stackMapTableEndOffset = currentOffset5 + attributeLength;
                            }
                        } else if ("StackMap".equals(attributeName)) {
                            if ((context.parsingOptions & 4) == 0) {
                                stackMapFrameOffset = currentOffset5 + 2;
                                stackMapTableEndOffset = currentOffset5 + attributeLength;
                                compressedFrames = false;
                            }
                        } else {
                            Attribute attribute = readAttribute(context.attributePrototypes, attributeName, currentOffset5, attributeLength, charBuffer, codeOffset, labels);
                            attribute.nextAttribute = attributes;
                            attributes = attribute;
                        }
                        currentOffset4 = currentOffset5 + attributeLength;
                    } else {
                        boolean expandFrames = (context.parsingOptions & 8) != 0;
                        if (stackMapFrameOffset != 0) {
                            context.currentFrameOffset = -1;
                            context.currentFrameType = 0;
                            context.currentFrameLocalCount = 0;
                            context.currentFrameLocalCountDelta = 0;
                            context.currentFrameLocalTypes = new Object[maxLocals];
                            context.currentFrameStackCount = 0;
                            context.currentFrameStackTypes = new Object[maxStack];
                            if (expandFrames) {
                                computeImplicitFrame(context);
                            }
                            for (int offset = stackMapFrameOffset; offset < stackMapTableEndOffset - 2; offset++) {
                                if (classBuffer[offset] == 8 && (potentialBytecodeOffset = readUnsignedShort(offset + 1)) >= 0 && potentialBytecodeOffset < codeLength && (classBuffer[currentOffset + potentialBytecodeOffset] & 255) == 187) {
                                    createLabel(potentialBytecodeOffset, labels);
                                }
                            }
                        }
                        if (expandFrames && (context.parsingOptions & 256) != 0) {
                            methodVisitor.visitFrame(-1, maxLocals, null, 0, null);
                        }
                        int currentVisibleTypeAnnotationIndex = 0;
                        int currentVisibleTypeAnnotationBytecodeOffset = getTypeAnnotationBytecodeOffset(visibleTypeAnnotationOffsets, 0);
                        int currentInvisibleTypeAnnotationIndex = 0;
                        int currentInvisibleTypeAnnotationBytecodeOffset = getTypeAnnotationBytecodeOffset(invisibleTypeAnnotationOffsets, 0);
                        boolean insertFrame = false;
                        int wideJumpOpcodeDelta = (context.parsingOptions & 256) == 0 ? 33 : 0;
                        int currentOffset6 = currentOffset;
                        while (currentOffset6 < bytecodeEndOffset) {
                            int currentBytecodeOffset = currentOffset6 - currentOffset;
                            readBytecodeInstructionOffset(currentBytecodeOffset);
                            Label currentLabel = labels[currentBytecodeOffset];
                            if (currentLabel != null) {
                                currentLabel.accept(methodVisitor, (context.parsingOptions & 2) == 0);
                            }
                            while (stackMapFrameOffset != 0 && (context.currentFrameOffset == currentBytecodeOffset || context.currentFrameOffset == -1)) {
                                if (context.currentFrameOffset != -1) {
                                    if (!compressedFrames || expandFrames) {
                                        methodVisitor.visitFrame(-1, context.currentFrameLocalCount, context.currentFrameLocalTypes, context.currentFrameStackCount, context.currentFrameStackTypes);
                                    } else {
                                        methodVisitor.visitFrame(context.currentFrameType, context.currentFrameLocalCountDelta, context.currentFrameLocalTypes, context.currentFrameStackCount, context.currentFrameStackTypes);
                                    }
                                    insertFrame = false;
                                }
                                if (stackMapFrameOffset < stackMapTableEndOffset) {
                                    stackMapFrameOffset = readStackMapFrame(stackMapFrameOffset, compressedFrames, expandFrames, context);
                                } else {
                                    stackMapFrameOffset = 0;
                                }
                            }
                            if (insertFrame) {
                                if ((context.parsingOptions & 8) != 0) {
                                    methodVisitor.visitFrame(256, 0, null, 0, null);
                                }
                                insertFrame = false;
                            }
                            int opcode = classBuffer[currentOffset6] & 255;
                            switch (opcode) {
                                case 0:
                                case 1:
                                case 2:
                                case 3:
                                case 4:
                                case 5:
                                case 6:
                                case 7:
                                case 8:
                                case 9:
                                case 10:
                                case 11:
                                case 12:
                                case 13:
                                case 14:
                                case 15:
                                case 46:
                                case 47:
                                case 48:
                                case 49:
                                case 50:
                                case 51:
                                case 52:
                                case 53:
                                case 79:
                                case 80:
                                case 81:
                                case 82:
                                case 83:
                                case 84:
                                case 85:
                                case 86:
                                case 87:
                                case 88:
                                case 89:
                                case 90:
                                case 91:
                                case 92:
                                case 93:
                                case 94:
                                case 95:
                                case 96:
                                case 97:
                                case 98:
                                case 99:
                                case 100:
                                case 101:
                                case 102:
                                case 103:
                                case 104:
                                case 105:
                                case 106:
                                case Opcodes.DMUL /* 107 */:
                                case 108:
                                case Opcodes.LDIV /* 109 */:
                                case Opcodes.FDIV /* 110 */:
                                case Opcodes.DDIV /* 111 */:
                                case 112:
                                case Opcodes.LREM /* 113 */:
                                case Opcodes.FREM /* 114 */:
                                case 115:
                                case 116:
                                case Opcodes.LNEG /* 117 */:
                                case Opcodes.FNEG /* 118 */:
                                case Opcodes.DNEG /* 119 */:
                                case 120:
                                case Opcodes.LSHL /* 121 */:
                                case 122:
                                case 123:
                                case 124:
                                case 125:
                                case 126:
                                case 127:
                                case 128:
                                case Opcodes.LOR /* 129 */:
                                case 130:
                                case Opcodes.LXOR /* 131 */:
                                case Opcodes.I2L /* 133 */:
                                case Opcodes.I2F /* 134 */:
                                case Opcodes.I2D /* 135 */:
                                case 136:
                                case Opcodes.L2F /* 137 */:
                                case Opcodes.L2D /* 138 */:
                                case Opcodes.F2I /* 139 */:
                                case Opcodes.F2L /* 140 */:
                                case Opcodes.F2D /* 141 */:
                                case Opcodes.D2I /* 142 */:
                                case Opcodes.D2L /* 143 */:
                                case 144:
                                case Opcodes.I2B /* 145 */:
                                case Opcodes.I2C /* 146 */:
                                case Opcodes.I2S /* 147 */:
                                case Opcodes.LCMP /* 148 */:
                                case Opcodes.FCMPL /* 149 */:
                                case 150:
                                case Opcodes.DCMPL /* 151 */:
                                case 152:
                                case Opcodes.IRETURN /* 172 */:
                                case Opcodes.LRETURN /* 173 */:
                                case Opcodes.FRETURN /* 174 */:
                                case Opcodes.DRETURN /* 175 */:
                                case 176:
                                case Opcodes.RETURN /* 177 */:
                                case Opcodes.ARRAYLENGTH /* 190 */:
                                case Opcodes.ATHROW /* 191 */:
                                case Opcodes.MONITORENTER /* 194 */:
                                case Opcodes.MONITOREXIT /* 195 */:
                                    methodVisitor.visitInsn(opcode);
                                    currentOffset6++;
                                    break;
                                case 16:
                                case Opcodes.NEWARRAY /* 188 */:
                                    methodVisitor.visitIntInsn(opcode, classBuffer[currentOffset6 + 1]);
                                    currentOffset6 += 2;
                                    break;
                                case 17:
                                    methodVisitor.visitIntInsn(opcode, readShort(currentOffset6 + 1));
                                    currentOffset6 += 3;
                                    break;
                                case 18:
                                    methodVisitor.visitLdcInsn(readConst(classBuffer[currentOffset6 + 1] & 255, charBuffer));
                                    currentOffset6 += 2;
                                    break;
                                case 19:
                                case 20:
                                    methodVisitor.visitLdcInsn(readConst(readUnsignedShort(currentOffset6 + 1), charBuffer));
                                    currentOffset6 += 3;
                                    break;
                                case 21:
                                case 22:
                                case 23:
                                case 24:
                                case 25:
                                case 54:
                                case 55:
                                case 56:
                                case 57:
                                case 58:
                                case Opcodes.RET /* 169 */:
                                    methodVisitor.visitVarInsn(opcode, classBuffer[currentOffset6 + 1] & 255);
                                    currentOffset6 += 2;
                                    break;
                                case 26:
                                case 27:
                                case 28:
                                case 29:
                                case 30:
                                case 31:
                                case 32:
                                case 33:
                                case 34:
                                case 35:
                                case 36:
                                case 37:
                                case 38:
                                case 39:
                                case 40:
                                case 41:
                                case 42:
                                case 43:
                                case 44:
                                case 45:
                                    int opcode2 = opcode - 26;
                                    methodVisitor.visitVarInsn(21 + (opcode2 >> 2), opcode2 & 3);
                                    currentOffset6++;
                                    break;
                                case 59:
                                case 60:
                                case 61:
                                case 62:
                                case 63:
                                case 64:
                                case 65:
                                case 66:
                                case 67:
                                case 68:
                                case 69:
                                case 70:
                                case 71:
                                case 72:
                                case 73:
                                case 74:
                                case 75:
                                case 76:
                                case 77:
                                case 78:
                                    int opcode3 = opcode - 59;
                                    methodVisitor.visitVarInsn(54 + (opcode3 >> 2), opcode3 & 3);
                                    currentOffset6++;
                                    break;
                                case Opcodes.IINC /* 132 */:
                                    methodVisitor.visitIincInsn(classBuffer[currentOffset6 + 1] & 255, classBuffer[currentOffset6 + 2]);
                                    currentOffset6 += 3;
                                    break;
                                case 153:
                                case 154:
                                case 155:
                                case 156:
                                case 157:
                                case 158:
                                case Opcodes.IF_ICMPEQ /* 159 */:
                                case 160:
                                case Opcodes.IF_ICMPLT /* 161 */:
                                case Opcodes.IF_ICMPGE /* 162 */:
                                case Opcodes.IF_ICMPGT /* 163 */:
                                case Opcodes.IF_ICMPLE /* 164 */:
                                case Opcodes.IF_ACMPEQ /* 165 */:
                                case Opcodes.IF_ACMPNE /* 166 */:
                                case Opcodes.GOTO /* 167 */:
                                case 168:
                                case Opcodes.IFNULL /* 198 */:
                                case Opcodes.IFNONNULL /* 199 */:
                                    methodVisitor.visitJumpInsn(opcode, labels[currentBytecodeOffset + readShort(currentOffset6 + 1)]);
                                    currentOffset6 += 3;
                                    break;
                                case Opcodes.TABLESWITCH /* 170 */:
                                    int currentOffset7 = currentOffset6 + (4 - (currentBytecodeOffset & 3));
                                    Label defaultLabel = labels[currentBytecodeOffset + readInt(currentOffset7)];
                                    int low = readInt(currentOffset7 + 4);
                                    int high = readInt(currentOffset7 + 8);
                                    currentOffset6 = currentOffset7 + 12;
                                    Label[] table = new Label[(high - low) + 1];
                                    for (int i8 = 0; i8 < table.length; i8++) {
                                        table[i8] = labels[currentBytecodeOffset + readInt(currentOffset6)];
                                        currentOffset6 += 4;
                                    }
                                    methodVisitor.visitTableSwitchInsn(low, high, defaultLabel, table);
                                    break;
                                case Opcodes.LOOKUPSWITCH /* 171 */:
                                    int currentOffset8 = currentOffset6 + (4 - (currentBytecodeOffset & 3));
                                    Label defaultLabel2 = labels[currentBytecodeOffset + readInt(currentOffset8)];
                                    int numPairs = readInt(currentOffset8 + 4);
                                    currentOffset6 = currentOffset8 + 8;
                                    int[] keys = new int[numPairs];
                                    Label[] values = new Label[numPairs];
                                    for (int i9 = 0; i9 < numPairs; i9++) {
                                        keys[i9] = readInt(currentOffset6);
                                        values[i9] = labels[currentBytecodeOffset + readInt(currentOffset6 + 4)];
                                        currentOffset6 += 8;
                                    }
                                    methodVisitor.visitLookupSwitchInsn(defaultLabel2, keys, values);
                                    break;
                                case Opcodes.GETSTATIC /* 178 */:
                                case Opcodes.PUTSTATIC /* 179 */:
                                case Opcodes.GETFIELD /* 180 */:
                                case Opcodes.PUTFIELD /* 181 */:
                                case Opcodes.INVOKEVIRTUAL /* 182 */:
                                case Opcodes.INVOKESPECIAL /* 183 */:
                                case 184:
                                case Opcodes.INVOKEINTERFACE /* 185 */:
                                    int cpInfoOffset = this.cpInfoOffsets[readUnsignedShort(currentOffset6 + 1)];
                                    int nameAndTypeCpInfoOffset = this.cpInfoOffsets[readUnsignedShort(cpInfoOffset + 2)];
                                    String owner = readClass(cpInfoOffset, charBuffer);
                                    String name = readUTF8(nameAndTypeCpInfoOffset, charBuffer);
                                    String descriptor = readUTF8(nameAndTypeCpInfoOffset + 2, charBuffer);
                                    if (opcode < 182) {
                                        methodVisitor.visitFieldInsn(opcode, owner, name, descriptor);
                                    } else {
                                        boolean isInterface = classBuffer[cpInfoOffset - 1] == 11;
                                        methodVisitor.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                                    }
                                    if (opcode == 185) {
                                        currentOffset6 += 5;
                                        break;
                                    } else {
                                        currentOffset6 += 3;
                                        break;
                                    }
                                case Opcodes.INVOKEDYNAMIC /* 186 */:
                                    int cpInfoOffset2 = this.cpInfoOffsets[readUnsignedShort(currentOffset6 + 1)];
                                    int nameAndTypeCpInfoOffset2 = this.cpInfoOffsets[readUnsignedShort(cpInfoOffset2 + 2)];
                                    String name2 = readUTF8(nameAndTypeCpInfoOffset2, charBuffer);
                                    String descriptor2 = readUTF8(nameAndTypeCpInfoOffset2 + 2, charBuffer);
                                    int bootstrapMethodOffset = this.bootstrapMethodOffsets[readUnsignedShort(cpInfoOffset2)];
                                    Handle handle = (Handle) readConst(readUnsignedShort(bootstrapMethodOffset), charBuffer);
                                    Object[] bootstrapMethodArguments = new Object[readUnsignedShort(bootstrapMethodOffset + 2)];
                                    int bootstrapMethodOffset2 = bootstrapMethodOffset + 4;
                                    for (int i10 = 0; i10 < bootstrapMethodArguments.length; i10++) {
                                        bootstrapMethodArguments[i10] = readConst(readUnsignedShort(bootstrapMethodOffset2), charBuffer);
                                        bootstrapMethodOffset2 += 2;
                                    }
                                    methodVisitor.visitInvokeDynamicInsn(name2, descriptor2, handle, bootstrapMethodArguments);
                                    currentOffset6 += 5;
                                    break;
                                case Opcodes.NEW /* 187 */:
                                case Opcodes.ANEWARRAY /* 189 */:
                                case Opcodes.CHECKCAST /* 192 */:
                                case Opcodes.INSTANCEOF /* 193 */:
                                    methodVisitor.visitTypeInsn(opcode, readClass(currentOffset6 + 1, charBuffer));
                                    currentOffset6 += 3;
                                    break;
                                case 196:
                                    int opcode4 = classBuffer[currentOffset6 + 1] & 255;
                                    if (opcode4 == 132) {
                                        methodVisitor.visitIincInsn(readUnsignedShort(currentOffset6 + 2), readShort(currentOffset6 + 4));
                                        currentOffset6 += 6;
                                        break;
                                    } else {
                                        methodVisitor.visitVarInsn(opcode4, readUnsignedShort(currentOffset6 + 2));
                                        currentOffset6 += 4;
                                        break;
                                    }
                                case Opcodes.MULTIANEWARRAY /* 197 */:
                                    methodVisitor.visitMultiANewArrayInsn(readClass(currentOffset6 + 1, charBuffer), classBuffer[currentOffset6 + 3] & 255);
                                    currentOffset6 += 4;
                                    break;
                                case 200:
                                case 201:
                                    methodVisitor.visitJumpInsn(opcode - wideJumpOpcodeDelta, labels[currentBytecodeOffset + readInt(currentOffset6 + 1)]);
                                    currentOffset6 += 5;
                                    break;
                                case 202:
                                case 203:
                                case 204:
                                case 205:
                                case 206:
                                case WebdavStatus.SC_MULTI_STATUS /* 207 */:
                                case 208:
                                case 209:
                                case 210:
                                case 211:
                                case Hessian2Constants.BC_INT_SHORT_ZERO /* 212 */:
                                case 213:
                                case 214:
                                case 215:
                                case 216:
                                case 217:
                                case 218:
                                case 219:
                                    if (opcode < 218) {
                                        i = opcode - 49;
                                    } else {
                                        i = opcode - 20;
                                    }
                                    int opcode5 = i;
                                    Label target = labels[currentBytecodeOffset + readUnsignedShort(currentOffset6 + 1)];
                                    if (opcode5 == 167 || opcode5 == 168) {
                                        methodVisitor.visitJumpInsn(opcode5 + 33, target);
                                    } else {
                                        int opcode6 = opcode5 < 167 ? ((opcode5 + 1) ^ 1) - 1 : opcode5 ^ 1;
                                        Label endif = createLabel(currentBytecodeOffset + 3, labels);
                                        methodVisitor.visitJumpInsn(opcode6, endif);
                                        methodVisitor.visitJumpInsn(200, target);
                                        insertFrame = true;
                                    }
                                    currentOffset6 += 3;
                                    break;
                                case 220:
                                    methodVisitor.visitJumpInsn(200, labels[currentBytecodeOffset + readInt(currentOffset6 + 1)]);
                                    insertFrame = true;
                                    currentOffset6 += 5;
                                    break;
                                default:
                                    throw new AssertionError();
                            }
                            while (visibleTypeAnnotationOffsets != null && currentVisibleTypeAnnotationIndex < visibleTypeAnnotationOffsets.length && currentVisibleTypeAnnotationBytecodeOffset <= currentBytecodeOffset) {
                                if (currentVisibleTypeAnnotationBytecodeOffset == currentBytecodeOffset) {
                                    int currentAnnotationOffset = readTypeAnnotationTarget(context, visibleTypeAnnotationOffsets[currentVisibleTypeAnnotationIndex]);
                                    String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
                                    readElementValues(methodVisitor.visitInsnAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, annotationDescriptor, true), currentAnnotationOffset + 2, true, charBuffer);
                                }
                                currentVisibleTypeAnnotationIndex++;
                                currentVisibleTypeAnnotationBytecodeOffset = getTypeAnnotationBytecodeOffset(visibleTypeAnnotationOffsets, currentVisibleTypeAnnotationIndex);
                            }
                            while (invisibleTypeAnnotationOffsets != null && currentInvisibleTypeAnnotationIndex < invisibleTypeAnnotationOffsets.length && currentInvisibleTypeAnnotationBytecodeOffset <= currentBytecodeOffset) {
                                if (currentInvisibleTypeAnnotationBytecodeOffset == currentBytecodeOffset) {
                                    int currentAnnotationOffset2 = readTypeAnnotationTarget(context, invisibleTypeAnnotationOffsets[currentInvisibleTypeAnnotationIndex]);
                                    String annotationDescriptor2 = readUTF8(currentAnnotationOffset2, charBuffer);
                                    readElementValues(methodVisitor.visitInsnAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, annotationDescriptor2, false), currentAnnotationOffset2 + 2, true, charBuffer);
                                }
                                currentInvisibleTypeAnnotationIndex++;
                                currentInvisibleTypeAnnotationBytecodeOffset = getTypeAnnotationBytecodeOffset(invisibleTypeAnnotationOffsets, currentInvisibleTypeAnnotationIndex);
                            }
                        }
                        if (labels[codeLength] != null) {
                            methodVisitor.visitLabel(labels[codeLength]);
                        }
                        if (localVariableTableOffset != 0 && (context.parsingOptions & 2) == 0) {
                            int[] typeTable = null;
                            if (localVariableTypeTableOffset != 0) {
                                typeTable = new int[readUnsignedShort(localVariableTypeTableOffset) * 3];
                                int currentOffset9 = localVariableTypeTableOffset + 2;
                                int typeTableIndex = typeTable.length;
                                while (typeTableIndex > 0) {
                                    int typeTableIndex2 = typeTableIndex - 1;
                                    typeTable[typeTableIndex2] = currentOffset9 + 6;
                                    int typeTableIndex3 = typeTableIndex2 - 1;
                                    typeTable[typeTableIndex3] = readUnsignedShort(currentOffset9 + 8);
                                    typeTableIndex = typeTableIndex3 - 1;
                                    typeTable[typeTableIndex] = readUnsignedShort(currentOffset9);
                                    currentOffset9 += 10;
                                }
                            }
                            int localVariableTableLength2 = readUnsignedShort(localVariableTableOffset);
                            int currentOffset10 = localVariableTableOffset + 2;
                            while (true) {
                                int i11 = localVariableTableLength2;
                                localVariableTableLength2--;
                                if (i11 > 0) {
                                    int startPc3 = readUnsignedShort(currentOffset10);
                                    int length2 = readUnsignedShort(currentOffset10 + 2);
                                    String name3 = readUTF8(currentOffset10 + 4, charBuffer);
                                    String descriptor3 = readUTF8(currentOffset10 + 6, charBuffer);
                                    int index = readUnsignedShort(currentOffset10 + 8);
                                    currentOffset10 += 10;
                                    String signature = null;
                                    if (typeTable != null) {
                                        int i12 = 0;
                                        while (true) {
                                            if (i12 >= typeTable.length) {
                                                break;
                                            }
                                            if (typeTable[i12] != startPc3 || typeTable[i12 + 1] != index) {
                                                i12 += 3;
                                            } else {
                                                signature = readUTF8(typeTable[i12 + 2], charBuffer);
                                            }
                                        }
                                    }
                                    methodVisitor.visitLocalVariable(name3, descriptor3, signature, labels[startPc3], labels[startPc3 + length2], index);
                                }
                            }
                        }
                        if (visibleTypeAnnotationOffsets != null) {
                            for (int typeAnnotationOffset : visibleTypeAnnotationOffsets) {
                                int targetType = readByte(typeAnnotationOffset);
                                if (targetType == 64 || targetType == 65) {
                                    int currentOffset11 = readTypeAnnotationTarget(context, typeAnnotationOffset);
                                    String annotationDescriptor3 = readUTF8(currentOffset11, charBuffer);
                                    readElementValues(methodVisitor.visitLocalVariableAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, context.currentLocalVariableAnnotationRangeStarts, context.currentLocalVariableAnnotationRangeEnds, context.currentLocalVariableAnnotationRangeIndices, annotationDescriptor3, true), currentOffset11 + 2, true, charBuffer);
                                }
                            }
                        }
                        if (invisibleTypeAnnotationOffsets != null) {
                            for (int typeAnnotationOffset2 : invisibleTypeAnnotationOffsets) {
                                int targetType2 = readByte(typeAnnotationOffset2);
                                if (targetType2 == 64 || targetType2 == 65) {
                                    int currentOffset12 = readTypeAnnotationTarget(context, typeAnnotationOffset2);
                                    String annotationDescriptor4 = readUTF8(currentOffset12, charBuffer);
                                    readElementValues(methodVisitor.visitLocalVariableAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, context.currentLocalVariableAnnotationRangeStarts, context.currentLocalVariableAnnotationRangeEnds, context.currentLocalVariableAnnotationRangeIndices, annotationDescriptor4, false), currentOffset12 + 2, true, charBuffer);
                                }
                            }
                        }
                        while (attributes != null) {
                            Attribute nextAttribute = attributes.nextAttribute;
                            attributes.nextAttribute = null;
                            methodVisitor.visitAttribute(attributes);
                            attributes = nextAttribute;
                        }
                        methodVisitor.visitMaxs(maxStack, maxLocals);
                        return;
                    }
                }
            }
        }
    }

    protected void readBytecodeInstructionOffset(final int bytecodeOffset) {
    }

    protected Label readLabel(final int bytecodeOffset, final Label[] labels) {
        if (bytecodeOffset >= labels.length) {
            return new Label();
        }
        if (labels[bytecodeOffset] == null) {
            labels[bytecodeOffset] = new Label();
        }
        return labels[bytecodeOffset];
    }

    private Label createLabel(final int bytecodeOffset, final Label[] labels) {
        Label label = readLabel(bytecodeOffset, labels);
        label.flags = (short) (label.flags & (-2));
        return label;
    }

    private void createDebugLabel(final int bytecodeOffset, final Label[] labels) {
        if (labels[bytecodeOffset] == null) {
            Label readLabel = readLabel(bytecodeOffset, labels);
            readLabel.flags = (short) (readLabel.flags | 1);
        }
    }

    private int[] readTypeAnnotations(final MethodVisitor methodVisitor, final Context context, final int runtimeTypeAnnotationsOffset, final boolean visible) {
        int currentOffset;
        int readElementValues;
        char[] charBuffer = context.charBuffer;
        int[] typeAnnotationsOffsets = new int[readUnsignedShort(runtimeTypeAnnotationsOffset)];
        int currentOffset2 = runtimeTypeAnnotationsOffset + 2;
        for (int i = 0; i < typeAnnotationsOffsets.length; i++) {
            typeAnnotationsOffsets[i] = currentOffset2;
            int targetType = readInt(currentOffset2);
            switch (targetType >>> 24) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                case 19:
                case 20:
                case 21:
                case 22:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57:
                case 58:
                case 59:
                case 60:
                case 61:
                case 62:
                case 63:
                default:
                    throw new IllegalArgumentException();
                case 16:
                case 17:
                case 18:
                case 23:
                case 66:
                case 67:
                case 68:
                case 69:
                case 70:
                    currentOffset = currentOffset2 + 3;
                    break;
                case 64:
                case 65:
                    int tableLength = readUnsignedShort(currentOffset2 + 1);
                    currentOffset = currentOffset2 + 3;
                    while (true) {
                        int i2 = tableLength;
                        tableLength--;
                        if (i2 <= 0) {
                            break;
                        } else {
                            int startPc = readUnsignedShort(currentOffset);
                            int length = readUnsignedShort(currentOffset + 2);
                            currentOffset += 6;
                            createLabel(startPc, context.currentMethodLabels);
                            createLabel(startPc + length, context.currentMethodLabels);
                        }
                    }
                case 71:
                case 72:
                case 73:
                case 74:
                case 75:
                    currentOffset = currentOffset2 + 4;
                    break;
            }
            int pathLength = readByte(currentOffset);
            if ((targetType >>> 24) == 66) {
                TypePath path = pathLength == 0 ? null : new TypePath(this.classFileBuffer, currentOffset);
                int currentOffset3 = currentOffset + 1 + (2 * pathLength);
                String annotationDescriptor = readUTF8(currentOffset3, charBuffer);
                readElementValues = readElementValues(methodVisitor.visitTryCatchAnnotation(targetType & (-256), path, annotationDescriptor, visible), currentOffset3 + 2, true, charBuffer);
            } else {
                readElementValues = readElementValues(null, currentOffset + 3 + (2 * pathLength), true, charBuffer);
            }
            currentOffset2 = readElementValues;
        }
        return typeAnnotationsOffsets;
    }

    private int getTypeAnnotationBytecodeOffset(final int[] typeAnnotationOffsets, final int typeAnnotationIndex) {
        if (typeAnnotationOffsets == null || typeAnnotationIndex >= typeAnnotationOffsets.length || readByte(typeAnnotationOffsets[typeAnnotationIndex]) < 67) {
            return -1;
        }
        return readUnsignedShort(typeAnnotationOffsets[typeAnnotationIndex] + 1);
    }

    private int readTypeAnnotationTarget(final Context context, final int typeAnnotationOffset) {
        int targetType;
        int currentOffset;
        int targetType2 = readInt(typeAnnotationOffset);
        switch (targetType2 >>> 24) {
            case 0:
            case 1:
            case 22:
                targetType = targetType2 & Opcodes.V_PREVIEW;
                currentOffset = typeAnnotationOffset + 2;
                break;
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            default:
                throw new IllegalArgumentException();
            case 16:
            case 17:
            case 18:
            case 23:
            case 66:
                targetType = targetType2 & (-256);
                currentOffset = typeAnnotationOffset + 3;
                break;
            case 19:
            case 20:
            case 21:
                targetType = targetType2 & (-16777216);
                currentOffset = typeAnnotationOffset + 1;
                break;
            case 64:
            case 65:
                targetType = targetType2 & (-16777216);
                int tableLength = readUnsignedShort(typeAnnotationOffset + 1);
                currentOffset = typeAnnotationOffset + 3;
                context.currentLocalVariableAnnotationRangeStarts = new Label[tableLength];
                context.currentLocalVariableAnnotationRangeEnds = new Label[tableLength];
                context.currentLocalVariableAnnotationRangeIndices = new int[tableLength];
                for (int i = 0; i < tableLength; i++) {
                    int startPc = readUnsignedShort(currentOffset);
                    int length = readUnsignedShort(currentOffset + 2);
                    int index = readUnsignedShort(currentOffset + 4);
                    currentOffset += 6;
                    context.currentLocalVariableAnnotationRangeStarts[i] = createLabel(startPc, context.currentMethodLabels);
                    context.currentLocalVariableAnnotationRangeEnds[i] = createLabel(startPc + length, context.currentMethodLabels);
                    context.currentLocalVariableAnnotationRangeIndices[i] = index;
                }
                break;
            case 67:
            case 68:
            case 69:
            case 70:
                targetType = targetType2 & (-16777216);
                currentOffset = typeAnnotationOffset + 3;
                break;
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
                targetType = targetType2 & (-16776961);
                currentOffset = typeAnnotationOffset + 4;
                break;
        }
        context.currentTypeAnnotationTarget = targetType;
        int pathLength = readByte(currentOffset);
        context.currentTypeAnnotationTargetPath = pathLength == 0 ? null : new TypePath(this.classFileBuffer, currentOffset);
        return currentOffset + 1 + (2 * pathLength);
    }

    private void readParameterAnnotations(final MethodVisitor methodVisitor, final Context context, final int runtimeParameterAnnotationsOffset, final boolean visible) {
        int currentOffset = runtimeParameterAnnotationsOffset + 1;
        int numParameters = this.classFileBuffer[runtimeParameterAnnotationsOffset] & 255;
        methodVisitor.visitAnnotableParameterCount(numParameters, visible);
        char[] charBuffer = context.charBuffer;
        for (int i = 0; i < numParameters; i++) {
            int numAnnotations = readUnsignedShort(currentOffset);
            currentOffset += 2;
            while (true) {
                int i2 = numAnnotations;
                numAnnotations--;
                if (i2 > 0) {
                    String annotationDescriptor = readUTF8(currentOffset, charBuffer);
                    currentOffset = readElementValues(methodVisitor.visitParameterAnnotation(i, annotationDescriptor, visible), currentOffset + 2, true, charBuffer);
                }
            }
        }
    }

    private int readElementValues(final AnnotationVisitor annotationVisitor, final int annotationOffset, final boolean named, final char[] charBuffer) {
        int numElementValuePairs = readUnsignedShort(annotationOffset);
        int currentOffset = annotationOffset + 2;
        if (!named) {
            while (true) {
                int i = numElementValuePairs;
                numElementValuePairs--;
                if (i <= 0) {
                    break;
                }
                currentOffset = readElementValue(annotationVisitor, currentOffset, null, charBuffer);
            }
        } else {
            while (true) {
                int i2 = numElementValuePairs;
                numElementValuePairs--;
                if (i2 <= 0) {
                    break;
                }
                String elementName = readUTF8(currentOffset, charBuffer);
                currentOffset = readElementValue(annotationVisitor, currentOffset + 2, elementName, charBuffer);
            }
        }
        if (annotationVisitor != null) {
            annotationVisitor.visitEnd();
        }
        return currentOffset;
    }

    private int readElementValue(final AnnotationVisitor annotationVisitor, final int elementValueOffset, final String elementName, final char[] charBuffer) {
        int currentOffset;
        Object obj;
        if (annotationVisitor != null) {
            int currentOffset2 = elementValueOffset + 1;
            switch (this.classFileBuffer[elementValueOffset] & 255) {
                case 64:
                    currentOffset = readElementValues(annotationVisitor.visitAnnotation(elementName, readUTF8(currentOffset2, charBuffer)), currentOffset2 + 2, true, charBuffer);
                    break;
                case 65:
                case 69:
                case 71:
                case 72:
                case 75:
                case 76:
                case 77:
                case 78:
                case 79:
                case 80:
                case 81:
                case 82:
                case 84:
                case 85:
                case 86:
                case 87:
                case 88:
                case 89:
                case 92:
                case 93:
                case 94:
                case 95:
                case 96:
                case 97:
                case 98:
                case 100:
                case 102:
                case 103:
                case 104:
                case 105:
                case 106:
                case Opcodes.DMUL /* 107 */:
                case 108:
                case Opcodes.LDIV /* 109 */:
                case Opcodes.FDIV /* 110 */:
                case Opcodes.DDIV /* 111 */:
                case 112:
                case Opcodes.LREM /* 113 */:
                case Opcodes.FREM /* 114 */:
                default:
                    throw new IllegalArgumentException();
                case 66:
                    annotationVisitor.visit(elementName, Byte.valueOf((byte) readInt(this.cpInfoOffsets[readUnsignedShort(currentOffset2)])));
                    currentOffset = currentOffset2 + 2;
                    break;
                case 67:
                    annotationVisitor.visit(elementName, Character.valueOf((char) readInt(this.cpInfoOffsets[readUnsignedShort(currentOffset2)])));
                    currentOffset = currentOffset2 + 2;
                    break;
                case 68:
                case 70:
                case 73:
                case 74:
                    annotationVisitor.visit(elementName, readConst(readUnsignedShort(currentOffset2), charBuffer));
                    currentOffset = currentOffset2 + 2;
                    break;
                case 83:
                    annotationVisitor.visit(elementName, Short.valueOf((short) readInt(this.cpInfoOffsets[readUnsignedShort(currentOffset2)])));
                    currentOffset = currentOffset2 + 2;
                    break;
                case 90:
                    if (readInt(this.cpInfoOffsets[readUnsignedShort(currentOffset2)]) == 0) {
                        obj = Boolean.FALSE;
                    } else {
                        obj = Boolean.TRUE;
                    }
                    annotationVisitor.visit(elementName, obj);
                    currentOffset = currentOffset2 + 2;
                    break;
                case 91:
                    int numValues = readUnsignedShort(currentOffset2);
                    currentOffset = currentOffset2 + 2;
                    if (numValues == 0) {
                        return readElementValues(annotationVisitor.visitArray(elementName), currentOffset - 2, false, charBuffer);
                    }
                    switch (this.classFileBuffer[currentOffset] & 255) {
                        case 66:
                            byte[] byteValues = new byte[numValues];
                            for (int i = 0; i < numValues; i++) {
                                byteValues[i] = (byte) readInt(this.cpInfoOffsets[readUnsignedShort(currentOffset + 1)]);
                                currentOffset += 3;
                            }
                            annotationVisitor.visit(elementName, byteValues);
                            break;
                        case 67:
                            char[] charValues = new char[numValues];
                            for (int i2 = 0; i2 < numValues; i2++) {
                                charValues[i2] = (char) readInt(this.cpInfoOffsets[readUnsignedShort(currentOffset + 1)]);
                                currentOffset += 3;
                            }
                            annotationVisitor.visit(elementName, charValues);
                            break;
                        case 68:
                            double[] doubleValues = new double[numValues];
                            for (int i3 = 0; i3 < numValues; i3++) {
                                doubleValues[i3] = Double.longBitsToDouble(readLong(this.cpInfoOffsets[readUnsignedShort(currentOffset + 1)]));
                                currentOffset += 3;
                            }
                            annotationVisitor.visit(elementName, doubleValues);
                            break;
                        case 69:
                        case 71:
                        case 72:
                        case 75:
                        case 76:
                        case 77:
                        case 78:
                        case 79:
                        case 80:
                        case 81:
                        case 82:
                        case 84:
                        case 85:
                        case 86:
                        case 87:
                        case 88:
                        case 89:
                        default:
                            currentOffset = readElementValues(annotationVisitor.visitArray(elementName), currentOffset - 2, false, charBuffer);
                            break;
                        case 70:
                            float[] floatValues = new float[numValues];
                            for (int i4 = 0; i4 < numValues; i4++) {
                                floatValues[i4] = Float.intBitsToFloat(readInt(this.cpInfoOffsets[readUnsignedShort(currentOffset + 1)]));
                                currentOffset += 3;
                            }
                            annotationVisitor.visit(elementName, floatValues);
                            break;
                        case 73:
                            int[] intValues = new int[numValues];
                            for (int i5 = 0; i5 < numValues; i5++) {
                                intValues[i5] = readInt(this.cpInfoOffsets[readUnsignedShort(currentOffset + 1)]);
                                currentOffset += 3;
                            }
                            annotationVisitor.visit(elementName, intValues);
                            break;
                        case 74:
                            long[] longValues = new long[numValues];
                            for (int i6 = 0; i6 < numValues; i6++) {
                                longValues[i6] = readLong(this.cpInfoOffsets[readUnsignedShort(currentOffset + 1)]);
                                currentOffset += 3;
                            }
                            annotationVisitor.visit(elementName, longValues);
                            break;
                        case 83:
                            short[] shortValues = new short[numValues];
                            for (int i7 = 0; i7 < numValues; i7++) {
                                shortValues[i7] = (short) readInt(this.cpInfoOffsets[readUnsignedShort(currentOffset + 1)]);
                                currentOffset += 3;
                            }
                            annotationVisitor.visit(elementName, shortValues);
                            break;
                        case 90:
                            boolean[] booleanValues = new boolean[numValues];
                            for (int i8 = 0; i8 < numValues; i8++) {
                                booleanValues[i8] = readInt(this.cpInfoOffsets[readUnsignedShort(currentOffset + 1)]) != 0;
                                currentOffset += 3;
                            }
                            annotationVisitor.visit(elementName, booleanValues);
                            break;
                    }
                case 99:
                    annotationVisitor.visit(elementName, Type.getType(readUTF8(currentOffset2, charBuffer)));
                    currentOffset = currentOffset2 + 2;
                    break;
                case 101:
                    annotationVisitor.visitEnum(elementName, readUTF8(currentOffset2, charBuffer), readUTF8(currentOffset2 + 2, charBuffer));
                    currentOffset = currentOffset2 + 4;
                    break;
                case 115:
                    annotationVisitor.visit(elementName, readUTF8(currentOffset2, charBuffer));
                    currentOffset = currentOffset2 + 2;
                    break;
            }
            return currentOffset;
        }
        switch (this.classFileBuffer[elementValueOffset] & 255) {
            case 64:
                return readElementValues(null, elementValueOffset + 3, true, charBuffer);
            case 91:
                return readElementValues(null, elementValueOffset + 1, false, charBuffer);
            case 101:
                return elementValueOffset + 5;
            default:
                return elementValueOffset + 3;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:41:0x016d, code lost:            r7.currentFrameLocalCount = r10;     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x0173, code lost:            return;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void computeImplicitFrame(final org.springframework.asm.Context r7) {
        /*
            Method dump skipped, instructions count: 375
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.asm.ClassReader.computeImplicitFrame(org.springframework.asm.Context):void");
    }

    private int readStackMapFrame(final int stackMapFrameOffset, final boolean compressed, final boolean expand, final Context context) {
        int frameType;
        int offsetDelta;
        int currentOffset = stackMapFrameOffset;
        char[] charBuffer = context.charBuffer;
        Label[] labels = context.currentMethodLabels;
        if (compressed) {
            currentOffset++;
            frameType = this.classFileBuffer[currentOffset] & 255;
        } else {
            frameType = 255;
            context.currentFrameOffset = -1;
        }
        context.currentFrameLocalCountDelta = 0;
        if (frameType < 64) {
            offsetDelta = frameType;
            context.currentFrameType = 3;
            context.currentFrameStackCount = 0;
        } else if (frameType < 128) {
            offsetDelta = frameType - 64;
            currentOffset = readVerificationTypeInfo(currentOffset, context.currentFrameStackTypes, 0, charBuffer, labels);
            context.currentFrameType = 4;
            context.currentFrameStackCount = 1;
        } else if (frameType >= 247) {
            offsetDelta = readUnsignedShort(currentOffset);
            currentOffset += 2;
            if (frameType == 247) {
                currentOffset = readVerificationTypeInfo(currentOffset, context.currentFrameStackTypes, 0, charBuffer, labels);
                context.currentFrameType = 4;
                context.currentFrameStackCount = 1;
            } else if (frameType >= 248 && frameType < 251) {
                context.currentFrameType = 2;
                context.currentFrameLocalCountDelta = 251 - frameType;
                context.currentFrameLocalCount -= context.currentFrameLocalCountDelta;
                context.currentFrameStackCount = 0;
            } else if (frameType == 251) {
                context.currentFrameType = 3;
                context.currentFrameStackCount = 0;
            } else if (frameType < 255) {
                int local = expand ? context.currentFrameLocalCount : 0;
                for (int k = frameType - 251; k > 0; k--) {
                    int i = local;
                    local++;
                    currentOffset = readVerificationTypeInfo(currentOffset, context.currentFrameLocalTypes, i, charBuffer, labels);
                }
                context.currentFrameType = 1;
                context.currentFrameLocalCountDelta = frameType - 251;
                context.currentFrameLocalCount += context.currentFrameLocalCountDelta;
                context.currentFrameStackCount = 0;
            } else {
                int numberOfLocals = readUnsignedShort(currentOffset);
                int currentOffset2 = currentOffset + 2;
                context.currentFrameType = 0;
                context.currentFrameLocalCountDelta = numberOfLocals;
                context.currentFrameLocalCount = numberOfLocals;
                for (int local2 = 0; local2 < numberOfLocals; local2++) {
                    currentOffset2 = readVerificationTypeInfo(currentOffset2, context.currentFrameLocalTypes, local2, charBuffer, labels);
                }
                int numberOfStackItems = readUnsignedShort(currentOffset2);
                currentOffset = currentOffset2 + 2;
                context.currentFrameStackCount = numberOfStackItems;
                for (int stack = 0; stack < numberOfStackItems; stack++) {
                    currentOffset = readVerificationTypeInfo(currentOffset, context.currentFrameStackTypes, stack, charBuffer, labels);
                }
            }
        } else {
            throw new IllegalArgumentException();
        }
        context.currentFrameOffset += offsetDelta + 1;
        createLabel(context.currentFrameOffset, labels);
        return currentOffset;
    }

    private int readVerificationTypeInfo(final int verificationTypeInfoOffset, final Object[] frame, final int index, final char[] charBuffer, final Label[] labels) {
        int currentOffset = verificationTypeInfoOffset + 1;
        int tag = this.classFileBuffer[verificationTypeInfoOffset] & Const.MAX_ARRAY_DIMENSIONS;
        switch (tag) {
            case 0:
                frame[index] = Opcodes.TOP;
                break;
            case 1:
                frame[index] = Opcodes.INTEGER;
                break;
            case 2:
                frame[index] = Opcodes.FLOAT;
                break;
            case 3:
                frame[index] = Opcodes.DOUBLE;
                break;
            case 4:
                frame[index] = Opcodes.LONG;
                break;
            case 5:
                frame[index] = Opcodes.NULL;
                break;
            case 6:
                frame[index] = Opcodes.UNINITIALIZED_THIS;
                break;
            case 7:
                frame[index] = readClass(currentOffset, charBuffer);
                currentOffset += 2;
                break;
            case 8:
                frame[index] = createLabel(readUnsignedShort(currentOffset), labels);
                currentOffset += 2;
                break;
            default:
                throw new IllegalArgumentException();
        }
        return currentOffset;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int getFirstAttributeOffset() {
        int currentOffset = this.header + 8 + (readUnsignedShort(this.header + 6) * 2);
        int fieldsCount = readUnsignedShort(currentOffset);
        int currentOffset2 = currentOffset + 2;
        while (true) {
            int i = fieldsCount;
            fieldsCount--;
            if (i <= 0) {
                break;
            }
            int attributesCount = readUnsignedShort(currentOffset2 + 6);
            currentOffset2 += 8;
            while (true) {
                int i2 = attributesCount;
                attributesCount--;
                if (i2 > 0) {
                    currentOffset2 += 6 + readInt(currentOffset2 + 2);
                }
            }
        }
        int methodsCount = readUnsignedShort(currentOffset2);
        int currentOffset3 = currentOffset2 + 2;
        while (true) {
            int i3 = methodsCount;
            methodsCount--;
            if (i3 > 0) {
                int attributesCount2 = readUnsignedShort(currentOffset3 + 6);
                currentOffset3 += 8;
                while (true) {
                    int i4 = attributesCount2;
                    attributesCount2--;
                    if (i4 > 0) {
                        currentOffset3 += 6 + readInt(currentOffset3 + 2);
                    }
                }
            } else {
                return currentOffset3 + 2;
            }
        }
    }

    private int[] readBootstrapMethodsAttribute(final int maxStringLength) {
        char[] charBuffer = new char[maxStringLength];
        int currentAttributeOffset = getFirstAttributeOffset();
        for (int i = readUnsignedShort(currentAttributeOffset - 2); i > 0; i--) {
            String attributeName = readUTF8(currentAttributeOffset, charBuffer);
            int attributeLength = readInt(currentAttributeOffset + 2);
            int currentAttributeOffset2 = currentAttributeOffset + 6;
            if ("BootstrapMethods".equals(attributeName)) {
                int[] result = new int[readUnsignedShort(currentAttributeOffset2)];
                int currentBootstrapMethodOffset = currentAttributeOffset2 + 2;
                for (int j = 0; j < result.length; j++) {
                    result[j] = currentBootstrapMethodOffset;
                    currentBootstrapMethodOffset += 4 + (readUnsignedShort(currentBootstrapMethodOffset + 2) * 2);
                }
                return result;
            }
            currentAttributeOffset = currentAttributeOffset2 + attributeLength;
        }
        throw new IllegalArgumentException();
    }

    private Attribute readAttribute(final Attribute[] attributePrototypes, final String type, final int offset, final int length, final char[] charBuffer, final int codeAttributeOffset, final Label[] labels) {
        for (Attribute attributePrototype : attributePrototypes) {
            if (attributePrototype.type.equals(type)) {
                return attributePrototype.read(this, offset, length, charBuffer, codeAttributeOffset, labels);
            }
        }
        return new Attribute(type).read(this, offset, length, null, -1, null);
    }

    public int getItemCount() {
        return this.cpInfoOffsets.length;
    }

    public int getItem(final int constantPoolEntryIndex) {
        return this.cpInfoOffsets[constantPoolEntryIndex];
    }

    public int getMaxStringLength() {
        return this.maxStringLength;
    }

    public int readByte(final int offset) {
        return this.classFileBuffer[offset] & 255;
    }

    public int readUnsignedShort(final int offset) {
        byte[] classBuffer = this.classFileBuffer;
        return ((classBuffer[offset] & 255) << 8) | (classBuffer[offset + 1] & 255);
    }

    public short readShort(final int offset) {
        byte[] classBuffer = this.classFileBuffer;
        return (short) (((classBuffer[offset] & 255) << 8) | (classBuffer[offset + 1] & 255));
    }

    public int readInt(final int offset) {
        byte[] classBuffer = this.classFileBuffer;
        return ((classBuffer[offset] & 255) << 24) | ((classBuffer[offset + 1] & 255) << 16) | ((classBuffer[offset + 2] & 255) << 8) | (classBuffer[offset + 3] & 255);
    }

    public long readLong(final int offset) {
        long l1 = readInt(offset);
        long l0 = readInt(offset + 4) & 4294967295L;
        return (l1 << 32) | l0;
    }

    public String readUTF8(final int offset, final char[] charBuffer) {
        int constantPoolEntryIndex = readUnsignedShort(offset);
        if (offset == 0 || constantPoolEntryIndex == 0) {
            return null;
        }
        return readUtf(constantPoolEntryIndex, charBuffer);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final String readUtf(final int constantPoolEntryIndex, final char[] charBuffer) {
        String value = this.constantUtf8Values[constantPoolEntryIndex];
        if (value != null) {
            return value;
        }
        int cpInfoOffset = this.cpInfoOffsets[constantPoolEntryIndex];
        String[] strArr = this.constantUtf8Values;
        String readUtf = readUtf(cpInfoOffset + 2, readUnsignedShort(cpInfoOffset), charBuffer);
        strArr[constantPoolEntryIndex] = readUtf;
        return readUtf;
    }

    private String readUtf(final int utfOffset, final int utfLength, final char[] charBuffer) {
        int currentOffset = utfOffset;
        int endOffset = currentOffset + utfLength;
        int strLength = 0;
        byte[] classBuffer = this.classFileBuffer;
        while (currentOffset < endOffset) {
            int i = currentOffset;
            currentOffset++;
            byte b = classBuffer[i];
            if ((b & 128) == 0) {
                int i2 = strLength;
                strLength++;
                charBuffer[i2] = (char) (b & Byte.MAX_VALUE);
            } else if ((b & 224) == 192) {
                int i3 = strLength;
                strLength++;
                currentOffset++;
                charBuffer[i3] = (char) (((b & 31) << 6) + (classBuffer[currentOffset] & 63));
            } else {
                int i4 = strLength;
                strLength++;
                int currentOffset2 = currentOffset + 1;
                int i5 = ((b & 15) << 12) + ((classBuffer[currentOffset] & 63) << 6);
                currentOffset = currentOffset2 + 1;
                charBuffer[i4] = (char) (i5 + (classBuffer[currentOffset2] & 63));
            }
        }
        return new String(charBuffer, 0, strLength);
    }

    private String readStringish(final int offset, final char[] charBuffer) {
        return readUTF8(this.cpInfoOffsets[readUnsignedShort(offset)], charBuffer);
    }

    public String readClass(final int offset, final char[] charBuffer) {
        return readStringish(offset, charBuffer);
    }

    public String readModule(final int offset, final char[] charBuffer) {
        return readStringish(offset, charBuffer);
    }

    public String readPackage(final int offset, final char[] charBuffer) {
        return readStringish(offset, charBuffer);
    }

    private ConstantDynamic readConstantDynamic(final int constantPoolEntryIndex, final char[] charBuffer) {
        ConstantDynamic constantDynamic = this.constantDynamicValues[constantPoolEntryIndex];
        if (constantDynamic != null) {
            return constantDynamic;
        }
        int cpInfoOffset = this.cpInfoOffsets[constantPoolEntryIndex];
        int nameAndTypeCpInfoOffset = this.cpInfoOffsets[readUnsignedShort(cpInfoOffset + 2)];
        String name = readUTF8(nameAndTypeCpInfoOffset, charBuffer);
        String descriptor = readUTF8(nameAndTypeCpInfoOffset + 2, charBuffer);
        int bootstrapMethodOffset = this.bootstrapMethodOffsets[readUnsignedShort(cpInfoOffset)];
        Handle handle = (Handle) readConst(readUnsignedShort(bootstrapMethodOffset), charBuffer);
        Object[] bootstrapMethodArguments = new Object[readUnsignedShort(bootstrapMethodOffset + 2)];
        int bootstrapMethodOffset2 = bootstrapMethodOffset + 4;
        for (int i = 0; i < bootstrapMethodArguments.length; i++) {
            bootstrapMethodArguments[i] = readConst(readUnsignedShort(bootstrapMethodOffset2), charBuffer);
            bootstrapMethodOffset2 += 2;
        }
        ConstantDynamic[] constantDynamicArr = this.constantDynamicValues;
        ConstantDynamic constantDynamic2 = new ConstantDynamic(name, descriptor, handle, bootstrapMethodArguments);
        constantDynamicArr[constantPoolEntryIndex] = constantDynamic2;
        return constantDynamic2;
    }

    public Object readConst(final int constantPoolEntryIndex, final char[] charBuffer) {
        int cpInfoOffset = this.cpInfoOffsets[constantPoolEntryIndex];
        switch (this.classFileBuffer[cpInfoOffset - 1]) {
            case 3:
                return Integer.valueOf(readInt(cpInfoOffset));
            case 4:
                return Float.valueOf(Float.intBitsToFloat(readInt(cpInfoOffset)));
            case 5:
                return Long.valueOf(readLong(cpInfoOffset));
            case 6:
                return Double.valueOf(Double.longBitsToDouble(readLong(cpInfoOffset)));
            case 7:
                return Type.getObjectType(readUTF8(cpInfoOffset, charBuffer));
            case 8:
                return readUTF8(cpInfoOffset, charBuffer);
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            default:
                throw new IllegalArgumentException();
            case 15:
                int referenceKind = readByte(cpInfoOffset);
                int referenceCpInfoOffset = this.cpInfoOffsets[readUnsignedShort(cpInfoOffset + 1)];
                int nameAndTypeCpInfoOffset = this.cpInfoOffsets[readUnsignedShort(referenceCpInfoOffset + 2)];
                String owner = readClass(referenceCpInfoOffset, charBuffer);
                String name = readUTF8(nameAndTypeCpInfoOffset, charBuffer);
                String descriptor = readUTF8(nameAndTypeCpInfoOffset + 2, charBuffer);
                boolean isInterface = this.classFileBuffer[referenceCpInfoOffset - 1] == 11;
                return new Handle(referenceKind, owner, name, descriptor, isInterface);
            case 16:
                return Type.getMethodType(readUTF8(cpInfoOffset, charBuffer));
            case 17:
                return readConstantDynamic(constantPoolEntryIndex, charBuffer);
        }
    }
}
