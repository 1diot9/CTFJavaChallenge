package ch.qos.logback.classic.selector.servlet;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.selector.ContextJNDISelector;
import ch.qos.logback.classic.selector.ContextSelector;
import ch.qos.logback.classic.util.ContextSelectorStaticBinder;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;
import org.slf4j.LoggerFactory;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/selector/servlet/LoggerContextFilter.class */
public class LoggerContextFilter implements Filter {
    @Override // jakarta.servlet.Filter
    public void destroy() {
    }

    @Override // jakarta.servlet.Filter
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        ContextSelector selector = ContextSelectorStaticBinder.getSingleton().getContextSelector();
        ContextJNDISelector sel = null;
        if (selector instanceof ContextJNDISelector) {
            sel = (ContextJNDISelector) selector;
            sel.setLocalContext(lc);
        }
        try {
            chain.doFilter(request, response);
            if (sel != null) {
                sel.removeLocalContext();
            }
        } catch (Throwable th) {
            if (sel != null) {
                sel.removeLocalContext();
            }
            throw th;
        }
    }

    @Override // jakarta.servlet.Filter
    public void init(FilterConfig arg0) throws ServletException {
    }
}
