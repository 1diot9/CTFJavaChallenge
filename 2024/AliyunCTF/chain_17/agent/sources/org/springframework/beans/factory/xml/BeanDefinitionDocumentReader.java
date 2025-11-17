package org.springframework.beans.factory.xml;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.w3c.dom.Document;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/xml/BeanDefinitionDocumentReader.class */
public interface BeanDefinitionDocumentReader {
    void registerBeanDefinitions(Document doc, XmlReaderContext readerContext) throws BeanDefinitionStoreException;
}
