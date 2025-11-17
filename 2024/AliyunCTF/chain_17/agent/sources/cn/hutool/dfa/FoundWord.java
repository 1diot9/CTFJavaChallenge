package cn.hutool.dfa;

import cn.hutool.core.lang.DefaultSegment;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/dfa/FoundWord.class */
public class FoundWord extends DefaultSegment<Integer> {
    private final String word;
    private final String foundWord;

    public FoundWord(String word, String foundWord, int startIndex, int endIndex) {
        super(Integer.valueOf(startIndex), Integer.valueOf(endIndex));
        this.word = word;
        this.foundWord = foundWord;
    }

    public String getWord() {
        return this.word;
    }

    public String getFoundWord() {
        return this.foundWord;
    }

    public String toString() {
        return this.foundWord;
    }
}
