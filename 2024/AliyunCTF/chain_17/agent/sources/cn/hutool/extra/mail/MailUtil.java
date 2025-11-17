package cn.hutool.extra.mail;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import java.io.Closeable;
import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.mail.Authenticator;
import javax.mail.Session;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/mail/MailUtil.class */
public class MailUtil {
    public static String sendText(String to, String subject, String content, File... files) {
        return send(to, subject, content, false, files);
    }

    public static String sendHtml(String to, String subject, String content, File... files) {
        return send(to, subject, content, true, files);
    }

    public static String send(String to, String subject, String content, boolean isHtml, File... files) {
        return send(splitAddress(to), subject, content, isHtml, files);
    }

    public static String send(String to, String cc, String bcc, String subject, String content, boolean isHtml, File... files) {
        return send(splitAddress(to), splitAddress(cc), splitAddress(bcc), subject, content, isHtml, files);
    }

    public static String sendText(Collection<String> tos, String subject, String content, File... files) {
        return send(tos, subject, content, false, files);
    }

    public static String sendHtml(Collection<String> tos, String subject, String content, File... files) {
        return send(tos, subject, content, true, files);
    }

    public static String send(Collection<String> tos, String subject, String content, boolean isHtml, File... files) {
        return send(tos, (Collection<String>) null, (Collection<String>) null, subject, content, isHtml, files);
    }

    public static String send(Collection<String> tos, Collection<String> ccs, Collection<String> bccs, String subject, String content, boolean isHtml, File... files) {
        return send(GlobalMailAccount.INSTANCE.getAccount(), true, tos, ccs, bccs, subject, content, null, isHtml, files);
    }

    public static String send(MailAccount mailAccount, String to, String subject, String content, boolean isHtml, File... files) {
        return send(mailAccount, splitAddress(to), subject, content, isHtml, files);
    }

    public static String send(MailAccount mailAccount, Collection<String> tos, String subject, String content, boolean isHtml, File... files) {
        return send(mailAccount, tos, (Collection<String>) null, (Collection<String>) null, subject, content, isHtml, files);
    }

    public static String send(MailAccount mailAccount, Collection<String> tos, Collection<String> ccs, Collection<String> bccs, String subject, String content, boolean isHtml, File... files) {
        return send(mailAccount, false, tos, ccs, bccs, subject, content, null, isHtml, files);
    }

    public static String sendHtml(String to, String subject, String content, Map<String, InputStream> imageMap, File... files) {
        return send(to, subject, content, imageMap, true, files);
    }

    public static String send(String to, String subject, String content, Map<String, InputStream> imageMap, boolean isHtml, File... files) {
        return send(splitAddress(to), subject, content, imageMap, isHtml, files);
    }

    public static String send(String to, String cc, String bcc, String subject, String content, Map<String, InputStream> imageMap, boolean isHtml, File... files) {
        return send(splitAddress(to), splitAddress(cc), splitAddress(bcc), subject, content, imageMap, isHtml, files);
    }

    public static String sendHtml(Collection<String> tos, String subject, String content, Map<String, InputStream> imageMap, File... files) {
        return send(tos, subject, content, imageMap, true, files);
    }

    public static String send(Collection<String> tos, String subject, String content, Map<String, InputStream> imageMap, boolean isHtml, File... files) {
        return send(tos, (Collection<String>) null, (Collection<String>) null, subject, content, imageMap, isHtml, files);
    }

    public static String send(Collection<String> tos, Collection<String> ccs, Collection<String> bccs, String subject, String content, Map<String, InputStream> imageMap, boolean isHtml, File... files) {
        return send(GlobalMailAccount.INSTANCE.getAccount(), true, tos, ccs, bccs, subject, content, imageMap, isHtml, files);
    }

    public static String send(MailAccount mailAccount, String to, String subject, String content, Map<String, InputStream> imageMap, boolean isHtml, File... files) {
        return send(mailAccount, splitAddress(to), subject, content, imageMap, isHtml, files);
    }

    public static String send(MailAccount mailAccount, Collection<String> tos, String subject, String content, Map<String, InputStream> imageMap, boolean isHtml, File... files) {
        return send(mailAccount, tos, null, null, subject, content, imageMap, isHtml, files);
    }

    public static String send(MailAccount mailAccount, Collection<String> tos, Collection<String> ccs, Collection<String> bccs, String subject, String content, Map<String, InputStream> imageMap, boolean isHtml, File... files) {
        return send(mailAccount, false, tos, ccs, bccs, subject, content, imageMap, isHtml, files);
    }

    public static Session getSession(MailAccount mailAccount, boolean isSingleton) {
        Authenticator authenticator = null;
        if (mailAccount.isAuth().booleanValue()) {
            authenticator = new UserPassAuthenticator(mailAccount.getUser(), mailAccount.getPass());
        }
        return isSingleton ? Session.getDefaultInstance(mailAccount.getSmtpProps(), authenticator) : Session.getInstance(mailAccount.getSmtpProps(), authenticator);
    }

    private static String send(MailAccount mailAccount, boolean useGlobalSession, Collection<String> tos, Collection<String> ccs, Collection<String> bccs, String subject, String content, Map<String, InputStream> imageMap, boolean isHtml, File... files) {
        Mail mail = Mail.create(mailAccount).setUseGlobalSession(useGlobalSession);
        if (CollUtil.isNotEmpty((Collection<?>) ccs)) {
            mail.setCcs((String[]) ccs.toArray(new String[0]));
        }
        if (CollUtil.isNotEmpty((Collection<?>) bccs)) {
            mail.setBccs((String[]) bccs.toArray(new String[0]));
        }
        mail.setTos((String[]) tos.toArray(new String[0]));
        mail.setTitle(subject);
        mail.setContent(content);
        mail.setHtml(isHtml);
        mail.setFiles(files);
        if (MapUtil.isNotEmpty(imageMap)) {
            for (Map.Entry<String, InputStream> entry : imageMap.entrySet()) {
                mail.addImage(entry.getKey(), entry.getValue());
                IoUtil.close((Closeable) entry.getValue());
            }
        }
        return mail.send();
    }

    private static List<String> splitAddress(String addresses) {
        List<String> result;
        if (StrUtil.isBlank(addresses)) {
            return null;
        }
        if (StrUtil.contains((CharSequence) addresses, ',')) {
            result = StrUtil.splitTrim((CharSequence) addresses, ',');
        } else if (StrUtil.contains((CharSequence) addresses, ';')) {
            result = StrUtil.splitTrim((CharSequence) addresses, ';');
        } else {
            result = CollUtil.newArrayList(addresses);
        }
        return result;
    }
}
