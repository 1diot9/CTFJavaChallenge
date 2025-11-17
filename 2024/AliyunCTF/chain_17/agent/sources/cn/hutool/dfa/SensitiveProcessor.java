package cn.hutool.dfa;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/dfa/SensitiveProcessor.class */
public interface SensitiveProcessor {
    default String process(FoundWord foundWord) {
        int length = foundWord.getFoundWord().length();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append("*");
        }
        return sb.toString();
    }
}
