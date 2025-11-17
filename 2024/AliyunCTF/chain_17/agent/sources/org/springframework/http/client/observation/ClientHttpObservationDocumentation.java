package org.springframework.http.client.observation;

import io.micrometer.common.docs.KeyName;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationConvention;
import io.micrometer.observation.docs.ObservationDocumentation;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.tags.BindTag;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/observation/ClientHttpObservationDocumentation.class */
public enum ClientHttpObservationDocumentation implements ObservationDocumentation {
    HTTP_CLIENT_EXCHANGES { // from class: org.springframework.http.client.observation.ClientHttpObservationDocumentation.1
        @Override // io.micrometer.observation.docs.ObservationDocumentation
        public Class<? extends ObservationConvention<? extends Observation.Context>> getDefaultConvention() {
            return DefaultClientRequestObservationConvention.class;
        }

        @Override // io.micrometer.observation.docs.ObservationDocumentation
        public KeyName[] getLowCardinalityKeyNames() {
            return LowCardinalityKeyNames.values();
        }

        @Override // io.micrometer.observation.docs.ObservationDocumentation
        public KeyName[] getHighCardinalityKeyNames() {
            return new KeyName[]{HighCardinalityKeyNames.HTTP_URL};
        }
    };

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/observation/ClientHttpObservationDocumentation$HighCardinalityKeyNames.class */
    public enum HighCardinalityKeyNames implements KeyName {
        HTTP_URL { // from class: org.springframework.http.client.observation.ClientHttpObservationDocumentation.HighCardinalityKeyNames.1
            @Override // io.micrometer.common.docs.KeyName
            public String asString() {
                return "http.url";
            }
        },
        CLIENT_NAME { // from class: org.springframework.http.client.observation.ClientHttpObservationDocumentation.HighCardinalityKeyNames.2
            @Override // io.micrometer.common.docs.KeyName
            public String asString() {
                return "client.name";
            }
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/observation/ClientHttpObservationDocumentation$LowCardinalityKeyNames.class */
    public enum LowCardinalityKeyNames implements KeyName {
        METHOD { // from class: org.springframework.http.client.observation.ClientHttpObservationDocumentation.LowCardinalityKeyNames.1
            @Override // io.micrometer.common.docs.KeyName
            public String asString() {
                return "method";
            }
        },
        URI { // from class: org.springframework.http.client.observation.ClientHttpObservationDocumentation.LowCardinalityKeyNames.2
            @Override // io.micrometer.common.docs.KeyName
            public String asString() {
                return "uri";
            }
        },
        STATUS { // from class: org.springframework.http.client.observation.ClientHttpObservationDocumentation.LowCardinalityKeyNames.3
            @Override // io.micrometer.common.docs.KeyName
            public String asString() {
                return BindTag.STATUS_VARIABLE_NAME;
            }
        },
        CLIENT_NAME { // from class: org.springframework.http.client.observation.ClientHttpObservationDocumentation.LowCardinalityKeyNames.4
            @Override // io.micrometer.common.docs.KeyName
            public String asString() {
                return "client.name";
            }
        },
        EXCEPTION { // from class: org.springframework.http.client.observation.ClientHttpObservationDocumentation.LowCardinalityKeyNames.5
            @Override // io.micrometer.common.docs.KeyName
            public String asString() {
                return SimpleMappingExceptionResolver.DEFAULT_EXCEPTION_ATTRIBUTE;
            }
        },
        OUTCOME { // from class: org.springframework.http.client.observation.ClientHttpObservationDocumentation.LowCardinalityKeyNames.6
            @Override // io.micrometer.common.docs.KeyName
            public String asString() {
                return "outcome";
            }
        }
    }
}
