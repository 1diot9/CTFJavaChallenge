package org.apache.tomcat.util.descriptor.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.tomcat.util.buf.UDecoder;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/descriptor/web/JspPropertyGroup.class */
public class JspPropertyGroup extends XmlEncodingBase {
    private Boolean deferredSyntax = null;
    private Boolean errorOnELNotFound = null;
    private Boolean elIgnored = null;
    private final Collection<String> includeCodas = new ArrayList();
    private final Collection<String> includePreludes = new ArrayList();
    private Boolean isXml = null;
    private String pageEncoding = null;
    private Boolean scriptingInvalid = null;
    private Boolean trimWhitespace = null;
    private LinkedHashSet<String> urlPattern = new LinkedHashSet<>();
    private String defaultContentType = null;
    private String buffer = null;
    private Boolean errorOnUndeclaredNamespace = null;

    public void setDeferredSyntax(String deferredSyntax) {
        this.deferredSyntax = Boolean.valueOf(deferredSyntax);
    }

    public Boolean getDeferredSyntax() {
        return this.deferredSyntax;
    }

    public void setErrorOnELNotFound(String errorOnELNotFound) {
        this.errorOnELNotFound = Boolean.valueOf(errorOnELNotFound);
    }

    public Boolean getErrorOnELNotFound() {
        return this.errorOnELNotFound;
    }

    public void setElIgnored(String elIgnored) {
        this.elIgnored = Boolean.valueOf(elIgnored);
    }

    public Boolean getElIgnored() {
        return this.elIgnored;
    }

    public void addIncludeCoda(String includeCoda) {
        this.includeCodas.add(includeCoda);
    }

    public Collection<String> getIncludeCodas() {
        return this.includeCodas;
    }

    public void addIncludePrelude(String includePrelude) {
        this.includePreludes.add(includePrelude);
    }

    public Collection<String> getIncludePreludes() {
        return this.includePreludes;
    }

    public void setIsXml(String isXml) {
        this.isXml = Boolean.valueOf(isXml);
    }

    public Boolean getIsXml() {
        return this.isXml;
    }

    public void setPageEncoding(String pageEncoding) {
        this.pageEncoding = pageEncoding;
    }

    public String getPageEncoding() {
        return this.pageEncoding;
    }

    public void setScriptingInvalid(String scriptingInvalid) {
        this.scriptingInvalid = Boolean.valueOf(scriptingInvalid);
    }

    public Boolean getScriptingInvalid() {
        return this.scriptingInvalid;
    }

    public void setTrimWhitespace(String trimWhitespace) {
        this.trimWhitespace = Boolean.valueOf(trimWhitespace);
    }

    public Boolean getTrimWhitespace() {
        return this.trimWhitespace;
    }

    public void addUrlPattern(String urlPattern) {
        addUrlPatternDecoded(UDecoder.URLDecode(urlPattern, getCharset()));
    }

    public void addUrlPatternDecoded(String urlPattern) {
        this.urlPattern.add(urlPattern);
    }

    public Set<String> getUrlPatterns() {
        return this.urlPattern;
    }

    public void setDefaultContentType(String defaultContentType) {
        this.defaultContentType = defaultContentType;
    }

    public String getDefaultContentType() {
        return this.defaultContentType;
    }

    public void setBuffer(String buffer) {
        this.buffer = buffer;
    }

    public String getBuffer() {
        return this.buffer;
    }

    public void setErrorOnUndeclaredNamespace(String errorOnUndeclaredNamespace) {
        this.errorOnUndeclaredNamespace = Boolean.valueOf(errorOnUndeclaredNamespace);
    }

    public Boolean getErrorOnUndeclaredNamespace() {
        return this.errorOnUndeclaredNamespace;
    }
}
