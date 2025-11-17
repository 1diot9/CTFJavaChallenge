package org.h2.util;

import ch.qos.logback.core.net.ssl.SSL;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/MathUtils.class */
public class MathUtils {
    static SecureRandom secureRandom;
    static volatile boolean seeded;

    private MathUtils() {
    }

    public static int roundUpInt(int i, int i2) {
        return ((i + i2) - 1) & (-i2);
    }

    public static long roundUpLong(long j, long j2) {
        return ((j + j2) - 1) & (-j2);
    }

    private static synchronized SecureRandom getSecureRandom() {
        if (secureRandom != null) {
            return secureRandom;
        }
        try {
            secureRandom = SecureRandom.getInstance(SSL.DEFAULT_SECURE_RANDOM_ALGORITHM);
            Runnable runnable = () -> {
                try {
                    byte[] generateSeed = SecureRandom.getInstance(SSL.DEFAULT_SECURE_RANDOM_ALGORITHM).generateSeed(20);
                    synchronized (secureRandom) {
                        secureRandom.setSeed(generateSeed);
                        seeded = true;
                    }
                } catch (Exception e) {
                    warn("SecureRandom", e);
                }
            };
            try {
                Thread thread = new Thread(runnable, "Generate Seed");
                thread.setDaemon(true);
                thread.start();
                Thread.yield();
                try {
                    thread.join(400L);
                } catch (InterruptedException e) {
                    warn("InterruptedException", e);
                }
                if (!seeded) {
                    byte[] generateAlternativeSeed = generateAlternativeSeed();
                    synchronized (secureRandom) {
                        secureRandom.setSeed(generateAlternativeSeed);
                    }
                }
            } catch (SecurityException e2) {
                runnable.run();
                generateAlternativeSeed();
            }
        } catch (Exception e3) {
            warn("SecureRandom", e3);
            secureRandom = new SecureRandom();
        }
        return secureRandom;
    }

    public static byte[] generateAlternativeSeed() {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            dataOutputStream.writeLong(System.currentTimeMillis());
            dataOutputStream.writeLong(System.nanoTime());
            dataOutputStream.writeInt(new Object().hashCode());
            Runtime runtime = Runtime.getRuntime();
            dataOutputStream.writeLong(runtime.freeMemory());
            dataOutputStream.writeLong(runtime.maxMemory());
            dataOutputStream.writeLong(runtime.totalMemory());
            try {
                String properties = System.getProperties().toString();
                dataOutputStream.writeInt(properties.length());
                dataOutputStream.write(properties.getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                warn("generateAlternativeSeed", e);
            }
            try {
                Class<?> cls = Class.forName("java.net.InetAddress");
                String obj = cls.getMethod("getHostName", new Class[0]).invoke(cls.getMethod("getLocalHost", new Class[0]).invoke(null, new Object[0]), new Object[0]).toString();
                dataOutputStream.writeUTF(obj);
                Object[] objArr = (Object[]) cls.getMethod("getAllByName", String.class).invoke(null, obj);
                Method method = cls.getMethod("getAddress", new Class[0]);
                for (Object obj2 : objArr) {
                    dataOutputStream.write((byte[]) method.invoke(obj2, new Object[0]));
                }
            } catch (Throwable th) {
            }
            for (int i = 0; i < 16; i++) {
                int i2 = 0;
                while (System.currentTimeMillis() == System.currentTimeMillis()) {
                    i2++;
                }
                dataOutputStream.writeInt(i2);
            }
            dataOutputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e2) {
            warn("generateAlternativeSeed", e2);
            return new byte[1];
        }
    }

    static void warn(String str, Throwable th) {
        System.out.println("Warning: " + str);
        if (th != null) {
            th.printStackTrace();
        }
    }

    public static int nextPowerOf2(int i) throws IllegalArgumentException {
        if (i - 2147483648 > -1073741824) {
            throw new IllegalArgumentException("Argument out of range [0x0-0x40000000]. Argument was: " + i);
        }
        if (i <= 1) {
            return 1;
        }
        return ((-1) >>> Integer.numberOfLeadingZeros(i - 1)) + 1;
    }

    public static int convertLongToInt(long j) {
        if (j <= -2147483648L) {
            return Integer.MIN_VALUE;
        }
        if (j >= 2147483647L) {
            return Integer.MAX_VALUE;
        }
        return (int) j;
    }

    public static short convertIntToShort(int i) {
        if (i <= -32768) {
            return Short.MIN_VALUE;
        }
        if (i >= 32767) {
            return Short.MAX_VALUE;
        }
        return (short) i;
    }

    public static long secureRandomLong() {
        return getSecureRandom().nextLong();
    }

    public static void randomBytes(byte[] bArr) {
        ThreadLocalRandom.current().nextBytes(bArr);
    }

    public static byte[] secureRandomBytes(int i) {
        if (i <= 0) {
            i = 1;
        }
        byte[] bArr = new byte[i];
        getSecureRandom().nextBytes(bArr);
        return bArr;
    }

    public static int randomInt(int i) {
        return ThreadLocalRandom.current().nextInt(i);
    }

    public static int secureRandomInt(int i) {
        return getSecureRandom().nextInt(i);
    }
}
