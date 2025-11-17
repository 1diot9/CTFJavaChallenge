package org.apache.tomcat.util.net;

import org.springframework.aop.framework.autoproxy.target.QuickTargetSourceCreator;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/IPv6Utils.class */
public class IPv6Utils {
    private static final int MAX_NUMBER_OF_GROUPS = 8;
    private static final int MAX_GROUP_LENGTH = 4;

    public static String canonize(String ipv6Address) throws IllegalArgumentException {
        if (ipv6Address == null) {
            return null;
        }
        if (!mayBeIPv6Address(ipv6Address)) {
            return ipv6Address;
        }
        int ipv6AddressLength = ipv6Address.length();
        if (ipv6Address.contains(".")) {
            int lastColonPos = ipv6Address.lastIndexOf(58);
            int lastColonsPos = ipv6Address.lastIndexOf("::");
            if (lastColonsPos >= 0 && lastColonPos == lastColonsPos + 1) {
                ipv6AddressLength = lastColonPos + 1;
            } else {
                ipv6AddressLength = lastColonPos;
            }
        } else if (ipv6Address.contains(QuickTargetSourceCreator.PREFIX_THREAD_LOCAL)) {
            ipv6AddressLength = ipv6Address.lastIndexOf(37);
        }
        StringBuilder result = new StringBuilder();
        char[][] groups = new char[8][4];
        int groupCounter = 0;
        int charInGroupCounter = 0;
        int zeroGroupIndex = -1;
        int zeroGroupLength = 0;
        int maxZeroGroupIndex = -1;
        int maxZeroGroupLength = 0;
        boolean isZero = true;
        boolean groupStart = true;
        StringBuilder expanded = new StringBuilder(ipv6Address);
        int colonsPos = ipv6Address.indexOf("::");
        int length = ipv6AddressLength;
        int change = 0;
        if (colonsPos >= 0 && colonsPos < ipv6AddressLength - 1) {
            int colonCounter = 0;
            for (int i = 0; i < ipv6AddressLength; i++) {
                if (ipv6Address.charAt(i) == ':') {
                    colonCounter++;
                }
            }
            if (colonsPos == 0) {
                expanded.insert(0, CustomBooleanEditor.VALUE_0);
                change = 0 + 1;
            }
            for (int i2 = 0; i2 < 8 - colonCounter; i2++) {
                expanded.insert(colonsPos + 1, "0:");
                change += 2;
            }
            if (colonsPos == ipv6AddressLength - 2) {
                expanded.setCharAt(colonsPos + change + 1, '0');
            } else {
                expanded.deleteCharAt(colonsPos + change + 1);
                change--;
            }
            length += change;
        }
        for (int charCounter = 0; charCounter < length; charCounter++) {
            char c = expanded.charAt(charCounter);
            if (c >= 'A' && c <= 'F') {
                c = (char) (c + ' ');
            }
            if (c != ':') {
                groups[groupCounter][charInGroupCounter] = c;
                if (!groupStart || c != '0') {
                    charInGroupCounter++;
                    groupStart = false;
                }
                if (c != '0') {
                    isZero = false;
                }
            }
            if (c == ':' || charCounter == length - 1) {
                if (isZero) {
                    zeroGroupLength++;
                    if (zeroGroupIndex == -1) {
                        zeroGroupIndex = groupCounter;
                    }
                }
                if (!isZero || charCounter == length - 1) {
                    if (zeroGroupLength > maxZeroGroupLength) {
                        maxZeroGroupLength = zeroGroupLength;
                        maxZeroGroupIndex = zeroGroupIndex;
                    }
                    zeroGroupLength = 0;
                    zeroGroupIndex = -1;
                }
                groupCounter++;
                charInGroupCounter = 0;
                isZero = true;
                groupStart = true;
            }
        }
        int numberOfGroups = groupCounter;
        for (int groupCounter2 = 0; groupCounter2 < numberOfGroups; groupCounter2++) {
            if (maxZeroGroupLength <= 1 || groupCounter2 < maxZeroGroupIndex || groupCounter2 >= maxZeroGroupIndex + maxZeroGroupLength) {
                for (int j = 0; j < 4; j++) {
                    if (groups[groupCounter2][j] != 0) {
                        result.append(groups[groupCounter2][j]);
                    }
                }
                if (groupCounter2 < numberOfGroups - 1 && (groupCounter2 != maxZeroGroupIndex - 1 || maxZeroGroupLength <= 1)) {
                    result.append(':');
                }
            } else if (groupCounter2 == maxZeroGroupIndex) {
                result.append("::");
            }
        }
        int resultLength = result.length();
        if (result.charAt(resultLength - 1) == ':' && ipv6AddressLength < ipv6Address.length() && ipv6Address.charAt(ipv6AddressLength) == ':') {
            result.delete(resultLength - 1, resultLength);
        }
        for (int i3 = ipv6AddressLength; i3 < ipv6Address.length(); i3++) {
            result.append(ipv6Address.charAt(i3));
        }
        return result.toString();
    }

    static boolean mayBeIPv6Address(String input) {
        char c;
        if (input == null) {
            return false;
        }
        int colonsCounter = 0;
        int length = input.length();
        for (int i = 0; i < length && (c = input.charAt(i)) != '.' && c != '%'; i++) {
            if ((c < '0' || c > '9') && ((c < 'a' || c > 'f') && ((c < 'A' || c > 'F') && c != ':'))) {
                return false;
            }
            if (c == ':') {
                colonsCounter++;
            }
        }
        if (colonsCounter < 2) {
            return false;
        }
        return true;
    }
}
