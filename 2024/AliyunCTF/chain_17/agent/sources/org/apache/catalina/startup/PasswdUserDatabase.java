package org.apache.catalina.startup;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.naming.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/startup/PasswdUserDatabase.class */
public final class PasswdUserDatabase implements UserDatabase {
    private static final Log log = LogFactory.getLog((Class<?>) PasswdUserDatabase.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) PasswdUserDatabase.class);
    private static final String PASSWORD_FILE = "/etc/passwd";
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
        try {
            BufferedReader reader = new BufferedReader(new FileReader(PASSWORD_FILE));
            try {
                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    String[] tokens = line.split(":");
                    if (tokens.length > 5 && tokens[0].length() > 0 && tokens[5].length() > 0) {
                        this.homes.put(tokens[0], tokens[5]);
                    }
                }
                reader.close();
            } finally {
            }
        } catch (Exception e) {
            log.warn(sm.getString("passwdUserDatabase.readFail"), e);
        }
    }
}
