package org.springframework.web.method.annotation;

import java.util.Collections;
import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.support.DefaultDataBinderFactory;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.InvocableHandlerMethod;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/method/annotation/InitBinderDataBinderFactory.class */
public class InitBinderDataBinderFactory extends DefaultDataBinderFactory {
    private final List<InvocableHandlerMethod> binderMethods;

    public InitBinderDataBinderFactory(@Nullable List<InvocableHandlerMethod> binderMethods, @Nullable WebBindingInitializer initializer) {
        super(initializer);
        this.binderMethods = binderMethods != null ? binderMethods : Collections.emptyList();
    }

    @Override // org.springframework.web.bind.support.DefaultDataBinderFactory
    public void initBinder(WebDataBinder dataBinder, NativeWebRequest request) throws Exception {
        for (InvocableHandlerMethod binderMethod : this.binderMethods) {
            if (isBinderMethodApplicable(binderMethod, dataBinder)) {
                Object returnValue = binderMethod.invokeForRequest(request, null, dataBinder);
                if (returnValue != null) {
                    throw new IllegalStateException("@InitBinder methods must not return a value (should be void): " + binderMethod);
                }
            }
        }
    }

    protected boolean isBinderMethodApplicable(HandlerMethod initBinderMethod, WebDataBinder dataBinder) {
        InitBinder ann = (InitBinder) initBinderMethod.getMethodAnnotation(InitBinder.class);
        Assert.state(ann != null, "No InitBinder annotation");
        String[] names = ann.value();
        return ObjectUtils.isEmpty((Object[]) names) || ObjectUtils.containsElement(names, dataBinder.getObjectName());
    }
}
