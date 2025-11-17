package org.h2.util;

import java.io.IOException;
import java.net.BindException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import org.h2.api.ErrorCode;
import org.h2.engine.SysProperties;
import org.h2.message.DbException;
import org.h2.security.CipherFactory;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/NetUtils.class */
public class NetUtils {
    private static final int CACHE_MILLIS = 1000;
    private static InetAddress cachedBindAddress;
    private static String cachedLocalAddress;
    private static long cachedLocalAddressTime;

    private NetUtils() {
    }

    public static Socket createLoopbackSocket(int i, boolean z) throws IOException {
        try {
            return createSocket(getLocalAddress(), i, z);
        } catch (IOException e) {
            try {
                return createSocket("localhost", i, z);
            } catch (IOException e2) {
                throw e;
            }
        }
    }

    public static Socket createSocket(String str, int i, boolean z) throws IOException {
        return createSocket(str, i, z, 0);
    }

    public static Socket createSocket(String str, int i, boolean z, int i2) throws IOException {
        int i3 = i;
        int indexOf = str.indexOf(58, str.startsWith("[") ? str.indexOf(93) : 0);
        if (indexOf >= 0) {
            i3 = Integer.decode(str.substring(indexOf + 1)).intValue();
            str = str.substring(0, indexOf);
        }
        return createSocket(InetAddress.getByName(str), i3, z, i2);
    }

    public static Socket createSocket(InetAddress inetAddress, int i, boolean z) throws IOException {
        return createSocket(inetAddress, i, z, 0);
    }

    public static Socket createSocket(InetAddress inetAddress, int i, boolean z, int i2) throws IOException {
        long nanoTime = System.nanoTime();
        int i3 = 0;
        while (true) {
            try {
                if (z) {
                    return CipherFactory.createSocket(inetAddress, i);
                }
                Socket socket = new Socket();
                socket.setSoTimeout(i2);
                socket.connect(new InetSocketAddress(inetAddress, i), SysProperties.SOCKET_CONNECT_TIMEOUT);
                return socket;
            } catch (IOException e) {
                if (System.nanoTime() - nanoTime >= SysProperties.SOCKET_CONNECT_TIMEOUT * 1000000) {
                    throw e;
                }
                if (i3 >= SysProperties.SOCKET_CONNECT_RETRY) {
                    throw e;
                }
                try {
                    Thread.sleep(Math.min(256, i3 * i3));
                } catch (InterruptedException e2) {
                }
                i3++;
            }
        }
    }

    public static ServerSocket createServerSocket(int i, boolean z) {
        try {
            return createServerSocketTry(i, z);
        } catch (Exception e) {
            return createServerSocketTry(i, z);
        }
    }

    private static InetAddress getBindAddress() throws UnknownHostException {
        String str = SysProperties.BIND_ADDRESS;
        if (str == null || str.isEmpty()) {
            return null;
        }
        synchronized (NetUtils.class) {
            if (cachedBindAddress == null) {
                cachedBindAddress = InetAddress.getByName(str);
            }
        }
        return cachedBindAddress;
    }

    private static ServerSocket createServerSocketTry(int i, boolean z) {
        try {
            InetAddress bindAddress = getBindAddress();
            if (z) {
                return CipherFactory.createServerSocket(i, bindAddress);
            }
            if (bindAddress == null) {
                return new ServerSocket(i);
            }
            return new ServerSocket(i, 0, bindAddress);
        } catch (BindException e) {
            throw DbException.get(ErrorCode.EXCEPTION_OPENING_PORT_2, e, Integer.toString(i), e.toString());
        } catch (IOException e2) {
            throw DbException.convertIOException(e2, "port: " + i + " ssl: " + z);
        }
    }

