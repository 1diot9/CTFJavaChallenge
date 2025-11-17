package org.apache.tomcat.util.descriptor.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.apache.tomcat.Jar;
import org.apache.tomcat.JarScannerCallback;
import org.springframework.util.ResourceUtils;
import org.xml.sax.InputSource;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/descriptor/web/FragmentJarScannerCallback.class */
public class FragmentJarScannerCallback implements JarScannerCallback {
    private static final String FRAGMENT_LOCATION = "META-INF/web-fragment.xml";
    private final WebXmlParser webXmlParser;
    private final boolean delegate;
    private final boolean parseRequired;
    private final Map<String, WebXml> fragments = new HashMap();
    private boolean ok = true;

    public FragmentJarScannerCallback(WebXmlParser webXmlParser, boolean delegate, boolean parseRequired) {
        this.webXmlParser = webXmlParser;
        this.delegate = delegate;
        this.parseRequired = parseRequired;
    }

    @Override // org.apache.tomcat.JarScannerCallback
    public void scan(Jar jar, String webappPath, boolean isWebapp) throws IOException {
        InputStream is = null;
        WebXml fragment = new WebXml();
        fragment.setWebappJar(isWebapp);
        fragment.setDelegate(this.delegate);
        if (isWebapp) {
            try {
                if (this.parseRequired) {
                    is = jar.getInputStream(FRAGMENT_LOCATION);
                }
            } finally {
                addFragment(fragment, jar.getJarFileURL());
            }
        }
        if (is == null) {
            fragment.setDistributable(true);
        } else {
            String fragmentUrl = jar.getURL(FRAGMENT_LOCATION);
            InputSource source = new InputSource(fragmentUrl);
            source.setByteStream(is);
            if (!this.webXmlParser.parseWebXml(source, fragment, true)) {
                this.ok = false;
            }
        }
    }

    private String extractJarFileName(URL input) {
        String url = input.toString();
        if (url.endsWith(ResourceUtils.JAR_URL_SEPARATOR)) {
            url = url.substring(0, url.length() - 2);
        }
        return url.substring(url.lastIndexOf(47) + 1);
    }

    @Override // org.apache.tomcat.JarScannerCallback
    public void scan(File file, String webappPath, boolean isWebapp) throws IOException {
        WebXml fragment = new WebXml();
        fragment.setWebappJar(isWebapp);
        fragment.setDelegate(this.delegate);
        File fragmentFile = new File(file, FRAGMENT_LOCATION);
        try {
            if (fragmentFile.isFile()) {
                InputStream stream = new FileInputStream(fragmentFile);
                try {
                    InputSource source = new InputSource(fragmentFile.toURI().toURL().toString());
                    source.setByteStream(stream);
                    if (!this.webXmlParser.parseWebXml(source, fragment, true)) {
                        this.ok = false;
                    }
                    stream.close();
                } finally {
                }
            } else {
                fragment.setDistributable(true);
            }
        } finally {
            addFragment(fragment, file.toURI().toURL());
        }
    }

    private void addFragment(WebXml fragment, URL url) {
        fragment.setURL(url);
        if (fragment.getName() == null) {
            fragment.setName(url.toString());
        }
        fragment.setJarName(extractJarFileName(url));
        if (this.fragments.containsKey(fragment.getName())) {
            String duplicateName = fragment.getName();
            this.fragments.get(duplicateName).addDuplicate(url.toString());
            fragment.setName(url.toString());
        }
        this.fragments.put(fragment.getName(), fragment);
    }

    @Override // org.apache.tomcat.JarScannerCallback
    public void scanWebInfClasses() {
    }

    public boolean isOk() {
        return this.ok;
    }

    public Map<String, WebXml> getFragments() {
        return this.fragments;
    }
}
