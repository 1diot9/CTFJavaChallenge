package cn.hutool.http;

import cn.hutool.http.HttpInterceptor;
import java.util.Iterator;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/http/GlobalInterceptor.class */
public enum GlobalInterceptor {
    INSTANCE;

    private final HttpInterceptor.Chain<HttpRequest> requestInterceptors = new HttpInterceptor.Chain<>();
    private final HttpInterceptor.Chain<HttpResponse> responseInterceptors = new HttpInterceptor.Chain<>();

    GlobalInterceptor() {
    }

    public synchronized GlobalInterceptor addRequestInterceptor(HttpInterceptor<HttpRequest> interceptor) {
        this.requestInterceptors.addChain(interceptor);
        return this;
    }

    public synchronized GlobalInterceptor addResponseInterceptor(HttpInterceptor<HttpResponse> interceptor) {
        this.responseInterceptors.addChain(interceptor);
        return this;
    }

    public GlobalInterceptor clear() {
        clearRequest();
        clearResponse();
        return this;
    }

    public synchronized GlobalInterceptor clearRequest() {
        this.requestInterceptors.clear();
        return this;
    }

    public synchronized GlobalInterceptor clearResponse() {
        this.responseInterceptors.clear();
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HttpInterceptor.Chain<HttpRequest> getCopiedRequestInterceptor() {
        HttpInterceptor.Chain<HttpRequest> copied = new HttpInterceptor.Chain<>();
        Iterator<HttpInterceptor<HttpRequest>> it = this.requestInterceptors.iterator();
        while (it.hasNext()) {
            HttpInterceptor<HttpRequest> interceptor = it.next();
            copied.addChain(interceptor);
        }
        return copied;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HttpInterceptor.Chain<HttpResponse> getCopiedResponseInterceptor() {
        HttpInterceptor.Chain<HttpResponse> copied = new HttpInterceptor.Chain<>();
        Iterator<HttpInterceptor<HttpResponse>> it = this.responseInterceptors.iterator();
        while (it.hasNext()) {
            HttpInterceptor<HttpResponse> interceptor = it.next();
            copied.addChain(interceptor);
        }
        return copied;
    }
}
