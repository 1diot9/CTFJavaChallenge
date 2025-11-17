package org.springframework.core.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/UrlResource.class */
public class UrlResource extends AbstractFileResolvingResource {
    private static final String AUTHORIZATION = "Authorization";

    @Nullable
    private final URI uri;
    private final URL url;

    @Nullable
    private volatile String cleanedUrl;

    public UrlResource(URL url) {
        Assert.notNull(url, "URL must not be null");
        this.uri = null;
        this.url = url;
    }

    public UrlResource(URI uri) throws MalformedURLException {
        Assert.notNull(uri, "URI must not be null");
        this.uri = uri;
        this.url = uri.toURL();
    }

    public UrlResource(String path) throws MalformedURLException {
        URI uri;
        URL url;
        Assert.notNull(path, "Path must not be null");
        String cleanedPath = StringUtils.cleanPath(path);
        try {
            uri = ResourceUtils.toURI(cleanedPath);
            url = uri.toURL();
        } catch (IllegalArgumentException | URISyntaxException e) {
            uri = null;
            url = ResourceUtils.toURL(path);
        }
        this.uri = uri;
        this.url = url;
        this.cleanedUrl = cleanedPath;
    }

    public UrlResource(String protocol, String location) throws MalformedURLException {
        this(protocol, location, null);
    }

    public UrlResource(String protocol, String location, @Nullable String fragment) throws MalformedURLException {
        try {
            this.uri = new URI(protocol, location, fragment);
            this.url = this.uri.toURL();
        } catch (URISyntaxException ex) {
            MalformedURLException exToThrow = new MalformedURLException(ex.getMessage());
            exToThrow.initCause(ex);
            throw exToThrow;
        }
    }

    public static UrlResource from(URI uri) throws UncheckedIOException {
        try {
            return new UrlResource(uri);
        } catch (MalformedURLException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public static UrlResource from(String path) throws UncheckedIOException {
        try {
            return new UrlResource(path);
        } catch (MalformedURLException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private String getCleanedUrl() {
        String cleanedUrl = this.cleanedUrl;
        if (cleanedUrl != null) {
            return cleanedUrl;
        }
        String originalPath = (this.uri != null ? this.uri : this.url).toString();
        String cleanedUrl2 = StringUtils.cleanPath(originalPath);
        this.cleanedUrl = cleanedUrl2;
        return cleanedUrl2;
    }

    @Override // org.springframework.core.io.InputStreamSource
    public InputStream getInputStream() throws IOException {
        URLConnection con = this.url.openConnection();
        customizeConnection(con);
        try {
            return con.getInputStream();
        } catch (IOException ex) {
            if (con instanceof HttpURLConnection) {
                HttpURLConnection httpConn = (HttpURLConnection) con;
                httpConn.disconnect();
            }
            throw ex;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.core.io.AbstractFileResolvingResource
    public void customizeConnection(URLConnection con) throws IOException {
        super.customizeConnection(con);
        String userInfo = this.url.getUserInfo();
        if (userInfo != null) {
            String encodedCredentials = Base64.getUrlEncoder().encodeToString(userInfo.getBytes());
            con.setRequestProperty("Authorization", "Basic " + encodedCredentials);
        }
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public URL getURL() {
        return this.url;
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public URI getURI() throws IOException {
        if (this.uri != null) {
            return this.uri;
        }
        return super.getURI();
    }

    @Override // org.springframework.core.io.AbstractFileResolvingResource, org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public boolean isFile() {
        if (this.uri != null) {
            return super.isFile(this.uri);
        }
        return super.isFile();
    }

    @Override // org.springframework.core.io.AbstractFileResolvingResource, org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public File getFile() throws IOException {
        if (this.uri != null) {
            return super.getFile(this.uri);
        }
        return super.getFile();
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public Resource createRelative(String relativePath) throws MalformedURLException {
        return new UrlResource(createRelativeURL(relativePath));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public URL createRelativeURL(String relativePath) throws MalformedURLException {
        if (relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }
        return ResourceUtils.toRelativeURL(this.url, relativePath);
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    @Nullable
    public String getFilename() {
        if (this.uri != null) {
            String path = this.uri.getPath();
            if (path != null) {
                return StringUtils.getFilename(this.uri.getPath());
            }
        }
        String filename = StringUtils.getFilename(StringUtils.cleanPath(this.url.getPath()));
        if (filename != null) {
            return URLDecoder.decode(filename, StandardCharsets.UTF_8);
        }
        return null;
    }

    @Override // org.springframework.core.io.Resource
    public String getDescription() {
        return "URL [" + (this.uri != null ? this.uri : this.url) + "]";
    }

    @Override // org.springframework.core.io.AbstractResource
    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof UrlResource) {
                UrlResource that = (UrlResource) other;
                if (getCleanedUrl().equals(that.getCleanedUrl())) {
                }
            }
            return false;
        }
        return true;
    }

    @Override // org.springframework.core.io.AbstractResource
    public int hashCode() {
        return getCleanedUrl().hashCode();
    }
}
