package ch.qos.logback.core.encoder;

import ch.qos.logback.core.CoreConstants;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/encoder/EchoEncoder.class */
public class EchoEncoder<E> extends EncoderBase<E> {
    String fileHeader;
    String fileFooter;

    @Override // ch.qos.logback.core.encoder.Encoder
    public byte[] encode(E event) {
        String val = String.valueOf(event) + CoreConstants.LINE_SEPARATOR;
        return val.getBytes();
    }

    @Override // ch.qos.logback.core.encoder.Encoder
    public byte[] footerBytes() {
        if (this.fileFooter == null) {
            return null;
        }
        return this.fileFooter.getBytes();
    }

    @Override // ch.qos.logback.core.encoder.Encoder
    public byte[] headerBytes() {
        if (this.fileHeader == null) {
            return null;
        }
        return this.fileHeader.getBytes();
    }
}
