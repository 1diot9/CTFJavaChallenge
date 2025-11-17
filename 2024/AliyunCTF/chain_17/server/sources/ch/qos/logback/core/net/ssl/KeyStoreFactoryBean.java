package ch.qos.logback.core.net.ssl;

import ch.qos.logback.core.util.LocationUtil;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/net/ssl/KeyStoreFactoryBean.class */
public class KeyStoreFactoryBean {
    private String location;
    private String provider;
    private String type;
    private String password;

    public KeyStore createKeyStore() throws NoSuchProviderException, NoSuchAlgorithmException, KeyStoreException {
        if (getLocation() == null) {
            throw new IllegalArgumentException("location is required");
        }
        InputStream inputStream = null;
        try {
            try {
                try {
                    try {
                        URL url = LocationUtil.urlForResource(getLocation());
                        inputStream = url.openStream();
                        KeyStore keyStore = newKeyStore();
                        keyStore.load(inputStream, getPassword().toCharArray());
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (IOException ex) {
                                ex.printStackTrace(System.err);
                            }
                        }
                        return keyStore;
                    } catch (FileNotFoundException e) {
                        throw new KeyStoreException(getLocation() + ": file not found");
                    } catch (Exception ex2) {
                        throw new KeyStoreException(getLocation() + ": " + ex2.getMessage(), ex2);
                    }
                } catch (NoSuchAlgorithmException e2) {
                    throw new NoSuchAlgorithmException("no such keystore type: " + getType());
                }
            } catch (NoSuchProviderException e3) {
                throw new NoSuchProviderException("no such keystore provider: " + getProvider());
            }
        } catch (Throwable th) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex3) {
                    ex3.printStackTrace(System.err);
                    throw th;
                }
            }
            throw th;
        }
    }

    private KeyStore newKeyStore() throws NoSuchAlgorithmException, NoSuchProviderException, KeyStoreException {
        return getProvider() != null ? KeyStore.getInstance(getType(), getProvider()) : KeyStore.getInstance(getType());
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        if (this.type == null) {
            return SSL.DEFAULT_KEYSTORE_TYPE;
        }
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProvider() {
        return this.provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getPassword() {
        if (this.password == null) {
            return "changeit";
        }
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
