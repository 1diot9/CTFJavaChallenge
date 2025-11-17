package org.springframework.format.number.money;

import java.util.Locale;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import org.springframework.format.Formatter;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/number/money/CurrencyUnitFormatter.class */
public class CurrencyUnitFormatter implements Formatter<CurrencyUnit> {
    @Override // org.springframework.format.Printer
    public String print(CurrencyUnit object, Locale locale) {
        return object.getCurrencyCode();
    }

    @Override // org.springframework.format.Parser
    public CurrencyUnit parse(String text, Locale locale) {
        return Monetary.getCurrency(text, new String[0]);
    }
}
