package cn.hutool.http;

import cn.hutool.http.HttpBase;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/http/HttpInterceptor.class */
public interface HttpInterceptor<T extends HttpBase<T>> {
    void process(T t);

    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/http/HttpInterceptor$Chain.class */
    public static class Chain<T extends HttpBase<T>> implements cn.hutool.core.lang.Chain<HttpInterceptor<T>, Chain<T>> {
        private final List<HttpInterceptor<T>> interceptors = new LinkedList();

        @Override // cn.hutool.core.lang.Chain
        public Chain<T> addChain(HttpInterceptor<T> element) {
            this.interceptors.add(element);
            return this;
        }

        @Override // java.lang.Iterable
        public Iterator<HttpInterceptor<T>> iterator() {
            return this.interceptors.iterator();
        }

        public Chain<T> clear() {
            this.interceptors.clear();
            return this;
        }
    }
}
