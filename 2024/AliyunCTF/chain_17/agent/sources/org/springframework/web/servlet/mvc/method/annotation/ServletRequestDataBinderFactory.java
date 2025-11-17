package org.springframework.web.servlet.mvc.method.annotation;

import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.InitBinderDataBinderFactory;
import org.springframework.web.method.support.InvocableHandlerMethod;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/ServletRequestDataBinderFactory.class */
public class ServletRequestDataBinderFactory extends InitBinderDataBinderFactory {
    public ServletRequestDataBinderFactory(@Nullable List<InvocableHandlerMethod> binderMethods, @Nullable WebBindingInitializer initializer) {
        super(binderMethods, initializer);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.bind.support.DefaultDataBinderFactory
    public ServletRequestDataBinder createBinderInstance(@Nullable Object target, String objectName, NativeWebRequest request) throws Exception {
        return new ExtendedServletRequestDataBinder(target, objectName);
    }
}
