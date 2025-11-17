package org.yaml.snakeyaml.constructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.util.EnumUtils;

/* loaded from: agent.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/constructor/Constructor.class */
public class Constructor extends SafeConstructor {
    public Constructor(LoaderOptions loadingConfig) {
        this((Class<? extends Object>) Object.class, loadingConfig);
    }

    public Constructor(Class<? extends Object> theRoot, LoaderOptions loadingConfig) {
        this(new TypeDescription(checkRoot(theRoot)), null, loadingConfig);
    }

    private static Class<? extends Object> checkRoot(Class<? extends Object> theRoot) {
        if (theRoot == null) {
            throw new NullPointerException("Root class must be provided.");
        }
        return theRoot;
    }

    public Constructor(TypeDescription theRoot, LoaderOptions loadingConfig) {
        this(theRoot, null, loadingConfig);
    }

    public Constructor(TypeDescription theRoot, Collection<TypeDescription> moreTDs, LoaderOptions loadingConfig) {
        super(loadingConfig);
        if (theRoot == null) {
            throw new NullPointerException("Root type must be provided.");
        }
        this.yamlConstructors.put(null, new ConstructYamlObject());
        if (!Object.class.equals(theRoot.getType())) {
            this.rootTag = new Tag(theRoot.getType());
        }
        this.yamlClassConstructors.put(NodeId.scalar, new ConstructScalar());
        this.yamlClassConstructors.put(NodeId.mapping, new ConstructMapping());
        this.yamlClassConstructors.put(NodeId.sequence, new ConstructSequence());
        addTypeDescription(theRoot);
        if (moreTDs != null) {
            for (TypeDescription td : moreTDs) {
                addTypeDescription(td);
            }
        }
    }

    public Constructor(String theRoot, LoaderOptions loadingConfig) throws ClassNotFoundException {
        this((Class<? extends Object>) Class.forName(check(theRoot)), loadingConfig);
    }

