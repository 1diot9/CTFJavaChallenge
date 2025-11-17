package com.fasterxml.jackson.databind.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Date;
import java.util.GregorianCalendar;

@Deprecated
/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/ISO8601DateFormat.class */
public class ISO8601DateFormat extends DateFormat {
    private static final long serialVersionUID = 1;

    public ISO8601DateFormat() {
        this.numberFormat = new DecimalFormat();
        this.calendar = new GregorianCalendar();
    }

    @Override // java.text.DateFormat
    public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
        toAppendTo.append(ISO8601Utils.format(date));
        return toAppendTo;
    }

    @Override // java.text.DateFormat
    public Date parse(String source, ParsePosition pos) {
        try {
            return ISO8601Utils.parse(source, pos);
        } catch (ParseException e) {
            return null;
        }
    }

    @Override // java.text.DateFormat
    public Date parse(String source) throws ParseException {
        return ISO8601Utils.parse(source, new ParsePosition(0));
    }

    @Override // java.text.DateFormat, java.text.Format
    public Object clone() {
        return this;
    }
}
