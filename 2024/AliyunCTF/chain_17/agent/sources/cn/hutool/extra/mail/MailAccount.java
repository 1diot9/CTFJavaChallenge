package cn.hutool.extra.mail;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.Setting;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/mail/MailAccount.class */
public class MailAccount implements Serializable {
    private static final long serialVersionUID = -6937313421815719204L;
    private static final String MAIL_PROTOCOL = "mail.transport.protocol";
    private static final String SMTP_HOST = "mail.smtp.host";
    private static final String SMTP_PORT = "mail.smtp.port";
    private static final String SMTP_AUTH = "mail.smtp.auth";
    private static final String SMTP_TIMEOUT = "mail.smtp.timeout";
    private static final String SMTP_CONNECTION_TIMEOUT = "mail.smtp.connectiontimeout";
    private static final String SMTP_WRITE_TIMEOUT = "mail.smtp.writetimeout";
    private static final String STARTTLS_ENABLE = "mail.smtp.starttls.enable";
    private static final String SSL_ENABLE = "mail.smtp.ssl.enable";
    private static final String SSL_PROTOCOLS = "mail.smtp.ssl.protocols";
    private static final String SOCKET_FACTORY = "mail.smtp.socketFactory.class";
    private static final String SOCKET_FACTORY_FALLBACK = "mail.smtp.socketFactory.fallback";
    private static final String SOCKET_FACTORY_PORT = "smtp.socketFactory.port";
    private static final String SPLIT_LONG_PARAMS = "mail.mime.splitlongparameters";
    private static final String MAIL_DEBUG = "mail.debug";
    public static final String[] MAIL_SETTING_PATHS = {"config/mail.setting", "config/mailAccount.setting", "mail.setting"};
    private String host;
    private Integer port;
    private Boolean auth;
    private String user;
    private String pass;
    private String from;
    private boolean debug;
    private Charset charset;
    private boolean splitlongparameters;
    private boolean encodefilename;
    private boolean starttlsEnable;
    private Boolean sslEnable;
    private String sslProtocols;
    private String socketFactoryClass;
    private boolean socketFactoryFallback;
    private int socketFactoryPort;
    private long timeout;
    private long connectionTimeout;
    private long writeTimeout;
    private final Map<String, Object> customProperty;

    public MailAccount() {
        this.charset = CharsetUtil.CHARSET_UTF_8;
        this.splitlongparameters = false;
        this.encodefilename = true;
        this.starttlsEnable = false;
        this.socketFactoryClass = "javax.net.ssl.SSLSocketFactory";
        this.socketFactoryPort = 465;
        this.customProperty = new HashMap();
    }

    public MailAccount(String settingPath) {
        this(new Setting(settingPath));
    }

    public MailAccount(Setting setting) {
        this.charset = CharsetUtil.CHARSET_UTF_8;
        this.splitlongparameters = false;
        this.encodefilename = true;
        this.starttlsEnable = false;
        this.socketFactoryClass = "javax.net.ssl.SSLSocketFactory";
        this.socketFactoryPort = 465;
        this.customProperty = new HashMap();
        setting.toBean((Setting) this);
    }

    public String getHost() {
        return this.host;
    }

    public MailAccount setHost(String host) {
        this.host = host;
        return this;
    }

    public Integer getPort() {
        return this.port;
    }

    public MailAccount setPort(Integer port) {
        this.port = port;
        return this;
    }

    public Boolean isAuth() {
        return this.auth;
    }

    public MailAccount setAuth(boolean isAuth) {
        this.auth = Boolean.valueOf(isAuth);
        return this;
    }

    public String getUser() {
        return this.user;
    }

    public MailAccount setUser(String user) {
        this.user = user;
        return this;
    }

    public String getPass() {
        return this.pass;
    }

    public MailAccount setPass(String pass) {
        this.pass = pass;
        return this;
    }

    public String getFrom() {
        return this.from;
    }

    public MailAccount setFrom(String from) {
        this.from = from;
        return this;
    }

    public boolean isDebug() {
        return this.debug;
    }

    public MailAccount setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public Charset getCharset() {
        return this.charset;
    }

    public MailAccount setCharset(Charset charset) {
        this.charset = charset;
        return this;
    }

    public boolean isSplitlongparameters() {
        return this.splitlongparameters;
    }

    public void setSplitlongparameters(boolean splitlongparameters) {
        this.splitlongparameters = splitlongparameters;
    }

    public boolean isEncodefilename() {
        return this.encodefilename;
    }

    public void setEncodefilename(boolean encodefilename) {
        this.encodefilename = encodefilename;
    }

    public boolean isStarttlsEnable() {
        return this.starttlsEnable;
    }

    public MailAccount setStarttlsEnable(boolean startttlsEnable) {
        this.starttlsEnable = startttlsEnable;
        return this;
    }

