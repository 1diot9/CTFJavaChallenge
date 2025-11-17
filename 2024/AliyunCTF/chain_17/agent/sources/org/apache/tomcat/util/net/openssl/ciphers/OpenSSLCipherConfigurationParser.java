package org.apache.tomcat.util.net.openssl.ciphers;

import ch.qos.logback.core.joran.JoranConstants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.net.Constants;
import org.apache.tomcat.util.res.StringManager;
import org.slf4j.Marker;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/openssl/ciphers/OpenSSLCipherConfigurationParser.class */
public class OpenSSLCipherConfigurationParser {
    private static final String SEPARATOR = ":|,| ";
    private static final String EXCLUDE = "!";
    private static final String DELETE = "-";
    private static final String TO_END = "+";
    private static final String AND = "+";
    private static final String eNULL = "eNULL";
    private static final String aNULL = "aNULL";
    private static final String HIGH = "HIGH";
    private static final String MEDIUM = "MEDIUM";
    private static final String LOW = "LOW";
    private static final String EXPORT = "EXPORT";
    private static final String EXPORT40 = "EXPORT40";
    private static final String EXPORT56 = "EXPORT56";
    private static final String kRSA = "kRSA";
    private static final String aRSA = "aRSA";
    private static final String RSA = "RSA";
    private static final String kEDH = "kEDH";
    private static final String kDHE = "kDHE";
    private static final String EDH = "EDH";
    private static final String DHE = "DHE";
    private static final String kDHr = "kDHr";
    private static final String kDHd = "kDHd";
    private static final String kDH = "kDH";
    private static final String kECDHr = "kECDHr";
    private static final String kECDHe = "kECDHe";
    private static final String kECDH = "kECDH";
    private static final String kEECDH = "kEECDH";
    private static final String EECDH = "EECDH";
    private static final String ECDH = "ECDH";
    private static final String kECDHE = "kECDHE";
    private static final String ECDHE = "ECDHE";
    private static final String AECDH = "AECDH";
    private static final String DSS = "DSS";
    private static final String aDSS = "aDSS";
    private static final String aDH = "aDH";
    private static final String aECDH = "aECDH";
    private static final String aECDSA = "aECDSA";
    private static final String ECDSA = "ECDSA";
    private static final String kFZA = "kFZA";
    private static final String aFZA = "aFZA";
    private static final String eFZA = "eFZA";
    private static final String FZA = "FZA";
    private static final String DH = "DH";
    private static final String ADH = "ADH";
    private static final String AES128 = "AES128";
    private static final String AES256 = "AES256";
    private static final String AES = "AES";
    private static final String AESGCM = "AESGCM";
    private static final String AESCCM = "AESCCM";
    private static final String AESCCM8 = "AESCCM8";
    private static final String ARIA128 = "ARIA128";
    private static final String ARIA256 = "ARIA256";
    private static final String ARIA = "ARIA";
    private static final String CAMELLIA128 = "CAMELLIA128";
    private static final String CAMELLIA256 = "CAMELLIA256";
    private static final String CAMELLIA = "CAMELLIA";
    private static final String CHACHA20 = "CHACHA20";
    private static final String TRIPLE_DES = "3DES";
    private static final String DES = "DES";
    private static final String RC4 = "RC4";
    private static final String RC2 = "RC2";
    private static final String IDEA = "IDEA";
    private static final String SEED = "SEED";
    private static final String MD5 = "MD5";
    private static final String SHA1 = "SHA1";
    private static final String SHA = "SHA";
    private static final String SHA256 = "SHA256";
    private static final String SHA384 = "SHA384";
    private static final String KRB5 = "KRB5";
    private static final String aGOST = "aGOST";
    private static final String aGOST01 = "aGOST01";
    private static final String aGOST94 = "aGOST94";
    private static final String kGOST = "kGOST";
    private static final String GOST94 = "GOST94";
    private static final String GOST89MAC = "GOST89MAC";
    private static final String aSRP = "aSRP";
    private static final String kSRP = "kSRP";
    private static final String SRP = "SRP";
    private static final String PSK = "PSK";
    private static final String aPSK = "aPSK";
    private static final String kPSK = "kPSK";
    private static final String kRSAPSK = "kRSAPSK";
    private static final String kECDHEPSK = "kECDHEPSK";
    private static final String kDHEPSK = "kDHEPSK";
    private static final String DEFAULT = "DEFAULT";
    private static final String COMPLEMENTOFDEFAULT = "COMPLEMENTOFDEFAULT";
    private static final String ALL = "ALL";
    private static final String COMPLEMENTOFALL = "COMPLEMENTOFALL";
    private static final Log log = LogFactory.getLog((Class<?>) OpenSSLCipherConfigurationParser.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) OpenSSLCipherConfigurationParser.class);
    private static boolean initialized = false;
    private static final Map<String, List<Cipher>> aliases = new LinkedHashMap();
    private static final Map<String, String> jsseToOpenSSL = new HashMap();

    private static void init() {
        for (Cipher cipher : Cipher.values()) {
            String alias = cipher.getOpenSSLAlias();
            if (aliases.containsKey(alias)) {
                aliases.get(alias).add(cipher);
            } else {
                List<Cipher> list = new ArrayList<>();
                list.add(cipher);
                aliases.put(alias, list);
            }
            aliases.put(cipher.name(), Collections.singletonList(cipher));
            for (String openSSlAltName : cipher.getOpenSSLAltNames()) {
                if (aliases.containsKey(openSSlAltName)) {
                    aliases.get(openSSlAltName).add(cipher);
                } else {
                    List<Cipher> list2 = new ArrayList<>();
                    list2.add(cipher);
                    aliases.put(openSSlAltName, list2);
                }
            }
            jsseToOpenSSL.put(cipher.name(), cipher.getOpenSSLAlias());
            Set<String> jsseNames = cipher.getJsseNames();
            for (String jsseName : jsseNames) {
                jsseToOpenSSL.put(jsseName, cipher.getOpenSSLAlias());
            }
        }
        List<Cipher> allCiphersList = Arrays.asList(Cipher.values());
        Collections.reverse(allCiphersList);
        LinkedHashSet<Cipher> allCiphers = defaultSort(new LinkedHashSet(allCiphersList));
        addListAlias(eNULL, filterByEncryption(allCiphers, Collections.singleton(Encryption.eNULL)));
        LinkedHashSet<Cipher> all = new LinkedHashSet<>(allCiphers);
        remove(all, eNULL);
        addListAlias(ALL, all);
        addListAlias(HIGH, filterByEncryptionLevel(allCiphers, Collections.singleton(EncryptionLevel.HIGH)));
        addListAlias(MEDIUM, filterByEncryptionLevel(allCiphers, Collections.singleton(EncryptionLevel.MEDIUM)));
        addListAlias(LOW, filterByEncryptionLevel(allCiphers, Collections.singleton(EncryptionLevel.LOW)));
        addListAlias(EXPORT, filterByEncryptionLevel(allCiphers, new HashSet(Arrays.asList(EncryptionLevel.EXP40, EncryptionLevel.EXP56))));
        aliases.put("EXP", aliases.get(EXPORT));
        addListAlias(EXPORT40, filterByEncryptionLevel(allCiphers, Collections.singleton(EncryptionLevel.EXP40)));
        addListAlias(EXPORT56, filterByEncryptionLevel(allCiphers, Collections.singleton(EncryptionLevel.EXP56)));
        aliases.put(JoranConstants.NULL, aliases.get(eNULL));
        aliases.put(COMPLEMENTOFALL, aliases.get(eNULL));
        addListAlias(aNULL, filterByAuthentication(allCiphers, Collections.singleton(Authentication.aNULL)));
        addListAlias(kRSA, filterByKeyExchange(allCiphers, Collections.singleton(KeyExchange.RSA)));
        addListAlias(aRSA, filterByAuthentication(allCiphers, Collections.singleton(Authentication.RSA)));
        aliases.put(RSA, aliases.get(kRSA));
        addListAlias(kEDH, filterByKeyExchange(allCiphers, Collections.singleton(KeyExchange.EDH)));
        addListAlias(kDHE, filterByKeyExchange(allCiphers, Collections.singleton(KeyExchange.EDH)));
        Set<Cipher> edh = filterByKeyExchange(allCiphers, Collections.singleton(KeyExchange.EDH));
        edh.removeAll(filterByAuthentication(allCiphers, Collections.singleton(Authentication.aNULL)));
        addListAlias(EDH, edh);
        addListAlias(DHE, edh);
        addListAlias(kDHr, filterByKeyExchange(allCiphers, Collections.singleton(KeyExchange.DHr)));
        addListAlias(kDHd, filterByKeyExchange(allCiphers, Collections.singleton(KeyExchange.DHd)));
        addListAlias(kDH, filterByKeyExchange(allCiphers, new HashSet(Arrays.asList(KeyExchange.DHr, KeyExchange.DHd))));
        addListAlias(kECDHr, filterByKeyExchange(allCiphers, Collections.singleton(KeyExchange.ECDHr)));
        addListAlias(kECDHe, filterByKeyExchange(allCiphers, Collections.singleton(KeyExchange.ECDHe)));
        addListAlias(kECDH, filterByKeyExchange(allCiphers, new HashSet(Arrays.asList(KeyExchange.ECDHe, KeyExchange.ECDHr))));
        addListAlias(ECDH, filterByKeyExchange(allCiphers, new HashSet(Arrays.asList(KeyExchange.ECDHe, KeyExchange.ECDHr, KeyExchange.EECDH))));
        addListAlias(kECDHE, filterByKeyExchange(allCiphers, Collections.singleton(KeyExchange.EECDH)));
        Set<Cipher> ecdhe = filterByKeyExchange(allCiphers, Collections.singleton(KeyExchange.EECDH));
        remove(ecdhe, aNULL);
        addListAlias(ECDHE, ecdhe);
        addListAlias(kEECDH, filterByKeyExchange(allCiphers, Collections.singleton(KeyExchange.EECDH)));
        Set<Cipher> eecdh = filterByKeyExchange(allCiphers, Collections.singleton(KeyExchange.EECDH));
        eecdh.removeAll(filterByAuthentication(allCiphers, Collections.singleton(Authentication.aNULL)));
        addListAlias(EECDH, eecdh);
        addListAlias(aDSS, filterByAuthentication(allCiphers, Collections.singleton(Authentication.DSS)));
        aliases.put(DSS, aliases.get(aDSS));
        addListAlias(aDH, filterByAuthentication(allCiphers, Collections.singleton(Authentication.DH)));
        Set<Cipher> aecdh = filterByKeyExchange(allCiphers, Collections.singleton(KeyExchange.EECDH));
        addListAlias(AECDH, filterByAuthentication(aecdh, Collections.singleton(Authentication.aNULL)));
        addListAlias(aECDH, filterByAuthentication(allCiphers, Collections.singleton(Authentication.ECDH)));
        addListAlias(ECDSA, filterByAuthentication(allCiphers, Collections.singleton(Authentication.ECDSA)));
        aliases.put(aECDSA, aliases.get(ECDSA));
        addListAlias(kFZA, filterByKeyExchange(allCiphers, Collections.singleton(KeyExchange.FZA)));
        addListAlias(aFZA, filterByAuthentication(allCiphers, Collections.singleton(Authentication.FZA)));
        addListAlias(eFZA, filterByEncryption(allCiphers, Collections.singleton(Encryption.FZA)));
        addListAlias(FZA, filter(allCiphers, null, Collections.singleton(KeyExchange.FZA), Collections.singleton(Authentication.FZA), Collections.singleton(Encryption.FZA), null, null));
        addListAlias("TLSv1.2", filterByProtocol(allCiphers, Collections.singleton(Protocol.TLSv1_2)));
        addListAlias(Constants.SSL_PROTO_TLSv1_0, filterByProtocol(allCiphers, Collections.singleton(Protocol.TLSv1)));
        addListAlias("SSLv3", filterByProtocol(allCiphers, Collections.singleton(Protocol.SSLv3)));
        aliases.put("TLSv1", aliases.get(Constants.SSL_PROTO_TLSv1_0));
        addListAlias("SSLv2", filterByProtocol(allCiphers, Collections.singleton(Protocol.SSLv2)));
        addListAlias(DH, filterByKeyExchange(allCiphers, new HashSet(Arrays.asList(KeyExchange.DHr, KeyExchange.DHd, KeyExchange.EDH))));
        Set<Cipher> adh = filterByKeyExchange(allCiphers, Collections.singleton(KeyExchange.EDH));
        adh.retainAll(filterByAuthentication(allCiphers, Collections.singleton(Authentication.aNULL)));
        addListAlias(ADH, adh);
        addListAlias(AES128, filterByEncryption(allCiphers, new HashSet(Arrays.asList(Encryption.AES128, Encryption.AES128CCM, Encryption.AES128CCM8, Encryption.AES128GCM))));
        addListAlias(AES256, filterByEncryption(allCiphers, new HashSet(Arrays.asList(Encryption.AES256, Encryption.AES256CCM, Encryption.AES256CCM8, Encryption.AES256GCM))));
        addListAlias(AES, filterByEncryption(allCiphers, new HashSet(Arrays.asList(Encryption.AES128, Encryption.AES128CCM, Encryption.AES128CCM8, Encryption.AES128GCM, Encryption.AES256, Encryption.AES256CCM, Encryption.AES256CCM8, Encryption.AES256GCM))));
        addListAlias(ARIA128, filterByEncryption(allCiphers, Collections.singleton(Encryption.ARIA128GCM)));
        addListAlias(ARIA256, filterByEncryption(allCiphers, Collections.singleton(Encryption.ARIA256GCM)));
        addListAlias(ARIA, filterByEncryption(allCiphers, new HashSet(Arrays.asList(Encryption.ARIA128GCM, Encryption.ARIA256GCM))));
        addListAlias(AESGCM, filterByEncryption(allCiphers, new HashSet(Arrays.asList(Encryption.AES128GCM, Encryption.AES256GCM))));
        addListAlias(AESCCM, filterByEncryption(allCiphers, new HashSet(Arrays.asList(Encryption.AES128CCM, Encryption.AES128CCM8, Encryption.AES256CCM, Encryption.AES256CCM8))));
        addListAlias(AESCCM8, filterByEncryption(allCiphers, new HashSet(Arrays.asList(Encryption.AES128CCM8, Encryption.AES256CCM8))));
        addListAlias(CAMELLIA, filterByEncryption(allCiphers, new HashSet(Arrays.asList(Encryption.CAMELLIA128, Encryption.CAMELLIA256))));
        addListAlias(CAMELLIA128, filterByEncryption(allCiphers, Collections.singleton(Encryption.CAMELLIA128)));
        addListAlias(CAMELLIA256, filterByEncryption(allCiphers, Collections.singleton(Encryption.CAMELLIA256)));
        addListAlias(CHACHA20, filterByEncryption(allCiphers, Collections.singleton(Encryption.CHACHA20POLY1305)));
        addListAlias(TRIPLE_DES, filterByEncryption(allCiphers, Collections.singleton(Encryption.TRIPLE_DES)));
        addListAlias(DES, filterByEncryption(allCiphers, Collections.singleton(Encryption.DES)));
        addListAlias(RC4, filterByEncryption(allCiphers, Collections.singleton(Encryption.RC4)));
        addListAlias(RC2, filterByEncryption(allCiphers, Collections.singleton(Encryption.RC2)));
        addListAlias(IDEA, filterByEncryption(allCiphers, Collections.singleton(Encryption.IDEA)));
        addListAlias(SEED, filterByEncryption(allCiphers, Collections.singleton(Encryption.SEED)));
        addListAlias(MD5, filterByMessageDigest(allCiphers, Collections.singleton(MessageDigest.MD5)));
        addListAlias(SHA1, filterByMessageDigest(allCiphers, Collections.singleton(MessageDigest.SHA1)));
        aliases.put(SHA, aliases.get(SHA1));
        addListAlias(SHA256, filterByMessageDigest(allCiphers, Collections.singleton(MessageDigest.SHA256)));
        addListAlias(SHA384, filterByMessageDigest(allCiphers, Collections.singleton(MessageDigest.SHA384)));
        addListAlias(aGOST, filterByAuthentication(allCiphers, new HashSet(Arrays.asList(Authentication.GOST01, Authentication.GOST94))));
        addListAlias(aGOST01, filterByAuthentication(allCiphers, Collections.singleton(Authentication.GOST01)));
        addListAlias(aGOST94, filterByAuthentication(allCiphers, Collections.singleton(Authentication.GOST94)));
        addListAlias(kGOST, filterByKeyExchange(allCiphers, Collections.singleton(KeyExchange.GOST)));
        addListAlias(GOST94, filterByMessageDigest(allCiphers, Collections.singleton(MessageDigest.GOST94)));
        addListAlias(GOST89MAC, filterByMessageDigest(allCiphers, Collections.singleton(MessageDigest.GOST89MAC)));
        addListAlias(PSK, filter(allCiphers, null, new HashSet(Arrays.asList(KeyExchange.PSK, KeyExchange.RSAPSK, KeyExchange.DHEPSK, KeyExchange.ECDHEPSK)), Collections.singleton(Authentication.PSK), null, null, null));
        addListAlias(aPSK, filterByAuthentication(allCiphers, Collections.singleton(Authentication.PSK)));
        addListAlias(kPSK, filterByKeyExchange(allCiphers, Collections.singleton(KeyExchange.PSK)));
        addListAlias(kRSAPSK, filterByKeyExchange(allCiphers, Collections.singleton(KeyExchange.RSAPSK)));
        addListAlias(kECDHEPSK, filterByKeyExchange(allCiphers, Collections.singleton(KeyExchange.ECDHEPSK)));
        addListAlias(kDHEPSK, filterByKeyExchange(allCiphers, Collections.singleton(KeyExchange.DHEPSK)));
        addListAlias(KRB5, filter(allCiphers, null, Collections.singleton(KeyExchange.KRB5), Collections.singleton(Authentication.KRB5), null, null, null));
        addListAlias(aSRP, filterByAuthentication(allCiphers, Collections.singleton(Authentication.SRP)));
        addListAlias(kSRP, filterByKeyExchange(allCiphers, Collections.singleton(KeyExchange.SRP)));
        addListAlias(SRP, filterByKeyExchange(allCiphers, Collections.singleton(KeyExchange.SRP)));
        initialized = true;
        addListAlias(DEFAULT, parse("ALL:!EXPORT:!eNULL:!aNULL:!SSLv2:!DES:!RC2:!RC4:!DSS:!SEED:!IDEA:!CAMELLIA:!AESCCM:!3DES:!ARIA"));
        LinkedHashSet<Cipher> complementOfDefault = filterByAuthentication(filterByKeyExchange(all, new HashSet(Arrays.asList(KeyExchange.EDH, KeyExchange.EECDH))), Collections.singleton(Authentication.aNULL));
        List<Cipher> list3 = aliases.get(eNULL);
        Objects.requireNonNull(complementOfDefault);
        list3.forEach((v1) -> {
            r1.remove(v1);
        });
        complementOfDefault.addAll(aliases.get("SSLv2"));
        complementOfDefault.addAll(aliases.get(EXPORT));
        complementOfDefault.addAll(aliases.get(DES));
        complementOfDefault.addAll(aliases.get(TRIPLE_DES));
        complementOfDefault.addAll(aliases.get(RC2));
        complementOfDefault.addAll(aliases.get(RC4));
        complementOfDefault.addAll(aliases.get(aDSS));
        complementOfDefault.addAll(aliases.get(SEED));
        complementOfDefault.addAll(aliases.get(IDEA));
        complementOfDefault.addAll(aliases.get(CAMELLIA));
        complementOfDefault.addAll(aliases.get(AESCCM));
        complementOfDefault.addAll(aliases.get(ARIA));
        defaultSort(complementOfDefault);
        addListAlias(COMPLEMENTOFDEFAULT, complementOfDefault);
    }

    static void addListAlias(String alias, Set<Cipher> ciphers) {
        aliases.put(alias, new ArrayList(ciphers));
    }

    static void moveToEnd(LinkedHashSet<Cipher> ciphers, String alias) {
        moveToEnd(ciphers, aliases.get(alias));
    }

    static void moveToEnd(LinkedHashSet<Cipher> ciphers, Collection<Cipher> toBeMovedCiphers) {
        List<Cipher> movedCiphers = new ArrayList<>(toBeMovedCiphers);
        movedCiphers.retainAll(ciphers);
        Objects.requireNonNull(ciphers);
        movedCiphers.forEach((v1) -> {
            r1.remove(v1);
        });
        ciphers.addAll(movedCiphers);
    }

    static void moveToStart(LinkedHashSet<Cipher> ciphers, Collection<Cipher> toBeMovedCiphers) {
        List<Cipher> movedCiphers = new ArrayList<>(toBeMovedCiphers);
        List<Cipher> originalCiphers = new ArrayList<>(ciphers);
        movedCiphers.retainAll(ciphers);
        ciphers.clear();
        ciphers.addAll(movedCiphers);
        ciphers.addAll(originalCiphers);
    }

    static void add(LinkedHashSet<Cipher> ciphers, String alias) {
        ciphers.addAll(aliases.get(alias));
    }

    static void remove(Set<Cipher> ciphers, String alias) {
        List<Cipher> list = aliases.get(alias);
        Objects.requireNonNull(ciphers);
        list.forEach((v1) -> {
            r1.remove(v1);
        });
    }

    static LinkedHashSet<Cipher> strengthSort(LinkedHashSet<Cipher> ciphers) {
        Set<Integer> keySizes = new HashSet<>();
        Iterator<Cipher> it = ciphers.iterator();
        while (it.hasNext()) {
            Cipher cipher = it.next();
            keySizes.add(Integer.valueOf(cipher.getStrength_bits()));
        }
        List<Integer> strength_bits = new ArrayList<>(keySizes);
        Collections.sort(strength_bits);
        Collections.reverse(strength_bits);
        LinkedHashSet<Cipher> result = new LinkedHashSet<>(ciphers);
        Iterator<Integer> it2 = strength_bits.iterator();
        while (it2.hasNext()) {
            int strength = it2.next().intValue();
            moveToEnd(result, filterByStrengthBits(ciphers, strength));
        }
        return result;
    }

    static LinkedHashSet<Cipher> defaultSort(LinkedHashSet<Cipher> ciphers) {
        LinkedHashSet<Cipher> result = new LinkedHashSet<>(ciphers.size());
        LinkedHashSet<Cipher> ecdh = new LinkedHashSet<>(ciphers.size());
        ecdh.addAll(filterByKeyExchange(ciphers, Collections.singleton(KeyExchange.EECDH)));
        Set<Encryption> aes = new HashSet<>(Arrays.asList(Encryption.AES128, Encryption.AES128CCM, Encryption.AES128CCM8, Encryption.AES128GCM, Encryption.AES256, Encryption.AES256CCM, Encryption.AES256CCM8, Encryption.AES256GCM));
        result.addAll(filterByEncryption(ecdh, aes));
        result.addAll(filterByEncryption(ciphers, aes));
        result.addAll(ecdh);
        result.addAll(ciphers);
        moveToEnd(result, filterByMessageDigest(result, Collections.singleton(MessageDigest.MD5)));
        moveToEnd(result, filterByAuthentication(result, Collections.singleton(Authentication.aNULL)));
        moveToEnd(result, filterByAuthentication(result, Collections.singleton(Authentication.ECDH)));
        moveToEnd(result, filterByKeyExchange(result, Collections.singleton(KeyExchange.RSA)));
        moveToEnd(result, filterByKeyExchange(result, Collections.singleton(KeyExchange.PSK)));
        moveToEnd(result, filterByEncryption(result, Collections.singleton(Encryption.RC4)));
        return strengthSort(result);
    }

    static Set<Cipher> filterByStrengthBits(Set<Cipher> ciphers, int strength_bits) {
        Set<Cipher> result = new LinkedHashSet<>(ciphers.size());
        for (Cipher cipher : ciphers) {
            if (cipher.getStrength_bits() == strength_bits) {
                result.add(cipher);
            }
        }
        return result;
    }

    static Set<Cipher> filterByProtocol(Set<Cipher> ciphers, Set<Protocol> protocol) {
        return filter(ciphers, protocol, null, null, null, null, null);
    }

    static LinkedHashSet<Cipher> filterByKeyExchange(Set<Cipher> ciphers, Set<KeyExchange> kx) {
        return filter(ciphers, null, kx, null, null, null, null);
    }

    static LinkedHashSet<Cipher> filterByAuthentication(Set<Cipher> ciphers, Set<Authentication> au) {
        return filter(ciphers, null, null, au, null, null, null);
    }

    static Set<Cipher> filterByEncryption(Set<Cipher> ciphers, Set<Encryption> enc) {
        return filter(ciphers, null, null, null, enc, null, null);
    }

    static Set<Cipher> filterByEncryptionLevel(Set<Cipher> ciphers, Set<EncryptionLevel> level) {
        return filter(ciphers, null, null, null, null, level, null);
    }

    static Set<Cipher> filterByMessageDigest(Set<Cipher> ciphers, Set<MessageDigest> mac) {
        return filter(ciphers, null, null, null, null, null, mac);
    }

    static LinkedHashSet<Cipher> filter(Set<Cipher> ciphers, Set<Protocol> protocol, Set<KeyExchange> kx, Set<Authentication> au, Set<Encryption> enc, Set<EncryptionLevel> level, Set<MessageDigest> mac) {
        LinkedHashSet<Cipher> result = new LinkedHashSet<>(ciphers.size());
        for (Cipher cipher : ciphers) {
            if (protocol != null && protocol.contains(cipher.getProtocol())) {
                result.add(cipher);
            }
            if (kx != null && kx.contains(cipher.getKx())) {
                result.add(cipher);
            }
            if (au != null && au.contains(cipher.getAu())) {
                result.add(cipher);
            }
            if (enc != null && enc.contains(cipher.getEnc())) {
                result.add(cipher);
            }
            if (level != null && level.contains(cipher.getLevel())) {
                result.add(cipher);
            }
            if (mac != null && mac.contains(cipher.getMac())) {
                result.add(cipher);
            }
        }
        return result;
    }

    public static LinkedHashSet<Cipher> parse(String expression) {
        if (!initialized) {
            init();
        }
        String[] elements = expression.split(SEPARATOR);
        LinkedHashSet<Cipher> ciphers = new LinkedHashSet<>();
        Set<Cipher> removedCiphers = new HashSet<>();
        int length = elements.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            String element = elements[i];
            if (element.startsWith("-")) {
                String alias = element.substring(1);
                if (aliases.containsKey(alias)) {
                    remove(ciphers, alias);
                }
            } else if (element.startsWith("!")) {
                String alias2 = element.substring(1);
                if (aliases.containsKey(alias2)) {
                    removedCiphers.addAll(aliases.get(alias2));
                } else {
                    log.warn(sm.getString("opensslCipherConfigurationParser.unknownElement", alias2));
                }
            } else if (element.startsWith(Marker.ANY_NON_NULL_MARKER)) {
                String alias3 = element.substring(1);
                if (aliases.containsKey(alias3)) {
                    moveToEnd(ciphers, alias3);
                }
            } else {
                if ("@STRENGTH".equals(element)) {
                    strengthSort(ciphers);
                    break;
                }
                if (aliases.containsKey(element)) {
                    add(ciphers, element);
                } else if (element.contains(Marker.ANY_NON_NULL_MARKER)) {
                    String[] intersections = element.split("\\+");
                    if (intersections.length > 0 && aliases.containsKey(intersections[0])) {
                        List<Cipher> result = new ArrayList<>(aliases.get(intersections[0]));
                        for (int i2 = 1; i2 < intersections.length; i2++) {
                            if (aliases.containsKey(intersections[i2])) {
                                result.retainAll(aliases.get(intersections[i2]));
                            }
                        }
                        ciphers.addAll(result);
                    }
                }
            }
            i++;
        }
        ciphers.removeAll(removedCiphers);
        return ciphers;
    }

    public static List<String> convertForJSSE(Collection<Cipher> ciphers) {
        List<String> result = new ArrayList<>(ciphers.size());
        for (Cipher cipher : ciphers) {
            result.addAll(cipher.getJsseNames());
        }
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("opensslCipherConfigurationParser.effectiveCiphers", displayResult(ciphers, true, ",")));
        }
        return result;
    }

    public static List<String> parseExpression(String expression) {
        return convertForJSSE(parse(expression));
    }

    public static String jsseToOpenSSL(String jsseCipherName) {
        if (!initialized) {
            init();
        }
        return jsseToOpenSSL.get(jsseCipherName);
    }

    public static String openSSLToJsse(String opensslCipherName) {
        if (!initialized) {
            init();
        }
        List<Cipher> ciphers = aliases.get(opensslCipherName);
        if (ciphers == null || ciphers.size() != 1) {
            return null;
        }
        Cipher cipher = ciphers.get(0);
        return cipher.getJsseNames().iterator().next();
    }

    static String displayResult(Collection<Cipher> ciphers, boolean useJSSEFormat, String separator) {
        if (ciphers.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder(ciphers.size() * 16);
        for (Cipher cipher : ciphers) {
            if (useJSSEFormat) {
                for (String name : cipher.getJsseNames()) {
                    builder.append(name);
                    builder.append(separator);
                }
            } else {
                builder.append(cipher.getOpenSSLAlias());
            }
            builder.append(separator);
        }
        return builder.toString().substring(0, builder.length() - 1);
    }

    public static void usage() {
        System.out.println("Usage: java " + OpenSSLCipherConfigurationParser.class.getName() + " [options] cipherspec");
        System.out.println();
        System.out.println("Displays the TLS cipher suites matching the cipherspec.");
        System.out.println();
        System.out.println(" --help,");
        System.out.println(" -h          Print this help message");
        System.out.println(" --openssl   Show OpenSSL cipher suite names instead of IANA cipher suite names.");
        System.out.println(" --verbose,");
        System.out.println(" -v          Provide detailed cipher listing");
    }

    public static void main(String[] args) throws Exception {
        String cipherSpec;
        boolean verbose = false;
        boolean useOpenSSLNames = false;
        int argindex = 0;
        while (true) {
            if (argindex >= args.length) {
                break;
            }
            String arg = args[argindex];
            if ("--verbose".equals(arg) || "-v".equals(arg)) {
                verbose = true;
            } else if ("--openssl".equals(arg)) {
                useOpenSSLNames = true;
            } else if ("--help".equals(arg) || "-h".equals(arg)) {
                usage();
                System.exit(0);
            } else if ("--".equals(arg)) {
                argindex++;
                break;
            } else {
                if (!arg.startsWith("-")) {
                    break;
                }
                System.out.println("Unknown option: " + arg);
                usage();
                System.exit(1);
            }
            argindex++;
        }
        if (argindex < args.length) {
            cipherSpec = args[argindex];
        } else {
            cipherSpec = DEFAULT;
        }
        Set<Cipher> ciphers = parse(cipherSpec);
        boolean first = true;
        if (null != ciphers && 0 < ciphers.size()) {
            for (Cipher cipher : ciphers) {
                if (first) {
                    first = false;
                } else if (!verbose) {
                    System.out.print(',');
                }
                if (useOpenSSLNames) {
                    System.out.print(cipher.getOpenSSLAlias());
                } else {
                    System.out.print(cipher.name());
                }
                if (verbose) {
                    System.out.println("\t" + cipher.getProtocol() + "\tKx=" + cipher.getKx() + "\tAu=" + cipher.getAu() + "\tEnc=" + cipher.getEnc() + "\tMac=" + cipher.getMac());
                }
            }
            System.out.println();
            return;
        }
        System.out.println("No ciphers match '" + cipherSpec + "'");
    }
}
