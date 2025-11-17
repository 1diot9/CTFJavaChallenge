package org.apache.catalina.security;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Server;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.StringUtils;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/security/SecurityListener.class */
public class SecurityListener implements LifecycleListener {
    private static final Log log = LogFactory.getLog((Class<?>) SecurityListener.class);
    private static final StringManager sm = StringManager.getManager(Constants.PACKAGE);
    private static final String UMASK_PROPERTY_NAME = "org.apache.catalina.security.SecurityListener.UMASK";
    private static final String UMASK_FORMAT = "%04o";
    private final Set<String> checkedOsUsers = new HashSet();
    private Integer minimumUmask = 7;

    public SecurityListener() {
        this.checkedOsUsers.add("root");
    }

    @Override // org.apache.catalina.LifecycleListener
    public void lifecycleEvent(LifecycleEvent event) {
        if (event.getType().equals(Lifecycle.BEFORE_INIT_EVENT)) {
            if (!(event.getLifecycle() instanceof Server)) {
                log.warn(sm.getString("listener.notServer", event.getLifecycle().getClass().getSimpleName()));
            }
            doChecks();
        }
    }

    public void setCheckedOsUsers(String userNameList) {
        if (userNameList == null || userNameList.length() == 0) {
            this.checkedOsUsers.clear();
            return;
        }
        String[] userNames = userNameList.split(",");
        for (String userName : userNames) {
            if (userName.length() > 0) {
                this.checkedOsUsers.add(userName.toLowerCase(Locale.getDefault()));
            }
        }
    }

    public String getCheckedOsUsers() {
        return StringUtils.join(this.checkedOsUsers);
    }

    public void setMinimumUmask(String umask) {
        if (umask == null || umask.length() == 0) {
            this.minimumUmask = 0;
        } else {
            this.minimumUmask = Integer.valueOf(umask, 8);
        }
    }

    public String getMinimumUmask() {
        return String.format(UMASK_FORMAT, this.minimumUmask);
    }

    protected void doChecks() {
        checkOsUser();
        checkUmask();
    }

    protected void checkOsUser() {
        String userName = System.getProperty("user.name");
        if (userName != null) {
            String userNameLC = userName.toLowerCase(Locale.getDefault());
            if (this.checkedOsUsers.contains(userNameLC)) {
                throw new Error(sm.getString("SecurityListener.checkUserWarning", userName));
            }
        }
    }

    protected void checkUmask() {
        String prop = System.getProperty(UMASK_PROPERTY_NAME);
        Integer umask = null;
        if (prop != null) {
            try {
                umask = Integer.valueOf(prop, 8);
            } catch (NumberFormatException e) {
                log.warn(sm.getString("SecurityListener.checkUmaskParseFail", prop));
            }
        }
        if (umask == null) {
            if ("\r\n".equals(System.lineSeparator())) {
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("SecurityListener.checkUmaskSkip"));
                    return;
                }
                return;
            } else {
                if (this.minimumUmask.intValue() > 0) {
                    log.warn(sm.getString("SecurityListener.checkUmaskNone", UMASK_PROPERTY_NAME, getMinimumUmask()));
                    return;
                }
                return;
            }
        }
        if ((umask.intValue() & this.minimumUmask.intValue()) != this.minimumUmask.intValue()) {
            throw new Error(sm.getString("SecurityListener.checkUmaskFail", String.format(UMASK_FORMAT, umask), getMinimumUmask()));
        }
    }
}
