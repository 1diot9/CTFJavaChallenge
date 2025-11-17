package org.springframework.boot.autoconfigure.web.reactive.error;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.servlet.tags.BindTag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/reactive/error/DefaultErrorWebExceptionHandler.class */
public class DefaultErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {
    private static final MediaType TEXT_HTML_UTF8 = new MediaType("text", "html", StandardCharsets.UTF_8);
    private static final Map<HttpStatus.Series, String> SERIES_VIEWS;
    private final ErrorProperties errorProperties;

    static {
        Map<HttpStatus.Series, String> views = new EnumMap<>(HttpStatus.Series.class);
        views.put(HttpStatus.Series.CLIENT_ERROR, "4xx");
        views.put(HttpStatus.Series.SERVER_ERROR, "5xx");
        SERIES_VIEWS = Collections.unmodifiableMap(views);
    }

    public DefaultErrorWebExceptionHandler(ErrorAttributes errorAttributes, WebProperties.Resources resources, ErrorProperties errorProperties, ApplicationContext applicationContext) {
        super(errorAttributes, resources, applicationContext);
        this.errorProperties = errorProperties;
    }

    @Override // org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(acceptsTextHtml(), this::renderErrorView).andRoute(RequestPredicates.all(), this::renderErrorResponse);
    }

    protected Mono<ServerResponse> renderErrorView(ServerRequest request) {
        Map<String, Object> error = getErrorAttributes(request, getErrorAttributeOptions(request, MediaType.TEXT_HTML));
        int errorStatus = getHttpStatus(error);
        ServerResponse.BodyBuilder responseBody = ServerResponse.status(errorStatus).contentType(TEXT_HTML_UTF8);
        return Flux.just((String[]) getData(errorStatus).toArray(new String[0])).flatMap(viewName -> {
            return renderErrorView(viewName, responseBody, error);
        }).switchIfEmpty(this.errorProperties.getWhitelabel().isEnabled() ? renderDefaultErrorView(responseBody, error) : Mono.error(getError(request))).next();
    }

    private List<String> getData(int errorStatus) {
        List<String> data = new ArrayList<>();
        data.add("error/" + errorStatus);
        HttpStatus.Series series = HttpStatus.Series.resolve(errorStatus);
        if (series != null) {
            data.add("error/" + SERIES_VIEWS.get(series));
        }
        data.add("error/error");
        return data;
    }

    protected Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Map<String, Object> error = getErrorAttributes(request, getErrorAttributeOptions(request, MediaType.ALL));
        return ServerResponse.status(getHttpStatus(error)).contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(error));
    }

    protected ErrorAttributeOptions getErrorAttributeOptions(ServerRequest request, MediaType mediaType) {
        ErrorAttributeOptions options = ErrorAttributeOptions.defaults();
        if (this.errorProperties.isIncludeException()) {
            options = options.including(ErrorAttributeOptions.Include.EXCEPTION);
        }
        if (isIncludeStackTrace(request, mediaType)) {
            options = options.including(ErrorAttributeOptions.Include.STACK_TRACE);
        }
        if (isIncludeMessage(request, mediaType)) {
            options = options.including(ErrorAttributeOptions.Include.MESSAGE);
        }
        if (isIncludeBindingErrors(request, mediaType)) {
            options = options.including(ErrorAttributeOptions.Include.BINDING_ERRORS);
        }
        return options;
    }

    protected boolean isIncludeStackTrace(ServerRequest request, MediaType produces) {
        switch (this.errorProperties.getIncludeStacktrace()) {
            case ALWAYS:
                return true;
            case ON_PARAM:
                return isTraceEnabled(request);
            default:
                return false;
        }
    }

    protected boolean isIncludeMessage(ServerRequest request, MediaType produces) {
        switch (this.errorProperties.getIncludeMessage()) {
            case ALWAYS:
                return true;
            case ON_PARAM:
                return isMessageEnabled(request);
            default:
                return false;
        }
    }

    protected boolean isIncludeBindingErrors(ServerRequest request, MediaType produces) {
        switch (this.errorProperties.getIncludeBindingErrors()) {
            case ALWAYS:
                return true;
            case ON_PARAM:
                return isBindingErrorsEnabled(request);
            default:
                return false;
        }
    }

    protected int getHttpStatus(Map<String, Object> errorAttributes) {
        return ((Integer) errorAttributes.get(BindTag.STATUS_VARIABLE_NAME)).intValue();
    }

    protected RequestPredicate acceptsTextHtml() {
        return serverRequest -> {
            try {
                List<MediaType> acceptedMediaTypes = serverRequest.headers().accept();
                MediaType mediaType = MediaType.ALL;
                Objects.requireNonNull(mediaType);
                acceptedMediaTypes.removeIf((v1) -> {
                    return r1.equalsTypeAndSubtype(v1);
                });
                MimeTypeUtils.sortBySpecificity(acceptedMediaTypes);
                Stream<MediaType> stream = acceptedMediaTypes.stream();
                MediaType mediaType2 = MediaType.TEXT_HTML;
                Objects.requireNonNull(mediaType2);
                return stream.anyMatch(mediaType2::isCompatibleWith);
            } catch (InvalidMediaTypeException e) {
                return false;
            }
        };
    }
}
