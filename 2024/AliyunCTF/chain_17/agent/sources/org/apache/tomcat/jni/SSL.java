package org.apache.tomcat.jni;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/jni/SSL.class */
public final class SSL {
    public static final int UNSET = -1;
    public static final int SSL_ALGO_UNKNOWN = 0;
    public static final int SSL_ALGO_RSA = 1;
    public static final int SSL_ALGO_DSA = 2;
    public static final int SSL_ALGO_ALL = 3;
    public static final int SSL_AIDX_RSA = 0;
    public static final int SSL_AIDX_DSA = 1;
    public static final int SSL_AIDX_ECC = 3;
    public static final int SSL_AIDX_MAX = 4;
    public static final int SSL_TMP_KEY_RSA_512 = 0;
    public static final int SSL_TMP_KEY_RSA_1024 = 1;
    public static final int SSL_TMP_KEY_RSA_2048 = 2;
    public static final int SSL_TMP_KEY_RSA_4096 = 3;
    public static final int SSL_TMP_KEY_DH_512 = 4;
    public static final int SSL_TMP_KEY_DH_1024 = 5;
    public static final int SSL_TMP_KEY_DH_2048 = 6;
    public static final int SSL_TMP_KEY_DH_4096 = 7;
    public static final int SSL_TMP_KEY_MAX = 8;
    public static final int SSL_OPT_NONE = 0;
    public static final int SSL_OPT_RELSET = 1;
    public static final int SSL_OPT_STDENVVARS = 2;
    public static final int SSL_OPT_EXPORTCERTDATA = 8;
    public static final int SSL_OPT_FAKEBASICAUTH = 16;
    public static final int SSL_OPT_STRICTREQUIRE = 32;
    public static final int SSL_OPT_OPTRENEGOTIATE = 64;
    public static final int SSL_OPT_ALL = 122;
    public static final int SSL_PROTOCOL_NONE = 0;
    public static final int SSL_PROTOCOL_SSLV2 = 1;
    public static final int SSL_PROTOCOL_SSLV3 = 2;
    public static final int SSL_PROTOCOL_TLSV1 = 4;
    public static final int SSL_PROTOCOL_TLSV1_1 = 8;
    public static final int SSL_PROTOCOL_TLSV1_2 = 16;
    public static final int SSL_PROTOCOL_TLSV1_3 = 32;
    public static final int SSL_PROTOCOL_ALL;
    public static final int SSL_CVERIFY_UNSET = -1;
    public static final int SSL_CVERIFY_NONE = 0;
    public static final int SSL_CVERIFY_OPTIONAL = 1;
    public static final int SSL_CVERIFY_REQUIRE = 2;
    public static final int SSL_CVERIFY_OPTIONAL_NO_CA = 3;
    public static final int SSL_VERIFY_NONE = 0;
    public static final int SSL_VERIFY_PEER = 1;
    public static final int SSL_VERIFY_FAIL_IF_NO_PEER_CERT = 2;
    public static final int SSL_VERIFY_CLIENT_ONCE = 4;
    public static final int SSL_VERIFY_PEER_STRICT = 3;
    public static final int SSL_OP_MICROSOFT_SESS_ID_BUG = 1;
    public static final int SSL_OP_NETSCAPE_CHALLENGE_BUG = 2;
    public static final int SSL_OP_NETSCAPE_REUSE_CIPHER_CHANGE_BUG = 8;
    public static final int SSL_OP_SSLREF2_REUSE_CERT_TYPE_BUG = 16;
    public static final int SSL_OP_MICROSOFT_BIG_SSLV3_BUFFER = 32;
    public static final int SSL_OP_MSIE_SSLV2_RSA_PADDING = 64;
    public static final int SSL_OP_SSLEAY_080_CLIENT_DH_BUG = 128;
    public static final int SSL_OP_TLS_D5_BUG = 256;
    public static final int SSL_OP_TLS_BLOCK_PADDING_BUG = 512;
    public static final int SSL_OP_DONT_INSERT_EMPTY_FRAGMENTS = 2048;
    public static final int SSL_OP_ALL = 4095;
    public static final int SSL_OP_NO_SESSION_RESUMPTION_ON_RENEGOTIATION = 65536;
    public static final int SSL_OP_NO_COMPRESSION = 131072;
    public static final int SSL_OP_ALLOW_UNSAFE_LEGACY_RENEGOTIATION = 262144;
    public static final int SSL_OP_SINGLE_ECDH_USE = 524288;
    public static final int SSL_OP_SINGLE_DH_USE = 1048576;
    public static final int SSL_OP_EPHEMERAL_RSA = 2097152;
    public static final int SSL_OP_CIPHER_SERVER_PREFERENCE = 4194304;
    public static final int SSL_OP_TLS_ROLLBACK_BUG = 8388608;
    public static final int SSL_OP_NO_SSLv2 = 16777216;
    public static final int SSL_OP_NO_SSLv3 = 33554432;
    public static final int SSL_OP_NO_TLSv1 = 67108864;
    public static final int SSL_OP_NO_TLSv1_2 = 134217728;
    public static final int SSL_OP_NO_TLSv1_1 = 268435456;
    public static final int SSL_OP_NO_TICKET = 16384;
    public static final int SSL_OP_NETSCAPE_CA_DN_BUG = 536870912;
    public static final int SSL_OP_NETSCAPE_DEMO_CIPHER_CHANGE_BUG = 1073741824;
    public static final int SSL_CRT_FORMAT_UNDEF = 0;
    public static final int SSL_CRT_FORMAT_ASN1 = 1;
    public static final int SSL_CRT_FORMAT_TEXT = 2;
    public static final int SSL_CRT_FORMAT_PEM = 3;
    public static final int SSL_CRT_FORMAT_NETSCAPE = 4;
    public static final int SSL_CRT_FORMAT_PKCS12 = 5;
    public static final int SSL_CRT_FORMAT_SMIME = 6;
    public static final int SSL_CRT_FORMAT_ENGINE = 7;
    public static final int SSL_MODE_CLIENT = 0;
    public static final int SSL_MODE_SERVER = 1;
    public static final int SSL_MODE_COMBINED = 2;
    public static final int SSL_CONF_FLAG_CMDLINE = 1;
    public static final int SSL_CONF_FLAG_FILE = 2;
    public static final int SSL_CONF_FLAG_CLIENT = 4;
    public static final int SSL_CONF_FLAG_SERVER = 8;
    public static final int SSL_CONF_FLAG_SHOW_ERRORS = 16;
    public static final int SSL_CONF_FLAG_CERTIFICATE = 32;
    public static final int SSL_CONF_TYPE_UNKNOWN = 0;
    public static final int SSL_CONF_TYPE_STRING = 1;
    public static final int SSL_CONF_TYPE_FILE = 2;
    public static final int SSL_CONF_TYPE_DIR = 3;
    public static final int SSL_SHUTDOWN_TYPE_UNSET = 0;
    public static final int SSL_SHUTDOWN_TYPE_STANDARD = 1;
    public static final int SSL_SHUTDOWN_TYPE_UNCLEAN = 2;
    public static final int SSL_SHUTDOWN_TYPE_ACCURATE = 3;
    public static final int SSL_INFO_SESSION_ID = 1;
    public static final int SSL_INFO_CIPHER = 2;
    public static final int SSL_INFO_CIPHER_USEKEYSIZE = 3;
    public static final int SSL_INFO_CIPHER_ALGKEYSIZE = 4;
    public static final int SSL_INFO_CIPHER_VERSION = 5;
    public static final int SSL_INFO_CIPHER_DESCRIPTION = 6;
    public static final int SSL_INFO_PROTOCOL = 7;
    public static final int SSL_INFO_CLIENT_S_DN = 16;
    public static final int SSL_INFO_CLIENT_I_DN = 32;
    public static final int SSL_INFO_SERVER_S_DN = 64;
    public static final int SSL_INFO_SERVER_I_DN = 128;
    public static final int SSL_INFO_DN_COUNTRYNAME = 1;
    public static final int SSL_INFO_DN_STATEORPROVINCENAME = 2;
    public static final int SSL_INFO_DN_LOCALITYNAME = 3;
    public static final int SSL_INFO_DN_ORGANIZATIONNAME = 4;
    public static final int SSL_INFO_DN_ORGANIZATIONALUNITNAME = 5;
    public static final int SSL_INFO_DN_COMMONNAME = 6;
    public static final int SSL_INFO_DN_TITLE = 7;
    public static final int SSL_INFO_DN_INITIALS = 8;
    public static final int SSL_INFO_DN_GIVENNAME = 9;
    public static final int SSL_INFO_DN_SURNAME = 10;
    public static final int SSL_INFO_DN_DESCRIPTION = 11;
    public static final int SSL_INFO_DN_UNIQUEIDENTIFIER = 12;
    public static final int SSL_INFO_DN_EMAILADDRESS = 13;
    public static final int SSL_INFO_CLIENT_M_VERSION = 257;
    public static final int SSL_INFO_CLIENT_M_SERIAL = 258;
    public static final int SSL_INFO_CLIENT_V_START = 259;
    public static final int SSL_INFO_CLIENT_V_END = 260;
    public static final int SSL_INFO_CLIENT_A_SIG = 261;
    public static final int SSL_INFO_CLIENT_A_KEY = 262;
    public static final int SSL_INFO_CLIENT_CERT = 263;
    public static final int SSL_INFO_CLIENT_V_REMAIN = 264;
    public static final int SSL_INFO_SERVER_M_VERSION = 513;
    public static final int SSL_INFO_SERVER_M_SERIAL = 514;
    public static final int SSL_INFO_SERVER_V_START = 515;
    public static final int SSL_INFO_SERVER_V_END = 516;
    public static final int SSL_INFO_SERVER_A_SIG = 517;
    public static final int SSL_INFO_SERVER_A_KEY = 518;
    public static final int SSL_INFO_SERVER_CERT = 519;
    public static final int SSL_INFO_CLIENT_CERT_CHAIN = 1024;
    public static final long SSL_SESS_CACHE_OFF = 0;
    public static final long SSL_SESS_CACHE_SERVER = 2;
    public static final int SSL_SELECTOR_FAILURE_NO_ADVERTISE = 0;
    public static final int SSL_SELECTOR_FAILURE_CHOOSE_MY_LAST_PROTOCOL = 1;
    public static final int SSL_SENT_SHUTDOWN = 1;
    public static final int SSL_RECEIVED_SHUTDOWN = 2;
    public static final int SSL_ERROR_NONE = 0;
    public static final int SSL_ERROR_SSL = 1;
    public static final int SSL_ERROR_WANT_READ = 2;
    public static final int SSL_ERROR_WANT_WRITE = 3;
    public static final int SSL_ERROR_WANT_X509_LOOKUP = 4;
    public static final int SSL_ERROR_SYSCALL = 5;
    public static final int SSL_ERROR_ZERO_RETURN = 6;
    public static final int SSL_ERROR_WANT_CONNECT = 7;
    public static final int SSL_ERROR_WANT_ACCEPT = 8;

