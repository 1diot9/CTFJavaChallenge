package org.apache.tomcat.util.http.fileupload;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import org.apache.tomcat.util.http.fileupload.impl.FileCountLimitExceededException;
import org.apache.tomcat.util.http.fileupload.impl.FileItemIteratorImpl;
import org.apache.tomcat.util.http.fileupload.impl.FileUploadIOException;
import org.apache.tomcat.util.http.fileupload.impl.IOFileUploadException;
import org.apache.tomcat.util.http.fileupload.util.FileItemHeadersImpl;
import org.apache.tomcat.util.http.fileupload.util.Streams;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/fileupload/FileUploadBase.class */
public abstract class FileUploadBase {
    public static final String CONTENT_TYPE = "Content-type";
    public static final String CONTENT_DISPOSITION = "Content-disposition";
    public static final String CONTENT_LENGTH = "Content-length";
    public static final String FORM_DATA = "form-data";
    public static final String ATTACHMENT = "attachment";
    public static final String MULTIPART = "multipart/";
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    public static final String MULTIPART_MIXED = "multipart/mixed";
    private long sizeMax = -1;
    private long fileSizeMax = -1;
    private long fileCountMax = -1;
    private String headerEncoding;
    private ProgressListener listener;

    public abstract FileItemFactory getFileItemFactory();

    public abstract void setFileItemFactory(FileItemFactory fileItemFactory);

    public long getSizeMax() {
        return this.sizeMax;
    }

    public void setSizeMax(long sizeMax) {
        this.sizeMax = sizeMax;
    }

    public long getFileSizeMax() {
        return this.fileSizeMax;
    }

    public void setFileSizeMax(long fileSizeMax) {
        this.fileSizeMax = fileSizeMax;
    }

    public long getFileCountMax() {
        return this.fileCountMax;
    }

    public void setFileCountMax(long fileCountMax) {
        this.fileCountMax = fileCountMax;
    }

    public String getHeaderEncoding() {
        return this.headerEncoding;
    }

    public void setHeaderEncoding(String encoding) {
        this.headerEncoding = encoding;
    }

    public FileItemIterator getItemIterator(RequestContext ctx) throws FileUploadException, IOException {
        try {
            return new FileItemIteratorImpl(this, ctx);
        } catch (FileUploadIOException e) {
            throw ((FileUploadException) e.getCause());
        }
    }

    public List<FileItem> parseRequest(RequestContext ctx) throws FileUploadException {
        List<FileItem> items = new ArrayList<>();
        boolean successful = false;
        try {
            try {
                try {
                    FileItemIterator iter = getItemIterator(ctx);
                    FileItemFactory fileItemFactory = (FileItemFactory) Objects.requireNonNull(getFileItemFactory(), "No FileItemFactory has been set.");
                    byte[] buffer = new byte[8192];
                    while (iter.hasNext()) {
                        if (items.size() == this.fileCountMax) {
                            throw new FileCountLimitExceededException(ATTACHMENT, getFileCountMax());
                        }
                        FileItemStream item = iter.next();
                        String fileName = item.getName();
                        FileItem fileItem = fileItemFactory.createItem(item.getFieldName(), item.getContentType(), item.isFormField(), fileName);
                        items.add(fileItem);
                        try {
                            Streams.copy(item.openStream(), fileItem.getOutputStream(), true, buffer);
                            FileItemHeaders fih = item.getHeaders();
                            fileItem.setHeaders(fih);
                        } catch (FileUploadIOException e) {
                            throw ((FileUploadException) e.getCause());
                        } catch (IOException e2) {
                            throw new IOFileUploadException(String.format("Processing of %s request failed. %s", "multipart/form-data", e2.getMessage()), e2);
                        }
                    }
                    successful = true;
                    return items;
                } catch (IOException e3) {
                    throw new FileUploadException(e3.getMessage(), e3);
                }
            } catch (FileUploadException e4) {
                throw e4;
            }
        } finally {
            if (!successful) {
                Iterator<FileItem> it = items.iterator();
                while (it.hasNext()) {
                    try {
                        it.next().delete();
                    } catch (Exception e5) {
                    }
                }
            }
        }
    }

