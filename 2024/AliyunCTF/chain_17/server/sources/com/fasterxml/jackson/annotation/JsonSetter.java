package com.fasterxml.jackson.annotation;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@JacksonAnnotation
@Retention(RetentionPolicy.RUNTIME)
/* loaded from: server.jar:BOOT-INF/lib/jackson-annotations-2.15.3.jar:com/fasterxml/jackson/annotation/JsonSetter.class */
public @interface JsonSetter {
    String value() default "";

    Nulls nulls() default Nulls.DEFAULT;

    Nulls contentNulls() default Nulls.DEFAULT;

    /* loaded from: server.jar:BOOT-INF/lib/jackson-annotations-2.15.3.jar:com/fasterxml/jackson/annotation/JsonSetter$Value.class */
    public static class Value implements JacksonAnnotationValue<JsonSetter>, Serializable {
        private static final long serialVersionUID = 1;
        private final Nulls _nulls;
        private final Nulls _contentNulls;
        protected static final Value EMPTY = new Value(Nulls.DEFAULT, Nulls.DEFAULT);

        protected Value(Nulls nulls, Nulls contentNulls) {
            this._nulls = nulls;
            this._contentNulls = contentNulls;
        }

        @Override // com.fasterxml.jackson.annotation.JacksonAnnotationValue
        public Class<JsonSetter> valueFor() {
            return JsonSetter.class;
        }

        protected Object readResolve() {
            if (_empty(this._nulls, this._contentNulls)) {
                return EMPTY;
            }
            return this;
        }

        public static Value from(JsonSetter src) {
            if (src == null) {
                return EMPTY;
            }
            return construct(src.nulls(), src.contentNulls());
        }

        public static Value construct(Nulls nulls, Nulls contentNulls) {
            if (nulls == null) {
                nulls = Nulls.DEFAULT;
            }
            if (contentNulls == null) {
                contentNulls = Nulls.DEFAULT;
            }
            if (_empty(nulls, contentNulls)) {
                return EMPTY;
            }
            return new Value(nulls, contentNulls);
        }

        public static Value empty() {
            return EMPTY;
        }

        public static Value merge(Value base, Value overrides) {
            return base == null ? overrides : base.withOverrides(overrides);
        }

        public static Value forValueNulls(Nulls nulls) {
            return construct(nulls, Nulls.DEFAULT);
        }

        public static Value forValueNulls(Nulls nulls, Nulls contentNulls) {
            return construct(nulls, contentNulls);
        }

        public static Value forContentNulls(Nulls nulls) {
            return construct(Nulls.DEFAULT, nulls);
        }

        public Value withOverrides(Value overrides) {
            if (overrides == null || overrides == EMPTY) {
                return this;
            }
            Nulls nulls = overrides._nulls;
            Nulls contentNulls = overrides._contentNulls;
            if (nulls == Nulls.DEFAULT) {
                nulls = this._nulls;
            }
            if (contentNulls == Nulls.DEFAULT) {
                contentNulls = this._contentNulls;
            }
            if (nulls == this._nulls && contentNulls == this._contentNulls) {
                return this;
            }
            return construct(nulls, contentNulls);
        }

        public Value withValueNulls(Nulls nulls) {
            if (nulls == null) {
                nulls = Nulls.DEFAULT;
            }
            if (nulls == this._nulls) {
                return this;
            }
            return construct(nulls, this._contentNulls);
        }

        public Value withValueNulls(Nulls valueNulls, Nulls contentNulls) {
            if (valueNulls == null) {
                valueNulls = Nulls.DEFAULT;
            }
            if (contentNulls == null) {
                contentNulls = Nulls.DEFAULT;
            }
            if (valueNulls == this._nulls && contentNulls == this._contentNulls) {
                return this;
            }
            return construct(valueNulls, contentNulls);
        }

        public Value withContentNulls(Nulls nulls) {
            if (nulls == null) {
                nulls = Nulls.DEFAULT;
            }
            if (nulls == this._contentNulls) {
                return this;
            }
            return construct(this._nulls, nulls);
        }

        public Nulls getValueNulls() {
            return this._nulls;
        }

        public Nulls getContentNulls() {
            return this._contentNulls;
        }

        public Nulls nonDefaultValueNulls() {
            if (this._nulls == Nulls.DEFAULT) {
                return null;
            }
            return this._nulls;
        }

        public Nulls nonDefaultContentNulls() {
            if (this._contentNulls == Nulls.DEFAULT) {
                return null;
            }
            return this._contentNulls;
        }

        public String toString() {
            return String.format("JsonSetter.Value(valueNulls=%s,contentNulls=%s)", this._nulls, this._contentNulls);
        }

        public int hashCode() {
            return this._nulls.ordinal() + (this._contentNulls.ordinal() << 2);
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o != null && o.getClass() == getClass()) {
                Value other = (Value) o;
                return other._nulls == this._nulls && other._contentNulls == this._contentNulls;
            }
            return false;
        }

        private static boolean _empty(Nulls nulls, Nulls contentNulls) {
            return nulls == Nulls.DEFAULT && contentNulls == Nulls.DEFAULT;
        }
    }
}
