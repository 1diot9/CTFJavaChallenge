package org.apache.catalina.webresources;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.cert.Certificate;
import java.util.jar.Manifest;
import org.apache.catalina.WebResourceRoot;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/webresources/FileResource.class */
public class FileResource extends AbstractResource {
    private static final Log log = LogFactory.getLog((Class<?>) FileResource.class);
    private static final boolean PROPERTIES_NEED_CONVERT;
    private final File resource;
    private final String name;
    private final boolean readOnly;
    private final Manifest manifest;
    private final boolean needConvert;

    static {
        boolean isEBCDIC = false;
        try {
            String encoding = Charset.defaultCharset().displayName();
            if (encoding.contains("EBCDIC")) {
                isEBCDIC = true;
            }
        } catch (SecurityException e) {
        }
        PROPERTIES_NEED_CONVERT = isEBCDIC;
    }

    public FileResource(WebResourceRoot root, String webAppPath, File resource, boolean readOnly, Manifest manifest) {
        super(root, webAppPath);
        this.resource = resource;
        if (webAppPath.charAt(webAppPath.length() - 1) == '/') {
            String realName = resource.getName() + "/";
            if (webAppPath.endsWith(realName)) {
                this.name = resource.getName();
            } else {
                int endOfName = webAppPath.length() - 1;
                this.name = webAppPath.substring(webAppPath.lastIndexOf(47, endOfName - 1) + 1, endOfName);
            }
        } else {
            this.name = resource.getName();
        }
        this.readOnly = readOnly;
        this.manifest = manifest;
        this.needConvert = PROPERTIES_NEED_CONVERT && this.name.endsWith(".properties");
    }

    @Override // org.apache.catalina.WebResource
    public long getLastModified() {
        return this.resource.lastModified();
    }

    @Override // org.apache.catalina.WebResource
    public boolean exists() {
        return this.resource.exists();
    }

    @Override // org.apache.catalina.WebResource
    public boolean isVirtual() {
        return false;
    }

    @Override // org.apache.catalina.WebResource
    public boolean isDirectory() {
        return this.resource.isDirectory();
    }

    @Override // org.apache.catalina.WebResource
    public boolean isFile() {
        return this.resource.isFile();
    }

    @Override // org.apache.catalina.WebResource
    public boolean delete() {
        if (this.readOnly) {
            return false;
        }
        return this.resource.delete();
    }

    @Override // org.apache.catalina.WebResource
    public String getName() {
        return this.name;
    }

    @Override // org.apache.catalina.WebResource
    public long getContentLength() {
        return getContentLengthInternal(this.needConvert);
    }

    private long getContentLengthInternal(boolean convert) {
        if (convert) {
            byte[] content = getContent();
            if (content == null) {
                return -1L;
            }
            return content.length;
        }
        if (isDirectory()) {
            return -1L;
        }
        return this.resource.length();
    }

    @Override // org.apache.catalina.WebResource
    public String getCanonicalPath() {
        try {
            return this.resource.getCanonicalPath();
        } catch (IOException ioe) {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("fileResource.getCanonicalPathFail", this.resource.getPath()), ioe);
                return null;
            }
            return null;
        }
    }

    @Override // org.apache.catalina.WebResource
    public boolean canRead() {
        return this.resource.canRead();
    }

    @Override // org.apache.catalina.webresources.AbstractResource
    protected InputStream doGetInputStream() {
        if (this.needConvert) {
            byte[] content = getContent();
            if (content == null) {
                return null;
            }
            return new ByteArrayInputStream(content);
        }
        try {
            return new FileInputStream(this.resource);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    @Override // org.apache.catalina.WebResource
    public final byte[] getContent() {
        long len = getContentLengthInternal(false);
        if (len > 2147483647L) {
            throw new ArrayIndexOutOfBoundsException(sm.getString("abstractResource.getContentTooLarge", getWebappPath(), Long.valueOf(len)));
        }
        if (len < 0) {
            return null;
        }
        int size = (int) len;
        byte[] result = new byte[size];
        int pos = 0;
        try {
            InputStream is = new FileInputStream(this.resource);
            while (pos < size) {
                try {
                    int n = is.read(result, pos, size - pos);
                    if (n < 0) {
                        break;
                    }
                    pos += n;
                } finally {
                }
            }
            is.close();
            if (this.needConvert) {
                String str = new String(result);
                try {
                    result = str.getBytes(StandardCharsets.UTF_8);
                } catch (Exception e) {
                    result = null;
                }
            }
            return result;
        } catch (IOException ioe) {
            if (getLog().isDebugEnabled()) {
                getLog().debug(sm.getString("abstractResource.getContentFail", getWebappPath()), ioe);
                return null;
            }
            return null;
        }
    }

    @Override // org.apache.catalina.WebResource
    public long getCreation() {
        try {
            BasicFileAttributes attrs = Files.readAttributes(this.resource.toPath(), (Class<BasicFileAttributes>) BasicFileAttributes.class, new LinkOption[0]);
            return attrs.creationTime().toMillis();
        } catch (IOException e) {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("fileResource.getCreationFail", this.resource.getPath()), e);
                return 0L;
            }
            return 0L;
        }
    }

    @Override // org.apache.catalina.WebResource
    public URL getURL() {
        if (this.resource.exists()) {
            try {
                return this.resource.toURI().toURL();
            } catch (MalformedURLException e) {
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("fileResource.getUrlFail", this.resource.getPath()), e);
                    return null;
                }
                return null;
            }
        }
        return null;
    }

    @Override // org.apache.catalina.WebResource
    public URL getCodeBase() {
        if (getWebappPath().startsWith("/WEB-INF/classes/") && this.name.endsWith(ClassUtils.CLASS_FILE_SUFFIX)) {
            return getWebResourceRoot().getResource("/WEB-INF/classes/").getURL();
        }
        return getURL();
    }

    @Override // org.apache.catalina.WebResource
    public Certificate[] getCertificates() {
        return null;
    }

    @Override // org.apache.catalina.WebResource
    public Manifest getManifest() {
        return this.manifest;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.webresources.AbstractResource
    public Log getLog() {
        return log;
    }
}