    public byte[] getBoundary(String contentType) {
        ParameterParser parser = new ParameterParser();
        parser.setLowerCaseNames(true);
        Map<String, String> params = parser.parse(contentType, new char[]{';', ','});
        String boundaryStr = params.get("boundary");
        if (boundaryStr == null) {
            return null;
        }
        byte[] boundary = boundaryStr.getBytes(StandardCharsets.ISO_8859_1);
        return boundary;
    }

    public String getFileName(FileItemHeaders headers) {
        return getFileName(headers.getHeader(CONTENT_DISPOSITION));
    }

    private String getFileName(String pContentDisposition) {
        String fileName = null;
        if (pContentDisposition != null) {
            String cdl = pContentDisposition.toLowerCase(Locale.ENGLISH);
            if (cdl.startsWith(FORM_DATA) || cdl.startsWith(ATTACHMENT)) {
                ParameterParser parser = new ParameterParser();
                parser.setLowerCaseNames(true);
                Map<String, String> params = parser.parse(pContentDisposition, ';');
                if (params.containsKey("filename")) {
                    String fileName2 = params.get("filename");
                    fileName = fileName2 != null ? fileName2.trim() : "";
                }
            }
        }
        return fileName;
    }

    public String getFieldName(FileItemHeaders headers) {
        return getFieldName(headers.getHeader(CONTENT_DISPOSITION));
    }

    private String getFieldName(String pContentDisposition) {
        String fieldName = null;
        if (pContentDisposition != null && pContentDisposition.toLowerCase(Locale.ENGLISH).startsWith(FORM_DATA)) {
            ParameterParser parser = new ParameterParser();
            parser.setLowerCaseNames(true);
            Map<String, String> params = parser.parse(pContentDisposition, ';');
            fieldName = params.get("name");
            if (fieldName != null) {
                fieldName = fieldName.trim();
            }
        }
        return fieldName;
    }

    public FileItemHeaders getParsedHeaders(String headerPart) {
        char c;
        int len = headerPart.length();
        FileItemHeadersImpl headers = newFileItemHeaders();
        int start = 0;
        while (true) {
            int end = parseEndOfLine(headerPart, start);
            if (start != end) {
                StringBuilder header = new StringBuilder(headerPart.substring(start, end));
                while (true) {
                    start = end + 2;
                    if (start < len) {
                        int nonWs = start;
                        while (nonWs < len && ((c = headerPart.charAt(nonWs)) == ' ' || c == '\t')) {
                            nonWs++;
                        }
                        if (nonWs == start) {
                            break;
                        }
                        end = parseEndOfLine(headerPart, nonWs);
                        header.append(' ').append((CharSequence) headerPart, nonWs, end);
                    }
                }
                parseHeaderLine(headers, header.toString());
            } else {
                return headers;
            }
        }
    }

    protected FileItemHeadersImpl newFileItemHeaders() {
        return new FileItemHeadersImpl();
    }

    private int parseEndOfLine(String headerPart, int end) {
        int i = end;
        while (true) {
            int index = i;
            int offset = headerPart.indexOf(13, index);
            if (offset == -1 || offset + 1 >= headerPart.length()) {
                break;
            }
            if (headerPart.charAt(offset + 1) == '\n') {
                return offset;
            }
            i = offset + 1;
        }
        throw new IllegalStateException("Expected headers to be terminated by an empty line.");
    }

    private void parseHeaderLine(FileItemHeadersImpl headers, String header) {
        int colonOffset = header.indexOf(58);
        if (colonOffset == -1) {
            return;
        }
        String headerName = header.substring(0, colonOffset).trim();
        String headerValue = header.substring(colonOffset + 1).trim();
        headers.addHeader(headerName, headerValue);
    }

    public ProgressListener getProgressListener() {
        return this.listener;
    }

    public void setProgressListener(ProgressListener pListener) {
        this.listener = pListener;
    }
}
