package org.apache.catalina;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/Host.class */
public interface Host extends Container {
    public static final String ADD_ALIAS_EVENT = "addAlias";
    public static final String REMOVE_ALIAS_EVENT = "removeAlias";

    String getXmlBase();

    void setXmlBase(String str);

    File getConfigBaseFile();

    String getAppBase();

    File getAppBaseFile();

    void setAppBase(String str);

    String getLegacyAppBase();

    File getLegacyAppBaseFile();

    void setLegacyAppBase(String str);

    boolean getAutoDeploy();

    void setAutoDeploy(boolean z);

    String getConfigClass();

    void setConfigClass(String str);

    boolean getDeployOnStartup();

    void setDeployOnStartup(boolean z);

    String getDeployIgnore();

    Pattern getDeployIgnorePattern();

    void setDeployIgnore(String str);

    ExecutorService getStartStopExecutor();

    boolean getCreateDirs();

    void setCreateDirs(boolean z);

    boolean getUndeployOldVersions();

    void setUndeployOldVersions(boolean z);

    void addAlias(String str);

    String[] findAliases();

    void removeAlias(String str);
}
