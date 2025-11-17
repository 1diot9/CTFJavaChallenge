package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/JsonMappingException.class */
public class JsonMappingException extends DatabindException {
    private static final long serialVersionUID = 3;
    static final int MAX_REFS_TO_LIST = 1000;
    protected LinkedList<Reference> _path;
    protected transient Closeable _processor;

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/JsonMappingException$Reference.class */
    public static class Reference implements Serializable {
        private static final long serialVersionUID = 2;
        protected transient Object _from;
        protected String _fieldName;
        protected int _index;
        protected String _desc;

        protected Reference() {
            this._index = -1;
        }

        public Reference(Object from) {
            this._index = -1;
            this._from = from;
        }

        public Reference(Object from, String fieldName) {
            this._index = -1;
            this._from = from;
            if (fieldName == null) {
                throw new NullPointerException("Cannot pass null fieldName");
            }
            this._fieldName = fieldName;
        }

        public Reference(Object from, int index) {
            this._index = -1;
            this._from = from;
            this._index = index;
        }

        void setFieldName(String n) {
            this._fieldName = n;
        }

        void setIndex(int ix) {
            this._index = ix;
        }

        void setDescription(String d) {
            this._desc = d;
        }

        @JsonIgnore
        public Object getFrom() {
            return this._from;
        }

        public String getFieldName() {
            return this._fieldName;
        }

        public int getIndex() {
            return this._index;
        }

        public String getDescription() {
            if (this._desc == null) {
                StringBuilder sb = new StringBuilder();
                if (this._from == null) {
                    sb.append("UNKNOWN");
                } else {
                    Class<?> cls = this._from instanceof Class ? (Class) this._from : this._from.getClass();
                    int arrays = 0;
                    while (cls.isArray()) {
                        cls = cls.getComponentType();
                        arrays++;
                    }
                    sb.append(cls.getName());
                    while (true) {
                        arrays--;
                        if (arrays < 0) {
                            break;
                        }
                        sb.append(ClassUtils.ARRAY_SUFFIX);
                    }
                }
                sb.append('[');
                if (this._fieldName != null) {
                    sb.append('\"');
                    sb.append(this._fieldName);
                    sb.append('\"');
                } else if (this._index >= 0) {
                    sb.append(this._index);
                } else {
                    sb.append('?');
                }
                sb.append(']');
                this._desc = sb.toString();
            }
            return this._desc;
        }

        public String toString() {
            return getDescription();
        }

        Object writeReplace() {
            getDescription();
            return this;
        }
    }

    @Deprecated
    public JsonMappingException(String msg) {
        super(msg);
    }

    @Deprecated
    public JsonMappingException(String msg, Throwable rootCause) {
        super(msg, rootCause);
    }

    @Deprecated
    public JsonMappingException(String msg, JsonLocation loc) {
        super(msg, loc);
    }

    @Deprecated
    public JsonMappingException(String msg, JsonLocation loc, Throwable rootCause) {
        super(msg, loc, rootCause);
    }

    public JsonMappingException(Closeable processor, String msg) {
        super(msg);
        this._processor = processor;
        if (processor instanceof JsonParser) {
            this._location = ((JsonParser) processor).getTokenLocation();
        }
    }

    public JsonMappingException(Closeable processor, String msg, Throwable problem) {
        super(msg, problem);
        this._processor = processor;
        if (problem instanceof JacksonException) {
            this._location = ((JacksonException) problem).getLocation();
        } else if (processor instanceof JsonParser) {
            this._location = ((JsonParser) processor).getTokenLocation();
        }
    }

    public JsonMappingException(Closeable processor, String msg, JsonLocation loc) {
        super(msg, loc);
        this._processor = processor;
    }

    public static JsonMappingException from(JsonParser p, String msg) {
        return new JsonMappingException(p, msg);
    }

    public static JsonMappingException from(JsonParser p, String msg, Throwable problem) {
        return new JsonMappingException(p, msg, problem);
    }

    public static JsonMappingException from(JsonGenerator g, String msg) {
        return new JsonMappingException(g, msg, (Throwable) null);
    }

    public static JsonMappingException from(JsonGenerator g, String msg, Throwable problem) {
        return new JsonMappingException(g, msg, problem);
    }

    public static JsonMappingException from(DeserializationContext ctxt, String msg) {
        return new JsonMappingException(_parser(ctxt), msg);
    }

