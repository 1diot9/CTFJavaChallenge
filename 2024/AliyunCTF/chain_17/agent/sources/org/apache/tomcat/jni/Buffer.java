package org.apache.tomcat.jni;

import java.nio.ByteBuffer;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/jni/Buffer.class */
public class Buffer {
    public static native long address(ByteBuffer byteBuffer);
}
