package cn.hutool.json.xml;

import cn.hutool.json.InternalJSONUtil;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import cn.hutool.json.XML;
import cn.hutool.json.XMLTokener;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/json/xml/JSONXMLParser.class */
public class JSONXMLParser {
    public static void parseJSONObject(JSONObject jo, String xmlStr, boolean keepStrings) throws JSONException {
        XMLTokener x = new XMLTokener(xmlStr, jo.getConfig());
        while (x.more() && x.skipPast("<")) {
            parse(x, jo, null, keepStrings);
        }
    }

    private static boolean parse(XMLTokener x, JSONObject context, String name, boolean keepStrings) throws JSONException {
        Object token = x.nextToken();
        if (token == XML.BANG) {
            char c = x.next();
            if (c == '-') {
                if (x.next() == '-') {
                    x.skipPast("-->");
                    return false;
                }
                x.back();
            } else if (c == '[') {
                if ("CDATA".equals(x.nextToken()) && x.next() == '[') {
                    String string = x.nextCDATA();
                    if (string.length() > 0) {
                        context.accumulate("content", string);
                        return false;
                    }
                    return false;
                }
                throw x.syntaxError("Expected 'CDATA['");
            }
            int i = 1;
            do {
                Object token2 = x.nextMeta();
                if (token2 == null) {
                    throw x.syntaxError("Missing '>' after '<!'.");
                }
                if (token2 == XML.LT) {
                    i++;
                } else if (token2 == XML.GT) {
                    i--;
                }
            } while (i > 0);
            return false;
        }
        if (token == XML.QUEST) {
            x.skipPast("?>");
            return false;
        }
        if (token == XML.SLASH) {
            Object token3 = x.nextToken();
            if (name == null) {
                throw x.syntaxError("Mismatched close tag " + token3);
            }
            if (!token3.equals(name)) {
                throw x.syntaxError("Mismatched " + name + " and " + token3);
            }
            if (x.nextToken() != XML.GT) {
                throw x.syntaxError("Misshaped close tag");
            }
            return true;
        }
        if (token instanceof Character) {
            throw x.syntaxError("Misshaped tag");
        }
        String tagName = (String) token;
        Object token4 = null;
        JSONObject jsonobject = new JSONObject();
        while (true) {
            if (token4 == null) {
                token4 = x.nextToken();
            }
            if (token4 instanceof String) {
                String string2 = (String) token4;
                token4 = x.nextToken();
                if (token4 == XML.EQ) {
                    Object token5 = x.nextToken();
                    if (!(token5 instanceof String)) {
                        throw x.syntaxError("Missing value");
                    }
                    jsonobject.accumulate(string2, keepStrings ? token5 : InternalJSONUtil.stringToValue((String) token5));
                    token4 = null;
                } else {
                    jsonobject.accumulate(string2, "");
                }
            } else {
                if (token4 == XML.SLASH) {
                    if (x.nextToken() != XML.GT) {
                        throw x.syntaxError("Misshaped tag");
                    }
                    if (jsonobject.size() > 0) {
                        context.accumulate(tagName, jsonobject);
                        return false;
                    }
                    context.accumulate(tagName, "");
                    return false;
                }
                if (token4 != XML.GT) {
                    throw x.syntaxError("Misshaped tag");
                }
                while (true) {
                    Object token6 = x.nextContent();
                    if (token6 == null) {
                        if (tagName != null) {
                            throw x.syntaxError("Unclosed tag " + tagName);
                        }
                        return false;
                    }
                    if (token6 instanceof String) {
                        String string3 = (String) token6;
                        if (string3.length() > 0) {
                            jsonobject.accumulate("content", keepStrings ? token6 : InternalJSONUtil.stringToValue(string3));
                        }
                    } else if (token6 == XML.LT && parse(x, jsonobject, tagName, keepStrings)) {
                        if (jsonobject.size() == 0) {
                            context.accumulate(tagName, "");
                            return false;
                        }
                        if (jsonobject.size() == 1 && jsonobject.get("content") != null) {
                            context.accumulate(tagName, jsonobject.get("content"));
                            return false;
                        }
                        context.accumulate(tagName, jsonobject);
                        return false;
                    }
                }
            }
        }
    }
}
