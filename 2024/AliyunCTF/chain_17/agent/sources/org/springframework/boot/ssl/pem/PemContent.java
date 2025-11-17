package org.springframework.boot.ssl.pem;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StreamUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/ssl/pem/PemContent.class */
public final class PemContent {
    private static final Pattern PEM_HEADER = Pattern.compile("-+BEGIN\\s+[^-]*-+", 2);
    private static final Pattern PEM_FOOTER = Pattern.compile("-+END\\s+[^-]*-+", 2);
    private final String text;

    private PemContent(String text) {
        this.text = text;
    }

    public List<X509Certificate> getCertificates() {
        return PemCertificateParser.parse(this.text);
    }

    public PrivateKey getPrivateKey() {
        return getPrivateKey(null);
    }

    public PrivateKey getPrivateKey(String password) {
        return PemPrivateKeyParser.parse(this.text, password);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return Objects.equals(this.text, ((PemContent) obj).text);
    }

    public int hashCode() {
        return Objects.hash(this.text);
    }

    public String toString() {
        return this.text;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static PemContent load(String content) throws IOException {
        if (content == null) {
            return null;
        }
        if (isPresentInText(content)) {
            return new PemContent(content);
        }
        try {
            return load(ResourceUtils.getURL(content));
        } catch (IOException | UncheckedIOException ex) {
            throw new IOException("Error reading certificate or key from file '%s'".formatted(content), ex);
        }
    }

    public static PemContent load(Path path) throws IOException {
        Assert.notNull(path, "Path must not be null");
        InputStream in = Files.newInputStream(path, StandardOpenOption.READ);
        try {
            PemContent load = load(in);
            if (in != null) {
                in.close();
            }
            return load;
        } catch (Throwable th) {
            if (in != null) {
                try {
                    in.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    private static PemContent load(URL url) throws IOException {
        Assert.notNull(url, "Url must not be null");
        InputStream in = url.openStream();
        try {
            PemContent load = load(in);
            if (in != null) {
                in.close();
            }
            return load;
        } catch (Throwable th) {
            if (in != null) {
                try {
                    in.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    private static PemContent load(InputStream in) throws IOException {
        return of(StreamUtils.copyToString(in, StandardCharsets.UTF_8));
    }

    public static PemContent of(String text) {
        if (text != null) {
            return new PemContent(text);
        }
        return null;
    }

    public static boolean isPresentInText(String text) {
        return text != null && PEM_HEADER.matcher(text).find() && PEM_FOOTER.matcher(text).find();
    }
}
