package org.springframework.web.servlet.view.xslt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContextException;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.SimpleTransformErrorListener;
import org.springframework.util.xml.TransformerUtils;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.util.WebUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/view/xslt/XsltView.class */
public class XsltView extends AbstractUrlBasedView {

    @Nullable
    private Class<? extends TransformerFactory> transformerFactoryClass;

    @Nullable
    private String sourceKey;

    @Nullable
    private URIResolver uriResolver;

    @Nullable
    private Properties outputProperties;

    @Nullable
    private TransformerFactory transformerFactory;

    @Nullable
    private Templates cachedTemplates;
    private ErrorListener errorListener = new SimpleTransformErrorListener(this.logger);
    private boolean indent = true;
    private boolean cacheTemplates = true;

    public void setTransformerFactoryClass(Class<? extends TransformerFactory> transformerFactoryClass) {
        this.transformerFactoryClass = transformerFactoryClass;
    }

    public void setSourceKey(String sourceKey) {
        this.sourceKey = sourceKey;
    }

    public void setUriResolver(URIResolver uriResolver) {
        this.uriResolver = uriResolver;
    }

    public void setErrorListener(@Nullable ErrorListener errorListener) {
        this.errorListener = errorListener != null ? errorListener : new SimpleTransformErrorListener(this.logger);
    }

    public void setIndent(boolean indent) {
        this.indent = indent;
    }

    public void setOutputProperties(Properties outputProperties) {
        this.outputProperties = outputProperties;
    }

    public void setCacheTemplates(boolean cacheTemplates) {
        this.cacheTemplates = cacheTemplates;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.context.support.ApplicationObjectSupport
    public void initApplicationContext() throws BeansException {
        this.transformerFactory = newTransformerFactory(this.transformerFactoryClass);
        this.transformerFactory.setErrorListener(this.errorListener);
        if (this.uriResolver != null) {
            this.transformerFactory.setURIResolver(this.uriResolver);
        }
        if (this.cacheTemplates) {
            this.cachedTemplates = loadTemplates();
        }
    }

    protected TransformerFactory newTransformerFactory(@Nullable Class<? extends TransformerFactory> transformerFactoryClass) {
        if (transformerFactoryClass != null) {
            try {
                return (TransformerFactory) ReflectionUtils.accessibleConstructor(transformerFactoryClass, new Class[0]).newInstance(new Object[0]);
            } catch (Exception ex) {
                throw new TransformerFactoryConfigurationError(ex, "Could not instantiate TransformerFactory");
            }
        }
        return TransformerFactory.newInstance();
    }

    protected final TransformerFactory getTransformerFactory() {
        Assert.state(this.transformerFactory != null, "No TransformerFactory available");
        return this.transformerFactory;
    }

    @Override // org.springframework.web.servlet.view.AbstractView
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Templates templates = this.cachedTemplates;
        if (templates == null) {
            templates = loadTemplates();
        }
        Transformer transformer = createTransformer(templates);
        configureTransformer(model, response, transformer);
        configureResponse(model, response, transformer);
        try {
            Source source = locateSource(model);
            if (source == null) {
                throw new IllegalArgumentException("Unable to locate Source object in model: " + model);
            }
            transformer.transform(source, createResult(response));
            closeSourceIfNecessary(source);
        } catch (Throwable th) {
            closeSourceIfNecessary(null);
            throw th;
        }
    }

    protected Result createResult(HttpServletResponse response) throws Exception {
        return new StreamResult(response.getOutputStream());
    }

    @Nullable
    protected Source locateSource(Map<String, Object> model) throws Exception {
        if (this.sourceKey != null) {
            return convertSource(model.get(this.sourceKey));
        }
        Object source = CollectionUtils.findValueOfType((Collection<?>) model.values(), getSourceTypes());
        if (source != null) {
            return convertSource(source);
        }
        return null;
    }

    protected Class<?>[] getSourceTypes() {
        return new Class[]{Source.class, Document.class, Node.class, Reader.class, InputStream.class, Resource.class};
    }

