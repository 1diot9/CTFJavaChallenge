package org.springframework.util.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ContentHandler;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/xml/DomUtils.class */
public abstract class DomUtils {
    public static List<Element> getChildElementsByTagName(Element ele, String... childEleNames) {
        Assert.notNull(ele, "Element must not be null");
        Assert.notNull(childEleNames, "Element names collection must not be null");
        List<String> childEleNameList = Arrays.asList(childEleNames);
        NodeList nl = ele.getChildNodes();
        List<Element> childEles = new ArrayList<>();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node instanceof Element) {
                Element element = (Element) node;
                if (nodeNameMatch(node, childEleNameList)) {
                    childEles.add(element);
                }
            }
        }
        return childEles;
    }

    public static List<Element> getChildElementsByTagName(Element ele, String childEleName) {
        return getChildElementsByTagName(ele, childEleName);
    }

    @Nullable
    public static Element getChildElementByTagName(Element ele, String childEleName) {
        Assert.notNull(ele, "Element must not be null");
        Assert.notNull(childEleName, "Element name must not be null");
        NodeList nl = ele.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node instanceof Element) {
                Element element = (Element) node;
                if (nodeNameMatch(node, childEleName)) {
                    return element;
                }
            }
        }
        return null;
    }

    @Nullable
    public static String getChildElementValueByTagName(Element ele, String childEleName) {
        Element child = getChildElementByTagName(ele, childEleName);
        if (child != null) {
            return getTextValue(child);
        }
        return null;
    }

    public static List<Element> getChildElements(Element ele) {
        Assert.notNull(ele, "Element must not be null");
        NodeList nl = ele.getChildNodes();
        List<Element> childEles = new ArrayList<>();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node instanceof Element) {
                Element element = (Element) node;
                childEles.add(element);
            }
        }
        return childEles;
    }

    public static String getTextValue(Element valueEle) {
        Assert.notNull(valueEle, "Element must not be null");
        StringBuilder sb = new StringBuilder();
        NodeList nl = valueEle.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node item = nl.item(i);
            if (((item instanceof CharacterData) && !(item instanceof Comment)) || (item instanceof EntityReference)) {
                sb.append(item.getNodeValue());
            }
        }
        return sb.toString();
    }

    public static boolean nodeNameEquals(Node node, String desiredName) {
        Assert.notNull(node, "Node must not be null");
        Assert.notNull(desiredName, "Desired name must not be null");
        return nodeNameMatch(node, desiredName);
    }

    public static ContentHandler createContentHandler(Node node) {
        return new DomContentHandler(node);
    }

    private static boolean nodeNameMatch(Node node, String desiredName) {
        return desiredName.equals(node.getNodeName()) || desiredName.equals(node.getLocalName());
    }

    private static boolean nodeNameMatch(Node node, Collection<?> desiredNames) {
        return desiredNames.contains(node.getNodeName()) || desiredNames.contains(node.getLocalName());
    }
}
