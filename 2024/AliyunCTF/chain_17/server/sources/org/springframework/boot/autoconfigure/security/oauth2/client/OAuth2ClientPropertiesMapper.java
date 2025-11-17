package org.springframework.boot.autoconfigure.security.oauth2.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.core.convert.ConversionException;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;
import org.springframework.security.oauth2.core.AuthenticationMethod;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/oauth2/client/OAuth2ClientPropertiesMapper.class */
public final class OAuth2ClientPropertiesMapper {
    private final OAuth2ClientProperties properties;

    public OAuth2ClientPropertiesMapper(OAuth2ClientProperties properties) {
        this.properties = properties;
    }

    public Map<String, ClientRegistration> asClientRegistrations() {
        Map<String, ClientRegistration> clientRegistrations = new HashMap<>();
        this.properties.getRegistration().forEach((key, value) -> {
            clientRegistrations.put(key, getClientRegistration(key, value, this.properties.getProvider()));
        });
        return clientRegistrations;
    }

    private static ClientRegistration getClientRegistration(String registrationId, OAuth2ClientProperties.Registration properties, Map<String, OAuth2ClientProperties.Provider> providers) {
        ClientRegistration.Builder builder = getBuilderFromIssuerIfPossible(registrationId, properties.getProvider(), providers);
        if (builder == null) {
            builder = getBuilder(registrationId, properties.getProvider(), providers);
        }
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        Objects.requireNonNull(properties);
        PropertyMapper.Source from = map.from(properties::getClientId);
        ClientRegistration.Builder builder2 = builder;
        Objects.requireNonNull(builder2);
        from.to(builder2::clientId);
        Objects.requireNonNull(properties);
        PropertyMapper.Source from2 = map.from(properties::getClientSecret);
        ClientRegistration.Builder builder3 = builder;
        Objects.requireNonNull(builder3);
        from2.to(builder3::clientSecret);
        Objects.requireNonNull(properties);
        PropertyMapper.Source as = map.from(properties::getClientAuthenticationMethod).as(ClientAuthenticationMethod::new);
        ClientRegistration.Builder builder4 = builder;
        Objects.requireNonNull(builder4);
        as.to(builder4::clientAuthenticationMethod);
        Objects.requireNonNull(properties);
        PropertyMapper.Source as2 = map.from(properties::getAuthorizationGrantType).as(AuthorizationGrantType::new);
        ClientRegistration.Builder builder5 = builder;
        Objects.requireNonNull(builder5);
        as2.to(builder5::authorizationGrantType);
        Objects.requireNonNull(properties);
        PropertyMapper.Source from3 = map.from(properties::getRedirectUri);
        ClientRegistration.Builder builder6 = builder;
        Objects.requireNonNull(builder6);
        from3.to(builder6::redirectUri);
        Objects.requireNonNull(properties);
        PropertyMapper.Source as3 = map.from(properties::getScope).as((v0) -> {
            return StringUtils.toStringArray(v0);
        });
        ClientRegistration.Builder builder7 = builder;
        Objects.requireNonNull(builder7);
        as3.to(builder7::scope);
        Objects.requireNonNull(properties);
        PropertyMapper.Source from4 = map.from(properties::getClientName);
        ClientRegistration.Builder builder8 = builder;
        Objects.requireNonNull(builder8);
        from4.to(builder8::clientName);
        return builder.build();
    }

    private static ClientRegistration.Builder getBuilderFromIssuerIfPossible(String registrationId, String configuredProviderId, Map<String, OAuth2ClientProperties.Provider> providers) {
        OAuth2ClientProperties.Provider provider;
        String issuer;
        String providerId = configuredProviderId != null ? configuredProviderId : registrationId;
        if (providers.containsKey(providerId) && (issuer = (provider = providers.get(providerId)).getIssuerUri()) != null) {
            ClientRegistration.Builder builder = ClientRegistrations.fromIssuerLocation(issuer).registrationId(registrationId);
            return getBuilder(builder, provider);
        }
        return null;
    }

    private static ClientRegistration.Builder getBuilder(String registrationId, String configuredProviderId, Map<String, OAuth2ClientProperties.Provider> providers) {
        String providerId = configuredProviderId != null ? configuredProviderId : registrationId;
        CommonOAuth2Provider provider = getCommonProvider(providerId);
        if (provider == null && !providers.containsKey(providerId)) {
            throw new IllegalStateException(getErrorMessage(configuredProviderId, registrationId));
        }
        ClientRegistration.Builder builder = provider != null ? provider.getBuilder(registrationId) : ClientRegistration.withRegistrationId(registrationId);
        if (providers.containsKey(providerId)) {
            return getBuilder(builder, providers.get(providerId));
        }
        return builder;
    }

    private static String getErrorMessage(String configuredProviderId, String registrationId) {
        return configuredProviderId != null ? "Unknown provider ID '" + configuredProviderId + "'" : "Provider ID must be specified for client registration '" + registrationId + "'";
    }

    private static ClientRegistration.Builder getBuilder(ClientRegistration.Builder builder, OAuth2ClientProperties.Provider provider) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        Objects.requireNonNull(provider);
        PropertyMapper.Source from = map.from(provider::getAuthorizationUri);
        Objects.requireNonNull(builder);
        from.to(builder::authorizationUri);
        Objects.requireNonNull(provider);
        PropertyMapper.Source from2 = map.from(provider::getTokenUri);
        Objects.requireNonNull(builder);
        from2.to(builder::tokenUri);
        Objects.requireNonNull(provider);
        PropertyMapper.Source from3 = map.from(provider::getUserInfoUri);
        Objects.requireNonNull(builder);
        from3.to(builder::userInfoUri);
        Objects.requireNonNull(provider);
        PropertyMapper.Source as = map.from(provider::getUserInfoAuthenticationMethod).as(AuthenticationMethod::new);
        Objects.requireNonNull(builder);
        as.to(builder::userInfoAuthenticationMethod);
        Objects.requireNonNull(provider);
        PropertyMapper.Source from4 = map.from(provider::getJwkSetUri);
        Objects.requireNonNull(builder);
        from4.to(builder::jwkSetUri);
        Objects.requireNonNull(provider);
        PropertyMapper.Source from5 = map.from(provider::getUserNameAttribute);
        Objects.requireNonNull(builder);
        from5.to(builder::userNameAttributeName);
        return builder;
    }

    private static CommonOAuth2Provider getCommonProvider(String providerId) {
        try {
            return (CommonOAuth2Provider) ApplicationConversionService.getSharedInstance().convert(providerId, CommonOAuth2Provider.class);
        } catch (ConversionException e) {
            return null;
        }
    }
}
