package org.springframework.boot.jdbc;

import java.sql.Wrapper;
import javax.sql.DataSource;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.jdbc.datasource.DelegatingDataSource;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jdbc/DataSourceUnwrapper.class */
public final class DataSourceUnwrapper {
    private static final boolean DELEGATING_DATA_SOURCE_PRESENT = ClassUtils.isPresent("org.springframework.jdbc.datasource.DelegatingDataSource", DataSourceUnwrapper.class.getClassLoader());

    private DataSourceUnwrapper() {
    }

    public static <I, T extends I> T unwrap(DataSource dataSource, Class<I> cls, Class<T> cls2) {
        DataSource targetDataSource;
        if (cls2.isInstance(dataSource)) {
            return cls2.cast(dataSource);
        }
        Object safeUnwrap = safeUnwrap(dataSource, cls);
        if (safeUnwrap != null && cls.isAssignableFrom(cls2)) {
            return cls2.cast(safeUnwrap);
        }
        if (DELEGATING_DATA_SOURCE_PRESENT && (targetDataSource = DelegatingDataSourceUnwrapper.getTargetDataSource(dataSource)) != null) {
            return (T) unwrap(targetDataSource, cls, cls2);
        }
        if (AopUtils.isAopProxy(dataSource)) {
            Object singletonTarget = AopProxyUtils.getSingletonTarget(dataSource);
            if (singletonTarget instanceof DataSource) {
                return (T) unwrap((DataSource) singletonTarget, cls, cls2);
            }
            return null;
        }
        return null;
    }

    public static <T> T unwrap(DataSource dataSource, Class<T> cls) {
        return (T) unwrap(dataSource, cls, cls);
    }

    private static <S> S safeUnwrap(Wrapper wrapper, Class<S> cls) {
        try {
            if (cls.isInterface() && wrapper.isWrapperFor(cls)) {
                return (S) wrapper.unwrap(cls);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jdbc/DataSourceUnwrapper$DelegatingDataSourceUnwrapper.class */
    public static final class DelegatingDataSourceUnwrapper {
        private DelegatingDataSourceUnwrapper() {
        }

        private static DataSource getTargetDataSource(DataSource dataSource) {
            if (dataSource instanceof DelegatingDataSource) {
                DelegatingDataSource delegatingDataSource = (DelegatingDataSource) dataSource;
                return delegatingDataSource.getTargetDataSource();
            }
            return null;
        }
    }
}
