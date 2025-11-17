package org.apache.catalina.startup;

import java.util.Enumeration;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/startup/UserDatabase.class */
public interface UserDatabase {
    UserConfig getUserConfig();

    void setUserConfig(UserConfig userConfig);

    String getHome(String str);

    Enumeration<String> getUsers();
}
