package org.springframework.aot.nativex;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.springframework.aot.hint.TypeReference;
import org.springframework.beans.PropertyAccessor;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/nativex/BasicJsonWriter.class */
public class BasicJsonWriter {
    private final IndentingWriter writer;

    public BasicJsonWriter(Writer writer, String singleIndent) {
        this.writer = new IndentingWriter(writer, singleIndent);
    }

    public BasicJsonWriter(Writer writer) {
        this(writer, "  ");
    }

    public void writeObject(Map<String, Object> attributes) {
        writeObject(attributes, true);
    }

    public void writeArray(List<?> items) {
        writeArray(items, true);
    }

    private void writeObject(Map<String, Object> attributes, boolean newLine) {
        if (attributes.isEmpty()) {
            this.writer.print("{ }");
        } else {
            this.writer.println("{").indented(writeAll(attributes.entrySet().iterator(), entry -> {
                writeAttribute((String) entry.getKey(), entry.getValue());
            })).print("}");
        }
        if (newLine) {
            this.writer.println();
        }
    }

    private void writeArray(List<?> items, boolean newLine) {
        if (items.isEmpty()) {
            this.writer.print("[ ]");
        } else {
            this.writer.println(PropertyAccessor.PROPERTY_KEY_PREFIX).indented(writeAll(items.iterator(), this::writeValue)).print("]");
        }
        if (newLine) {
            this.writer.println();
        }
    }

    private <T> Runnable writeAll(Iterator<T> it, Consumer<T> writer) {
        return () -> {
            while (it.hasNext()) {
                writer.accept(it.next());
                if (it.hasNext()) {
                    this.writer.println(",");
                } else {
                    this.writer.println();
                }
            }
        };
    }

    private void writeAttribute(String name, Object value) {
        this.writer.print(quote(name) + ": ");
        writeValue(value);
    }

    private void writeValue(Object value) {
        if (value instanceof Map) {
            Map<?, ?> map = (Map) value;
            writeObject(map, false);
            return;
        }
        if (value instanceof List) {
            List<?> list = (List) value;
            writeArray(list, false);
            return;
        }
        if (value instanceof TypeReference) {
            TypeReference typeReference = (TypeReference) value;
            this.writer.print(quote(typeReference.getName()));
        } else if (value instanceof CharSequence) {
            CharSequence string = (CharSequence) value;
            this.writer.print(quote(escape(string)));
        } else {
            if (value instanceof Boolean) {
                Boolean flag = (Boolean) value;
                this.writer.print(Boolean.toString(flag.booleanValue()));
                return;
            }
            throw new IllegalStateException("unsupported type: " + value.getClass());
        }
    }

    private String quote(String name) {
        return "\"" + name + "\"";
    }

    private static String escape(CharSequence input) {
        StringBuilder builder = new StringBuilder();
        input.chars().forEach(c -> {
            Object valueOf;
            switch (c) {
                case 8:
                    valueOf = "\\b";
                    break;
                case 9:
                    valueOf = "\\t";
                    break;
                case 10:
                    valueOf = "\\n";
                    break;
                case 12:
                    valueOf = "\\f";
                    break;
                case 13:
                    valueOf = "\\r";
                    break;
                case 34:
                    valueOf = "\\\"";
                    break;
                case 47:
                    valueOf = "\\/";
                    break;
                case 92:
                    valueOf = "\\\\";
                    break;
                default:
                    if (c <= 31) {
                        valueOf = String.format("\\u%04x", Integer.valueOf(c));
                        break;
                    } else {
                        valueOf = Character.valueOf((char) c);
                        break;
                    }
            }
            builder.append(valueOf);
        });
        return builder.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/nativex/BasicJsonWriter$IndentingWriter.class */
    public static class IndentingWriter extends Writer {
        private final Writer out;
        private final String singleIndent;
        private int level = 0;
        private String currentIndent = "";
        private boolean prependIndent = false;

        IndentingWriter(Writer out, String singleIndent) {
            this.out = out;
            this.singleIndent = singleIndent;
        }

        public IndentingWriter print(String string) {
            write(string.toCharArray(), 0, string.length());
            return this;
        }

        public IndentingWriter println(String string) {
            write(string.toCharArray(), 0, string.length());
            return println();
        }

        public IndentingWriter println() {
            String separator = System.lineSeparator();
            try {
                this.out.write(separator.toCharArray(), 0, separator.length());
                this.prependIndent = true;
                return this;
            } catch (IOException ex) {
                throw new IllegalStateException(ex);
            }
        }

        public IndentingWriter indented(Runnable runnable) {
            indent();
            runnable.run();
            return outdent();
        }

        private IndentingWriter indent() {
            this.level++;
            return refreshIndent();
        }

        private IndentingWriter outdent() {
            this.level--;
            return refreshIndent();
        }

        private IndentingWriter refreshIndent() {
            this.currentIndent = this.singleIndent.repeat(Math.max(0, this.level));
            return this;
        }

        @Override // java.io.Writer
        public void write(char[] chars, int offset, int length) {
            try {
                if (this.prependIndent) {
                    this.out.write(this.currentIndent.toCharArray(), 0, this.currentIndent.length());
                    this.prependIndent = false;
                }
                this.out.write(chars, offset, length);
            } catch (IOException ex) {
                throw new IllegalStateException(ex);
            }
        }

        @Override // java.io.Writer, java.io.Flushable
        public void flush() throws IOException {
            this.out.flush();
        }

        @Override // java.io.Writer, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            this.out.close();
        }
    }
}
