package org.springframework.web.servlet.function;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import org.reactivestreams.Publisher;
import org.springframework.core.ReactiveAdapter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.async.AsyncWebRequest;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncManager;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.function.ServerResponse;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/DefaultAsyncServerResponse.class */
final class DefaultAsyncServerResponse extends ErrorHandlingServerResponse implements AsyncServerResponse {
    static final boolean reactiveStreamsPresent = ClassUtils.isPresent("org.reactivestreams.Publisher", DefaultAsyncServerResponse.class.getClassLoader());
    private final CompletableFuture<ServerResponse> futureResponse;

    @Nullable
    private final Duration timeout;

    private DefaultAsyncServerResponse(CompletableFuture<ServerResponse> futureResponse, @Nullable Duration timeout) {
        this.futureResponse = futureResponse;
        this.timeout = timeout;
    }

    @Override // org.springframework.web.servlet.function.AsyncServerResponse
    public ServerResponse block() {
        try {
            if (this.timeout != null) {
                return this.futureResponse.get(this.timeout.toMillis(), TimeUnit.MILLISECONDS);
            }
            return this.futureResponse.get();
        } catch (InterruptedException | ExecutionException | TimeoutException ex) {
            throw new IllegalStateException("Failed to get future response", ex);
        }
    }

    @Override // org.springframework.web.servlet.function.ServerResponse
    public HttpStatusCode statusCode() {
        return (HttpStatusCode) delegate((v0) -> {
            return v0.statusCode();
        });
    }

    @Override // org.springframework.web.servlet.function.ServerResponse
    @Deprecated
    public int rawStatusCode() {
        return ((Integer) delegate((v0) -> {
            return v0.rawStatusCode();
        })).intValue();
    }

    @Override // org.springframework.web.servlet.function.ServerResponse
    public HttpHeaders headers() {
        return (HttpHeaders) delegate((v0) -> {
            return v0.headers();
        });
    }

    @Override // org.springframework.web.servlet.function.ServerResponse
    public MultiValueMap<String, Cookie> cookies() {
        return (MultiValueMap) delegate((v0) -> {
            return v0.cookies();
        });
    }

    private <R> R delegate(Function<ServerResponse, R> function) {
        ServerResponse response = this.futureResponse.getNow(null);
        if (response != null) {
            return function.apply(response);
        }
        throw new IllegalStateException("Future ServerResponse has not yet completed");
    }

    @Override // org.springframework.web.servlet.function.ServerResponse
    @Nullable
    public ModelAndView writeTo(HttpServletRequest request, HttpServletResponse response, ServerResponse.Context context) throws ServletException, IOException {
        writeAsync(request, response, createDeferredResult(request));
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void writeAsync(HttpServletRequest request, HttpServletResponse response, DeferredResult<?> deferredResult) throws ServletException, IOException {
        WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);
        AsyncWebRequest asyncWebRequest = WebAsyncUtils.createAsyncWebRequest(request, response);
        asyncManager.setAsyncWebRequest(asyncWebRequest);
        try {
            asyncManager.startDeferredResultProcessing(deferredResult, new Object[0]);
        } catch (ServletException | IOException ex) {
            throw ex;
        } catch (Exception ex2) {
            throw new ServletException("Async processing failed", ex2);
        }
    }

    private DeferredResult<ServerResponse> createDeferredResult(HttpServletRequest request) {
        DeferredResult<ServerResponse> result;
        if (this.timeout != null) {
            result = new DeferredResult<>(Long.valueOf(this.timeout.toMillis()));
        } else {
            result = new DeferredResult<>();
        }
        DeferredResult<ServerResponse> deferredResult = result;
        this.futureResponse.whenComplete((value, ex) -> {
            if (ex != null) {
                if ((ex instanceof CompletionException) && ex.getCause() != null) {
                    ex = ex.getCause();
                }
                ServerResponse errorResponse = errorResponse(ex, request);
                if (errorResponse != null) {
                    deferredResult.setResult(errorResponse);
                    return;
                } else {
                    deferredResult.setErrorResult(ex);
                    return;
                }
            }
            deferredResult.setResult(value);
        });
        return result;
    }

    public static AsyncServerResponse create(Object obj, @Nullable Duration timeout) {
        ReactiveAdapterRegistry registry;
        ReactiveAdapter publisherAdapter;
        Assert.notNull(obj, "Argument to async must not be null");
        if (obj instanceof CompletableFuture) {
            CompletableFuture futureResponse = (CompletableFuture) obj;
            return new DefaultAsyncServerResponse(futureResponse, timeout);
        }
        if (reactiveStreamsPresent && (publisherAdapter = (registry = ReactiveAdapterRegistry.getSharedInstance()).getAdapter(obj.getClass())) != null) {
            Publisher<ServerResponse> publisher = publisherAdapter.toPublisher(obj);
            ReactiveAdapter futureAdapter = registry.getAdapter(CompletableFuture.class);
            if (futureAdapter != null) {
                CompletableFuture<ServerResponse> futureResponse2 = (CompletableFuture) futureAdapter.fromPublisher(publisher);
                return new DefaultAsyncServerResponse(futureResponse2, timeout);
            }
        }
        throw new IllegalArgumentException("Asynchronous type not supported: " + obj.getClass());
    }
}
