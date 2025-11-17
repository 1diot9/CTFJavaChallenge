package cn.hutool.core.codec;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/codec/Morse.class */
public class Morse {
    private static final Map<Integer, String> ALPHABETS = new HashMap();
    private static final Map<String, Integer> DICTIONARIES = new HashMap();
    private final char dit;
    private final char dah;
    private final char split;

    static {
        registerMorse('A', "01");
        registerMorse('B', "1000");
        registerMorse('C', "1010");
        registerMorse('D', "100");
        registerMorse('E', CustomBooleanEditor.VALUE_0);
        registerMorse('F', "0010");
        registerMorse('G', "110");
        registerMorse('H', "0000");
        registerMorse('I', "00");
        registerMorse('J', "0111");
        registerMorse('K', "101");
        registerMorse('L', "0100");
        registerMorse('M', "11");
        registerMorse('N', "10");
        registerMorse('O', "111");
        registerMorse('P', "0110");
        registerMorse('Q', "1101");
        registerMorse('R', "010");
        registerMorse('S', "000");
        registerMorse('T', CustomBooleanEditor.VALUE_1);
        registerMorse('U', "001");
        registerMorse('V', "0001");
        registerMorse('W', "011");
        registerMorse('X', "1001");
        registerMorse('Y', "1011");
        registerMorse('Z', "1100");
        registerMorse('0', "11111");
        registerMorse('1', "01111");
        registerMorse('2', "00111");
        registerMorse('3', "00011");
        registerMorse('4', "00001");
        registerMorse('5', "00000");
        registerMorse('6', "10000");
        registerMorse('7', "11000");
        registerMorse('8', "11100");
        registerMorse('9', "11110");
        registerMorse('.', "010101");
        registerMorse(',', "110011");
        registerMorse('?', "001100");
        registerMorse('\'', "011110");
        registerMorse('!', "101011");
        registerMorse('/', "10010");
        registerMorse('(', "10110");
        registerMorse(')', "101101");
        registerMorse('&', "01000");
        registerMorse(':', "111000");
        registerMorse(';', "101010");
        registerMorse('=', "10001");
        registerMorse('+', "01010");
        registerMorse('-', "100001");
        registerMorse('_', "001101");
        registerMorse('\"', "010010");
        registerMorse('$', "0001001");
        registerMorse('@', "011010");
    }

    private static void registerMorse(Character abc, String dict) {
        ALPHABETS.put(Integer.valueOf(abc.charValue()), dict);
        DICTIONARIES.put(dict, Integer.valueOf(abc.charValue()));
    }

    public Morse() {
        this('.', '-', '/');
    }

    public Morse(char dit, char dah, char split) {
        this.dit = dit;
        this.dah = dah;
        this.split = split;
    }

    public String encode(String text) {
        Assert.notNull(text, "Text should not be null.", new Object[0]);
        String text2 = text.toUpperCase();
        StringBuilder morseBuilder = new StringBuilder();
        int len = text2.codePointCount(0, text2.length());
        for (int i = 0; i < len; i++) {
            int codePoint = text2.codePointAt(i);
            String word = ALPHABETS.get(Integer.valueOf(codePoint));
            if (word == null) {
                word = Integer.toBinaryString(codePoint);
            }
            morseBuilder.append(word.replace('0', this.dit).replace('1', this.dah)).append(this.split);
        }
        return morseBuilder.toString();
    }

    public String decode(String morse) {
        Assert.notNull(morse, "Morse should not be null.", new Object[0]);
        char dit = this.dit;
        char dah = this.dah;
        char split = this.split;
        if (false == StrUtil.containsOnly(morse, dit, dah, split)) {
            throw new IllegalArgumentException("Incorrect morse.");
        }
        List<String> words = StrUtil.split((CharSequence) morse, split);
        StringBuilder textBuilder = new StringBuilder();
        for (String word : words) {
            if (!StrUtil.isEmpty(word)) {
                String word2 = word.replace(dit, '0').replace(dah, '1');
                Integer codePoint = DICTIONARIES.get(word2);
                if (codePoint == null) {
                    codePoint = Integer.valueOf(word2, 2);
                }
                textBuilder.appendCodePoint(codePoint.intValue());
            }
        }
        return textBuilder.toString();
    }
}
