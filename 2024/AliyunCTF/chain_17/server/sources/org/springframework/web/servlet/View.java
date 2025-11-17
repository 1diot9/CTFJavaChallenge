package org.springframework.web.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/View.class */
public interface View {
    public static final String RESPONSE_STATUS_ATTRIBUTE = View.class.getName() + ".responseStatus";
    public static final String PATH_VARIABLES = View.class.getName() + ".pathVariables";
    public static final String SELECTED_CONTENT_TYPE = View.class.getName() + ".selectedContentType";

    void render(@Nullable Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception;

    @Nullable
    default String getContentType() {
        return null;
    }
}
