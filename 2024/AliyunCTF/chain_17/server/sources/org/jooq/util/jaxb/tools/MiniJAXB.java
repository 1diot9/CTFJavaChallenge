package org.jooq.util.jaxb.tools;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlList;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchema;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.jetbrains.annotations.ApiStatus;
import org.jooq.Constants;
import org.jooq.exception.ConfigurationException;
import org.jooq.tools.Convert;
import org.jooq.tools.JooqLogger;
import org.jooq.tools.reflect.Reflect;
import org.jooq.tools.reflect.ReflectException;
import org.springframework.asm.Opcodes;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

@ApiStatus.Internal
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/util/jaxb/tools/MiniJAXB.class */
public final class MiniJAXB {
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) MiniJAXB.class);
    private static final Map<String, String> PROVIDED_SCHEMAS = new HashMap();

    static {
        PROVIDED_SCHEMAS.put(Constants.NS_CODEGEN, Constants.CP_CODEGEN);
        PROVIDED_SCHEMAS.put(Constants.NS_EXPORT, Constants.CP_EXPORT);
        PROVIDED_SCHEMAS.put("http://www.jooq.org/xsd/jooq-meta-3.19.0.xsd", "/org/jooq/xsd/jooq-meta-3.19.0.xsd");
        PROVIDED_SCHEMAS.put(Constants.NS_RUNTIME, Constants.CP_RUNTIME);
    }

    public static String marshal(XMLAppendable object) {
        StringWriter writer = new StringWriter();
        marshal(object, writer);
        return writer.toString();
    }

    public static void marshal(XMLAppendable object, OutputStream out) {
        marshal(object, new OutputStreamWriter(out));
    }

    public static void marshal(XMLAppendable object, Writer out) {
        try {
            XMLBuilder builder = XMLBuilder.formatting();
            XmlRootElement e = getAnnotation(object.getClass(), XmlRootElement.class);
            if (e != null) {
                out.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
                builder.append(e.name(), object);
            } else {
                builder.append(object);
            }
            builder.appendTo(out);
            out.flush();
        } catch (Exception e2) {
            throw new ConfigurationException("Cannot print object", e2);
        }
    }

    public static <T extends XMLAppendable> T unmarshal(Reader reader, Class<T> cls) {
        return (T) unmarshal0(new InputSource(reader), cls);
    }

    public static <T extends XMLAppendable> T unmarshal(InputStream inputStream, Class<T> cls) {
        return (T) unmarshal0(new InputSource(inputStream), cls);
    }

    public static <T extends XMLAppendable> T unmarshal(String str, Class<T> cls) {
        return (T) unmarshal0(new InputSource(new StringReader(str)), cls);
    }

    public static <T extends XMLAppendable> T unmarshal(File file, Class<T> cls) {
        try {
            return (T) unmarshal0(new InputSource(new FileInputStream(file)), cls);
        } catch (Exception e) {
            throw new ConfigurationException("Error while opening file", e);
        }
    }

    private static <T extends XMLAppendable> T unmarshal0(InputSource in, Class<T> type) {
        try {
            addDefaultNamespace(in, type);
            Document document = builder(type).parse(in);
            T t = (T) Reflect.on((Class<?>) type).create().get();
            unmarshal0(t, document.getDocumentElement(), new IdentityHashMap());
            return t;
        } catch (Exception e) {
            throw new ConfigurationException("Error while reading xml", e);
        }
    }

    private static void addDefaultNamespace(InputSource in, Class<?> type) throws IOException {
        Reader reader;
        int startIdx;
        String namespace = getNamespace(type);
        if (namespace != null) {
            if (in.getCharacterStream() != null) {
                reader = in.getCharacterStream();
            } else {
                reader = new InputStreamReader(in.getByteStream(), in.getEncoding() != null ? in.getEncoding() : Charset.defaultCharset().name());
            }
            StringWriter writer = new StringWriter();
            copyLarge(reader, writer);
            String xml = writer.toString();
            int indexOf = xml.indexOf(60);
            while (true) {
                startIdx = indexOf;
                if (startIdx <= 0 || xml.length() <= startIdx + 1 || xml.charAt(startIdx + 1) != '?') {
                    break;
                } else {
                    indexOf = xml.indexOf(60, startIdx + 1);
                }
            }
            int endIdx = xml.indexOf(62, startIdx);
            if (!xml.substring(startIdx, endIdx).contains("xmlns")) {
                xml = xml.replaceFirst("<([a-z_]+)\\s*(/?>)", "<$1 xmlns=\"" + namespace + "\"$2");
            }
            in.setCharacterStream(new StringReader(xml));
        }
    }

    private static long copyLarge(Reader reader, Writer writer) throws IOException {
        char[] buffer = new char[Opcodes.ACC_SYNTHETIC];
        long count = 0;
        while (true) {
            int n = reader.read(buffer);
            if (-1 != n) {
                writer.write(buffer, 0, n);
                count += n;
            } else {
                return count;
            }
        }
    }

    private static void unmarshal0(Object result, Element element, Map<Class<?>, Map<String, Field>> fieldsByClass) throws Exception {
        if (result == null) {
            return;
        }
        Map<String, Field> fieldsByElementName = fieldsByElementName(fieldsByClass, result.getClass());
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            unmarshal1(result, item, fieldsByClass, fieldsByElementName);
        }
        NamedNodeMap attributes = element.getAttributes();
        for (int i2 = 0; i2 < attributes.getLength(); i2++) {
            Node item2 = attributes.item(i2);
            unmarshal1(result, item2, fieldsByClass, fieldsByElementName);
        }
    }

    private static void unmarshal1(Object result, Node item, Map<Class<?>, Map<String, Field>> fieldsByClass, Map<String, Field> fieldsByName) throws Exception {
        Field child = null;
        Element childElement = null;
        String textContent = null;
        if (item.getNodeType() == 1) {
            childElement = (Element) item;
            child = fieldsByName.remove(childElement.getTagName());
            if (child == null) {
                child = fieldsByName.remove(childElement.getLocalName());
            }
            if (child != null) {
                textContent = childElement.getTextContent();
            }
        } else if (item.getNodeType() == 2) {
            Attr childAttr = (Attr) item;
            child = fieldsByName.remove(childAttr.getName());
            if (child == null) {
                child = fieldsByName.remove(childAttr.getLocalName());
            }
            if (child != null) {
                textContent = childAttr.getValue();
            }
        }
        if (child == null) {
            return;
        }
        XmlElementWrapper w = child.getAnnotation(XmlElementWrapper.class);
        XmlElement e = child.getAnnotation(XmlElement.class);
        XmlJavaTypeAdapter a = child.getAnnotation(XmlJavaTypeAdapter.class);
        XmlList l = child.getAnnotation(XmlList.class);
        String childName = child.getName();
        Class<?> childType = child.getType();
        if (List.class.isAssignableFrom(childType) && w != null && e != null) {
            if (childElement == null) {
                return;
            }
            List<Object> list = new ArrayList<>();
            unmarshalList0(list, childElement, e.name(), (Class) ((ParameterizedType) child.getGenericType()).getActualTypeArguments()[0], fieldsByClass);
            Reflect.on(result).set(childName, list);
            return;
        }
        if (List.class.isAssignableFrom(childType) && l != null) {
            if (childElement == null) {
                return;
            }
            Reflect.on(result).set(childName, Convert.convert((Collection<?>) new ArrayList<>(Arrays.asList(childElement.getTextContent().split(" +"))), (Class) ((ParameterizedType) child.getGenericType()).getActualTypeArguments()[0]));
        } else {
            if (getAnnotation(childType, XmlEnum.class) != null) {
                Reflect.on(result).set(childName, Reflect.onClass(childType).call("fromValue", textContent.trim()));
                return;
            }
            if (getAnnotation(childType, XmlType.class) != null) {
                Object object = Reflect.on(childType).create().get();
                Reflect.on(result).set(childName, object);
                unmarshal0(object, childElement, fieldsByClass);
            } else if (a != null) {
                XmlAdapter<Object, Object> adapter = (XmlAdapter) a.value().getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                Reflect.on(result).set(childName, adapter.unmarshal(textContent.trim()));
            } else {
                Reflect.on(result).set(childName, Convert.convert(textContent.trim(), childType));
            }
        }
    }

    private static void unmarshalList0(List<Object> result, Element element, String name, Class<?> type, Map<Class<?>, Map<String, Field>> fieldsByClass) throws Exception {
        if (result == null) {
            return;
        }
        boolean isComplexType = getAnnotation(type, XmlType.class) != null;
        NodeList list = element.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node item = list.item(i);
            if (item.getNodeType() == 1 && (name.equals(((Element) item).getTagName()) || name.equals(((Element) item).getLocalName()))) {
                if (isComplexType) {
                    Object o = Reflect.on(type).create().get();
                    unmarshal0(o, (Element) item, fieldsByClass);
                    result.add(o);
                } else {
                    result.add(Convert.convert(item.getTextContent().trim(), type));
                }
            }
        }
    }

    private static Map<String, Field> fieldsByElementName(Map<Class<?>, Map<String, Field>> fieldsByClass, Class<?> type) {
        String str;
        Map<String, Field> result = fieldsByClass.get(type);
        if (result == null) {
            result = new HashMap();
            fieldsByClass.put(type, result);
            for (Field child : type.getDeclaredFields()) {
                int modifiers = child.getModifiers();
                if (!Modifier.isFinal(modifiers) && !Modifier.isStatic(modifiers)) {
                    XmlElementWrapper w = child.getAnnotation(XmlElementWrapper.class);
                    XmlElement e = child.getAnnotation(XmlElement.class);
                    String childName = child.getName();
                    if (w != null) {
                        if ("##default".equals(w.name())) {
                            str = child.getName();
                        } else {
                            str = w.name();
                        }
                    } else if (e == null || "##default".equals(e.name())) {
                        str = childName;
                    } else {
                        str = e.name();
                    }
                    String childElementName = str;
                    result.put(childElementName, child);
                }
            }
        }
        return new HashMap(result);
    }

    private static DocumentBuilder builder(Class<?> type) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            try {
                factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            } catch (ParserConfigurationException e) {
            }
            try {
                factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            } catch (ParserConfigurationException e2) {
            }
            try {
                factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            } catch (ParserConfigurationException e3) {
            }
            try {
                factory.setXIncludeAware(false);
            } catch (UnsupportedOperationException e4) {
            }
            String namespace = getNamespace(type);
            if (namespace != null) {
                try {
                    Schema schema = getSchema(type, namespace);
                    factory.setSchema(schema);
                } catch (UnsupportedOperationException e5) {
                }
            }
            factory.setExpandEntityReferences(false);
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new ErrorHandler() { // from class: org.jooq.util.jaxb.tools.MiniJAXB.1
                @Override // org.xml.sax.ErrorHandler
                public void warning(SAXParseException exception) throws SAXException {
                    MiniJAXB.log.warn(exception);
                }

                @Override // org.xml.sax.ErrorHandler
                public void fatalError(SAXParseException exception) throws SAXException {
                    MiniJAXB.log.warn(exception);
                }

                @Override // org.xml.sax.ErrorHandler
                public void error(SAXParseException exception) throws SAXException {
                    MiniJAXB.log.warn(exception);
                }
            });
            return builder;
        } catch (Exception e6) {
            throw new RuntimeException(e6);
        }
    }

    private static String getNamespace(Class<?> type) {
        if (type != null && type.getPackage() != null && type.getPackage().isAnnotationPresent(XmlSchema.class)) {
            return type.getPackage().getAnnotation(XmlSchema.class).namespace();
        }
        return null;
    }

    private static Schema getSchema(Class<?> type, String namespace) {
        URL url;
        try {
            if (PROVIDED_SCHEMAS.containsKey(namespace)) {
                url = type.getResource(PROVIDED_SCHEMAS.get(namespace));
            } else {
                url = new URL(namespace);
            }
            if (url != null) {
                SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
                Schema schema = schemaFactory.newSchema(url);
                return schema;
            }
            return null;
        } catch (Exception e) {
            log.warn((Object) ("Failed to load schema for namespace " + namespace), (Throwable) e);
            return null;
        }
    }

    public static <T> T append(T first, T second) {
        Method defaultsGetter;
        Method firstGetter;
        Method secondGetter;
        if (first == null) {
            return second;
        }
        if (second == null) {
            return first;
        }
        Class<?> cls = first.getClass();
        Class<?> cls2 = second.getClass();
        if (!cls.isAssignableFrom(cls2) && !cls2.isAssignableFrom(cls)) {
            throw new IllegalArgumentException("Can only append compatible types");
        }
        if (cls.isEnum()) {
            return first;
        }
        Package pkg = cls.getPackage();
        try {
            Class<T> defaultsClass = nonGradleExtensionClass(cls);
            T defaults = defaultsClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            for (Method setter : cls.getMethods()) {
                if (setter.getName().startsWith("set") && setter.getParameterCount() == 1) {
                    try {
                        defaultsClass.getMethod(setter.getName(), setter.getParameterTypes());
                        try {
                            defaultsGetter = defaultsClass.getMethod("get" + setter.getName().substring(3), new Class[0]);
                            firstGetter = cls.getMethod("get" + setter.getName().substring(3), new Class[0]);
                            secondGetter = cls2.getMethod("get" + setter.getName().substring(3), new Class[0]);
                        } catch (NoSuchMethodException e) {
                            defaultsGetter = defaultsClass.getMethod("is" + setter.getName().substring(3), new Class[0]);
                            firstGetter = cls.getMethod("is" + setter.getName().substring(3), new Class[0]);
                            secondGetter = cls.getMethod("is" + setter.getName().substring(3), new Class[0]);
                        }
                        Class<?> childType = setter.getParameterTypes()[0];
                        Object firstChild = firstGetter.invoke(first, new Object[0]);
                        Object secondChild = secondGetter.invoke(second, new Object[0]);
                        Object defaultChild = defaults != null ? defaultsGetter.invoke(defaults, new Object[0]) : null;
                        if (Collection.class.isAssignableFrom(childType)) {
                            ((List) firstChild).addAll((List) secondChild);
                        } else if (secondChild != null && (firstChild == null || firstChild.equals(defaultChild))) {
                            setter.invoke(first, secondChild);
                        } else if (secondChild != null && pkg == childType.getPackage()) {
                            append(firstChild, secondChild);
                        }
                    } catch (NoSuchMethodException e2) {
                    }
                }
            }
            return first;
        } catch (Exception e3) {
            throw new ReflectException(e3);
        }
    }

    private static <T> Class<T> nonGradleExtensionClass(Class<T> klass) {
        while (klass.getName().startsWith("org.jooq.codegen.gradle")) {
            klass = klass.getSuperclass();
        }
        return klass;
    }

    private static <A extends Annotation> A getAnnotation(Class<?> cls, Class<A> cls2) {
        return (A) nonGradleExtensionClass(cls).getAnnotation(cls2);
    }
}
