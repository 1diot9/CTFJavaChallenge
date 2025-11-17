package org.springframework.web.filter.reactive;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.observation.DefaultServerRequestObservationConvention;
import org.springframework.http.server.reactive.observation.ServerHttpObservationDocumentation;
import org.springframework.http.server.reactive.observation.ServerRequestObservationContext;
import org.springframework.http.server.reactive.observation.ServerRequestObservationConvention;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.observability.DefaultSignalListener;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@Deprecated(since = "6.1", forRemoval = true)
/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/filter/reactive/ServerHttpObservationFilter.class */
public class ServerHttpObservationFilter implements WebFilter {
    public static final String CURRENT_OBSERVATION_CONTEXT_ATTRIBUTE = ServerHttpObservationFilter.class.getName() + ".context";
    private static final ServerRequestObservationConvention DEFAULT_OBSERVATION_CONVENTION = new DefaultServerRequestObservationConvention();
    private final ObservationRegistry observationRegistry;
    private final ServerRequestObservationConvention observationConvention;

    public ServerHttpObservationFilter(ObservationRegistry observationRegistry) {
        this(observationRegistry, DEFAULT_OBSERVATION_CONVENTION);
    }

    public ServerHttpObservationFilter(ObservationRegistry observationRegistry, ServerRequestObservationConvention observationConvention) {
        this.observationRegistry = observationRegistry;
        this.observationConvention = observationConvention;
    }

    public static Optional<ServerRequestObservationContext> findObservationContext(ServerWebExchange exchange) {
        return Optional.ofNullable((ServerRequestObservationContext) exchange.getAttribute(CURRENT_OBSERVATION_CONTEXT_ATTRIBUTE));
    }

    @Override // org.springframework.web.server.WebFilter
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerRequestObservationContext observationContext = new ServerRequestObservationContext(exchange.getRequest(), exchange.getResponse(), exchange.getAttributes());
        exchange.getAttributes().put(CURRENT_OBSERVATION_CONTEXT_ATTRIBUTE, observationContext);
        return chain.filter(exchange).tap(() -> {
            return new ObservationSignalListener(observationContext);
        });
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/filter/reactive/ServerHttpObservationFilter$ObservationSignalListener.class */
    private final class ObservationSignalListener extends DefaultSignalListener<Void> {
        private static final Set<String> DISCONNECTED_CLIENT_EXCEPTIONS = Set.of("AbortedException", "ClientAbortException", "EOFException", "EofException");
        private final ServerRequestObservationContext observationContext;
        private final Observation observation;
        private final AtomicBoolean observationRecorded = new AtomicBoolean();

        ObservationSignalListener(ServerRequestObservationContext observationContext) {
            this.observationContext = observationContext;
            this.observation = ServerHttpObservationDocumentation.HTTP_REACTIVE_SERVER_REQUESTS.observation(ServerHttpObservationFilter.this.observationConvention, ServerHttpObservationFilter.DEFAULT_OBSERVATION_CONVENTION, () -> {
                return observationContext;
            }, ServerHttpObservationFilter.this.observationRegistry);
        }

        public Context addToContext(Context originalContext) {
            return originalContext.put(ObservationThreadLocalAccessor.KEY, this.observation);
        }

        public void doFirst() throws Throwable {
            this.observation.start();
        }

        public void doOnCancel() throws Throwable {
            if (this.observationRecorded.compareAndSet(false, true)) {
                this.observationContext.setConnectionAborted(true);
                this.observation.stop();
            }
        }

        public void doOnComplete() throws Throwable {
            if (this.observationRecorded.compareAndSet(false, true)) {
                doOnTerminate(this.observationContext);
            }
        }

        public void doOnError(Throwable error) throws Throwable {
            if (this.observationRecorded.compareAndSet(false, true)) {
                if (DISCONNECTED_CLIENT_EXCEPTIONS.contains(error.getClass().getSimpleName())) {
                    this.observationContext.setConnectionAborted(true);
                }
                this.observationContext.setError(error);
                doOnTerminate(this.observationContext);
            }
        }

        private void doOnTerminate(ServerRequestObservationContext context) {
            ServerHttpResponse response = context.getResponse();
            if (response.isCommitted()) {
                this.observation.stop();
            } else {
                response.beforeCommit(() -> {
                    this.observation.stop();
                    return Mono.empty();
                });
            }
        }
    }
}
