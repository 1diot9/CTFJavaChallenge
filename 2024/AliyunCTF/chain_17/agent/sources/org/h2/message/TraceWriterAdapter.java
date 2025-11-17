package org.h2.message;

import cn.hutool.core.text.CharSequenceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/message/TraceWriterAdapter.class */
public class TraceWriterAdapter implements TraceWriter {
    private String name;
    private final Logger logger = LoggerFactory.getLogger("h2database");

    @Override // org.h2.message.TraceWriter
    public void setName(String str) {
        this.name = str;
    }

    @Override // org.h2.message.TraceWriter
    public boolean isEnabled(int i) {
        switch (i) {
            case 1:
                return this.logger.isErrorEnabled();
            case 2:
                return this.logger.isInfoEnabled();
            case 3:
                return this.logger.isDebugEnabled();
            default:
                return false;
        }
    }

    @Override // org.h2.message.TraceWriter
    public void write(int i, int i2, String str, Throwable th) {
        write(i, Trace.MODULE_NAMES[i2], str, th);
    }

    @Override // org.h2.message.TraceWriter
    public void write(int i, String str, String str2, Throwable th) {
        String str3;
        if (isEnabled(i)) {
            if (this.name != null) {
                str3 = this.name + ":" + str + CharSequenceUtil.SPACE + str2;
            } else {
                str3 = str + CharSequenceUtil.SPACE + str2;
            }
            switch (i) {
                case 1:
                    this.logger.error(str3, th);
                    return;
                case 2:
                    this.logger.info(str3, th);
                    return;
                case 3:
                    this.logger.debug(str3, th);
                    return;
                default:
                    return;
            }
        }
    }
}
