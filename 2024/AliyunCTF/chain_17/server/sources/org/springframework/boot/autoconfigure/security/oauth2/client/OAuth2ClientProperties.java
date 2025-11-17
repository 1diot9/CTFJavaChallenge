package org.springframework.boot.autoconfigure.security.oauth2.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "spring.security.oauth2.client")
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/oauth2/client/OAuth2ClientProperties.class */
public class OAuth2ClientProperties implements InitializingBean {
    private final Map<String, Provider> provider = new HashMap();
    private final Map<String, Registration> registration = new HashMap();

    public Map<String, Provider> getProvider() {
        return this.provider;
    }

    public Map<String, Registration> getRegistration() {
        return this.registration;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        validate();
    }

    public void validate() {
        getRegistration().values().forEach(this::validateRegistration);
    }

    private void validateRegistration(Registration registration) {
        if (!StringUtils.hasText(registration.getClientId())) {
            throw new IllegalStateException("Client id must not be empty.");
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/oauth2/client/OAuth2ClientProperties$Registration.class */
    public static class Registration {
        private String provider;
        private String clientId;
        private String clientSecret;
        private String clientAuthenticationMethod;
        private String authorizationGrantType;
        private String redirectUri;
        private Set<String> scope;
        private String clientName;

        public String getProvider() {
            return this.provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public String getClientId() {
            return this.clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getClientSecret() {
            return this.clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }

        public String getClientAuthenticationMethod() {
            return this.clientAuthenticationMethod;
        }

        public void setClientAuthenticationMethod(String clientAuthenticationMethod) {
            this.clientAuthenticationMethod = clientAuthenticationMethod;
        }

        public String getAuthorizationGrantType() {
            return this.authorizationGrantType;
        }

        public void setAuthorizationGrantType(String authorizationGrantType) {
            this.authorizationGrantType = authorizationGrantType;
        }

        public String getRedirectUri() {
            return this.redirectUri;
        }

        public void setRedirectUri(String redirectUri) {
            this.redirectUri = redirectUri;
        }

        public Set<String> getScope() {
            return this.scope;
        }

        public void setScope(Set<String> scope) {
            this.scope = scope;
        }

        public String getClientName() {
            return this.clientName;
        }

        public void setClientName(String clientName) {
            this.clientName = clientName;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/oauth2/client/OAuth2ClientProperties$Provider.class */
    public static class Provider {
        private String authorizationUri;
        private String tokenUri;
        private String userInfoUri;
        private String userInfoAuthenticationMethod;
        private String userNameAttribute;
        private String jwkSetUri;
        private String issuerUri;

        public String getAuthorizationUri() {
            return this.authorizationUri;
        }

        public void setAuthorizationUri(String authorizationUri) {
            this.authorizationUri = authorizationUri;
        }

        public String getTokenUri() {
            return this.tokenUri;
        }

        public void setTokenUri(String tokenUri) {
            this.tokenUri = tokenUri;
        }

        public String getUserInfoUri() {
            return this.userInfoUri;
        }

        public void setUserInfoUri(String userInfoUri) {
            this.userInfoUri = userInfoUri;
        }

        public String getUserInfoAuthenticationMethod() {
            return this.userInfoAuthenticationMethod;
        }

        public void setUserInfoAuthenticationMethod(String userInfoAuthenticationMethod) {
            this.userInfoAuthenticationMethod = userInfoAuthenticationMethod;
        }

        public String getUserNameAttribute() {
            return this.userNameAttribute;
        }

        public void setUserNameAttribute(String userNameAttribute) {
            this.userNameAttribute = userNameAttribute;
        }

        public String getJwkSetUri() {
            return this.jwkSetUri;
        }

        public void setJwkSetUri(String jwkSetUri) {
            this.jwkSetUri = jwkSetUri;
        }

        public String getIssuerUri() {
            return this.issuerUri;
        }

        public void setIssuerUri(String issuerUri) {
            this.issuerUri = issuerUri;
        }
    }
}
