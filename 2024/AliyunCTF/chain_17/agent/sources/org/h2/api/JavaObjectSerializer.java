package org.h2.api;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/api/JavaObjectSerializer.class */
public interface JavaObjectSerializer {
    byte[] serialize(Object obj) throws Exception;

    Object deserialize(byte[] bArr) throws Exception;
}
