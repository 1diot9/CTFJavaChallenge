package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/introspect/VisibilityChecker.class */
public interface VisibilityChecker<T extends VisibilityChecker<T>> {
    T with(JsonAutoDetect jsonAutoDetect);

    T withOverrides(JsonAutoDetect.Value value);

    T with(JsonAutoDetect.Visibility visibility);

    T withVisibility(PropertyAccessor propertyAccessor, JsonAutoDetect.Visibility visibility);

    T withGetterVisibility(JsonAutoDetect.Visibility visibility);

    T withIsGetterVisibility(JsonAutoDetect.Visibility visibility);

    T withSetterVisibility(JsonAutoDetect.Visibility visibility);

    T withCreatorVisibility(JsonAutoDetect.Visibility visibility);

    T withFieldVisibility(JsonAutoDetect.Visibility visibility);

    boolean isGetterVisible(Method method);

    boolean isGetterVisible(AnnotatedMethod annotatedMethod);

    boolean isIsGetterVisible(Method method);

    boolean isIsGetterVisible(AnnotatedMethod annotatedMethod);

    boolean isSetterVisible(Method method);

    boolean isSetterVisible(AnnotatedMethod annotatedMethod);

    boolean isCreatorVisible(Member member);

    boolean isCreatorVisible(AnnotatedMember annotatedMember);

    boolean isFieldVisible(Field field);

    boolean isFieldVisible(AnnotatedField annotatedField);

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/introspect/VisibilityChecker$Std.class */
    public static class Std implements VisibilityChecker<Std>, Serializable {
        private static final long serialVersionUID = 1;
        protected static final Std DEFAULT = new Std(JsonAutoDetect.Visibility.PUBLIC_ONLY, JsonAutoDetect.Visibility.PUBLIC_ONLY, JsonAutoDetect.Visibility.ANY, JsonAutoDetect.Visibility.ANY, JsonAutoDetect.Visibility.PUBLIC_ONLY);
        protected static final Std ALL_PUBLIC = new Std(JsonAutoDetect.Visibility.PUBLIC_ONLY, JsonAutoDetect.Visibility.PUBLIC_ONLY, JsonAutoDetect.Visibility.PUBLIC_ONLY, JsonAutoDetect.Visibility.PUBLIC_ONLY, JsonAutoDetect.Visibility.PUBLIC_ONLY);
        protected final JsonAutoDetect.Visibility _getterMinLevel;
        protected final JsonAutoDetect.Visibility _isGetterMinLevel;
        protected final JsonAutoDetect.Visibility _setterMinLevel;
        protected final JsonAutoDetect.Visibility _creatorMinLevel;
        protected final JsonAutoDetect.Visibility _fieldMinLevel;

        public static Std defaultInstance() {
            return DEFAULT;
        }

        public static Std allPublicInstance() {
            return ALL_PUBLIC;
        }

        public Std(JsonAutoDetect ann) {
            this._getterMinLevel = ann.getterVisibility();
            this._isGetterMinLevel = ann.isGetterVisibility();
            this._setterMinLevel = ann.setterVisibility();
            this._creatorMinLevel = ann.creatorVisibility();
            this._fieldMinLevel = ann.fieldVisibility();
        }

        public Std(JsonAutoDetect.Visibility getter, JsonAutoDetect.Visibility isGetter, JsonAutoDetect.Visibility setter, JsonAutoDetect.Visibility creator, JsonAutoDetect.Visibility field) {
            this._getterMinLevel = getter;
            this._isGetterMinLevel = isGetter;
            this._setterMinLevel = setter;
            this._creatorMinLevel = creator;
            this._fieldMinLevel = field;
        }

