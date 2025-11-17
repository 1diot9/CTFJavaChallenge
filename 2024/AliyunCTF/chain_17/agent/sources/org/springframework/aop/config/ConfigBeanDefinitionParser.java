package org.springframework.aop.config;

import ch.qos.logback.classic.encoder.JsonEncoder;
import java.util.ArrayList;
import java.util.List;
import org.springframework.aop.aspectj.AspectJAfterAdvice;
import org.springframework.aop.aspectj.AspectJAfterReturningAdvice;
import org.springframework.aop.aspectj.AspectJAfterThrowingAdvice;
import org.springframework.aop.aspectj.AspectJAroundAdvice;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.AspectJMethodBeforeAdvice;
import org.springframework.aop.aspectj.AspectJPointcutAdvisor;
import org.springframework.aop.aspectj.DeclareParentsAdvisor;
import org.springframework.aop.support.DefaultBeanFactoryPointcutAdvisor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanReference;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanNameReference;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.parsing.ParseState;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/config/ConfigBeanDefinitionParser.class */
class ConfigBeanDefinitionParser implements BeanDefinitionParser {
    private static final String ASPECT = "aspect";
    private static final String EXPRESSION = "expression";
    private static final String ID = "id";
    private static final String POINTCUT = "pointcut";
    private static final String ADVICE_BEAN_NAME = "adviceBeanName";
    private static final String ADVISOR = "advisor";
    private static final String ADVICE_REF = "advice-ref";
    private static final String POINTCUT_REF = "pointcut-ref";
    private static final String REF = "ref";
    private static final String BEFORE = "before";
    private static final String DECLARE_PARENTS = "declare-parents";
    private static final String TYPE_PATTERN = "types-matching";
    private static final String DEFAULT_IMPL = "default-impl";
    private static final String DELEGATE_REF = "delegate-ref";
    private static final String IMPLEMENT_INTERFACE = "implement-interface";
    private static final String AFTER = "after";
    private static final String AFTER_RETURNING_ELEMENT = "after-returning";
    private static final String AFTER_THROWING_ELEMENT = "after-throwing";
    private static final String AROUND = "around";
    private static final String RETURNING = "returning";
    private static final String RETURNING_PROPERTY = "returningName";
    private static final String THROWING = "throwing";
    private static final String THROWING_PROPERTY = "throwingName";
    private static final String ARG_NAMES = "arg-names";
    private static final String ARG_NAMES_PROPERTY = "argumentNames";
    private static final String ASPECT_NAME_PROPERTY = "aspectName";
    private static final String DECLARATION_ORDER_PROPERTY = "declarationOrder";
    private static final String ORDER_PROPERTY = "order";
    private static final int METHOD_INDEX = 0;
    private static final int POINTCUT_INDEX = 1;
    private static final int ASPECT_INSTANCE_FACTORY_INDEX = 2;
    private final ParseState parseState = new ParseState();

