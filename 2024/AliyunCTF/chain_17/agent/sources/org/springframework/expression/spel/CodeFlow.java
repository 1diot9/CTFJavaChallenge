package org.springframework.expression.spel;

import ch.qos.logback.core.CoreConstants;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import org.springframework.asm.ClassWriter;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.cglib.core.Constants;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/CodeFlow.class */
public class CodeFlow implements Opcodes {
    private final String className;
    private final ClassWriter classWriter;

    @Nullable
    private List<FieldAdder> fieldAdders;

    @Nullable
    private List<ClinitAdder> clinitAdders;
    private int nextFieldId = 1;
    private int nextFreeVariableId = 1;
    private final Deque<List<String>> compilationScopes = new ArrayDeque();

    @FunctionalInterface
    /* loaded from: agent.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/CodeFlow$ClinitAdder.class */
    public interface ClinitAdder {
        void generateCode(MethodVisitor mv, CodeFlow codeflow);
    }

    @FunctionalInterface
    /* loaded from: agent.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/CodeFlow$FieldAdder.class */
    public interface FieldAdder {
        void generateField(ClassWriter cw, CodeFlow codeflow);
    }

    public CodeFlow(String className, ClassWriter classWriter) {
        this.className = className;
        this.classWriter = classWriter;
        this.compilationScopes.add(new ArrayList());
    }

    public void loadTarget(MethodVisitor mv) {
        mv.visitVarInsn(25, 1);
    }

    public void loadEvaluationContext(MethodVisitor mv) {
        mv.visitVarInsn(25, 2);
    }

    public void pushDescriptor(@Nullable String descriptor) {
        if (descriptor != null) {
            this.compilationScopes.element().add(descriptor);
        }
    }

    public void enterCompilationScope() {
        this.compilationScopes.push(new ArrayList());
    }

    public void exitCompilationScope() {
        this.compilationScopes.pop();
    }

    @Nullable
    public String lastDescriptor() {
        return (String) CollectionUtils.lastElement(this.compilationScopes.peek());
    }

    public void unboxBooleanIfNecessary(MethodVisitor mv) {
        if ("Ljava/lang/Boolean".equals(lastDescriptor())) {
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
        }
    }

