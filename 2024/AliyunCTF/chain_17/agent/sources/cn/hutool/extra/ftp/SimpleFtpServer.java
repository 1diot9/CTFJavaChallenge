package cn.hutool.extra.ftp;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.net.NetUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.ftpserver.ConnectionConfig;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.Ftplet;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.ssl.SslConfiguration;
import org.apache.ftpserver.ssl.SslConfigurationFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/ftp/SimpleFtpServer.class */
public class SimpleFtpServer {
    FtpServerFactory serverFactory = new FtpServerFactory();
    ListenerFactory listenerFactory = new ListenerFactory();

    public static SimpleFtpServer create() {
        return new SimpleFtpServer();
    }

    public FtpServerFactory getServerFactory() {
        return this.serverFactory;
    }

    public SimpleFtpServer setConnectionConfig(ConnectionConfig connectionConfig) {
        this.serverFactory.setConnectionConfig(connectionConfig);
        return this;
    }

    public ListenerFactory getListenerFactory() {
        return this.listenerFactory;
    }

    public SimpleFtpServer setPort(int port) {
        Assert.isTrue(NetUtil.isValidPort(port), "Invalid port!", new Object[0]);
        this.listenerFactory.setPort(port);
        return this;
    }

    public UserManager getUserManager() {
        return this.serverFactory.getUserManager();
    }

    public SimpleFtpServer addUser(User user) {
        try {
            getUserManager().save(user);
            return this;
        } catch (org.apache.ftpserver.ftplet.FtpException e) {
            throw new FtpException((Throwable) e);
        }
    }

    public SimpleFtpServer addAnonymous(String homePath) {
        BaseUser user = new BaseUser();
        user.setName("anonymous");
        user.setHomeDirectory(homePath);
        List<Authority> authorities = new ArrayList<>();
        authorities.add(new WritePermission());
        user.setAuthorities(authorities);
        return addUser(user);
    }

    public SimpleFtpServer delUser(String userName) {
        try {
            getUserManager().delete(userName);
            return this;
        } catch (org.apache.ftpserver.ftplet.FtpException e) {
            throw new FtpException((Throwable) e);
        }
    }

    public SimpleFtpServer setSsl(SslConfiguration ssl) {
        this.listenerFactory.setSslConfiguration(ssl);
        this.listenerFactory.setImplicitSsl(true);
        return this;
    }

    public SimpleFtpServer setSsl(File keystoreFile, String password) {
        SslConfigurationFactory sslFactory = new SslConfigurationFactory();
        sslFactory.setKeystoreFile(keystoreFile);
        sslFactory.setKeystorePassword(password);
        return setSsl(sslFactory.createSslConfiguration());
    }

    public SimpleFtpServer setUserManager(UserManager userManager) {
        this.serverFactory.setUserManager(userManager);
        return this;
    }

    public SimpleFtpServer setUsersConfig(File propertiesFile) {
        PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
        userManagerFactory.setFile(propertiesFile);
        return setUserManager(userManagerFactory.createUserManager());
    }

    public SimpleFtpServer addFtplet(String name, Ftplet ftplet) {
        this.serverFactory.getFtplets().put(name, ftplet);
        return this;
    }

    public void start() {
        this.serverFactory.addListener("default", this.listenerFactory.createListener());
        try {
            this.serverFactory.createServer().start();
        } catch (org.apache.ftpserver.ftplet.FtpException e) {
            throw new FtpException((Throwable) e);
        }
    }
}
