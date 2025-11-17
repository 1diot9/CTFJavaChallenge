package org.apache.catalina.realm;

import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextInputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/realm/JAASCallbackHandler.class */
public class JAASCallbackHandler implements CallbackHandler {
    protected static final StringManager sm = StringManager.getManager((Class<?>) JAASCallbackHandler.class);
    protected final String password;
    protected final JAASRealm realm;
    protected final String username;
    protected final String nonce;
    protected final String nc;
    protected final String cnonce;
    protected final String qop;
    protected final String realmName;
    protected final String digestA2;
    protected final String authMethod;

    public JAASCallbackHandler(JAASRealm realm, String username, String password) {
        this(realm, username, password, null, null, null, null, null, null, null, null);
    }

    public JAASCallbackHandler(JAASRealm realm, String username, String password, String nonce, String nc, String cnonce, String qop, String realmName, String digestA2, String algorithm, String authMethod) {
        this.realm = realm;
        this.username = username;
        if (password != null && realm.hasMessageDigest(algorithm)) {
            this.password = realm.getCredentialHandler().mutate(password);
        } else {
            this.password = password;
        }
        this.nonce = nonce;
        this.nc = nc;
        this.cnonce = cnonce;
        this.qop = qop;
        this.realmName = realmName;
        this.digestA2 = digestA2;
        this.authMethod = authMethod;
    }

    @Override // javax.security.auth.callback.CallbackHandler
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        char[] passwordcontents;
        for (Callback callback : callbacks) {
            if (callback instanceof NameCallback) {
                if (this.realm.getContainer().getLogger().isTraceEnabled()) {
                    this.realm.getContainer().getLogger().trace(sm.getString("jaasCallback.username", this.username));
                }
                ((NameCallback) callback).setName(this.username);
            } else if (callback instanceof PasswordCallback) {
                if (this.password != null) {
                    passwordcontents = this.password.toCharArray();
                } else {
                    passwordcontents = new char[0];
                }
                ((PasswordCallback) callback).setPassword(passwordcontents);
            } else if (callback instanceof TextInputCallback) {
                TextInputCallback cb = (TextInputCallback) callback;
                if (cb.getPrompt().equals("nonce")) {
                    cb.setText(this.nonce);
                } else if (cb.getPrompt().equals("nc")) {
                    cb.setText(this.nc);
                } else if (cb.getPrompt().equals("cnonce")) {
                    cb.setText(this.cnonce);
                } else if (cb.getPrompt().equals("qop")) {
                    cb.setText(this.qop);
                } else if (cb.getPrompt().equals("realmName")) {
                    cb.setText(this.realmName);
                } else if (cb.getPrompt().equals("digestA2")) {
                    cb.setText(this.digestA2);
                } else if (cb.getPrompt().equals("authMethod")) {
                    cb.setText(this.authMethod);
                } else if (cb.getPrompt().equals("catalinaBase")) {
                    cb.setText(this.realm.getContainer().getCatalinaBase().getAbsolutePath());
                } else {
                    throw new UnsupportedCallbackException(callback);
                }
            } else {
                throw new UnsupportedCallbackException(callback);
            }
        }
    }
}
