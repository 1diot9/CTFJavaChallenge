package org.apache.tomcat.websocket;

import jakarta.websocket.Decoder;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/DecoderEntry.class */
public class DecoderEntry {
    private final Class<?> clazz;
    private final Class<? extends Decoder> decoderClazz;

    public DecoderEntry(Class<?> clazz, Class<? extends Decoder> decoderClazz) {
        this.clazz = clazz;
        this.decoderClazz = decoderClazz;
    }

    public Class<?> getClazz() {
        return this.clazz;
    }

    public Class<? extends Decoder> getDecoderClazz() {
        return this.decoderClazz;
    }
}
