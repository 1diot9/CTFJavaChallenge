package org.apache.catalina.core;

import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.apache.tomcat.util.http.fileupload.ParameterParser;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItem;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.springframework.http.HttpHeaders;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/ApplicationPart.class */
public class ApplicationPart implements Part {
    private final FileItem fileItem;
    private final File location;

    public ApplicationPart(FileItem fileItem, File location) {
        this.fileItem = fileItem;
        this.location = location;
    }

    @Override // jakarta.servlet.http.Part
    public void delete() throws IOException {
        this.fileItem.delete();
    }

    @Override // jakarta.servlet.http.Part
    public String getContentType() {
        return this.fileItem.getContentType();
    }

    @Override // jakarta.servlet.http.Part
    public String getHeader(String name) {
        if (this.fileItem instanceof DiskFileItem) {
            return this.fileItem.getHeaders().getHeader(name);
        }
        return null;
    }

    @Override // jakarta.servlet.http.Part
    public Collection<String> getHeaderNames() {
        if (this.fileItem instanceof DiskFileItem) {
            LinkedHashSet<String> headerNames = new LinkedHashSet<>();
            Iterator<String> iter = this.fileItem.getHeaders().getHeaderNames();
            while (iter.hasNext()) {
                headerNames.add(iter.next());
            }
            return headerNames;
        }
        return Collections.emptyList();
    }

    @Override // jakarta.servlet.http.Part
    public Collection<String> getHeaders(String name) {
        if (this.fileItem instanceof DiskFileItem) {
            LinkedHashSet<String> headers = new LinkedHashSet<>();
            Iterator<String> iter = this.fileItem.getHeaders().getHeaders(name);
            while (iter.hasNext()) {
                headers.add(iter.next());
            }
            return headers;
        }
        return Collections.emptyList();
    }

    @Override // jakarta.servlet.http.Part
    public InputStream getInputStream() throws IOException {
        return this.fileItem.getInputStream();
    }

    @Override // jakarta.servlet.http.Part
    public String getName() {
        return this.fileItem.getFieldName();
    }

    @Override // jakarta.servlet.http.Part
    public long getSize() {
        return this.fileItem.getSize();
    }

    @Override // jakarta.servlet.http.Part
    public void write(String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.isAbsolute()) {
            file = new File(this.location, fileName);
        }
        try {
            this.fileItem.write(file);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public String getString(String encoding) throws UnsupportedEncodingException, IOException {
        return this.fileItem.getString(encoding);
    }

    @Override // jakarta.servlet.http.Part
    public String getSubmittedFileName() {
        String fileName = null;
        String cd = getHeader(HttpHeaders.CONTENT_DISPOSITION);
        if (cd != null) {
            String cdl = cd.toLowerCase(Locale.ENGLISH);
            if (cdl.startsWith(FileUploadBase.FORM_DATA) || cdl.startsWith(FileUploadBase.ATTACHMENT)) {
                ParameterParser paramParser = new ParameterParser();
                paramParser.setLowerCaseNames(true);
                Map<String, String> params = paramParser.parse(cd, ';');
                if (params.containsKey("filename")) {
                    String fileName2 = params.get("filename");
                    fileName = fileName2 != null ? fileName2.indexOf(92) > -1 ? HttpParser.unquote(fileName2.trim()) : fileName2.trim() : "";
                }
            }
        }
        return fileName;
    }
}
