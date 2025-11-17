package org.apache.catalina.valves.rewrite;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.connector.Request;
import org.apache.tomcat.util.http.FastHttpDateFormat;
import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.net.SSLUtilBase;
import org.apache.tomcat.util.net.jsse.PEMFile;
import org.apache.tomcat.util.net.openssl.ciphers.Cipher;
import org.apache.tomcat.util.net.openssl.ciphers.EncryptionLevel;
import org.apache.tomcat.util.net.openssl.ciphers.OpenSSLCipherConfigurationParser;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/rewrite/ResolverImpl.class */
public class ResolverImpl extends Resolver {
    protected Request request;

    public ResolverImpl(Request request) {
        this.request = null;
        this.request = request;
    }

    @Override // org.apache.catalina.valves.rewrite.Resolver
    public String resolve(String key) {
        if (key.equals("HTTP_USER_AGENT")) {
            return this.request.getHeader("user-agent");
        }
        if (key.equals("HTTP_REFERER")) {
            return this.request.getHeader("referer");
        }
        if (key.equals("HTTP_COOKIE")) {
            return this.request.getHeader("cookie");
        }
        if (key.equals("HTTP_FORWARDED")) {
            return this.request.getHeader("forwarded");
        }
        if (key.equals("HTTP_HOST")) {
            return this.request.getServerName();
        }
        if (key.equals("HTTP_PROXY_CONNECTION")) {
            return this.request.getHeader("proxy-connection");
        }
        if (key.equals("HTTP_ACCEPT")) {
            return this.request.getHeader("accept");
        }
        if (key.equals("REMOTE_ADDR")) {
            return this.request.getRemoteAddr();
        }
        if (key.equals("REMOTE_HOST")) {
            return this.request.getRemoteHost();
        }
        if (key.equals("REMOTE_PORT")) {
            return String.valueOf(this.request.getRemotePort());
        }
        if (key.equals("REMOTE_USER")) {
            return this.request.getRemoteUser();
        }
        if (key.equals("REMOTE_IDENT")) {
            return this.request.getRemoteUser();
        }
        if (key.equals("REQUEST_METHOD")) {
            return this.request.getMethod();
        }
        if (key.equals("SCRIPT_FILENAME")) {
            return this.request.getServletContext().getRealPath(this.request.getServletPath());
        }
        if (key.equals("REQUEST_PATH")) {
            return this.request.getRequestPathMB().toString();
        }
        if (key.equals("CONTEXT_PATH")) {
            return this.request.getContextPath();
        }
        if (key.equals("SERVLET_PATH")) {
            return emptyStringIfNull(this.request.getServletPath());
        }
        if (key.equals("PATH_INFO")) {
            return emptyStringIfNull(this.request.getPathInfo());
        }
        if (key.equals("QUERY_STRING")) {
            return emptyStringIfNull(this.request.getQueryString());
        }
        if (key.equals("AUTH_TYPE")) {
            return this.request.getAuthType();
        }
        if (key.equals("DOCUMENT_ROOT")) {
            return this.request.getServletContext().getRealPath("/");
        }
        if (key.equals("SERVER_NAME")) {
            return this.request.getLocalName();
        }
        if (key.equals("SERVER_ADDR")) {
            return this.request.getLocalAddr();
        }
        if (key.equals("SERVER_PORT")) {
            return String.valueOf(this.request.getLocalPort());
        }
        if (key.equals("SERVER_PROTOCOL")) {
            return this.request.getProtocol();
        }
        if (key.equals("SERVER_SOFTWARE")) {
            return SSLUtilBase.DEFAULT_KEY_ALIAS;
        }
        if (key.equals("THE_REQUEST")) {
            return this.request.getMethod() + " " + this.request.getRequestURI() + " " + this.request.getProtocol();
        }
        if (key.equals("REQUEST_URI")) {
            return this.request.getRequestURI();
        }
        if (key.equals("REQUEST_FILENAME")) {
            return this.request.getPathTranslated();
        }
        if (key.equals("HTTPS")) {
            return this.request.isSecure() ? CustomBooleanEditor.VALUE_ON : CustomBooleanEditor.VALUE_OFF;
        }
        if (key.equals("TIME_YEAR")) {
            return String.valueOf(Calendar.getInstance().get(1));
        }
        if (key.equals("TIME_MON")) {
            return String.valueOf(Calendar.getInstance().get(2));
        }
        if (key.equals("TIME_DAY")) {
            return String.valueOf(Calendar.getInstance().get(5));
        }
        if (key.equals("TIME_HOUR")) {
            return String.valueOf(Calendar.getInstance().get(11));
        }
        if (key.equals("TIME_MIN")) {
            return String.valueOf(Calendar.getInstance().get(12));
        }
        if (key.equals("TIME_SEC")) {
            return String.valueOf(Calendar.getInstance().get(13));
        }
        if (key.equals("TIME_WDAY")) {
            return String.valueOf(Calendar.getInstance().get(7));
        }
        if (key.equals("TIME")) {
            return FastHttpDateFormat.getCurrentDate();
        }
        return null;
    }

