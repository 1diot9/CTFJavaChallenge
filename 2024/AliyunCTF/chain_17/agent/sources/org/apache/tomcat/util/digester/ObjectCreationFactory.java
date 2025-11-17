package org.apache.tomcat.util.digester;

import org.xml.sax.Attributes;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/digester/ObjectCreationFactory.class */
public interface ObjectCreationFactory {
    Object createObject(Attributes attributes) throws Exception;

    Digester getDigester();

    void setDigester(Digester digester);
}
