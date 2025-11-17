package org.apache.tomcat.util.http.fileupload.disk;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.tomcat.util.http.fileupload.DeferredFileOutputStream;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileItemHeaders;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.apache.tomcat.util.http.fileupload.ParameterParser;
import org.apache.tomcat.util.http.fileupload.util.Streams;
import org.apache.tomcat.websocket.BasicAuthenticator;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/fileupload/disk/DiskFileItem.class */
public class DiskFileItem implements FileItem {
    public static final String DEFAULT_CHARSET = "ISO-8859-1";
    private static final String UID = UUID.randomUUID().toString().replace('-', '_');
    private static final AtomicInteger COUNTER = new AtomicInteger(0);
    private String fieldName;
    private final String contentType;
    private boolean isFormField;
    private final String fileName;
    private final int sizeThreshold;
    private final File repository;
    private byte[] cachedContent;
    private transient DeferredFileOutputStream dfos;
    private transient File tempFile;
    private FileItemHeaders headers;
    private long size = -1;
    private String defaultCharset = "ISO-8859-1";

    public DiskFileItem(String fieldName, String contentType, boolean isFormField, String fileName, int sizeThreshold, File repository) {
        this.fieldName = fieldName;
        this.contentType = contentType;
        this.isFormField = isFormField;
        this.fileName = fileName;
        this.sizeThreshold = sizeThreshold;
        this.repository = repository;
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItem
    public InputStream getInputStream() throws IOException {
        if (!isInMemory()) {
            return Files.newInputStream(this.dfos.getFile().toPath(), new OpenOption[0]);
        }
        if (this.cachedContent == null) {
            this.cachedContent = this.dfos.getData();
        }
        return new ByteArrayInputStream(this.cachedContent);
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItem
    public String getContentType() {
        return this.contentType;
    }

    public String getCharSet() {
        ParameterParser parser = new ParameterParser();
        parser.setLowerCaseNames(true);
        Map<String, String> params = parser.parse(getContentType(), ';');
        return params.get(BasicAuthenticator.charsetparam);
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItem
    public String getName() {
        return Streams.checkFileName(this.fileName);
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItem
    public boolean isInMemory() {
        if (this.cachedContent != null) {
            return true;
        }
        return this.dfos.isInMemory();
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItem
    public long getSize() {
        if (this.size >= 0) {
            return this.size;
        }
        if (this.cachedContent != null) {
            return this.cachedContent.length;
        }
        if (this.dfos.isInMemory()) {
            return this.dfos.getData().length;
        }
        return this.dfos.getFile().length();
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItem
    public byte[] get() throws UncheckedIOException {
        if (isInMemory()) {
            if (this.cachedContent == null && this.dfos != null) {
                this.cachedContent = this.dfos.getData();
            }
            return this.cachedContent != null ? (byte[]) this.cachedContent.clone() : new byte[0];
        }
        byte[] fileData = new byte[(int) getSize()];
        try {
            InputStream fis = Files.newInputStream(this.dfos.getFile().toPath(), new OpenOption[0]);
            try {
                IOUtils.readFully(fis, fileData);
                if (fis != null) {
                    fis.close();
                }
                return fileData;
            } finally {
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItem
    public String getString(String charset) throws UnsupportedEncodingException, IOException {
        return new String(get(), charset);
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItem
    public String getString() {
        try {
            byte[] rawData = get();
            String charset = getCharSet();
            if (charset == null) {
                charset = this.defaultCharset;
            }
            return new String(rawData, charset);
        } catch (IOException e) {
            return "";
        }
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItem
    public void write(File file) throws Exception {
        if (isInMemory()) {
            OutputStream fout = Files.newOutputStream(file.toPath(), new OpenOption[0]);
            try {
                fout.write(get());
                if (fout != null) {
                    fout.close();
                    return;
                }
                return;
            } catch (Throwable th) {
                if (fout != null) {
                    try {
                        fout.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }
        File outputFile = getStoreLocation();
        if (outputFile == null) {
            throw new FileUploadException("Cannot write uploaded file to disk!");
        }
        this.size = outputFile.length();
        if (file.exists() && !file.delete()) {
            throw new FileUploadException("Cannot write uploaded file to disk!");
        }
        if (!outputFile.renameTo(file)) {
            BufferedInputStream in = null;
            BufferedOutputStream out = null;
            try {
                in = new BufferedInputStream(new FileInputStream(outputFile));
                out = new BufferedOutputStream(new FileOutputStream(file));
                IOUtils.copy(in, out);
                out.close();
                IOUtils.closeQuietly(in);
                IOUtils.closeQuietly(out);
            } catch (Throwable th3) {
                IOUtils.closeQuietly(in);
                IOUtils.closeQuietly(out);
                throw th3;
            }
        }
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItem
    public void delete() {
        this.cachedContent = null;
        File outputFile = getStoreLocation();
        if (outputFile != null && !isInMemory() && outputFile.exists() && !outputFile.delete()) {
            String desc = "Cannot delete " + outputFile.toString();
            throw new UncheckedIOException(desc, new IOException(desc));
        }
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItem
    public String getFieldName() {
        return this.fieldName;
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItem
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItem
    public boolean isFormField() {
        return this.isFormField;
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItem
    public void setFormField(boolean state) {
        this.isFormField = state;
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItem
    public OutputStream getOutputStream() {
        if (this.dfos == null) {
            File outputFile = getTempFile();
            this.dfos = new DeferredFileOutputStream(this.sizeThreshold, outputFile);
        }
        return this.dfos;
    }

    public File getStoreLocation() {
        if (this.dfos == null || isInMemory()) {
            return null;
        }
        return this.dfos.getFile();
    }

    protected File getTempFile() {
        if (this.tempFile == null) {
            File tempDir = this.repository;
            if (tempDir == null) {
                tempDir = new File(System.getProperty("java.io.tmpdir"));
            }
            String tempFileName = String.format("upload_%s_%s.tmp", UID, getUniqueId());
            this.tempFile = new File(tempDir, tempFileName);
        }
        return this.tempFile;
    }

    private static String getUniqueId() {
        int current = COUNTER.getAndIncrement();
        String id = Integer.toString(current);
        if (current < 100000000) {
            id = ("00000000" + id).substring(id.length());
        }
        return id;
    }

    public String toString() {
        return String.format("name=%s, StoreLocation=%s, size=%s bytes, isFormField=%s, FieldName=%s", getName(), getStoreLocation(), Long.valueOf(getSize()), Boolean.valueOf(isFormField()), getFieldName());
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItemHeadersSupport
    public FileItemHeaders getHeaders() {
        return this.headers;
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItemHeadersSupport
    public void setHeaders(FileItemHeaders pHeaders) {
        this.headers = pHeaders;
    }

    public String getDefaultCharset() {
        return this.defaultCharset;
    }

    public void setDefaultCharset(String charset) {
        this.defaultCharset = charset;
    }
}
