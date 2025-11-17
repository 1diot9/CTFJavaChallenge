package org.apache.catalina.authenticator.jaspic;

import jakarta.security.auth.message.callback.CallerPrincipalCallback;
import jakarta.security.auth.message.callback.GroupPrincipalCallback;
import jakarta.security.auth.message.callback.PasswordValidationCallback;
import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import org.apache.catalina.Contained;
import org.apache.catalina.Container;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/authenticator/jaspic/CallbackHandlerImpl.class */
public class CallbackHandlerImpl implements CallbackHandler, Contained {
    private static final StringManager sm = StringManager.getManager((Class<?>) CallbackHandlerImpl.class);
    private final Log log = LogFactory.getLog((Class<?>) CallbackHandlerImpl.class);
    private Container container;

    @Override // javax.security.auth.callback.CallbackHandler
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        String name = null;
        Principal principal = null;
        Subject subject = null;
        String[] groups = null;
        if (callbacks != null) {
            for (Callback callback : callbacks) {
                if (callback instanceof CallerPrincipalCallback) {
                    CallerPrincipalCallback cpc = (CallerPrincipalCallback) callback;
                    name = cpc.getName();
                    principal = cpc.getPrincipal();
                    subject = cpc.getSubject();
                } else if (callback instanceof GroupPrincipalCallback) {
                    GroupPrincipalCallback gpc = (GroupPrincipalCallback) callback;
                    groups = gpc.getGroups();
                } else if (callback instanceof PasswordValidationCallback) {
                    if (this.container == null) {
                        this.log.warn(sm.getString("callbackHandlerImpl.containerMissing", callback.getClass().getName()));
                    } else if (this.container.getRealm() == null) {
                        this.log.warn(sm.getString("callbackHandlerImpl.realmMissing", callback.getClass().getName(), this.container.getName()));
                    } else {
                        PasswordValidationCallback pvc = (PasswordValidationCallback) callback;
                        principal = this.container.getRealm().authenticate(pvc.getUsername(), String.valueOf(pvc.getPassword()));
                        pvc.setResult(principal != null);
                        subject = pvc.getSubject();
                    }
                } else {
                    this.log.error(sm.getString("callbackHandlerImpl.jaspicCallbackMissing", callback.getClass().getName()));
                }
            }
            Principal gp = getPrincipal(principal, name, groups);
            if (subject != null && gp != null) {
                subject.getPrivateCredentials().add(gp);
            }
        }
    }

    private Principal getPrincipal(Principal principal, String name, String[] groups) {
        List<String> roles;
        if (principal instanceof GenericPrincipal) {
            return principal;
        }
        if (name == null && principal != null) {
            name = principal.getName();
        }
        if (name == null) {
            return null;
        }
        if (groups == null || groups.length == 0) {
            roles = Collections.emptyList();
        } else {
            roles = Arrays.asList(groups);
        }
        return new GenericPrincipal(name, roles, principal);
    }

    @Override // org.apache.catalina.Contained
    public Container getContainer() {
        return this.container;
    }

    @Override // org.apache.catalina.Contained
    public void setContainer(Container container) {
        this.container = container;
    }
}
