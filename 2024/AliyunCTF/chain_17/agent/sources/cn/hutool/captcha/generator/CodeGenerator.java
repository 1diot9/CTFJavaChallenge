package cn.hutool.captcha.generator;

import java.io.Serializable;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/captcha/generator/CodeGenerator.class */
public interface CodeGenerator extends Serializable {
    String generate();

    boolean verify(String str, String str2);
}
