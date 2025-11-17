package org.apache.catalina.filters;

import ch.qos.logback.core.pattern.color.ANSIConstants;
import cn.hutool.core.text.CharSequenceUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import jakarta.servlet.http.MappingMatch;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/filters/ExpiresFilter.class */
public class ExpiresFilter extends FilterBase {
    private static final Pattern commaSeparatedValuesPattern = Pattern.compile("\\s*,\\s*");
    private static final String HEADER_CACHE_CONTROL = "Cache-Control";
    private static final String HEADER_EXPIRES = "Expires";
    private static final String HEADER_LAST_MODIFIED = "Last-Modified";
    private static final String PARAMETER_EXPIRES_BY_TYPE = "ExpiresByType";
    private static final String PARAMETER_EXPIRES_DEFAULT = "ExpiresDefault";
    private static final String PARAMETER_EXPIRES_EXCLUDED_RESPONSE_STATUS_CODES = "ExpiresExcludedResponseStatusCodes";
    private ExpiresConfiguration defaultExpiresConfiguration;
    private final Log log = LogFactory.getLog((Class<?>) ExpiresFilter.class);
    private int[] excludedResponseStatusCodes = {304};
    private Map<String, ExpiresConfiguration> expiresConfigurationByContentType = new LinkedHashMap();

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/filters/ExpiresFilter$StartingPoint.class */
    public enum StartingPoint {
        ACCESS_TIME,
        LAST_MODIFICATION_TIME
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/filters/ExpiresFilter$Duration.class */
    public static class Duration {
        protected final int amount;
        protected final DurationUnit unit;

        public Duration(int amount, DurationUnit unit) {
            this.amount = amount;
            this.unit = unit;
        }

        public int getAmount() {
            return this.amount;
        }

        public DurationUnit getUnit() {
            return this.unit;
        }