    public void finish() {
        if (this.fieldAdders != null) {
            for (FieldAdder fieldAdder : this.fieldAdders) {
                fieldAdder.generateField(this.classWriter, this);
            }
        }
        if (this.clinitAdders != null) {
            MethodVisitor mv = this.classWriter.visitMethod(9, Constants.STATIC_NAME, "()V", null, null);
            mv.visitCode();
            this.nextFreeVariableId = 0;
            for (ClinitAdder clinitAdder : this.clinitAdders) {
                clinitAdder.generateCode(mv, this);
            }
            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
    }

    public void registerNewField(FieldAdder fieldAdder) {
        if (this.fieldAdders == null) {
            this.fieldAdders = new ArrayList();
        }
        this.fieldAdders.add(fieldAdder);
    }

    public void registerNewClinit(ClinitAdder clinitAdder) {
        if (this.clinitAdders == null) {
            this.clinitAdders = new ArrayList();
        }
        this.clinitAdders.add(clinitAdder);
    }

    public int nextFieldId() {
        int i = this.nextFieldId;
        this.nextFieldId = i + 1;
        return i;
    }

    public int nextFreeVariableId() {
        int i = this.nextFreeVariableId;
        this.nextFreeVariableId = i + 1;
        return i;
    }

    public String getClassName() {
        return this.className;
    }

    public static void insertUnboxInsns(MethodVisitor mv, char ch2, @Nullable String stackDescriptor) {
        if (stackDescriptor == null) {
            return;
        }
        switch (ch2) {
            case 'B':
                if (!stackDescriptor.equals("Ljava/lang/Byte")) {
                    mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Byte");
                }
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B", false);
                return;
            case 'C':
                if (!stackDescriptor.equals("Ljava/lang/Character")) {
                    mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Character");
                }
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C", false);
                return;
            case 'D':
                if (!stackDescriptor.equals("Ljava/lang/Double")) {
                    mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Double");
                }
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
                return;
            case 'E':
            case 'G':
            case 'H':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            default:
                throw new IllegalArgumentException("Unboxing should not be attempted for descriptor '" + ch2 + "'");
            case 'F':
                if (!stackDescriptor.equals("Ljava/lang/Float")) {
                    mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Float");
                }
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false);
                return;
            case 'I':
                if (!stackDescriptor.equals("Ljava/lang/Integer")) {
                    mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Integer");
                }
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
                return;
            case 'J':
                if (!stackDescriptor.equals("Ljava/lang/Long")) {
                    mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Long");
                }
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false);
                return;
            case 'S':
                if (!stackDescriptor.equals("Ljava/lang/Short")) {
                    mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Short");
                }
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S", false);
                return;
            case 'Z':
                if (!stackDescriptor.equals("Ljava/lang/Boolean")) {
                    mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Boolean");
                }
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
                return;
        }
    }

    public static void insertUnboxNumberInsns(MethodVisitor mv, char targetDescriptor, @Nullable String stackDescriptor) {
        if (stackDescriptor == null) {
            return;
        }
        switch (targetDescriptor) {
            case 'D':
                if (stackDescriptor.equals("Ljava/lang/Object")) {
                    mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Number");
                }
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "doubleValue", "()D", false);
                return;
            case 'E':
            case 'G':
            case 'H':
            default:
                throw new IllegalArgumentException("Unboxing should not be attempted for descriptor '" + targetDescriptor + "'");
            case 'F':
                if (stackDescriptor.equals("Ljava/lang/Object")) {
                    mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Number");
                }
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "floatValue", "()F", false);
                return;
            case 'I':
                if (stackDescriptor.equals("Ljava/lang/Object")) {
                    mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Number");
                }
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "intValue", "()I", false);
                return;
            case 'J':
                if (stackDescriptor.equals("Ljava/lang/Object")) {
                    mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Number");
                }
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "longValue", "()J", false);
                return;
        }
    }

    public static void insertAnyNecessaryTypeConversionBytecodes(MethodVisitor mv, char targetDescriptor, String stackDescriptor) {
        if (!isPrimitive(stackDescriptor)) {
            return;
        }
        char stackTop = stackDescriptor.charAt(0);
        switch (stackTop) {
            case 'B':
            case 'C':
            case 'I':
            case 'S':
                switch (targetDescriptor) {
                    case 'D':
                        mv.visitInsn(Opcodes.I2D);
                        return;
                    case 'E':
                    case 'G':
                    case 'H':
                    default:
                        throw new IllegalStateException("Cannot get from " + stackTop + " to " + targetDescriptor);
                    case 'F':
                        mv.visitInsn(Opcodes.I2F);
                        return;
                    case 'I':
                        return;
                    case 'J':
                        mv.visitInsn(Opcodes.I2L);
                        return;
                }
            case 'D':
                switch (targetDescriptor) {
                    case 'D':
                        return;
                    case 'E':
                    case 'G':
                    case 'H':
                    default:
                        throw new IllegalStateException("Cannot get from " + stackDescriptor + " to " + targetDescriptor);
                    case 'F':
                        mv.visitInsn(144);
                        return;
                    case 'I':
                        mv.visitInsn(Opcodes.D2I);
                        return;
                    case 'J':
                        mv.visitInsn(Opcodes.D2L);
                        return;
                }
            case 'E':
            case 'G':
            case 'H':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            default:
                return;
            case 'F':
                switch (targetDescriptor) {
                    case 'D':
                        mv.visitInsn(Opcodes.F2D);
                        return;
                    case 'E':
                    case 'G':
                    case 'H':
                    default:
                        throw new IllegalStateException("Cannot get from " + stackTop + " to " + targetDescriptor);
                    case 'F':
                        return;
                    case 'I':
                        mv.visitInsn(Opcodes.F2I);
                        return;
                    case 'J':
                        mv.visitInsn(Opcodes.F2L);
                        return;
                }
            case 'J':
                switch (targetDescriptor) {
                    case 'D':
                        mv.visitInsn(Opcodes.L2D);
                        return;
                    case 'E':
                    case 'G':
                    case 'H':
                    default:
                        throw new IllegalStateException("Cannot get from " + stackTop + " to " + targetDescriptor);
                    case 'F':
                        mv.visitInsn(Opcodes.L2F);
                        return;
                    case 'I':
                        mv.visitInsn(136);
                        return;
                    case 'J':
                        return;
                }
        }
    }

    public static String createSignatureDescriptor(Method method) {
        Class<?>[] params = method.getParameterTypes();
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        for (Class<?> param : params) {
            sb.append(toJvmDescriptor(param));
        }
        sb.append(')');
        sb.append(toJvmDescriptor(method.getReturnType()));
        return sb.toString();
    }

    public static String createSignatureDescriptor(Constructor<?> ctor) {
        Class<?>[] params = ctor.getParameterTypes();
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        for (Class<?> param : params) {
            sb.append(toJvmDescriptor(param));
        }
        sb.append(")V");
        return sb.toString();
    }

    public static String toJvmDescriptor(Class<?> clazz) {
        StringBuilder sb = new StringBuilder();
        if (clazz.isArray()) {
            while (clazz.isArray()) {
                sb.append('[');
                clazz = clazz.componentType();
            }
        }
        if (clazz.isPrimitive()) {
            if (clazz == Boolean.TYPE) {
                sb.append('Z');
            } else if (clazz == Byte.TYPE) {
                sb.append('B');
            } else if (clazz == Character.TYPE) {
                sb.append('C');
            } else if (clazz == Double.TYPE) {
                sb.append('D');
            } else if (clazz == Float.TYPE) {
                sb.append('F');
            } else if (clazz == Integer.TYPE) {
                sb.append('I');
            } else if (clazz == Long.TYPE) {
                sb.append('J');
            } else if (clazz == Short.TYPE) {
                sb.append('S');
            } else if (clazz == Void.TYPE) {
                sb.append('V');
            }
        } else {
            sb.append('L');
            sb.append(clazz.getName().replace('.', '/'));
            sb.append(';');
        }
        return sb.toString();
    }

    public static String toDescriptorFromObject(@Nullable Object value) {
        if (value == null) {
            return "Ljava/lang/Object";
        }
        return toDescriptor(value.getClass());
    }

    public static boolean isBooleanCompatible(@Nullable String descriptor) {
        return descriptor != null && (descriptor.equals("Z") || descriptor.equals("Ljava/lang/Boolean"));
    }

    public static boolean isPrimitive(@Nullable String descriptor) {
        return descriptor != null && descriptor.length() == 1;
    }

    public static boolean isPrimitiveArray(@Nullable String descriptor) {
        if (descriptor == null) {
            return false;
        }
        boolean primitive = true;
        int i = 0;
        int max = descriptor.length();
        while (true) {
            if (i >= max) {
                break;
            }
            char ch2 = descriptor.charAt(i);
            if (ch2 == '[') {
                i++;
            } else {
                primitive = ch2 != 'L';
            }
        }
        return primitive;
    }

    public static boolean areBoxingCompatible(String desc1, String desc2) {
        if (desc1.equals(desc2)) {
            return true;
        }
        if (desc1.length() == 1) {
            return checkPairs(desc1, desc2);
        }
        if (desc2.length() == 1) {
            return checkPairs(desc2, desc1);
        }
        return false;
    }

    private static boolean checkPairs(String desc1, String desc2) {
        boolean z = -1;
        switch (desc1.hashCode()) {
            case 68:
                if (desc1.equals("D")) {
                    z = true;
                    break;
                }
                break;
            case 70:
                if (desc1.equals("F")) {
                    z = 2;
                    break;
                }
                break;
            case 73:
                if (desc1.equals("I")) {
                    z = 3;
                    break;
                }
                break;
            case 74:
                if (desc1.equals("J")) {
                    z = 4;
                    break;
                }
                break;
            case 90:
                if (desc1.equals("Z")) {
                    z = false;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                return desc2.equals("Ljava/lang/Boolean");
            case true:
                return desc2.equals("Ljava/lang/Double");
            case true:
                return desc2.equals("Ljava/lang/Float");
            case true:
                return desc2.equals("Ljava/lang/Integer");
            case true:
                return desc2.equals("Ljava/lang/Long");
            default:
                return false;
        }
    }

    public static boolean isPrimitiveOrUnboxableSupportedNumberOrBoolean(@Nullable String descriptor) {
        if (descriptor == null) {
            return false;
        }
        return isPrimitiveOrUnboxableSupportedNumber(descriptor) || "Z".equals(descriptor) || descriptor.equals("Ljava/lang/Boolean");
    }

    public static boolean isPrimitiveOrUnboxableSupportedNumber(@Nullable String descriptor) {
        if (descriptor == null) {
            return false;
        }
        if (descriptor.length() == 1) {
            return "DFIJ".contains(descriptor);
        }
        if (descriptor.startsWith("Ljava/lang/")) {
            String name = descriptor.substring("Ljava/lang/".length());
            return name.equals("Double") || name.equals("Float") || name.equals("Integer") || name.equals("Long");
        }
        return false;
    }

    public static boolean isIntegerForNumericOp(Number number) {
        return (number instanceof Integer) || (number instanceof Short) || (number instanceof Byte);
    }

    public static char toPrimitiveTargetDesc(String descriptor) {
        if (descriptor.length() == 1) {
            return descriptor.charAt(0);
        }
        boolean z = -1;
        switch (descriptor.hashCode()) {
            case -1187262392:
                if (descriptor.equals("Ljava/lang/Byte")) {
                    z = 6;
                    break;
                }
                break;
            case -1186974276:
                if (descriptor.equals("Ljava/lang/Long")) {
                    z = 3;
                    break;
                }
                break;
            case -968837912:
                if (descriptor.equals("Ljava/lang/Boolean")) {
                    z = 4;
                    break;
                }
                break;
            case 460444649:
                if (descriptor.equals("Ljava/lang/Character")) {
                    z = 5;
                    break;
                }
                break;
            case 924502526:
                if (descriptor.equals("Ljava/lang/Integer")) {
                    z = 2;
                    break;
                }
                break;
            case 1550195473:
                if (descriptor.equals("Ljava/lang/Double")) {
                    z = false;
                    break;
                }
                break;
            case 1852873500:
                if (descriptor.equals("Ljava/lang/Float")) {
                    z = true;
                    break;
                }
                break;
            case 1864760636:
                if (descriptor.equals("Ljava/lang/Short")) {
                    z = 7;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                return 'D';
            case true:
                return 'F';
            case true:
                return 'I';
            case true:
                return 'J';
            case true:
                return 'Z';
            case true:
                return 'C';
            case true:
                return 'B';
            case true:
                return 'S';
            default:
                throw new IllegalStateException("No primitive for '" + descriptor + "'");
        }
    }

    public static void insertCheckCast(MethodVisitor mv, @Nullable String descriptor) {
        if (descriptor != null && descriptor.length() != 1) {
            if (descriptor.charAt(0) == '[') {
                if (isPrimitiveArray(descriptor)) {
                    mv.visitTypeInsn(Opcodes.CHECKCAST, descriptor);
                    return;
                } else {
                    mv.visitTypeInsn(Opcodes.CHECKCAST, descriptor + ";");
                    return;
                }
            }
            if (!descriptor.equals("Ljava/lang/Object")) {
                mv.visitTypeInsn(Opcodes.CHECKCAST, descriptor.substring(1));
            }
        }
    }

    public static void insertBoxIfNecessary(MethodVisitor mv, @Nullable String descriptor) {
        if (descriptor != null && descriptor.length() == 1) {
            insertBoxIfNecessary(mv, descriptor.charAt(0));
        }
    }

    public static void insertBoxIfNecessary(MethodVisitor mv, char ch2) {
        switch (ch2) {
            case 'B':
                mv.visitMethodInsn(184, "java/lang/Byte", CoreConstants.VALUE_OF, "(B)Ljava/lang/Byte;", false);
                return;
            case 'C':
                mv.visitMethodInsn(184, "java/lang/Character", CoreConstants.VALUE_OF, "(C)Ljava/lang/Character;", false);
                return;
            case 'D':
                mv.visitMethodInsn(184, "java/lang/Double", CoreConstants.VALUE_OF, "(D)Ljava/lang/Double;", false);
                return;
            case 'E':
            case 'G':
            case 'H':
            case 'K':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'T':
            case 'U':
            case 'W':
            case 'X':
            case 'Y':
            default:
                throw new IllegalArgumentException("Boxing should not be attempted for descriptor '" + ch2 + "'");
            case 'F':
                mv.visitMethodInsn(184, "java/lang/Float", CoreConstants.VALUE_OF, "(F)Ljava/lang/Float;", false);
                return;
            case 'I':
                mv.visitMethodInsn(184, "java/lang/Integer", CoreConstants.VALUE_OF, "(I)Ljava/lang/Integer;", false);
                return;
            case 'J':
                mv.visitMethodInsn(184, "java/lang/Long", CoreConstants.VALUE_OF, "(J)Ljava/lang/Long;", false);
                return;
            case 'L':
            case 'V':
            case '[':
                return;
            case 'S':
                mv.visitMethodInsn(184, "java/lang/Short", CoreConstants.VALUE_OF, "(S)Ljava/lang/Short;", false);
                return;
            case 'Z':
                mv.visitMethodInsn(184, "java/lang/Boolean", CoreConstants.VALUE_OF, "(Z)Ljava/lang/Boolean;", false);
                return;
        }
    }

    public static String toDescriptor(Class<?> type) {
        String name = type.getName();
        if (type.isPrimitive()) {
            switch (name.length()) {
                case 3:
                    return "I";
                case 4:
                    boolean z = -1;
                    switch (name.hashCode()) {
                        case 3039496:
                            if (name.equals("byte")) {
                                z = false;
                                break;
                            }
                            break;
                        case 3052374:
                            if (name.equals("char")) {
                                z = true;
                                break;
                            }
                            break;
                        case 3327612:
                            if (name.equals("long")) {
                                z = 2;
                                break;
                            }
                            break;
                        case 3625364:
                            if (name.equals("void")) {
                                z = 3;
                                break;
                            }
                            break;
                    }
                    switch (z) {
                        case false:
                            return "B";
                        case true:
                            return "C";
                        case true:
                            return "J";
                        case true:
                            return "V";
                        default:
                            throw new IllegalArgumentException("Unknown primitive type: " + name);
                    }
                case 5:
                    if (name.equals("float")) {
                        return "F";
                    }
                    if (name.equals("short")) {
                        return "S";
                    }
                    return "";
                case 6:
                    if (name.equals("double")) {
                        return "D";
                    }
                    return "";
                case 7:
                    if (name.equals("boolean")) {
                        return "Z";
                    }
                    return "";
                default:
                    throw new IllegalArgumentException("Unknown primitive type: " + name);
            }
        }
        if (name.charAt(0) != '[') {
            return "L" + type.getName().replace('.', '/');
        }
        if (name.endsWith(";")) {
            return name.substring(0, name.length() - 1).replace('.', '/');
        }
        return name;
    }

    public static String[] toParamDescriptors(Method method) {
        return toDescriptors(method.getParameterTypes());
    }

    public static String[] toParamDescriptors(Constructor<?> ctor) {
        return toDescriptors(ctor.getParameterTypes());
    }

    public static String[] toDescriptors(Class<?>[] types) {
        int typesCount = types.length;
        String[] descriptors = new String[typesCount];
        for (int p = 0; p < typesCount; p++) {
            descriptors[p] = toDescriptor(types[p]);
        }
        return descriptors;
    }

    public static void insertOptimalLoad(MethodVisitor mv, int value) {
        if (value < 6) {
            mv.visitInsn(3 + value);
            return;
        }
        if (value < 127) {
            mv.visitIntInsn(16, value);
        } else if (value < 32767) {
            mv.visitIntInsn(17, value);
        } else {
            mv.visitLdcInsn(Integer.valueOf(value));
        }
    }

    public static void insertArrayStore(MethodVisitor mv, String arrayElementType) {
        if (arrayElementType.length() == 1) {
            switch (arrayElementType.charAt(0)) {
                case 'B':
                case 'Z':
                    mv.visitInsn(84);
                    return;
                case 'C':
                    mv.visitInsn(85);
                    return;
                case 'D':
                    mv.visitInsn(82);
                    return;
                case 'E':
                case 'G':
                case 'H':
                case 'K':
                case 'L':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'T':
                case 'U':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                default:
                    throw new IllegalArgumentException("Unexpected array type " + arrayElementType.charAt(0));
                case 'F':
                    mv.visitInsn(81);
                    return;
                case 'I':
                    mv.visitInsn(79);
                    return;
                case 'J':
                    mv.visitInsn(80);
                    return;
                case 'S':
                    mv.visitInsn(86);
                    return;
            }
        }
        mv.visitInsn(83);
    }

    public static int arrayCodeFor(String arrayType) {
        switch (arrayType.charAt(0)) {
            case 'B':
                return 8;
            case 'C':
                return 5;
            case 'D':
                return 7;
            case 'E':
            case 'G':
            case 'H':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            default:
                throw new IllegalArgumentException("Unexpected array type " + arrayType.charAt(0));
            case 'F':
                return 6;
            case 'I':
                return 10;
            case 'J':
                return 11;
            case 'S':
                return 9;
            case 'Z':
                return 4;
        }
    }

    public static boolean isReferenceTypeArray(String arrayType) {
        int length = arrayType.length();
        for (int i = 0; i < length; i++) {
            char ch2 = arrayType.charAt(i);
            if (ch2 != '[') {
                return ch2 == 'L';
            }
        }
        return false;
    }

    public static void insertNewArrayCode(MethodVisitor mv, int size, String arrayType) {
        insertOptimalLoad(mv, size);
        if (arrayType.length() == 1) {
            mv.visitIntInsn(Opcodes.NEWARRAY, arrayCodeFor(arrayType));
            return;
        }
        if (arrayType.charAt(0) == '[') {
            if (isReferenceTypeArray(arrayType)) {
                mv.visitTypeInsn(Opcodes.ANEWARRAY, arrayType + ";");
                return;
            } else {
                mv.visitTypeInsn(Opcodes.ANEWARRAY, arrayType);
                return;
            }
        }
        mv.visitTypeInsn(Opcodes.ANEWARRAY, arrayType.substring(1));
    }

    public static void insertNumericUnboxOrPrimitiveTypeCoercion(MethodVisitor mv, @Nullable String stackDescriptor, char targetDescriptor) {
        if (!isPrimitive(stackDescriptor)) {
            insertUnboxNumberInsns(mv, targetDescriptor, stackDescriptor);
        } else {
            insertAnyNecessaryTypeConversionBytecodes(mv, targetDescriptor, stackDescriptor);
        }
    }

    public static String toBoxedDescriptor(String primitiveDescriptor) {
        switch (primitiveDescriptor.charAt(0)) {
            case 'B':
                return "Ljava/lang/Byte";
            case 'C':
                return "Ljava/lang/Character";
            case 'D':
                return "Ljava/lang/Double";
            case 'E':
            case 'G':
            case 'H':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            default:
                throw new IllegalArgumentException("Unexpected non primitive descriptor " + primitiveDescriptor);
            case 'F':
                return "Ljava/lang/Float";
            case 'I':
                return "Ljava/lang/Integer";
            case 'J':
                return "Ljava/lang/Long";
            case 'S':
                return "Ljava/lang/Short";
            case 'Z':
                return "Ljava/lang/Boolean";
        }
    }
}
