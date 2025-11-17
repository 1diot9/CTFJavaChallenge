package org.apache.tomcat.util.http;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import org.apache.tomcat.util.http.parser.TokenList;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/ResponseUtil.class */
public class ResponseUtil {
    private static final String VARY_HEADER = "vary";
    private static final String VARY_ALL = "*";

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/ResponseUtil$Adapter.class */
    public interface Adapter {
        Collection<String> getHeaders(String str);

        void setHeader(String str, String str2);

        void addHeader(String str, String str2);
    }

    private ResponseUtil() {
    }

    public static void addVaryFieldName(MimeHeaders headers, String name) {
        addVaryFieldName(new HeaderAdapter(headers), name);
    }

    public static void addVaryFieldName(HttpServletResponse response, String name) {
        addVaryFieldName(new ResponseAdapter(response), name);
    }

    private static void addVaryFieldName(Adapter adapter, String name) {
        Collection<String> varyHeaders = adapter.getHeaders(VARY_HEADER);
        if (varyHeaders.size() == 1 && varyHeaders.iterator().next().trim().equals("*")) {
            return;
        }
        if (varyHeaders.size() == 0) {
            adapter.addHeader(VARY_HEADER, name);
            return;
        }
        if ("*".equals(name.trim())) {
            adapter.setHeader(VARY_HEADER, "*");
            return;
        }
        LinkedHashSet<String> fieldNames = new LinkedHashSet<>();
        Iterator<String> it = varyHeaders.iterator();
        while (it.hasNext()) {
            StringReader input = new StringReader(it.next());
            try {
                TokenList.parseTokenList(input, fieldNames);
            } catch (IOException e) {
            }
        }
        if (fieldNames.contains("*")) {
            adapter.setHeader(VARY_HEADER, "*");
            return;
        }
        fieldNames.add(name);
        StringBuilder varyHeader = new StringBuilder();
        Iterator<String> iter = fieldNames.iterator();
        varyHeader.append(iter.next());
        while (iter.hasNext()) {
            varyHeader.append(',');
            varyHeader.append(iter.next());
        }
        adapter.setHeader(VARY_HEADER, varyHeader.toString());
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/ResponseUtil$HeaderAdapter.class */
    private static final class HeaderAdapter implements Adapter {
        private final MimeHeaders headers;

        HeaderAdapter(MimeHeaders headers) {
            this.headers = headers;
        }

        @Override // org.apache.tomcat.util.http.ResponseUtil.Adapter
        public Collection<String> getHeaders(String name) {
            Enumeration<String> values = this.headers.values(name);
            List<String> result = new ArrayList<>();
            while (values.hasMoreElements()) {
                result.add(values.nextElement());
            }
            return result;
        }

        @Override // org.apache.tomcat.util.http.ResponseUtil.Adapter
        public void setHeader(String name, String value) {
            this.headers.setValue(name).setString(value);
        }

        @Override // org.apache.tomcat.util.http.ResponseUtil.Adapter
        public void addHeader(String name, String value) {
            this.headers.addValue(name).setString(value);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/ResponseUtil$ResponseAdapter.class */
    private static final class ResponseAdapter implements Adapter {
        private final HttpServletResponse response;

        ResponseAdapter(HttpServletResponse response) {
            this.response = response;
        }

        @Override // org.apache.tomcat.util.http.ResponseUtil.Adapter
        public Collection<String> getHeaders(String name) {
            return this.response.getHeaders(name);
        }

        @Override // org.apache.tomcat.util.http.ResponseUtil.Adapter
        public void setHeader(String name, String value) {
            this.response.setHeader(name, value);
        }

        @Override // org.apache.tomcat.util.http.ResponseUtil.Adapter
        public void addHeader(String name, String value) {
            this.response.addHeader(name, value);
        }
    }
}
