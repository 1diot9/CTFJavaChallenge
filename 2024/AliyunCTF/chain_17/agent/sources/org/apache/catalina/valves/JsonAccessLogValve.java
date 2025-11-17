package org.apache.catalina.valves;

import ch.qos.logback.classic.ClassicConstants;
import ch.qos.logback.classic.encoder.JsonEncoder;
import java.io.CharArrayWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.filters.RateLimitFilter;
import org.apache.catalina.valves.AbstractAccessLogValve;
import org.apache.tomcat.util.json.JSONFilter;
import org.springframework.web.servlet.tags.form.InputTag;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/JsonAccessLogValve.class */
public class JsonAccessLogValve extends AccessLogValve {
    private static final Map<Character, String> PATTERNS;
    private static final Map<Character, String> SUB_OBJECT_PATTERNS;

    static {
        Map<Character, String> pattern2AttributeName = new HashMap<>();
        pattern2AttributeName.put('a', "remoteAddr");
        pattern2AttributeName.put('A', "localAddr");
        pattern2AttributeName.put('b', InputTag.SIZE_ATTRIBUTE);
        pattern2AttributeName.put('B', "byteSentNC");
        pattern2AttributeName.put('D', "elapsedTime");
        pattern2AttributeName.put('F', "firstByteTime");
        pattern2AttributeName.put('h', "host");
        pattern2AttributeName.put('H', "protocol");
        pattern2AttributeName.put('I', JsonEncoder.THREAD_NAME_ATTR_NAME);
        pattern2AttributeName.put('l', "logicalUserName");
        pattern2AttributeName.put('m', "method");
        pattern2AttributeName.put('p', "port");
        pattern2AttributeName.put('q', "query");
        pattern2AttributeName.put('r', "request");
        pattern2AttributeName.put('s', RateLimitFilter.PARAM_STATUS_CODE);
        pattern2AttributeName.put('S', "sessionId");
        pattern2AttributeName.put('t', "time");
        pattern2AttributeName.put('T', "elapsedTimeS");
        pattern2AttributeName.put('u', ClassicConstants.USER_MDC_KEY);
        pattern2AttributeName.put('U', "path");
        pattern2AttributeName.put('v', "localServerName");
        pattern2AttributeName.put('X', "connectionStatus");
        PATTERNS = Collections.unmodifiableMap(pattern2AttributeName);
        Map<Character, String> pattern2AttributeName2 = new HashMap<>();
        pattern2AttributeName2.put('c', "cookies");
        pattern2AttributeName2.put('i', "requestHeaders");
        pattern2AttributeName2.put('o', "responseHeaders");
        pattern2AttributeName2.put('r', "requestAttributes");
        pattern2AttributeName2.put('s', "sessionAttributes");
        SUB_OBJECT_PATTERNS = Collections.unmodifiableMap(pattern2AttributeName2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/JsonAccessLogValve$CharElement.class */
    public static class CharElement implements AbstractAccessLogValve.AccessLogElement {

        /* renamed from: ch, reason: collision with root package name */
        private final char f4ch;

        public CharElement(char ch2) {
            this.f4ch = ch2;
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            buf.write(this.f4ch);
        }
    }

    private boolean addSubkeyedItems(ListIterator<AbstractAccessLogValve.AccessLogElement> iterator, List<JsonWrappedElement> elements, String patternAttribute) {
        if (!elements.isEmpty()) {
            iterator.add(new AbstractAccessLogValve.StringElement("\"" + patternAttribute + "\": {"));
            for (JsonWrappedElement element : elements) {
                iterator.add(element);
                iterator.add(new CharElement(','));
            }
            iterator.previous();
            iterator.remove();
            iterator.add(new AbstractAccessLogValve.StringElement("},"));
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.valves.AbstractAccessLogValve
    public AbstractAccessLogValve.AccessLogElement[] createLogElements() {
        Map<Character, List<JsonWrappedElement>> subTypeLists = new HashMap<>();
        Iterator<Character> it = SUB_OBJECT_PATTERNS.keySet().iterator();
        while (it.hasNext()) {
            subTypeLists.put(it.next(), new ArrayList<>());
        }
        boolean hasSub = false;
        List<AbstractAccessLogValve.AccessLogElement> logElements = new ArrayList<>(Arrays.asList(super.createLogElements()));
        ListIterator<AbstractAccessLogValve.AccessLogElement> lit = logElements.listIterator();
        lit.add(new CharElement('{'));
        while (lit.hasNext()) {
            AbstractAccessLogValve.AccessLogElement logElement = lit.next();
            if (!(logElement instanceof JsonWrappedElement)) {
                lit.remove();
            } else {
                JsonWrappedElement wrappedLogElement = (JsonWrappedElement) logElement;
                AbstractAccessLogValve.AccessLogElement ale = wrappedLogElement.getDelegate();
                if (ale instanceof AbstractAccessLogValve.HeaderElement) {
                    subTypeLists.get('i').add(wrappedLogElement);
                    lit.remove();
                } else if (ale instanceof AbstractAccessLogValve.ResponseHeaderElement) {
                    subTypeLists.get('o').add(wrappedLogElement);
                    lit.remove();
                } else if (ale instanceof AbstractAccessLogValve.RequestAttributeElement) {
                    subTypeLists.get('r').add(wrappedLogElement);
                    lit.remove();
                } else if (ale instanceof AbstractAccessLogValve.SessionAttributeElement) {
                    subTypeLists.get('s').add(wrappedLogElement);
                    lit.remove();
                } else if (ale instanceof AbstractAccessLogValve.CookieElement) {
                    subTypeLists.get('c').add(wrappedLogElement);
                    lit.remove();
                } else {
                    lit.add(new CharElement(','));
                }
            }
        }
        for (Character pattern : SUB_OBJECT_PATTERNS.keySet()) {
            if (addSubkeyedItems(lit, subTypeLists.get(pattern), SUB_OBJECT_PATTERNS.get(pattern))) {
                hasSub = true;
            }
        }
        lit.previous();
        lit.remove();
        if (hasSub) {
            lit.add(new AbstractAccessLogValve.StringElement("}}"));
        } else {
            lit.add(new CharElement('}'));
        }
        return (AbstractAccessLogValve.AccessLogElement[]) logElements.toArray(new AbstractAccessLogValve.AccessLogElement[0]);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.valves.AbstractAccessLogValve
    public AbstractAccessLogValve.AccessLogElement createAccessLogElement(String name, char pattern) {
        AbstractAccessLogValve.AccessLogElement ale = super.createAccessLogElement(name, pattern);
        return new JsonWrappedElement(pattern, name, true, ale);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.valves.AbstractAccessLogValve
    public AbstractAccessLogValve.AccessLogElement createAccessLogElement(char pattern) {
        AbstractAccessLogValve.AccessLogElement ale = super.createAccessLogElement(pattern);
        return new JsonWrappedElement(pattern, true, ale);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/JsonAccessLogValve$JsonWrappedElement.class */
    public static class JsonWrappedElement implements AbstractAccessLogValve.AccessLogElement, AbstractAccessLogValve.CachedElement {
        private CharSequence attributeName;
        private boolean quoteValue;
        private AbstractAccessLogValve.AccessLogElement delegate;

        private CharSequence escapeJsonString(CharSequence nonEscaped) {
            return JSONFilter.escape(nonEscaped);
        }

        JsonWrappedElement(char pattern, String key, boolean quoteValue, AbstractAccessLogValve.AccessLogElement delegate) {
            this.quoteValue = quoteValue;
            this.delegate = delegate;
            String patternAttribute = JsonAccessLogValve.PATTERNS.get(Character.valueOf(pattern));
            patternAttribute = patternAttribute == null ? "other-" + Character.toString(pattern) : patternAttribute;
            if (key != null && !"".equals(key)) {
                if (JsonAccessLogValve.SUB_OBJECT_PATTERNS.containsKey(Character.valueOf(pattern))) {
                    this.attributeName = escapeJsonString(key);
                    return;
                } else {
                    this.attributeName = escapeJsonString(patternAttribute + "-" + key);
                    return;
                }
            }
            this.attributeName = escapeJsonString(patternAttribute);
        }

        JsonWrappedElement(char pattern, boolean quoteValue, AbstractAccessLogValve.AccessLogElement delegate) {
            this(pattern, null, quoteValue, delegate);
        }

        public AbstractAccessLogValve.AccessLogElement getDelegate() {
            return this.delegate;
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            buf.append('\"').append(this.attributeName).append('\"').append(':');
            if (this.quoteValue) {
                buf.append('\"');
            }
            this.delegate.addElement(buf, date, request, response, time);
            if (this.quoteValue) {
                buf.append('\"');
            }
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.CachedElement
        public void cache(Request request) {
            if (this.delegate instanceof AbstractAccessLogValve.CachedElement) {
                ((AbstractAccessLogValve.CachedElement) this.delegate).cache(request);
            }
        }
    }
}
