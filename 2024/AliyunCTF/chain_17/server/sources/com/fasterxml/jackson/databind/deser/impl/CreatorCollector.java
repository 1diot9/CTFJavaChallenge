package com.fasterxml.jackson.databind.deser.impl;

import ch.qos.logback.core.CoreConstants;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.std.StdValueInstantiator;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.lang.reflect.Member;
import java.util.HashMap;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/impl/CreatorCollector.class */
public class CreatorCollector {
    protected static final int C_DEFAULT = 0;
    protected static final int C_STRING = 1;
    protected static final int C_INT = 2;
    protected static final int C_LONG = 3;
    protected static final int C_BIG_INTEGER = 4;
    protected static final int C_DOUBLE = 5;
    protected static final int C_BIG_DECIMAL = 6;
    protected static final int C_BOOLEAN = 7;
    protected static final int C_DELEGATE = 8;
    protected static final int C_PROPS = 9;
    protected static final int C_ARRAY_DELEGATE = 10;
    protected static final String[] TYPE_DESCS = {"default", "from-String", "from-int", "from-long", "from-big-integer", "from-double", "from-big-decimal", "from-boolean", "delegate", "property-based", "array-delegate"};
    protected final BeanDescription _beanDesc;
    protected final boolean _canFixAccess;
    protected final boolean _forceAccess;
    protected final AnnotatedWithParams[] _creators = new AnnotatedWithParams[11];
    protected int _explicitCreators = 0;
    protected boolean _hasNonDefaultCreator = false;
    protected SettableBeanProperty[] _delegateArgs;
    protected SettableBeanProperty[] _arrayDelegateArgs;
    protected SettableBeanProperty[] _propertyBasedArgs;

