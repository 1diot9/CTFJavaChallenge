package org.springframework.web.server.adapter;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.LoggingCodecSupport;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.observation.DefaultServerRequestObservationConvention;
import org.springframework.http.server.reactive.observation.ServerHttpObservationDocumentation;
import org.springframework.http.server.reactive.observation.ServerRequestObservationContext;
import org.springframework.http.server.reactive.observation.ServerRequestObservationConvention;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebHandler;
import org.springframework.web.server.handler.ExceptionHandlingWebHandler;
import org.springframework.web.server.handler.WebHandlerDecorator;
import org.springframework.web.server.i18n.AcceptHeaderLocaleContextResolver;
import org.springframework.web.server.i18n.LocaleContextResolver;
import org.springframework.web.server.session.DefaultWebSessionManager;
import org.springframework.web.server.session.WebSessionManager;
import org.springframework.web.util.DisconnectedClientHelper;
import reactor.core.observability.DefaultSignalListener;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/adapter/HttpWebHandlerAdapter.class */
public class HttpWebHandlerAdapter extends WebHandlerDecorator implements HttpHandler {
    private static final String DISCONNECTED_CLIENT_LOG_CATEGORY = "org.springframework.web.server.DisconnectedClient";
    private static final DisconnectedClientHelper disconnectedClientHelper = new DisconnectedClientHelper(DISCONNECTED_CLIENT_LOG_CATEGORY);
    private static final ServerRequestObservationConvention DEFAULT_OBSERVATION_CONVENTION = new DefaultServerRequestObservationConvention();
    private static final Log logger = LogFactory.getLog((Class<?>) HttpWebHandlerAdapter.class);
    private WebSessionManager sessionManager;

    @Nullable
    private ServerCodecConfigurer codecConfigurer;
    private LocaleContextResolver localeContextResolver;

    @Nullable
    private ForwardedHeaderTransformer forwardedHeaderTransformer;
    private ObservationRegistry observationRegistry;
    private ServerRequestObservationConvention observationConvention;

    @Nullable
    private ApplicationContext applicationContext;
    private boolean enableLoggingRequestDetails;

    public HttpWebHandlerAdapter(WebHandler delegate) {
        super(delegate);
        this.sessionManager = new DefaultWebSessionManager();
        this.localeContextResolver = new AcceptHeaderLocaleContextResolver();
        this.observationRegistry = ObservationRegistry.NOOP;
        this.observationConvention = DEFAULT_OBSERVATION_CONVENTION;
        this.enableLoggingRequestDetails = false;
    }

    public void setSessionManager(WebSessionManager sessionManager) {
        Assert.notNull(sessionManager, "WebSessionManager must not be null");
        this.sessionManager = sessionManager;
    }

    public WebSessionManager getSessionManager() {
        return this.sessionManager;
    }

    public void setCodecConfigurer(ServerCodecConfigurer codecConfigurer) {
        Assert.notNull(codecConfigurer, "ServerCodecConfigurer is required");
        this.codecConfigurer = codecConfigurer;
        this.enableLoggingRequestDetails = false;
        Stream<HttpMessageReader<?>> stream = this.codecConfigurer.getReaders().stream();
        Class<LoggingCodecSupport> cls = LoggingCodecSupport.class;
        Objects.requireNonNull(LoggingCodecSupport.class);
        stream.filter((v1) -> {
            return r1.isInstance(v1);
        }).forEach(httpMessageReader -> {
            if (((LoggingCodecSupport) httpMessageReader).isEnableLoggingRequestDetails()) {
                this.enableLoggingRequestDetails = true;
            }
        });
    }

    public ServerCodecConfigurer getCodecConfigurer() {
        if (this.codecConfigurer == null) {
            setCodecConfigurer(ServerCodecConfigurer.create());
        }
        return this.codecConfigurer;
    }

    public void setLocaleContextResolver(LocaleContextResolver resolver) {
        Assert.notNull(resolver, "LocaleContextResolver is required");
        this.localeContextResolver = resolver;
    }

    public LocaleContextResolver getLocaleContextResolver() {
        return this.localeContextResolver;
    }

    public void setForwardedHeaderTransformer(@Nullable ForwardedHeaderTransformer transformer) {
        this.forwardedHeaderTransformer = transformer;
    }

    @Nullable
    public ForwardedHeaderTransformer getForwardedHeaderTransformer() {
        return this.forwardedHeaderTransformer;
    }

    public void setObservationRegistry(ObservationRegistry observationRegistry) {
        this.observationRegistry = observationRegistry;
    }

    public ObservationRegistry getObservationRegistry() {
        return this.observationRegistry;
    }

    public void setObservationConvention(ServerRequestObservationConvention observationConvention) {
        this.observationConvention = observationConvention;
    }

