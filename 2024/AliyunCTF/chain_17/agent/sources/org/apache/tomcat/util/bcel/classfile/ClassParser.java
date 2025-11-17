package org.apache.tomcat.util.bcel.classfile;

import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/bcel/classfile/ClassParser.class */
public final class ClassParser {
    private static final int BUFSIZE = 8192;
    private final DataInput dataInputStream;
    private String className;
    private String superclassName;
    private int accessFlags;
    private String[] interfaceNames;
    private ConstantPool constantPool;
    private Annotations runtimeVisibleAnnotations;
    private List<Annotations> runtimeVisibleFieldOrMethodAnnotations;
    private static final String[] INTERFACES_EMPTY_ARRAY = new String[0];

    public ClassParser(InputStream inputStream) {
        this.dataInputStream = new DataInputStream(new BufferedInputStream(inputStream, 8192));
    }

    public JavaClass parse() throws IOException, ClassFormatException {
        readID();
        readVersion();
        readConstantPool();
        readClassInfo();
        readInterfaces();
        readFields();
        readMethods();
        readAttributes(false);
        return new JavaClass(this.className, this.superclassName, this.accessFlags, this.constantPool, this.interfaceNames, this.runtimeVisibleAnnotations, this.runtimeVisibleFieldOrMethodAnnotations);
    }

    private void readAttributes(boolean fieldOrMethod) throws IOException, ClassFormatException {
        int attributesCount = this.dataInputStream.readUnsignedShort();
        for (int i = 0; i < attributesCount; i++) {
            int name_index = this.dataInputStream.readUnsignedShort();
            ConstantUtf8 c = (ConstantUtf8) this.constantPool.getConstant(name_index, (byte) 1);
            String name = c.getBytes();
            int length = this.dataInputStream.readInt();
            if (name.equals("RuntimeVisibleAnnotations")) {
                if (fieldOrMethod) {
                    Annotations fieldOrMethodAnnotations = new Annotations(this.dataInputStream, this.constantPool);
                    if (this.runtimeVisibleFieldOrMethodAnnotations == null) {
                        this.runtimeVisibleFieldOrMethodAnnotations = new ArrayList();
                    }
                    this.runtimeVisibleFieldOrMethodAnnotations.add(fieldOrMethodAnnotations);
                } else {
                    if (this.runtimeVisibleAnnotations != null) {
                        throw new ClassFormatException("RuntimeVisibleAnnotations attribute is not allowed more than once in a class file");
                    }
                    this.runtimeVisibleAnnotations = new Annotations(this.dataInputStream, this.constantPool);
                }
            } else {
                Utility.skipFully(this.dataInputStream, length);
            }
        }
    }

    private void readClassInfo() throws IOException, ClassFormatException {
        this.accessFlags = this.dataInputStream.readUnsignedShort();
        if ((this.accessFlags & 512) != 0) {
            this.accessFlags |= 1024;
        }
        if ((this.accessFlags & 1024) != 0 && (this.accessFlags & 16) != 0) {
            throw new ClassFormatException("Class can't be both final and abstract");
        }
        int classNameIndex = this.dataInputStream.readUnsignedShort();
        this.className = Utility.getClassName(this.constantPool, classNameIndex);
        int superclass_name_index = this.dataInputStream.readUnsignedShort();
        if (superclass_name_index > 0) {
            this.superclassName = Utility.getClassName(this.constantPool, superclass_name_index);
        } else {
            this.superclassName = "java.lang.Object";
        }
    }

    private void readConstantPool() throws IOException, ClassFormatException {
        this.constantPool = new ConstantPool(this.dataInputStream);
    }

    private void readFields() throws IOException, ClassFormatException {
        int fieldsCount = this.dataInputStream.readUnsignedShort();
        for (int i = 0; i < fieldsCount; i++) {
            Utility.skipFully(this.dataInputStream, 6);
            readAttributes(true);
        }
    }

    private void readID() throws IOException, ClassFormatException {
        if (this.dataInputStream.readInt() != -889275714) {
            throw new ClassFormatException("It is not a Java .class file");
        }
    }

    private void readInterfaces() throws IOException, ClassFormatException {
        int interfacesCount = this.dataInputStream.readUnsignedShort();
        if (interfacesCount > 0) {
            this.interfaceNames = new String[interfacesCount];
            for (int i = 0; i < interfacesCount; i++) {
                int index = this.dataInputStream.readUnsignedShort();
                this.interfaceNames[i] = Utility.getClassName(this.constantPool, index);
            }
            return;
        }
        this.interfaceNames = INTERFACES_EMPTY_ARRAY;
    }

    private void readMethods() throws IOException, ClassFormatException {
        int methodsCount = this.dataInputStream.readUnsignedShort();
        for (int i = 0; i < methodsCount; i++) {
            Utility.skipFully(this.dataInputStream, 6);
            readAttributes(true);
        }
    }

    private void readVersion() throws IOException, ClassFormatException {
        Utility.skipFully(this.dataInputStream, 4);
    }
}
