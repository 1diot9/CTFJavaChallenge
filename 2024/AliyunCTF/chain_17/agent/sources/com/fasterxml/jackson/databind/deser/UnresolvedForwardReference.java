package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/UnresolvedForwardReference.class */
public class UnresolvedForwardReference extends JsonMappingException {
    private static final long serialVersionUID = 1;
    private ReadableObjectId _roid;
    private List<UnresolvedId> _unresolvedIds;

    public UnresolvedForwardReference(JsonParser p, String msg, JsonLocation loc, ReadableObjectId roid) {
        super(p, msg, loc);
        this._roid = roid;
    }

    public UnresolvedForwardReference(JsonParser p, String msg) {
        super(p, msg);
        this._unresolvedIds = new ArrayList();
    }

    public ReadableObjectId getRoid() {
        return this._roid;
    }

    public Object getUnresolvedId() {
        return this._roid.getKey().key;
    }

    public void addUnresolvedId(Object id, Class<?> type, JsonLocation where) {
        this._unresolvedIds.add(new UnresolvedId(id, type, where));
    }

    public List<UnresolvedId> getUnresolvedIds() {
        return this._unresolvedIds;
    }

    @Override // com.fasterxml.jackson.databind.JsonMappingException, com.fasterxml.jackson.core.JsonProcessingException, java.lang.Throwable
    public String getMessage() {
        String msg = super.getMessage();
        if (this._unresolvedIds == null) {
            return msg;
        }
        StringBuilder sb = new StringBuilder(msg);
        Iterator<UnresolvedId> iterator = this._unresolvedIds.iterator();
        while (iterator.hasNext()) {
            UnresolvedId unresolvedId = iterator.next();
            sb.append(unresolvedId.toString());
            if (iterator.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append('.');
        return sb.toString();
    }

    @Override // java.lang.Throwable
    public synchronized UnresolvedForwardReference fillInStackTrace() {
        return this;
    }

    public UnresolvedForwardReference withStackTrace() {
        super.fillInStackTrace();
        return this;
    }
}
