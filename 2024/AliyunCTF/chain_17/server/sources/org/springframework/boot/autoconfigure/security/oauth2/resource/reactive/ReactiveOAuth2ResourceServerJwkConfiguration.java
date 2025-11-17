package org.springframework.boot.autoconfigure.security.oauth2.resource.reactive;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.oauth2.resource.IssuerUriCondition;
import org.springframework.boot.autoconfigure.security.oauth2.resource.KeyValueCondition;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.SupplierReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.util.CollectionUtils;

@Configuration(proxyBeanMethods = false)
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/oauth2/resource/reactive/ReactiveOAuth2ResourceServerJwkConfiguration.class */
class ReactiveOAuth2ResourceServerJwkConfiguration {
    ReactiveOAuth2ResourceServerJwkConfiguration() {
    }

    @ConditionalOnMissingBean({ReactiveJwtDecoder.class})
    @Configuration(proxyBeanMethods = false)
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/oauth2/resource/reactive/ReactiveOAuth2ResourceServerJwkConfiguration$JwtConfiguration.class */
    static class JwtConfiguration {
        private final OAuth2ResourceServerProperties.Jwt properties;
        private final List<OAuth2TokenValidator<Jwt>> additionalValidators;

        JwtConfiguration(OAuth2ResourceServerProperties properties, ObjectProvider<OAuth2TokenValidator<Jwt>> additionalValidators) {
            this.properties = properties.getJwt();
            this.additionalValidators = additionalValidators.orderedStream().toList();
        }

        @ConditionalOnProperty(name = {"spring.security.oauth2.resourceserver.jwt.jwk-set-uri"})
        @Bean
        ReactiveJwtDecoder jwtDecoder(ObjectProvider<JwkSetUriReactiveJwtDecoderBuilderCustomizer> customizers) {
            NimbusReactiveJwtDecoder.JwkSetUriReactiveJwtDecoderBuilder builder = NimbusReactiveJwtDecoder.withJwkSetUri(this.properties.getJwkSetUri()).jwsAlgorithms(this::jwsAlgorithms);
            customizers.orderedStream().forEach(customizer -> {
                customizer.customize(builder);
            });
            NimbusReactiveJwtDecoder nimbusReactiveJwtDecoder = builder.build();
            String issuerUri = this.properties.getIssuerUri();
            OAuth2TokenValidator<Jwt> defaultValidator = issuerUri != null ? JwtValidators.createDefaultWithIssuer(issuerUri) : JwtValidators.createDefault();
            nimbusReactiveJwtDecoder.setJwtValidator(getValidators(defaultValidator));
            return nimbusReactiveJwtDecoder;
        }

        private void jwsAlgorithms(Set<SignatureAlgorithm> signatureAlgorithms) {
            for (String algorithm : this.properties.getJwsAlgorithms()) {
                signatureAlgorithms.add(SignatureAlgorithm.from(algorithm));
            }
        }

        private OAuth2TokenValidator<Jwt> getValidators(OAuth2TokenValidator<Jwt> defaultValidator) {
            List<String> audiences = this.properties.getAudiences();
            if (CollectionUtils.isEmpty(audiences) && this.additionalValidators.isEmpty()) {
                return defaultValidator;
            }
            List<OAuth2TokenValidator<Jwt>> validators = new ArrayList<>();
            validators.add(defaultValidator);
            if (!CollectionUtils.isEmpty(audiences)) {
                validators.add(new JwtClaimValidator("aud", aud -> {
                    return (aud == null || Collections.disjoint(aud, audiences)) ? false : true;
                }));
            }
            validators.addAll(this.additionalValidators);
            return new DelegatingOAuth2TokenValidator(validators);
        }

        @Conditional({KeyValueCondition.class})
        @Bean
        NimbusReactiveJwtDecoder jwtDecoderByPublicKeyValue() throws Exception {
            RSAPublicKey publicKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(getKeySpec(this.properties.readPublicKey())));
            NimbusReactiveJwtDecoder jwtDecoder = NimbusReactiveJwtDecoder.withPublicKey(publicKey).signatureAlgorithm(SignatureAlgorithm.from(exactlyOneAlgorithm())).build();
            jwtDecoder.setJwtValidator(getValidators(JwtValidators.createDefault()));
            return jwtDecoder;
        }

        private byte[] getKeySpec(String keyValue) {
            return Base64.getMimeDecoder().decode(keyValue.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", ""));
        }

        private String exactlyOneAlgorithm() {
            List<String> algorithms = this.properties.getJwsAlgorithms();
            int count = algorithms != null ? algorithms.size() : 0;
            if (count != 1) {
                throw new IllegalStateException("Creating a JWT decoder using a public key requires exactly one JWS algorithm but " + count + " were configured");
            }
            return algorithms.get(0);
        }

        @Conditional({IssuerUriCondition.class})
        @Bean
        SupplierReactiveJwtDecoder jwtDecoderByIssuerUri(ObjectProvider<JwkSetUriReactiveJwtDecoderBuilderCustomizer> customizers) {
            return new SupplierReactiveJwtDecoder(() -> {
                NimbusReactiveJwtDecoder.JwkSetUriReactiveJwtDecoderBuilder builder = NimbusReactiveJwtDecoder.withIssuerLocation(this.properties.getIssuerUri());
                customizers.orderedStream().forEach(customizer -> {
                    customizer.customize(builder);
                });
                NimbusReactiveJwtDecoder jwtDecoder = builder.build();
                jwtDecoder.setJwtValidator(getValidators(JwtValidators.createDefaultWithIssuer(this.properties.getIssuerUri())));
                return jwtDecoder;
            });
        }
    }

    @ConditionalOnMissingBean({SecurityWebFilterChain.class})
    @Configuration(proxyBeanMethods = false)
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/oauth2/resource/reactive/ReactiveOAuth2ResourceServerJwkConfiguration$WebSecurityConfiguration.class */
    static class WebSecurityConfiguration {
        WebSecurityConfiguration() {
        }

        @ConditionalOnBean({ReactiveJwtDecoder.class})
        @Bean
        SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, ReactiveJwtDecoder jwtDecoder) {
            http.authorizeExchange(exchanges -> {
                exchanges.anyExchange().authenticated();
            });
            http.oauth2ResourceServer(server -> {
                customDecoder(server, jwtDecoder);
            });
            return http.build();
        }

        private void customDecoder(ServerHttpSecurity.OAuth2ResourceServerSpec server, ReactiveJwtDecoder decoder) {
            server.jwt(jwt -> {
                jwt.jwtDecoder(decoder);
            });
        }
    }
}
