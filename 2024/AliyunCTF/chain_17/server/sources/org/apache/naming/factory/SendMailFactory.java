package org.apache.naming.factory;

import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimePartDataSource;
import java.security.AccessController;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/naming/factory/SendMailFactory.class */
public class SendMailFactory implements ObjectFactory {
    protected static final String DataSourceClassName = "jakarta.mail.internet.MimePartDataSource";

    public Object getObjectInstance(Object refObj, Name name, Context ctx, Hashtable<?, ?> env) throws Exception {
        Reference ref = (Reference) refObj;
        if (ref.getClassName().equals(DataSourceClassName)) {
            return AccessController.doPrivileged(() -> {
                Properties props = new Properties();
                Enumeration<RefAddr> list = ref.getAll();
                props.put("mail.transport.protocol", "smtp");
                while (list.hasMoreElements()) {
                    RefAddr refaddr = list.nextElement();
                    props.put(refaddr.getType(), refaddr.getContent());
                }
                MimeMessage message = new MimeMessage(Session.getInstance(props));
                try {
                    RefAddr fromAddr = ref.get("mail.from");
                    String from = null;
                    if (fromAddr != null) {
                        from = (String) fromAddr.getContent();
                    }
                    if (from != null) {
                        message.setFrom(new InternetAddress(from));
                    }
                    message.setSubject("");
                } catch (Exception e) {
                }
                MimePartDataSource mds = new MimePartDataSource(message);
                return mds;
            });
        }
        return null;
    }
}
