package cn.hutool.http.webservice;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/http/webservice/SoapProtocol.class */
public enum SoapProtocol {
    SOAP_1_1("SOAP 1.1 Protocol"),
    SOAP_1_2("SOAP 1.2 Protocol");

    private final String value;

    SoapProtocol(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