    public ServerRequestObservationConvention getObservationConvention() {
        return this.observationConvention;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Nullable
    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    public void afterPropertiesSet() {
        String str;
        if (logger.isDebugEnabled()) {
            if (this.enableLoggingRequestDetails) {
                str = "shown which may lead to unsafe logging of potentially sensitive data";
            } else {
                str = "masked to prevent unsafe logging of potentially sensitive data";
            }
            String value = str;
            logger.debug("enableLoggingRequestDetails='" + this.enableLoggingRequestDetails + "': form data and headers will be " + value);
        }
    }

    @Override // org.springframework.http.server.reactive.HttpHandler
    public Mono<Void> handle(ServerHttpRequest request, ServerHttpResponse response) {
        if (this.forwardedHeaderTransformer != null) {
            try {
                request = this.forwardedHeaderTransformer.apply(request);
            } catch (Throwable ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to apply forwarded headers to " + formatRequest(request), ex);
                }
                response.setStatusCode(HttpStatus.BAD_REQUEST);
                return response.setComplete();
            }
        }
        ServerWebExchange exchange = createExchange(request, response);
        LogFormatUtils.traceDebug(logger, traceOn -> {
            return exchange.getLogPrefix() + formatRequest(exchange.getRequest()) + (traceOn.booleanValue() ? ", headers=" + formatHeaders(exchange.getRequest().getHeaders()) : "");
        });
        ServerRequestObservationContext observationContext = new ServerRequestObservationContext(exchange.getRequest(), exchange.getResponse(), exchange.getAttributes());
        exchange.getAttributes().put(ServerRequestObservationContext.CURRENT_OBSERVATION_CONTEXT_ATTRIBUTE, observationContext);
        Mono then = getDelegate().handle(exchange).doOnSuccess(aVoid -> {
            logResponse(exchange);
        }).onErrorResume(ex2 -> {
            return handleUnresolvedError(exchange, observationContext, ex2);
        }).tap(() -> {
            return new ObservationSignalListener(observationContext);
        }).then(exchange.cleanupMultipart());
        Objects.requireNonNull(response);
        return then.then(Mono.defer(response::setComplete));
    }

    protected ServerWebExchange createExchange(ServerHttpRequest request, ServerHttpResponse response) {
        return new DefaultServerWebExchange(request, response, this.sessionManager, getCodecConfigurer(), getLocaleContextResolver(), this.applicationContext);
    }

    protected String formatRequest(ServerHttpRequest request) {
        String rawQuery = request.getURI().getRawQuery();
        String query = StringUtils.hasText(rawQuery) ? "?" + rawQuery : "";
        return "HTTP " + request.getMethod() + " \"" + request.getPath() + query + "\"";
    }

    private void logResponse(ServerWebExchange exchange) {
        LogFormatUtils.traceDebug(logger, traceOn -> {
            Object status = exchange.getResponse().getStatusCode();
            return exchange.getLogPrefix() + "Completed " + (status != null ? status : "200 OK") + (traceOn.booleanValue() ? ", headers=" + formatHeaders(exchange.getResponse().getHeaders()) : "");
        });
    }

    private String formatHeaders(HttpHeaders responseHeaders) {
        if (this.enableLoggingRequestDetails) {
            return responseHeaders.toString();
        }
        return responseHeaders.isEmpty() ? "{}" : "{masked}";
    }

    private Mono<Void> handleUnresolvedError(ServerWebExchange exchange, ServerRequestObservationContext observationContext, Throwable ex) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String logPrefix = exchange.getLogPrefix();
        if (response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR)) {
            logger.error(logPrefix + "500 Server Error for " + formatRequest(request), ex);
            return Mono.empty();
        }
        if (disconnectedClientHelper.checkAndLogClientDisconnectedException(ex)) {
            observationContext.setConnectionAborted(true);
            return Mono.empty();
        }
        logger.error(logPrefix + "Error [" + ex + "] for " + formatRequest(request) + ", but ServerHttpResponse already committed (" + response.getStatusCode() + ")");
        return Mono.error(ex);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/adapter/HttpWebHandlerAdapter$ObservationSignalListener.class */
    private final class ObservationSignalListener extends DefaultSignalListener<Void> {
        private final ServerRequestObservationContext observationContext;
        private final Observation observation;
        private final AtomicBoolean observationRecorded = new AtomicBoolean();

        ObservationSignalListener(ServerRequestObservationContext observationContext) {
            this.observationContext = observationContext;
            this.observation = ServerHttpObservationDocumentation.HTTP_REACTIVE_SERVER_REQUESTS.observation(HttpWebHandlerAdapter.this.observationConvention, HttpWebHandlerAdapter.DEFAULT_OBSERVATION_CONVENTION, () -> {
                return observationContext;
            }, HttpWebHandlerAdapter.this.observationRegistry);
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
                Throwable throwable = (Throwable) this.observationContext.getAttributes().get(ExceptionHandlingWebHandler.HANDLED_WEB_EXCEPTION);
                if (throwable != null) {
                    this.observation.error(throwable);
                }
                doOnTerminate(this.observationContext);
            }
        }

        public void doOnError(Throwable error) throws Throwable {
            if (this.observationRecorded.compareAndSet(false, true)) {
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
