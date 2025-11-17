package com.fasterxml.jackson.annotation;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.util.ClassUtils;

@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER})
@JacksonAnnotation
@Retention(RetentionPolicy.RUNTIME)
/* loaded from: server.jar:BOOT-INF/lib/jackson-annotations-2.15.3.jar:com/fasterxml/jackson/annotation/JsonInclude.class */
public @interface JsonInclude {

    /* loaded from: server.jar:BOOT-INF/lib/jackson-annotations-2.15.3.jar:com/fasterxml/jackson/annotation/JsonInclude$Include.class */
    public enum Include {
        ALWAYS,
        NON_NULL,
        NON_ABSENT,
        NON_EMPTY,
        NON_DEFAULT,
        CUSTOM,
        USE_DEFAULTS
    }

    Include value() default Include.ALWAYS;

    Include content() default Include.ALWAYS;

    Class<?> valueFilter() default Void.class;

    Class<?> contentFilter() default Void.class;

    /* loaded from: server.jar:BOOT-INF/lib/jackson-annotations-2.15.3.jar:com/fasterxml/jackson/annotation/JsonInclude$Value.class */
    public static class Value implements JacksonAnnotationValue<JsonInclude>, Serializable {
        private static final long serialVersionUID = 1;
        protected static final Value EMPTY = new Value(Include.USE_DEFAULTS, Include.USE_DEFAULTS, null, null);
        protected final Include _valueInclusion;
        protected final Include _contentInclusion;
        protected final Class<?> _valueFilter;
        protected final Class<?> _contentFilter;

        public Value(JsonInclude src) {
            this(src.value(), src.content(), src.valueFilter(), src.contentFilter());
        }

        protected Value(Include vi, Include ci, Class<?> valueFilter, Class<?> contentFilter) {
            this._valueInclusion = vi == null ? Include.USE_DEFAULTS : vi;
            this._contentInclusion = ci == null ? Include.USE_DEFAULTS : ci;
            this._valueFilter = valueFilter == Void.class ? null : valueFilter;
            this._contentFilter = contentFilter == Void.class ? null : contentFilter;
        }

        public static Value empty() {
            return EMPTY;
        }

        public static Value merge(Value base, Value overrides) {
            return base == null ? overrides : base.withOverrides(overrides);
        }

        public static Value mergeAll(Value... values) {
            Value result = null;
            for (Value curr : values) {
                if (curr != null) {
                    result = result == null ? curr : result.withOverrides(curr);
                }
            }
            return result;
        }

        protected Object readResolve() {
            if (this._valueInclusion == Include.USE_DEFAULTS && this._contentInclusion == Include.USE_DEFAULTS && this._valueFilter == null && this._contentFilter == null) {
                return EMPTY;
            }
            return this;
        }

        public Value withOverrides(Value overrides) {
            if (overrides == null || overrides == EMPTY) {
                return this;
            }
            Include vi = overrides._valueInclusion;
            Include ci = overrides._contentInclusion;
            Class<?> vf = overrides._valueFilter;
            Class<?> cf = overrides._contentFilter;
            boolean viDiff = (vi == this._valueInclusion || vi == Include.USE_DEFAULTS) ? false : true;
            boolean ciDiff = (ci == this._contentInclusion || ci == Include.USE_DEFAULTS) ? false : true;
            boolean filterDiff = (vf == this._valueFilter && cf == this._valueFilter) ? false : true;
            if (viDiff) {
                if (ciDiff) {
                    return new Value(vi, ci, vf, cf);
                }
                return new Value(vi, this._contentInclusion, vf, cf);
            }
            if (ciDiff) {
                return new Value(this._valueInclusion, ci, vf, cf);
            }
            if (filterDiff) {
                return new Value(this._valueInclusion, this._contentInclusion, vf, cf);
            }
            return this;
        }

        public static Value construct(Include valueIncl, Include contentIncl) {
            if ((valueIncl == Include.USE_DEFAULTS || valueIncl == null) && (contentIncl == Include.USE_DEFAULTS || contentIncl == null)) {
                return EMPTY;
            }
            return new Value(valueIncl, contentIncl, null, null);
        }

        public static Value construct(Include valueIncl, Include contentIncl, Class<?> valueFilter, Class<?> contentFilter) {
            if (valueFilter == Void.class) {
                valueFilter = null;
            }
            if (contentFilter == Void.class) {
                contentFilter = null;
            }
            if ((valueIncl == Include.USE_DEFAULTS || valueIncl == null) && ((contentIncl == Include.USE_DEFAULTS || contentIncl == null) && valueFilter == null && contentFilter == null)) {
                return EMPTY;
            }
            return new Value(valueIncl, contentIncl, valueFilter, contentFilter);
        }

        public static Value from(JsonInclude src) {
            if (src == null) {
                return EMPTY;
            }
            Include vi = src.value();
            Include ci = src.content();
            if (vi == Include.USE_DEFAULTS && ci == Include.USE_DEFAULTS) {
                return EMPTY;
            }
            Class<?> vf = src.valueFilter();
            if (vf == Void.class) {
                vf = null;
            }
            Class<?> cf = src.contentFilter();
            if (cf == Void.class) {
                cf = null;
            }
            return new Value(vi, ci, vf, cf);
        }

        public Value withValueInclusion(Include incl) {
            return incl == this._valueInclusion ? this : new Value(incl, this._contentInclusion, this._valueFilter, this._contentFilter);
        }

        public Value withValueFilter(Class<?> filter) {
            Include incl;
            if (filter == null || filter == Void.class) {
                incl = Include.USE_DEFAULTS;
                filter = null;
            } else {
                incl = Include.CUSTOM;
            }
            return construct(incl, this._contentInclusion, filter, this._contentFilter);
        }

        public Value withContentFilter(Class<?> filter) {
            Include incl;
            if (filter == null || filter == Void.class) {
                incl = Include.USE_DEFAULTS;
                filter = null;
            } else {
                incl = Include.CUSTOM;
            }
            return construct(this._valueInclusion, incl, this._valueFilter, filter);
        }

        public Value withContentInclusion(Include incl) {
            return incl == this._contentInclusion ? this : new Value(this._valueInclusion, incl, this._valueFilter, this._contentFilter);
        }

        @Override // com.fasterxml.jackson.annotation.JacksonAnnotationValue
        public Class<JsonInclude> valueFor() {
            return JsonInclude.class;
        }

        public Include getValueInclusion() {
            return this._valueInclusion;
        }

        public Include getContentInclusion() {
            return this._contentInclusion;
        }

        public Class<?> getValueFilter() {
            return this._valueFilter;
        }

        public Class<?> getContentFilter() {
            return this._contentFilter;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(80);
            sb.append("JsonInclude.Value(value=").append(this._valueInclusion).append(",content=").append(this._contentInclusion);
            if (this._valueFilter != null) {
                sb.append(",valueFilter=").append(this._valueFilter.getName()).append(ClassUtils.CLASS_FILE_SUFFIX);
            }
            if (this._contentFilter != null) {
                sb.append(",contentFilter=").append(this._contentFilter.getName()).append(ClassUtils.CLASS_FILE_SUFFIX);
            }
            return sb.append(')').toString();
        }

        public int hashCode() {
            return (this._valueInclusion.hashCode() << 2) + this._contentInclusion.hashCode();
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o == null || o.getClass() != getClass()) {
                return false;
            }
            Value other = (Value) o;
            return other._valueInclusion == this._valueInclusion && other._contentInclusion == this._contentInclusion && other._valueFilter == this._valueFilter && other._contentFilter == this._contentFilter;
        }
    }
}
