package org.springframework.boot.json;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.PropertyAccessor;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/json/BasicJsonParser.class */
public class BasicJsonParser extends AbstractJsonParser {
    private static final int MAX_DEPTH = 1000;

    @Override // org.springframework.boot.json.JsonParser
    public Map<String, Object> parseMap(String json) {
        return (Map) tryParse(() -> {
            return parseMap(json, jsonToParse -> {
                return parseMapInternal(0, jsonToParse);
            });
        }, Exception.class);
    }

    @Override // org.springframework.boot.json.JsonParser
    public List<Object> parseList(String json) {
        return (List) tryParse(() -> {
            return parseList(json, jsonToParse -> {
                return parseListInternal(0, jsonToParse);
            });
        }, Exception.class);
    }

    private List<Object> parseListInternal(int nesting, String json) {
        List<Object> list = new ArrayList<>();
        for (String value : tokenize(trimLeadingCharacter(trimTrailingCharacter(json, ']'), '[').trim())) {
            list.add(parseInternal(nesting + 1, value));
        }
        return list;
    }

    private Object parseInternal(int nesting, String json) {
        if (nesting > 1000) {
            throw new IllegalStateException("JSON is too deeply nested");
        }
        if (json.startsWith(PropertyAccessor.PROPERTY_KEY_PREFIX)) {
            return parseListInternal(nesting + 1, json);
        }
        if (json.startsWith("{")) {
            return parseMapInternal(nesting + 1, json);
        }
        if (json.startsWith("\"")) {
            return trimTrailingCharacter(trimLeadingCharacter(json, '\"'), '\"');
        }
        try {
            return Long.valueOf(json);
        } catch (NumberFormatException e) {
            try {
                return Double.valueOf(json);
            } catch (NumberFormatException e2) {
                return json;
            }
        }
    }

    private Map<String, Object> parseMapInternal(int nesting, String json) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (String pair : tokenize(trimLeadingCharacter(trimTrailingCharacter(json, '}'), '{').trim())) {
            String[] values = StringUtils.trimArrayElements(StringUtils.split(pair, ":"));
            Assert.state(values[0].startsWith("\"") && values[0].endsWith("\""), "Expecting double-quotes around field names");
            String key = trimLeadingCharacter(trimTrailingCharacter(values[0], '\"'), '\"');
            Object value = parseInternal(nesting, values[1]);
            map.put(key, value);
        }
        return map;
    }

    private static String trimTrailingCharacter(String string, char c) {
        if (!string.isEmpty() && string.charAt(string.length() - 1) == c) {
            return string.substring(0, string.length() - 1);
        }
        return string;
    }

    private static String trimLeadingCharacter(String string, char c) {
        if (!string.isEmpty() && string.charAt(0) == c) {
            return string.substring(1);
        }
        return string;
    }

    private List<String> tokenize(String json) {
        List<String> list = new ArrayList<>();
        int index = 0;
        int inObject = 0;
        int inList = 0;
        boolean inValue = false;
        boolean inEscape = false;
        StringBuilder build = new StringBuilder();
        while (index < json.length()) {
            char current = json.charAt(index);
            if (inEscape) {
                build.append(current);
                index++;
                inEscape = false;
            } else {
                if (current == '{') {
                    inObject++;
                }
                if (current == '}') {
                    inObject--;
                }
                if (current == '[') {
                    inList++;
                }
                if (current == ']') {
                    inList--;
                }
                if (current == '\"') {
                    inValue = !inValue;
                }
                if (current == ',' && inObject == 0 && inList == 0 && !inValue) {
                    list.add(build.toString());
                    build.setLength(0);
                } else if (current == '\\') {
                    inEscape = true;
                } else {
                    build.append(current);
                }
                index++;
            }
        }
        if (!build.isEmpty()) {
            list.add(build.toString().trim());
        }
        return list;
    }
}
