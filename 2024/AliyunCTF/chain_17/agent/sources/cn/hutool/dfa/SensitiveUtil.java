package cn.hutool.dfa;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/dfa/SensitiveUtil.class */
public final class SensitiveUtil {
    public static final char DEFAULT_SEPARATOR = ',';
    private static final WordTree sensitiveTree = new WordTree();

    public static boolean isInited() {
        return false == sensitiveTree.isEmpty();
    }

    public static void init(Collection<String> sensitiveWords, boolean isAsync) {
        if (isAsync) {
            ThreadUtil.execAsync(() -> {
                init(sensitiveWords);
                return true;
            });
        } else {
            init(sensitiveWords);
        }
    }

    public static void init(Collection<String> sensitiveWords) {
        sensitiveTree.clear();
        sensitiveTree.addWords(sensitiveWords);
    }

    public static void init(String sensitiveWords, char separator, boolean isAsync) {
        if (StrUtil.isNotBlank(sensitiveWords)) {
            init(StrUtil.split((CharSequence) sensitiveWords, separator), isAsync);
        }
    }

    public static void init(String sensitiveWords, boolean isAsync) {
        init(sensitiveWords, ',', isAsync);
    }

    public static void setCharFilter(Filter<Character> charFilter) {
        if (charFilter != null) {
            sensitiveTree.setCharFilter(charFilter);
        }
    }

    public static boolean containsSensitive(String text) {
        return sensitiveTree.isMatch(text);
    }

    public static boolean containsSensitive(Object obj) {
        return sensitiveTree.isMatch(JSONUtil.toJsonStr(obj));
    }

    public static FoundWord getFoundFirstSensitive(String text) {
        return sensitiveTree.matchWord(text);
    }

    public static FoundWord getFoundFirstSensitive(Object obj) {
        return sensitiveTree.matchWord(JSONUtil.toJsonStr(obj));
    }

    public static List<FoundWord> getFoundAllSensitive(String text) {
        return sensitiveTree.matchAllWords(text);
    }

    public static List<FoundWord> getFoundAllSensitive(String text, boolean isDensityMatch, boolean isGreedMatch) {
        return sensitiveTree.matchAllWords(text, -1, isDensityMatch, isGreedMatch);
    }

    public static List<FoundWord> getFoundAllSensitive(Object bean) {
        return sensitiveTree.matchAllWords(JSONUtil.toJsonStr(bean));
    }

    public static List<FoundWord> getFoundAllSensitive(Object bean, boolean isDensityMatch, boolean isGreedMatch) {
        return getFoundAllSensitive(JSONUtil.toJsonStr(bean), isDensityMatch, isGreedMatch);
    }

    public static <T> T sensitiveFilter(T t, boolean z, SensitiveProcessor sensitiveProcessor) {
        String jsonStr = JSONUtil.toJsonStr(t);
        return (T) JSONUtil.toBean(sensitiveFilter(jsonStr, z, sensitiveProcessor), t.getClass());
    }

    public static String sensitiveFilter(String text) {
        return sensitiveFilter(text, true, (SensitiveProcessor) null);
    }

    public static String sensitiveFilter(String text, boolean isGreedMatch, SensitiveProcessor sensitiveProcessor) {
        if (StrUtil.isEmpty(text)) {
            return text;
        }
        List<FoundWord> foundWordList = getFoundAllSensitive(text, true, isGreedMatch);
        if (CollUtil.isEmpty((Collection<?>) foundWordList)) {
            return text;
        }
        SensitiveProcessor sensitiveProcessor2 = sensitiveProcessor == null ? new SensitiveProcessor() { // from class: cn.hutool.dfa.SensitiveUtil.1
        } : sensitiveProcessor;
        Map<Integer, FoundWord> foundWordMap = new HashMap<>(foundWordList.size(), 1.0f);
        foundWordList.forEach(foundWord -> {
        });
        int length = text.length();
        StringBuilder textStringBuilder = new StringBuilder();
        int i = 0;
        while (i < length) {
            FoundWord fw = foundWordMap.get(Integer.valueOf(i));
            if (fw != null) {
                textStringBuilder.append(sensitiveProcessor2.process(fw));
                i = fw.getEndIndex().intValue();
            } else {
                textStringBuilder.append(text.charAt(i));
            }
            i++;
        }
        return textStringBuilder.toString();
    }
}
