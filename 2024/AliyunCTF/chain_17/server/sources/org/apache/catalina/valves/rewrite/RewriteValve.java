package org.apache.catalina.valves.rewrite;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Pipeline;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.util.URLEncoder;
import org.apache.catalina.valves.ValveBase;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.CharChunk;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.buf.UDecoder;
import org.apache.tomcat.util.buf.UriUtil;
import org.apache.tomcat.util.file.ConfigFileLoader;
import org.apache.tomcat.util.file.ConfigurationSource;
import org.apache.tomcat.util.http.RequestUtil;
import org.springframework.beans.PropertyAccessor;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/rewrite/RewriteValve.class */
public class RewriteValve extends ValveBase {
    protected RewriteRule[] rules;
    protected ThreadLocal<Boolean> invoked;
    protected String resourcePath;
    protected boolean context;
    protected boolean enabled;
    protected Map<String, RewriteMap> maps;
    protected ArrayList<String> mapsConfiguration;

    public RewriteValve() {
        super(true);
        this.rules = null;
        this.invoked = new ThreadLocal<>();
        this.resourcePath = "rewrite.config";
        this.context = false;
        this.enabled = true;
        this.maps = new ConcurrentHashMap();
        this.mapsConfiguration = new ArrayList<>();
    }

    public boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.valves.ValveBase, org.apache.catalina.util.LifecycleMBeanBase, org.apache.catalina.util.LifecycleBase
    public void initInternal() throws LifecycleException {
        super.initInternal();
        this.containerLog = LogFactory.getLog(getContainer().getLogName() + ".rewrite");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.valves.ValveBase, org.apache.catalina.util.LifecycleBase
    public synchronized void startInternal() throws LifecycleException {
        super.startInternal();
        InputStream is = null;
        if (getContainer() instanceof Context) {
            this.context = true;
            is = ((Context) getContainer()).getServletContext().getResourceAsStream("/WEB-INF/" + this.resourcePath);
            if (this.containerLog.isDebugEnabled()) {
                if (is == null) {
                    this.containerLog.debug("No configuration resource found: /WEB-INF/" + this.resourcePath);
                } else {
                    this.containerLog.debug("Read configuration from: /WEB-INF/" + this.resourcePath);
                }
            }
        } else {
            String resourceName = Container.getConfigPath(getContainer(), this.resourcePath);
            try {
                ConfigurationSource.Resource resource = ConfigFileLoader.getSource().getResource(resourceName);
                is = resource.getInputStream();
            } catch (IOException e) {
                if (this.containerLog.isDebugEnabled()) {
                    this.containerLog.debug("No configuration resource found: " + resourceName, e);
                }
            }
        }
        try {
            if (is == null) {
                return;
            }
            try {
                InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
                try {
                    BufferedReader reader = new BufferedReader(isr);
                    try {
                        parse(reader);
                        reader.close();
                        isr.close();
                    } catch (Throwable th) {
                        try {
                            reader.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    try {
                        isr.close();
                    } catch (Throwable th4) {
                        th3.addSuppressed(th4);
                    }
                    throw th3;
                }
            } catch (IOException ioe) {
                this.containerLog.error(sm.getString("rewriteValve.closeError"), ioe);
                try {
                    is.close();
                } catch (IOException e2) {
                    this.containerLog.error(sm.getString("rewriteValve.closeError"), e2);
                }
            }
        } finally {
            try {
                is.close();
            } catch (IOException e3) {
                this.containerLog.error(sm.getString("rewriteValve.closeError"), e3);
            }
        }
    }

    public void setConfiguration(String configuration) throws Exception {
        if (this.containerLog == null) {
            this.containerLog = LogFactory.getLog(getContainer().getLogName() + ".rewrite");
        }
        this.maps.clear();
        parse(new BufferedReader(new StringReader(configuration)));
    }

    public String getConfiguration() {
        StringBuilder buffer = new StringBuilder();
        Iterator<String> it = this.mapsConfiguration.iterator();
        while (it.hasNext()) {
            String mapConfiguration = it.next();
            buffer.append(mapConfiguration).append("\r\n");
        }
        if (this.mapsConfiguration.size() > 0) {
            buffer.append("\r\n");
        }
        for (RewriteRule rule : this.rules) {
            for (int j = 0; j < rule.getConditions().length; j++) {
                buffer.append(rule.getConditions()[j].toString()).append("\r\n");
            }
            buffer.append(rule.toString()).append("\r\n").append("\r\n");
        }
        return buffer.toString();
    }

    protected void parse(BufferedReader reader) throws LifecycleException {
        String line;
        List<RewriteRule> rules = new ArrayList<>();
        List<RewriteCond> conditions = new ArrayList<>();
        while (true) {
            try {
                line = reader.readLine();
            } catch (IOException e) {
                this.containerLog.error(sm.getString("rewriteValve.readError"), e);
            }
            if (line == null) {
                break;
            }
            Object result = parse(line);
            if (result instanceof RewriteRule) {
                RewriteRule rule = (RewriteRule) result;
                if (this.containerLog.isDebugEnabled()) {
                    this.containerLog.debug("Add rule with pattern " + rule.getPatternString() + " and substitution " + rule.getSubstitutionString());
                }
                for (int i = conditions.size() - 1; i > 0; i--) {
                    if (conditions.get(i - 1).isOrnext()) {
                        conditions.get(i).setOrnext(true);
                    }
                }
                for (RewriteCond condition : conditions) {
                    if (this.containerLog.isDebugEnabled()) {
                        this.containerLog.debug("Add condition " + condition.getCondPattern() + " test " + condition.getTestString() + " to rule with pattern " + rule.getPatternString() + " and substitution " + rule.getSubstitutionString() + (condition.isOrnext() ? " [OR]" : "") + (condition.isNocase() ? " [NC]" : ""));
                    }
                    rule.addCondition(condition);
                }
                conditions.clear();
                rules.add(rule);
            } else if (result instanceof RewriteCond) {
                conditions.add((RewriteCond) result);
            } else if (result instanceof Object[]) {
                String mapName = (String) ((Object[]) result)[0];
                RewriteMap map = (RewriteMap) ((Object[]) result)[1];
                this.maps.put(mapName, map);
                this.mapsConfiguration.add(line);
                if (map instanceof Lifecycle) {
                    ((Lifecycle) map).start();
                }
            }
        }
        this.rules = (RewriteRule[]) rules.toArray(new RewriteRule[0]);
        for (RewriteRule rewriteRule : this.rules) {
            rewriteRule.parse(this.maps);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.valves.ValveBase, org.apache.catalina.util.LifecycleBase
    public synchronized void stopInternal() throws LifecycleException {
        super.stopInternal();
        for (RewriteMap map : this.maps.values()) {
            if (map instanceof Lifecycle) {
                ((Lifecycle) map).stop();
            }
        }
        this.maps.clear();
        this.rules = null;
    }

    @Override // org.apache.catalina.Valve
    public void invoke(Request request, Response response) throws IOException, ServletException {
        String rewrittenQueryStringDecoded;
        if (!getEnabled() || this.rules == null || this.rules.length == 0) {
            getNext().invoke(request, response);
            return;
        }
        if (Boolean.TRUE.equals(this.invoked.get())) {
            try {
                getNext().invoke(request, response);
                return;
            } finally {
            }
        }
        try {
            Resolver resolver = new ResolverImpl(request);
            this.invoked.set(Boolean.TRUE);
            Charset uriCharset = request.getConnector().getURICharset();
            String originalQueryStringEncoded = request.getQueryString();
            MessageBytes urlMB = this.context ? request.getRequestPathMB() : request.getDecodedRequestURIMB();
            urlMB.toChars();
            CharSequence urlDecoded = urlMB.getCharChunk();
            CharSequence host = request.getServerName();
            boolean rewritten = false;
            boolean done = false;
            boolean qsa = false;
            boolean qsd = false;
            int i = 0;
            while (true) {
                if (i >= this.rules.length) {
                    break;
                }
                RewriteRule rule = this.rules[i];
                CharSequence test = rule.isHost() ? host : urlDecoded;
                CharSequence newtest = rule.evaluate(test, resolver);
                if (newtest != null && !test.equals(newtest.toString())) {
                    if (this.containerLog.isDebugEnabled()) {
                        this.containerLog.debug("Rewrote " + test + " as " + newtest + " with rule pattern " + rule.getPatternString());
                    }
                    if (rule.isHost()) {
                        host = newtest;
                    } else {
                        urlDecoded = newtest;
                    }
                    rewritten = true;
                }
                if (!qsa && newtest != null && rule.isQsappend()) {
                    qsa = true;
                }
                if (!qsd && newtest != null && rule.isQsdiscard()) {
                    qsd = true;
                }
                if (rule.isForbidden() && newtest != null) {
                    response.sendError(403);
                    done = true;
                    break;
                }
                if (rule.isGone() && newtest != null) {
                    response.sendError(HttpServletResponse.SC_GONE);
                    done = true;
                    break;
                }
                if (rule.isRedirect() && newtest != null) {
                    String urlStringDecoded = urlDecoded.toString();
                    int index = urlStringDecoded.indexOf(63);
                    if (index == -1) {
                        rewrittenQueryStringDecoded = null;
                    } else {
                        rewrittenQueryStringDecoded = urlStringDecoded.substring(index + 1);
                        urlStringDecoded = urlStringDecoded.substring(0, index);
                    }
                    StringBuilder urlStringEncoded = new StringBuilder(URLEncoder.DEFAULT.encode(urlStringDecoded, uriCharset));
                    if (!qsd && originalQueryStringEncoded != null && originalQueryStringEncoded.length() > 0) {
                        if (rewrittenQueryStringDecoded == null) {
                            urlStringEncoded.append('?');
                            urlStringEncoded.append(originalQueryStringEncoded);
                        } else if (qsa) {
                            urlStringEncoded.append('?');
                            urlStringEncoded.append(URLEncoder.QUERY.encode(rewrittenQueryStringDecoded, uriCharset));
                            urlStringEncoded.append('&');
                            urlStringEncoded.append(originalQueryStringEncoded);
                        } else if (index == urlStringEncoded.length() - 1) {
                            urlStringEncoded.deleteCharAt(index);
                        } else {
                            urlStringEncoded.append('?');
                            urlStringEncoded.append(URLEncoder.QUERY.encode(rewrittenQueryStringDecoded, uriCharset));
                        }
                    } else if (rewrittenQueryStringDecoded != null) {
                        urlStringEncoded.append('?');
                        urlStringEncoded.append(URLEncoder.QUERY.encode(rewrittenQueryStringDecoded, uriCharset));
                    }
                    if (this.context && urlStringEncoded.charAt(0) == '/' && !UriUtil.hasScheme(urlStringEncoded)) {
                        urlStringEncoded.insert(0, request.getContext().getEncodedPath());
                    }
                    if (rule.isNoescape()) {
                        response.sendRedirect(UDecoder.URLDecode(urlStringEncoded.toString(), uriCharset));
                    } else {
                        response.sendRedirect(urlStringEncoded.toString());
                    }
                    response.setStatus(rule.getRedirectCode());
                    done = true;
                } else {
                    if (rule.isCookie() && newtest != null) {
                        Cookie cookie = new Cookie(rule.getCookieName(), rule.getCookieResult());
                        cookie.setDomain(rule.getCookieDomain());
                        cookie.setMaxAge(rule.getCookieLifetime());
                        cookie.setPath(rule.getCookiePath());
                        cookie.setSecure(rule.isCookieSecure());
                        cookie.setHttpOnly(rule.isCookieHttpOnly());
                        response.addCookie(cookie);
                    }
                    if (rule.isEnv() && newtest != null) {
                        for (int j = 0; j < rule.getEnvSize(); j++) {
                            request.setAttribute(rule.getEnvName(j), rule.getEnvResult(j));
                        }
                    }
                    if (rule.isType() && newtest != null) {
                        response.setContentType(rule.getTypeValue());
                    }
                    if (rule.isChain() && newtest == null) {
                        int j2 = i;
                        while (true) {
                            if (j2 >= this.rules.length) {
                                break;
                            }
                            if (this.rules[j2].isChain()) {
                                j2++;
                            } else {
                                i = j2;
                                break;
                            }
                        }
                    } else {
                        if (rule.isLast() && newtest != null) {
                            break;
                        }
                        if (rule.isNext() && newtest != null) {
                            i = 0;
                        } else if (newtest != null) {
                            i += rule.getSkip();
                        }
                    }
                    i++;
                }
            }
            if (rewritten) {
                if (!done) {
                    String urlStringDecoded2 = urlDecoded.toString();
                    String queryStringDecoded = null;
                    int queryIndex = urlStringDecoded2.indexOf(63);
                    if (queryIndex != -1) {
                        queryStringDecoded = urlStringDecoded2.substring(queryIndex + 1);
                        urlStringDecoded2 = urlStringDecoded2.substring(0, queryIndex);
                    }
                    String contextPath = null;
                    if (this.context) {
                        contextPath = request.getContextPath();
                    }
                    request.getCoyoteRequest().requestURI().setChars(MessageBytes.EMPTY_CHAR_ARRAY, 0, 0);
                    CharChunk chunk = request.getCoyoteRequest().requestURI().getCharChunk();
                    if (this.context) {
                        chunk.append(contextPath);
                    }
                    chunk.append(URLEncoder.DEFAULT.encode(urlStringDecoded2, uriCharset));
                    String urlStringDecoded3 = RequestUtil.normalize(urlStringDecoded2);
                    request.getCoyoteRequest().decodedURI().setChars(MessageBytes.EMPTY_CHAR_ARRAY, 0, 0);
                    CharChunk chunk2 = request.getCoyoteRequest().decodedURI().getCharChunk();
                    if (this.context) {
                        chunk2.append(request.getServletContext().getContextPath());
                    }
                    chunk2.append(urlStringDecoded3);
                    if (queryStringDecoded != null) {
                        request.getCoyoteRequest().queryString().setChars(MessageBytes.EMPTY_CHAR_ARRAY, 0, 0);
                        CharChunk chunk3 = request.getCoyoteRequest().queryString().getCharChunk();
                        chunk3.append(URLEncoder.QUERY.encode(queryStringDecoded, uriCharset));
                        if (qsa && originalQueryStringEncoded != null && originalQueryStringEncoded.length() > 0) {
                            chunk3.append('&');
                            chunk3.append(originalQueryStringEncoded);
                        }
                    }
                    if (!host.equals(request.getServerName())) {
                        request.getCoyoteRequest().serverName().setChars(MessageBytes.EMPTY_CHAR_ARRAY, 0, 0);
                        request.getCoyoteRequest().serverName().getCharChunk().append(host.toString());
                    }
                    request.getMappingData().recycle();
                    Connector connector = request.getConnector();
                    if (!connector.getProtocolHandler().getAdapter().prepare(request.getCoyoteRequest(), response.getCoyoteResponse())) {
                        return;
                    }
                    Pipeline pipeline = connector.getService().getContainer().getPipeline();
                    request.setAsyncSupported(pipeline.isAsyncSupported());
                    pipeline.getFirst().invoke(request, response);
                }
            } else {
                getNext().invoke(request, response);
            }
        } finally {
        }
    }

    public static Object parse(String line) {
        QuotedStringTokenizer tokenizer = new QuotedStringTokenizer(line);
        if (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (token.equals("RewriteCond")) {
                RewriteCond condition = new RewriteCond();
                if (tokenizer.countTokens() < 2) {
                    throw new IllegalArgumentException(sm.getString("rewriteValve.invalidLine", line));
                }
                condition.setTestString(tokenizer.nextToken());
                condition.setCondPattern(tokenizer.nextToken());
                if (tokenizer.hasMoreTokens()) {
                    String flags = tokenizer.nextToken();
                    condition.setFlagsString(flags);
                    if (flags.startsWith(PropertyAccessor.PROPERTY_KEY_PREFIX) && flags.endsWith("]")) {
                        flags = flags.substring(1, flags.length() - 1);
                    }
                    StringTokenizer flagsTokenizer = new StringTokenizer(flags, ",");
                    while (flagsTokenizer.hasMoreElements()) {
                        parseCondFlag(line, condition, flagsTokenizer.nextToken());
                    }
                }
                return condition;
            }
            if (token.equals("RewriteRule")) {
                RewriteRule rule = new RewriteRule();
                if (tokenizer.countTokens() < 2) {
                    throw new IllegalArgumentException(sm.getString("rewriteValve.invalidLine", line));
                }
                rule.setPatternString(tokenizer.nextToken());
                rule.setSubstitutionString(tokenizer.nextToken());
                if (tokenizer.hasMoreTokens()) {
                    String flags2 = tokenizer.nextToken();
                    rule.setFlagsString(flags2);
                    if (flags2.startsWith(PropertyAccessor.PROPERTY_KEY_PREFIX) && flags2.endsWith("]")) {
                        flags2 = flags2.substring(1, flags2.length() - 1);
                    }
                    StringTokenizer flagsTokenizer2 = new StringTokenizer(flags2, ",");
                    while (flagsTokenizer2.hasMoreElements()) {
                        parseRuleFlag(line, rule, flagsTokenizer2.nextToken());
                    }
                }
                return rule;
            }
            if (token.equals("RewriteMap")) {
                if (tokenizer.countTokens() < 2) {
                    throw new IllegalArgumentException(sm.getString("rewriteValve.invalidLine", line));
                }
                String name = tokenizer.nextToken();
                String rewriteMapClassName = tokenizer.nextToken();
                RewriteMap map = null;
                if (rewriteMapClassName.startsWith("int:")) {
                    map = InternalRewriteMap.toMap(rewriteMapClassName.substring("int:".length()));
                } else if (rewriteMapClassName.startsWith("txt:")) {
                    map = new RandomizedTextRewriteMap(rewriteMapClassName.substring("txt:".length()), false);
                } else if (rewriteMapClassName.startsWith("rnd:")) {
                    map = new RandomizedTextRewriteMap(rewriteMapClassName.substring("rnd:".length()), true);
                } else if (rewriteMapClassName.startsWith("prg:")) {
                    rewriteMapClassName = rewriteMapClassName.substring("prg:".length());
                } else if (!rewriteMapClassName.startsWith("dbm:") && !rewriteMapClassName.startsWith("dbd:") && rewriteMapClassName.startsWith("fastdbd:")) {
                }
                if (map == null) {
                    try {
                        map = (RewriteMap) Class.forName(rewriteMapClassName).getConstructor(new Class[0]).newInstance(new Object[0]);
                    } catch (Exception e) {
                        throw new IllegalArgumentException(sm.getString("rewriteValve.invalidMapClassName", line));
                    }
                }
                if (tokenizer.hasMoreTokens()) {
                    if (tokenizer.countTokens() == 1) {
                        map.setParameters(tokenizer.nextToken());
                    } else {
                        List<String> params = new ArrayList<>();
                        while (tokenizer.hasMoreTokens()) {
                            params.add(tokenizer.nextToken());
                        }
                        map.setParameters((String[]) params.toArray(new String[0]));
                    }
                }
                return new Object[]{name, map};
            }
            if (!token.startsWith("#")) {
                throw new IllegalArgumentException(sm.getString("rewriteValve.invalidLine", line));
            }
            return null;
        }
        return null;
    }

    protected static void parseCondFlag(String line, RewriteCond condition, String flag) {
        if (flag.equals("NC") || flag.equals("nocase")) {
            condition.setNocase(true);
        } else {
            if (flag.equals("OR") || flag.equals("ornext")) {
                condition.setOrnext(true);
                return;
            }
            throw new IllegalArgumentException(sm.getString("rewriteValve.invalidFlags", line, flag));
        }
    }

    protected static void parseRuleFlag(String line, RewriteRule rule, String flag) {
        if (flag.equals("B")) {
            rule.setEscapeBackReferences(true);
            return;
        }
        if (flag.equals("chain") || flag.equals("C")) {
            rule.setChain(true);
            return;
        }
        if (flag.startsWith("cookie=") || flag.startsWith("CO=")) {
            rule.setCookie(true);
            if (flag.startsWith("cookie")) {
                flag = flag.substring("cookie=".length());
            } else if (flag.startsWith("CO=")) {
                flag = flag.substring("CO=".length());
            }
            StringTokenizer tokenizer = new StringTokenizer(flag, ":");
            if (tokenizer.countTokens() < 2) {
                throw new IllegalArgumentException(sm.getString("rewriteValve.invalidFlags", line, flag));
            }
            rule.setCookieName(tokenizer.nextToken());
            rule.setCookieValue(tokenizer.nextToken());
            if (tokenizer.hasMoreTokens()) {
                rule.setCookieDomain(tokenizer.nextToken());
            }
            if (tokenizer.hasMoreTokens()) {
                try {
                    rule.setCookieLifetime(Integer.parseInt(tokenizer.nextToken()));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(sm.getString("rewriteValve.invalidFlags", line, flag), e);
                }
            }
            if (tokenizer.hasMoreTokens()) {
                rule.setCookiePath(tokenizer.nextToken());
            }
            if (tokenizer.hasMoreTokens()) {
                rule.setCookieSecure(Boolean.parseBoolean(tokenizer.nextToken()));
            }
            if (tokenizer.hasMoreTokens()) {
                rule.setCookieHttpOnly(Boolean.parseBoolean(tokenizer.nextToken()));
                return;
            }
            return;
        }
        if (flag.startsWith("env=") || flag.startsWith("E=")) {
            rule.setEnv(true);
            if (flag.startsWith("env=")) {
                flag = flag.substring("env=".length());
            } else if (flag.startsWith("E=")) {
                flag = flag.substring("E=".length());
            }
            int pos = flag.indexOf(58);
            if (pos == -1 || pos + 1 == flag.length()) {
                throw new IllegalArgumentException(sm.getString("rewriteValve.invalidFlags", line, flag));
            }
            rule.addEnvName(flag.substring(0, pos));
            rule.addEnvValue(flag.substring(pos + 1));
            return;
        }
        if (flag.startsWith("forbidden") || flag.startsWith("F")) {
            rule.setForbidden(true);
            return;
        }
        if (flag.startsWith("gone") || flag.startsWith("G")) {
            rule.setGone(true);
            return;
        }
        if (flag.startsWith("host") || flag.startsWith("H")) {
            rule.setHost(true);
            return;
        }
        if (flag.startsWith("last") || flag.startsWith("L")) {
            rule.setLast(true);
            return;
        }
        if (flag.startsWith("nocase") || flag.startsWith("NC")) {
            rule.setNocase(true);
            return;
        }
        if (flag.startsWith("noescape") || flag.startsWith("NE")) {
            rule.setNoescape(true);
            return;
        }
        if (flag.startsWith("next") || flag.startsWith("N")) {
            rule.setNext(true);
            return;
        }
        if (flag.startsWith("qsappend") || flag.startsWith("QSA")) {
            rule.setQsappend(true);
            return;
        }
        if (flag.startsWith("qsdiscard") || flag.startsWith("QSD")) {
            rule.setQsdiscard(true);
            return;
        }
        if (!flag.startsWith("redirect") && !flag.startsWith("R")) {
            if (flag.startsWith("skip") || flag.startsWith("S")) {
                if (flag.startsWith("skip=")) {
                    flag = flag.substring("skip=".length());
                } else if (flag.startsWith("S=")) {
                    flag = flag.substring("S=".length());
                }
                rule.setSkip(Integer.parseInt(flag));
                return;
            }
            if (flag.startsWith("type") || flag.startsWith("T")) {
                if (flag.startsWith("type=")) {
                    flag = flag.substring("type=".length());
                } else if (flag.startsWith("T=")) {
                    flag = flag.substring("T=".length());
                }
                rule.setType(true);
                rule.setTypeValue(flag);
                return;
            }
            throw new IllegalArgumentException(sm.getString("rewriteValve.invalidFlags", line, flag));
        }
        rule.setRedirect(true);
        int redirectCode = 302;
        if (flag.startsWith("redirect=") || flag.startsWith("R=")) {
            if (flag.startsWith("redirect=")) {
                flag = flag.substring("redirect=".length());
            } else if (flag.startsWith("R=")) {
                flag = flag.substring("R=".length());
            }
            String str = flag;
            boolean z = -1;
            switch (str.hashCode()) {
                case 3556308:
                    if (str.equals("temp")) {
                        z = false;
                        break;
                    }
                    break;
                case 668488878:
                    if (str.equals("permanent")) {
                        z = true;
                        break;
                    }
                    break;
                case 1000898205:
                    if (str.equals("seeother")) {
                        z = 2;
                        break;
                    }
                    break;
            }
            switch (z) {
                case false:
                    redirectCode = 302;
                    break;
                case true:
                    redirectCode = 301;
                    break;
                case true:
                    redirectCode = 303;
                    break;
                default:
                    redirectCode = Integer.parseInt(flag);
                    break;
            }
        }
        rule.setRedirectCode(redirectCode);
    }
}
