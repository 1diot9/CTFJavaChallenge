package org.springframework.boot.autoconfigure.security.oauth2.server.servlet;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "spring.security.oauth2.authorizationserver")
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/oauth2/server/servlet/OAuth2AuthorizationServerProperties.class */
public class OAuth2AuthorizationServerProperties implements InitializingBean {
    private String issuer;
    private final Map<String, Client> client = new HashMap();
    private final Endpoint endpoint = new Endpoint();

    public String getIssuer() {
        return this.issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public Map<String, Client> getClient() {
        return this.client;
    }

    public Endpoint getEndpoint() {
        return this.endpoint;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        validate();
    }

    public void validate() {
        getClient().values().forEach(this::validateClient);
    }

    private void validateClient(Client client) {
        if (!StringUtils.hasText(client.getRegistration().getClientId())) {
            throw new IllegalStateException("Client id must not be empty.");
        }
        if (CollectionUtils.isEmpty(client.getRegistration().getClientAuthenticationMethods())) {
            throw new IllegalStateException("Client authentication methods must not be empty.");
        }
        if (CollectionUtils.isEmpty(client.getRegistration().getAuthorizationGrantTypes())) {
            throw new IllegalStateException("Authorization grant types must not be empty.");
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/oauth2/server/servlet/OAuth2AuthorizationServerProperties$Endpoint.class */
    public static class Endpoint {
        private String authorizationUri = "/oauth2/authorize";
        private String deviceAuthorizationUri = "/oauth2/device_authorization";
        private String deviceVerificationUri = "/oauth2/device_verification";
        private String tokenUri = "/oauth2/token";
        private String jwkSetUri = "/oauth2/jwks";
        private String tokenRevocationUri = "/oauth2/revoke";
        private String tokenIntrospectionUri = "/oauth2/introspect";

        @NestedConfigurationProperty
        private final OidcEndpoint oidc = new OidcEndpoint();

        public String getAuthorizationUri() {
            return this.authorizationUri;
        }

        public void setAuthorizationUri(String authorizationUri) {
            this.authorizationUri = authorizationUri;
        }

        public String getDeviceAuthorizationUri() {
            return this.deviceAuthorizationUri;
        }

        public void setDeviceAuthorizationUri(String deviceAuthorizationUri) {
            this.deviceAuthorizationUri = deviceAuthorizationUri;
        }

        public String getDeviceVerificationUri() {
            return this.deviceVerificationUri;
        }

        public void setDeviceVerificationUri(String deviceVerificationUri) {
            this.deviceVerificationUri = deviceVerificationUri;
        }

        public String getTokenUri() {
            return this.tokenUri;
        }

        public void setTokenUri(String tokenUri) {
            this.tokenUri = tokenUri;
        }

        public String getJwkSetUri() {
            return this.jwkSetUri;
        }

        public void setJwkSetUri(String jwkSetUri) {
            this.jwkSetUri = jwkSetUri;
        }

        public String getTokenRevocationUri() {
            return this.tokenRevocationUri;
        }

        public void setTokenRevocationUri(String tokenRevocationUri) {
            this.tokenRevocationUri = tokenRevocationUri;
        }

        public String getTokenIntrospectionUri() {
            return this.tokenIntrospectionUri;
        }

        public void setTokenIntrospectionUri(String tokenIntrospectionUri) {
            this.tokenIntrospectionUri = tokenIntrospectionUri;
        }

        public OidcEndpoint getOidc() {
            return this.oidc;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/oauth2/server/servlet/OAuth2AuthorizationServerProperties$OidcEndpoint.class */
    public static class OidcEndpoint {
        private String logoutUri = "/connect/logout";
        private String clientRegistrationUri = "/connect/register";
        private String userInfoUri = "/userinfo";

        public String getLogoutUri() {
            return this.logoutUri;
        }

        public void setLogoutUri(String logoutUri) {
            this.logoutUri = logoutUri;
        }

        public String getClientRegistrationUri() {
            return this.clientRegistrationUri;
        }

        public void setClientRegistrationUri(String clientRegistrationUri) {
            this.clientRegistrationUri = clientRegistrationUri;
        }

        public String getUserInfoUri() {
            return this.userInfoUri;
        }

        public void setUserInfoUri(String userInfoUri) {
            this.userInfoUri = userInfoUri;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/oauth2/server/servlet/OAuth2AuthorizationServerProperties$Client.class */
    public static class Client {
        private String jwkSetUri;
        private String tokenEndpointAuthenticationSigningAlgorithm;

        @NestedConfigurationProperty
        private final Registration registration = new Registration();
        private boolean requireProofKey = false;
        private boolean requireAuthorizationConsent = false;

        @NestedConfigurationProperty
        private final Token token = new Token();

        public Registration getRegistration() {
            return this.registration;
        }

        public boolean isRequireProofKey() {
            return this.requireProofKey;
        }

        public void setRequireProofKey(boolean requireProofKey) {
            this.requireProofKey = requireProofKey;
        }

        public boolean isRequireAuthorizationConsent() {
            return this.requireAuthorizationConsent;
        }

        public void setRequireAuthorizationConsent(boolean requireAuthorizationConsent) {
            this.requireAuthorizationConsent = requireAuthorizationConsent;
        }

        public String getJwkSetUri() {
            return this.jwkSetUri;
        }

        public void setJwkSetUri(String jwkSetUri) {
            this.jwkSetUri = jwkSetUri;
        }

        public String getTokenEndpointAuthenticationSigningAlgorithm() {
            return this.tokenEndpointAuthenticationSigningAlgorithm;
        }

        public void setTokenEndpointAuthenticationSigningAlgorithm(String tokenEndpointAuthenticationSigningAlgorithm) {
            this.tokenEndpointAuthenticationSigningAlgorithm = tokenEndpointAuthenticationSigningAlgorithm;
        }

        public Token getToken() {
            return this.token;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/oauth2/server/servlet/OAuth2AuthorizationServerProperties$Registration.class */
    public static class Registration {
        private String clientId;
        private String clientSecret;
        private String clientName;
        private Set<String> clientAuthenticationMethods = new HashSet();
        private Set<String> authorizationGrantTypes = new HashSet();
        private Set<String> redirectUris = new HashSet();
        private Set<String> postLogoutRedirectUris = new HashSet();
        private Set<String> scopes = new HashSet();

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

        public String getClientName() {
            return this.clientName;
        }

        public void setClientName(String clientName) {
            this.clientName = clientName;
        }

        public Set<String> getClientAuthenticationMethods() {
            return this.clientAuthenticationMethods;
        }

        public void setClientAuthenticationMethods(Set<String> clientAuthenticationMethods) {
            this.clientAuthenticationMethods = clientAuthenticationMethods;
        }

        public Set<String> getAuthorizationGrantTypes() {
            return this.authorizationGrantTypes;
        }

        public void setAuthorizationGrantTypes(Set<String> authorizationGrantTypes) {
            this.authorizationGrantTypes = authorizationGrantTypes;
        }

        public Set<String> getRedirectUris() {
            return this.redirectUris;
        }

        public void setRedirectUris(Set<String> redirectUris) {
            this.redirectUris = redirectUris;
        }

        public Set<String> getPostLogoutRedirectUris() {
            return this.postLogoutRedirectUris;
        }

        public void setPostLogoutRedirectUris(Set<String> postLogoutRedirectUris) {
            this.postLogoutRedirectUris = postLogoutRedirectUris;
        }

        public Set<String> getScopes() {
            return this.scopes;
        }

        public void setScopes(Set<String> scopes) {
            this.scopes = scopes;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/oauth2/server/servlet/OAuth2AuthorizationServerProperties$Token.class */
    public static class Token {
        private Duration authorizationCodeTimeToLive = Duration.ofMinutes(5);
        private Duration accessTokenTimeToLive = Duration.ofMinutes(5);
        private String accessTokenFormat = "self-contained";
        private Duration deviceCodeTimeToLive = Duration.ofMinutes(5);
        private boolean reuseRefreshTokens = true;
        private Duration refreshTokenTimeToLive = Duration.ofMinutes(60);
        private String idTokenSignatureAlgorithm = "RS256";

        public Duration getAuthorizationCodeTimeToLive() {
            return this.authorizationCodeTimeToLive;
        }

        public void setAuthorizationCodeTimeToLive(Duration authorizationCodeTimeToLive) {
            this.authorizationCodeTimeToLive = authorizationCodeTimeToLive;
        }

        public Duration getAccessTokenTimeToLive() {
            return this.accessTokenTimeToLive;
        }

        public void setAccessTokenTimeToLive(Duration accessTokenTimeToLive) {
            this.accessTokenTimeToLive = accessTokenTimeToLive;
        }

        public String getAccessTokenFormat() {
            return this.accessTokenFormat;
        }

        public void setAccessTokenFormat(String accessTokenFormat) {
            this.accessTokenFormat = accessTokenFormat;
        }

        public Duration getDeviceCodeTimeToLive() {
            return this.deviceCodeTimeToLive;
        }

        public void setDeviceCodeTimeToLive(Duration deviceCodeTimeToLive) {
            this.deviceCodeTimeToLive = deviceCodeTimeToLive;
        }

        public boolean isReuseRefreshTokens() {
            return this.reuseRefreshTokens;
        }

        public void setReuseRefreshTokens(boolean reuseRefreshTokens) {
            this.reuseRefreshTokens = reuseRefreshTokens;
        }

        public Duration getRefreshTokenTimeToLive() {
            return this.refreshTokenTimeToLive;
        }

        public void setRefreshTokenTimeToLive(Duration refreshTokenTimeToLive) {
            this.refreshTokenTimeToLive = refreshTokenTimeToLive;
        }

        public String getIdTokenSignatureAlgorithm() {
            return this.idTokenSignatureAlgorithm;
        }

        public void setIdTokenSignatureAlgorithm(String idTokenSignatureAlgorithm) {
            this.idTokenSignatureAlgorithm = idTokenSignatureAlgorithm;
        }
    }
}
