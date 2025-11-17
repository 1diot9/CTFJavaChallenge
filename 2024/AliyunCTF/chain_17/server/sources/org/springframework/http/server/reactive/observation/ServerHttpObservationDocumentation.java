package org.springframework.http.server.reactive.observation;

import io.micrometer.common.docs.KeyName;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationConvention;
import io.micrometer.observation.docs.ObservationDocumentation;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.tags.BindTag;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/observation/ServerHttpObservationDocumentation.class */
public enum ServerHttpObservationDocumentation implements ObservationDocumentation {
    HTTP_REACTIVE_SERVER_REQUESTS { // from class: org.springframework.http.server.reactive.observation.ServerHttpObservationDocumentation.1
        @Override // io.micrometer.observation.docs.ObservationDocumentation
        public Class<? extends ObservationConvention<? extends Observation.Context>> getDefaultConvention() {
            return DefaultServerRequestObservationConvention.class;
        }

        @Override // io.micrometer.observation.docs.ObservationDocumentation
        public KeyName[] getLowCardinalityKeyNames() {
            return LowCardinalityKeyNames.values();
        }

        @Override // io.micrometer.observation.docs.ObservationDocumentation
        public KeyName[] getHighCardinalityKeyNames() {
            return HighCardinalityKeyNames.values();
        }
    };

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/observation/ServerHttpObservationDocumentation$HighCardinalityKeyNames.class */
    public enum HighCardinalityKeyNames implements KeyName {
        HTTP_URL { // from class: org.springframework.http.server.reactive.observation.ServerHttpObservationDocumentation.HighCardinalityKeyNames.1
            @Override // io.micrometer.common.docs.KeyName
            public String asString() {
                return "http.url";
            }
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/observation/ServerHttpObservationDocumentation$LowCardinalityKeyNames.class */
    public enum LowCardinalityKeyNames implements KeyName {
        METHOD { // from class: org.springframework.http.server.reactive.observation.ServerHttpObservationDocumentation.LowCardinalityKeyNames.1
            @Override // io.micrometer.common.docs.KeyName
            public String asString() {
                return "method";
            }
        },
        STATUS { // from class: org.springframework.http.server.reactive.observation.ServerHttpObservationDocumentation.LowCardinalityKeyNames.2
            @Override // io.micrometer.common.docs.KeyName
            public String asString() {
                return BindTag.STATUS_VARIABLE_NAME;
            }
        },
        URI { // from class: org.springframework.http.server.reactive.observation.ServerHttpObservationDocumentation.LowCardinalityKeyNames.3
            @Override // io.micrometer.common.docs.KeyName
            public String asString() {
                return "uri";
            }
        },
        EXCEPTION { // from class: org.springframework.http.server.reactive.observation.ServerHttpObservationDocumentation.LowCardinalityKeyNames.4
            @Override // io.micrometer.common.docs.KeyName
            public String asString() {
                return SimpleMappingExceptionResolver.DEFAULT_EXCEPTION_ATTRIBUTE;
            }
        },
        OUTCOME { // from class: org.springframework.http.server.reactive.observation.ServerHttpObservationDocumentation.LowCardinalityKeyNames.5
            @Override // io.micrometer.common.docs.KeyName
            public String asString() {
                return "outcome";
            }
        }
    }
}
