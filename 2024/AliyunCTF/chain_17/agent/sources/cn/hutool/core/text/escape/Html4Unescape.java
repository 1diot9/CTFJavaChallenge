package cn.hutool.core.text.escape;

import cn.hutool.core.text.replacer.LookupReplacer;
import cn.hutool.core.text.replacer.StrReplacer;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/text/escape/Html4Unescape.class */
public class Html4Unescape extends XmlUnescape {
    private static final long serialVersionUID = 1;
    protected static final String[][] ISO8859_1_UNESCAPE = InternalEscapeUtil.invert(Html4Escape.ISO8859_1_ESCAPE);
    protected static final String[][] HTML40_EXTENDED_UNESCAPE = InternalEscapeUtil.invert(Html4Escape.HTML40_EXTENDED_ESCAPE);

    public Html4Unescape() {
        addChain((StrReplacer) new LookupReplacer(ISO8859_1_UNESCAPE));
        addChain((StrReplacer) new LookupReplacer(HTML40_EXTENDED_UNESCAPE));
    }
}
