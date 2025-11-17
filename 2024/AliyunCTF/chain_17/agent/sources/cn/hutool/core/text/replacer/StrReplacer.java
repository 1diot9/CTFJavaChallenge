package cn.hutool.core.text.replacer;

import cn.hutool.core.lang.Replacer;
import cn.hutool.core.text.StrBuilder;
import java.io.Serializable;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/text/replacer/StrReplacer.class */
public abstract class StrReplacer implements Replacer<CharSequence>, Serializable {
    private static final long serialVersionUID = 1;

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract int replace(CharSequence charSequence, int i, StrBuilder strBuilder);

    @Override // cn.hutool.core.lang.Replacer
    public CharSequence replace(CharSequence t) {
        int len = t.length();
        StrBuilder builder = StrBuilder.create(len);
        int i = 0;
        while (true) {
            int pos = i;
            if (pos < len) {
                int consumed = replace(t, pos, builder);
                if (0 == consumed) {
                    builder.append(t.charAt(pos));
                    pos++;
                }
                i = pos + consumed;
            } else {
                return builder;
            }
        }
    }
}
