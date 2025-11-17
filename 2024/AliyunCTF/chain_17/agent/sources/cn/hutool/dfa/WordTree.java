package cn.hutool.dfa;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Filter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/dfa/WordTree.class */
public class WordTree extends HashMap<Character, WordTree> {
    private static final long serialVersionUID = -4646423269465809276L;
    private final Set<Character> endCharacterSet = new HashSet();
    private Filter<Character> charFilter = (v0) -> {
        return StopChar.isNotStopChar(v0);
    };

    public WordTree setCharFilter(Filter<Character> charFilter) {
        this.charFilter = charFilter;
        return this;
    }

    public WordTree addWords(Collection<String> words) {
        if (false == (words instanceof Set)) {
            words = new HashSet(words);
        }
        for (String word : words) {
            addWord(word);
        }
        return this;
    }

    public WordTree addWords(String... words) {
        Iterator it = CollUtil.newHashSet(words).iterator();
        while (it.hasNext()) {
            String word = (String) it.next();
            addWord(word);
        }
        return this;
    }

    public WordTree addWord(String word) {
        Filter<Character> charFilter = this.charFilter;
        WordTree parent = null;
        WordTree current = this;
        char currentChar = 0;
        int length = word.length();
        for (int i = 0; i < length; i++) {
            currentChar = word.charAt(i);
            if (charFilter.accept(Character.valueOf(currentChar))) {
                WordTree child = current.get(Character.valueOf(currentChar));
                if (child == null) {
                    child = new WordTree();
                    current.put(Character.valueOf(currentChar), child);
                }
                parent = current;
                current = child;
            }
        }
        if (null != parent) {
            parent.setEnd(Character.valueOf(currentChar));
        }
        return this;
    }

    public boolean isMatch(String text) {
        return (null == text || null == matchWord(text)) ? false : true;
    }

    public String match(String text) {
        FoundWord foundWord = matchWord(text);
        if (null != foundWord) {
            return foundWord.toString();
        }
        return null;
    }

    public FoundWord matchWord(String text) {
        if (null == text) {
            return null;
        }
        List<FoundWord> matchAll = matchAllWords(text, 1);
        return (FoundWord) CollUtil.get(matchAll, 0);
    }

    public List<String> matchAll(String text) {
        return matchAll(text, -1);
    }

    public List<FoundWord> matchAllWords(String text) {
        return matchAllWords(text, -1);
    }

    public List<String> matchAll(String text, int limit) {
        return matchAll(text, limit, false, false);
    }

    public List<FoundWord> matchAllWords(String text, int limit) {
        return matchAllWords(text, limit, false, false);
    }

    public List<String> matchAll(String text, int limit, boolean isDensityMatch, boolean isGreedMatch) {
        List<FoundWord> matchAllWords = matchAllWords(text, limit, isDensityMatch, isGreedMatch);
        return CollUtil.map(matchAllWords, (v0) -> {
            return v0.toString();
        }, true);
    }

    /* JADX WARN: Code restructure failed: missing block: B:44:0x010b, code lost:            continue;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public java.util.List<cn.hutool.dfa.FoundWord> matchAllWords(java.lang.String r9, int r10, boolean r11, boolean r12) {
        /*
            Method dump skipped, instructions count: 279
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: cn.hutool.dfa.WordTree.matchAllWords(java.lang.String, int, boolean, boolean):java.util.List");
    }

    private boolean isEnd(Character c) {
        return this.endCharacterSet.contains(c);
    }

    private void setEnd(Character c) {
        if (null != c) {
            this.endCharacterSet.add(c);
        }
    }

    @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
    public void clear() {
        super.clear();
        this.endCharacterSet.clear();
    }
}