    /* JADX WARN: Failed to find 'out' block for switch in B:5:0x005a. Please report as an issue. */
    @Override // org.springframework.beans.factory.xml.BeanDefinitionParser
    @Nullable
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        CompositeComponentDefinition compositeDef = new CompositeComponentDefinition(element.getTagName(), parserContext.extractSource(element));
        parserContext.pushContainingComponent(compositeDef);
        configureAutoProxyCreator(parserContext, element);
        List<Element> childElts = DomUtils.getChildElements(element);
        for (Element elt : childElts) {
            String localName = parserContext.getDelegate().getLocalName(elt);
            boolean z = -1;
            switch (localName.hashCode()) {
                case -1408297896:
                    if (localName.equals(ASPECT)) {
                        z = 2;
                        break;
                    }
                    break;
                case -1131323456:
                    if (localName.equals(ADVISOR)) {
                        z = true;
                        break;
                    }
                    break;
                case 466125490:
                    if (localName.equals(POINTCUT)) {
                        z = false;
                        break;
                    }
                    break;
            }
            switch (z) {
                case false:
                    parsePointcut(elt, parserContext);
                    break;
                case true:
                    parseAdvisor(elt, parserContext);
                    break;
                case true:
                    parseAspect(elt, parserContext);
                    break;
            }
        }
        parserContext.popAndRegisterContainingComponent();
        return null;
    }

    private void configureAutoProxyCreator(ParserContext parserContext, Element element) {
        AopNamespaceUtils.registerAspectJAutoProxyCreatorIfNecessary(parserContext, element);
    }

    private void parseAdvisor(Element advisorElement, ParserContext parserContext) {
        AbstractBeanDefinition advisorDef = createAdvisorBeanDefinition(advisorElement, parserContext);
        String id = advisorElement.getAttribute("id");
        try {
            this.parseState.push(new AdvisorEntry(id));
            String advisorBeanName = id;
            if (StringUtils.hasText(advisorBeanName)) {
                parserContext.getRegistry().registerBeanDefinition(advisorBeanName, advisorDef);
            } else {
                advisorBeanName = parserContext.getReaderContext().registerWithGeneratedName(advisorDef);
            }
            Object pointcut = parsePointcutProperty(advisorElement, parserContext);
            if (pointcut instanceof BeanDefinition) {
                BeanDefinition beanDefinition = (BeanDefinition) pointcut;
                advisorDef.getPropertyValues().add(POINTCUT, pointcut);
                parserContext.registerComponent(new AdvisorComponentDefinition(advisorBeanName, advisorDef, beanDefinition));
            } else if (pointcut instanceof String) {
                String beanName = (String) pointcut;
                advisorDef.getPropertyValues().add(POINTCUT, new RuntimeBeanReference(beanName));
                parserContext.registerComponent(new AdvisorComponentDefinition(advisorBeanName, advisorDef));
            }
        } finally {
            this.parseState.pop();
        }
    }

    private AbstractBeanDefinition createAdvisorBeanDefinition(Element advisorElement, ParserContext parserContext) {
        RootBeanDefinition advisorDefinition = new RootBeanDefinition((Class<?>) DefaultBeanFactoryPointcutAdvisor.class);
        advisorDefinition.setSource(parserContext.extractSource(advisorElement));
        String adviceRef = advisorElement.getAttribute(ADVICE_REF);
        if (!StringUtils.hasText(adviceRef)) {
            parserContext.getReaderContext().error("'advice-ref' attribute contains empty value.", advisorElement, this.parseState.snapshot());
        } else {
            advisorDefinition.getPropertyValues().add(ADVICE_BEAN_NAME, new RuntimeBeanNameReference(adviceRef));
        }
        if (advisorElement.hasAttribute("order")) {
            advisorDefinition.getPropertyValues().add("order", advisorElement.getAttribute("order"));
        }
        return advisorDefinition;
    }

    private void parseAspect(Element aspectElement, ParserContext parserContext) {
        String aspectId = aspectElement.getAttribute("id");
        String aspectName = aspectElement.getAttribute("ref");
        try {
            this.parseState.push(new AspectEntry(aspectId, aspectName));
            List<BeanDefinition> beanDefinitions = new ArrayList<>();
            List<BeanReference> beanReferences = new ArrayList<>();
            List<Element> declareParents = DomUtils.getChildElementsByTagName(aspectElement, DECLARE_PARENTS);
            for (Element declareParent : declareParents) {
                beanDefinitions.add(parseDeclareParents(declareParent, parserContext));
            }
            NodeList nodeList = aspectElement.getChildNodes();
            boolean adviceFoundAlready = false;
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (isAdviceNode(node, parserContext)) {
                    if (!adviceFoundAlready) {
                        adviceFoundAlready = true;
                        if (!StringUtils.hasText(aspectName)) {
                            parserContext.getReaderContext().error("<aspect> tag needs aspect bean reference via 'ref' attribute when declaring advices.", aspectElement, this.parseState.snapshot());
                            this.parseState.pop();
                            return;
                        }
                        beanReferences.add(new RuntimeBeanReference(aspectName));
                    }
                    AbstractBeanDefinition advisorDefinition = parseAdvice(aspectName, i, aspectElement, (Element) node, parserContext, beanDefinitions, beanReferences);
                    beanDefinitions.add(advisorDefinition);
                }
            }
            AspectComponentDefinition aspectComponentDefinition = createAspectComponentDefinition(aspectElement, aspectId, beanDefinitions, beanReferences, parserContext);
            parserContext.pushContainingComponent(aspectComponentDefinition);
            List<Element> pointcuts = DomUtils.getChildElementsByTagName(aspectElement, POINTCUT);
            for (Element pointcutElement : pointcuts) {
                parsePointcut(pointcutElement, parserContext);
            }
            parserContext.popAndRegisterContainingComponent();
            this.parseState.pop();
        } catch (Throwable th) {
            this.parseState.pop();
            throw th;
        }
    }

    private AspectComponentDefinition createAspectComponentDefinition(Element aspectElement, String aspectId, List<BeanDefinition> beanDefs, List<BeanReference> beanRefs, ParserContext parserContext) {
        BeanDefinition[] beanDefArray = (BeanDefinition[]) beanDefs.toArray(new BeanDefinition[0]);
        BeanReference[] beanRefArray = (BeanReference[]) beanRefs.toArray(new BeanReference[0]);
        Object source = parserContext.extractSource(aspectElement);
        return new AspectComponentDefinition(aspectId, beanDefArray, beanRefArray, source);
    }

    private boolean isAdviceNode(Node aNode, ParserContext parserContext) {
        if (!(aNode instanceof Element)) {
            return false;
        }
        String name = parserContext.getDelegate().getLocalName(aNode);
        return BEFORE.equals(name) || AFTER.equals(name) || AFTER_RETURNING_ELEMENT.equals(name) || AFTER_THROWING_ELEMENT.equals(name) || AROUND.equals(name);
    }

    private AbstractBeanDefinition parseDeclareParents(Element declareParentsElement, ParserContext parserContext) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition((Class<?>) DeclareParentsAdvisor.class);
        builder.addConstructorArgValue(declareParentsElement.getAttribute(IMPLEMENT_INTERFACE));
        builder.addConstructorArgValue(declareParentsElement.getAttribute(TYPE_PATTERN));
        String defaultImpl = declareParentsElement.getAttribute(DEFAULT_IMPL);
        String delegateRef = declareParentsElement.getAttribute(DELEGATE_REF);
        if (StringUtils.hasText(defaultImpl) && !StringUtils.hasText(delegateRef)) {
            builder.addConstructorArgValue(defaultImpl);
        } else if (StringUtils.hasText(delegateRef) && !StringUtils.hasText(defaultImpl)) {
            builder.addConstructorArgReference(delegateRef);
        } else {
            parserContext.getReaderContext().error("Exactly one of the default-impl or delegate-ref attributes must be specified", declareParentsElement, this.parseState.snapshot());
        }
        AbstractBeanDefinition definition = builder.getBeanDefinition();
        definition.setSource(parserContext.extractSource(declareParentsElement));
        parserContext.getReaderContext().registerWithGeneratedName(definition);
        return definition;
    }

    private AbstractBeanDefinition parseAdvice(String aspectName, int order, Element aspectElement, Element adviceElement, ParserContext parserContext, List<BeanDefinition> beanDefinitions, List<BeanReference> beanReferences) {
        try {
            this.parseState.push(new AdviceEntry(parserContext.getDelegate().getLocalName(adviceElement)));
            RootBeanDefinition methodDefinition = new RootBeanDefinition((Class<?>) MethodLocatingFactoryBean.class);
            methodDefinition.getPropertyValues().add("targetBeanName", aspectName);
            methodDefinition.getPropertyValues().add(JsonEncoder.METHOD_NAME_ATTR_NAME, adviceElement.getAttribute("method"));
            methodDefinition.setSynthetic(true);
            RootBeanDefinition aspectFactoryDef = new RootBeanDefinition((Class<?>) SimpleBeanFactoryAwareAspectInstanceFactory.class);
            aspectFactoryDef.getPropertyValues().add("aspectBeanName", aspectName);
            aspectFactoryDef.setSynthetic(true);
            AbstractBeanDefinition adviceDef = createAdviceDefinition(adviceElement, parserContext, aspectName, order, methodDefinition, aspectFactoryDef, beanDefinitions, beanReferences);
            RootBeanDefinition advisorDefinition = new RootBeanDefinition((Class<?>) AspectJPointcutAdvisor.class);
            advisorDefinition.setSource(parserContext.extractSource(adviceElement));
            advisorDefinition.getConstructorArgumentValues().addGenericArgumentValue(adviceDef);
            if (aspectElement.hasAttribute("order")) {
                advisorDefinition.getPropertyValues().add("order", aspectElement.getAttribute("order"));
            }
            parserContext.getReaderContext().registerWithGeneratedName(advisorDefinition);
            this.parseState.pop();
            return advisorDefinition;
        } catch (Throwable th) {
            this.parseState.pop();
            throw th;
        }
    }

    private AbstractBeanDefinition createAdviceDefinition(Element adviceElement, ParserContext parserContext, String aspectName, int order, RootBeanDefinition methodDef, RootBeanDefinition aspectFactoryDef, List<BeanDefinition> beanDefinitions, List<BeanReference> beanReferences) {
        RootBeanDefinition adviceDefinition = new RootBeanDefinition(getAdviceClass(adviceElement, parserContext));
        adviceDefinition.setSource(parserContext.extractSource(adviceElement));
        adviceDefinition.getPropertyValues().add(ASPECT_NAME_PROPERTY, aspectName);
        adviceDefinition.getPropertyValues().add(DECLARATION_ORDER_PROPERTY, Integer.valueOf(order));
        if (adviceElement.hasAttribute(RETURNING)) {
            adviceDefinition.getPropertyValues().add(RETURNING_PROPERTY, adviceElement.getAttribute(RETURNING));
        }
        if (adviceElement.hasAttribute(THROWING)) {
            adviceDefinition.getPropertyValues().add(THROWING_PROPERTY, adviceElement.getAttribute(THROWING));
        }
        if (adviceElement.hasAttribute(ARG_NAMES)) {
            adviceDefinition.getPropertyValues().add(ARG_NAMES_PROPERTY, adviceElement.getAttribute(ARG_NAMES));
        }
        ConstructorArgumentValues cav = adviceDefinition.getConstructorArgumentValues();
        cav.addIndexedArgumentValue(0, methodDef);
        Object pointcut = parsePointcutProperty(adviceElement, parserContext);
        if (pointcut instanceof BeanDefinition) {
            BeanDefinition beanDefinition = (BeanDefinition) pointcut;
            cav.addIndexedArgumentValue(1, pointcut);
            beanDefinitions.add(beanDefinition);
        } else if (pointcut instanceof String) {
            String beanName = (String) pointcut;
            RuntimeBeanReference pointcutRef = new RuntimeBeanReference(beanName);
            cav.addIndexedArgumentValue(1, pointcutRef);
            beanReferences.add(pointcutRef);
        }
        cav.addIndexedArgumentValue(2, aspectFactoryDef);
        return adviceDefinition;
    }

    private Class<?> getAdviceClass(Element adviceElement, ParserContext parserContext) {
        String elementName = parserContext.getDelegate().getLocalName(adviceElement);
        boolean z = -1;
        switch (elementName.hashCode()) {
            case -1409235507:
                if (elementName.equals(AROUND)) {
                    z = 4;
                    break;
                }
                break;
            case -1392885889:
                if (elementName.equals(BEFORE)) {
                    z = false;
                    break;
                }
                break;
            case 92734940:
                if (elementName.equals(AFTER)) {
                    z = true;
                    break;
                }
                break;
            case 153097229:
                if (elementName.equals(AFTER_THROWING_ELEMENT)) {
                    z = 3;
                    break;
                }
                break;
            case 779833857:
                if (elementName.equals(AFTER_RETURNING_ELEMENT)) {
                    z = 2;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                return AspectJMethodBeforeAdvice.class;
            case true:
                return AspectJAfterAdvice.class;
            case true:
                return AspectJAfterReturningAdvice.class;
            case true:
                return AspectJAfterThrowingAdvice.class;
            case true:
                return AspectJAroundAdvice.class;
            default:
                throw new IllegalArgumentException("Unknown advice kind [" + elementName + "].");
        }
    }

    private AbstractBeanDefinition parsePointcut(Element pointcutElement, ParserContext parserContext) {
        String id = pointcutElement.getAttribute("id");
        String expression = pointcutElement.getAttribute(EXPRESSION);
        try {
            this.parseState.push(new PointcutEntry(id));
            AbstractBeanDefinition pointcutDefinition = createPointcutDefinition(expression);
            pointcutDefinition.setSource(parserContext.extractSource(pointcutElement));
            String pointcutBeanName = id;
            if (StringUtils.hasText(pointcutBeanName)) {
                parserContext.getRegistry().registerBeanDefinition(pointcutBeanName, pointcutDefinition);
            } else {
                pointcutBeanName = parserContext.getReaderContext().registerWithGeneratedName(pointcutDefinition);
            }
            parserContext.registerComponent(new PointcutComponentDefinition(pointcutBeanName, pointcutDefinition, expression));
            this.parseState.pop();
            return pointcutDefinition;
        } catch (Throwable th) {
            this.parseState.pop();
            throw th;
        }
    }

    @Nullable
    private Object parsePointcutProperty(Element element, ParserContext parserContext) {
        if (element.hasAttribute(POINTCUT) && element.hasAttribute(POINTCUT_REF)) {
            parserContext.getReaderContext().error("Cannot define both 'pointcut' and 'pointcut-ref' on <advisor> tag.", element, this.parseState.snapshot());
            return null;
        }
        if (element.hasAttribute(POINTCUT)) {
            String expression = element.getAttribute(POINTCUT);
            AbstractBeanDefinition pointcutDefinition = createPointcutDefinition(expression);
            pointcutDefinition.setSource(parserContext.extractSource(element));
            return pointcutDefinition;
        }
        if (element.hasAttribute(POINTCUT_REF)) {
            String pointcutRef = element.getAttribute(POINTCUT_REF);
            if (!StringUtils.hasText(pointcutRef)) {
                parserContext.getReaderContext().error("'pointcut-ref' attribute contains empty value.", element, this.parseState.snapshot());
                return null;
            }
            return pointcutRef;
        }
        parserContext.getReaderContext().error("Must define one of 'pointcut' or 'pointcut-ref' on <advisor> tag.", element, this.parseState.snapshot());
        return null;
    }

    protected AbstractBeanDefinition createPointcutDefinition(String expression) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition((Class<?>) AspectJExpressionPointcut.class);
        beanDefinition.setScope("prototype");
        beanDefinition.setSynthetic(true);
        beanDefinition.getPropertyValues().add(EXPRESSION, expression);
        return beanDefinition;
    }
}
