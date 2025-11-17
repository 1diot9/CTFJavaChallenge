package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import java.io.IOException;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/impl/MergingSettableBeanProperty.class */
public class MergingSettableBeanProperty extends SettableBeanProperty.Delegating {
    private static final long serialVersionUID = 1;
    protected final AnnotatedMember _accessor;

    protected MergingSettableBeanProperty(SettableBeanProperty delegate, AnnotatedMember accessor) {
        super(delegate);
        this._accessor = accessor;
    }

    protected MergingSettableBeanProperty(MergingSettableBeanProperty src, SettableBeanProperty delegate) {
        super(delegate);
        this._accessor = src._accessor;
    }

    public static MergingSettableBeanProperty construct(SettableBeanProperty delegate, AnnotatedMember accessor) {
        return new MergingSettableBeanProperty(delegate, accessor);
    }

    @Override // com.fasterxml.jackson.databind.deser.SettableBeanProperty.Delegating
    protected SettableBeanProperty withDelegate(SettableBeanProperty d) {
        return new MergingSettableBeanProperty(d, this._accessor);
    }

    @Override // com.fasterxml.jackson.databind.deser.SettableBeanProperty.Delegating, com.fasterxml.jackson.databind.deser.SettableBeanProperty
    public void deserializeAndSet(JsonParser p, DeserializationContext ctxt, Object instance) throws IOException {
        Object newValue;
        Object oldValue = this._accessor.getValue(instance);
        if (oldValue == null) {
            newValue = this.delegate.deserialize(p, ctxt);
        } else {
            newValue = this.delegate.deserializeWith(p, ctxt, oldValue);
        }
        if (newValue != oldValue) {
            this.delegate.set(instance, newValue);
        }
    }

    @Override // com.fasterxml.jackson.databind.deser.SettableBeanProperty.Delegating, com.fasterxml.jackson.databind.deser.SettableBeanProperty
    public Object deserializeSetAndReturn(JsonParser p, DeserializationContext ctxt, Object instance) throws IOException {
        Object newValue;
        Object oldValue = this._accessor.getValue(instance);
        if (oldValue == null) {
            newValue = this.delegate.deserialize(p, ctxt);
        } else {
            newValue = this.delegate.deserializeWith(p, ctxt, oldValue);
        }
        if (newValue != oldValue && newValue != null) {
            return this.delegate.setAndReturn(instance, newValue);
        }
        return instance;
    }

    @Override // com.fasterxml.jackson.databind.deser.SettableBeanProperty.Delegating, com.fasterxml.jackson.databind.deser.SettableBeanProperty
    public void set(Object instance, Object value) throws IOException {
        if (value != null) {
            this.delegate.set(instance, value);
        }
    }

    @Override // com.fasterxml.jackson.databind.deser.SettableBeanProperty.Delegating, com.fasterxml.jackson.databind.deser.SettableBeanProperty
    public Object setAndReturn(Object instance, Object value) throws IOException {
        if (value != null) {
            return this.delegate.setAndReturn(instance, value);
        }
        return instance;
    }
}
