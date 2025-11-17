package org.apache.catalina.startup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.apache.tomcat.util.buf.UriUtil;
import org.apache.tomcat.util.file.ConfigurationSource;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/startup/CatalinaBaseConfigurationSource.class */
public class CatalinaBaseConfigurationSource implements ConfigurationSource {
    protected static final StringManager sm = StringManager.getManager(Constants.Package);
    private final String serverXmlPath;
    private final File catalinaBaseFile;
    private final URI catalinaBaseUri;

    public CatalinaBaseConfigurationSource(File catalinaBaseFile, String serverXmlPath) {
        this.catalinaBaseFile = catalinaBaseFile;
        this.catalinaBaseUri = catalinaBaseFile.toURI();
        this.serverXmlPath = serverXmlPath;
    }

    @Override // org.apache.tomcat.util.file.ConfigurationSource
    public ConfigurationSource.Resource getServerXml() throws IOException {
        InputStream stream;
        IOException ioe = null;
        ConfigurationSource.Resource result = null;
        try {
            if (this.serverXmlPath == null || this.serverXmlPath.equals(Catalina.SERVER_XML)) {
                result = super.getServerXml();
            } else {
                result = getResource(this.serverXmlPath);
            }
        } catch (IOException e) {
            ioe = e;
        }
        if (result == null && (stream = getClass().getClassLoader().getResourceAsStream("server-embed.xml")) != null) {
            try {
                result = new ConfigurationSource.Resource(stream, getClass().getClassLoader().getResource("server-embed.xml").toURI());
            } catch (URISyntaxException e2) {
                stream.close();
            }
        }
        if (result == null && ioe != null) {
            throw ioe;
        }
        return result;
    }

    @Override // org.apache.tomcat.util.file.ConfigurationSource
    public ConfigurationSource.Resource getResource(String name) throws IOException {
        if (!UriUtil.isAbsoluteURI(name)) {
            File f = new File(name);
            if (!f.isAbsolute()) {
                f = new File(this.catalinaBaseFile, name);
            }
            if (f.isFile()) {
                FileInputStream fis = new FileInputStream(f);
                return new ConfigurationSource.Resource(fis, f.toURI());
            }
            InputStream stream = null;
            try {
                stream = getClass().getClassLoader().getResourceAsStream(name);
                if (stream != null) {
                    return new ConfigurationSource.Resource(stream, getClass().getClassLoader().getResource(name).toURI());
                }
            } catch (URISyntaxException e) {
                stream.close();
                throw new IOException(sm.getString("catalinaConfigurationSource.cannotObtainURL", name), e);
            }
        }
        try {
            URI uri = getURIInternal(name);
            try {
                URL url = uri.toURL();
                return new ConfigurationSource.Resource(url.openConnection().getInputStream(), uri);
            } catch (MalformedURLException e2) {
                throw new IOException(sm.getString("catalinaConfigurationSource.cannotObtainURL", name), e2);
            }
        } catch (IllegalArgumentException e3) {
            throw new IOException(sm.getString("catalinaConfigurationSource.cannotObtainURL", name));
        }
    }

    @Override // org.apache.tomcat.util.file.ConfigurationSource
    public URI getURI(String name) {
        if (!UriUtil.isAbsoluteURI(name)) {
            File f = new File(name);
            if (!f.isAbsolute()) {
                f = new File(this.catalinaBaseFile, name);
            }
            if (f.isFile()) {
                return f.toURI();
            }
            try {
                URL resource = getClass().getClassLoader().getResource(name);
                if (resource != null) {
                    return resource.toURI();
                }
            } catch (Exception e) {
            }
        }
        return getURIInternal(name);
    }

    private URI getURIInternal(String name) {
        URI uri;
        if (this.catalinaBaseUri != null) {
            uri = this.catalinaBaseUri.resolve(name);
        } else {
            uri = URI.create(name);
        }
        return uri;
    }
}
