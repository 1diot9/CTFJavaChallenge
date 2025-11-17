package org.apache.naming.factory;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import java.security.AccessController;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/naming/factory/MailSessionFactory.class */
public class MailSessionFactory implements ObjectFactory {
    protected static final String factoryType = "jakarta.mail.Session";

    public Object getObjectInstance(Object refObj, Name name, Context context, Hashtable<?, ?> env) throws Exception {
        Reference ref = (Reference) refObj;
        if (!ref.getClassName().equals(factoryType)) {
            return null;
        }
        return AccessController.doPrivileged(() -> {
            Properties props = new Properties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.host", "localhost");
            String password = null;
            Enumeration<RefAddr> attrs = ref.getAll();
            while (attrs.hasMoreElements()) {
                RefAddr attr = attrs.nextElement();
                if (!Constants.FACTORY.equals(attr.getType())) {
                    if ("password".equals(attr.getType())) {
                        password = (String) attr.getContent();
                    } else {
                        props.put(attr.getType(), attr.getContent());
                    }
                }
            }
            Authenticator auth = null;
            if (password != null) {
                String user = props.getProperty("mail.smtp.user");
                if (user == null) {
                    user = props.getProperty("mail.user");
                }
                if (user != null) {
                    final PasswordAuthentication pa = new PasswordAuthentication(user, password);
                    auth = new Authenticator() { // from class: org.apache.naming.factory.MailSessionFactory.1
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return pa;
                        }
                    };
                }
            }
            Session session = Session.getInstance(props, auth);
            return session;
        });
    }
}
