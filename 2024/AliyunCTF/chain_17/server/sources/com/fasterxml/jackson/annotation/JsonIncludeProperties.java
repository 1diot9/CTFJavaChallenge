package com.fasterxml.jackson.annotation;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
@JacksonAnnotation
@Retention(RetentionPolicy.RUNTIME)
/* loaded from: server.jar:BOOT-INF/lib/jackson-annotations-2.15.3.jar:com/fasterxml/jackson/annotation/JsonIncludeProperties.class */
public @interface JsonIncludeProperties {
    String[] value() default {};

    /* loaded from: server.jar:BOOT-INF/lib/jackson-annotations-2.15.3.jar:com/fasterxml/jackson/annotation/JsonIncludeProperties$Value.class */
    public static class Value implements JacksonAnnotationValue<JsonIncludeProperties>, Serializable {
        private static final long serialVersionUID = 1;
        protected static final Value ALL = new Value(null);
        protected final Set<String> _included;

        protected Value(Set<String> included) {
            this._included = included;
        }

        public static Value from(JsonIncludeProperties src) {
            if (src == null) {
                return ALL;
            }
            return new Value(_asSet(src.value()));
        }

        public static Value all() {
            return ALL;
        }

        @Override // com.fasterxml.jackson.annotation.JacksonAnnotationValue
        public Class<JsonIncludeProperties> valueFor() {
            return JsonIncludeProperties.class;
        }

        public Set<String> getIncluded() {
            return this._included;
        }

        public Value withOverrides(Value overrides) {
            Set<String> otherIncluded;
            if (overrides == null || (otherIncluded = overrides.getIncluded()) == null) {
                return this;
            }
            if (this._included == null) {
                return overrides;
            }
            HashSet<String> toInclude = new HashSet<>();
            for (String incl : otherIncluded) {
                if (this._included.contains(incl)) {
                    toInclude.add(incl);
                }
            }
            return new Value(toInclude);
        }

        public String toString() {
            return String.format("JsonIncludeProperties.Value(included=%s)", this._included);
        }

        public int hashCode() {
            if (this._included == null) {
                return 0;
            }
            return this._included.size();
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            return o != null && o.getClass() == getClass() && _equals(this._included, ((Value) o)._included);
        }

        private static boolean _equals(Set<String> a, Set<String> b) {
            if (a == null) {
                return b == null;
            }
            return a.equals(b);
        }

        private static Set<String> _asSet(String[] v) {
            if (v == null || v.length == 0) {
                return Collections.emptySet();
            }
            Set<String> s = new HashSet<>(v.length);
            for (String str : v) {
                s.add(str);
            }
            return s;
        }
    }
}