    public static JsonMappingException from(DeserializationContext ctxt, String msg, Throwable t) {
        return new JsonMappingException(_parser(ctxt), msg, t);
    }

    private static JsonParser _parser(DeserializationContext ctxt) {
        if (ctxt == null) {
            return null;
        }
        return ctxt.getParser();
    }

    public static JsonMappingException from(SerializerProvider ctxt, String msg) {
        return new JsonMappingException(_generator(ctxt), msg);
    }

    public static JsonMappingException from(SerializerProvider ctxt, String msg, Throwable problem) {
        return new JsonMappingException(_generator(ctxt), msg, problem);
    }

    private static JsonGenerator _generator(SerializerProvider ctxt) {
        if (ctxt == null) {
            return null;
        }
        return ctxt.getGenerator();
    }

    public static JsonMappingException fromUnexpectedIOE(IOException src) {
        return new JsonMappingException((Closeable) null, String.format("Unexpected IOException (of type %s): %s", src.getClass().getName(), ClassUtil.exceptionMessage(src)));
    }

    public static JsonMappingException wrapWithPath(Throwable src, Object refFrom, String refFieldName) {
        return wrapWithPath(src, new Reference(refFrom, refFieldName));
    }

    public static JsonMappingException wrapWithPath(Throwable src, Object refFrom, int index) {
        return wrapWithPath(src, new Reference(refFrom, index));
    }

    public static JsonMappingException wrapWithPath(Throwable src, Reference ref) {
        JsonMappingException jme;
        if (src instanceof JsonMappingException) {
            jme = (JsonMappingException) src;
        } else {
            String msg = ClassUtil.exceptionMessage(src);
            if (msg == null || msg.isEmpty()) {
                msg = "(was " + src.getClass().getName() + ")";
            }
            Closeable proc = null;
            if (src instanceof JacksonException) {
                Object proc0 = ((JacksonException) src).getProcessor();
                if (proc0 instanceof Closeable) {
                    proc = (Closeable) proc0;
                }
            }
            jme = new JsonMappingException(proc, msg, src);
        }
        jme.prependPath(ref);
        return jme;
    }

    public JsonMappingException withCause(Throwable cause) {
        initCause(cause);
        return this;
    }

    public List<Reference> getPath() {
        if (this._path == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(this._path);
    }

    public String getPathReference() {
        return getPathReference(new StringBuilder()).toString();
    }

    public StringBuilder getPathReference(StringBuilder sb) {
        _appendPathDesc(sb);
        return sb;
    }

    @Override // com.fasterxml.jackson.databind.DatabindException
    public void prependPath(Object referrer, String fieldName) {
        prependPath(new Reference(referrer, fieldName));
    }

    @Override // com.fasterxml.jackson.databind.DatabindException
    public void prependPath(Object referrer, int index) {
        prependPath(new Reference(referrer, index));
    }

    public void prependPath(Reference r) {
        if (this._path == null) {
            this._path = new LinkedList<>();
        }
        if (this._path.size() < 1000) {
            this._path.addFirst(r);
        }
    }

    @Override // com.fasterxml.jackson.core.JsonProcessingException, com.fasterxml.jackson.core.JacksonException
    @JsonIgnore
    public Object getProcessor() {
        return this._processor;
    }

    @Override // java.lang.Throwable
    public String getLocalizedMessage() {
        return _buildMessage();
    }

    @Override // com.fasterxml.jackson.core.JsonProcessingException, java.lang.Throwable
    public String getMessage() {
        return _buildMessage();
    }

    protected String _buildMessage() {
        String msg = super.getMessage();
        if (this._path == null) {
            return msg;
        }
        StringBuilder sb = msg == null ? new StringBuilder() : new StringBuilder(msg);
        sb.append(" (through reference chain: ");
        StringBuilder sb2 = getPathReference(sb);
        sb2.append(')');
        return sb2.toString();
    }

    @Override // com.fasterxml.jackson.core.JsonProcessingException, java.lang.Throwable
    public String toString() {
        return getClass().getName() + ": " + getMessage();
    }

    protected void _appendPathDesc(StringBuilder sb) {
        if (this._path == null) {
            return;
        }
        Iterator<Reference> it = this._path.iterator();
        while (it.hasNext()) {
            sb.append(it.next().toString());
            if (it.hasNext()) {
                sb.append("->");
            }
        }
    }
}
