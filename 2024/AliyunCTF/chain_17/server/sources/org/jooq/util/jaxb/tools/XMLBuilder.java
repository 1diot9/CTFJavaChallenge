package org.jooq.util.jaxb.tools;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;
import org.jetbrains.annotations.ApiStatus;
import org.springframework.beans.factory.BeanFactory;

@ApiStatus.Internal
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/util/jaxb/tools/XMLBuilder.class */
public final class XMLBuilder {
    private final StringBuilder builder = new StringBuilder();
    private final boolean format;
    private int indentLevel;
    private boolean onNewLine;
    private static final Pattern P_XML_SPECIAL_CHARACTERS = Pattern.compile("[<>&]");

    private XMLBuilder(boolean format) {
        this.format = format;
    }

    public static XMLBuilder formatting() {
        return new XMLBuilder(true);
    }

    public static XMLBuilder nonFormatting() {
        return new XMLBuilder(false);
    }

    public XMLBuilder append(XMLAppendable appendable) {
        if (appendable != null) {
            appendable.appendTo(this);
        }
        return this;
    }

    public XMLBuilder append(String elementName, XMLAppendable appendable) {
        if (appendable != null) {
            openTag(elementName).newLine().indent();
            appendable.appendTo(this);
            unindent().closeTag(elementName).newLine();
        }
        return this;
    }

    public XMLBuilder append(String wrappingElementName, String elementName, List<?> list) {
        if (list != null) {
            openTag(wrappingElementName).newLine().indent();
            for (Object o : list) {
                if (o instanceof XMLAppendable) {
                    XMLAppendable x = (XMLAppendable) o;
                    append(elementName, x);
                } else {
                    append(elementName, o);
                }
            }
            unindent().closeTag(wrappingElementName).newLine();
        }
        return this;
    }

    private XMLBuilder openTag(String elementName) {
        if (this.format && this.onNewLine) {
            for (int i = 0; i < this.indentLevel; i++) {
                this.builder.append("    ");
            }
        }
        this.builder.append('<').append(elementName).append('>');
        this.onNewLine = false;
        return this;
    }

    private XMLBuilder closeTag(String elementName) {
        if (this.format && this.onNewLine) {
            for (int i = 0; i < this.indentLevel; i++) {
                this.builder.append("    ");
            }
        }
        this.builder.append("</").append(elementName).append('>');
        this.onNewLine = false;
        return this;
    }

    private XMLBuilder indent() {
        this.indentLevel++;
        return this;
    }

    private XMLBuilder unindent() {
        this.indentLevel--;
        return this;
    }

    private XMLBuilder newLine() {
        if (this.format) {
            this.builder.append('\n');
        }
        this.onNewLine = true;
        return this;
    }

    public XMLBuilder append(String elementName, int i) {
        openTag(elementName);
        this.builder.append(i);
        closeTag(elementName).newLine();
        return this;
    }

    public XMLBuilder append(String elementName, boolean b) {
        openTag(elementName);
        this.builder.append(b);
        closeTag(elementName).newLine();
        return this;
    }

    public XMLBuilder append(String elementName, String s) {
        if (s != null) {
            openTag(elementName);
            this.builder.append(escape(s));
            closeTag(elementName).newLine();
        }
        return this;
    }

    public XMLBuilder append(String elementName, Pattern p) {
        if (p != null) {
            append(elementName, p.pattern());
        }
        return this;
    }

    public XMLBuilder append(String elementName, Object o) {
        if (o != null) {
            append(elementName, String.valueOf(o));
        }
        return this;
    }

    private static final String escape(String string) {
        if (P_XML_SPECIAL_CHARACTERS.matcher(string).find()) {
            return string.replace(BeanFactory.FACTORY_BEAN_PREFIX, "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        }
        return string;
    }

    public void appendTo(Appendable a) throws IOException {
        a.append(this.builder);
    }

    public String toString() {
        return this.builder.toString();
    }
}
