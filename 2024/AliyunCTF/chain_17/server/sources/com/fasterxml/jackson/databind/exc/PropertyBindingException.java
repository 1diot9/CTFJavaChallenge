package com.fasterxml.jackson.databind.exc;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/exc/PropertyBindingException.class */
public abstract class PropertyBindingException extends MismatchedInputException {
    protected final Class<?> _referringClass;
    protected final String _propertyName;
    protected final Collection<Object> _propertyIds;
    protected transient String _propertiesAsString;
    private static final int MAX_DESC_LENGTH = 1000;

    /* JADX INFO: Access modifiers changed from: protected */
    public PropertyBindingException(JsonParser p, String msg, JsonLocation loc, Class<?> referringClass, String propName, Collection<Object> propertyIds) {
        super(p, msg, loc);
        this._referringClass = referringClass;
        this._propertyName = propName;
        this._propertyIds = propertyIds;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Deprecated
    public PropertyBindingException(String msg, JsonLocation loc, Class<?> referringClass, String propName, Collection<Object> propertyIds) {
        this(null, msg, loc, referringClass, propName, propertyIds);
    }

    @Override // com.fasterxml.jackson.core.JsonProcessingException
    public String getMessageSuffix() {
        String suffix = this._propertiesAsString;
        if (suffix == null && this._propertyIds != null) {
            StringBuilder sb = new StringBuilder(100);
            int len = this._propertyIds.size();
            if (len == 1) {
                sb.append(" (one known property: \"");
                sb.append(String.valueOf(this._propertyIds.iterator().next()));
                sb.append('\"');
            } else {
                sb.append(" (").append(len).append(" known properties: ");
                Iterator<Object> it = this._propertyIds.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    sb.append('\"');
                    sb.append(String.valueOf(it.next()));
                    sb.append('\"');
                    if (sb.length() > 1000) {
                        sb.append(" [truncated]");
                        break;
                    }
                    if (it.hasNext()) {
                        sb.append(", ");
                    }
                }
            }
            sb.append("])");
            String sb2 = sb.toString();
            suffix = sb2;
            this._propertiesAsString = sb2;
        }
        return suffix;
    }

    public Class<?> getReferringClass() {
        return this._referringClass;
    }

    public String getPropertyName() {
        return this._propertyName;
    }

    public Collection<Object> getKnownPropertyIds() {
        if (this._propertyIds == null) {
            return null;
        }
        return Collections.unmodifiableCollection(this._propertyIds);
    }
}