    public Boolean isSslEnable() {
        return this.sslEnable;
    }

    public MailAccount setSslEnable(Boolean sslEnable) {
        this.sslEnable = sslEnable;
        return this;
    }

    public String getSslProtocols() {
        return this.sslProtocols;
    }

    public void setSslProtocols(String sslProtocols) {
        this.sslProtocols = sslProtocols;
    }

    public String getSocketFactoryClass() {
        return this.socketFactoryClass;
    }

    public MailAccount setSocketFactoryClass(String socketFactoryClass) {
        this.socketFactoryClass = socketFactoryClass;
        return this;
    }

    public boolean isSocketFactoryFallback() {
        return this.socketFactoryFallback;
    }

    public MailAccount setSocketFactoryFallback(boolean socketFactoryFallback) {
        this.socketFactoryFallback = socketFactoryFallback;
        return this;
    }

    public int getSocketFactoryPort() {
        return this.socketFactoryPort;
    }

    public MailAccount setSocketFactoryPort(int socketFactoryPort) {
        this.socketFactoryPort = socketFactoryPort;
        return this;
    }

    public MailAccount setTimeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    public MailAccount setConnectionTimeout(long connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public MailAccount setWriteTimeout(long writeTimeout) {
        this.writeTimeout = writeTimeout;
        return this;
    }

    public Map<String, Object> getCustomProperty() {
        return this.customProperty;
    }

    public MailAccount setCustomProperty(String key, Object value) {
        if (StrUtil.isNotBlank(key) && ObjectUtil.isNotNull(value)) {
            this.customProperty.put(key, value);
        }
        return this;
    }

    public Properties getSmtpProps() {
        System.setProperty(SPLIT_LONG_PARAMS, String.valueOf(this.splitlongparameters));
        Properties p = new Properties();
        p.put(MAIL_PROTOCOL, "smtp");
        p.put(SMTP_HOST, this.host);
        p.put(SMTP_PORT, String.valueOf(this.port));
        p.put(SMTP_AUTH, String.valueOf(this.auth));
        if (this.timeout > 0) {
            p.put(SMTP_TIMEOUT, String.valueOf(this.timeout));
        }
        if (this.connectionTimeout > 0) {
            p.put(SMTP_CONNECTION_TIMEOUT, String.valueOf(this.connectionTimeout));
        }
        if (this.writeTimeout > 0) {
            p.put(SMTP_WRITE_TIMEOUT, String.valueOf(this.writeTimeout));
        }
        p.put(MAIL_DEBUG, String.valueOf(this.debug));
        if (this.starttlsEnable) {
            p.put(STARTTLS_ENABLE, "true");
            if (null == this.sslEnable) {
                this.sslEnable = true;
            }
        }
        if (null != this.sslEnable && this.sslEnable.booleanValue()) {
            p.put(SSL_ENABLE, "true");
            p.put(SOCKET_FACTORY, this.socketFactoryClass);
            p.put(SOCKET_FACTORY_FALLBACK, String.valueOf(this.socketFactoryFallback));
            p.put(SOCKET_FACTORY_PORT, String.valueOf(this.socketFactoryPort));
            if (StrUtil.isNotBlank(this.sslProtocols)) {
                p.put(SSL_PROTOCOLS, this.sslProtocols);
            }
        }
        p.putAll(this.customProperty);
        return p;
    }

    public MailAccount defaultIfEmpty() {
        String fromAddress = InternalMailUtil.parseFirstAddress(this.from, this.charset).getAddress();
        if (StrUtil.isBlank(this.host)) {
            this.host = StrUtil.format("smtp.{}", StrUtil.subSuf(fromAddress, fromAddress.indexOf(64) + 1));
        }
        if (StrUtil.isBlank(this.user)) {
            this.user = fromAddress;
        }
        if (null == this.auth) {
            this.auth = Boolean.valueOf(false == StrUtil.isBlank(this.pass));
        }
        if (null == this.port) {
            this.port = Integer.valueOf((null == this.sslEnable || !this.sslEnable.booleanValue()) ? 25 : this.socketFactoryPort);
        }
        if (null == this.charset) {
            this.charset = CharsetUtil.CHARSET_UTF_8;
        }
        return this;
    }

    public String toString() {
        return "MailAccount [host=" + this.host + ", port=" + this.port + ", auth=" + this.auth + ", user=" + this.user + ", pass=" + (StrUtil.isEmpty(this.pass) ? "" : "******") + ", from=" + this.from + ", startttlsEnable=" + this.starttlsEnable + ", socketFactoryClass=" + this.socketFactoryClass + ", socketFactoryFallback=" + this.socketFactoryFallback + ", socketFactoryPort=" + this.socketFactoryPort + "]";
    }
}
