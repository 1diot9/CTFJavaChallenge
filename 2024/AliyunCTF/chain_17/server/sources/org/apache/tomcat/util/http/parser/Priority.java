package org.apache.tomcat.util.http.parser;

import ch.qos.logback.core.rolling.helper.IntegerTokenConverter;
import java.io.IOException;
import java.io.Reader;
import org.apache.tomcat.util.http.parser.StructuredField;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/parser/Priority.class */
public class Priority {
    public static final int DEFAULT_URGENCY = 3;
    public static final boolean DEFAULT_INCREMENTAL = false;
    private int urgency = 3;
    private boolean incremental = false;

    public int getUrgency() {
        return this.urgency;
    }

    public void setUrgency(int urgency) {
        this.urgency = urgency;
    }

    public boolean getIncremental() {
        return this.incremental;
    }

    public void setIncremental(boolean incremental) {
        this.incremental = incremental;
    }

    public static Priority parsePriority(Reader input) throws IOException {
        Priority result = new Priority();
        StructuredField.SfDictionary dictionary = StructuredField.parseSfDictionary(input);
        StructuredField.SfListMember urgencyListMember = dictionary.getDictionaryMember("u");
        if (urgencyListMember instanceof StructuredField.SfInteger) {
            long urgency = ((StructuredField.SfInteger) urgencyListMember).getVaue().longValue();
            if (urgency > -1 && urgency < 8) {
                result.setUrgency((int) urgency);
            }
        }
        StructuredField.SfListMember incrementalListMember = dictionary.getDictionaryMember(IntegerTokenConverter.CONVERTER_KEY);
        if (incrementalListMember instanceof StructuredField.SfBoolean) {
            result.setIncremental(((StructuredField.SfBoolean) incrementalListMember).getVaue().booleanValue());
        }
        return result;
    }
}
