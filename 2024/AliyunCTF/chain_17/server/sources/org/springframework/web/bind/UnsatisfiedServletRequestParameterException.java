package org.springframework.web.bind;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/bind/UnsatisfiedServletRequestParameterException.class */
public class UnsatisfiedServletRequestParameterException extends ServletRequestBindingException {
    private final List<String[]> paramConditions;
    private final Map<String, String[]> actualParams;

    public UnsatisfiedServletRequestParameterException(String[] paramConditions, Map<String, String[]> actualParams) {
        this((List<String[]>) List.of(paramConditions), actualParams);
    }

    public UnsatisfiedServletRequestParameterException(List<String[]> paramConditions, Map<String, String[]> actualParams) {
        super("", null, new Object[]{paramsToStringList(paramConditions)});
        this.paramConditions = paramConditions;
        this.actualParams = actualParams;
        getBody().setDetail("Invalid request parameters.");
    }

    private static List<String> paramsToStringList(List<String[]> paramConditions) {
        Assert.notEmpty(paramConditions, "Parameter conditions must not be empty");
        return (List) paramConditions.stream().map(condition -> {
            return "\"" + StringUtils.arrayToDelimitedString(condition, ", ") + "\"";
        }).collect(Collectors.toList());
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        return "Parameter conditions " + String.join(" OR ", paramsToStringList(this.paramConditions)) + " not met for actual request parameters: " + requestParameterMapToString(this.actualParams);
    }

    public final String[] getParamConditions() {
        return this.paramConditions.get(0);
    }

    public final List<String[]> getParamConditionGroups() {
        return this.paramConditions;
    }

    public final Map<String, String[]> getActualParams() {
        return this.actualParams;
    }

    private static String requestParameterMapToString(Map<String, String[]> actualParams) {
        StringBuilder result = new StringBuilder();
        Iterator<Map.Entry<String, String[]>> it = actualParams.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String[]> entry = it.next();
            result.append(entry.getKey()).append('=').append(ObjectUtils.nullSafeToString((Object[]) entry.getValue()));
            if (it.hasNext()) {
                result.append(", ");
            }
        }
        return result.toString();
    }
}
