package cn.hutool.extra.ssh;

import cn.hutool.core.lang.SimpleCache;
import cn.hutool.core.util.StrUtil;
import com.jcraft.jsch.Session;
import java.lang.invoke.SerializedLambda;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/ssh/JschSessionPool.class */
public enum JschSessionPool {
    INSTANCE;

    private final SimpleCache<String, Session> cache = new SimpleCache<>(new HashMap());

    JschSessionPool() {
    }

    private static /* synthetic */ Object $deserializeLambda$(SerializedLambda lambda) {
        String implMethodName = lambda.getImplMethodName();
        boolean z = -1;
        switch (implMethodName.hashCode()) {
            case -574507699:
                if (implMethodName.equals("lambda$getSession$59bcceb4$1")) {
                    z = true;
                    break;
                }
                break;
            case 793676584:
                if (implMethodName.equals("lambda$getSession$b6481cf0$1")) {
                    z = false;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/lang/func/Func0") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("()Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/extra/ssh/JschSessionPool") && lambda.getImplMethodSignature().equals("(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;)Lcom/jcraft/jsch/Session;")) {
                    String str = (String) lambda.getCapturedArg(0);
                    int intValue = ((Integer) lambda.getCapturedArg(1)).intValue();
                    String str2 = (String) lambda.getCapturedArg(2);
                    String str3 = (String) lambda.getCapturedArg(3);
                    return () -> {
                        return JschUtil.openSession(str, intValue, str2, str3);
                    };
                }
                break;
            case true:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/lang/func/Func0") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("()Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/extra/ssh/JschSessionPool") && lambda.getImplMethodSignature().equals("(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;[B)Lcom/jcraft/jsch/Session;")) {
                    String str4 = (String) lambda.getCapturedArg(0);
                    int intValue2 = ((Integer) lambda.getCapturedArg(1)).intValue();
                    String str5 = (String) lambda.getCapturedArg(2);
                    String str6 = (String) lambda.getCapturedArg(3);
                    byte[] bArr = (byte[]) lambda.getCapturedArg(4);
                    return () -> {
                        return JschUtil.openSession(str4, intValue2, str5, str6, bArr);
                    };
                }
                break;
        }
        throw new IllegalArgumentException("Invalid lambda deserialization");
    }

    public Session get(String key) {
        return this.cache.get(key);
    }

    public Session getSession(String sshHost, int sshPort, String sshUser, String sshPass) {
        String key = StrUtil.format("{}@{}:{}", sshUser, sshHost, Integer.valueOf(sshPort));
        return this.cache.get(key, (v0) -> {
            return v0.isConnected();
        }, () -> {
            return JschUtil.openSession(sshHost, sshPort, sshUser, sshPass);
        });
    }

    public Session getSession(String sshHost, int sshPort, String sshUser, String prvkey, byte[] passphrase) {
        String key = StrUtil.format("{}@{}:{}", sshUser, sshHost, Integer.valueOf(sshPort));
        return this.cache.get(key, (v0) -> {
            return v0.isConnected();
        }, () -> {
            return JschUtil.openSession(sshHost, sshPort, sshUser, prvkey, passphrase);
        });
    }

    public void put(String key, Session session) {
        this.cache.put(key, session);
    }

    public void close(String key) {
        Session session = get(key);
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
        this.cache.remove(key);
    }

    public void remove(Session session) {
        if (null != session) {
            Iterator<Map.Entry<String, Session>> iterator = this.cache.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Session> entry = iterator.next();
                if (session.equals(entry.getValue())) {
                    iterator.remove();
                    return;
                }
            }
        }
    }

    public void closeAll() {
        Iterator<Map.Entry<String, Session>> it = this.cache.iterator();
        while (it.hasNext()) {
            Map.Entry<String, Session> entry = it.next();
            Session session = entry.getValue();
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
        this.cache.clear();
    }
}