        public Std(JsonAutoDetect.Visibility v) {
            if (v == JsonAutoDetect.Visibility.DEFAULT) {
                this._getterMinLevel = DEFAULT._getterMinLevel;
                this._isGetterMinLevel = DEFAULT._isGetterMinLevel;
                this._setterMinLevel = DEFAULT._setterMinLevel;
                this._creatorMinLevel = DEFAULT._creatorMinLevel;
                this._fieldMinLevel = DEFAULT._fieldMinLevel;
                return;
            }
            this._getterMinLevel = v;
            this._isGetterMinLevel = v;
            this._setterMinLevel = v;
            this._creatorMinLevel = v;
            this._fieldMinLevel = v;
        }

        public static Std construct(JsonAutoDetect.Value vis) {
            return DEFAULT.withOverrides(vis);
        }

        protected Std _with(JsonAutoDetect.Visibility g, JsonAutoDetect.Visibility isG, JsonAutoDetect.Visibility s, JsonAutoDetect.Visibility cr, JsonAutoDetect.Visibility f) {
            if (g == this._getterMinLevel && isG == this._isGetterMinLevel && s == this._setterMinLevel && cr == this._creatorMinLevel && f == this._fieldMinLevel) {
                return this;
            }
            return new Std(g, isG, s, cr, f);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.fasterxml.jackson.databind.introspect.VisibilityChecker
        public Std with(JsonAutoDetect ann) {
            if (ann != null) {
                return _with(_defaultOrOverride(this._getterMinLevel, ann.getterVisibility()), _defaultOrOverride(this._isGetterMinLevel, ann.isGetterVisibility()), _defaultOrOverride(this._setterMinLevel, ann.setterVisibility()), _defaultOrOverride(this._creatorMinLevel, ann.creatorVisibility()), _defaultOrOverride(this._fieldMinLevel, ann.fieldVisibility()));
            }
            return this;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.fasterxml.jackson.databind.introspect.VisibilityChecker
        public Std withOverrides(JsonAutoDetect.Value vis) {
            if (vis != null) {
                return _with(_defaultOrOverride(this._getterMinLevel, vis.getGetterVisibility()), _defaultOrOverride(this._isGetterMinLevel, vis.getIsGetterVisibility()), _defaultOrOverride(this._setterMinLevel, vis.getSetterVisibility()), _defaultOrOverride(this._creatorMinLevel, vis.getCreatorVisibility()), _defaultOrOverride(this._fieldMinLevel, vis.getFieldVisibility()));
            }
            return this;
        }

        private JsonAutoDetect.Visibility _defaultOrOverride(JsonAutoDetect.Visibility defaults, JsonAutoDetect.Visibility override) {
            if (override == JsonAutoDetect.Visibility.DEFAULT) {
                return defaults;
            }
            return override;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.fasterxml.jackson.databind.introspect.VisibilityChecker
        public Std with(JsonAutoDetect.Visibility v) {
            if (v == JsonAutoDetect.Visibility.DEFAULT) {
                return DEFAULT;
            }
            return new Std(v);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.fasterxml.jackson.databind.introspect.VisibilityChecker
        public Std withVisibility(PropertyAccessor method, JsonAutoDetect.Visibility v) {
            switch (method) {
                case GETTER:
                    return withGetterVisibility(v);
                case SETTER:
                    return withSetterVisibility(v);
                case CREATOR:
                    return withCreatorVisibility(v);
                case FIELD:
                    return withFieldVisibility(v);
                case IS_GETTER:
                    return withIsGetterVisibility(v);
                case ALL:
                    return with(v);
                default:
                    return this;
            }
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.fasterxml.jackson.databind.introspect.VisibilityChecker
        public Std withGetterVisibility(JsonAutoDetect.Visibility v) {
            if (v == JsonAutoDetect.Visibility.DEFAULT) {
                v = DEFAULT._getterMinLevel;
            }
            return this._getterMinLevel == v ? this : new Std(v, this._isGetterMinLevel, this._setterMinLevel, this._creatorMinLevel, this._fieldMinLevel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.fasterxml.jackson.databind.introspect.VisibilityChecker
        public Std withIsGetterVisibility(JsonAutoDetect.Visibility v) {
            if (v == JsonAutoDetect.Visibility.DEFAULT) {
                v = DEFAULT._isGetterMinLevel;
            }
            return this._isGetterMinLevel == v ? this : new Std(this._getterMinLevel, v, this._setterMinLevel, this._creatorMinLevel, this._fieldMinLevel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.fasterxml.jackson.databind.introspect.VisibilityChecker
        public Std withSetterVisibility(JsonAutoDetect.Visibility v) {
            if (v == JsonAutoDetect.Visibility.DEFAULT) {
                v = DEFAULT._setterMinLevel;
            }
            return this._setterMinLevel == v ? this : new Std(this._getterMinLevel, this._isGetterMinLevel, v, this._creatorMinLevel, this._fieldMinLevel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.fasterxml.jackson.databind.introspect.VisibilityChecker
        public Std withCreatorVisibility(JsonAutoDetect.Visibility v) {
            if (v == JsonAutoDetect.Visibility.DEFAULT) {
                v = DEFAULT._creatorMinLevel;
            }
            return this._creatorMinLevel == v ? this : new Std(this._getterMinLevel, this._isGetterMinLevel, this._setterMinLevel, v, this._fieldMinLevel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.fasterxml.jackson.databind.introspect.VisibilityChecker
        public Std withFieldVisibility(JsonAutoDetect.Visibility v) {
            if (v == JsonAutoDetect.Visibility.DEFAULT) {
                v = DEFAULT._fieldMinLevel;
            }
            return this._fieldMinLevel == v ? this : new Std(this._getterMinLevel, this._isGetterMinLevel, this._setterMinLevel, this._creatorMinLevel, v);
        }

        @Override // com.fasterxml.jackson.databind.introspect.VisibilityChecker
        public boolean isCreatorVisible(Member m) {
            return this._creatorMinLevel.isVisible(m);
        }

        @Override // com.fasterxml.jackson.databind.introspect.VisibilityChecker
        public boolean isCreatorVisible(AnnotatedMember m) {
            return isCreatorVisible(m.getMember());
        }

        @Override // com.fasterxml.jackson.databind.introspect.VisibilityChecker
        public boolean isFieldVisible(Field f) {
            return this._fieldMinLevel.isVisible(f);
        }

        @Override // com.fasterxml.jackson.databind.introspect.VisibilityChecker
        public boolean isFieldVisible(AnnotatedField f) {
            return isFieldVisible(f.getAnnotated());
        }

        @Override // com.fasterxml.jackson.databind.introspect.VisibilityChecker
        public boolean isGetterVisible(Method m) {
            return this._getterMinLevel.isVisible(m);
        }

        @Override // com.fasterxml.jackson.databind.introspect.VisibilityChecker
        public boolean isGetterVisible(AnnotatedMethod m) {
            return isGetterVisible(m.getAnnotated());
        }

        @Override // com.fasterxml.jackson.databind.introspect.VisibilityChecker
        public boolean isIsGetterVisible(Method m) {
            return this._isGetterMinLevel.isVisible(m);
        }

        @Override // com.fasterxml.jackson.databind.introspect.VisibilityChecker
        public boolean isIsGetterVisible(AnnotatedMethod m) {
            return isIsGetterVisible(m.getAnnotated());
        }

        @Override // com.fasterxml.jackson.databind.introspect.VisibilityChecker
        public boolean isSetterVisible(Method m) {
            return this._setterMinLevel.isVisible(m);
        }

        @Override // com.fasterxml.jackson.databind.introspect.VisibilityChecker
        public boolean isSetterVisible(AnnotatedMethod m) {
            return isSetterVisible(m.getAnnotated());
        }

        public String toString() {
            return String.format("[Visibility: getter=%s,isGetter=%s,setter=%s,creator=%s,field=%s]", this._getterMinLevel, this._isGetterMinLevel, this._setterMinLevel, this._creatorMinLevel, this._fieldMinLevel);
        }
    }
}
