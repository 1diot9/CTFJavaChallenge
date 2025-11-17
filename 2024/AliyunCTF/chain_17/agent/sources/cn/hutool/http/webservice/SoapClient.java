package cn.hutool.http.webservice;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.http.HttpBase;
import cn.hutool.http.HttpGlobalConfig;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/http/webservice/SoapClient.class */
public class SoapClient extends HttpBase<SoapClient> {
    private static final String CONTENT_TYPE_SOAP11_TEXT_XML = "text/xml;charset=";
    private static final String CONTENT_TYPE_SOAP12_SOAP_XML = "application/soap+xml;charset=";
    private String url;
    private int connectionTimeout;
    private int readTimeout;
    private MessageFactory factory;
    private SOAPMessage message;
    private SOAPBodyElement methodEle;
    private final String namespaceURI;
    private final SoapProtocol protocol;

    public static SoapClient create(String url) {
        return new SoapClient(url);
    }

    public static SoapClient create(String url, SoapProtocol protocol) {
        return new SoapClient(url, protocol);
    }

    public static SoapClient create(String url, SoapProtocol protocol, String namespaceURI) {
        return new SoapClient(url, protocol, namespaceURI);
    }

    public SoapClient(String url) {
        this(url, SoapProtocol.SOAP_1_1);
    }

    public SoapClient(String url, SoapProtocol protocol) {
        this(url, protocol, null);
    }

    public SoapClient(String url, SoapProtocol protocol, String namespaceURI) {
        this.connectionTimeout = HttpGlobalConfig.getTimeout();
        this.readTimeout = HttpGlobalConfig.getTimeout();
        this.url = url;
        this.namespaceURI = namespaceURI;
        this.protocol = protocol;
        init(protocol);
    }

    public SoapClient init(SoapProtocol protocol) {
        try {
            this.factory = MessageFactory.newInstance(protocol.getValue());
            this.message = this.factory.createMessage();
            return this;
        } catch (SOAPException e) {
            throw new SoapRuntimeException((Throwable) e);
        }
    }

    public SoapClient reset() {
        try {
            this.message = this.factory.createMessage();
            this.methodEle = null;
            return this;
        } catch (SOAPException e) {
            throw new SoapRuntimeException((Throwable) e);
        }
    }

    public SoapClient setCharset(Charset charset) {
        return charset(charset);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.http.HttpBase
    public SoapClient charset(Charset charset) {
        super.charset(charset);
        try {
            this.message.setProperty("javax.xml.soap.character-set-encoding", charset());
            this.message.setProperty("javax.xml.soap.write-xml-declaration", "true");
        } catch (SOAPException e) {
        }
        return this;
    }

    public SoapClient setUrl(String url) {
        this.url = url;
        return this;
    }

    public SOAPHeaderElement addSOAPHeader(QName name, String actorURI, String roleUri, Boolean mustUnderstand, Boolean relay) {
        SOAPHeaderElement ele = addSOAPHeader(name);
        try {
            if (StrUtil.isNotBlank(roleUri)) {
                ele.setRole(roleUri);
            }
            if (null != relay) {
                ele.setRelay(relay.booleanValue());
            }
            if (StrUtil.isNotBlank(actorURI)) {
                ele.setActor(actorURI);
            }
            if (null != mustUnderstand) {
                ele.setMustUnderstand(mustUnderstand.booleanValue());
            }
            return ele;
        } catch (SOAPException e) {
            throw new SoapRuntimeException((Throwable) e);
        }
    }

    public SOAPHeaderElement addSOAPHeader(String localName) {
        return addSOAPHeader(new QName(localName));
    }

    public SOAPHeaderElement addSOAPHeader(String localName, String value) {
        SOAPHeaderElement soapHeaderElement = addSOAPHeader(localName);
        soapHeaderElement.setTextContent(value);
        return soapHeaderElement;
    }

    public SOAPHeaderElement addSOAPHeader(QName name) {
        try {
            SOAPHeaderElement ele = this.message.getSOAPHeader().addHeaderElement(name);
            return ele;
        } catch (SOAPException e) {
            throw new SoapRuntimeException((Throwable) e);
        }
    }

    public SoapClient setMethod(Name name, Map<String, Object> params, boolean useMethodPrefix) {
        return setMethod(new QName(name.getURI(), name.getLocalName(), name.getPrefix()), params, useMethodPrefix);
    }

    public SoapClient setMethod(QName name, Map<String, Object> params, boolean useMethodPrefix) {
        setMethod(name);
        String prefix = useMethodPrefix ? name.getPrefix() : null;
        SOAPBodyElement methodEle = this.methodEle;
        Iterator it = MapUtil.wrap(params).iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = (Map.Entry) it.next();
            setParam(methodEle, entry.getKey(), entry.getValue(), prefix);
        }
        return this;
    }

    public SoapClient setMethod(String methodName) {
        return setMethod(methodName, (String) ObjectUtil.defaultIfNull(this.namespaceURI, ""));
    }