    @Override // org.apache.catalina.valves.rewrite.Resolver
    public String resolveEnv(String key) {
        Object result = this.request.getAttribute(key);
        return result != null ? result.toString() : System.getProperty(key);
    }

    @Override // org.apache.catalina.valves.rewrite.Resolver
    public String resolveSsl(String key) {
        X509Certificate[] certificates;
        SSLSupport sslSupport = (SSLSupport) this.request.getAttribute("jakarta.servlet.request.ssl_session_mgr");
        try {
            if (key.equals("HTTPS")) {
                return String.valueOf(sslSupport != null);
            }
            if (key.equals("SSL_PROTOCOL")) {
                return sslSupport.getProtocol();
            }
            if (key.equals("SSL_SESSION_ID")) {
                return sslSupport.getSessionId();
            }
            if (!key.equals("SSL_SESSION_RESUMED") && !key.equals("SSL_SECURE_RENEG") && !key.equals("SSL_COMPRESS_METHOD") && !key.equals("SSL_TLS_SNI")) {
                if (key.equals("SSL_CIPHER")) {
                    return sslSupport.getCipherSuite();
                }
                if (key.equals("SSL_CIPHER_EXPORT")) {
                    String cipherSuite = sslSupport.getCipherSuite();
                    if (cipherSuite != null) {
                        Set<Cipher> cipherList = OpenSSLCipherConfigurationParser.parse(cipherSuite);
                        if (cipherList.size() == 1) {
                            Cipher cipher = cipherList.iterator().next();
                            if (cipher.getLevel().equals(EncryptionLevel.EXP40) || cipher.getLevel().equals(EncryptionLevel.EXP56)) {
                                return "true";
                            }
                            return "false";
                        }
                    }
                } else if (key.equals("SSL_CIPHER_ALGKEYSIZE")) {
                    String cipherSuite2 = sslSupport.getCipherSuite();
                    if (cipherSuite2 != null) {
                        Set<Cipher> cipherList2 = OpenSSLCipherConfigurationParser.parse(cipherSuite2);
                        if (cipherList2.size() == 1) {
                            return String.valueOf(cipherList2.iterator().next().getAlg_bits());
                        }
                    }
                } else {
                    if (key.equals("SSL_CIPHER_USEKEYSIZE")) {
                        Integer keySize = sslSupport.getKeySize();
                        if (keySize == null) {
                            return null;
                        }
                        return sslSupport.getKeySize().toString();
                    }
                    if (key.startsWith("SSL_CLIENT_")) {
                        X509Certificate[] certificates2 = sslSupport.getPeerCertificateChain();
                        if (certificates2 != null && certificates2.length > 0) {
                            String key2 = key.substring("SSL_CLIENT_".length());
                            String result = resolveSslCertificates(key2, certificates2);
                            if (result != null) {
                                return result;
                            }
                            if (key2.startsWith("SAN_OTHER_msUPN_")) {
                                key2.substring("SAN_OTHER_msUPN_".length());
                            } else if (!key2.equals("CERT_RFC4523_CEA") && key2.equals("VERIFY")) {
                            }
                        }
                    } else if (key.startsWith("SSL_SERVER_") && (certificates = sslSupport.getLocalCertificateChain()) != null && certificates.length > 0) {
                        String key3 = key.substring("SSL_SERVER_".length());
                        String result2 = resolveSslCertificates(key3, certificates);
                        if (result2 != null) {
                            return result2;
                        }
                        if (key3.startsWith("SAN_OTHER_dnsSRV_")) {
                            key3.substring("SAN_OTHER_dnsSRV_".length());
                        }
                    }
                }
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    private String resolveSslCertificates(String key, X509Certificate[] certificates) {
        if (key.equals("M_VERSION")) {
            return String.valueOf(certificates[0].getVersion());
        }
        if (key.equals("M_SERIAL")) {
            return certificates[0].getSerialNumber().toString();
        }
        if (key.equals("S_DN")) {
            return certificates[0].getSubjectX500Principal().toString();
        }
        if (key.startsWith("S_DN_")) {
            return resolveComponent(certificates[0].getSubjectX500Principal().getName(), key.substring("S_DN_".length()));
        }
        if (key.startsWith("SAN_Email_")) {
            return resolveAlternateName(certificates[0], 1, Integer.parseInt(key.substring("SAN_Email_".length())));
        }
        if (key.startsWith("SAN_DNS_")) {
            return resolveAlternateName(certificates[0], 2, Integer.parseInt(key.substring("SAN_DNS_".length())));
        }
        if (key.equals("I_DN")) {
            return certificates[0].getIssuerX500Principal().getName();
        }
        if (key.startsWith("I_DN_")) {
            return resolveComponent(certificates[0].getIssuerX500Principal().toString(), key.substring("I_DN_".length()));
        }
        if (key.equals("V_START")) {
            return String.valueOf(certificates[0].getNotBefore().getTime());
        }
        if (key.equals("V_END")) {
            return String.valueOf(certificates[0].getNotAfter().getTime());
        }
        if (key.equals("V_REMAIN")) {
            long remain = certificates[0].getNotAfter().getTime() - System.currentTimeMillis();
            if (remain < 0) {
                remain = 0;
            }
            return String.valueOf(TimeUnit.MILLISECONDS.toDays(remain));
        }
        if (key.equals("A_SIG")) {
            return certificates[0].getSigAlgName();
        }
        if (key.equals("A_KEY")) {
            return certificates[0].getPublicKey().getAlgorithm();
        }
        if (key.equals("CERT")) {
            try {
                return PEMFile.toPEM(certificates[0]);
            } catch (CertificateEncodingException e) {
                return null;
            }
        }
        if (key.startsWith("CERT_CHAIN_")) {
            try {
                return PEMFile.toPEM(certificates[Integer.parseInt(key.substring("CERT_CHAIN_".length()))]);
            } catch (ArrayIndexOutOfBoundsException | NumberFormatException | CertificateEncodingException e2) {
                return null;
            }
        }
        return null;
    }

    private String resolveComponent(String fullDN, String component) {
        HashMap<String, String> components = new HashMap<>();
        StringTokenizer tokenizer = new StringTokenizer(fullDN, ",");
        while (tokenizer.hasMoreElements()) {
            String token = tokenizer.nextToken().trim();
            int pos = token.indexOf(61);
            if (pos > 0 && pos + 1 < token.length()) {
                components.put(token.substring(0, pos), token.substring(pos + 1));
            }
        }
        return components.get(component);
    }

    private String resolveAlternateName(X509Certificate certificate, int type, int n) {
        try {
            Collection<List<?>> alternateNames = certificate.getSubjectAlternativeNames();
            if (alternateNames != null) {
                List<String> elements = new ArrayList<>();
                for (List<?> alternateName : alternateNames) {
                    Integer alternateNameType = (Integer) alternateName.get(0);
                    if (alternateNameType.intValue() == type) {
                        elements.add(String.valueOf(alternateName.get(1)));
                    }
                }
                if (elements.size() > n) {
                    return elements.get(n);
                }
                return null;
            }
            return null;
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException | CertificateParsingException e) {
            return null;
        }
    }

    @Override // org.apache.catalina.valves.rewrite.Resolver
    public String resolveHttp(String key) {
        String header = this.request.getHeader(key);
        if (header == null) {
            return "";
        }
        return header;
    }

    @Override // org.apache.catalina.valves.rewrite.Resolver
    public boolean resolveResource(int type, String name) {
        WebResourceRoot resources = this.request.getContext().getResources();
        WebResource resource = resources.getResource(name);
        if (!resource.exists()) {
            return false;
        }
        switch (type) {
            case 0:
                return resource.isDirectory();
            case 1:
                return resource.isFile();
            case 2:
                return resource.isFile() && resource.getContentLength() > 0;
            default:
                return false;
        }
    }

    private static String emptyStringIfNull(String value) {
        if (value == null) {
            return "";
        }
        return value;
    }

    @Override // org.apache.catalina.valves.rewrite.Resolver
    public Charset getUriCharset() {
        return this.request.getConnector().getURICharset();
    }
}
