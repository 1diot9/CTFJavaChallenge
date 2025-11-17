package org.springframework.javapoet;

import java.io.IOException;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/javapoet/LineWrapper.class */
final class LineWrapper {
    private final RecordingAppendable out;
    private final String indent;
    private final int columnLimit;
    private boolean closed;
    private final StringBuilder buffer = new StringBuilder();
    private int column = 0;
    private int indentLevel = -1;
    private FlushType nextFlush;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/javapoet/LineWrapper$FlushType.class */
    public enum FlushType {
        WRAP,
        SPACE,
        EMPTY
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public LineWrapper(Appendable out, String indent, int columnLimit) {
        Util.checkNotNull(out, "out == null", new Object[0]);
        this.out = new RecordingAppendable(out);
        this.indent = indent;
        this.columnLimit = columnLimit;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public char lastChar() {
        return this.out.lastChar;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void append(String s) throws IOException {
        int length;
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        if (this.nextFlush != null) {
            int nextNewline = s.indexOf(10);
            if (nextNewline == -1 && this.column + s.length() <= this.columnLimit) {
                this.buffer.append(s);
                this.column += s.length();
                return;
            } else {
                boolean wrap = nextNewline == -1 || this.column + nextNewline > this.columnLimit;
                flush(wrap ? FlushType.WRAP : this.nextFlush);
            }
        }
        this.out.append(s);
        int lastNewline = s.lastIndexOf(10);
        if (lastNewline != -1) {
            length = (s.length() - lastNewline) - 1;
        } else {
            length = this.column + s.length();
        }
        this.column = length;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void wrappingSpace(int indentLevel) throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        if (this.nextFlush != null) {
            flush(this.nextFlush);
        }
        this.column++;
        this.nextFlush = FlushType.SPACE;
        this.indentLevel = indentLevel;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void zeroWidthSpace(int indentLevel) throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        if (this.column == 0) {
            return;
        }
        if (this.nextFlush != null) {
            flush(this.nextFlush);
        }
        this.nextFlush = FlushType.EMPTY;
        this.indentLevel = indentLevel;
    }

    void close() throws IOException {
        if (this.nextFlush != null) {
            flush(this.nextFlush);
        }
        this.closed = true;
    }

    private void flush(FlushType flushType) throws IOException {
        switch (flushType) {
            case WRAP:
                this.out.append('\n');
                for (int i = 0; i < this.indentLevel; i++) {
                    this.out.append(this.indent);
                }
                this.column = this.indentLevel * this.indent.length();
                this.column += this.buffer.length();
                break;
            case SPACE:
                this.out.append(' ');
                break;
            case EMPTY:
                break;
            default:
                throw new IllegalArgumentException("Unknown FlushType: " + flushType);
        }
        this.out.append(this.buffer);
        this.buffer.delete(0, this.buffer.length());
        this.indentLevel = -1;
        this.nextFlush = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/javapoet/LineWrapper$RecordingAppendable.class */
    public static final class RecordingAppendable implements Appendable {
        private final Appendable delegate;
        char lastChar = 0;

        RecordingAppendable(Appendable delegate) {
            this.delegate = delegate;
        }

        @Override // java.lang.Appendable
        public Appendable append(CharSequence csq) throws IOException {
            int length = csq.length();
            if (length != 0) {
                this.lastChar = csq.charAt(length - 1);
            }
            return this.delegate.append(csq);
        }

        @Override // java.lang.Appendable
        public Appendable append(CharSequence csq, int start, int end) throws IOException {
            CharSequence sub = csq.subSequence(start, end);
            return append(sub);
        }

        @Override // java.lang.Appendable
        public Appendable append(char c) throws IOException {
            this.lastChar = c;
            return this.delegate.append(c);
        }
    }
}
