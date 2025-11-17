package org.apache.catalina.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.GenericFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import org.apache.catalina.util.TimeBucketCounter;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.threads.ScheduledThreadPoolExecutor;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/filters/RateLimitFilter.class */
public class RateLimitFilter extends GenericFilter {
    private static final long serialVersionUID = 1;
    public static final int DEFAULT_BUCKET_DURATION = 60;
    public static final int DEFAULT_BUCKET_REQUESTS = 300;
    public static final boolean DEFAULT_ENFORCE = true;
    public static final int DEFAULT_STATUS_CODE = 429;
    public static final String DEFAULT_STATUS_MESSAGE = "Too many requests";
    public static final String RATE_LIMIT_ATTRIBUTE_COUNT = "org.apache.catalina.filters.RateLimitFilter.Count";
    public static final String PARAM_BUCKET_DURATION = "bucketDuration";
    public static final String PARAM_BUCKET_REQUESTS = "bucketRequests";
    public static final String PARAM_ENFORCE = "enforce";
    public static final String PARAM_STATUS_CODE = "statusCode";
    public static final String PARAM_STATUS_MESSAGE = "statusMessage";
    transient TimeBucketCounter bucketCounter;
    private int actualRequests;
    private int bucketRequests = 300;
    private int bucketDuration = 60;
    private boolean enforce = true;
    private int statusCode = DEFAULT_STATUS_CODE;
    private String statusMessage = DEFAULT_STATUS_MESSAGE;
    private transient Log log = LogFactory.getLog((Class<?>) RateLimitFilter.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) RateLimitFilter.class);

    public int getActualRequests() {
        return this.actualRequests;
    }

    public int getActualDurationInSeconds() {
        return this.bucketCounter.getActualDuration() / 1000;
    }

    @Override // jakarta.servlet.GenericFilter
    public void init() throws ServletException {
        FilterConfig config = getFilterConfig();
        String param = config.getInitParameter(PARAM_BUCKET_DURATION);
        if (param != null) {
            this.bucketDuration = Integer.parseInt(param);
        }
        String param2 = config.getInitParameter(PARAM_BUCKET_REQUESTS);
        if (param2 != null) {
            this.bucketRequests = Integer.parseInt(param2);
        }
        String param3 = config.getInitParameter(PARAM_ENFORCE);
        if (param3 != null) {
            this.enforce = Boolean.parseBoolean(param3);
        }
        String param4 = config.getInitParameter(PARAM_STATUS_CODE);
        if (param4 != null) {
            this.statusCode = Integer.parseInt(param4);
        }
        String param5 = config.getInitParameter(PARAM_STATUS_MESSAGE);
        if (param5 != null) {
            this.statusMessage = param5;
        }
        ScheduledExecutorService executorService = (ScheduledExecutorService) getServletContext().getAttribute(ScheduledThreadPoolExecutor.class.getName());
        if (executorService == null) {
            executorService = new java.util.concurrent.ScheduledThreadPoolExecutor(1);
        }
        this.bucketCounter = new TimeBucketCounter(this.bucketDuration, executorService);
        this.actualRequests = (int) Math.round(this.bucketCounter.getRatio() * this.bucketRequests);
        Log log = this.log;
        StringManager stringManager = sm;
        Object[] objArr = new Object[6];
        objArr[0] = super.getFilterName();
        objArr[1] = Integer.valueOf(this.bucketRequests);
        objArr[2] = Integer.valueOf(this.bucketDuration);
        objArr[3] = Integer.valueOf(getActualRequests());
        objArr[4] = Integer.valueOf(getActualDurationInSeconds());
        objArr[5] = (!this.enforce ? "Not " : "") + "enforcing";
        log.info(stringManager.getString("rateLimitFilter.initialized", objArr));
    }

    @Override // jakarta.servlet.Filter
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String ipAddr = request.getRemoteAddr();
        int reqCount = this.bucketCounter.increment(ipAddr);
        request.setAttribute(RATE_LIMIT_ATTRIBUTE_COUNT, Integer.valueOf(reqCount));
        if (this.enforce && reqCount > this.actualRequests) {
            ((HttpServletResponse) response).sendError(this.statusCode, this.statusMessage);
            this.log.warn(sm.getString("rateLimitFilter.maxRequestsExceeded", super.getFilterName(), Integer.valueOf(reqCount), ipAddr, Integer.valueOf(getActualRequests()), Integer.valueOf(getActualDurationInSeconds())));
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override // jakarta.servlet.Filter
    public void destroy() {
        this.bucketCounter.destroy();
        super.destroy();
    }
}