    private static String check(String s) {
        if (s == null) {
            throw new NullPointerException("Root type must be provided.");
        }
        if (s.trim().length() == 0) {
            throw new YAMLException("Root type must be provided.");
        }
        return s;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/constructor/Constructor$ConstructMapping.class */
    protected class ConstructMapping implements Construct {
        /* JADX INFO: Access modifiers changed from: protected */
        public ConstructMapping() {
        }

        @Override // org.yaml.snakeyaml.constructor.Construct
        public Object construct(Node node) {
            MappingNode mnode = (MappingNode) node;
            if (Map.class.isAssignableFrom(node.getType())) {
                if (node.isTwoStepsConstruction()) {
                    return Constructor.this.newMap(mnode);
                }
                return Constructor.this.constructMapping(mnode);
            }
            if (Collection.class.isAssignableFrom(node.getType())) {
                if (node.isTwoStepsConstruction()) {
                    return Constructor.this.newSet(mnode);
                }
                return Constructor.this.constructSet(mnode);
            }
            Object obj = Constructor.this.newInstance(mnode);
            if (obj != BaseConstructor.NOT_INSTANTIATED_OBJECT) {
                if (node.isTwoStepsConstruction()) {
                    return obj;
                }
                return constructJavaBean2ndStep(mnode, obj);
            }
            throw new ConstructorException(null, null, "Can't create an instance for " + mnode.getTag(), node.getStartMark());
        }

        @Override // org.yaml.snakeyaml.constructor.Construct
        public void construct2ndStep(Node node, Object object) {
            if (Map.class.isAssignableFrom(node.getType())) {
                Constructor.this.constructMapping2ndStep((MappingNode) node, (Map) object);
            } else if (Set.class.isAssignableFrom(node.getType())) {
                Constructor.this.constructSet2ndStep((MappingNode) node, (Set) object);
            } else {
                constructJavaBean2ndStep((MappingNode) node, object);
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Multi-variable type inference failed */
        public Object constructJavaBean2ndStep(MappingNode node, Object object) {
            Class<?>[] arguments;
            Constructor.this.flattenMapping(node, true);
            Class<? extends Object> beanType = node.getType();
            List<NodeTuple> nodeValue = node.getValue();
            for (NodeTuple tuple : nodeValue) {
                Node valueNode = tuple.getValueNode();
                String key = (String) Constructor.this.constructObject(tuple.getKeyNode());
                try {
                    TypeDescription memberDescription = Constructor.this.typeDefinitions.get(beanType);
                    Property property = memberDescription == null ? getProperty(beanType, key) : memberDescription.getProperty(key);
                    if (!property.isWritable()) {
                        throw new YAMLException("No writable property '" + key + "' on class: " + beanType.getName());
                    }
                    valueNode.setType(property.getType());
                    boolean typeDetected = memberDescription != null && memberDescription.setupPropertyType(key, valueNode);
                    if (!typeDetected && valueNode.getNodeId() != NodeId.scalar && (arguments = property.getActualTypeArguments()) != null && arguments.length > 0) {
                        if (valueNode.getNodeId() == NodeId.sequence) {
                            Class<?> t = arguments[0];
                            ((SequenceNode) valueNode).setListType(t);
                        } else if (Map.class.isAssignableFrom(valueNode.getType())) {
                            Class<?> keyType = arguments[0];
                            Class<?> valueType = arguments[1];
                            MappingNode mappingNode = (MappingNode) valueNode;
                            mappingNode.setTypes(keyType, valueType);
                            mappingNode.setUseClassConstructor(true);
                        } else if (Collection.class.isAssignableFrom(valueNode.getType())) {
                            Class<?> t2 = arguments[0];
                            MappingNode mappingNode2 = (MappingNode) valueNode;
                            mappingNode2.setOnlyKeyType(t2);
                            mappingNode2.setUseClassConstructor(true);
                        }
                    }
                    Object value = memberDescription != null ? newInstance(memberDescription, key, valueNode) : Constructor.this.constructObject(valueNode);
                    if ((property.getType() == Float.TYPE || property.getType() == Float.class) && (value instanceof Double)) {
                        value = Float.valueOf(((Double) value).floatValue());
                    }
                    if (property.getType() == String.class && Tag.BINARY.equals(valueNode.getTag()) && (value instanceof byte[])) {
                        value = new String((byte[]) value);
                    }
                    if (memberDescription == null || !memberDescription.setProperty(object, key, value)) {
                        property.set(object, value);
                    }
                } catch (DuplicateKeyException e) {
                    throw e;
                } catch (Exception e2) {
                    throw new ConstructorException("Cannot create property=" + key + " for JavaBean=" + object, node.getStartMark(), e2.getMessage(), valueNode.getStartMark(), e2);
                }
            }
            return object;
        }

        private Object newInstance(TypeDescription memberDescription, String propertyName, Node node) {
            Object newInstance = memberDescription.newInstance(propertyName, node);
            if (newInstance != null) {
                Constructor.this.constructedObjects.put(node, newInstance);
                return Constructor.this.constructObjectNoCheck(node);
            }
            return Constructor.this.constructObject(node);
        }

        protected Property getProperty(Class<? extends Object> type, String name) {
            return Constructor.this.getPropertyUtils().getProperty(type, name);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/constructor/Constructor$ConstructYamlObject.class */
    protected class ConstructYamlObject implements Construct {
        protected ConstructYamlObject() {
        }

        /* JADX WARN: Multi-variable type inference failed */
        private Construct getConstructor(Node node) {
            Class<?> cl = Constructor.this.getClassForNode(node);
            node.setType(cl);
            Construct constructor = Constructor.this.yamlClassConstructors.get(node.getNodeId());
            return constructor;
        }

        @Override // org.yaml.snakeyaml.constructor.Construct
        public Object construct(Node node) {
            try {
                return getConstructor(node).construct(node);
            } catch (ConstructorException e) {
                throw e;
            } catch (Exception e2) {
                throw new ConstructorException(null, null, "Can't construct a java object for " + node.getTag() + "; exception=" + e2.getMessage(), node.getStartMark(), e2);
            }
        }

        @Override // org.yaml.snakeyaml.constructor.Construct
        public void construct2ndStep(Node node, Object object) {
            try {
                getConstructor(node).construct2ndStep(node, object);
            } catch (Exception e) {
                throw new ConstructorException(null, null, "Can't construct a second step for a java object for " + node.getTag() + "; exception=" + e.getMessage(), node.getStartMark(), e);
            }
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/constructor/Constructor$ConstructScalar.class */
    protected class ConstructScalar extends AbstractConstruct {
        protected ConstructScalar() {
        }

        @Override // org.yaml.snakeyaml.constructor.Construct
        public Object construct(Node nnode) {
            Object result;
            Object argument;
            ScalarNode node = (ScalarNode) nnode;
            Class<?> type = node.getType();
            Object instance = Constructor.this.newInstance(type, node, false);
            if (instance != BaseConstructor.NOT_INSTANTIATED_OBJECT) {
                return instance;
            }
            if (type.isPrimitive() || type == String.class || Number.class.isAssignableFrom(type) || type == Boolean.class || Date.class.isAssignableFrom(type) || type == Character.class || type == BigInteger.class || type == BigDecimal.class || Enum.class.isAssignableFrom(type) || Tag.BINARY.equals(node.getTag()) || Calendar.class.isAssignableFrom(type) || type == UUID.class) {
                result = constructStandardJavaInstance(type, node);
            } else {
                java.lang.reflect.Constructor<?>[] javaConstructors = type.getDeclaredConstructors();
                int oneArgCount = 0;
                java.lang.reflect.Constructor<?> javaConstructor = null;
                for (java.lang.reflect.Constructor<?> c : javaConstructors) {
                    if (c.getParameterTypes().length == 1) {
                        oneArgCount++;
                        javaConstructor = c;
                    }
                }
                if (javaConstructor == null) {
                    throw new YAMLException("No single argument constructor found for " + type);
                }
                if (oneArgCount == 1) {
                    argument = constructStandardJavaInstance(javaConstructor.getParameterTypes()[0], node);
                } else {
                    argument = Constructor.this.constructScalar(node);
                    try {
                        javaConstructor = type.getDeclaredConstructor(String.class);
                    } catch (Exception e) {
                        throw new YAMLException("Can't construct a java object for scalar " + node.getTag() + "; No String constructor found. Exception=" + e.getMessage(), e);
                    }
                }
                try {
                    javaConstructor.setAccessible(true);
                    result = javaConstructor.newInstance(argument);
                } catch (Exception e2) {
                    throw new ConstructorException(null, null, "Can't construct a java object for scalar " + node.getTag() + "; exception=" + e2.getMessage(), node.getStartMark(), e2);
                }
            }
            return result;
        }

        private Object constructStandardJavaInstance(Class type, ScalarNode node) {
            Object result;
            if (type == String.class) {
                Construct stringConstructor = Constructor.this.yamlConstructors.get(Tag.STR);
                result = stringConstructor.construct(node);
            } else if (type == Boolean.class || type == Boolean.TYPE) {
                Construct boolConstructor = Constructor.this.yamlConstructors.get(Tag.BOOL);
                result = boolConstructor.construct(node);
            } else if (type == Character.class || type == Character.TYPE) {
                Construct charConstructor = Constructor.this.yamlConstructors.get(Tag.STR);
                String ch2 = (String) charConstructor.construct(node);
                if (ch2.length() == 0) {
                    result = null;
                } else {
                    if (ch2.length() != 1) {
                        throw new YAMLException("Invalid node Character: '" + ch2 + "'; length: " + ch2.length());
                    }
                    result = Character.valueOf(ch2.charAt(0));
                }
            } else if (Date.class.isAssignableFrom(type)) {
                Construct dateConstructor = Constructor.this.yamlConstructors.get(Tag.TIMESTAMP);
                Date date = (Date) dateConstructor.construct(node);
                if (type == Date.class) {
                    result = date;
                } else {
                    try {
                        java.lang.reflect.Constructor<?> constr = type.getConstructor(Long.TYPE);
                        result = constr.newInstance(Long.valueOf(date.getTime()));
                    } catch (RuntimeException e) {
                        throw e;
                    } catch (Exception e2) {
                        throw new YAMLException("Cannot construct: '" + type + "'");
                    }
                }
            } else if (type == Float.class || type == Double.class || type == Float.TYPE || type == Double.TYPE || type == BigDecimal.class) {
                if (type == BigDecimal.class) {
                    result = new BigDecimal(node.getValue());
                } else {
                    Construct doubleConstructor = Constructor.this.yamlConstructors.get(Tag.FLOAT);
                    result = doubleConstructor.construct(node);
                    if (type == Float.class || type == Float.TYPE) {
                        result = Float.valueOf(((Double) result).floatValue());
                    }
                }
            } else if (type == Byte.class || type == Short.class || type == Integer.class || type == Long.class || type == BigInteger.class || type == Byte.TYPE || type == Short.TYPE || type == Integer.TYPE || type == Long.TYPE) {
                Construct intConstructor = Constructor.this.yamlConstructors.get(Tag.INT);
                Object result2 = intConstructor.construct(node);
                if (type == Byte.class || type == Byte.TYPE) {
                    result = Byte.valueOf(Integer.valueOf(result2.toString()).byteValue());
                } else if (type == Short.class || type == Short.TYPE) {
                    result = Short.valueOf(Integer.valueOf(result2.toString()).shortValue());
                } else if (type == Integer.class || type == Integer.TYPE) {
                    result = Integer.valueOf(Integer.parseInt(result2.toString()));
                } else if (type == Long.class || type == Long.TYPE) {
                    result = Long.valueOf(result2.toString());
                } else {
                    result = new BigInteger(result2.toString());
                }
            } else if (Enum.class.isAssignableFrom(type)) {
                String enumValueName = node.getValue();
                try {
                    if (Constructor.this.loadingConfig.isEnumCaseSensitive()) {
                        result = Enum.valueOf(type, enumValueName);
                    } else {
                        result = EnumUtils.findEnumInsensitiveCase(type, enumValueName);
                    }
                } catch (Exception e3) {
                    throw new YAMLException("Unable to find enum value '" + enumValueName + "' for enum class: " + type.getName());
                }
            } else if (Calendar.class.isAssignableFrom(type)) {
                SafeConstructor.ConstructYamlTimestamp contr = new SafeConstructor.ConstructYamlTimestamp();
                contr.construct(node);
                result = contr.getCalendar();
            } else if (Number.class.isAssignableFrom(type)) {
                result = new SafeConstructor.ConstructYamlFloat().construct(node);
            } else if (UUID.class == type) {
                result = UUID.fromString(node.getValue());
            } else if (Constructor.this.yamlConstructors.containsKey(node.getTag())) {
                result = Constructor.this.yamlConstructors.get(node.getTag()).construct(node);
            } else {
                throw new YAMLException("Unsupported class: " + type);
            }
            return result;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/constructor/Constructor$ConstructSequence.class */
    protected class ConstructSequence implements Construct {
        protected ConstructSequence() {
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // org.yaml.snakeyaml.constructor.Construct
        public Object construct(Node node) {
            SequenceNode snode = (SequenceNode) node;
            if (Set.class.isAssignableFrom(node.getType())) {
                if (node.isTwoStepsConstruction()) {
                    throw new YAMLException("Set cannot be recursive.");
                }
                return Constructor.this.constructSet(snode);
            }
            if (Collection.class.isAssignableFrom(node.getType())) {
                if (node.isTwoStepsConstruction()) {
                    return Constructor.this.newList(snode);
                }
                return Constructor.this.constructSequence(snode);
            }
            if (node.getType().isArray()) {
                if (node.isTwoStepsConstruction()) {
                    return Constructor.this.createArray(node.getType(), snode.getValue().size());
                }
                return Constructor.this.constructArray(snode);
            }
            List<java.lang.reflect.Constructor<?>> possibleConstructors = new ArrayList<>(snode.getValue().size());
            for (java.lang.reflect.Constructor<?> constructor : node.getType().getDeclaredConstructors()) {
                if (snode.getValue().size() == constructor.getParameterTypes().length) {
                    possibleConstructors.add(constructor);
                }
            }
            if (!possibleConstructors.isEmpty()) {
                if (possibleConstructors.size() == 1) {
                    Object[] argumentList = new Object[snode.getValue().size()];
                    java.lang.reflect.Constructor<?> c = possibleConstructors.get(0);
                    int index = 0;
                    for (Node node2 : snode.getValue()) {
                        Class<?> type = c.getParameterTypes()[index];
                        node2.setType(type);
                        int i = index;
                        index++;
                        argumentList[i] = Constructor.this.constructObject(node2);
                    }
                    try {
                        c.setAccessible(true);
                        return c.newInstance(argumentList);
                    } catch (Exception e) {
                        throw new YAMLException(e);
                    }
                }
                List<Object> argumentList2 = Constructor.this.constructSequence(snode);
                Class<?>[] parameterTypes = new Class[argumentList2.size()];
                int index2 = 0;
                for (Object parameter : argumentList2) {
                    parameterTypes[index2] = parameter.getClass();
                    index2++;
                }
                for (java.lang.reflect.Constructor<?> c2 : possibleConstructors) {
                    Class<?>[] argTypes = c2.getParameterTypes();
                    boolean foundConstructor = true;
                    int i2 = 0;
                    while (true) {
                        if (i2 >= argTypes.length) {
                            break;
                        }
                        if (wrapIfPrimitive(argTypes[i2]).isAssignableFrom(parameterTypes[i2])) {
                            i2++;
                        } else {
                            foundConstructor = false;
                            break;
                        }
                    }
                    if (foundConstructor) {
                        try {
                            c2.setAccessible(true);
                            return c2.newInstance(argumentList2.toArray());
                        } catch (Exception e2) {
                            throw new YAMLException(e2);
                        }
                    }
                }
            }
            throw new YAMLException("No suitable constructor with " + snode.getValue().size() + " arguments found for " + node.getType());
        }

        /* JADX WARN: Multi-variable type inference failed */
        private Class<? extends Object> wrapIfPrimitive(Class<?> cls) {
            if (!cls.isPrimitive()) {
                return cls;
            }
            if (cls == Integer.TYPE) {
                return Integer.class;
            }
            if (cls == Float.TYPE) {
                return Float.class;
            }
            if (cls == Double.TYPE) {
                return Double.class;
            }
            if (cls == Boolean.TYPE) {
                return Boolean.class;
            }
            if (cls == Long.TYPE) {
                return Long.class;
            }
            if (cls == Character.TYPE) {
                return Character.class;
            }
            if (cls == Short.TYPE) {
                return Short.class;
            }
            if (cls == Byte.TYPE) {
                return Byte.class;
            }
            throw new YAMLException("Unexpected primitive " + cls);
        }

        @Override // org.yaml.snakeyaml.constructor.Construct
        public void construct2ndStep(Node node, Object object) {
            SequenceNode snode = (SequenceNode) node;
            if (List.class.isAssignableFrom(node.getType())) {
                List<Object> list = (List) object;
                Constructor.this.constructSequenceStep2(snode, list);
            } else {
                if (node.getType().isArray()) {
                    Constructor.this.constructArrayStep2(snode, object);
                    return;
                }
                throw new YAMLException("Immutable objects cannot be recursive.");
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected Class<?> getClassForNode(Node node) {
        Class<? extends Object> classForTag = this.typeTags.get(node.getTag());
        if (classForTag == null) {
            String name = node.getTag().getClassName();
            try {
                Class<?> cl = getClassForName(name);
                this.typeTags.put(node.getTag(), cl);
                return cl;
            } catch (ClassNotFoundException e) {
                throw new YAMLException("Class not found: " + name);
            }
        }
        return classForTag;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Class<?> getClassForName(String name) throws ClassNotFoundException {
        try {
            return Class.forName(name, true, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            return Class.forName(name);
        }
    }
}
