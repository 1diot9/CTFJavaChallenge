package org.springframework.boot.autoconfigure.security.oauth2.client;

import java.util.Map;
import org.springframework.security.oauth2.client.registration.ClientRegistration;

@Deprecated(since = "3.1.0", forRemoval = true)
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/oauth2/client/OAuth2ClientPropertiesRegistrationAdapter.class */
public final class OAuth2ClientPropertiesRegistrationAdapter {
    private OAuth2ClientPropertiesRegistrationAdapter() {
    }

    public static Map<String, ClientRegistration> getClientRegistrations(OAuth2ClientProperties properties) {
        return new OAuth2ClientPropertiesMapper(properties).asClientRegistrations();
    }
}