    public static boolean isLocalAddress(Socket socket) throws UnknownHostException {
        InetAddress inetAddress = socket.getInetAddress();
        if (inetAddress.isLoopbackAddress()) {
            return true;
        }
        for (InetAddress inetAddress2 : InetAddress.getAllByName(InetAddress.getLocalHost().getHostAddress())) {
            if (inetAddress.equals(inetAddress2)) {
                return true;
            }
        }
        return false;
    }

    public static ServerSocket closeSilently(ServerSocket serverSocket) {
        if (serverSocket != null) {
            try {
                serverSocket.close();
                return null;
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }

    public static synchronized String getLocalAddress() {
        String hostAddress;
        long nanoTime = System.nanoTime();
        if (cachedLocalAddress != null && nanoTime - cachedLocalAddressTime < DateTimeUtils.NANOS_PER_SECOND) {
            return cachedLocalAddress;
        }
        InetAddress inetAddress = null;
        boolean z = false;
        try {
            inetAddress = getBindAddress();
            if (inetAddress == null) {
                z = true;
            }
        } catch (UnknownHostException e) {
        }
        if (z) {
            try {
                inetAddress = InetAddress.getLocalHost();
            } catch (UnknownHostException e2) {
                throw DbException.convert(e2);
            }
        }
        if (inetAddress == null) {
            hostAddress = "localhost";
        } else {
            hostAddress = inetAddress.getHostAddress();
            if (inetAddress instanceof Inet6Address) {
                if (hostAddress.indexOf(37) >= 0) {
                    hostAddress = "localhost";
                } else if (hostAddress.indexOf(58) >= 0 && !hostAddress.startsWith("[")) {
                    hostAddress = "[" + hostAddress + "]";
                }
            }
        }
        if (hostAddress.equals("127.0.0.1")) {
            hostAddress = "localhost";
        }
        cachedLocalAddress = hostAddress;
        cachedLocalAddressTime = nanoTime;
        return hostAddress;
    }

    public static String getHostName(String str) {
        try {
            return InetAddress.getByName(str).getHostName();
        } catch (Exception e) {
            return "unknown";
        }
    }

    public static StringBuilder ipToShortForm(StringBuilder sb, byte[] bArr, boolean z) {
        int i;
        switch (bArr.length) {
            case 4:
                if (sb == null) {
                    sb = new StringBuilder(15);
                }
                sb.append(bArr[0] & 255).append('.').append(bArr[1] & 255).append('.').append(bArr[2] & 255).append('.').append(bArr[3] & 255);
                break;
            case 16:
                short[] sArr = new short[8];
                int i2 = 0;
                int i3 = 0;
                int i4 = 0;
                int i5 = 0;
                for (int i6 = 0; i6 < 8; i6++) {
                    int i7 = i5;
                    int i8 = i5 + 1;
                    i5 = i8 + 1;
                    short s = (short) (((bArr[i7] & 255) << 8) | (bArr[i8] & 255));
                    sArr[i6] = s;
                    if (s == 0) {
                        i4++;
                        if (i4 > i3) {
                            i3 = i4;
                            i2 = (i6 - i4) + 1;
                        }
                    } else {
                        i4 = 0;
                    }
                }
                if (sb == null) {
                    sb = new StringBuilder(z ? 41 : 39);
                }
                if (z) {
                    sb.append('[');
                }
                if (i3 > 1) {
                    for (int i9 = 0; i9 < i2; i9++) {
                        sb.append(Integer.toHexString(sArr[i9] & 65535)).append(':');
                    }
                    if (i2 == 0) {
                        sb.append(':');
                    }
                    sb.append(':');
                    i = i2 + i3;
                } else {
                    i = 0;
                }
                for (int i10 = i; i10 < 8; i10++) {
                    sb.append(Integer.toHexString(sArr[i10] & 65535));
                    if (i10 < 7) {
                        sb.append(':');
                    }
                }
                if (z) {
                    sb.append(']');
                    break;
                }
                break;
            default:
                StringUtils.convertBytesToHex(sb, bArr);
                break;
        }
        return sb;
    }
}
