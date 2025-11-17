package org.springframework.boot.autoconfigure.security.saml2;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.security.saml2.provider.service.registration.Saml2MessageBinding;

@ConfigurationProperties("spring.security.saml2.relyingparty")
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/saml2/Saml2RelyingPartyProperties.class */
public class Saml2RelyingPartyProperties {
    private final Map<String, Registration> registration = new LinkedHashMap();

    public Map<String, Registration> getRegistration() {
        return this.registration;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/saml2/Saml2RelyingPartyProperties$Registration.class */
    public static class Registration {
        private String entityId = "{baseUrl}/saml2/service-provider-metadata/{registrationId}";
        private final Acs acs = new Acs();
        private final Signing signing = new Signing();
        private final Decryption decryption = new Decryption();
        private final Singlelogout singlelogout = new Singlelogout();
        private final AssertingParty assertingparty = new AssertingParty();

        public String getEntityId() {
            return this.entityId;
        }

        public void setEntityId(String entityId) {
            this.entityId = entityId;
        }

        public Acs getAcs() {
            return this.acs;
        }

        public Signing getSigning() {
            return this.signing;
        }

        public Decryption getDecryption() {
            return this.decryption;
        }

        public AssertingParty getAssertingparty() {
            return this.assertingparty;
        }

        public Singlelogout getSinglelogout() {
            return this.singlelogout;
        }

        /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/saml2/Saml2RelyingPartyProperties$Registration$Acs.class */
        public static class Acs {
            private String location = "{baseUrl}/login/saml2/sso/{registrationId}";
            private Saml2MessageBinding binding = Saml2MessageBinding.POST;

            public String getLocation() {
                return this.location;
            }

            public void setLocation(String location) {
                this.location = location;
            }

            public Saml2MessageBinding getBinding() {
                return this.binding;
            }

            public void setBinding(Saml2MessageBinding binding) {
                this.binding = binding;
            }
        }

        /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/saml2/Saml2RelyingPartyProperties$Registration$Signing.class */
        public static class Signing {
            private List<Credential> credentials = new ArrayList();

            public List<Credential> getCredentials() {
                return this.credentials;
            }

            public void setCredentials(List<Credential> credentials) {
                this.credentials = credentials;
            }

            /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/saml2/Saml2RelyingPartyProperties$Registration$Signing$Credential.class */
            public static class Credential {
                private Resource privateKeyLocation;
                private Resource certificateLocation;

                public Resource getPrivateKeyLocation() {
                    return this.privateKeyLocation;
                }

                public void setPrivateKeyLocation(Resource privateKey) {
                    this.privateKeyLocation = privateKey;
                }

                public Resource getCertificateLocation() {
                    return this.certificateLocation;
                }

                public void setCertificateLocation(Resource certificate) {
                    this.certificateLocation = certificate;
                }
            }
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/saml2/Saml2RelyingPartyProperties$Decryption.class */
    public static class Decryption {
        private List<Credential> credentials = new ArrayList();

        public List<Credential> getCredentials() {
            return this.credentials;
        }

        public void setCredentials(List<Credential> credentials) {
            this.credentials = credentials;
        }

        /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/saml2/Saml2RelyingPartyProperties$Decryption$Credential.class */
        public static class Credential {
            private Resource privateKeyLocation;
            private Resource certificateLocation;

            public Resource getPrivateKeyLocation() {
                return this.privateKeyLocation;
            }

            public void setPrivateKeyLocation(Resource privateKey) {
                this.privateKeyLocation = privateKey;
            }

            public Resource getCertificateLocation() {
                return this.certificateLocation;
            }

            public void setCertificateLocation(Resource certificate) {
                this.certificateLocation = certificate;
            }
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/saml2/Saml2RelyingPartyProperties$AssertingParty.class */
    public static class AssertingParty {
        private String entityId;
        private String metadataUri;
        private final Singlesignon singlesignon = new Singlesignon();
        private final Verification verification = new Verification();
        private final Singlelogout singlelogout = new Singlelogout();

        public String getEntityId() {
            return this.entityId;
        }

        public void setEntityId(String entityId) {
            this.entityId = entityId;
        }

        public String getMetadataUri() {
            return this.metadataUri;
        }

        public void setMetadataUri(String metadataUri) {
            this.metadataUri = metadataUri;
        }

        public Singlesignon getSinglesignon() {
            return this.singlesignon;
        }

        public Verification getVerification() {
            return this.verification;
        }

        public Singlelogout getSinglelogout() {
            return this.singlelogout;
        }

        /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/saml2/Saml2RelyingPartyProperties$AssertingParty$Singlesignon.class */
        public static class Singlesignon {
            private String url;
            private Saml2MessageBinding binding;
            private Boolean signRequest;

            public String getUrl() {
                return this.url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public Saml2MessageBinding getBinding() {
                return this.binding;
            }

            public void setBinding(Saml2MessageBinding binding) {
                this.binding = binding;
            }

            public boolean isSignRequest() {
                return this.signRequest.booleanValue();
            }

            public Boolean getSignRequest() {
                return this.signRequest;
            }

            public void setSignRequest(Boolean signRequest) {
                this.signRequest = signRequest;
            }
        }

        /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/saml2/Saml2RelyingPartyProperties$AssertingParty$Verification.class */
        public static class Verification {
            private List<Credential> credentials = new ArrayList();

            public List<Credential> getCredentials() {
                return this.credentials;
            }

            public void setCredentials(List<Credential> credentials) {
                this.credentials = credentials;
            }

            /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/saml2/Saml2RelyingPartyProperties$AssertingParty$Verification$Credential.class */
            public static class Credential {
                private Resource certificate;

                public Resource getCertificateLocation() {
                    return this.certificate;
                }

                public void setCertificateLocation(Resource certificate) {
                    this.certificate = certificate;
                }
            }
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/saml2/Saml2RelyingPartyProperties$Singlelogout.class */
    public static class Singlelogout {
        private String url;
        private String responseUrl;
        private Saml2MessageBinding binding;

        public String getUrl() {
            return this.url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getResponseUrl() {
            return this.responseUrl;
        }

        public void setResponseUrl(String responseUrl) {
            this.responseUrl = responseUrl;
        }

        public Saml2MessageBinding getBinding() {
            return this.binding;
        }

        public void setBinding(Saml2MessageBinding binding) {
            this.binding = binding;
        }
    }
}
