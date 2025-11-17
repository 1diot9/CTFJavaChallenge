package org.jooq.tools.json;

import java.io.IOException;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/json/ContentHandler.class */
public interface ContentHandler {
    void startJSON() throws ParseException, IOException;

    void endJSON() throws ParseException, IOException;

    boolean startObject() throws ParseException, IOException;

    boolean endObject() throws ParseException, IOException;

    boolean startObjectEntry(String str) throws ParseException, IOException;

    boolean endObjectEntry() throws ParseException, IOException;

    boolean startArray() throws ParseException, IOException;

    boolean endArray() throws ParseException, IOException;

    boolean primitive(Object obj) throws ParseException, IOException;
}
