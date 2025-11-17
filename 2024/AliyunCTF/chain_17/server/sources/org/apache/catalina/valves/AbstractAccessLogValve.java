package org.apache.catalina.valves;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpSession;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.catalina.AccessLog;
import org.apache.catalina.Session;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.util.TLSUtil;
import org.apache.catalina.valves.Constants;
import org.apache.coyote.ActionCode;
import org.apache.coyote.RequestInfo;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.buf.HexUtils;
import org.apache.tomcat.util.codec.binary.BaseNCodec;
import org.apache.tomcat.util.collections.SynchronizedStack;
import org.apache.tomcat.util.net.IPv6Utils;
import org.springframework.asm.Opcodes;
import org.springframework.asm.TypeReference;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/AbstractAccessLogValve.class */
public abstract class AbstractAccessLogValve extends ValveBase implements AccessLog {
    protected boolean enabled;
    private boolean ipv6Canonical;
    protected String pattern;
    private static final int globalCacheSize = 300;
    private static final int localCacheSize = 60;
    protected String condition;
    protected String conditionIf;
    protected String localeName;
    protected Locale locale;
    protected AccessLogElement[] logElements;
    protected CachedElement[] cachedElements;
    protected boolean requestAttributesEnabled;
    private SynchronizedStack<CharArrayWriter> charArrayWriters;
    private int maxLogMessageBufferSize;
    private boolean tlsAttributeRequired;
    private static final Log log = LogFactory.getLog((Class<?>) AbstractAccessLogValve.class);
    private static final DateFormatCache globalDateCache = new DateFormatCache(300, Locale.getDefault(), null);
    private static final ThreadLocal<DateFormatCache> localDateCache = ThreadLocal.withInitial(() -> {
        return new DateFormatCache(60, Locale.getDefault(), globalDateCache);
    });
    private static final ThreadLocal<Date> localDate = ThreadLocal.withInitial(Date::new);

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/AbstractAccessLogValve$AccessLogElement.class */
    public interface AccessLogElement {
        void addElement(CharArrayWriter charArrayWriter, Date date, Request request, Response response, long j);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/AbstractAccessLogValve$CachedElement.class */
    public interface CachedElement {
        void cache(Request request);
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/AbstractAccessLogValve$FormatType.class */
    private enum FormatType {
        CLF,
        SEC,
        MSEC,
        MSEC_FRAC,
        SDF
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/AbstractAccessLogValve$PortType.class */
    private enum PortType {
        LOCAL,
        REMOTE
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/AbstractAccessLogValve$RemoteAddressType.class */
    private enum RemoteAddressType {
        REMOTE,
        PEER
    }

    protected abstract void log(CharArrayWriter charArrayWriter);

