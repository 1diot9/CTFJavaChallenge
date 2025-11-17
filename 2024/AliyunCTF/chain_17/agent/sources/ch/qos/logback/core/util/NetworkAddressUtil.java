package ch.qos.logback.core.util;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.spi.ContextAwareBase;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/util/NetworkAddressUtil.class */
public class NetworkAddressUtil extends ContextAwareBase {
    public NetworkAddressUtil(Context context) {
        setContext(context);
    }

    public static String getLocalHostName() throws UnknownHostException, SocketException {
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            return localhost.getHostName();
        } catch (UnknownHostException e) {
            return getLocalAddressAsString();
        }
    }

    public static String getCanonicalLocalHostName() throws UnknownHostException, SocketException {
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            return localhost.getCanonicalHostName();
        } catch (UnknownHostException e) {
            return getLocalAddressAsString();
        }
    }

    private static String getLocalAddressAsString() throws UnknownHostException, SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces != null && interfaces.hasMoreElements()) {
            Enumeration<InetAddress> addresses = interfaces.nextElement().getInetAddresses();
            while (addresses != null && addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                if (acceptableAddress(address)) {
                    return address.getHostAddress();
                }
            }
        }
        throw new UnknownHostException();
    }

    private static boolean acceptableAddress(InetAddress address) {
        return (address == null || address.isLoopbackAddress() || address.isAnyLocalAddress() || address.isLinkLocalAddress()) ? false : true;
    }

    public String safelyGetLocalHostName() {
        try {
            String localhostName = getLocalHostName();
            return localhostName;
        } catch (SecurityException | SocketException | UnknownHostException e) {
            addError("Failed to get local hostname", e);
            return CoreConstants.UNKNOWN_LOCALHOST;
        }
    }

    public String safelyGetCanonicalLocalHostName() {
        try {
            String localhostName = getCanonicalLocalHostName();
            return localhostName;
        } catch (SecurityException | SocketException | UnknownHostException e) {
            addError("Failed to get canonical local hostname", e);
            return CoreConstants.UNKNOWN_LOCALHOST;
        }
    }
}
