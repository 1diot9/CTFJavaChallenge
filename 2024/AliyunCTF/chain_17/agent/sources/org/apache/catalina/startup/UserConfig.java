package org.apache.catalina.startup;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.regex.Pattern;
import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/startup/UserConfig.class */
public final class UserConfig implements LifecycleListener {
    private String configClass = "org.apache.catalina.startup.ContextConfig";
    private String contextClass = "org.apache.catalina.core.StandardContext";
    private String directoryName = "public_html";
    private String homeBase = null;
    private Host host = null;
    private String userClass = "org.apache.catalina.startup.PasswdUserDatabase";
    Pattern allow = null;
    Pattern deny = null;
    private static final Log log = LogFactory.getLog((Class<?>) UserConfig.class);
    private static final StringManager sm = StringManager.getManager(Constants.Package);

    public String getConfigClass() {
        return this.configClass;
    }

    public void setConfigClass(String configClass) {
        this.configClass = configClass;
    }

    public String getContextClass() {
        return this.contextClass;
    }

    public void setContextClass(String contextClass) {
        this.contextClass = contextClass;
    }

    public String getDirectoryName() {
        return this.directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    public String getHomeBase() {
        return this.homeBase;
    }

    public void setHomeBase(String homeBase) {
        this.homeBase = homeBase;
    }

    public String getUserClass() {
        return this.userClass;
    }

    public void setUserClass(String userClass) {
        this.userClass = userClass;
    }

    public String getAllow() {
        if (this.allow == null) {
            return null;
        }
        return this.allow.toString();
    }

    public void setAllow(String allow) {
        if (allow == null || allow.length() == 0) {
            this.allow = null;
        } else {
            this.allow = Pattern.compile(allow);
        }
    }

    public String getDeny() {
        if (this.deny == null) {
            return null;
        }
        return this.deny.toString();
    }

    public void setDeny(String deny) {
        if (deny == null || deny.length() == 0) {
            this.deny = null;
        } else {
            this.deny = Pattern.compile(deny);
        }
    }

    @Override // org.apache.catalina.LifecycleListener
    public void lifecycleEvent(LifecycleEvent event) {
        try {
            this.host = (Host) event.getLifecycle();
            if (event.getType().equals(Lifecycle.START_EVENT)) {
                start();
            } else if (event.getType().equals(Lifecycle.STOP_EVENT)) {
                stop();
            }
        } catch (ClassCastException e) {
            log.error(sm.getString("hostConfig.cce", event.getLifecycle()), e);
        }
    }

    private void deploy() {
        if (this.host.getLogger().isDebugEnabled()) {
            this.host.getLogger().debug(sm.getString("userConfig.deploying"));
        }
        try {
            Class<?> clazz = Class.forName(this.userClass);
            UserDatabase database = (UserDatabase) clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
            database.setUserConfig(this);
            ExecutorService executor = this.host.getStartStopExecutor();
            List<Future<?>> results = new ArrayList<>();
            Enumeration<String> users = database.getUsers();
            while (users.hasMoreElements()) {
                String user = users.nextElement();
                if (isDeployAllowed(user)) {
                    String home = database.getHome(user);
                    results.add(executor.submit(new DeployUserDirectory(this, user, home)));
                }
            }
            for (Future<?> result : results) {
                try {
                    result.get();
                } catch (Exception e) {
                    this.host.getLogger().error(sm.getString("userConfig.deploy.threaded.error"), e);
                }
            }
        } catch (Exception e2) {
            this.host.getLogger().error(sm.getString("userConfig.database"), e2);
        }
    }

    private void deploy(String user, String home) {
        String contextPath = "/~" + user;
        if (this.host.findChild(contextPath) != null) {
            return;
        }
        File app = new File(home, this.directoryName);
        if (!app.exists() || !app.isDirectory()) {
            return;
        }
        this.host.getLogger().info(sm.getString("userConfig.deploy", user));
        try {
            Class<?> clazz = Class.forName(this.contextClass);
            Context context = (Context) clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
            context.setPath(contextPath);
            context.setDocBase(app.toString());
            Class<?> clazz2 = Class.forName(this.configClass);
            LifecycleListener listener = (LifecycleListener) clazz2.getConstructor(new Class[0]).newInstance(new Object[0]);
            context.addLifecycleListener(listener);
            this.host.addChild(context);
        } catch (Exception e) {
            this.host.getLogger().error(sm.getString("userConfig.error", user), e);
        }
    }

    private void start() {
        if (this.host.getLogger().isDebugEnabled()) {
            this.host.getLogger().debug(sm.getString("userConfig.start"));
        }
        deploy();
    }

    private void stop() {
        if (this.host.getLogger().isDebugEnabled()) {
            this.host.getLogger().debug(sm.getString("userConfig.stop"));
        }
    }

    private boolean isDeployAllowed(String user) {
        if (this.deny != null && this.deny.matcher(user).matches()) {
            return false;
        }
        if (this.allow == null || this.allow.matcher(user).matches()) {
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/startup/UserConfig$DeployUserDirectory.class */
    public static class DeployUserDirectory implements Runnable {
        private UserConfig config;
        private String user;
        private String home;

        DeployUserDirectory(UserConfig config, String user, String home) {
            this.config = config;
            this.user = user;
            this.home = home;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.config.deploy(this.user, this.home);
        }
    }
}
