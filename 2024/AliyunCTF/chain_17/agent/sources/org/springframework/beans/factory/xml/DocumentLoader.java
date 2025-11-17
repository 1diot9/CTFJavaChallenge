package org.springframework.beans.factory.xml;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/xml/DocumentLoader.class */
public interface DocumentLoader {
    Document loadDocument(InputSource inputSource, EntityResolver entityResolver, ErrorHandler errorHandler, int validationMode, boolean namespaceAware) throws Exception;
}