    public static native int version();

    public static native String versionString();

    public static native int initialize(String str);

    public static native int fipsModeGet() throws Exception;

    public static native int fipsModeSet(int i) throws Exception;

    public static native void randSet(String str);

    public static native int getHandshakeCount(long j);

    public static native long newSSL(long j, boolean z);

    public static native int pendingWrittenBytesInBIO(long j);

    public static native int pendingReadableBytesInSSL(long j);

    public static native int writeToBIO(long j, long j2, int i);

    public static native int readFromBIO(long j, long j2, int i);

    public static native int writeToSSL(long j, long j2, int i);

    public static native int readFromSSL(long j, long j2, int i);

    public static native int getShutdown(long j);

    public static native void freeSSL(long j);

    public static native long makeNetworkBIO(long j);

    public static native void freeBIO(long j);

    public static native int shutdownSSL(long j);

    public static native int getLastErrorNumber();

    public static native String getCipherForSSL(long j);

    public static native String getVersion(long j);

    public static native int doHandshake(long j);

    public static native int renegotiate(long j);

    public static native int renegotiatePending(long j);

    public static native int verifyClientPostHandshake(long j);

    public static native int getPostHandshakeAuthInProgress(long j);

    public static native int isInInit(long j);

    public static native String getAlpnSelected(long j);

    public static native byte[][] getPeerCertChain(long j);

    public static native byte[] getPeerCertificate(long j);

    public static native String getErrorString(long j);

    public static native long getTime(long j);

    public static native void setVerify(long j, int i, int i2);

    public static native void setOptions(long j, int i);

    public static native int getOptions(long j);

    public static native String[] getCiphers(long j);

    public static native boolean setCipherSuites(long j, String str) throws Exception;

    public static native byte[] getSessionId(long j);

    static {
        if (version() >= 269488143) {
            SSL_PROTOCOL_ALL = 60;
        } else {
            SSL_PROTOCOL_ALL = 28;
        }
    }
}
