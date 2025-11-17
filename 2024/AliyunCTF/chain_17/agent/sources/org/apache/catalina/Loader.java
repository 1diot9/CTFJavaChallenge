package org.apache.catalina;

import java.beans.PropertyChangeListener;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/Loader.class */
public interface Loader {
    void backgroundProcess();

    ClassLoader getClassLoader();

    Context getContext();

    void setContext(Context context);

    boolean getDelegate();

    void setDelegate(boolean z);

    void addPropertyChangeListener(PropertyChangeListener propertyChangeListener);

    boolean modified();

    void removePropertyChangeListener(PropertyChangeListener propertyChangeListener);
}
