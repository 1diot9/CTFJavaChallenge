package org.springframework.jmx.export.metadata;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jmx/export/metadata/AbstractJmxAttribute.class */
public abstract class AbstractJmxAttribute {
    private String description = "";
    private int currencyTimeLimit = -1;

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public void setCurrencyTimeLimit(int currencyTimeLimit) {
        this.currencyTimeLimit = currencyTimeLimit;
    }

    public int getCurrencyTimeLimit() {
        return this.currencyTimeLimit;
    }
}
