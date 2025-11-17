package org.apache.catalina.authenticator.jaspic;

import jakarta.security.auth.message.MessageInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/authenticator/jaspic/MessageInfoImpl.class */
public class MessageInfoImpl implements MessageInfo {
    protected static final StringManager sm = StringManager.getManager((Class<?>) MessageInfoImpl.class);
    public static final String IS_MANDATORY = "jakarta.security.auth.message.MessagePolicy.isMandatory";
    private final Map<String, Object> map = new HashMap();
    private HttpServletRequest request;
    private HttpServletResponse response;

    public MessageInfoImpl() {
    }

    public MessageInfoImpl(HttpServletRequest request, HttpServletResponse response, boolean authMandatory) {
        this.request = request;
        this.response = response;
        this.map.put(IS_MANDATORY, Boolean.toString(authMandatory));
    }

    @Override // jakarta.security.auth.message.MessageInfo
    public Map<String, Object> getMap() {
        return this.map;
    }

    @Override // jakarta.security.auth.message.MessageInfo
    public Object getRequestMessage() {
        return this.request;
    }

    @Override // jakarta.security.auth.message.MessageInfo
    public Object getResponseMessage() {
        return this.response;
    }

    @Override // jakarta.security.auth.message.MessageInfo
    public void setRequestMessage(Object request) {
        if (!(request instanceof HttpServletRequest)) {
            throw new IllegalArgumentException(sm.getString("authenticator.jaspic.badRequestType", request.getClass().getName()));
        }
        this.request = (HttpServletRequest) request;
    }

    @Override // jakarta.security.auth.message.MessageInfo
    public void setResponseMessage(Object response) {
        if (!(response instanceof HttpServletResponse)) {
            throw new IllegalArgumentException(sm.getString("authenticator.jaspic.badResponseType", response.getClass().getName()));
        }
        this.response = (HttpServletResponse) response;
    }
}
