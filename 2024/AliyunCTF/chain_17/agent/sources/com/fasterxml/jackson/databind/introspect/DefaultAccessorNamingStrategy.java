package com.fasterxml.jackson.databind.introspect;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AccessorNamingStrategy;
import com.fasterxml.jackson.databind.jdk14.JDK14Util;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/introspect/DefaultAccessorNamingStrategy.class */
public class DefaultAccessorNamingStrategy extends AccessorNamingStrategy {
    protected final MapperConfig<?> _config;
    protected final AnnotatedClass _forClass;
    protected final BaseNameValidator _baseNameValidator;
    protected final boolean _stdBeanNaming;
    protected final boolean _isGettersNonBoolean;
    protected final String _getterPrefix;
    protected final String _isGetterPrefix;
    protected final String _mutatorPrefix;

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/introspect/DefaultAccessorNamingStrategy$BaseNameValidator.class */
    public interface BaseNameValidator {
        boolean accept(char c, String str, int i);
    }

    protected DefaultAccessorNamingStrategy(MapperConfig<?> config, AnnotatedClass forClass, String mutatorPrefix, String getterPrefix, String isGetterPrefix, BaseNameValidator baseNameValidator) {
        this._config = config;
        this._forClass = forClass;
        this._stdBeanNaming = config.isEnabled(MapperFeature.USE_STD_BEAN_NAMING);
        this._isGettersNonBoolean = config.isEnabled(MapperFeature.ALLOW_IS_GETTERS_FOR_NON_BOOLEAN);
        this._mutatorPrefix = mutatorPrefix;
        this._getterPrefix = getterPrefix;
        this._isGetterPrefix = isGetterPrefix;
        this._baseNameValidator = baseNameValidator;
    }

    @Override // com.fasterxml.jackson.databind.introspect.AccessorNamingStrategy
    public String findNameForIsGetter(AnnotatedMethod am, String name) {
        if (this._isGetterPrefix != null) {
            if ((this._isGettersNonBoolean || _booleanType(am.getType())) && name.startsWith(this._isGetterPrefix)) {
                if (this._stdBeanNaming) {
                    return stdManglePropertyName(name, this._isGetterPrefix.length());
                }
                return legacyManglePropertyName(name, this._isGetterPrefix.length());
            }
            return null;
        }
        return null;
    }

    private boolean _booleanType(JavaType type) {
        if (type.isReferenceType()) {
            type = type.getReferencedType();
        }
        if (type.hasRawClass(Boolean.TYPE) || type.hasRawClass(Boolean.class) || type.hasRawClass(AtomicBoolean.class)) {
            return true;
        }
        return false;
    }

