package com.fasterxml.jackson.annotation;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import java.util.HashMap;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-annotations-2.15.3.jar:com/fasterxml/jackson/annotation/SimpleObjectIdResolver.class */
public class SimpleObjectIdResolver implements ObjectIdResolver {
    protected Map<ObjectIdGenerator.IdKey, Object> _items;

    @Override // com.fasterxml.jackson.annotation.ObjectIdResolver
    public void bindItem(ObjectIdGenerator.IdKey id, Object ob) {
        if (this._items == null) {
            this._items = new HashMap();
        } else {
            Object old = this._items.get(id);
            if (old != null) {
                if (old == ob) {
                    return;
                } else {
                    throw new IllegalStateException("Already had POJO for id (" + id.key.getClass().getName() + ") [" + id + "]");
                }
            }
        }
        this._items.put(id, ob);
    }

    @Override // com.fasterxml.jackson.annotation.ObjectIdResolver
    public Object resolveId(ObjectIdGenerator.IdKey id) {
        if (this._items == null) {
            return null;
        }
        return this._items.get(id);
    }

    @Override // com.fasterxml.jackson.annotation.ObjectIdResolver
    public boolean canUseFor(ObjectIdResolver resolverType) {
        return resolverType.getClass() == getClass();
    }

    @Override // com.fasterxml.jackson.annotation.ObjectIdResolver
    public ObjectIdResolver newForDeserialization(Object context) {
        return new SimpleObjectIdResolver();
    }
}
