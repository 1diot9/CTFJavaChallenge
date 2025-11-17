package org.apache.tomcat.util.net.openssl;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Set;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/openssl/OpenSSLX509Certificate.class */
public final class OpenSSLX509Certificate extends X509Certificate {
    private final byte[] bytes;
    private X509Certificate wrapped;

    /* JADX INFO: Access modifiers changed from: package-private */
    public OpenSSLX509Certificate(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override // java.security.cert.X509Certificate
    public void checkValidity() throws CertificateExpiredException, CertificateNotYetValidException {
        unwrap().checkValidity();
    }

    @Override // java.security.cert.X509Certificate
    public void checkValidity(Date date) throws CertificateExpiredException, CertificateNotYetValidException {
        unwrap().checkValidity(date);
    }

    @Override // java.security.cert.X509Certificate
    public int getVersion() {
        return unwrap().getVersion();
    }

    @Override // java.security.cert.X509Certificate
    public BigInteger getSerialNumber() {
        return unwrap().getSerialNumber();
    }

    @Override // java.security.cert.X509Certificate
    @Deprecated
    public Principal getIssuerDN() {
        return unwrap().getIssuerDN();
    }

    @Override // java.security.cert.X509Certificate
    @Deprecated
    public Principal getSubjectDN() {
        return unwrap().getSubjectDN();
    }

    @Override // java.security.cert.X509Certificate
    public Date getNotBefore() {
        return unwrap().getNotBefore();
    }

    @Override // java.security.cert.X509Certificate
    public Date getNotAfter() {
        return unwrap().getNotAfter();
    }

    @Override // java.security.cert.X509Certificate
    public byte[] getTBSCertificate() throws CertificateEncodingException {
        return unwrap().getTBSCertificate();
    }

    @Override // java.security.cert.X509Certificate
    public byte[] getSignature() {
        return unwrap().getSignature();
    }

    @Override // java.security.cert.X509Certificate
    public String getSigAlgName() {
        return unwrap().getSigAlgName();
    }

    @Override // java.security.cert.X509Certificate
    public String getSigAlgOID() {
        return unwrap().getSigAlgOID();
    }

    @Override // java.security.cert.X509Certificate
    public byte[] getSigAlgParams() {
        return unwrap().getSigAlgParams();
    }

    @Override // java.security.cert.X509Certificate
    public boolean[] getIssuerUniqueID() {
        return unwrap().getIssuerUniqueID();
    }

    @Override // java.security.cert.X509Certificate
    public boolean[] getSubjectUniqueID() {
        return unwrap().getSubjectUniqueID();
    }

    @Override // java.security.cert.X509Certificate
    public boolean[] getKeyUsage() {
        return unwrap().getKeyUsage();
    }

    @Override // java.security.cert.X509Certificate
    public int getBasicConstraints() {
        return unwrap().getBasicConstraints();
    }

    @Override // java.security.cert.Certificate
    public byte[] getEncoded() {
        return (byte[]) this.bytes.clone();
    }

    @Override // java.security.cert.Certificate
    public void verify(PublicKey key) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        unwrap().verify(key);
    }

    @Override // java.security.cert.Certificate
    public void verify(PublicKey key, String sigProvider) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        unwrap().verify(key, sigProvider);
    }

    @Override // java.security.cert.Certificate
    public String toString() {
        return unwrap().toString();
    }

    @Override // java.security.cert.Certificate
    public PublicKey getPublicKey() {
        return unwrap().getPublicKey();
    }

    @Override // java.security.cert.X509Extension
    public boolean hasUnsupportedCriticalExtension() {
        return unwrap().hasUnsupportedCriticalExtension();
    }

    @Override // java.security.cert.X509Extension
    public Set<String> getCriticalExtensionOIDs() {
        return unwrap().getCriticalExtensionOIDs();
    }

    @Override // java.security.cert.X509Extension
    public Set<String> getNonCriticalExtensionOIDs() {
        return unwrap().getNonCriticalExtensionOIDs();
    }

    @Override // java.security.cert.X509Extension
    public byte[] getExtensionValue(String oid) {
        return unwrap().getExtensionValue(oid);
    }

    private X509Certificate unwrap() {
        X509Certificate wrapped = this.wrapped;
        if (wrapped == null) {
            try {
                X509Certificate x509Certificate = (X509Certificate) OpenSSLContext.X509_CERT_FACTORY.generateCertificate(new ByteArrayInputStream(this.bytes));
                this.wrapped = x509Certificate;
                wrapped = x509Certificate;
            } catch (CertificateException e) {
                throw new IllegalStateException(e);
            }
        }
        return wrapped;
    }
}