    public AbstractAccessLogValve() {
        super(true);
        this.enabled = true;
        this.ipv6Canonical = false;
        this.pattern = null;
        this.condition = null;
        this.conditionIf = null;
        this.localeName = Locale.getDefault().toString();
        this.locale = Locale.getDefault();
        this.logElements = null;
        this.cachedElements = null;
        this.requestAttributesEnabled = false;
        this.charArrayWriters = new SynchronizedStack<>();
        this.maxLogMessageBufferSize = 256;
        this.tlsAttributeRequired = false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/AbstractAccessLogValve$DateFormatCache.class */
    public static class DateFormatCache {
        private int cacheSize;
        private final Locale cacheDefaultLocale;
        private final DateFormatCache parent;
        protected final Cache cLFCache;
        private final Map<String, Cache> formatCache = new HashMap();

        /* JADX INFO: Access modifiers changed from: protected */
        /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/AbstractAccessLogValve$DateFormatCache$Cache.class */
        public class Cache {
            private static final String cLFFormat = "dd/MMM/yyyy:HH:mm:ss Z";
            private long previousSeconds;
            private String previousFormat;
            private long first;
            private long last;
            private int offset;
            private final Date currentDate;
            protected final String[] cache;
            private SimpleDateFormat formatter;
            private boolean isCLF;
            private Cache parent;

            private Cache(DateFormatCache this$0, Cache parent) {
                this(this$0, null, parent);
            }

            private Cache(DateFormatCache this$0, String format, Cache parent) {
                this(format, null, parent);
            }

            private Cache(String format, Locale loc, Cache parent) {
                this.previousSeconds = Long.MIN_VALUE;
                this.previousFormat = "";
                this.first = Long.MIN_VALUE;
                this.last = Long.MIN_VALUE;
                this.offset = 0;
                this.currentDate = new Date();
                this.isCLF = false;
                this.parent = null;
                this.cache = new String[DateFormatCache.this.cacheSize];
                for (int i = 0; i < DateFormatCache.this.cacheSize; i++) {
                    this.cache[i] = null;
                }
                loc = loc == null ? DateFormatCache.this.cacheDefaultLocale : loc;
                if (format == null) {
                    this.isCLF = true;
                    this.formatter = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z", Locale.US);
                } else {
                    this.formatter = new SimpleDateFormat(format, loc);
                }
                this.formatter.setTimeZone(TimeZone.getDefault());
                this.parent = parent;
            }

            private String getFormatInternal(long time) {
                long seconds = time / 1000;
                if (seconds == this.previousSeconds) {
                    return this.previousFormat;
                }
                this.previousSeconds = seconds;
                int index = (this.offset + ((int) (seconds - this.first))) % DateFormatCache.this.cacheSize;
                if (index < 0) {
                    index += DateFormatCache.this.cacheSize;
                }
                if (seconds < this.first || seconds > this.last) {
                    if (seconds >= this.last + DateFormatCache.this.cacheSize || seconds <= this.first - DateFormatCache.this.cacheSize) {
                        this.first = seconds;
                        this.last = (this.first + DateFormatCache.this.cacheSize) - 1;
                        index = 0;
                        this.offset = 0;
                        for (int i = 1; i < DateFormatCache.this.cacheSize; i++) {
                            this.cache[i] = null;
                        }
                    } else if (seconds > this.last) {
                        for (int i2 = 1; i2 < seconds - this.last; i2++) {
                            this.cache[((index + DateFormatCache.this.cacheSize) - i2) % DateFormatCache.this.cacheSize] = null;
                        }
                        this.first = seconds - (DateFormatCache.this.cacheSize - 1);
                        this.last = seconds;
                        this.offset = (index + 1) % DateFormatCache.this.cacheSize;
                    } else if (seconds < this.first) {
                        for (int i3 = 1; i3 < this.first - seconds; i3++) {
                            this.cache[(index + i3) % DateFormatCache.this.cacheSize] = null;
                        }
                        this.first = seconds;
                        this.last = seconds + (DateFormatCache.this.cacheSize - 1);
                        this.offset = index;
                    }
                } else if (this.cache[index] != null) {
                    this.previousFormat = this.cache[index];
                    return this.previousFormat;
                }
                if (this.parent != null) {
                    synchronized (this.parent) {
                        this.previousFormat = this.parent.getFormatInternal(time);
                    }
                } else {
                    this.currentDate.setTime(time);
                    this.previousFormat = this.formatter.format(this.currentDate);
                    if (this.isCLF) {
                        StringBuilder current = new StringBuilder(32);
                        current.append('[');
                        current.append(this.previousFormat);
                        current.append(']');
                        this.previousFormat = current.toString();
                    }
                }
                this.cache[index] = this.previousFormat;
                return this.previousFormat;
            }
        }

        protected DateFormatCache(int size, Locale loc, DateFormatCache parent) {
            this.cacheSize = 0;
            this.cacheSize = size;
            this.cacheDefaultLocale = loc;
            this.parent = parent;
            Cache parentCache = null;
            if (parent != null) {
                synchronized (parent) {
                    parentCache = parent.getCache(null, null);
                }
            }
            this.cLFCache = new Cache(this, parentCache);
        }

        private Cache getCache(String format, Locale loc) {
            Cache cache;
            if (format == null) {
                cache = this.cLFCache;
            } else {
                cache = this.formatCache.get(format);
                if (cache == null) {
                    Cache parentCache = null;
                    if (this.parent != null) {
                        synchronized (this.parent) {
                            parentCache = this.parent.getCache(format, loc);
                        }
                    }
                    cache = new Cache(format, loc, parentCache);
                    this.formatCache.put(format, cache);
                }
            }
            return cache;
        }

        public String getFormat(long time) {
            return this.cLFCache.getFormatInternal(time);
        }

        public String getFormat(String format, Locale loc, long time) {
            return getCache(format, loc).getFormatInternal(time);
        }
    }

    public int getMaxLogMessageBufferSize() {
        return this.maxLogMessageBufferSize;
    }

    public void setMaxLogMessageBufferSize(int maxLogMessageBufferSize) {
        this.maxLogMessageBufferSize = maxLogMessageBufferSize;
    }

    public boolean getIpv6Canonical() {
        return this.ipv6Canonical;
    }

    public void setIpv6Canonical(boolean ipv6Canonical) {
        this.ipv6Canonical = ipv6Canonical;
    }

    @Override // org.apache.catalina.AccessLog
    public void setRequestAttributesEnabled(boolean requestAttributesEnabled) {
        this.requestAttributesEnabled = requestAttributesEnabled;
    }

    @Override // org.apache.catalina.AccessLog
    public boolean getRequestAttributesEnabled() {
        return this.requestAttributesEnabled;
    }

    public boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPattern() {
        return this.pattern;
    }

    public void setPattern(String pattern) {
        if (pattern == null) {
            this.pattern = "";
        } else if (pattern.equals(Constants.AccessLog.COMMON_ALIAS)) {
            this.pattern = Constants.AccessLog.COMMON_PATTERN;
        } else if (pattern.equals(Constants.AccessLog.COMBINED_ALIAS)) {
            this.pattern = Constants.AccessLog.COMBINED_PATTERN;
        } else {
            this.pattern = pattern;
        }
        this.logElements = createLogElements();
        if (this.logElements != null) {
            this.cachedElements = createCachedElements(this.logElements);
        }
    }

    public String getCondition() {
        return this.condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getConditionUnless() {
        return getCondition();
    }

    public void setConditionUnless(String condition) {
        setCondition(condition);
    }

    public String getConditionIf() {
        return this.conditionIf;
    }

    public void setConditionIf(String condition) {
        this.conditionIf = condition;
    }

    public String getLocale() {
        return this.localeName;
    }

    public void setLocale(String localeName) {
        this.localeName = localeName;
        this.locale = findLocale(localeName, this.locale);
    }

    @Override // org.apache.catalina.Valve
    public void invoke(Request request, Response response) throws IOException, ServletException {
        if (this.tlsAttributeRequired) {
            request.getAttribute("jakarta.servlet.request.X509Certificate");
        }
        if (this.cachedElements != null) {
            for (CachedElement element : this.cachedElements) {
                element.cache(request);
            }
        }
        getNext().invoke(request, response);
    }

    @Override // org.apache.catalina.AccessLog
    public void log(Request request, Response response, long time) {
        if (getState().isAvailable() && getEnabled() && this.logElements != null) {
            if (this.condition == null || null == request.getRequest().getAttribute(this.condition)) {
                if (this.conditionIf != null && null == request.getRequest().getAttribute(this.conditionIf)) {
                    return;
                }
                Date date = getDate(request.getCoyoteRequest().getStartTime());
                CharArrayWriter result = this.charArrayWriters.pop();
                if (result == null) {
                    result = new CharArrayWriter(128);
                }
                for (AccessLogElement logElement : this.logElements) {
                    logElement.addElement(result, date, request, response, time);
                }
                log(result);
                if (result.size() <= this.maxLogMessageBufferSize) {
                    result.reset();
                    this.charArrayWriters.push(result);
                }
            }
        }
    }

    private static Date getDate(long systime) {
        Date date = localDate.get();
        date.setTime(systime);
        return date;
    }

    protected static Locale findLocale(String name, Locale fallback) {
        if (name == null || name.isEmpty()) {
            return Locale.getDefault();
        }
        for (Locale l : Locale.getAvailableLocales()) {
            if (name.equals(l.toString())) {
                return l;
            }
        }
        log.error(sm.getString("accessLogValve.invalidLocale", name));
        return fallback;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/AbstractAccessLogValve$ThreadNameElement.class */
    public static class ThreadNameElement implements AccessLogElement {
        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            RequestInfo info = request.getCoyoteRequest().getRequestProcessor();
            if (info != null) {
                buf.append((CharSequence) info.getWorkerThreadName());
            } else {
                buf.append('-');
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/AbstractAccessLogValve$LocalAddrElement.class */
    public static class LocalAddrElement implements AccessLogElement {
        private final String localAddrValue;

        public LocalAddrElement(boolean ipv6Canonical) {
            String init;
            try {
                init = InetAddress.getLocalHost().getHostAddress();
            } catch (Throwable e) {
                ExceptionUtils.handleThrowable(e);
                init = "127.0.0.1";
            }
            if (ipv6Canonical) {
                this.localAddrValue = IPv6Utils.canonize(init);
            } else {
                this.localAddrValue = init;
            }
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            buf.append((CharSequence) this.localAddrValue);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/AbstractAccessLogValve$RemoteAddrElement.class */
    public class RemoteAddrElement implements AccessLogElement, CachedElement {
        private static final String remoteAddress = "remote";
        private static final String peerAddress = "peer";
        private final RemoteAddressType remoteAddressType;

        public RemoteAddrElement() {
            this.remoteAddressType = RemoteAddressType.REMOTE;
        }

        public RemoteAddrElement(String type) {
            boolean z = -1;
            switch (type.hashCode()) {
                case -934610874:
                    if (type.equals("remote")) {
                        z = false;
                        break;
                    }
                    break;
                case 3436898:
                    if (type.equals(peerAddress)) {
                        z = true;
                        break;
                    }
                    break;
            }
            switch (z) {
                case false:
                    this.remoteAddressType = RemoteAddressType.REMOTE;
                    return;
                case true:
                    this.remoteAddressType = RemoteAddressType.PEER;
                    return;
                default:
                    AbstractAccessLogValve.log.error(ValveBase.sm.getString("accessLogValve.invalidRemoteAddressType", type));
                    this.remoteAddressType = RemoteAddressType.REMOTE;
                    return;
            }
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            String value;
            if (this.remoteAddressType == RemoteAddressType.PEER) {
                value = request.getPeerAddr();
            } else if (AbstractAccessLogValve.this.requestAttributesEnabled) {
                Object addr = request.getAttribute(AccessLog.REMOTE_ADDR_ATTRIBUTE);
                if (addr == null) {
                    value = request.getRemoteAddr();
                } else {
                    value = addr.toString();
                }
            } else {
                value = request.getRemoteAddr();
            }
            if (AbstractAccessLogValve.this.ipv6Canonical) {
                value = IPv6Utils.canonize(value);
            }
            buf.append((CharSequence) value);
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.CachedElement
        public void cache(Request request) {
            if (!AbstractAccessLogValve.this.requestAttributesEnabled) {
                if (this.remoteAddressType == RemoteAddressType.PEER) {
                    request.getPeerAddr();
                } else {
                    request.getRemoteAddr();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/AbstractAccessLogValve$HostElement.class */
    public class HostElement implements AccessLogElement, CachedElement {
        /* JADX INFO: Access modifiers changed from: protected */
        public HostElement() {
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            Object host;
            String value = null;
            if (AbstractAccessLogValve.this.requestAttributesEnabled && (host = request.getAttribute(AccessLog.REMOTE_HOST_ATTRIBUTE)) != null) {
                value = host.toString();
            }
            if (value == null || value.length() == 0) {
                value = request.getRemoteHost();
            }
            if (value == null || value.length() == 0) {
                value = "-";
            }
            if (AbstractAccessLogValve.this.ipv6Canonical) {
                value = IPv6Utils.canonize(value);
            }
            buf.append((CharSequence) value);
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.CachedElement
        public void cache(Request request) {
            if (!AbstractAccessLogValve.this.requestAttributesEnabled) {
                request.getRemoteHost();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/AbstractAccessLogValve$LogicalUserNameElement.class */
    public static class LogicalUserNameElement implements AccessLogElement {
        protected LogicalUserNameElement() {
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            buf.append('-');
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/AbstractAccessLogValve$ProtocolElement.class */
    public class ProtocolElement implements AccessLogElement {
        protected ProtocolElement() {
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            if (AbstractAccessLogValve.this.requestAttributesEnabled) {
                Object proto = request.getAttribute(AccessLog.PROTOCOL_ATTRIBUTE);
                if (proto == null) {
                    buf.append((CharSequence) request.getProtocol());
                    return;
                } else {
                    buf.append((CharSequence) proto.toString());
                    return;
                }
            }
            buf.append((CharSequence) request.getProtocol());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/AbstractAccessLogValve$UserElement.class */
    public static class UserElement implements AccessLogElement {
        protected UserElement() {
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            if (request != null) {
                String value = request.getRemoteUser();
                if (value != null) {
                    AbstractAccessLogValve.escapeAndAppend(value, buf);
                    return;
                } else {
                    buf.append('-');
                    return;
                }
            }
            buf.append('-');
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/AbstractAccessLogValve$DateAndTimeElement.class */
    public class DateAndTimeElement implements AccessLogElement {
        private static final String requestStartPrefix = "begin";
        private static final String responseEndPrefix = "end";
        private static final String prefixSeparator = ":";
        private static final String secFormat = "sec";
        private static final String msecFormat = "msec";
        private static final String msecFractionFormat = "msec_frac";
        private static final String msecPattern = "{#}";
        private static final String tripleMsecPattern = "{#}{#}{#}";
        private final String format;
        private final boolean needsEscaping;
        private final boolean usesBegin;
        private final FormatType type;
        private boolean usesMsecs;

        protected DateAndTimeElement(AbstractAccessLogValve this$0) {
            this(null);
        }

        private String tidyFormat(String format) {
            boolean escape = false;
            StringBuilder result = new StringBuilder();
            int len = format.length();
            for (int i = 0; i < len; i++) {
                char x = format.charAt(i);
                if (escape || x != 'S') {
                    result.append(x);
                } else {
                    result.append(msecPattern);
                    this.usesMsecs = true;
                }
                if (x == '\'') {
                    escape = !escape;
                }
            }
            return result.toString();
        }

        protected DateAndTimeElement(String sdf) {
            this.usesMsecs = false;
            String format = sdf;
            boolean needsEscaping = false;
            if (sdf != null) {
                CharArrayWriter writer = new CharArrayWriter();
                AbstractAccessLogValve.escapeAndAppend(sdf, writer);
                String escaped = writer.toString();
                if (!escaped.equals(sdf)) {
                    needsEscaping = true;
                }
            }
            this.needsEscaping = needsEscaping;
            boolean usesBegin = false;
            FormatType type = FormatType.CLF;
            if (format != null) {
                if (format.equals(requestStartPrefix)) {
                    usesBegin = true;
                    format = "";
                } else if (format.startsWith("begin:")) {
                    usesBegin = true;
                    format = format.substring(6);
                } else if (format.equals(responseEndPrefix)) {
                    usesBegin = false;
                    format = "";
                } else if (format.startsWith("end:")) {
                    usesBegin = false;
                    format = format.substring(4);
                }
                if (format.length() == 0) {
                    type = FormatType.CLF;
                } else if (format.equals(secFormat)) {
                    type = FormatType.SEC;
                } else if (format.equals(msecFormat)) {
                    type = FormatType.MSEC;
                } else if (format.equals(msecFractionFormat)) {
                    type = FormatType.MSEC_FRAC;
                } else {
                    type = FormatType.SDF;
                    format = tidyFormat(format);
                }
            }
            this.format = format;
            this.usesBegin = usesBegin;
            this.type = type;
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            long timestamp = date.getTime();
            if (!this.usesBegin) {
                timestamp += TimeUnit.NANOSECONDS.toMillis(time);
            }
            if (this.type == FormatType.CLF) {
                buf.append((CharSequence) AbstractAccessLogValve.localDateCache.get().getFormat(timestamp));
                return;
            }
            if (this.type == FormatType.SEC) {
                buf.append((CharSequence) Long.toString(timestamp / 1000));
                return;
            }
            if (this.type == FormatType.MSEC) {
                buf.append((CharSequence) Long.toString(timestamp));
                return;
            }
            if (this.type == FormatType.MSEC_FRAC) {
                long frac = timestamp % 1000;
                if (frac < 100) {
                    if (frac < 10) {
                        buf.append('0');
                        buf.append('0');
                    } else {
                        buf.append('0');
                    }
                }
                buf.append((CharSequence) Long.toString(frac));
                return;
            }
            String temp = AbstractAccessLogValve.localDateCache.get().getFormat(this.format, AbstractAccessLogValve.this.locale, timestamp);
            if (this.usesMsecs) {
                long frac2 = timestamp % 1000;
                StringBuilder tripleMsec = new StringBuilder(4);
                if (frac2 < 100) {
                    if (frac2 < 10) {
                        tripleMsec.append('0');
                        tripleMsec.append('0');
                    } else {
                        tripleMsec.append('0');
                    }
                }
                tripleMsec.append(frac2);
                temp = temp.replace(tripleMsecPattern, tripleMsec).replace(msecPattern, Long.toString(frac2));
            }
            if (this.needsEscaping) {
                AbstractAccessLogValve.escapeAndAppend(temp, buf);
            } else {
                buf.append((CharSequence) temp);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/AbstractAccessLogValve$RequestElement.class */
    public static class RequestElement implements AccessLogElement {
        protected RequestElement() {
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            if (request != null) {
                String method = request.getMethod();
                if (method == null) {
                    buf.append('-');
                    return;
                }
                buf.append((CharSequence) request.getMethod());
                buf.append(' ');
                buf.append((CharSequence) request.getRequestURI());
                if (request.getQueryString() != null) {
                    buf.append('?');
                    buf.append((CharSequence) request.getQueryString());
                }
                buf.append(' ');
                buf.append((CharSequence) request.getProtocol());
                return;
            }
            buf.append('-');
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/AbstractAccessLogValve$HttpStatusCodeElement.class */
    public static class HttpStatusCodeElement implements AccessLogElement {
        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            if (response != null) {
                int status = response.getStatus();
                if (100 <= status && status < 1000) {
                    buf.append((char) (48 + (status / 100))).append((char) (48 + ((status / 10) % 10))).append((char) (48 + (status % 10)));
                    return;
                } else {
                    buf.append((CharSequence) Integer.toString(status));
                    return;
                }
            }
            buf.append('-');
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/AbstractAccessLogValve$PortElement.class */
    public class PortElement implements AccessLogElement, CachedElement {
        private static final String localPort = "local";
        private static final String remotePort = "remote";
        private final PortType portType;

        public PortElement() {
            this.portType = PortType.LOCAL;
        }

        public PortElement(String type) {
            boolean z = -1;
            switch (type.hashCode()) {
                case -934610874:
                    if (type.equals("remote")) {
                        z = false;
                        break;
                    }
                    break;
                case 103145323:
                    if (type.equals(localPort)) {
                        z = true;
                        break;
                    }
                    break;
            }
            switch (z) {
                case false:
                    this.portType = PortType.REMOTE;
                    return;
                case true:
                    this.portType = PortType.LOCAL;
                    return;
                default:
                    AbstractAccessLogValve.log.error(ValveBase.sm.getString("accessLogValve.invalidPortType", type));
                    this.portType = PortType.LOCAL;
                    return;
            }
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            if (AbstractAccessLogValve.this.requestAttributesEnabled && this.portType == PortType.LOCAL) {
                Object port = request.getAttribute(AccessLog.SERVER_PORT_ATTRIBUTE);
                if (port == null) {
                    buf.append((CharSequence) Integer.toString(request.getServerPort()));
                    return;
                } else {
                    buf.append((CharSequence) port.toString());
                    return;
                }
            }
            if (this.portType == PortType.LOCAL) {
                buf.append((CharSequence) Integer.toString(request.getServerPort()));
            } else {
                buf.append((CharSequence) Integer.toString(request.getRemotePort()));
            }
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.CachedElement
        public void cache(Request request) {
            if (this.portType == PortType.REMOTE) {
                request.getRemotePort();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/AbstractAccessLogValve$ByteSentElement.class */
    public static class ByteSentElement implements AccessLogElement {
        private final boolean conversion;

        public ByteSentElement(boolean conversion) {
            this.conversion = conversion;
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            long length = response.getBytesWritten(false);
            if (length <= 0) {
                Object start = request.getAttribute("org.apache.tomcat.sendfile.start");
                if (start instanceof Long) {
                    Object end = request.getAttribute("org.apache.tomcat.sendfile.end");
                    if (end instanceof Long) {
                        length = ((Long) end).longValue() - ((Long) start).longValue();
                    }
                }
            }
            if (length <= 0 && this.conversion) {
                buf.append('-');
            } else {
                buf.append((CharSequence) Long.toString(length));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/AbstractAccessLogValve$MethodElement.class */
    public static class MethodElement implements AccessLogElement {
        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            if (request != null) {
                buf.append((CharSequence) request.getMethod());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/AbstractAccessLogValve$ElapsedTimeElement.class */
    public static class ElapsedTimeElement implements AccessLogElement {
        private final boolean micros;
        private final boolean millis;

        public ElapsedTimeElement(boolean micros, boolean millis) {
            this.micros = micros;
            this.millis = millis;
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            if (this.micros) {
                buf.append((CharSequence) Long.toString(TimeUnit.NANOSECONDS.toMicros(time)));
            } else if (this.millis) {
                buf.append((CharSequence) Long.toString(TimeUnit.NANOSECONDS.toMillis(time)));
            } else {
                buf.append((CharSequence) Long.toString(TimeUnit.NANOSECONDS.toSeconds(time)));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/AbstractAccessLogValve$FirstByteTimeElement.class */
    public static class FirstByteTimeElement implements AccessLogElement {
        protected FirstByteTimeElement() {
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            long commitTime = response.getCoyoteResponse().getCommitTimeNanos();
            if (commitTime == -1) {
                buf.append('-');
            } else {
                long delta = commitTime - request.getCoyoteRequest().getStartTimeNanos();
                buf.append((CharSequence) Long.toString(TimeUnit.NANOSECONDS.toMillis(delta)));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/AbstractAccessLogValve$QueryElement.class */
    public static class QueryElement implements AccessLogElement {
        protected QueryElement() {
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            String query = null;
            if (request != null) {
                query = request.getQueryString();
            }
            if (query != null) {
                buf.append('?');
                buf.append((CharSequence) query);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/AbstractAccessLogValve$SessionIdElement.class */
    public static class SessionIdElement implements AccessLogElement {
        protected SessionIdElement() {
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            if (request == null) {
                buf.append('-');
                return;
            }
            Session session = request.getSessionInternal(false);
            if (session == null) {
                buf.append('-');
            } else {
                buf.append((CharSequence) session.getIdInternal());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/AbstractAccessLogValve$RequestURIElement.class */
    public static class RequestURIElement implements AccessLogElement {
        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            if (request != null) {
                buf.append((CharSequence) request.getRequestURI());
            } else {
                buf.append('-');
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/AbstractAccessLogValve$LocalServerNameElement.class */
    public class LocalServerNameElement implements AccessLogElement {
        protected LocalServerNameElement() {
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            Object serverName;
            String value = null;
            if (AbstractAccessLogValve.this.requestAttributesEnabled && (serverName = request.getAttribute(AccessLog.SERVER_NAME_ATTRIBUTE)) != null) {
                value = serverName.toString();
            }
            if (value == null || value.length() == 0) {
                value = request.getServerName();
            }
            if (value == null || value.length() == 0) {
                value = "-";
            }
            if (AbstractAccessLogValve.this.ipv6Canonical) {
                value = IPv6Utils.canonize(value);
            }
            buf.append((CharSequence) value);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/AbstractAccessLogValve$StringElement.class */
    public static class StringElement implements AccessLogElement {
        private final String str;

        public StringElement(String str) {
            this.str = str;
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            buf.append((CharSequence) this.str);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/AbstractAccessLogValve$HeaderElement.class */
    public static class HeaderElement implements AccessLogElement {
        private final String header;

        public HeaderElement(String header) {
            this.header = header;
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            Enumeration<String> iter = request.getHeaders(this.header);
            if (iter.hasMoreElements()) {
                AbstractAccessLogValve.escapeAndAppend(iter.nextElement(), buf);
                while (iter.hasMoreElements()) {
                    buf.append(',');
                    AbstractAccessLogValve.escapeAndAppend(iter.nextElement(), buf);
                }
                return;
            }
            buf.append('-');
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/AbstractAccessLogValve$CookieElement.class */
    public static class CookieElement implements AccessLogElement {
        private final String cookieNameToLog;

        public CookieElement(String cookieNameToLog) {
            this.cookieNameToLog = cookieNameToLog;
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            StringBuilder value = new StringBuilder();
            boolean first = true;
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (this.cookieNameToLog.equals(cookie.getName())) {
                        if (first) {
                            first = false;
                        } else {
                            value.append(',');
                        }
                        value.append(cookie.getValue());
                    }
                }
            }
            if (value.length() == 0) {
                buf.append('-');
            } else {
                AbstractAccessLogValve.escapeAndAppend(value.toString(), buf);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/AbstractAccessLogValve$ResponseHeaderElement.class */
    public static class ResponseHeaderElement implements AccessLogElement {
        private final String header;

        public ResponseHeaderElement(String header) {
            this.header = header;
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            if (null != response) {
                Iterator<String> iter = response.getHeaders(this.header).iterator();
                if (iter.hasNext()) {
                    AbstractAccessLogValve.escapeAndAppend(iter.next(), buf);
                    while (iter.hasNext()) {
                        buf.append(',');
                        AbstractAccessLogValve.escapeAndAppend(iter.next(), buf);
                    }
                    return;
                }
            }
            buf.append('-');
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/AbstractAccessLogValve$RequestAttributeElement.class */
    public static class RequestAttributeElement implements AccessLogElement {
        private final String attribute;

        public RequestAttributeElement(String attribute) {
            this.attribute = attribute;
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            Object value;
            if (request != null) {
                value = request.getAttribute(this.attribute);
            } else {
                value = "??";
            }
            if (value != null) {
                if (value instanceof String) {
                    AbstractAccessLogValve.escapeAndAppend((String) value, buf);
                    return;
                } else {
                    AbstractAccessLogValve.escapeAndAppend(value.toString(), buf);
                    return;
                }
            }
            buf.append('-');
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/AbstractAccessLogValve$SessionAttributeElement.class */
    public static class SessionAttributeElement implements AccessLogElement {
        private final String attribute;

        public SessionAttributeElement(String attribute) {
            this.attribute = attribute;
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            Object value = null;
            if (null != request) {
                HttpSession sess = request.getSession(false);
                if (null != sess) {
                    value = sess.getAttribute(this.attribute);
                }
            } else {
                value = "??";
            }
            if (value != null) {
                if (value instanceof String) {
                    AbstractAccessLogValve.escapeAndAppend((String) value, buf);
                    return;
                } else {
                    AbstractAccessLogValve.escapeAndAppend(value.toString(), buf);
                    return;
                }
            }
            buf.append('-');
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/AbstractAccessLogValve$ConnectionStatusElement.class */
    public static class ConnectionStatusElement implements AccessLogElement {
        protected ConnectionStatusElement() {
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            if (response != null && request != null) {
                boolean statusFound = false;
                AtomicBoolean isIoAllowed = new AtomicBoolean(false);
                request.getCoyoteRequest().action(ActionCode.IS_IO_ALLOWED, isIoAllowed);
                if (!isIoAllowed.get()) {
                    buf.append('X');
                    statusFound = true;
                } else if (response.isError()) {
                    Throwable ex = (Throwable) request.getAttribute("jakarta.servlet.error.exception");
                    if (ex instanceof ClientAbortException) {
                        buf.append('X');
                        statusFound = true;
                    }
                }
                if (!statusFound) {
                    String connStatus = response.getHeader("Connection");
                    if (org.apache.coyote.http11.Constants.CLOSE.equalsIgnoreCase(connStatus)) {
                        buf.append('-');
                        return;
                    } else {
                        buf.append('+');
                        return;
                    }
                }
                return;
            }
            buf.append('?');
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AccessLogElement[] createLogElements() {
        List<AccessLogElement> list = new ArrayList<>();
        boolean replace = false;
        StringBuilder buf = new StringBuilder();
        int i = 0;
        while (i < this.pattern.length()) {
            char ch2 = this.pattern.charAt(i);
            if (replace) {
                if ('{' == ch2) {
                    StringBuilder name = new StringBuilder();
                    int j = i + 1;
                    while (j < this.pattern.length() && '}' != this.pattern.charAt(j)) {
                        name.append(this.pattern.charAt(j));
                        j++;
                    }
                    if (j + 1 < this.pattern.length()) {
                        int j2 = j + 1;
                        list.add(createAccessLogElement(name.toString(), this.pattern.charAt(j2)));
                        i = j2;
                    } else {
                        list.add(createAccessLogElement(ch2));
                    }
                } else {
                    list.add(createAccessLogElement(ch2));
                }
                replace = false;
            } else if (ch2 == '%') {
                replace = true;
                list.add(new StringElement(buf.toString()));
                buf = new StringBuilder();
            } else {
                buf.append(ch2);
            }
            i++;
        }
        if (buf.length() > 0) {
            list.add(new StringElement(buf.toString()));
        }
        return (AccessLogElement[]) list.toArray(new AccessLogElement[0]);
    }

    private CachedElement[] createCachedElements(AccessLogElement[] elements) {
        List<CachedElement> list = new ArrayList<>();
        for (AccessLogElement element : elements) {
            if (element instanceof CachedElement) {
                list.add((CachedElement) element);
            }
        }
        return (CachedElement[]) list.toArray(new CachedElement[0]);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AccessLogElement createAccessLogElement(String name, char pattern) {
        switch (pattern) {
            case Opcodes.BASTORE /* 84 */:
                if ("ms".equals(name)) {
                    return new ElapsedTimeElement(false, true);
                }
                if ("us".equals(name)) {
                    return new ElapsedTimeElement(true, false);
                }
                return new ElapsedTimeElement(false, false);
            case Opcodes.CASTORE /* 85 */:
            case Opcodes.SASTORE /* 86 */:
            case Opcodes.POP /* 87 */:
            case 'X':
            case Opcodes.DUP /* 89 */:
            case 'Z':
            case '[':
            case '\\':
            case ']':
            case Opcodes.DUP2_X2 /* 94 */:
            case Opcodes.SWAP /* 95 */:
            case '`':
            case Opcodes.FADD /* 98 */:
            case 'd':
            case 'e':
            case Opcodes.FSUB /* 102 */:
            case Opcodes.DSUB /* 103 */:
            case 'h':
            case Opcodes.FMUL /* 106 */:
            case Opcodes.DMUL /* 107 */:
            case 'l':
            case Opcodes.LDIV /* 109 */:
            case Opcodes.FDIV /* 110 */:
            case Opcodes.LREM /* 113 */:
            default:
                return new StringElement("???");
            case 'a':
                return new RemoteAddrElement(name);
            case 'c':
                return new CookieElement(name);
            case Opcodes.LMUL /* 105 */:
                return new HeaderElement(name);
            case Opcodes.DDIV /* 111 */:
                return new ResponseHeaderElement(name);
            case 'p':
                return new PortElement(name);
            case Opcodes.FREM /* 114 */:
                if (TLSUtil.isTLSRequestAttribute(name)) {
                    this.tlsAttributeRequired = true;
                }
                return new RequestAttributeElement(name);
            case 's':
                return new SessionAttributeElement(name);
            case 't':
                return new DateAndTimeElement(name);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AccessLogElement createAccessLogElement(char pattern) {
        switch (pattern) {
            case 'A':
                return new LocalAddrElement(this.ipv6Canonical);
            case 'B':
                return new ByteSentElement(false);
            case 'C':
            case TypeReference.CONSTRUCTOR_REFERENCE /* 69 */:
            case TypeReference.CAST /* 71 */:
            case 'J':
            case TypeReference.METHOD_REFERENCE_TYPE_ARGUMENT /* 75 */:
            case BaseNCodec.MIME_CHUNK_SIZE /* 76 */:
            case 'M':
            case 'N':
            case Opcodes.IASTORE /* 79 */:
            case 'P':
            case Opcodes.FASTORE /* 81 */:
            case Opcodes.DASTORE /* 82 */:
            case Opcodes.SASTORE /* 86 */:
            case Opcodes.POP /* 87 */:
            case Opcodes.DUP /* 89 */:
            case 'Z':
            case '[':
            case '\\':
            case ']':
            case Opcodes.DUP2_X2 /* 94 */:
            case Opcodes.SWAP /* 95 */:
            case '`':
            case 'c':
            case 'd':
            case 'e':
            case Opcodes.FSUB /* 102 */:
            case Opcodes.DSUB /* 103 */:
            case Opcodes.LMUL /* 105 */:
            case Opcodes.FMUL /* 106 */:
            case Opcodes.DMUL /* 107 */:
            case Opcodes.FDIV /* 110 */:
            case Opcodes.DDIV /* 111 */:
            default:
                return new StringElement("???" + pattern + "???");
            case 'D':
                return new ElapsedTimeElement(true, false);
            case 'F':
                return new FirstByteTimeElement();
            case 'H':
                return new ProtocolElement();
            case 'I':
                return new ThreadNameElement();
            case 'S':
                return new SessionIdElement();
            case Opcodes.BASTORE /* 84 */:
                return new ElapsedTimeElement(false, false);
            case Opcodes.CASTORE /* 85 */:
                return new RequestURIElement();
            case 'X':
                return new ConnectionStatusElement();
            case 'a':
                return new RemoteAddrElement();
            case Opcodes.FADD /* 98 */:
                return new ByteSentElement(true);
            case 'h':
                return new HostElement();
            case 'l':
                return new LogicalUserNameElement();
            case Opcodes.LDIV /* 109 */:
                return new MethodElement();
            case 'p':
                return new PortElement();
            case Opcodes.LREM /* 113 */:
                return new QueryElement();
            case Opcodes.FREM /* 114 */:
                return new RequestElement();
            case 's':
                return new HttpStatusCodeElement();
            case 't':
                return new DateAndTimeElement(this);
            case Opcodes.LNEG /* 117 */:
                return new UserElement();
            case Opcodes.FNEG /* 118 */:
                return new LocalServerNameElement();
        }
    }

    protected static void escapeAndAppend(String input, CharArrayWriter dest) {
        if (input == null || input.isEmpty()) {
            dest.append('-');
            return;
        }
        int len = input.length();
        int next = 0;
        for (int current = 0; current < len; current++) {
            char c = input.charAt(current);
            if (c >= ' ' && c < 127) {
                switch (c) {
                    case '\"':
                        if (current > next) {
                            dest.write(input, next, current - next);
                        }
                        next = current + 1;
                        dest.append("\\\"");
                        break;
                    case '\\':
                        if (current > next) {
                            dest.write(input, next, current - next);
                        }
                        next = current + 1;
                        dest.append("\\\\");
                        break;
                }
            } else {
                if (current > next) {
                    dest.write(input, next, current - next);
                }
                next = current + 1;
                switch (c) {
                    case '\t':
                        dest.append("\\t");
                        break;
                    case '\n':
                        dest.append("\\n");
                        break;
                    case 11:
                    default:
                        dest.append("\\u");
                        dest.append((CharSequence) HexUtils.toHexString(c));
                        break;
                    case '\f':
                        dest.append("\\f");
                        break;
                    case '\r':
                        dest.append("\\r");
                        break;
                }
            }
        }
        if (len > next) {
            dest.write(input, next, len - next);
        }
    }
}
