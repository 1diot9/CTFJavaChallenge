package org.springframework.web.servlet.mvc;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.WebContentGenerator;
import org.springframework.web.util.WebUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/AbstractController.class */
public abstract class AbstractController extends WebContentGenerator implements Controller {
    private boolean synchronizeOnSession;

    @Nullable
    protected abstract ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception;

    public AbstractController() {
        this(true);
    }

    public AbstractController(boolean restrictDefaultSupportedMethods) {
        super(restrictDefaultSupportedMethods);
        this.synchronizeOnSession = false;
    }

    public final void setSynchronizeOnSession(boolean synchronizeOnSession) {
        this.synchronizeOnSession = synchronizeOnSession;
    }

    public final boolean isSynchronizeOnSession() {
        return this.synchronizeOnSession;
    }

    @Override // org.springframework.web.servlet.mvc.Controller
    @Nullable
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session;
        ModelAndView handleRequestInternal;
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            response.setHeader(HttpHeaders.ALLOW, getAllowHeader());
            return null;
        }
        checkRequest(request);
        prepareResponse(response);
        if (this.synchronizeOnSession && (session = request.getSession(false)) != null) {
            Object mutex = WebUtils.getSessionMutex(session);
            synchronized (mutex) {
                handleRequestInternal = handleRequestInternal(request, response);
            }
            return handleRequestInternal;
        }
        return handleRequestInternal(request, response);
    }
}
