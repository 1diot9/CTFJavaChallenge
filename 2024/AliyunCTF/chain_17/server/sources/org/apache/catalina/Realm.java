package org.apache.catalina;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.security.Principal;
import java.security.cert.X509Certificate;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSName;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/Realm.class */
public interface Realm extends Contained {
    CredentialHandler getCredentialHandler();

    void setCredentialHandler(CredentialHandler credentialHandler);

    void addPropertyChangeListener(PropertyChangeListener propertyChangeListener);

    Principal authenticate(String str);

    Principal authenticate(String str, String str2);

    @Deprecated
    Principal authenticate(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    Principal authenticate(GSSContext gSSContext, boolean z);

    Principal authenticate(GSSName gSSName, GSSCredential gSSCredential);

    Principal authenticate(X509Certificate[] x509CertificateArr);

    void backgroundProcess();

    SecurityConstraint[] findSecurityConstraints(Request request, Context context);

    boolean hasResourcePermission(Request request, Response response, SecurityConstraint[] securityConstraintArr, Context context) throws IOException;

    boolean hasRole(Wrapper wrapper, Principal principal, String str);

    boolean hasUserDataPermission(Request request, Response response, SecurityConstraint[] securityConstraintArr) throws IOException;

    void removePropertyChangeListener(PropertyChangeListener propertyChangeListener);

    default Principal authenticate(String username, String digest, String nonce, String nc, String cnonce, String qop, String realm, String digestA2, String algorithm) {
        return authenticate(username, digest, nonce, nc, cnonce, qop, realm, digestA2);
    }

    default boolean isAvailable() {
        return true;
    }
}
