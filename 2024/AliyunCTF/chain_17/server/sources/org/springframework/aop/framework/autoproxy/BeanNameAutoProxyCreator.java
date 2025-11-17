package org.springframework.aop.framework.autoproxy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.springframework.aop.TargetSource;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.PatternMatchUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/autoproxy/BeanNameAutoProxyCreator.class */
public class BeanNameAutoProxyCreator extends AbstractAutoProxyCreator {
    private static final String[] NO_ALIASES = new String[0];

    @Nullable
    private List<String> beanNames;

    public void setBeanNames(String... beanNames) {
        Assert.notEmpty(beanNames, "'beanNames' must not be empty");
        this.beanNames = new ArrayList(beanNames.length);
        for (String mappedName : beanNames) {
            this.beanNames.add(mappedName.strip());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator
    public TargetSource getCustomTargetSource(Class<?> beanClass, String beanName) {
        if (isSupportedBeanName(beanClass, beanName)) {
            return super.getCustomTargetSource(beanClass, beanName);
        }
        return null;
    }

    @Override // org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator
    @Nullable
    protected Object[] getAdvicesAndAdvisorsForBean(Class<?> beanClass, String beanName, @Nullable TargetSource targetSource) {
        return isSupportedBeanName(beanClass, beanName) ? PROXY_WITHOUT_ADDITIONAL_INTERCEPTORS : DO_NOT_PROXY;
    }

    private boolean isSupportedBeanName(Class<?> beanClass, String beanName) {
        if (this.beanNames != null) {
            boolean isFactoryBean = FactoryBean.class.isAssignableFrom(beanClass);
            Iterator<String> it = this.beanNames.iterator();
            while (it.hasNext()) {
                String mappedName = it.next();
                if (isFactoryBean) {
                    if (mappedName.startsWith(BeanFactory.FACTORY_BEAN_PREFIX)) {
                        mappedName = mappedName.substring(BeanFactory.FACTORY_BEAN_PREFIX.length());
                    } else {
                        continue;
                    }
                }
                if (isMatch(beanName, mappedName)) {
                    return true;
                }
            }
            BeanFactory beanFactory = getBeanFactory();
            String[] aliases = beanFactory != null ? beanFactory.getAliases(beanName) : NO_ALIASES;
            for (String alias : aliases) {
                Iterator<String> it2 = this.beanNames.iterator();
                while (it2.hasNext()) {
                    if (isMatch(alias, it2.next())) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    protected boolean isMatch(String beanName, String mappedName) {
        return PatternMatchUtils.simpleMatch(mappedName, beanName);
    }
}
