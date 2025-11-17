package org.springframework.web.filter;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.observation.DefaultServerRequestObservationConvention;
import org.springframework.http.server.observation.ServerHttpObservationDocumentation;
import org.springframework.http.server.observation.ServerRequestObservationContext;
import org.springframework.http.server.observation.ServerRequestObservationConvention;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/filter/ServerHttpObservationFilter.class */
public class ServerHttpObservationFilter extends OncePerRequestFilter {
    public static final String CURRENT_OBSERVATION_CONTEXT_ATTRIBUTE = ServerHttpObservationFilter.class.getName() + ".context";
    private static final ServerRequestObservationConvention DEFAULT_OBSERVATION_CONVENTION = new DefaultServerRequestObservationConvention();
    private static final String CURRENT_OBSERVATION_ATTRIBUTE = ServerHttpObservationFilter.class.getName() + ".observation";
    private final ObservationRegistry observationRegistry;
    private final ServerRequestObservationConvention observationConvention;

    public ServerHttpObservationFilter(ObservationRegistry observationRegistry) {
        this(observationRegistry, DEFAULT_OBSERVATION_CONVENTION);
    }

    public ServerHttpObservationFilter(ObservationRegistry observationRegistry, ServerRequestObservationConvention observationConvention) {
        this.observationRegistry = observationRegistry;
        this.observationConvention = observationConvention;
    }

    public static Optional<ServerRequestObservationContext> findObservationContext(HttpServletRequest request) {
        return Optional.ofNullable((ServerRequestObservationContext) request.getAttribute(CURRENT_OBSERVATION_CONTEXT_ATTRIBUTE));
    }

    @Override // org.springframework.web.filter.OncePerRequestFilter
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }

    @Override // org.springframework.web.filter.OncePerRequestFilter
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Observation observation = createOrFetchObservation(request, response);
        try {
            try {
                Observation.Scope scope = observation.openScope();
                try {
                    filterChain.doFilter(request, response);
                    if (scope != null) {
                        scope.close();
                    }
                } catch (Throwable th) {
                    if (scope != null) {
                        try {
                            scope.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } catch (Exception ex) {
                observation.error(unwrapServletException(ex));
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                throw ex;
            }
        } finally {
            if (!request.isAsyncStarted()) {
                Throwable error = fetchException(request);
                if (error != null) {
                    observation.error(error);
                }
                observation.stop();
            }
        }
    }

    private Observation createOrFetchObservation(HttpServletRequest request, HttpServletResponse response) {
        Observation observation = (Observation) request.getAttribute(CURRENT_OBSERVATION_ATTRIBUTE);
        if (observation == null) {
            ServerRequestObservationContext context = new ServerRequestObservationContext(request, response);
            observation = ServerHttpObservationDocumentation.HTTP_SERVLET_SERVER_REQUESTS.observation(this.observationConvention, DEFAULT_OBSERVATION_CONVENTION, () -> {
                return context;
            }, this.observationRegistry).start();
            request.setAttribute(CURRENT_OBSERVATION_ATTRIBUTE, observation);
            if (!observation.isNoop()) {
                request.setAttribute(CURRENT_OBSERVATION_CONTEXT_ATTRIBUTE, observation.getContext());
            }
        }
        return observation;
    }

    private Throwable unwrapServletException(Throwable ex) {
        return ex instanceof ServletException ? ex.getCause() : ex;
    }

    @Nullable
    private Throwable fetchException(HttpServletRequest request) {
        return (Throwable) request.getAttribute("jakarta.servlet.error.exception");
    }
}
