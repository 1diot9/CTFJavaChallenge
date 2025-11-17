package org.apache.tomcat.util.http.fileupload.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.tomcat.util.http.fileupload.FileItemHeaders;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/fileupload/util/FileItemHeadersImpl.class */
public class FileItemHeadersImpl implements FileItemHeaders, Serializable {
    private static final long serialVersionUID = -4455695752627032559L;
    private final Map<String, List<String>> headerNameToValueListMap = new LinkedHashMap();

    @Override // org.apache.tomcat.util.http.fileupload.FileItemHeaders
    public String getHeader(String name) {
        String nameLower = name.toLowerCase(Locale.ENGLISH);
        List<String> headerValueList = this.headerNameToValueListMap.get(nameLower);
        if (null == headerValueList) {
            return null;
        }
        return headerValueList.get(0);
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItemHeaders
    public Iterator<String> getHeaderNames() {
        return this.headerNameToValueListMap.keySet().iterator();
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItemHeaders
    public Iterator<String> getHeaders(String name) {
        String nameLower = name.toLowerCase(Locale.ENGLISH);
        List<String> headerValueList = this.headerNameToValueListMap.get(nameLower);
        if (null == headerValueList) {
            headerValueList = Collections.emptyList();
        }
        return headerValueList.iterator();
    }

    public synchronized void addHeader(String name, String value) {
        String nameLower = name.toLowerCase(Locale.ENGLISH);
        List<String> headerValueList = this.headerNameToValueListMap.computeIfAbsent(nameLower, k -> {
            return new ArrayList();
        });
        headerValueList.add(value);
    }
}
