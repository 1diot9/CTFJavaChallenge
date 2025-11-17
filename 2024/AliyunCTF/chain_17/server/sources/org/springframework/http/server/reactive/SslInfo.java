package org.springframework.http.server.reactive;

import java.security.cert.X509Certificate;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/SslInfo.class */
public interface SslInfo {
    @Nullable
    String getSessionId();

    @Nullable
    X509Certificate[] getPeerCertificates();
}