    protected Source convertSource(Object sourceObject) throws Exception {
        if (sourceObject instanceof Source) {
            Source source = (Source) sourceObject;
            return source;
        }
        if (sourceObject instanceof Document) {
            Document document = (Document) sourceObject;
            return new DOMSource(document.getDocumentElement());
        }
        if (sourceObject instanceof Node) {
            Node node = (Node) sourceObject;
            return new DOMSource(node);
        }
        if (sourceObject instanceof Reader) {
            Reader reader = (Reader) sourceObject;
            return new StreamSource(reader);
        }
        if (sourceObject instanceof InputStream) {
            InputStream inputStream = (InputStream) sourceObject;
            return new StreamSource(inputStream);
        }
        if (sourceObject instanceof Resource) {
            Resource resource = (Resource) sourceObject;
            return new StreamSource(resource.getInputStream(), resource.getURI().toASCIIString());
        }
        throw new IllegalArgumentException("Value '" + sourceObject + "' cannot be converted to XSLT Source");
    }

    protected void configureTransformer(Map<String, Object> model, HttpServletResponse response, Transformer transformer) {
        copyModelParameters(model, transformer);
        copyOutputProperties(transformer);
        configureIndentation(transformer);
    }

    protected final void configureIndentation(Transformer transformer) {
        if (this.indent) {
            TransformerUtils.enableIndenting(transformer);
        } else {
            TransformerUtils.disableIndenting(transformer);
        }
    }

    protected final void copyOutputProperties(Transformer transformer) {
        if (this.outputProperties != null) {
            Enumeration<?> en = this.outputProperties.propertyNames();
            while (en.hasMoreElements()) {
                String name = (String) en.nextElement();
                transformer.setOutputProperty(name, this.outputProperties.getProperty(name));
            }
        }
    }

    protected final void copyModelParameters(Map<String, Object> model, Transformer transformer) {
        Objects.requireNonNull(transformer);
        model.forEach(transformer::setParameter);
    }

    protected void configureResponse(Map<String, Object> model, HttpServletResponse response, Transformer transformer) {
        String contentType = getContentType();
        String mediaType = transformer.getOutputProperty("media-type");
        String encoding = transformer.getOutputProperty("encoding");
        if (StringUtils.hasText(mediaType)) {
            contentType = mediaType;
        }
        if (StringUtils.hasText(encoding) && contentType != null && !contentType.toLowerCase().contains(WebUtils.CONTENT_TYPE_CHARSET_PREFIX)) {
            contentType = contentType + ";charset=" + encoding;
        }
        response.setContentType(contentType);
    }

    private Templates loadTemplates() throws ApplicationContextException {
        Source stylesheetSource = getStylesheetSource();
        try {
            try {
                Templates templates = getTransformerFactory().newTemplates(stylesheetSource);
                closeSourceIfNecessary(stylesheetSource);
                return templates;
            } catch (TransformerConfigurationException ex) {
                throw new ApplicationContextException("Can't load stylesheet from '" + getUrl() + "'", ex);
            }
        } catch (Throwable th) {
            closeSourceIfNecessary(stylesheetSource);
            throw th;
        }
    }

    protected Transformer createTransformer(Templates templates) throws TransformerConfigurationException {
        Transformer transformer = templates.newTransformer();
        if (this.uriResolver != null) {
            transformer.setURIResolver(this.uriResolver);
        }
        return transformer;
    }

    protected Source getStylesheetSource() {
        String url = getUrl();
        Assert.state(url != null, "'url' not set");
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Applying stylesheet [" + url + "]");
        }
        try {
            Resource resource = obtainApplicationContext().getResource(url);
            return new StreamSource(resource.getInputStream(), resource.getURI().toASCIIString());
        } catch (IOException ex) {
            throw new ApplicationContextException("Can't load XSLT stylesheet from '" + url + "'", ex);
        }
    }

    private void closeSourceIfNecessary(@Nullable Source source) {
        if (source instanceof StreamSource) {
            StreamSource streamSource = (StreamSource) source;
            if (streamSource.getReader() != null) {
                try {
                    streamSource.getReader().close();
                } catch (IOException e) {
                }
            }
            if (streamSource.getInputStream() != null) {
                try {
                    streamSource.getInputStream().close();
                } catch (IOException e2) {
                }
            }
        }
    }
}
