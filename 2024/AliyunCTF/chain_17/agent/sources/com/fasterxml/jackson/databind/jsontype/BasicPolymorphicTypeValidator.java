package com.fasterxml.jackson.databind.jsontype;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/jsontype/BasicPolymorphicTypeValidator.class */
public class BasicPolymorphicTypeValidator extends PolymorphicTypeValidator.Base implements Serializable {
    private static final long serialVersionUID = 1;
    protected final Set<Class<?>> _invalidBaseTypes;
    protected final TypeMatcher[] _baseTypeMatchers;
    protected final NameMatcher[] _subTypeNameMatchers;
    protected final TypeMatcher[] _subClassMatchers;

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/jsontype/BasicPolymorphicTypeValidator$NameMatcher.class */
    public static abstract class NameMatcher {
        public abstract boolean match(MapperConfig<?> mapperConfig, String str);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/jsontype/BasicPolymorphicTypeValidator$TypeMatcher.class */
    public static abstract class TypeMatcher {
        public abstract boolean match(MapperConfig<?> mapperConfig, Class<?> cls);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/jsontype/BasicPolymorphicTypeValidator$Builder.class */
    public static class Builder {
        protected Set<Class<?>> _invalidBaseTypes;
        protected List<TypeMatcher> _baseTypeMatchers;
        protected List<NameMatcher> _subTypeNameMatchers;
        protected List<TypeMatcher> _subTypeClassMatchers;

        protected Builder() {
        }

        public Builder allowIfBaseType(final Class<?> baseOfBase) {
            return _appendBaseMatcher(new TypeMatcher() { // from class: com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator.Builder.1
                @Override // com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator.TypeMatcher
                public boolean match(MapperConfig<?> config, Class<?> clazz) {
                    return baseOfBase.isAssignableFrom(clazz);
                }
            });
        }

        public Builder allowIfBaseType(final Pattern patternForBase) {
            return _appendBaseMatcher(new TypeMatcher() { // from class: com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator.Builder.2
                @Override // com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator.TypeMatcher
                public boolean match(MapperConfig<?> config, Class<?> clazz) {
                    return patternForBase.matcher(clazz.getName()).matches();
                }
            });
        }

        public Builder allowIfBaseType(final String prefixForBase) {
            return _appendBaseMatcher(new TypeMatcher() { // from class: com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator.Builder.3
                @Override // com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator.TypeMatcher
                public boolean match(MapperConfig<?> config, Class<?> clazz) {
                    return clazz.getName().startsWith(prefixForBase);
                }
            });
        }

        public Builder allowIfBaseType(TypeMatcher matcher) {
            return _appendBaseMatcher(matcher);
        }

        public Builder denyForExactBaseType(Class<?> baseTypeToDeny) {
            if (this._invalidBaseTypes == null) {
                this._invalidBaseTypes = new HashSet();
            }
            this._invalidBaseTypes.add(baseTypeToDeny);
            return this;
        }

        public Builder allowIfSubType(final Class<?> subTypeBase) {
            return _appendSubClassMatcher(new TypeMatcher() { // from class: com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator.Builder.4
                @Override // com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator.TypeMatcher
                public boolean match(MapperConfig<?> config, Class<?> clazz) {
                    return subTypeBase.isAssignableFrom(clazz);
                }
            });
        }

        public Builder allowIfSubType(final Pattern patternForSubType) {
            return _appendSubNameMatcher(new NameMatcher() { // from class: com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator.Builder.5
                @Override // com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator.NameMatcher
                public boolean match(MapperConfig<?> config, String clazzName) {
                    return patternForSubType.matcher(clazzName).matches();
                }
            });
        }

        public Builder allowIfSubType(final String prefixForSubType) {
            return _appendSubNameMatcher(new NameMatcher() { // from class: com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator.Builder.6
                @Override // com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator.NameMatcher
                public boolean match(MapperConfig<?> config, String clazzName) {
                    return clazzName.startsWith(prefixForSubType);
                }
            });
        }

