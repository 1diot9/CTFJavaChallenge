package org.h2.security.auth.impl;

import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import org.h2.api.CredentialsValidator;
import org.h2.security.auth.AuthenticationInfo;
import org.h2.security.auth.ConfigProperties;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/security/auth/impl/JaasCredentialsValidator.class */
public class JaasCredentialsValidator implements CredentialsValidator {
    public static final String DEFAULT_APPNAME = "h2";
    private String appName;

    public JaasCredentialsValidator() {
        this(DEFAULT_APPNAME);
    }

    public JaasCredentialsValidator(String str) {
        this.appName = str;
    }

    @Override // org.h2.security.auth.Configurable
    public void configure(ConfigProperties configProperties) {
        this.appName = configProperties.getStringValue("appName", this.appName);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/security/auth/impl/JaasCredentialsValidator$AuthenticationInfoCallbackHandler.class */
    static class AuthenticationInfoCallbackHandler implements CallbackHandler {
        AuthenticationInfo authenticationInfo;

        AuthenticationInfoCallbackHandler(AuthenticationInfo authenticationInfo) {
            this.authenticationInfo = authenticationInfo;
        }

        @Override // javax.security.auth.callback.CallbackHandler
        public void handle(Callback[] callbackArr) throws IOException, UnsupportedCallbackException {
            for (int i = 0; i < callbackArr.length; i++) {
                if (callbackArr[i] instanceof NameCallback) {
                    ((NameCallback) callbackArr[i]).setName(this.authenticationInfo.getUserName());
                } else if (callbackArr[i] instanceof PasswordCallback) {
                    ((PasswordCallback) callbackArr[i]).setPassword(this.authenticationInfo.getPassword().toCharArray());
                }
            }
        }
    }

    @Override // org.h2.api.CredentialsValidator
    public boolean validateCredentials(AuthenticationInfo authenticationInfo) throws Exception {
        LoginContext loginContext = new LoginContext(this.appName, new AuthenticationInfoCallbackHandler(authenticationInfo));
        loginContext.login();
        authenticationInfo.setNestedIdentity(loginContext.getSubject());
        return true;
    }
}
