package ch.qos.logback.core.encoder;

import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.OutputStreamAppender;
import ch.qos.logback.core.spi.ContextAware;
import java.nio.charset.Charset;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/encoder/LayoutWrappingEncoder.class */
public class LayoutWrappingEncoder<E> extends EncoderBase<E> {
    protected Layout<E> layout;
    private Charset charset;
    ContextAware parent;
    Boolean immediateFlush = null;

    public Layout<E> getLayout() {
        return this.layout;
    }

    public void setLayout(Layout<E> layout) {
        this.layout = layout;
    }

    public Charset getCharset() {
        return this.charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public void setImmediateFlush(boolean immediateFlush) {
        addWarn("As of version 1.2.0 \"immediateFlush\" property should be set within the enclosing Appender.");
        addWarn("Please move \"immediateFlush\" property into the enclosing appender.");
        this.immediateFlush = Boolean.valueOf(immediateFlush);
    }

    @Override // ch.qos.logback.core.encoder.Encoder
    public byte[] headerBytes() {
        if (this.layout == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        appendIfNotNull(sb, this.layout.getFileHeader());
        appendIfNotNull(sb, this.layout.getPresentationHeader());
        if (sb.length() > 0) {
            sb.append(CoreConstants.LINE_SEPARATOR);
        }
        return convertToBytes(sb.toString());
    }

    @Override // ch.qos.logback.core.encoder.Encoder
    public byte[] footerBytes() {
        if (this.layout == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        appendIfNotNull(sb, this.layout.getPresentationFooter());
        appendIfNotNull(sb, this.layout.getFileFooter());
        return convertToBytes(sb.toString());
    }

    private byte[] convertToBytes(String s) {
        if (this.charset == null) {
            return s.getBytes();
        }
        return s.getBytes(this.charset);
    }

    @Override // ch.qos.logback.core.encoder.Encoder
    public byte[] encode(E event) {
        String txt = this.layout.doLayout(event);
        return convertToBytes(txt);
    }

    @Override // ch.qos.logback.core.encoder.EncoderBase, ch.qos.logback.core.spi.LifeCycle
    public boolean isStarted() {
        return this.started;
    }

    @Override // ch.qos.logback.core.encoder.EncoderBase, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        if (this.immediateFlush != null) {
            if (this.parent instanceof OutputStreamAppender) {
                addWarn("Setting the \"immediateFlush\" property of the enclosing appender to " + this.immediateFlush);
                OutputStreamAppender<E> parentOutputStreamAppender = (OutputStreamAppender) this.parent;
                parentOutputStreamAppender.setImmediateFlush(this.immediateFlush.booleanValue());
            } else {
                addError("Could not set the \"immediateFlush\" property of the enclosing appender.");
            }
        }
        this.started = true;
    }

    @Override // ch.qos.logback.core.encoder.EncoderBase, ch.qos.logback.core.spi.LifeCycle
    public void stop() {
        this.started = false;
    }

    private void appendIfNotNull(StringBuilder sb, String s) {
        if (s != null) {
            sb.append(s);
        }
    }

    public void setParent(ContextAware parent) {
        this.parent = parent;
    }
}
