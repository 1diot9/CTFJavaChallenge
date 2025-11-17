package com.fasterxml.jackson.annotation;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;

/* loaded from: server.jar:BOOT-INF/lib/jackson-annotations-2.15.3.jar:com/fasterxml/jackson/annotation/ObjectIdResolver.class */
public interface ObjectIdResolver {
    void bindItem(ObjectIdGenerator.IdKey idKey, Object obj);

    Object resolveId(ObjectIdGenerator.IdKey idKey);

    ObjectIdResolver newForDeserialization(Object obj);

    boolean canUseFor(ObjectIdResolver objectIdResolver);
}
