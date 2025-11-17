package cn.hutool.cron.task;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.cron.CronException;
import java.lang.reflect.Method;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/cron/task/InvokeTask.class */
public class InvokeTask implements Task {
    private final Object obj;
    private final Method method;

    public InvokeTask(String classNameWithMethodName) {
        int splitIndex = classNameWithMethodName.lastIndexOf(35);
        splitIndex = splitIndex <= 0 ? classNameWithMethodName.lastIndexOf(46) : splitIndex;
        if (splitIndex <= 0) {
            throw new UtilException("Invalid classNameWithMethodName [{}]!", classNameWithMethodName);
        }
        String className = classNameWithMethodName.substring(0, splitIndex);
        if (StrUtil.isBlank(className)) {
            throw new IllegalArgumentException("Class name is blank !");
        }
        Class<?> clazz = ClassLoaderUtil.loadClass(className);
        if (null == clazz) {
            throw new IllegalArgumentException("Load class with name of [" + className + "] fail !");
        }
        this.obj = ReflectUtil.newInstanceIfPossible(clazz);
        String methodName = classNameWithMethodName.substring(splitIndex + 1);
        if (StrUtil.isBlank(methodName)) {
            throw new IllegalArgumentException("Method name is blank !");
        }
        this.method = ClassUtil.getPublicMethod(clazz, methodName, new Class[0]);
        if (null == this.method) {
            throw new IllegalArgumentException("No method with name of [" + methodName + "] !");
        }
    }

    @Override // cn.hutool.cron.task.Task
    public void execute() {
        try {
            ReflectUtil.invoke(this.obj, this.method, new Object[0]);
        } catch (UtilException e) {
            throw new CronException(e.getCause());
        }
    }
}
