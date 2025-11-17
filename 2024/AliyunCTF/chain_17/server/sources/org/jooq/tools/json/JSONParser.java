package org.jooq.tools.json;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/json/JSONParser.class */
public class JSONParser {
    public static final int S_INIT = 0;
    public static final int S_IN_FINISHED_VALUE = 1;
    public static final int S_IN_OBJECT = 2;
    public static final int S_IN_ARRAY = 3;
    public static final int S_PASSED_PAIR_KEY = 4;
    public static final int S_IN_PAIR_VALUE = 5;
    public static final int S_END = 6;
    public static final int S_IN_ERROR = -1;
    private LinkedList handlerStatusStack;
    private Yylex lexer = new Yylex((Reader) null);
    private Yytoken token = null;
    private int status = 0;

    private int peekStatus(LinkedList statusStack) {
        if (statusStack.size() == 0) {
            return -1;
        }
        Integer status = (Integer) statusStack.getFirst();
        return status.intValue();
    }

    public void reset() {
        this.token = null;
        this.status = 0;
        this.handlerStatusStack = null;
    }

    public void reset(Reader in) {
        this.lexer.yyreset(in);
        reset();
    }

    public int getPosition() {
        return this.lexer.getPosition();
    }

    public Object parse(String s) throws ParseException {
        return parse(s, (ContainerFactory) null);
    }

    public Object parse(String s, ContainerFactory containerFactory) throws ParseException {
        StringReader in = new StringReader(s);
        try {
            return parse(in, containerFactory);
        } catch (IOException ie) {
            throw new ParseException(-1, 2, ie);
        }
    }

    public Object parse(Reader in) throws IOException, ParseException {
        return parse(in, (ContainerFactory) null);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:4:0x001e. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:43:0x0393 A[Catch: IOException -> 0x03a1, TryCatch #0 {IOException -> 0x03a1, blocks: (B:3:0x0016, B:4:0x001e, B:5:0x0044, B:6:0x004b, B:7:0x0068, B:8:0x0087, B:9:0x00a4, B:10:0x00c1, B:55:0x00c9, B:57:0x00d4, B:59:0x00da, B:60:0x00ea, B:11:0x00eb, B:12:0x00f2, B:14:0x0117, B:16:0x0124, B:17:0x014a, B:18:0x0152, B:20:0x015b, B:21:0x0172, B:22:0x017a, B:23:0x0182, B:24:0x0189, B:26:0x01b7, B:27:0x01ed, B:28:0x0233, B:29:0x0279, B:30:0x0281, B:31:0x0288, B:33:0x02b3, B:34:0x02cf, B:36:0x02d8, B:37:0x02ef, B:38:0x02f7, B:39:0x032c, B:40:0x0361, B:62:0x0369, B:63:0x0379, B:41:0x037a, B:52:0x0382, B:53:0x0392, B:43:0x0393), top: B:2:0x0016 }] */
    /* JADX WARN: Removed duplicated region for block: B:51:0x0382 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public java.lang.Object parse(java.io.Reader r7, org.jooq.tools.json.ContainerFactory r8) throws java.io.IOException, org.jooq.tools.json.ParseException {
        /*
            Method dump skipped, instructions count: 951
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jooq.tools.json.JSONParser.parse(java.io.Reader, org.jooq.tools.json.ContainerFactory):java.lang.Object");
    }

    private void nextToken() throws ParseException, IOException {
        this.token = this.lexer.yylex();
        if (this.token == null) {
            this.token = new Yytoken(-1, null);
        }
    }

    private Map createObjectContainer(ContainerFactory containerFactory) {
        if (containerFactory == null) {
            return new JSONObject();
        }
        Map m = containerFactory.createObjectContainer();
        if (m == null) {
            return new JSONObject();
        }
        return m;
    }

    private List createArrayContainer(ContainerFactory containerFactory) {
        if (containerFactory == null) {
            return new JSONArray();
        }
        List l = containerFactory.createArrayContainer();
        if (l == null) {
            return new JSONArray();
        }
        return l;
    }

    public void parse(String s, ContentHandler contentHandler) throws ParseException {
        parse(s, contentHandler, false);
    }

    public void parse(String s, ContentHandler contentHandler, boolean isResume) throws ParseException {
        StringReader in = new StringReader(s);
        try {
            parse(in, contentHandler, isResume);
        } catch (IOException ie) {
            throw new ParseException(-1, 2, ie);
        }
    }

    public void parse(Reader in, ContentHandler contentHandler) throws IOException, ParseException {
        parse(in, contentHandler, false);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:7:0x003a. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:101:0x036f A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:94:0x0380 A[Catch: IOException -> 0x038e, ParseException -> 0x0398, RuntimeException -> 0x03a2, Error -> 0x03ac, TryCatch #2 {IOException -> 0x038e, Error -> 0x03ac, RuntimeException -> 0x03a2, ParseException -> 0x0398, blocks: (B:6:0x0036, B:7:0x003a, B:8:0x0068, B:9:0x0079, B:10:0x0098, B:15:0x00ba, B:19:0x00d5, B:23:0x00f0, B:105:0x00f8, B:107:0x0107, B:109:0x0114, B:110:0x0129, B:24:0x012a, B:25:0x0135, B:27:0x015b, B:29:0x0168, B:34:0x0194, B:35:0x019c, B:37:0x01a5, B:38:0x01bd, B:42:0x01b8, B:43:0x01c7, B:44:0x01cf, B:45:0x01da, B:47:0x0207, B:49:0x0228, B:55:0x0232, B:59:0x025c, B:63:0x0286, B:64:0x028e, B:68:0x02a8, B:69:0x02b3, B:71:0x02db, B:75:0x02ec, B:77:0x02f5, B:78:0x030d, B:82:0x0308, B:83:0x0317, B:87:0x0332, B:91:0x034d, B:114:0x0356, B:115:0x0366, B:92:0x0367, B:102:0x036f, B:103:0x037f, B:94:0x0380), top: B:5:0x0036 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void parse(java.io.Reader r7, org.jooq.tools.json.ContentHandler r8, boolean r9) throws java.io.IOException, org.jooq.tools.json.ParseException {
        /*
            Method dump skipped, instructions count: 972
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jooq.tools.json.JSONParser.parse(java.io.Reader, org.jooq.tools.json.ContentHandler, boolean):void");
    }
}
