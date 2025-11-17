package org.springframework.web.servlet.mvc.method.annotation;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/MatrixVariableMapMethodArgumentResolver.class */
public class MatrixVariableMapMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override // org.springframework.web.method.support.HandlerMethodArgumentResolver
    public boolean supportsParameter(MethodParameter parameter) {
        MatrixVariable matrixVariable = (MatrixVariable) parameter.getParameterAnnotation(MatrixVariable.class);
        return (matrixVariable == null || !Map.class.isAssignableFrom(parameter.getParameterType()) || StringUtils.hasText(matrixVariable.name())) ? false : true;
    }

    @Override // org.springframework.web.method.support.HandlerMethodArgumentResolver
    @Nullable
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest request, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        Map<String, MultiValueMap<String, String>> matrixVariables = (Map) request.getAttribute(HandlerMapping.MATRIX_VARIABLES_ATTRIBUTE, 0);
        MultiValueMap<String, String> map = mapMatrixVariables(parameter, matrixVariables);
        return isSingleValueMap(parameter) ? map.toSingleValueMap() : map;
    }

    private MultiValueMap<String, String> mapMatrixVariables(MethodParameter parameter, @Nullable Map<String, MultiValueMap<String, String>> matrixVariables) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        if (CollectionUtils.isEmpty(matrixVariables)) {
            return map;
        }
        MatrixVariable ann = (MatrixVariable) parameter.getParameterAnnotation(MatrixVariable.class);
        Assert.state(ann != null, "No MatrixVariable annotation");
        String pathVariable = ann.pathVar();
        if (!pathVariable.equals(ValueConstants.DEFAULT_NONE)) {
            MultiValueMap<String, String> mapForPathVariable = matrixVariables.get(pathVariable);
            if (mapForPathVariable == null) {
                return map;
            }
            map.putAll(mapForPathVariable);
        } else {
            for (MultiValueMap<String, String> vars : matrixVariables.values()) {
                vars.forEach((name, values) -> {
                    Iterator it = values.iterator();
                    while (it.hasNext()) {
                        String value = (String) it.next();
                        map.add(name, value);
                    }
                });
            }
        }
        return map;
    }

    private boolean isSingleValueMap(MethodParameter parameter) {
        if (!MultiValueMap.class.isAssignableFrom(parameter.getParameterType())) {
            ResolvableType[] genericTypes = ResolvableType.forMethodParameter(parameter).getGenerics();
            return genericTypes.length == 2 && !List.class.isAssignableFrom(genericTypes[1].toClass());
        }
        return false;
    }
}
