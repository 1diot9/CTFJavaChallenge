package org.springframework.boot.ssl;

import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.KeyManagerFactorySpi;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedKeyManager;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/ssl/AliasKeyManagerFactory.class */
final class AliasKeyManagerFactory extends KeyManagerFactory {
    /* JADX INFO: Access modifiers changed from: package-private */
    public AliasKeyManagerFactory(KeyManagerFactory delegate, String alias, String algorithm) {
        super(new AliasKeyManagerFactorySpi(delegate, alias), delegate.getProvider(), algorithm);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/ssl/AliasKeyManagerFactory$AliasKeyManagerFactorySpi.class */
    private static final class AliasKeyManagerFactorySpi extends KeyManagerFactorySpi {
        private final KeyManagerFactory delegate;
        private final String alias;

        private AliasKeyManagerFactorySpi(KeyManagerFactory delegate, String alias) {
            this.delegate = delegate;
            this.alias = alias;
        }

        @Override // javax.net.ssl.KeyManagerFactorySpi
        protected void engineInit(KeyStore keyStore, char[] chars) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
            this.delegate.init(keyStore, chars);
        }

        @Override // javax.net.ssl.KeyManagerFactorySpi
        protected void engineInit(ManagerFactoryParameters managerFactoryParameters) throws InvalidAlgorithmParameterException {
            throw new InvalidAlgorithmParameterException("Unsupported ManagerFactoryParameters");
        }

        @Override // javax.net.ssl.KeyManagerFactorySpi
        protected KeyManager[] engineGetKeyManagers() {
            Stream stream = Arrays.stream(this.delegate.getKeyManagers());
            Class<X509ExtendedKeyManager> cls = X509ExtendedKeyManager.class;
            Objects.requireNonNull(X509ExtendedKeyManager.class);
            Stream filter = stream.filter((v1) -> {
                return r1.isInstance(v1);
            });
            Class<X509ExtendedKeyManager> cls2 = X509ExtendedKeyManager.class;
            Objects.requireNonNull(X509ExtendedKeyManager.class);
            return (KeyManager[]) filter.map((v1) -> {
                return r1.cast(v1);
            }).map(this::wrap).toArray(x$0 -> {
                return new KeyManager[x$0];
            });
        }

        private AliasX509ExtendedKeyManager wrap(X509ExtendedKeyManager keyManager) {
            return new AliasX509ExtendedKeyManager(keyManager, this.alias);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/ssl/AliasKeyManagerFactory$AliasX509ExtendedKeyManager.class */
    static final class AliasX509ExtendedKeyManager extends X509ExtendedKeyManager {
        private final X509ExtendedKeyManager delegate;
        private final String alias;

        private AliasX509ExtendedKeyManager(X509ExtendedKeyManager keyManager, String alias) {
            this.delegate = keyManager;
            this.alias = alias;
        }

        @Override // javax.net.ssl.X509ExtendedKeyManager
        public String chooseEngineClientAlias(String[] strings, Principal[] principals, SSLEngine sslEngine) {
            return this.delegate.chooseEngineClientAlias(strings, principals, sslEngine);
        }

        @Override // javax.net.ssl.X509ExtendedKeyManager
        public String chooseEngineServerAlias(String s, Principal[] principals, SSLEngine sslEngine) {
            return this.alias;
        }

        @Override // javax.net.ssl.X509KeyManager
        public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
            return this.delegate.chooseClientAlias(keyType, issuers, socket);
        }

        @Override // javax.net.ssl.X509KeyManager
        public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
            return this.delegate.chooseServerAlias(keyType, issuers, socket);
        }

        @Override // javax.net.ssl.X509KeyManager
        public X509Certificate[] getCertificateChain(String alias) {
            return this.delegate.getCertificateChain(alias);
        }

        @Override // javax.net.ssl.X509KeyManager
        public String[] getClientAliases(String keyType, Principal[] issuers) {
            return this.delegate.getClientAliases(keyType, issuers);
        }

        @Override // javax.net.ssl.X509KeyManager
        public PrivateKey getPrivateKey(String alias) {
            return this.delegate.getPrivateKey(alias);
        }

        @Override // javax.net.ssl.X509KeyManager
        public String[] getServerAliases(String keyType, Principal[] issuers) {
            return this.delegate.getServerAliases(keyType, issuers);
        }
    }
}
