package org.springframework.format.datetime.standard;

import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/datetime/standard/DateTimeFormatterFactoryBean.class */
public class DateTimeFormatterFactoryBean extends DateTimeFormatterFactory implements FactoryBean<DateTimeFormatter>, InitializingBean {

    @Nullable
    private DateTimeFormatter dateTimeFormatter;

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        this.dateTimeFormatter = createDateTimeFormatter();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.beans.factory.FactoryBean
    @Nullable
    public DateTimeFormatter getObject() {
        return this.dateTimeFormatter;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public Class<?> getObjectType() {
        return DateTimeFormatter.class;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public boolean isSingleton() {
        return true;
    }
}
