package cn.hutool.core.text.replacer;

import cn.hutool.core.lang.Chain;
import cn.hutool.core.text.StrBuilder;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/text/replacer/ReplacerChain.class */
public class ReplacerChain extends StrReplacer implements Chain<StrReplacer, ReplacerChain> {
    private static final long serialVersionUID = 1;
    private final List<StrReplacer> replacers = new LinkedList();

    public ReplacerChain(StrReplacer... strReplacers) {
        for (StrReplacer strReplacer : strReplacers) {
            addChain(strReplacer);
        }
    }

    @Override // java.lang.Iterable
    public Iterator<StrReplacer> iterator() {
        return this.replacers.iterator();
    }

    @Override // cn.hutool.core.lang.Chain
    public ReplacerChain addChain(StrReplacer element) {
        this.replacers.add(element);
        return this;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // cn.hutool.core.text.replacer.StrReplacer
    public int replace(CharSequence str, int pos, StrBuilder out) {
        int consumed = 0;
        for (StrReplacer strReplacer : this.replacers) {
            consumed = strReplacer.replace(str, pos, out);
            if (0 != consumed) {
                return consumed;
            }
        }
        return consumed;
    }
}
