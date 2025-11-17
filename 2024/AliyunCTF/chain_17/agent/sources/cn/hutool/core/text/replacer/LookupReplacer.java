package cn.hutool.core.text.replacer;

import cn.hutool.core.text.StrBuilder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/text/replacer/LookupReplacer.class */
public class LookupReplacer extends StrReplacer {
    private static final long serialVersionUID = 1;
    private final Map<String, String> lookupMap = new HashMap();
    private final Set<Character> prefixSet = new HashSet();
    private final int minLength;
    private final int maxLength;

    public LookupReplacer(String[]... lookup) {
        int minLength = Integer.MAX_VALUE;
        int maxLength = 0;
        for (String[] pair : lookup) {
            String key = pair[0];
            this.lookupMap.put(key, pair[1]);
            this.prefixSet.add(Character.valueOf(key.charAt(0)));
            int keySize = key.length();
            maxLength = keySize > maxLength ? keySize : maxLength;
            if (keySize < minLength) {
                minLength = keySize;
            }
        }
        this.maxLength = maxLength;
        this.minLength = minLength;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // cn.hutool.core.text.replacer.StrReplacer
    public int replace(CharSequence str, int pos, StrBuilder out) {
        if (this.prefixSet.contains(Character.valueOf(str.charAt(pos)))) {
            int max = this.maxLength;
            if (pos + this.maxLength > str.length()) {
                max = str.length() - pos;
            }
            for (int i = max; i >= this.minLength; i--) {
                CharSequence subSeq = str.subSequence(pos, pos + i);
                String result = this.lookupMap.get(subSeq.toString());
                if (null != result) {
                    out.append((CharSequence) result);
                    return i;
                }
            }
            return 0;
        }
        return 0;
    }
}
