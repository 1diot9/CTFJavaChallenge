package ch.qos.logback.classic.html;

import ch.qos.logback.core.html.CssBuilder;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/html/UrlCssBuilder.class */
public class UrlCssBuilder implements CssBuilder {
    String url = "http://logback.qos.ch/css/classic.css";

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override // ch.qos.logback.core.html.CssBuilder
    public void addCss(StringBuilder sbuf) {
        sbuf.append("<link REL=StyleSheet HREF=\"");
        sbuf.append(this.url);
        sbuf.append("\" TITLE=\"Basic\" />");
    }
}