        public String toString() {
            return this.amount + " " + this.unit;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/filters/ExpiresFilter$DurationUnit.class */
    public enum DurationUnit {
        DAY(6),
        HOUR(10),
        MINUTE(12),
        MONTH(2),
        SECOND(13),
        WEEK(3),
        YEAR(1);

        private final int calendarField;

        DurationUnit(int calendarField) {
            this.calendarField = calendarField;
        }

        public int getCalendardField() {
            return this.calendarField;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/filters/ExpiresFilter$ExpiresConfiguration.class */
    public static class ExpiresConfiguration {
        private final List<Duration> durations;
        private final StartingPoint startingPoint;

        public ExpiresConfiguration(StartingPoint startingPoint, List<Duration> durations) {
            this.startingPoint = startingPoint;
            this.durations = durations;
        }

        public List<Duration> getDurations() {
            return this.durations;
        }

        public StartingPoint getStartingPoint() {
            return this.startingPoint;
        }

        public String toString() {
            return "ExpiresConfiguration[startingPoint=" + this.startingPoint + ", duration=" + this.durations + "]";
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/filters/ExpiresFilter$XHttpServletResponse.class */
    public class XHttpServletResponse extends HttpServletResponseWrapper {
        private String cacheControlHeader;
        private long lastModifiedHeader;
        private boolean lastModifiedHeaderSet;
        private PrintWriter printWriter;
        private final HttpServletRequest request;
        private ServletOutputStream servletOutputStream;
        private boolean writeResponseBodyStarted;

        public XHttpServletResponse(HttpServletRequest request, HttpServletResponse response) {
            super(response);
            this.request = request;
        }

        @Override // jakarta.servlet.http.HttpServletResponseWrapper, jakarta.servlet.http.HttpServletResponse
        public void addDateHeader(String name, long date) {
            super.addDateHeader(name, date);
            if (!this.lastModifiedHeaderSet) {
                this.lastModifiedHeader = date;
                this.lastModifiedHeaderSet = true;
            }
        }

        @Override // jakarta.servlet.http.HttpServletResponseWrapper, jakarta.servlet.http.HttpServletResponse
        public void addHeader(String name, String value) {
            super.addHeader(name, value);
            if ("Cache-Control".equalsIgnoreCase(name) && this.cacheControlHeader == null) {
                this.cacheControlHeader = value;
            }
        }

        public String getCacheControlHeader() {
            return this.cacheControlHeader;
        }

        public long getLastModifiedHeader() {
            return this.lastModifiedHeader;
        }

        @Override // jakarta.servlet.ServletResponseWrapper, jakarta.servlet.ServletResponse
        public ServletOutputStream getOutputStream() throws IOException {
            if (this.servletOutputStream == null) {
                this.servletOutputStream = new XServletOutputStream(super.getOutputStream(), this.request, this);
            }
            return this.servletOutputStream;
        }

        @Override // jakarta.servlet.ServletResponseWrapper, jakarta.servlet.ServletResponse
        public PrintWriter getWriter() throws IOException {
            if (this.printWriter == null) {
                this.printWriter = new XPrintWriter(super.getWriter(), this.request, this);
            }
            return this.printWriter;
        }

        public boolean isLastModifiedHeaderSet() {
            return this.lastModifiedHeaderSet;
        }

        public boolean isWriteResponseBodyStarted() {
            return this.writeResponseBodyStarted;
        }

        @Override // jakarta.servlet.ServletResponseWrapper, jakarta.servlet.ServletResponse
        public void reset() {
            super.reset();
            this.lastModifiedHeader = 0L;
            this.lastModifiedHeaderSet = false;
            this.cacheControlHeader = null;
        }

        @Override // jakarta.servlet.http.HttpServletResponseWrapper, jakarta.servlet.http.HttpServletResponse
        public void setDateHeader(String name, long date) {
            super.setDateHeader(name, date);
            if ("Last-Modified".equalsIgnoreCase(name)) {
                this.lastModifiedHeader = date;
                this.lastModifiedHeaderSet = true;
            }
        }

        @Override // jakarta.servlet.http.HttpServletResponseWrapper, jakarta.servlet.http.HttpServletResponse
        public void setHeader(String name, String value) {
            super.setHeader(name, value);
            if ("Cache-Control".equalsIgnoreCase(name)) {
                this.cacheControlHeader = value;
            }
        }

        public void setWriteResponseBodyStarted(boolean writeResponseBodyStarted) {
            this.writeResponseBodyStarted = writeResponseBodyStarted;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/filters/ExpiresFilter$XPrintWriter.class */
    public class XPrintWriter extends PrintWriter {
        private final PrintWriter out;
        private final HttpServletRequest request;
        private final XHttpServletResponse response;

        public XPrintWriter(PrintWriter out, HttpServletRequest request, XHttpServletResponse response) {
            super(out);
            this.out = out;
            this.request = request;
            this.response = response;
        }

        @Override // java.io.PrintWriter, java.io.Writer, java.lang.Appendable
        public PrintWriter append(char c) {
            fireBeforeWriteResponseBodyEvent();
            return this.out.append(c);
        }

        @Override // java.io.PrintWriter, java.io.Writer, java.lang.Appendable
        public PrintWriter append(CharSequence csq) {
            fireBeforeWriteResponseBodyEvent();
            return this.out.append(csq);
        }

        @Override // java.io.PrintWriter, java.io.Writer, java.lang.Appendable
        public PrintWriter append(CharSequence csq, int start, int end) {
            fireBeforeWriteResponseBodyEvent();
            return this.out.append(csq, start, end);
        }

        @Override // java.io.PrintWriter, java.io.Writer, java.io.Closeable, java.lang.AutoCloseable
        public void close() {
            fireBeforeWriteResponseBodyEvent();
            this.out.close();
        }

        private void fireBeforeWriteResponseBodyEvent() {
            if (!this.response.isWriteResponseBodyStarted()) {
                this.response.setWriteResponseBodyStarted(true);
                ExpiresFilter.this.onBeforeWriteResponseBody(this.request, this.response);
            }
        }

        @Override // java.io.PrintWriter, java.io.Writer, java.io.Flushable
        public void flush() {
            fireBeforeWriteResponseBodyEvent();
            this.out.flush();
        }

        @Override // java.io.PrintWriter
        public void print(boolean b) {
            fireBeforeWriteResponseBodyEvent();
            this.out.print(b);
        }

        @Override // java.io.PrintWriter
        public void print(char c) {
            fireBeforeWriteResponseBodyEvent();
            this.out.print(c);
        }

        @Override // java.io.PrintWriter
        public void print(char[] s) {
            fireBeforeWriteResponseBodyEvent();
            this.out.print(s);
        }

        @Override // java.io.PrintWriter
        public void print(double d) {
            fireBeforeWriteResponseBodyEvent();
            this.out.print(d);
        }

        @Override // java.io.PrintWriter
        public void print(float f) {
            fireBeforeWriteResponseBodyEvent();
            this.out.print(f);
        }

        @Override // java.io.PrintWriter
        public void print(int i) {
            fireBeforeWriteResponseBodyEvent();
            this.out.print(i);
        }

        @Override // java.io.PrintWriter
        public void print(long l) {
            fireBeforeWriteResponseBodyEvent();
            this.out.print(l);
        }

        @Override // java.io.PrintWriter
        public void print(Object obj) {
            fireBeforeWriteResponseBodyEvent();
            this.out.print(obj);
        }

        @Override // java.io.PrintWriter
        public void print(String s) {
            fireBeforeWriteResponseBodyEvent();
            this.out.print(s);
        }

        @Override // java.io.PrintWriter
        public PrintWriter printf(Locale l, String format, Object... args) {
            fireBeforeWriteResponseBodyEvent();
            return this.out.printf(l, format, args);
        }

        @Override // java.io.PrintWriter
        public PrintWriter printf(String format, Object... args) {
            fireBeforeWriteResponseBodyEvent();
            return this.out.printf(format, args);
        }

        @Override // java.io.PrintWriter
        public void println() {
            fireBeforeWriteResponseBodyEvent();
            this.out.println();
        }

        @Override // java.io.PrintWriter
        public void println(boolean x) {
            fireBeforeWriteResponseBodyEvent();
            this.out.println(x);
        }

        @Override // java.io.PrintWriter
        public void println(char x) {
            fireBeforeWriteResponseBodyEvent();
            this.out.println(x);
        }

        @Override // java.io.PrintWriter
        public void println(char[] x) {
            fireBeforeWriteResponseBodyEvent();
            this.out.println(x);
        }

        @Override // java.io.PrintWriter
        public void println(double x) {
            fireBeforeWriteResponseBodyEvent();
            this.out.println(x);
        }

        @Override // java.io.PrintWriter
        public void println(float x) {
            fireBeforeWriteResponseBodyEvent();
            this.out.println(x);
        }

        @Override // java.io.PrintWriter
        public void println(int x) {
            fireBeforeWriteResponseBodyEvent();
            this.out.println(x);
        }

        @Override // java.io.PrintWriter
        public void println(long x) {
            fireBeforeWriteResponseBodyEvent();
            this.out.println(x);
        }

        @Override // java.io.PrintWriter
        public void println(Object x) {
            fireBeforeWriteResponseBodyEvent();
            this.out.println(x);
        }

        @Override // java.io.PrintWriter
        public void println(String x) {
            fireBeforeWriteResponseBodyEvent();
            this.out.println(x);
        }

        @Override // java.io.PrintWriter, java.io.Writer
        public void write(char[] buf) {
            fireBeforeWriteResponseBodyEvent();
            this.out.write(buf);
        }

        @Override // java.io.PrintWriter, java.io.Writer
        public void write(char[] buf, int off, int len) {
            fireBeforeWriteResponseBodyEvent();
            this.out.write(buf, off, len);
        }

        @Override // java.io.PrintWriter, java.io.Writer
        public void write(int c) {
            fireBeforeWriteResponseBodyEvent();
            this.out.write(c);
        }

        @Override // java.io.PrintWriter, java.io.Writer
        public void write(String s) {
            fireBeforeWriteResponseBodyEvent();
            this.out.write(s);
        }

        @Override // java.io.PrintWriter, java.io.Writer
        public void write(String s, int off, int len) {
            fireBeforeWriteResponseBodyEvent();
            this.out.write(s, off, len);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/filters/ExpiresFilter$XServletOutputStream.class */
    public class XServletOutputStream extends ServletOutputStream {
        private final HttpServletRequest request;
        private final XHttpServletResponse response;
        private final ServletOutputStream servletOutputStream;

        public XServletOutputStream(ServletOutputStream servletOutputStream, HttpServletRequest request, XHttpServletResponse response) {
            this.servletOutputStream = servletOutputStream;
            this.response = response;
            this.request = request;
        }

        @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.close();
        }

        private void fireOnBeforeWriteResponseBodyEvent() {
            if (!this.response.isWriteResponseBodyStarted()) {
                this.response.setWriteResponseBodyStarted(true);
                ExpiresFilter.this.onBeforeWriteResponseBody(this.request, this.response);
            }
        }

        @Override // java.io.OutputStream, java.io.Flushable
        public void flush() throws IOException {
            fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.flush();
        }

        @Override // jakarta.servlet.ServletOutputStream
        public void print(boolean b) throws IOException {
            fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.print(b);
        }

        @Override // jakarta.servlet.ServletOutputStream
        public void print(char c) throws IOException {
            fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.print(c);
        }

        @Override // jakarta.servlet.ServletOutputStream
        public void print(double d) throws IOException {
            fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.print(d);
        }

        @Override // jakarta.servlet.ServletOutputStream
        public void print(float f) throws IOException {
            fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.print(f);
        }

        @Override // jakarta.servlet.ServletOutputStream
        public void print(int i) throws IOException {
            fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.print(i);
        }

        @Override // jakarta.servlet.ServletOutputStream
        public void print(long l) throws IOException {
            fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.print(l);
        }

        @Override // jakarta.servlet.ServletOutputStream
        public void print(String s) throws IOException {
            fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.print(s);
        }

        @Override // jakarta.servlet.ServletOutputStream
        public void println() throws IOException {
            fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.println();
        }

        @Override // jakarta.servlet.ServletOutputStream
        public void println(boolean b) throws IOException {
            fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.println(b);
        }

        @Override // jakarta.servlet.ServletOutputStream
        public void println(char c) throws IOException {
            fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.println(c);
        }

        @Override // jakarta.servlet.ServletOutputStream
        public void println(double d) throws IOException {
            fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.println(d);
        }

        @Override // jakarta.servlet.ServletOutputStream
        public void println(float f) throws IOException {
            fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.println(f);
        }

        @Override // jakarta.servlet.ServletOutputStream
        public void println(int i) throws IOException {
            fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.println(i);
        }

        @Override // jakarta.servlet.ServletOutputStream
        public void println(long l) throws IOException {
            fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.println(l);
        }

        @Override // jakarta.servlet.ServletOutputStream
        public void println(String s) throws IOException {
            fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.println(s);
        }

        @Override // java.io.OutputStream
        public void write(byte[] b) throws IOException {
            fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.write(b);
        }

        @Override // java.io.OutputStream
        public void write(byte[] b, int off, int len) throws IOException {
            fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.write(b, off, len);
        }

        @Override // java.io.OutputStream
        public void write(int b) throws IOException {
            fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.write(b);
        }

        @Override // jakarta.servlet.ServletOutputStream
        public boolean isReady() {
            return false;
        }

        @Override // jakarta.servlet.ServletOutputStream
        public void setWriteListener(WriteListener listener) {
        }
    }

    protected static int[] commaDelimitedListToIntArray(String commaDelimitedInts) {
        String[] intsAsStrings = commaDelimitedListToStringArray(commaDelimitedInts);
        int[] ints = new int[intsAsStrings.length];
        for (int i = 0; i < intsAsStrings.length; i++) {
            String intAsString = intsAsStrings[i];
            try {
                ints[i] = Integer.parseInt(intAsString);
            } catch (NumberFormatException e) {
                throw new RuntimeException(sm.getString("expiresFilter.numberError", Integer.valueOf(i), commaDelimitedInts));
            }
        }
        return ints;
    }

    protected static String[] commaDelimitedListToStringArray(String commaDelimitedStrings) {
        return (commaDelimitedStrings == null || commaDelimitedStrings.length() == 0) ? new String[0] : commaSeparatedValuesPattern.split(commaDelimitedStrings);
    }

    protected static boolean contains(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }
        return str.contains(searchStr);
    }

    protected static String intsToCommaDelimitedString(int[] ints) {
        if (ints == null) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < ints.length; i++) {
            result.append(ints[i]);
            if (i < ints.length - 1) {
                result.append(", ");
            }
        }
        return result.toString();
    }

    protected static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    protected static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    protected static boolean startsWithIgnoreCase(String string, String prefix) {
        if (string == null || prefix == null) {
            return string == null && prefix == null;
        }
        if (prefix.length() > string.length()) {
            return false;
        }
        return string.regionMatches(true, 0, prefix, 0, prefix.length());
    }

    protected static String substringBefore(String str, String separator) {
        if (str == null || str.isEmpty() || separator == null) {
            return null;
        }
        if (separator.isEmpty()) {
            return "";
        }
        int separatorIndex = str.indexOf(separator);
        if (separatorIndex == -1) {
            return str;
        }
        return str.substring(0, separatorIndex);
    }

    @Override // jakarta.servlet.Filter
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if ((request instanceof HttpServletRequest) && (response instanceof HttpServletResponse)) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            if (response.isCommitted()) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug(sm.getString("expiresFilter.responseAlreadyCommitted", httpRequest.getRequestURL()));
                }
                chain.doFilter(request, response);
                return;
            } else {
                XHttpServletResponse xResponse = new XHttpServletResponse(httpRequest, httpResponse);
                chain.doFilter(request, xResponse);
                if (!xResponse.isWriteResponseBodyStarted()) {
                    onBeforeWriteResponseBody(httpRequest, xResponse);
                    return;
                }
                return;
            }
        }
        chain.doFilter(request, response);
    }

    public ExpiresConfiguration getDefaultExpiresConfiguration() {
        return this.defaultExpiresConfiguration;
    }

    public String getExcludedResponseStatusCodes() {
        return intsToCommaDelimitedString(this.excludedResponseStatusCodes);
    }

    public int[] getExcludedResponseStatusCodesAsInts() {
        return this.excludedResponseStatusCodes;
    }

    protected Date getExpirationDate(HttpServletRequest request, XHttpServletResponse response) {
        String majorType;
        ExpiresConfiguration configuration;
        String contentTypeWithoutCharset;
        ExpiresConfiguration configuration2;
        String servletPath;
        int lastSlash;
        String contentType = response.getContentType();
        if (contentType == null && request != null && request.getHttpServletMapping().getMappingMatch() == MappingMatch.DEFAULT && response.getStatus() == 304 && (servletPath = request.getServletPath()) != null && (lastSlash = servletPath.lastIndexOf(47)) > -1) {
            String fileName = servletPath.substring(lastSlash + 1);
            contentType = request.getServletContext().getMimeType(fileName);
        }
        if (contentType != null) {
            contentType = contentType.toLowerCase(Locale.ENGLISH);
        }
        ExpiresConfiguration configuration3 = this.expiresConfigurationByContentType.get(contentType);
        if (configuration3 != null) {
            Date result = getExpirationDate(configuration3, response);
            if (this.log.isDebugEnabled()) {
                this.log.debug(sm.getString("expiresFilter.useMatchingConfiguration", configuration3, contentType, contentType, result));
            }
            return result;
        }
        if (contains(contentType, ";") && (configuration2 = this.expiresConfigurationByContentType.get((contentTypeWithoutCharset = substringBefore(contentType, ";").trim()))) != null) {
            Date result2 = getExpirationDate(configuration2, response);
            if (this.log.isDebugEnabled()) {
                this.log.debug(sm.getString("expiresFilter.useMatchingConfiguration", configuration2, contentTypeWithoutCharset, contentType, result2));
            }
            return result2;
        }
        if (contains(contentType, "/") && (configuration = this.expiresConfigurationByContentType.get((majorType = substringBefore(contentType, "/")))) != null) {
            Date result3 = getExpirationDate(configuration, response);
            if (this.log.isDebugEnabled()) {
                this.log.debug(sm.getString("expiresFilter.useMatchingConfiguration", configuration, majorType, contentType, result3));
            }
            return result3;
        }
        if (this.defaultExpiresConfiguration != null) {
            Date result4 = getExpirationDate(this.defaultExpiresConfiguration, response);
            if (this.log.isDebugEnabled()) {
                this.log.debug(sm.getString("expiresFilter.useDefaultConfiguration", this.defaultExpiresConfiguration, contentType, result4));
            }
            return result4;
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug(sm.getString("expiresFilter.noExpirationConfiguredForContentType", contentType));
            return null;
        }
        return null;
    }

    protected Date getExpirationDate(ExpiresConfiguration configuration, XHttpServletResponse response) {
        Calendar calendar;
        switch (configuration.getStartingPoint()) {
            case ACCESS_TIME:
                calendar = Calendar.getInstance();
                break;
            case LAST_MODIFICATION_TIME:
                if (response.isLastModifiedHeaderSet()) {
                    try {
                        long lastModified = response.getLastModifiedHeader();
                        calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(lastModified);
                        break;
                    } catch (NumberFormatException e) {
                        calendar = Calendar.getInstance();
                        break;
                    }
                } else {
                    calendar = Calendar.getInstance();
                    break;
                }
            default:
                throw new IllegalStateException(sm.getString("expiresFilter.unsupportedStartingPoint", configuration.getStartingPoint()));
        }
        for (Duration duration : configuration.getDurations()) {
            calendar.add(duration.getUnit().getCalendardField(), duration.getAmount());
        }
        return calendar.getTime();
    }

    public Map<String, ExpiresConfiguration> getExpiresConfigurationByContentType() {
        return this.expiresConfigurationByContentType;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.filters.FilterBase
    public Log getLogger() {
        return this.log;
    }

    @Override // org.apache.catalina.filters.FilterBase, jakarta.servlet.Filter
    public void init(FilterConfig filterConfig) throws ServletException {
        Enumeration<String> names = filterConfig.getInitParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            String value = filterConfig.getInitParameter(name);
            try {
                if (name.startsWith(PARAMETER_EXPIRES_BY_TYPE)) {
                    String contentType = name.substring(PARAMETER_EXPIRES_BY_TYPE.length()).trim().toLowerCase(Locale.ENGLISH);
                    ExpiresConfiguration expiresConfiguration = parseExpiresConfiguration(value);
                    this.expiresConfigurationByContentType.put(contentType, expiresConfiguration);
                } else if (name.equalsIgnoreCase(PARAMETER_EXPIRES_DEFAULT)) {
                    ExpiresConfiguration expiresConfiguration2 = parseExpiresConfiguration(value);
                    this.defaultExpiresConfiguration = expiresConfiguration2;
                } else if (name.equalsIgnoreCase(PARAMETER_EXPIRES_EXCLUDED_RESPONSE_STATUS_CODES)) {
                    this.excludedResponseStatusCodes = commaDelimitedListToIntArray(value);
                } else {
                    this.log.warn(sm.getString("expiresFilter.unknownParameterIgnored", name, value));
                }
            } catch (RuntimeException e) {
                throw new ServletException(sm.getString("expiresFilter.exceptionProcessingParameter", name, value), e);
            }
        }
        this.log.debug(sm.getString("expiresFilter.filterInitialized", toString()));
    }

    protected boolean isEligibleToExpirationHeaderGeneration(HttpServletRequest request, XHttpServletResponse response) {
        boolean expirationHeaderHasBeenSet = response.containsHeader("Expires") || contains(response.getCacheControlHeader(), "max-age");
        if (expirationHeaderHasBeenSet) {
            if (this.log.isDebugEnabled()) {
                this.log.debug(sm.getString("expiresFilter.expirationHeaderAlreadyDefined", request.getRequestURI(), Integer.valueOf(response.getStatus()), response.getContentType()));
                return false;
            }
            return false;
        }
        for (int skippedStatusCode : this.excludedResponseStatusCodes) {
            if (response.getStatus() == skippedStatusCode) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug(sm.getString("expiresFilter.skippedStatusCode", request.getRequestURI(), Integer.valueOf(response.getStatus()), response.getContentType()));
                    return false;
                }
                return false;
            }
        }
        return true;
    }

