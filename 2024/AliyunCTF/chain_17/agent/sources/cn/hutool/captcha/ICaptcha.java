package cn.hutool.captcha;

import java.io.OutputStream;
import java.io.Serializable;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/captcha/ICaptcha.class */
public interface ICaptcha extends Serializable {
    void createCode();

    String getCode();

    boolean verify(String str);

    void write(OutputStream outputStream);
}
