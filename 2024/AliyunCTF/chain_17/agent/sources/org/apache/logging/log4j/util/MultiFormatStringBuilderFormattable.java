package org.apache.logging.log4j.util;

import org.apache.logging.log4j.message.MultiformatMessage;

/* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/util/MultiFormatStringBuilderFormattable.class */
public interface MultiFormatStringBuilderFormattable extends MultiformatMessage, StringBuilderFormattable {
    void formatTo(String[] formats, StringBuilder buffer);
}
