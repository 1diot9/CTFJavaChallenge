package org.springframework.javapoet;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.StreamSupport;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import org.apache.tomcat.util.codec.binary.BaseNCodec;
import org.springframework.asm.Opcodes;
import org.springframework.beans.factory.support.PropertiesBeanDefinitionReader;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/javapoet/CodeBlock.class */
public final class CodeBlock {
    private static final Pattern NAMED_ARGUMENT = Pattern.compile("\\$(?<argumentName>[\\w_]+):(?<typeChar>[\\w]).*");
    private static final Pattern LOWERCASE = Pattern.compile("[a-z]+[\\w_]*");
    final List<String> formatParts;
    final List<Object> args;

    private CodeBlock(Builder builder) {
        this.formatParts = Util.immutableList(builder.formatParts);
        this.args = Util.immutableList(builder.args);
    }

    public boolean isEmpty() {
        return this.formatParts.isEmpty();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o != null && getClass() == o.getClass()) {
            return toString().equals(o.toString());
        }
        return false;
    }

    public int hashCode() {
        return toString().hashCode();
    }

    public String toString() {
        StringBuilder out = new StringBuilder();
        try {
            new CodeWriter(out).emit(this);
            return out.toString();
        } catch (IOException e) {
            throw new AssertionError();
        }
    }

    public static CodeBlock of(String format, Object... args) {
        return new Builder().add(format, args).build();
    }

    public static CodeBlock join(Iterable<CodeBlock> codeBlocks, String separator) {
        return (CodeBlock) StreamSupport.stream(codeBlocks.spliterator(), false).collect(joining(separator));
    }

    public static Collector<CodeBlock, ?, CodeBlock> joining(String separator) {
        return Collector.of(() -> {
            return new CodeBlockJoiner(separator, builder());
        }, (v0, v1) -> {
            v0.add(v1);
        }, (v0, v1) -> {
            return v0.merge(v1);
        }, (v0) -> {
            return v0.join();
        }, new Collector.Characteristics[0]);
    }

    public static Collector<CodeBlock, ?, CodeBlock> joining(String separator, String prefix, String suffix) {
        Builder builder = builder().add("$N", prefix);
        return Collector.of(() -> {
            return new CodeBlockJoiner(separator, builder);
        }, (v0, v1) -> {
            v0.add(v1);
        }, (v0, v1) -> {
            return v0.merge(v1);
        }, joiner -> {
            builder.add(of("$N", suffix));
            return joiner.join();
        }, new Collector.Characteristics[0]);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        Builder builder = new Builder();
        builder.formatParts.addAll(this.formatParts);
        builder.args.addAll(this.args);
        return builder;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/javapoet/CodeBlock$Builder.class */
    public static final class Builder {
        final List<String> formatParts;
        final List<Object> args;

        private Builder() {
            this.formatParts = new ArrayList();
            this.args = new ArrayList();
        }

        public boolean isEmpty() {
            return this.formatParts.isEmpty();
        }

        public Builder addNamed(String format, Map<String, ?> arguments) {
            int p = 0;
            for (String argument : arguments.keySet()) {
                Util.checkArgument(CodeBlock.LOWERCASE.matcher(argument).matches(), "argument '%s' must start with a lowercase character", argument);
            }
            while (true) {
                if (p >= format.length()) {
                    break;
                }
                int nextP = format.indexOf(PropertiesBeanDefinitionReader.CONSTRUCTOR_ARG_PREFIX, p);
                if (nextP == -1) {
                    this.formatParts.add(format.substring(p));
                    break;
                }
                if (p != nextP) {
                    this.formatParts.add(format.substring(p, nextP));
                    p = nextP;
                }
                Matcher matcher = null;
                int colon = format.indexOf(58, p);
                if (colon != -1) {
                    int endIndex = Math.min(colon + 2, format.length());
                    matcher = CodeBlock.NAMED_ARGUMENT.matcher(format.substring(p, endIndex));
                }
                if (matcher != null && matcher.lookingAt()) {
                    String argumentName = matcher.group("argumentName");
                    Util.checkArgument(arguments.containsKey(argumentName), "Missing named argument for $%s", argumentName);
                    char formatChar = matcher.group("typeChar").charAt(0);
                    addArgument(format, formatChar, arguments.get(argumentName));
                    this.formatParts.add(PropertiesBeanDefinitionReader.CONSTRUCTOR_ARG_PREFIX + formatChar);
                    p += matcher.regionEnd();
                } else {
                    Util.checkArgument(p < format.length() - 1, "dangling $ at end", new Object[0]);
                    Util.checkArgument(isNoArgPlaceholder(format.charAt(p + 1)), "unknown format $%s at %s in '%s'", Character.valueOf(format.charAt(p + 1)), Integer.valueOf(p + 1), format);
                    this.formatParts.add(format.substring(p, p + 2));
                    p += 2;
                }
            }
            return this;
        }

        public Builder add(String format, Object... args) {
            char c;
            int index;
            boolean hasRelative = false;
            boolean hasIndexed = false;
            int relativeParameterCount = 0;
            int[] indexedParameterCount = new int[args.length];
            int p = 0;
            while (p < format.length()) {
                if (format.charAt(p) != '$') {
                    int nextP = format.indexOf(36, p + 1);
                    if (nextP == -1) {
                        nextP = format.length();
                    }
                    this.formatParts.add(format.substring(p, nextP));
                    p = nextP;
                } else {
                    p++;
                    do {
                        Util.checkArgument(p < format.length(), "dangling format characters in '%s'", format);
                        int i = p;
                        p++;
                        c = format.charAt(i);
                        if (c < '0') {
                            break;
                        }
                    } while (c <= '9');
                    int indexEnd = p - 1;
                    if (isNoArgPlaceholder(c)) {
                        Util.checkArgument(p == indexEnd, "$$, $>, $<, $[, $], $W, and $Z may not have an index", new Object[0]);
                        this.formatParts.add(PropertiesBeanDefinitionReader.CONSTRUCTOR_ARG_PREFIX + c);
                    } else {
                        if (p < indexEnd) {
                            index = Integer.parseInt(format.substring(p, indexEnd)) - 1;
                            hasIndexed = true;
                            if (args.length > 0) {
                                int length = index % args.length;
                                indexedParameterCount[length] = indexedParameterCount[length] + 1;
                            }
                        } else {
                            index = relativeParameterCount;
                            hasRelative = true;
                            relativeParameterCount++;
                        }
                        Util.checkArgument(index >= 0 && index < args.length, "index %d for '%s' not in range (received %s arguments)", Integer.valueOf(index + 1), format.substring(p - 1, indexEnd + 1), Integer.valueOf(args.length));
                        Util.checkArgument((hasIndexed && hasRelative) ? false : true, "cannot mix indexed and positional parameters", new Object[0]);
                        addArgument(format, c, args[index]);
                        this.formatParts.add(PropertiesBeanDefinitionReader.CONSTRUCTOR_ARG_PREFIX + c);
                    }
                }
            }
            if (hasRelative) {
                Util.checkArgument(relativeParameterCount >= args.length, "unused arguments: expected %s, received %s", Integer.valueOf(relativeParameterCount), Integer.valueOf(args.length));
            }
            if (hasIndexed) {
                List<String> unused = new ArrayList<>();
                for (int i2 = 0; i2 < args.length; i2++) {
                    if (indexedParameterCount[i2] == 0) {
                        unused.add(PropertiesBeanDefinitionReader.CONSTRUCTOR_ARG_PREFIX + (i2 + 1));
                    }
                }
                String s = unused.size() == 1 ? "" : "s";
                Util.checkArgument(unused.isEmpty(), "unused argument%s: %s", s, String.join(", ", unused));
            }
            return this;
        }

        private boolean isNoArgPlaceholder(char c) {
            return c == '$' || c == '>' || c == '<' || c == '[' || c == ']' || c == 'W' || c == 'Z';
        }

        private void addArgument(String format, char c, Object arg) {
            switch (c) {
                case BaseNCodec.MIME_CHUNK_SIZE /* 76 */:
                    this.args.add(argToLiteral(arg));
                    return;
                case 'M':
                case Opcodes.IASTORE /* 79 */:
                case 'P':
                case Opcodes.FASTORE /* 81 */:
                case Opcodes.DASTORE /* 82 */:
                default:
                    throw new IllegalArgumentException(String.format("invalid format string: '%s'", format));
                case 'N':
                    this.args.add(argToName(arg));
                    return;
                case 'S':
                    this.args.add(argToString(arg));
                    return;
                case Opcodes.BASTORE /* 84 */:
                    this.args.add(argToType(arg));
                    return;
            }
        }

        private String argToName(Object o) {
            if (o instanceof CharSequence) {
                return o.toString();
            }
            if (o instanceof ParameterSpec) {
                return ((ParameterSpec) o).name;
            }
            if (o instanceof FieldSpec) {
                return ((FieldSpec) o).name;
            }
            if (o instanceof MethodSpec) {
                return ((MethodSpec) o).name;
            }
            if (o instanceof TypeSpec) {
                return ((TypeSpec) o).name;
            }
            throw new IllegalArgumentException("expected name but was " + o);
        }

        private Object argToLiteral(Object o) {
            return o;
        }

        private String argToString(Object o) {
            if (o != null) {
                return String.valueOf(o);
            }
            return null;
        }

        private TypeName argToType(Object o) {
            if (o instanceof TypeName) {
                return (TypeName) o;
            }
            if (o instanceof TypeMirror) {
                return TypeName.get((TypeMirror) o);
            }
            if (o instanceof Element) {
                return TypeName.get(((Element) o).asType());
            }
            if (o instanceof Type) {
                return TypeName.get((Type) o);
            }
            throw new IllegalArgumentException("expected type but was " + o);
        }

        public Builder beginControlFlow(String controlFlow, Object... args) {
            add(controlFlow + " {\n", args);
            indent();
            return this;
        }

        public Builder nextControlFlow(String controlFlow, Object... args) {
            unindent();
            add("} " + controlFlow + " {\n", args);
            indent();
            return this;
        }

        public Builder endControlFlow() {
            unindent();
            add("}\n", new Object[0]);
            return this;
        }

        public Builder endControlFlow(String controlFlow, Object... args) {
            unindent();
            add("} " + controlFlow + ";\n", args);
            return this;
        }

        public Builder addStatement(String format, Object... args) {
            add("$[", new Object[0]);
            add(format, args);
            add(";\n$]", new Object[0]);
            return this;
        }

        public Builder addStatement(CodeBlock codeBlock) {
            return addStatement("$L", codeBlock);
        }

        public Builder add(CodeBlock codeBlock) {
            this.formatParts.addAll(codeBlock.formatParts);
            this.args.addAll(codeBlock.args);
            return this;
        }

        public Builder indent() {
            this.formatParts.add("$>");
            return this;
        }

        public Builder unindent() {
            this.formatParts.add("$<");
            return this;
        }

        public Builder clear() {
            this.formatParts.clear();
            this.args.clear();
            return this;
        }

        public CodeBlock build() {
            return new CodeBlock(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/javapoet/CodeBlock$CodeBlockJoiner.class */
    public static final class CodeBlockJoiner {
        private final String delimiter;
        private final Builder builder;
        private boolean first = true;

        CodeBlockJoiner(String delimiter, Builder builder) {
            this.delimiter = delimiter;
            this.builder = builder;
        }

        CodeBlockJoiner add(CodeBlock codeBlock) {
            if (!this.first) {
                this.builder.add(this.delimiter, new Object[0]);
            }
            this.first = false;
            this.builder.add(codeBlock);
            return this;
        }

        CodeBlockJoiner merge(CodeBlockJoiner other) {
            CodeBlock otherBlock = other.builder.build();
            if (!otherBlock.isEmpty()) {
                add(otherBlock);
            }
            return this;
        }

        CodeBlock join() {
            return this.builder.build();
        }
    }
}
