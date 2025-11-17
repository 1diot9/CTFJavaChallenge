package org.springframework.boot.autoconfigure.security.oauth2.server.servlet;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.boot.autoconfigure.security.oauth2.server.servlet.OAuth2AuthorizationServerProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithm;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/oauth2/server/servlet/OAuth2AuthorizationServerPropertiesMapper.class */
final class OAuth2AuthorizationServerPropertiesMapper {
    private final OAuth2AuthorizationServerProperties properties;

    /* JADX INFO: Access modifiers changed from: package-private */
    public OAuth2AuthorizationServerPropertiesMapper(OAuth2AuthorizationServerProperties properties) {
        this.properties = properties;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AuthorizationServerSettings asAuthorizationServerSettings() {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        OAuth2AuthorizationServerProperties.Endpoint endpoint = this.properties.getEndpoint();
        OAuth2AuthorizationServerProperties.OidcEndpoint oidc = endpoint.getOidc();
        AuthorizationServerSettings.Builder builder = AuthorizationServerSettings.builder();
        OAuth2AuthorizationServerProperties oAuth2AuthorizationServerProperties = this.properties;
        Objects.requireNonNull(oAuth2AuthorizationServerProperties);
        PropertyMapper.Source from = map.from(oAuth2AuthorizationServerProperties::getIssuer);
        Objects.requireNonNull(builder);
        from.to(builder::issuer);
        Objects.requireNonNull(endpoint);
        PropertyMapper.Source from2 = map.from(endpoint::getAuthorizationUri);
        Objects.requireNonNull(builder);
        from2.to(builder::authorizationEndpoint);
        Objects.requireNonNull(endpoint);
        PropertyMapper.Source from3 = map.from(endpoint::getDeviceAuthorizationUri);
        Objects.requireNonNull(builder);
        from3.to(builder::deviceAuthorizationEndpoint);
        Objects.requireNonNull(endpoint);
        PropertyMapper.Source from4 = map.from(endpoint::getDeviceVerificationUri);
        Objects.requireNonNull(builder);
        from4.to(builder::deviceVerificationEndpoint);
        Objects.requireNonNull(endpoint);
        PropertyMapper.Source from5 = map.from(endpoint::getTokenUri);
        Objects.requireNonNull(builder);
        from5.to(builder::tokenEndpoint);
        Objects.requireNonNull(endpoint);
        PropertyMapper.Source from6 = map.from(endpoint::getJwkSetUri);
        Objects.requireNonNull(builder);
        from6.to(builder::jwkSetEndpoint);
        Objects.requireNonNull(endpoint);
        PropertyMapper.Source from7 = map.from(endpoint::getTokenRevocationUri);
        Objects.requireNonNull(builder);
        from7.to(builder::tokenRevocationEndpoint);
        Objects.requireNonNull(endpoint);
        PropertyMapper.Source from8 = map.from(endpoint::getTokenIntrospectionUri);
        Objects.requireNonNull(builder);
        from8.to(builder::tokenIntrospectionEndpoint);
        Objects.requireNonNull(oidc);
        PropertyMapper.Source from9 = map.from(oidc::getLogoutUri);
        Objects.requireNonNull(builder);
        from9.to(builder::oidcLogoutEndpoint);
        Objects.requireNonNull(oidc);
        PropertyMapper.Source from10 = map.from(oidc::getClientRegistrationUri);
        Objects.requireNonNull(builder);
        from10.to(builder::oidcClientRegistrationEndpoint);
        Objects.requireNonNull(oidc);
        PropertyMapper.Source from11 = map.from(oidc::getUserInfoUri);
        Objects.requireNonNull(builder);
        from11.to(builder::oidcUserInfoEndpoint);
        return builder.build();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public List<RegisteredClient> asRegisteredClients() {
        List<RegisteredClient> registeredClients = new ArrayList<>();
        this.properties.getClient().forEach((registrationId, client) -> {
            registeredClients.add(getRegisteredClient(registrationId, client));
        });
        return registeredClients;
    }

    private RegisteredClient getRegisteredClient(String registrationId, OAuth2AuthorizationServerProperties.Client client) {
        OAuth2AuthorizationServerProperties.Registration registration = client.getRegistration();
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        RegisteredClient.Builder builder = RegisteredClient.withId(registrationId);
        Objects.requireNonNull(registration);
        PropertyMapper.Source from = map.from(registration::getClientId);
        Objects.requireNonNull(builder);
        from.to(builder::clientId);
        Objects.requireNonNull(registration);
        PropertyMapper.Source from2 = map.from(registration::getClientSecret);
        Objects.requireNonNull(builder);
        from2.to(builder::clientSecret);
        Objects.requireNonNull(registration);
        PropertyMapper.Source from3 = map.from(registration::getClientName);
        Objects.requireNonNull(builder);
        from3.to(builder::clientName);
        registration.getClientAuthenticationMethods().forEach(clientAuthenticationMethod -> {
            PropertyMapper.Source as = map.from((PropertyMapper) clientAuthenticationMethod).as(ClientAuthenticationMethod::new);
            Objects.requireNonNull(builder);
            as.to(builder::clientAuthenticationMethod);
        });
        registration.getAuthorizationGrantTypes().forEach(authorizationGrantType -> {
            PropertyMapper.Source as = map.from((PropertyMapper) authorizationGrantType).as(AuthorizationGrantType::new);
            Objects.requireNonNull(builder);
            as.to(builder::authorizationGrantType);
        });
        registration.getRedirectUris().forEach(redirectUri -> {
            PropertyMapper.Source from4 = map.from((PropertyMapper) redirectUri);
            Objects.requireNonNull(builder);
            from4.to(builder::redirectUri);
        });
        registration.getPostLogoutRedirectUris().forEach(redirectUri2 -> {
            PropertyMapper.Source from4 = map.from((PropertyMapper) redirectUri2);
            Objects.requireNonNull(builder);
            from4.to(builder::postLogoutRedirectUri);
        });
        registration.getScopes().forEach(scope -> {
            PropertyMapper.Source from4 = map.from((PropertyMapper) scope);
            Objects.requireNonNull(builder);
            from4.to(builder::scope);
        });
        builder.clientSettings(getClientSettings(client, map));
        builder.tokenSettings(getTokenSettings(client, map));
        return builder.build();
    }

    private ClientSettings getClientSettings(OAuth2AuthorizationServerProperties.Client client, PropertyMapper map) {
        ClientSettings.Builder builder = ClientSettings.builder();
        Objects.requireNonNull(client);
        PropertyMapper.Source from = map.from(client::isRequireProofKey);
        Objects.requireNonNull(builder);
        from.to((v1) -> {
            r1.requireProofKey(v1);
        });
        Objects.requireNonNull(client);
        PropertyMapper.Source from2 = map.from(client::isRequireAuthorizationConsent);
        Objects.requireNonNull(builder);
        from2.to((v1) -> {
            r1.requireAuthorizationConsent(v1);
        });
        Objects.requireNonNull(client);
        PropertyMapper.Source from3 = map.from(client::getJwkSetUri);
        Objects.requireNonNull(builder);
        from3.to(builder::jwkSetUrl);
        Objects.requireNonNull(client);
        PropertyMapper.Source as = map.from(client::getTokenEndpointAuthenticationSigningAlgorithm).as(this::jwsAlgorithm);
        Objects.requireNonNull(builder);
        as.to(builder::tokenEndpointAuthenticationSigningAlgorithm);
        return builder.build();
    }

    private TokenSettings getTokenSettings(OAuth2AuthorizationServerProperties.Client client, PropertyMapper map) {
        OAuth2AuthorizationServerProperties.Token token = client.getToken();
        TokenSettings.Builder builder = TokenSettings.builder();
        Objects.requireNonNull(token);
        PropertyMapper.Source from = map.from(token::getAuthorizationCodeTimeToLive);
        Objects.requireNonNull(builder);
        from.to(builder::authorizationCodeTimeToLive);
        Objects.requireNonNull(token);
        PropertyMapper.Source from2 = map.from(token::getAccessTokenTimeToLive);
        Objects.requireNonNull(builder);
        from2.to(builder::accessTokenTimeToLive);
        Objects.requireNonNull(token);
        PropertyMapper.Source as = map.from(token::getAccessTokenFormat).as(OAuth2TokenFormat::new);
        Objects.requireNonNull(builder);
        as.to(builder::accessTokenFormat);
        Objects.requireNonNull(token);
        PropertyMapper.Source from3 = map.from(token::getDeviceCodeTimeToLive);
        Objects.requireNonNull(builder);
        from3.to(builder::deviceCodeTimeToLive);
        Objects.requireNonNull(token);
        PropertyMapper.Source from4 = map.from(token::isReuseRefreshTokens);
        Objects.requireNonNull(builder);
        from4.to((v1) -> {
            r1.reuseRefreshTokens(v1);
        });
        Objects.requireNonNull(token);
        PropertyMapper.Source from5 = map.from(token::getRefreshTokenTimeToLive);
        Objects.requireNonNull(builder);
        from5.to(builder::refreshTokenTimeToLive);
        Objects.requireNonNull(token);
        PropertyMapper.Source as2 = map.from(token::getIdTokenSignatureAlgorithm).as(this::signatureAlgorithm);
        Objects.requireNonNull(builder);
        as2.to(builder::idTokenSignatureAlgorithm);
        return builder.build();
    }

    private JwsAlgorithm jwsAlgorithm(String signingAlgorithm) {
        String name = signingAlgorithm.toUpperCase();
        MacAlgorithm from = SignatureAlgorithm.from(name);
        if (from == null) {
            from = MacAlgorithm.from(name);
        }
        return from;
    }

    private SignatureAlgorithm signatureAlgorithm(String signatureAlgorithm) {
        return SignatureAlgorithm.from(signatureAlgorithm.toUpperCase());
    }
}