    public CreatorCollector(BeanDescription beanDesc, MapperConfig<?> config) {
        this._beanDesc = beanDesc;
        this._canFixAccess = config.canOverrideAccessModifiers();
        this._forceAccess = config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS);
    }

    public ValueInstantiator constructValueInstantiator(DeserializationContext ctxt) throws JsonMappingException {
        DeserializationConfig config = ctxt.getConfig();
        JavaType delegateType = _computeDelegateType(ctxt, this._creators[8], this._delegateArgs);
        JavaType arrayDelegateType = _computeDelegateType(ctxt, this._creators[10], this._arrayDelegateArgs);
        JavaType type = this._beanDesc.getType();
        StdValueInstantiator inst = new StdValueInstantiator(config, type);
        inst.configureFromObjectSettings(this._creators[0], this._creators[8], delegateType, this._delegateArgs, this._creators[9], this._propertyBasedArgs);
        inst.configureFromArraySettings(this._creators[10], arrayDelegateType, this._arrayDelegateArgs);
        inst.configureFromStringCreator(this._creators[1]);
        inst.configureFromIntCreator(this._creators[2]);
        inst.configureFromLongCreator(this._creators[3]);
        inst.configureFromBigIntegerCreator(this._creators[4]);
        inst.configureFromDoubleCreator(this._creators[5]);
        inst.configureFromBigDecimalCreator(this._creators[6]);
        inst.configureFromBooleanCreator(this._creators[7]);
        return inst;
    }

    public void setDefaultCreator(AnnotatedWithParams creator) {
        this._creators[0] = (AnnotatedWithParams) _fixAccess(creator);
    }

    public void addStringCreator(AnnotatedWithParams creator, boolean explicit) {
        verifyNonDup(creator, 1, explicit);
    }

    public void addIntCreator(AnnotatedWithParams creator, boolean explicit) {
        verifyNonDup(creator, 2, explicit);
    }

    public void addLongCreator(AnnotatedWithParams creator, boolean explicit) {
        verifyNonDup(creator, 3, explicit);
    }

    public void addBigIntegerCreator(AnnotatedWithParams creator, boolean explicit) {
        verifyNonDup(creator, 4, explicit);
    }

    public void addDoubleCreator(AnnotatedWithParams creator, boolean explicit) {
        verifyNonDup(creator, 5, explicit);
    }

    public void addBigDecimalCreator(AnnotatedWithParams creator, boolean explicit) {
        verifyNonDup(creator, 6, explicit);
    }

    public void addBooleanCreator(AnnotatedWithParams creator, boolean explicit) {
        verifyNonDup(creator, 7, explicit);
    }

    public void addDelegatingCreator(AnnotatedWithParams creator, boolean explicit, SettableBeanProperty[] injectables, int delegateeIndex) {
        if (creator.getParameterType(delegateeIndex).isCollectionLikeType()) {
            if (verifyNonDup(creator, 10, explicit)) {
                this._arrayDelegateArgs = injectables;
            }
        } else if (verifyNonDup(creator, 8, explicit)) {
            this._delegateArgs = injectables;
        }
    }

    public void addPropertyCreator(AnnotatedWithParams creator, boolean explicit, SettableBeanProperty[] properties) {
        Integer old;
        if (verifyNonDup(creator, 9, explicit)) {
            if (properties.length > 1) {
                HashMap<String, Integer> names = new HashMap<>();
                int len = properties.length;
                for (int i = 0; i < len; i++) {
                    String name = properties[i].getName();
                    if ((!name.isEmpty() || properties[i].getInjectableValueId() == null) && (old = names.put(name, Integer.valueOf(i))) != null) {
                        throw new IllegalArgumentException(String.format("Duplicate creator property \"%s\" (index %s vs %d) for type %s ", name, old, Integer.valueOf(i), ClassUtil.nameOf(this._beanDesc.getBeanClass())));
                    }
                }
            }
            this._propertyBasedArgs = properties;
        }
    }

    public boolean hasDefaultCreator() {
        return this._creators[0] != null;
    }

    public boolean hasDelegatingCreator() {
        return this._creators[8] != null;
    }

    public boolean hasPropertyBasedCreator() {
        return this._creators[9] != null;
    }

    private JavaType _computeDelegateType(DeserializationContext ctxt, AnnotatedWithParams creator, SettableBeanProperty[] delegateArgs) throws JsonMappingException {
        if (!this._hasNonDefaultCreator || creator == null) {
            return null;
        }
        int ix = 0;
        if (delegateArgs != null) {
            int i = 0;
            int len = delegateArgs.length;
            while (true) {
                if (i >= len) {
                    break;
                }
                if (delegateArgs[i] != null) {
                    i++;
                } else {
                    ix = i;
                    break;
                }
            }
        }
        DeserializationConfig config = ctxt.getConfig();
        JavaType baseType = creator.getParameterType(ix);
        AnnotationIntrospector intr = config.getAnnotationIntrospector();
        if (intr != null) {
            AnnotatedParameter delegate = creator.getParameter(ix);
            Object deserDef = intr.findDeserializer(delegate);
            if (deserDef != null) {
                JsonDeserializer<Object> deser = ctxt.deserializerInstance(delegate, deserDef);
                baseType = baseType.withValueHandler(deser);
            } else {
                baseType = intr.refineDeserializationType(config, delegate, baseType);
            }
        }
        return baseType;
    }

    private <T extends AnnotatedMember> T _fixAccess(T member) {
        if (member != null && this._canFixAccess) {
            ClassUtil.checkAndFixAccess((Member) member.getAnnotated(), this._forceAccess);
        }
        return member;
    }

    protected boolean verifyNonDup(AnnotatedWithParams newOne, int typeIndex, boolean explicit) {
        boolean verify;
        int mask = 1 << typeIndex;
        this._hasNonDefaultCreator = true;
        AnnotatedWithParams oldOne = this._creators[typeIndex];
        if (oldOne != null) {
            if ((this._explicitCreators & mask) != 0) {
                if (!explicit) {
                    return false;
                }
                verify = true;
            } else {
                verify = !explicit;
            }
            if (verify && oldOne.getClass() == newOne.getClass()) {
                Class<?> oldType = oldOne.getRawParameterType(0);
                Class<?> newType = newOne.getRawParameterType(0);
                if (oldType == newType) {
                    if (_isEnumValueOf(newOne)) {
                        return false;
                    }
                    if (!_isEnumValueOf(oldOne)) {
                        _reportDuplicateCreator(typeIndex, explicit, oldOne, newOne);
                    }
                } else {
                    if (newType.isAssignableFrom(oldType)) {
                        return false;
                    }
                    if (!oldType.isAssignableFrom(newType)) {
                        if (oldType.isPrimitive() != newType.isPrimitive()) {
                            if (oldType.isPrimitive()) {
                                return false;
                            }
                        } else {
                            _reportDuplicateCreator(typeIndex, explicit, oldOne, newOne);
                        }
                    }
                }
            }
        }
        if (explicit) {
            this._explicitCreators |= mask;
        }
        this._creators[typeIndex] = (AnnotatedWithParams) _fixAccess(newOne);
        return true;
    }

    protected void _reportDuplicateCreator(int typeIndex, boolean explicit, AnnotatedWithParams oldOne, AnnotatedWithParams newOne) {
        Object[] objArr = new Object[4];
        objArr[0] = TYPE_DESCS[typeIndex];
        objArr[1] = explicit ? "explicitly marked" : "implicitly discovered";
        objArr[2] = oldOne;
        objArr[3] = newOne;
        throw new IllegalArgumentException(String.format("Conflicting %s creators: already had %s creator %s, encountered another: %s", objArr));
    }

    protected boolean _isEnumValueOf(AnnotatedWithParams creator) {
        return ClassUtil.isEnumType(creator.getDeclaringClass()) && CoreConstants.VALUE_OF.equals(creator.getName());
    }
}
