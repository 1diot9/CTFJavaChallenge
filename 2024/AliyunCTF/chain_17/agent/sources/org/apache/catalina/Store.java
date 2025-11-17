package org.apache.catalina;

import java.beans.PropertyChangeListener;
import java.io.IOException;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/Store.class */
public interface Store {
    Manager getManager();

    void setManager(Manager manager);

    int getSize() throws IOException;

    void addPropertyChangeListener(PropertyChangeListener propertyChangeListener);

    String[] keys() throws IOException;

    Session load(String str) throws ClassNotFoundException, IOException;

    void remove(String str) throws IOException;

    void clear() throws IOException;

    void removePropertyChangeListener(PropertyChangeListener propertyChangeListener);

    void save(Session session) throws IOException;
}