        public Builder allowIfSubType(TypeMatcher matcher) {
            return _appendSubClassMatcher(matcher);
        }

        public Builder allowIfSubTypeIsArray() {
            return _appendSubClassMatcher(new TypeMatcher() { // from class: com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator.Builder.7
                @Override // com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator.TypeMatcher
                public boolean match(MapperConfig<?> config, Class<?> clazz) {
                    return clazz.isArray();
                }
            });
        }

        public BasicPolymorphicTypeValidator build() {
            return new BasicPolymorphicTypeValidator(this._invalidBaseTypes, this._baseTypeMatchers == null ? null : (TypeMatcher[]) this._baseTypeMatchers.toArray(new TypeMatcher[0]), this._subTypeNameMatchers == null ? null : (NameMatcher[]) this._subTypeNameMatchers.toArray(new NameMatcher[0]), this._subTypeClassMatchers == null ? null : (TypeMatcher[]) this._subTypeClassMatchers.toArray(new TypeMatcher[0]));
        }

        protected Builder _appendBaseMatcher(TypeMatcher matcher) {
            if (this._baseTypeMatchers == null) {
                this._baseTypeMatchers = new ArrayList();
            }
            this._baseTypeMatchers.add(matcher);
            return this;
        }

        protected Builder _appendSubNameMatcher(NameMatcher matcher) {
            if (this._subTypeNameMatchers == null) {
                this._subTypeNameMatchers = new ArrayList();
            }
            this._subTypeNameMatchers.add(matcher);
            return this;
        }

        protected Builder _appendSubClassMatcher(TypeMatcher matcher) {
            if (this._subTypeClassMatchers == null) {
                this._subTypeClassMatchers = new ArrayList();
            }
            this._subTypeClassMatchers.add(matcher);
            return this;
        }
    }

    protected BasicPolymorphicTypeValidator(Set<Class<?>> invalidBaseTypes, TypeMatcher[] baseTypeMatchers, NameMatcher[] subTypeNameMatchers, TypeMatcher[] subClassMatchers) {
        this._invalidBaseTypes = invalidBaseTypes;
        this._baseTypeMatchers = baseTypeMatchers;
        this._subTypeNameMatchers = subTypeNameMatchers;
        this._subClassMatchers = subClassMatchers;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override // com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator.Base, com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator
    public PolymorphicTypeValidator.Validity validateBaseType(MapperConfig<?> ctxt, JavaType baseType) {
        Class<?> rawBase = baseType.getRawClass();
        if (this._invalidBaseTypes != null && this._invalidBaseTypes.contains(rawBase)) {
            return PolymorphicTypeValidator.Validity.DENIED;
        }
        if (this._baseTypeMatchers != null) {
            for (TypeMatcher m : this._baseTypeMatchers) {
                if (m.match(ctxt, rawBase)) {
                    return PolymorphicTypeValidator.Validity.ALLOWED;
                }
            }
        }
        return PolymorphicTypeValidator.Validity.INDETERMINATE;
    }

    @Override // com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator.Base, com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator
    public PolymorphicTypeValidator.Validity validateSubClassName(MapperConfig<?> ctxt, JavaType baseType, String subClassName) throws JsonMappingException {
        if (this._subTypeNameMatchers != null) {
            for (NameMatcher m : this._subTypeNameMatchers) {
                if (m.match(ctxt, subClassName)) {
                    return PolymorphicTypeValidator.Validity.ALLOWED;
                }
            }
        }
        return PolymorphicTypeValidator.Validity.INDETERMINATE;
    }

    @Override // com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator.Base, com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator
    public PolymorphicTypeValidator.Validity validateSubType(MapperConfig<?> ctxt, JavaType baseType, JavaType subType) throws JsonMappingException {
        if (this._subClassMatchers != null) {
            Class<?> subClass = subType.getRawClass();
            for (TypeMatcher m : this._subClassMatchers) {
                if (m.match(ctxt, subClass)) {
                    return PolymorphicTypeValidator.Validity.ALLOWED;
                }
            }
        }
        return PolymorphicTypeValidator.Validity.INDETERMINATE;
    }
}
