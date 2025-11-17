package com.fasterxml.jackson.core.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import java.io.IOException;

/* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/util/DefaultIndenter.class */
public class DefaultIndenter extends DefaultPrettyPrinter.NopIndenter {
    private static final long serialVersionUID = 1;
    public static final String SYS_LF;
    public static final DefaultIndenter SYSTEM_LINEFEED_INSTANCE;
    private static final int INDENT_LEVELS = 16;
    private final char[] indents;
    private final int charsPerLevel;
    private final String eol;

    static {
        String lf;
        try {
            lf = System.getProperty("line.separator");
        } catch (Throwable th) {
            lf = "\n";
        }
        SYS_LF = lf;
        SYSTEM_LINEFEED_INSTANCE = new DefaultIndenter("  ", SYS_LF);
    }

    public DefaultIndenter() {
        this("  ", SYS_LF);
    }

    public DefaultIndenter(String indent, String eol) {
        this.charsPerLevel = indent.length();
        this.indents = new char[indent.length() * 16];
        int offset = 0;
        for (int i = 0; i < 16; i++) {
            indent.getChars(0, indent.length(), this.indents, offset);
            offset += indent.length();
        }
        this.eol = eol;
    }

    public DefaultIndenter withLinefeed(String lf) {
        if (lf.equals(this.eol)) {
            return this;
        }
        return new DefaultIndenter(getIndent(), lf);
    }

    public DefaultIndenter withIndent(String indent) {
        if (indent.equals(getIndent())) {
            return this;
        }
        return new DefaultIndenter(indent, this.eol);
    }

    @Override // com.fasterxml.jackson.core.util.DefaultPrettyPrinter.NopIndenter, com.fasterxml.jackson.core.util.DefaultPrettyPrinter.Indenter
    public boolean isInline() {
        return false;
    }

    @Override // com.fasterxml.jackson.core.util.DefaultPrettyPrinter.NopIndenter, com.fasterxml.jackson.core.util.DefaultPrettyPrinter.Indenter
    public void writeIndentation(JsonGenerator jg, int level) throws IOException {
        jg.writeRaw(this.eol);
        if (level > 0) {
            int i = level * this.charsPerLevel;
            while (true) {
                int level2 = i;
                if (level2 > this.indents.length) {
                    jg.writeRaw(this.indents, 0, this.indents.length);
                    i = level2 - this.indents.length;
                } else {
                    jg.writeRaw(this.indents, 0, level2);
                    return;
                }
            }
        }
    }

    public String getEol() {
        return this.eol;
    }

    public String getIndent() {
        return new String(this.indents, 0, this.charsPerLevel);
    }
}
