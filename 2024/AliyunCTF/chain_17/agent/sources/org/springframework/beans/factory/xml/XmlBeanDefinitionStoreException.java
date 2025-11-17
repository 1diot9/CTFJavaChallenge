package org.springframework.beans.factory.xml;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/xml/XmlBeanDefinitionStoreException.class */
public class XmlBeanDefinitionStoreException extends BeanDefinitionStoreException {
    public XmlBeanDefinitionStoreException(String resourceDescription, String msg, SAXException cause) {
        super(resourceDescription, msg, cause);
    }

    public int getLineNumber() {
        Throwable cause = getCause();
        if (cause instanceof SAXParseException) {
            SAXParseException parseEx = (SAXParseException) cause;
            return parseEx.getLineNumber();
        }
        return -1;
    }
}
