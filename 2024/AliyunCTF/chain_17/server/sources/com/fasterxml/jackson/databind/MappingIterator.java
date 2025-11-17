package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.core.FormatSchema;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.JsonToken;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/MappingIterator.class */
public class MappingIterator<T> implements Iterator<T>, Closeable {
    protected static final MappingIterator<?> EMPTY_ITERATOR = new MappingIterator<>(null, null, null, null, false, null);
    protected static final int STATE_CLOSED = 0;
    protected static final int STATE_NEED_RESYNC = 1;
    protected static final int STATE_MAY_HAVE_VALUE = 2;
    protected static final int STATE_HAS_VALUE = 3;
    protected final JavaType _type;
    protected final DeserializationContext _context;
    protected final JsonDeserializer<T> _deserializer;
    protected final JsonParser _parser;
    protected final JsonStreamContext _seqContext;
    protected final T _updatedValue;
    protected final boolean _closeParser;
    protected int _state;

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Multi-variable type inference failed */
    public MappingIterator(JavaType type, JsonParser p, DeserializationContext ctxt, JsonDeserializer<?> jsonDeserializer, boolean managedParser, Object obj) {
        this._type = type;
        this._parser = p;
        this._context = ctxt;
        this._deserializer = jsonDeserializer;
        this._closeParser = managedParser;
        if (obj == 0) {
            this._updatedValue = null;
        } else {
            this._updatedValue = obj;
        }
        if (p == null) {
            this._seqContext = null;
            this._state = 0;
            return;
        }
        JsonStreamContext sctxt = p.getParsingContext();
        if (managedParser && p.isExpectedStartArrayToken()) {
            p.clearCurrentToken();
        } else {
            JsonToken t = p.currentToken();
            if (t == JsonToken.START_OBJECT || t == JsonToken.START_ARRAY) {
                sctxt = sctxt.getParent();
            }
        }
        this._seqContext = sctxt;
        this._state = 2;
    }

    public static <T> MappingIterator<T> emptyIterator() {
        return (MappingIterator<T>) EMPTY_ITERATOR;
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        try {
            return hasNextValue();
        } catch (JsonMappingException e) {
            return ((Boolean) _handleMappingException(e)).booleanValue();
        } catch (IOException e2) {
            return ((Boolean) _handleIOException(e2)).booleanValue();
        }
    }

    @Override // java.util.Iterator
    public T next() {
        try {
            return nextValue();
        } catch (JsonMappingException e) {
            return (T) _handleMappingException(e);
        } catch (IOException e2) {
            return (T) _handleIOException(e2);
        }
    }

    @Override // java.util.Iterator
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this._state != 0) {
            this._state = 0;
            if (this._parser != null) {
                this._parser.close();
            }
        }
    }

    public boolean hasNextValue() throws IOException {
        JsonToken t;
        switch (this._state) {
            case 0:
                return false;
            case 1:
                _resync();
                break;
            case 2:
                break;
            case 3:
            default:
                return true;
        }
        if (this._parser == null) {
            return false;
        }
        if (this._parser.currentToken() == null && ((t = this._parser.nextToken()) == null || t == JsonToken.END_ARRAY)) {
            this._state = 0;
            if (this._closeParser) {
                this._parser.close();
                return false;
            }
            return false;
        }
        this._state = 3;
        return true;
    }

    public T nextValue() throws IOException {
        T t;
        switch (this._state) {
            case 0:
                return (T) _throwNoSuchElement();
            case 1:
            case 2:
                if (!hasNextValue()) {
                    return (T) _throwNoSuchElement();
                }
                break;
        }
        int i = 1;
        try {
            if (this._updatedValue == null) {
                t = this._deserializer.deserialize(this._parser, this._context);
            } else {
                this._deserializer.deserialize(this._parser, this._context, this._updatedValue);
                t = this._updatedValue;
            }
            int i2 = 2;
            return t;
        } finally {
            this._state = i;
            this._parser.clearCurrentToken();
        }
    }

    public List<T> readAll() throws IOException {
        return readAll((MappingIterator<T>) new ArrayList());
    }

    public <L extends List<? super T>> L readAll(L resultList) throws IOException {
        while (hasNextValue()) {
            resultList.add(nextValue());
        }
        return resultList;
    }

    public <C extends Collection<? super T>> C readAll(C results) throws IOException {
        while (hasNextValue()) {
            results.add(nextValue());
        }
        return results;
    }

    public JsonParser getParser() {
        return this._parser;
    }

    public FormatSchema getParserSchema() {
        return this._parser.getSchema();
    }

    public JsonLocation getCurrentLocation() {
        return this._parser.getCurrentLocation();
    }

    protected void _resync() throws IOException {
        JsonParser p = this._parser;
        if (p.getParsingContext() == this._seqContext) {
            return;
        }
        while (true) {
            JsonToken t = p.nextToken();
            if (t == JsonToken.END_ARRAY || t == JsonToken.END_OBJECT) {
                if (p.getParsingContext() == this._seqContext) {
                    p.clearCurrentToken();
                    return;
                }
            } else if (t == JsonToken.START_ARRAY || t == JsonToken.START_OBJECT) {
                p.skipChildren();
            } else if (t == null) {
                return;
            }
        }
    }

    protected <R> R _throwNoSuchElement() {
        throw new NoSuchElementException();
    }

    protected <R> R _handleMappingException(JsonMappingException e) {
        throw new RuntimeJsonMappingException(e.getMessage(), e);
    }

    protected <R> R _handleIOException(IOException e) {
        throw new RuntimeException(e.getMessage(), e);
    }
}
