package com.fasterxml.jackson.annotation;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import java.util.UUID;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-annotations-2.15.3.jar:com/fasterxml/jackson/annotation/ObjectIdGenerators.class */
public class ObjectIdGenerators {

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-annotations-2.15.3.jar:com/fasterxml/jackson/annotation/ObjectIdGenerators$None.class */
    public static abstract class None extends ObjectIdGenerator<Object> {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/jackson-annotations-2.15.3.jar:com/fasterxml/jackson/annotation/ObjectIdGenerators$Base.class */
    public static abstract class Base<T> extends ObjectIdGenerator<T> {
        protected final Class<?> _scope;

        @Override // com.fasterxml.jackson.annotation.ObjectIdGenerator
        public abstract T generateId(Object obj);

        protected Base(Class<?> scope) {
            this._scope = scope;
        }

        @Override // com.fasterxml.jackson.annotation.ObjectIdGenerator
        public final Class<?> getScope() {
            return this._scope;
        }

        @Override // com.fasterxml.jackson.annotation.ObjectIdGenerator
        public boolean canUseFor(ObjectIdGenerator<?> gen) {
            return gen.getClass() == getClass() && gen.getScope() == this._scope;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-annotations-2.15.3.jar:com/fasterxml/jackson/annotation/ObjectIdGenerators$PropertyGenerator.class */
    public static abstract class PropertyGenerator extends Base<Object> {
        private static final long serialVersionUID = 1;

        @Override // com.fasterxml.jackson.annotation.ObjectIdGenerators.Base, com.fasterxml.jackson.annotation.ObjectIdGenerator
        public /* bridge */ /* synthetic */ boolean canUseFor(ObjectIdGenerator objectIdGenerator) {
            return super.canUseFor(objectIdGenerator);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public PropertyGenerator(Class<?> scope) {
            super(scope);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-annotations-2.15.3.jar:com/fasterxml/jackson/annotation/ObjectIdGenerators$IntSequenceGenerator.class */
    public static final class IntSequenceGenerator extends Base<Integer> {
        private static final long serialVersionUID = 1;
        protected transient int _nextValue;

        @Override // com.fasterxml.jackson.annotation.ObjectIdGenerators.Base, com.fasterxml.jackson.annotation.ObjectIdGenerator
        public /* bridge */ /* synthetic */ boolean canUseFor(ObjectIdGenerator objectIdGenerator) {
            return super.canUseFor(objectIdGenerator);
        }

        public IntSequenceGenerator() {
            this(Object.class, -1);
        }

        public IntSequenceGenerator(Class<?> scope, int fv) {
            super(scope);
            this._nextValue = fv;
        }

        protected int initialValue() {
            return 1;
        }

        @Override // com.fasterxml.jackson.annotation.ObjectIdGenerator
        public ObjectIdGenerator<Integer> forScope(Class<?> scope) {
            return this._scope == scope ? this : new IntSequenceGenerator(scope, this._nextValue);
        }

        @Override // com.fasterxml.jackson.annotation.ObjectIdGenerator
        public ObjectIdGenerator<Integer> newForSerialization(Object context) {
            return new IntSequenceGenerator(this._scope, initialValue());
        }

        @Override // com.fasterxml.jackson.annotation.ObjectIdGenerator
        public ObjectIdGenerator.IdKey key(Object key) {
            if (key == null) {
                return null;
            }
            return new ObjectIdGenerator.IdKey(getClass(), this._scope, key);
        }

        @Override // com.fasterxml.jackson.annotation.ObjectIdGenerators.Base, com.fasterxml.jackson.annotation.ObjectIdGenerator
        public Integer generateId(Object forPojo) {
            if (forPojo == null) {
                return null;
            }
            int id = this._nextValue;
            this._nextValue++;
            return Integer.valueOf(id);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-annotations-2.15.3.jar:com/fasterxml/jackson/annotation/ObjectIdGenerators$UUIDGenerator.class */
    public static final class UUIDGenerator extends Base<UUID> {
        private static final long serialVersionUID = 1;

        public UUIDGenerator() {
            this(Object.class);
        }

        private UUIDGenerator(Class<?> scope) {
            super(Object.class);
        }

        @Override // com.fasterxml.jackson.annotation.ObjectIdGenerator
        public ObjectIdGenerator<UUID> forScope(Class<?> scope) {
            return this;
        }

        @Override // com.fasterxml.jackson.annotation.ObjectIdGenerator
        public ObjectIdGenerator<UUID> newForSerialization(Object context) {
            return this;
        }

        @Override // com.fasterxml.jackson.annotation.ObjectIdGenerators.Base, com.fasterxml.jackson.annotation.ObjectIdGenerator
        public UUID generateId(Object forPojo) {
            return UUID.randomUUID();
        }

        @Override // com.fasterxml.jackson.annotation.ObjectIdGenerator
        public ObjectIdGenerator.IdKey key(Object key) {
            if (key == null) {
                return null;
            }
            return new ObjectIdGenerator.IdKey(getClass(), null, key);
        }

        @Override // com.fasterxml.jackson.annotation.ObjectIdGenerators.Base, com.fasterxml.jackson.annotation.ObjectIdGenerator
        public boolean canUseFor(ObjectIdGenerator<?> gen) {
            return gen.getClass() == getClass();
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-annotations-2.15.3.jar:com/fasterxml/jackson/annotation/ObjectIdGenerators$StringIdGenerator.class */
    public static final class StringIdGenerator extends Base<String> {
        private static final long serialVersionUID = 1;

        public StringIdGenerator() {
            this(Object.class);
        }

        private StringIdGenerator(Class<?> scope) {
            super(Object.class);
        }

        @Override // com.fasterxml.jackson.annotation.ObjectIdGenerator
        public ObjectIdGenerator<String> forScope(Class<?> scope) {
            return this;
        }

        @Override // com.fasterxml.jackson.annotation.ObjectIdGenerator
        public ObjectIdGenerator<String> newForSerialization(Object context) {
            return this;
        }

        @Override // com.fasterxml.jackson.annotation.ObjectIdGenerators.Base, com.fasterxml.jackson.annotation.ObjectIdGenerator
        public String generateId(Object forPojo) {
            return UUID.randomUUID().toString();
        }

        @Override // com.fasterxml.jackson.annotation.ObjectIdGenerator
        public ObjectIdGenerator.IdKey key(Object key) {
            if (key == null) {
                return null;
            }
            return new ObjectIdGenerator.IdKey(getClass(), null, key);
        }

        @Override // com.fasterxml.jackson.annotation.ObjectIdGenerators.Base, com.fasterxml.jackson.annotation.ObjectIdGenerator
        public boolean canUseFor(ObjectIdGenerator<?> gen) {
            return gen instanceof StringIdGenerator;
        }
    }
}
