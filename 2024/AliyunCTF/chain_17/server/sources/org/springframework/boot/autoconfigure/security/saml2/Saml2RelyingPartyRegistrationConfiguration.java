package org.springframework.boot.autoconfigure.security.saml2;

import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.saml2.Saml2RelyingPartyProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.converter.RsaKeyConverters;
import org.springframework.security.saml2.core.Saml2X509Credential;
import org.springframework.security.saml2.provider.service.registration.InMemoryRelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrations;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@ConditionalOnMissingBean({RelyingPartyRegistrationRepository.class})
@Configuration(proxyBeanMethods = false)
@Conditional({RegistrationConfiguredCondition.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/saml2/Saml2RelyingPartyRegistrationConfiguration.class */
class Saml2RelyingPartyRegistrationConfiguration {
    Saml2RelyingPartyRegistrationConfiguration() {
    }

    @Bean
    RelyingPartyRegistrationRepository relyingPartyRegistrationRepository(Saml2RelyingPartyProperties properties) {
        List<RelyingPartyRegistration> registrations = properties.getRegistration().entrySet().stream().map(this::asRegistration).toList();
        return new InMemoryRelyingPartyRegistrationRepository(registrations);
    }

    private RelyingPartyRegistration asRegistration(Map.Entry<String, Saml2RelyingPartyProperties.Registration> entry) {
        return asRegistration(entry.getKey(), entry.getValue());
    }

    private RelyingPartyRegistration asRegistration(String id, Saml2RelyingPartyProperties.Registration properties) {
        boolean usingMetadata = StringUtils.hasText(properties.getAssertingparty().getMetadataUri());
        RelyingPartyRegistration.Builder builder = !usingMetadata ? RelyingPartyRegistration.withRegistrationId(id) : createBuilderUsingMetadata(properties.getAssertingparty()).registrationId(id);
        builder.assertionConsumerServiceLocation(properties.getAcs().getLocation());
        builder.assertionConsumerServiceBinding(properties.getAcs().getBinding());
        builder.assertingPartyDetails(mapAssertingParty(properties.getAssertingparty()));
        builder.signingX509Credentials(credentials -> {
            Stream<R> map = properties.getSigning().getCredentials().stream().map(this::asSigningCredential);
            Objects.requireNonNull(credentials);
            map.forEach((v1) -> {
                r1.add(v1);
            });
        });
        builder.decryptionX509Credentials(credentials2 -> {
            Stream<R> map = properties.getDecryption().getCredentials().stream().map(this::asDecryptionCredential);
            Objects.requireNonNull(credentials2);
            map.forEach((v1) -> {
                r1.add(v1);
            });
        });
        builder.assertingPartyDetails(details -> {
            details.verificationX509Credentials(credentials3 -> {
                Stream<R> map = properties.getAssertingparty().getVerification().getCredentials().stream().map(this::asVerificationCredential);
                Objects.requireNonNull(credentials3);
                map.forEach((v1) -> {
                    r1.add(v1);
                });
            });
        });
        builder.singleLogoutServiceLocation(properties.getSinglelogout().getUrl());
        builder.singleLogoutServiceResponseLocation(properties.getSinglelogout().getResponseUrl());
        builder.singleLogoutServiceBinding(properties.getSinglelogout().getBinding());
        builder.entityId(properties.getEntityId());
        RelyingPartyRegistration registration = builder.build();
        boolean signRequest = registration.getAssertingPartyDetails().getWantAuthnRequestsSigned();
        validateSigningCredentials(properties, signRequest);
        return registration;
    }

    private RelyingPartyRegistration.Builder createBuilderUsingMetadata(Saml2RelyingPartyProperties.AssertingParty properties) {
        String requiredEntityId = properties.getEntityId();
        Collection<RelyingPartyRegistration.Builder> candidates = RelyingPartyRegistrations.collectionFromMetadataLocation(properties.getMetadataUri());
        for (RelyingPartyRegistration.Builder candidate : candidates) {
            if (requiredEntityId == null || requiredEntityId.equals(getEntityId(candidate))) {
                return candidate;
            }
        }
        throw new IllegalStateException("No relying party with Entity ID '" + requiredEntityId + "' found");
    }

    private Object getEntityId(RelyingPartyRegistration.Builder candidate) {
        String[] result = new String[1];
        candidate.assertingPartyDetails(builder -> {
            result[0] = builder.build().getEntityId();
        });
        return result[0];
    }

    private Consumer<RelyingPartyRegistration.AssertingPartyDetails.Builder> mapAssertingParty(Saml2RelyingPartyProperties.AssertingParty assertingParty) {
        return details -> {
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            Objects.requireNonNull(assertingParty);
            PropertyMapper.Source from = map.from(assertingParty::getEntityId);
            Objects.requireNonNull(details);
            from.to(details::entityId);
            Saml2RelyingPartyProperties.AssertingParty.Singlesignon singlesignon = assertingParty.getSinglesignon();
            Objects.requireNonNull(singlesignon);
            PropertyMapper.Source from2 = map.from(singlesignon::getBinding);
            Objects.requireNonNull(details);
            from2.to(details::singleSignOnServiceBinding);
            Saml2RelyingPartyProperties.AssertingParty.Singlesignon singlesignon2 = assertingParty.getSinglesignon();
            Objects.requireNonNull(singlesignon2);
            PropertyMapper.Source from3 = map.from(singlesignon2::getUrl);
            Objects.requireNonNull(details);
            from3.to(details::singleSignOnServiceLocation);
            Saml2RelyingPartyProperties.AssertingParty.Singlesignon singlesignon3 = assertingParty.getSinglesignon();
            Objects.requireNonNull(singlesignon3);
            PropertyMapper.Source from4 = map.from(singlesignon3::getSignRequest);
            Objects.requireNonNull(details);
            from4.to((v1) -> {
                r1.wantAuthnRequestsSigned(v1);
            });
            Saml2RelyingPartyProperties.Singlelogout singlelogout = assertingParty.getSinglelogout();
            Objects.requireNonNull(singlelogout);
            PropertyMapper.Source from5 = map.from(singlelogout::getUrl);
            Objects.requireNonNull(details);
            from5.to(details::singleLogoutServiceLocation);
            Saml2RelyingPartyProperties.Singlelogout singlelogout2 = assertingParty.getSinglelogout();
            Objects.requireNonNull(singlelogout2);
            PropertyMapper.Source from6 = map.from(singlelogout2::getResponseUrl);
            Objects.requireNonNull(details);
            from6.to(details::singleLogoutServiceResponseLocation);
            Saml2RelyingPartyProperties.Singlelogout singlelogout3 = assertingParty.getSinglelogout();
            Objects.requireNonNull(singlelogout3);
            PropertyMapper.Source from7 = map.from(singlelogout3::getBinding);
            Objects.requireNonNull(details);
            from7.to(details::singleLogoutServiceBinding);
        };
    }

    private void validateSigningCredentials(Saml2RelyingPartyProperties.Registration properties, boolean signRequest) {
        if (signRequest) {
            Assert.state(!properties.getSigning().getCredentials().isEmpty(), "Signing credentials must not be empty when authentication requests require signing.");
        }
    }

    private Saml2X509Credential asSigningCredential(Saml2RelyingPartyProperties.Registration.Signing.Credential properties) {
        RSAPrivateKey privateKey = readPrivateKey(properties.getPrivateKeyLocation());
        X509Certificate certificate = readCertificate(properties.getCertificateLocation());
        return new Saml2X509Credential(privateKey, certificate, new Saml2X509Credential.Saml2X509CredentialType[]{Saml2X509Credential.Saml2X509CredentialType.SIGNING});
    }

    private Saml2X509Credential asDecryptionCredential(Saml2RelyingPartyProperties.Decryption.Credential properties) {
        RSAPrivateKey privateKey = readPrivateKey(properties.getPrivateKeyLocation());
        X509Certificate certificate = readCertificate(properties.getCertificateLocation());
        return new Saml2X509Credential(privateKey, certificate, new Saml2X509Credential.Saml2X509CredentialType[]{Saml2X509Credential.Saml2X509CredentialType.DECRYPTION});
    }

    private Saml2X509Credential asVerificationCredential(Saml2RelyingPartyProperties.AssertingParty.Verification.Credential properties) {
        X509Certificate certificate = readCertificate(properties.getCertificateLocation());
        return new Saml2X509Credential(certificate, new Saml2X509Credential.Saml2X509CredentialType[]{Saml2X509Credential.Saml2X509CredentialType.ENCRYPTION, Saml2X509Credential.Saml2X509CredentialType.VERIFICATION});
    }

    private RSAPrivateKey readPrivateKey(Resource location) {
        Assert.state(location != null, "No private key location specified");
        Assert.state(location.exists(), (Supplier<String>) () -> {
            return "Private key location '" + location + "' does not exist";
        });
        try {
            InputStream inputStream = location.getInputStream();
            try {
                RSAPrivateKey rSAPrivateKey = (RSAPrivateKey) RsaKeyConverters.pkcs8().convert(inputStream);
                if (inputStream != null) {
                    inputStream.close();
                }
                return rSAPrivateKey;
            } finally {
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    private X509Certificate readCertificate(Resource location) {
        Assert.state(location != null, "No certificate location specified");
        Assert.state(location.exists(), (Supplier<String>) () -> {
            return "Certificate  location '" + location + "' does not exist";
        });
        try {
            InputStream inputStream = location.getInputStream();
            try {
                X509Certificate x509Certificate = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(inputStream);
                if (inputStream != null) {
                    inputStream.close();
                }
                return x509Certificate;
            } finally {
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex);
        }
    }
}
