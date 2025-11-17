package cn.hutool.extra.mail;

import cn.hutool.core.io.IORuntimeException;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/mail/GlobalMailAccount.class */
public enum GlobalMailAccount {
    INSTANCE;

    private final MailAccount mailAccount = createDefaultAccount();

    GlobalMailAccount() {
    }

    public MailAccount getAccount() {
        return this.mailAccount;
    }

    private MailAccount createDefaultAccount() {
        for (String mailSettingPath : MailAccount.MAIL_SETTING_PATHS) {
            try {
                return new MailAccount(mailSettingPath);
            } catch (IORuntimeException e) {
            }
        }
        return null;
    }
}