    @Override // com.fasterxml.jackson.databind.introspect.AccessorNamingStrategy
    public String findNameForRegularGetter(AnnotatedMethod am, String name) {
        if (this._getterPrefix != null && name.startsWith(this._getterPrefix)) {
            if ("getCallbacks".equals(name)) {
                if (_isCglibGetCallbacks(am)) {
                    return null;
                }
            } else if ("getMetaClass".equals(name) && _isGroovyMetaClassGetter(am)) {
                return null;
            }
            if (this._stdBeanNaming) {
                return stdManglePropertyName(name, this._getterPrefix.length());
            }
            return legacyManglePropertyName(name, this._getterPrefix.length());
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.introspect.AccessorNamingStrategy
    public String findNameForMutator(AnnotatedMethod am, String name) {
        if (this._mutatorPrefix != null && name.startsWith(this._mutatorPrefix)) {
            if (this._stdBeanNaming) {
                return stdManglePropertyName(name, this._mutatorPrefix.length());
            }
            return legacyManglePropertyName(name, this._mutatorPrefix.length());
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.introspect.AccessorNamingStrategy
    public String modifyFieldName(AnnotatedField field, String name) {
        return name;
    }

    protected String legacyManglePropertyName(String basename, int offset) {
        int end = basename.length();
        if (end == offset) {
            return null;
        }
        char c = basename.charAt(offset);
        if (this._baseNameValidator != null && !this._baseNameValidator.accept(c, basename, offset)) {
            return null;
        }
        char d = Character.toLowerCase(c);
        if (c == d) {
            return basename.substring(offset);
        }
        StringBuilder sb = new StringBuilder(end - offset);
        sb.append(d);
        int i = offset + 1;
        while (true) {
            if (i >= end) {
                break;
            }
            char c2 = basename.charAt(i);
            char d2 = Character.toLowerCase(c2);
            if (c2 == d2) {
                sb.append((CharSequence) basename, i, end);
                break;
            }
            sb.append(d2);
            i++;
        }
        return sb.toString();
    }

    protected String stdManglePropertyName(String basename, int offset) {
        int end = basename.length();
        if (end == offset) {
            return null;
        }
        char c0 = basename.charAt(offset);
        if (this._baseNameValidator != null && !this._baseNameValidator.accept(c0, basename, offset)) {
            return null;
        }
        char c1 = Character.toLowerCase(c0);
        if (c0 == c1) {
            return basename.substring(offset);
        }
        if (offset + 1 < end && Character.isUpperCase(basename.charAt(offset + 1))) {
            return basename.substring(offset);
        }
        StringBuilder sb = new StringBuilder(end - offset);
        sb.append(c1);
        sb.append((CharSequence) basename, offset + 1, end);
        return sb.toString();
    }

    protected boolean _isCglibGetCallbacks(AnnotatedMethod am) {
        Class<?> rt = am.getRawType();
        if (rt.isArray()) {
            Class<?> compType = rt.getComponentType();
            String className = compType.getName();
            if (className.contains(".cglib")) {
                return className.startsWith("net.sf.cglib") || className.startsWith("org.hibernate.repackage.cglib") || className.startsWith("org.springframework.cglib");
            }
            return false;
        }
        return false;
    }

    protected boolean _isGroovyMetaClassGetter(AnnotatedMethod am) {
        return am.getRawType().getName().startsWith("groovy.lang");
    }

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/introspect/DefaultAccessorNamingStrategy$Provider.class */
    public static class Provider extends AccessorNamingStrategy.Provider implements Serializable {
        private static final long serialVersionUID = 1;
        protected final String _setterPrefix;
        protected final String _withPrefix;
        protected final String _getterPrefix;
        protected final String _isGetterPrefix;
        protected final BaseNameValidator _baseNameValidator;

        public Provider() {
            this("set", JsonPOJOBuilder.DEFAULT_WITH_PREFIX, BeanUtil.PREFIX_GETTER_GET, BeanUtil.PREFIX_GETTER_IS, (BaseNameValidator) null);
        }

        protected Provider(Provider p, String setterPrefix, String withPrefix, String getterPrefix, String isGetterPrefix) {
            this(setterPrefix, withPrefix, getterPrefix, isGetterPrefix, p._baseNameValidator);
        }

        protected Provider(Provider p, BaseNameValidator vld) {
            this(p._setterPrefix, p._withPrefix, p._getterPrefix, p._isGetterPrefix, vld);
        }

        protected Provider(String setterPrefix, String withPrefix, String getterPrefix, String isGetterPrefix, BaseNameValidator vld) {
            this._setterPrefix = setterPrefix;
            this._withPrefix = withPrefix;
            this._getterPrefix = getterPrefix;
            this._isGetterPrefix = isGetterPrefix;
            this._baseNameValidator = vld;
        }

        public Provider withSetterPrefix(String prefix) {
            return new Provider(this, prefix, this._withPrefix, this._getterPrefix, this._isGetterPrefix);
        }

        public Provider withBuilderPrefix(String prefix) {
            return new Provider(this, this._setterPrefix, prefix, this._getterPrefix, this._isGetterPrefix);
        }

        public Provider withGetterPrefix(String prefix) {
            return new Provider(this, this._setterPrefix, this._withPrefix, prefix, this._isGetterPrefix);
        }

        public Provider withIsGetterPrefix(String prefix) {
            return new Provider(this, this._setterPrefix, this._withPrefix, this._getterPrefix, prefix);
        }

        public Provider withFirstCharAcceptance(boolean allowLowerCaseFirstChar, boolean allowNonLetterFirstChar) {
            return withBaseNameValidator(FirstCharBasedValidator.forFirstNameRule(allowLowerCaseFirstChar, allowNonLetterFirstChar));
        }

        public Provider withBaseNameValidator(BaseNameValidator vld) {
            return new Provider(this, vld);
        }

        @Override // com.fasterxml.jackson.databind.introspect.AccessorNamingStrategy.Provider
        public AccessorNamingStrategy forPOJO(MapperConfig<?> config, AnnotatedClass targetClass) {
            return new DefaultAccessorNamingStrategy(config, targetClass, this._setterPrefix, this._getterPrefix, this._isGetterPrefix, this._baseNameValidator);
        }

        @Override // com.fasterxml.jackson.databind.introspect.AccessorNamingStrategy.Provider
        public AccessorNamingStrategy forBuilder(MapperConfig<?> config, AnnotatedClass builderClass, BeanDescription valueTypeDesc) {
            AnnotationIntrospector ai = config.isAnnotationProcessingEnabled() ? config.getAnnotationIntrospector() : null;
            JsonPOJOBuilder.Value builderConfig = ai == null ? null : ai.findPOJOBuilderConfig(builderClass);
            String mutatorPrefix = builderConfig == null ? this._withPrefix : builderConfig.withPrefix;
            return new DefaultAccessorNamingStrategy(config, builderClass, mutatorPrefix, this._getterPrefix, this._isGetterPrefix, this._baseNameValidator);
        }

        @Override // com.fasterxml.jackson.databind.introspect.AccessorNamingStrategy.Provider
        public AccessorNamingStrategy forRecord(MapperConfig<?> config, AnnotatedClass recordClass) {
            return new RecordNaming(config, recordClass);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/introspect/DefaultAccessorNamingStrategy$FirstCharBasedValidator.class */
    public static class FirstCharBasedValidator implements BaseNameValidator {
        private final boolean _allowLowerCaseFirstChar;
        private final boolean _allowNonLetterFirstChar;

        protected FirstCharBasedValidator(boolean allowLowerCaseFirstChar, boolean allowNonLetterFirstChar) {
            this._allowLowerCaseFirstChar = allowLowerCaseFirstChar;
            this._allowNonLetterFirstChar = allowNonLetterFirstChar;
        }

        public static BaseNameValidator forFirstNameRule(boolean allowLowerCaseFirstChar, boolean allowNonLetterFirstChar) {
            if (!allowLowerCaseFirstChar && !allowNonLetterFirstChar) {
                return null;
            }
            return new FirstCharBasedValidator(allowLowerCaseFirstChar, allowNonLetterFirstChar);
        }

        @Override // com.fasterxml.jackson.databind.introspect.DefaultAccessorNamingStrategy.BaseNameValidator
        public boolean accept(char firstChar, String basename, int offset) {
            if (Character.isLetter(firstChar)) {
                return this._allowLowerCaseFirstChar || !Character.isLowerCase(firstChar);
            }
            return this._allowNonLetterFirstChar;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/introspect/DefaultAccessorNamingStrategy$RecordNaming.class */
    public static class RecordNaming extends DefaultAccessorNamingStrategy {
        protected final Set<String> _fieldNames;

        public RecordNaming(MapperConfig<?> config, AnnotatedClass forClass) {
            super(config, forClass, null, BeanUtil.PREFIX_GETTER_GET, BeanUtil.PREFIX_GETTER_IS, null);
            Set<String> hashSet;
            String[] recordFieldNames = JDK14Util.getRecordFieldNames(forClass.getRawType());
            if (recordFieldNames == null) {
                hashSet = Collections.emptySet();
            } else {
                hashSet = new HashSet<>(Arrays.asList(recordFieldNames));
            }
            this._fieldNames = hashSet;
        }

        @Override // com.fasterxml.jackson.databind.introspect.DefaultAccessorNamingStrategy, com.fasterxml.jackson.databind.introspect.AccessorNamingStrategy
        public String findNameForRegularGetter(AnnotatedMethod am, String name) {
            if (this._fieldNames.contains(name)) {
                return name;
            }
            return super.findNameForRegularGetter(am, name);
        }
    }
}