    public void onBeforeWriteResponseBody(HttpServletRequest request, XHttpServletResponse response) {
        if (!isEligibleToExpirationHeaderGeneration(request, response)) {
            return;
        }
        Date expirationDate = getExpirationDate(request, response);
        if (expirationDate == null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug(sm.getString("expiresFilter.noExpirationConfigured", request.getRequestURI(), Integer.valueOf(response.getStatus()), response.getContentType()));
                return;
            }
            return;
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug(sm.getString("expiresFilter.setExpirationDate", request.getRequestURI(), Integer.valueOf(response.getStatus()), response.getContentType(), expirationDate));
        }
        String maxAgeDirective = "max-age=" + ((expirationDate.getTime() - System.currentTimeMillis()) / 1000);
        String cacheControlHeader = response.getCacheControlHeader();
        String newCacheControlHeader = cacheControlHeader == null ? maxAgeDirective : cacheControlHeader + ", " + maxAgeDirective;
        response.setHeader("Cache-Control", newCacheControlHeader);
        response.setDateHeader("Expires", expirationDate.getTime());
    }

    protected ExpiresConfiguration parseExpiresConfiguration(String inputLine) {
        StartingPoint startingPoint;
        DurationUnit durationUnit;
        String line = inputLine.trim();
        StringTokenizer tokenizer = new StringTokenizer(line, CharSequenceUtil.SPACE);
        try {
            String currentToken = tokenizer.nextToken();
            if ("access".equalsIgnoreCase(currentToken) || "now".equalsIgnoreCase(currentToken)) {
                startingPoint = StartingPoint.ACCESS_TIME;
            } else if ("modification".equalsIgnoreCase(currentToken)) {
                startingPoint = StartingPoint.LAST_MODIFICATION_TIME;
            } else if (!tokenizer.hasMoreTokens() && startsWithIgnoreCase(currentToken, "a")) {
                startingPoint = StartingPoint.ACCESS_TIME;
                tokenizer = new StringTokenizer(currentToken.substring(1) + " seconds", CharSequenceUtil.SPACE);
            } else if (!tokenizer.hasMoreTokens() && startsWithIgnoreCase(currentToken, ANSIConstants.ESC_END)) {
                startingPoint = StartingPoint.LAST_MODIFICATION_TIME;
                tokenizer = new StringTokenizer(currentToken.substring(1) + " seconds", CharSequenceUtil.SPACE);
            } else {
                throw new IllegalStateException(sm.getString("expiresFilter.startingPointInvalid", currentToken, line));
            }
            try {
                String currentToken2 = tokenizer.nextToken();
                if ("plus".equalsIgnoreCase(currentToken2)) {
                    try {
                        currentToken2 = tokenizer.nextToken();
                    } catch (NoSuchElementException e) {
                        throw new IllegalStateException(sm.getString("expiresFilter.noDurationFound", line));
                    }
                }
                List<Duration> durations = new ArrayList<>();
                while (currentToken2 != null) {
                    try {
                        int amount = Integer.parseInt(currentToken2);
                        try {
                            String currentToken3 = tokenizer.nextToken();
                            if ("year".equalsIgnoreCase(currentToken3) || "years".equalsIgnoreCase(currentToken3)) {
                                durationUnit = DurationUnit.YEAR;
                            } else if ("month".equalsIgnoreCase(currentToken3) || "months".equalsIgnoreCase(currentToken3)) {
                                durationUnit = DurationUnit.MONTH;
                            } else if ("week".equalsIgnoreCase(currentToken3) || "weeks".equalsIgnoreCase(currentToken3)) {
                                durationUnit = DurationUnit.WEEK;
                            } else if ("day".equalsIgnoreCase(currentToken3) || "days".equalsIgnoreCase(currentToken3)) {
                                durationUnit = DurationUnit.DAY;
                            } else if ("hour".equalsIgnoreCase(currentToken3) || "hours".equalsIgnoreCase(currentToken3)) {
                                durationUnit = DurationUnit.HOUR;
                            } else if ("minute".equalsIgnoreCase(currentToken3) || "minutes".equalsIgnoreCase(currentToken3)) {
                                durationUnit = DurationUnit.MINUTE;
                            } else if ("second".equalsIgnoreCase(currentToken3) || "seconds".equalsIgnoreCase(currentToken3)) {
                                durationUnit = DurationUnit.SECOND;
                            } else {
                                throw new IllegalStateException(sm.getString("expiresFilter.invalidDurationUnit", currentToken3, line));
                            }
                            Duration duration = new Duration(amount, durationUnit);
                            durations.add(duration);
                            if (tokenizer.hasMoreTokens()) {
                                currentToken2 = tokenizer.nextToken();
                            } else {
                                currentToken2 = null;
                            }
                        } catch (NoSuchElementException e2) {
                            throw new IllegalStateException(sm.getString("expiresFilter.noDurationUnitAfterAmount", Integer.valueOf(amount), line));
                        }
                    } catch (NumberFormatException e3) {
                        throw new IllegalStateException(sm.getString("expiresFilter.invalidDurationNumber", currentToken2, line));
                    }
                }
                return new ExpiresConfiguration(startingPoint, durations);
            } catch (NoSuchElementException e4) {
                throw new IllegalStateException(sm.getString("expiresFilter.noDurationFound", line));
            }
        } catch (NoSuchElementException e5) {
            throw new IllegalStateException(sm.getString("expiresFilter.startingPointNotFound", line));
        }
    }

    public void setDefaultExpiresConfiguration(ExpiresConfiguration defaultExpiresConfiguration) {
        this.defaultExpiresConfiguration = defaultExpiresConfiguration;
    }

    public void setExcludedResponseStatusCodes(int[] excludedResponseStatusCodes) {
        this.excludedResponseStatusCodes = excludedResponseStatusCodes;
    }

    public void setExpiresConfigurationByContentType(Map<String, ExpiresConfiguration> expiresConfigurationByContentType) {
        this.expiresConfigurationByContentType = expiresConfigurationByContentType;
    }

    public String toString() {
        return getClass().getSimpleName() + "[excludedResponseStatusCode=[" + intsToCommaDelimitedString(this.excludedResponseStatusCodes) + "], default=" + this.defaultExpiresConfiguration + ", byType=" + this.expiresConfigurationByContentType + "]";
    }
}
