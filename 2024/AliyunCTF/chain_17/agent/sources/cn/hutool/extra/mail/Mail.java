package cn.hutool.extra.mail;

import cn.hutool.core.builder.Builder;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Date;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.FileTypeMap;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/mail/Mail.class */
public class Mail implements Builder<MimeMessage> {
    private static final long serialVersionUID = 1;
    private final MailAccount mailAccount;
    private String[] tos;
    private String[] ccs;
    private String[] bccs;
    private String[] reply;
    private String title;
    private String content;
    private boolean isHtml;
    private final Multipart multipart;
    private boolean useGlobalSession;
    private PrintStream debugOutput;

    public static Mail create(MailAccount mailAccount) {
        return new Mail(mailAccount);
    }

    public static Mail create() {
        return new Mail();
    }

    public Mail() {
        this(GlobalMailAccount.INSTANCE.getAccount());
    }

    public Mail(MailAccount mailAccount) {
        this.multipart = new MimeMultipart();
        this.useGlobalSession = false;
        this.mailAccount = (null != mailAccount ? mailAccount : GlobalMailAccount.INSTANCE.getAccount()).defaultIfEmpty();
    }

    public Mail to(String... tos) {
        return setTos(tos);
    }

    public Mail setTos(String... tos) {
        this.tos = tos;
        return this;
    }

    public Mail setCcs(String... ccs) {
        this.ccs = ccs;
        return this;
    }

    public Mail setBccs(String... bccs) {
        this.bccs = bccs;
        return this;
    }

    public Mail setReply(String... reply) {
        this.reply = reply;
        return this;
    }

    public Mail setTitle(String title) {
        this.title = title;
        return this;
    }

    public Mail setContent(String content) {
        this.content = content;
        return this;
    }

    public Mail setHtml(boolean isHtml) {
        this.isHtml = isHtml;
        return this;
    }

    public Mail setContent(String content, boolean isHtml) {
        setContent(content);
        return setHtml(isHtml);
    }

    public Mail setFiles(File... files) {
        if (ArrayUtil.isEmpty((Object[]) files)) {
            return this;
        }
        DataSource[] attachments = new DataSource[files.length];
        for (int i = 0; i < files.length; i++) {
            attachments[i] = new FileDataSource(files[i]);
        }
        return setAttachments(attachments);
    }

    public Mail setAttachments(DataSource... attachments) {
        if (ArrayUtil.isNotEmpty((Object[]) attachments)) {
            Charset charset = this.mailAccount.getCharset();
            try {
                for (DataSource attachment : attachments) {
                    MimeBodyPart bodyPart = new MimeBodyPart();
                    bodyPart.setDataHandler(new DataHandler(attachment));
                    String nameEncoded = attachment.getName();
                    if (this.mailAccount.isEncodefilename()) {
                        nameEncoded = InternalMailUtil.encodeText(nameEncoded, charset);
                    }
                    bodyPart.setFileName(nameEncoded);
                    if (StrUtil.startWith(attachment.getContentType(), "image/")) {
                        bodyPart.setContentID(nameEncoded);
                    }
                    this.multipart.addBodyPart(bodyPart);
                }
            } catch (MessagingException e) {
                throw new MailException((Throwable) e);
            }
        }
        return this;
    }

    public Mail addImage(String cid, InputStream imageStream) {
        return addImage(cid, imageStream, null);
    }

    public Mail addImage(String cid, InputStream imageStream, String contentType) {
        try {
            ByteArrayDataSource imgSource = new ByteArrayDataSource(imageStream, (String) ObjectUtil.defaultIfNull(contentType, "image/jpeg"));
            imgSource.setName(cid);
            return setAttachments(imgSource);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public Mail addImage(String cid, File imageFile) {
        InputStream in = null;
        try {
            in = FileUtil.getInputStream(imageFile);
            Mail addImage = addImage(cid, in, FileTypeMap.getDefaultFileTypeMap().getContentType(imageFile));
            IoUtil.close((Closeable) in);
            return addImage;
        } catch (Throwable th) {
            IoUtil.close((Closeable) in);
            throw th;
        }
    }

    public Mail setCharset(Charset charset) {
        this.mailAccount.setCharset(charset);
        return this;
    }

    public Mail setUseGlobalSession(boolean isUseGlobalSession) {
        this.useGlobalSession = isUseGlobalSession;
        return this;
    }

    public Mail setDebugOutput(PrintStream debugOutput) {
        this.debugOutput = debugOutput;
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.core.builder.Builder
    public MimeMessage build() {
        try {
            return buildMsg();
        } catch (MessagingException e) {
            throw new MailException((Throwable) e);
        }
    }

    public String send() throws MailException {
        try {
            return doSend();
        } catch (MessagingException e) {
            if (e instanceof SendFailedException) {
                Address[] invalidAddresses = e.getInvalidAddresses();
                String msg = StrUtil.format("Invalid Addresses: {}", ArrayUtil.toString(invalidAddresses));
                throw new MailException(msg, (Throwable) e);
            }
            throw new MailException((Throwable) e);
        }
    }

    private String doSend() throws MessagingException {
        MimeMessage mimeMessage = buildMsg();
        Transport.send(mimeMessage);
        return mimeMessage.getMessageID();
    }

    private MimeMessage buildMsg() throws MessagingException {
        Charset charset = this.mailAccount.getCharset();
        MimeMessage msg = new MimeMessage(getSession());
        String from = this.mailAccount.getFrom();
        if (StrUtil.isEmpty(from)) {
            msg.setFrom();
        } else {
            msg.setFrom(InternalMailUtil.parseFirstAddress(from, charset));
        }
        msg.setSubject(this.title, null == charset ? null : charset.name());
        msg.setSentDate(new Date());
        msg.setContent(buildContent(charset));
        msg.setRecipients(MimeMessage.RecipientType.TO, InternalMailUtil.parseAddressFromStrs(this.tos, charset));
        if (ArrayUtil.isNotEmpty((Object[]) this.ccs)) {
            msg.setRecipients(MimeMessage.RecipientType.CC, InternalMailUtil.parseAddressFromStrs(this.ccs, charset));
        }
        if (ArrayUtil.isNotEmpty((Object[]) this.bccs)) {
            msg.setRecipients(MimeMessage.RecipientType.BCC, InternalMailUtil.parseAddressFromStrs(this.bccs, charset));
        }
        if (ArrayUtil.isNotEmpty((Object[]) this.reply)) {
            msg.setReplyTo(InternalMailUtil.parseAddressFromStrs(this.reply, charset));
        }
        return msg;
    }

    private Multipart buildContent(Charset charset) throws MessagingException {
        String charsetStr = null != charset ? charset.name() : MimeUtility.getDefaultJavaCharset();
        MimeBodyPart body = new MimeBodyPart();
        String str = this.content;
        Object[] objArr = new Object[2];
        objArr[0] = this.isHtml ? "html" : "plain";
        objArr[1] = charsetStr;
        body.setContent(str, StrUtil.format("text/{}; charset={}", objArr));
        this.multipart.addBodyPart(body);
        return this.multipart;
    }

    private Session getSession() {
        Session session = MailUtil.getSession(this.mailAccount, this.useGlobalSession);
        if (null != this.debugOutput) {
            session.setDebugOut(this.debugOutput);
        }
        return session;
    }
}
