package jakarta.el;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:jakarta/el/BeanNameResolver.class */
public abstract class BeanNameResolver {
    public boolean isNameResolved(String beanName) {
        return false;
    }

    public Object getBean(String beanName) {
        return null;
    }

    public void setBeanValue(String beanName, Object value) throws PropertyNotWritableException {
        throw new PropertyNotWritableException();
    }

    public boolean isReadOnly(String beanName) {
        return true;
    }

    public boolean canCreateBean(String beanName) {
        return false;
    }
}
