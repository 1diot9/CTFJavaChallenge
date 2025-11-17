package org.springframework.beans.factory.xml;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/xml/DefaultBeanDefinitionDocumentReader.class */
public class DefaultBeanDefinitionDocumentReader implements BeanDefinitionDocumentReader {
    public static final String BEAN_ELEMENT = "bean";
    public static final String NESTED_BEANS_ELEMENT = "beans";
    public static final String ALIAS_ELEMENT = "alias";
    public static final String NAME_ATTRIBUTE = "name";
    public static final String ALIAS_ATTRIBUTE = "alias";
    public static final String IMPORT_ELEMENT = "import";
    public static final String RESOURCE_ATTRIBUTE = "resource";
    public static final String PROFILE_ATTRIBUTE = "profile";
    protected final Log logger = LogFactory.getLog(getClass());

    @Nullable
    private XmlReaderContext readerContext;

    @Nullable
    private BeanDefinitionParserDelegate delegate;

    @Override // org.springframework.beans.factory.xml.BeanDefinitionDocumentReader
    public void registerBeanDefinitions(Document doc, XmlReaderContext readerContext) {
        this.readerContext = readerContext;
        doRegisterBeanDefinitions(doc.getDocumentElement());
    }

    protected final XmlReaderContext getReaderContext() {
        Assert.state(this.readerContext != null, "No XmlReaderContext available");
        return this.readerContext;
    }

    @Nullable
    protected Object extractSource(Element ele) {
        return getReaderContext().extractSource(ele);
    }

    protected void doRegisterBeanDefinitions(Element root) {
        BeanDefinitionParserDelegate parent = this.delegate;
        this.delegate = createDelegate(getReaderContext(), root, parent);
        if (this.delegate.isDefaultNamespace(root)) {
            String profileSpec = root.getAttribute(PROFILE_ATTRIBUTE);
            if (StringUtils.hasText(profileSpec)) {
                String[] specifiedProfiles = StringUtils.tokenizeToStringArray(profileSpec, BeanDefinitionParserDelegate.MULTI_VALUE_ATTRIBUTE_DELIMITERS);
                if (!getReaderContext().getEnvironment().acceptsProfiles(specifiedProfiles)) {
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug("Skipped XML bean definition file due to specified profiles [" + profileSpec + "] not matching: " + getReaderContext().getResource());
                        return;
                    }
                    return;
                }
            }
        }
        preProcessXml(root);
        parseBeanDefinitions(root, this.delegate);
        postProcessXml(root);
        this.delegate = parent;
    }

    protected BeanDefinitionParserDelegate createDelegate(XmlReaderContext readerContext, Element root, @Nullable BeanDefinitionParserDelegate parentDelegate) {
        BeanDefinitionParserDelegate delegate = new BeanDefinitionParserDelegate(readerContext);
        delegate.initDefaults(root, parentDelegate);
        return delegate;
    }

    protected void parseBeanDefinitions(Element root, BeanDefinitionParserDelegate delegate) {
        if (delegate.isDefaultNamespace(root)) {
            NodeList nl = root.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                Node node = nl.item(i);
                if (node instanceof Element) {
                    Element ele = (Element) node;
                    if (delegate.isDefaultNamespace(ele)) {
                        parseDefaultElement(ele, delegate);
                    } else {
                        delegate.parseCustomElement(ele);
                    }
                }
            }
            return;
        }
        delegate.parseCustomElement(root);
    }

    private void parseDefaultElement(Element ele, BeanDefinitionParserDelegate delegate) {
        if (delegate.nodeNameEquals(ele, IMPORT_ELEMENT)) {
            importBeanDefinitionResource(ele);
            return;
        }
        if (delegate.nodeNameEquals(ele, "alias")) {
            processAliasRegistration(ele);
        } else if (delegate.nodeNameEquals(ele, "bean")) {
            processBeanDefinition(ele, delegate);
        } else if (delegate.nodeNameEquals(ele, NESTED_BEANS_ELEMENT)) {
            doRegisterBeanDefinitions(ele);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:16:0x0057 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:26:0x009a A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected void importBeanDefinitionResource(org.w3c.dom.Element r7) {
        /*
            Method dump skipped, instructions count: 340
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader.importBeanDefinitionResource(org.w3c.dom.Element):void");
    }

    protected void processAliasRegistration(Element ele) {
        String name = ele.getAttribute("name");
        String alias = ele.getAttribute("alias");
        boolean valid = true;
        if (!StringUtils.hasText(name)) {
            getReaderContext().error("Name must not be empty", ele);
            valid = false;
        }
        if (!StringUtils.hasText(alias)) {
            getReaderContext().error("Alias must not be empty", ele);
            valid = false;
        }
        if (valid) {
            try {
                getReaderContext().getRegistry().registerAlias(name, alias);
            } catch (Exception ex) {
                getReaderContext().error("Failed to register alias '" + alias + "' for bean with name '" + name + "'", ele, ex);
            }
            getReaderContext().fireAliasRegistered(name, alias, extractSource(ele));
        }
    }

    protected void processBeanDefinition(Element ele, BeanDefinitionParserDelegate delegate) {
        BeanDefinitionHolder bdHolder = delegate.parseBeanDefinitionElement(ele);
        if (bdHolder != null) {
            BeanDefinitionHolder bdHolder2 = delegate.decorateBeanDefinitionIfRequired(ele, bdHolder);
            try {
                BeanDefinitionReaderUtils.registerBeanDefinition(bdHolder2, getReaderContext().getRegistry());
            } catch (BeanDefinitionStoreException ex) {
                getReaderContext().error("Failed to register bean definition with name '" + bdHolder2.getBeanName() + "'", ele, ex);
            }
            getReaderContext().fireComponentRegistered(new BeanComponentDefinition(bdHolder2));
        }
    }

    protected void preProcessXml(Element root) {
    }

    protected void postProcessXml(Element root) {
    }
}
