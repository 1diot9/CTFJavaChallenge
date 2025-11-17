package org.springframework.web.servlet.function;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.function.ServerResponse;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/ErrorHandlingServerResponse.class */
public abstract class ErrorHandlingServerResponse implements ServerResponse {
    protected final Log logger = LogFactory.getLog(getClass());
    private final List<ErrorHandler<?>> errorHandlers = new ArrayList();

    /* JADX INFO: Access modifiers changed from: protected */
    public final <T extends ServerResponse> void addErrorHandler(Predicate<Throwable> predicate, BiFunction<Throwable, ServerRequest, T> errorHandler) {
        Assert.notNull(predicate, "Predicate must not be null");
        Assert.notNull(errorHandler, "ErrorHandler must not be null");
        this.errorHandlers.add(new ErrorHandler<>(predicate, errorHandler));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public final ModelAndView handleError(Throwable t, HttpServletRequest servletRequest, HttpServletResponse servletResponse, ServerResponse.Context context) throws ServletException, IOException {
        ServerResponse serverResponse = errorResponse(t, servletRequest);
        if (serverResponse != null) {
            return serverResponse.writeTo(servletRequest, servletResponse, context);
        }
        if (t instanceof ServletException) {
            ServletException servletException = (ServletException) t;
            throw servletException;
        }
        if (t instanceof IOException) {
            IOException ioException = (IOException) t;
            throw ioException;
        }
        throw new ServletException(t);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Type inference failed for: r0v15, types: [org.springframework.web.servlet.function.ServerResponse] */
    @Nullable
    public final ServerResponse errorResponse(Throwable t, HttpServletRequest servletRequest) {
        for (ErrorHandler<?> errorHandler : this.errorHandlers) {
            if (errorHandler.test(t)) {
                ServerRequest serverRequest = (ServerRequest) servletRequest.getAttribute(RouterFunctions.REQUEST_ATTRIBUTE);
                return errorHandler.handle(t, serverRequest);
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/ErrorHandlingServerResponse$ErrorHandler.class */
    public static class ErrorHandler<T extends ServerResponse> {
        private final Predicate<Throwable> predicate;
        private final BiFunction<Throwable, ServerRequest, T> responseProvider;

        public ErrorHandler(Predicate<Throwable> predicate, BiFunction<Throwable, ServerRequest, T> responseProvider) {
            Assert.notNull(predicate, "Predicate must not be null");
            Assert.notNull(responseProvider, "ResponseProvider must not be null");
            this.predicate = predicate;
            this.responseProvider = responseProvider;
        }

        public boolean test(Throwable t) {
            return this.predicate.test(t);
        }

        public T handle(Throwable t, ServerRequest serverRequest) {
            return this.responseProvider.apply(t, serverRequest);
        }
    }
}
