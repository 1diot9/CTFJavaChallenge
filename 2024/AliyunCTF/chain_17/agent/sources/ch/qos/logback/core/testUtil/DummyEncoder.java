package ch.qos.logback.core.testUtil;

import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.encoder.EncoderBase;
import java.nio.charset.Charset;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/testUtil/DummyEncoder.class */
public class DummyEncoder<E> extends EncoderBase<E> {
    public static final String DUMMY = "dummy" + CoreConstants.LINE_SEPARATOR;
    String val;
    String fileHeader;
    String fileFooter;
    Charset charset;

    public Charset getCharset() {
        return this.charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public DummyEncoder() {
        this.val = DUMMY;
    }

    public DummyEncoder(String val) {
        this.val = DUMMY;
        this.val = val;
    }

    @Override // ch.qos.logback.core.encoder.Encoder
    public byte[] encode(E event) {
        return encodeString(this.val);
    }

    byte[] encodeString(String s) {
        if (this.charset == null) {
            return s.getBytes();
        }
        return s.getBytes(this.charset);
    }

    private void appendIfNotNull(StringBuilder sb, String s) {
        if (s != null) {
            sb.append(s);
        }
    }

    byte[] header() {
        StringBuilder sb = new StringBuilder();
        appendIfNotNull(sb, this.fileHeader);
        if (sb.length() > 0) {
            sb.append(CoreConstants.LINE_SEPARATOR);
        }
        return encodeString(sb.toString());
    }

    @Override // ch.qos.logback.core.encoder.Encoder
    public byte[] headerBytes() {
        return header();
    }

    @Override // ch.qos.logback.core.encoder.Encoder
    public byte[] footerBytes() {
        if (this.fileFooter == null) {
            return null;
        }
        return encodeString(this.fileFooter);
    }

    public String getFileHeader() {
        return this.fileHeader;
    }

    public void setFileHeader(String fileHeader) {
        this.fileHeader = fileHeader;
    }

    public String getFileFooter() {
        return this.fileFooter;
    }

    public void setFileFooter(String fileFooter) {
        this.fileFooter = fileFooter;
    }
}
