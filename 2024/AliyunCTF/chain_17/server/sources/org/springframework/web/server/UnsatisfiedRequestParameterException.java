package org.springframework.web.server;

import java.util.List;
import org.springframework.util.MultiValueMap;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/UnsatisfiedRequestParameterException.class */
public class UnsatisfiedRequestParameterException extends ServerWebInputException {
    private final List<String> conditions;
    private final MultiValueMap<String, String> requestParams;

    public UnsatisfiedRequestParameterException(List<String> conditions, MultiValueMap<String, String> params) {
        super(initReason(conditions, params), null, null, null, new Object[]{conditions});
        this.conditions = conditions;
        this.requestParams = params;
        setDetail("Invalid request parameters.");
    }

    private static String initReason(List<String> conditions, MultiValueMap<String, String> queryParams) {
        StringBuilder sb = new StringBuilder("Parameter conditions ");
        int i = 0;
        for (String condition : conditions) {
            if (i > 0) {
                sb.append(" OR ");
            }
            sb.append('\"').append(condition).append('\"');
            i++;
        }
        sb.append(" not met for actual request parameters: ").append(queryParams);
        return sb.toString();
    }

    public List<String> getConditions() {
        return this.conditions;
    }

    public MultiValueMap<String, String> getRequestParams() {
        return this.requestParams;
    }
}