    public SoapClient setMethod(String methodName, String namespaceURI) {
        QName qName;
        List<String> methodNameList = StrUtil.split((CharSequence) methodName, ':');
        if (2 == methodNameList.size()) {
            qName = new QName(namespaceURI, methodNameList.get(1), methodNameList.get(0));
        } else {
            qName = new QName(namespaceURI, methodName);
        }
        return setMethod(qName);
    }

    public SoapClient setMethod(QName name) {
        try {
            this.methodEle = this.message.getSOAPBody().addBodyElement(name);
            return this;
        } catch (SOAPException e) {
            throw new SoapRuntimeException((Throwable) e);
        }
    }

    public SoapClient setParam(String name, Object value) {
        return setParam(name, value, true);
    }

    public SoapClient setParam(String name, Object value, boolean useMethodPrefix) {
        setParam(this.methodEle, name, value, useMethodPrefix ? this.methodEle.getPrefix() : null);
        return this;
    }

    public SoapClient setParams(Map<String, Object> params) {
        return setParams(params, true);
    }

    public SoapClient setParams(Map<String, Object> params, boolean useMethodPrefix) {
        Iterator it = MapUtil.wrap(params).iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = (Map.Entry) it.next();
            setParam(entry.getKey(), entry.getValue(), useMethodPrefix);
        }
        return this;
    }

    public SOAPBodyElement getMethodEle() {
        return this.methodEle;
    }

    public SOAPMessage getMessage() {
        return this.message;
    }

    public String getMsgStr(boolean pretty) {
        return SoapUtil.toString(this.message, pretty, this.charset);
    }

    public SoapClient write(OutputStream out) {
        try {
            this.message.writeTo(out);
            return this;
        } catch (SOAPException | IOException e) {
            throw new SoapRuntimeException((Throwable) e);
        }
    }

    public SoapClient timeout(int milliseconds) {
        setConnectionTimeout(milliseconds);
        setReadTimeout(milliseconds);
        return this;
    }

    public SoapClient setConnectionTimeout(int milliseconds) {
        this.connectionTimeout = milliseconds;
        return this;
    }

    public SoapClient setReadTimeout(int milliseconds) {
        this.readTimeout = milliseconds;
        return this;
    }

    public SOAPMessage sendForMessage() {
        HttpResponse res = sendForResponse();
        MimeHeaders headers = new MimeHeaders();
        for (Map.Entry<String, List<String>> entry : res.headers().entrySet()) {
            if (StrUtil.isNotEmpty(entry.getKey())) {
                headers.setHeader(entry.getKey(), (String) CollUtil.get(entry.getValue(), 0));
            }
        }
        try {
            try {
                SOAPMessage createMessage = this.factory.createMessage(headers, res.bodyStream());
                IoUtil.close((Closeable) res);
                return createMessage;
            } catch (IOException | SOAPException e) {
                throw new SoapRuntimeException(e);
            }
        } catch (Throwable th) {
            IoUtil.close((Closeable) res);
            throw th;
        }
    }

    public String send() {
        return send(false);
    }

    public String send(boolean pretty) {
        String body = sendForResponse().body();
        return pretty ? XmlUtil.format(body) : body;
    }

    public HttpResponse sendForResponse() {
        return HttpRequest.post(this.url).setFollowRedirects(true).setConnectionTimeout(this.connectionTimeout).setReadTimeout(this.readTimeout).contentType(getXmlContentType()).header(headers()).body(getMsgStr(false)).executeAsync();
    }

    private String getXmlContentType() {
        switch (this.protocol) {
            case SOAP_1_1:
                return CONTENT_TYPE_SOAP11_TEXT_XML.concat(this.charset.toString());
            case SOAP_1_2:
                return CONTENT_TYPE_SOAP12_SOAP_XML.concat(this.charset.toString());
            default:
                throw new SoapRuntimeException("Unsupported protocol: {}", this.protocol);
        }
    }

    private static SOAPElement setParam(SOAPElement ele, String name, Object value, String prefix) {
        SOAPElement childEle;
        try {
            if (StrUtil.isNotBlank(prefix)) {
                childEle = ele.addChildElement(name, prefix);
            } else {
                childEle = ele.addChildElement(name);
            }
            if (null != value) {
                if (value instanceof SOAPElement) {
                    try {
                        ele.addChildElement((SOAPElement) value);
                    } catch (SOAPException e) {
                        throw new SoapRuntimeException((Throwable) e);
                    }
                } else if (value instanceof Map) {
                    for (Object obj : ((Map) value).entrySet()) {
                        Map.Entry entry = (Map.Entry) obj;
                        setParam(childEle, entry.getKey().toString(), entry.getValue(), prefix);
                    }
                } else {
                    childEle.setValue(value.toString());
                }
            }
            return childEle;
        } catch (SOAPException e2) {
            throw new SoapRuntimeException((Throwable) e2);
        }
    }
}
