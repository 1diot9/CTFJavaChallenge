package org.apache.catalina.startup;

import java.io.File;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/startup/HomesUserDatabase.class */
public final class HomesUserDatabase implements UserDatabase {
    private final Map<String, String> homes = new HashMap();
    private UserConfig userConfig = null;

    @Override // org.apache.catalina.startup.UserDatabase
    public UserConfig getUserConfig() {
        return this.userConfig;
    }

    @Override // org.apache.catalina.startup.UserDatabase
    public void setUserConfig(UserConfig userConfig) {
        this.userConfig = userConfig;
        init();
    }

    @Override // org.apache.catalina.startup.UserDatabase
    public String getHome(String user) {
        return this.homes.get(user);
    }

    @Override // org.apache.catalina.startup.UserDatabase
    public Enumeration<String> getUsers() {
        return Collections.enumeration(this.homes.keySet());
    }

    private void init() {
        String[] homeBaseFiles;
        String homeBase = this.userConfig.getHomeBase();
        File homeBaseDir = new File(homeBase);
        if (!homeBaseDir.exists() || !homeBaseDir.isDirectory() || (homeBaseFiles = homeBaseDir.list()) == null) {
            return;
        }
        for (String homeBaseFile : homeBaseFiles) {
            File homeDir = new File(homeBaseDir, homeBaseFile);
            if (homeDir.isDirectory() && homeDir.canRead()) {
                this.homes.put(homeBaseFile, homeDir.toString());
            }
        }
    }
}
