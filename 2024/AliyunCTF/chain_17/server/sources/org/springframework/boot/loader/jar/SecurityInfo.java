package org.springframework.boot.loader.jar;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.security.CodeSigner;
import java.security.cert.Certificate;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import org.springframework.boot.loader.zip.ZipContent;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:org/springframework/boot/loader/jar/SecurityInfo.class */
public final class SecurityInfo {
    static final SecurityInfo NONE = new SecurityInfo(null, null);
    private final Certificate[][] certificateLookups;
    private final CodeSigner[][] codeSignerLookups;

    private SecurityInfo(Certificate[][] entryCertificates, CodeSigner[][] entryCodeSigners) {
        this.certificateLookups = entryCertificates;
        this.codeSignerLookups = entryCodeSigners;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Certificate[] getCertificates(ZipContent.Entry contentEntry) {
        if (this.certificateLookups != null) {
            return (Certificate[]) clone(this.certificateLookups[contentEntry.getLookupIndex()]);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CodeSigner[] getCodeSigners(ZipContent.Entry contentEntry) {
        if (this.codeSignerLookups != null) {
            return (CodeSigner[]) clone(this.codeSignerLookups[contentEntry.getLookupIndex()]);
        }
        return null;
    }

    private <T> T[] clone(T[] tArr) {
        if (tArr != null) {
            return (T[]) ((Object[]) tArr.clone());
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static SecurityInfo get(ZipContent content) {
        if (!content.hasJarSignatureFile()) {
            return NONE;
        }
        try {
            return load(content);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    /* JADX WARN: Type inference failed for: r0v4, types: [java.security.cert.Certificate[], java.security.cert.Certificate[][]] */
    /* JADX WARN: Type inference failed for: r0v6, types: [java.security.CodeSigner[], java.security.CodeSigner[][]] */
    private static SecurityInfo load(ZipContent content) throws IOException {
        ZipContent.Entry contentEntry;
        int size = content.size();
        boolean hasSecurityInfo = false;
        ?? r0 = new Certificate[size];
        ?? r02 = new CodeSigner[size];
        JarInputStream in = new JarInputStream(content.openRawZipData().asInputStream());
        try {
            for (JarEntry jarEntry = in.getNextJarEntry(); jarEntry != null; jarEntry = in.getNextJarEntry()) {
                in.closeEntry();
                Certificate[] certificates = jarEntry.getCertificates();
                CodeSigner[] codeSigners = jarEntry.getCodeSigners();
                if ((certificates != null || codeSigners != null) && (contentEntry = content.getEntry(jarEntry.getName())) != null) {
                    hasSecurityInfo = true;
                    r0[contentEntry.getLookupIndex()] = certificates;
                    r02[contentEntry.getLookupIndex()] = codeSigners;
                }
            }
            SecurityInfo securityInfo = !hasSecurityInfo ? NONE : new SecurityInfo(r0, r02);
            in.close();
            return securityInfo;
        } catch (Throwable th) {
            try {
                in.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }
}
