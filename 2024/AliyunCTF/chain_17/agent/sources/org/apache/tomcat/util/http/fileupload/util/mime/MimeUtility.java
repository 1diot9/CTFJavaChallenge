package org.apache.tomcat.util.http.fileupload.util.mime;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.tomcat.util.codec.binary.Base64;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/fileupload/util/mime/MimeUtility.class */
public final class MimeUtility {
    private static final String BASE64_ENCODING_MARKER = "B";
    private static final String QUOTEDPRINTABLE_ENCODING_MARKER = "Q";
    private static final String ENCODED_TOKEN_MARKER = "=?";
    private static final String ENCODED_TOKEN_FINISHER = "?=";
    private static final String LINEAR_WHITESPACE = " \t\r\n";
    private static final Map<String, String> MIME2JAVA = new HashMap();

    static {
        MIME2JAVA.put("iso-2022-cn", "ISO2022CN");
        MIME2JAVA.put("iso-2022-kr", "ISO2022KR");
        MIME2JAVA.put("utf-8", "UTF8");
        MIME2JAVA.put("utf8", "UTF8");
        MIME2JAVA.put("ja_jp.iso2022-7", "ISO2022JP");
        MIME2JAVA.put("ja_jp.eucjp", "EUCJIS");
        MIME2JAVA.put("euc-kr", "KSC5601");
        MIME2JAVA.put("euckr", "KSC5601");
        MIME2JAVA.put("us-ascii", "ISO-8859-1");
        MIME2JAVA.put("x-us-ascii", "ISO-8859-1");
    }

    private MimeUtility() {
    }

    public static String decodeText(String text) throws UnsupportedEncodingException {
        if (!text.contains(ENCODED_TOKEN_MARKER)) {
            return text;
        }
        int offset = 0;
        int endOffset = text.length();
        int startWhiteSpace = -1;
        int endWhiteSpace = -1;
        StringBuilder decodedText = new StringBuilder(text.length());
        boolean previousTokenEncoded = false;
        while (offset < endOffset) {
            char ch2 = text.charAt(offset);
            if (LINEAR_WHITESPACE.indexOf(ch2) != -1) {
                startWhiteSpace = offset;
                while (true) {
                    if (offset < endOffset) {
                        char ch3 = text.charAt(offset);
                        if (LINEAR_WHITESPACE.indexOf(ch3) == -1) {
                            endWhiteSpace = offset;
                            break;
                        }
                        offset++;
                    }
                }
            } else {
                int wordStart = offset;
                while (offset < endOffset) {
                    char ch4 = text.charAt(offset);
                    if (LINEAR_WHITESPACE.indexOf(ch4) != -1) {
                        break;
                    }
                    offset++;
                }
                String word = text.substring(wordStart, offset);
                if (word.startsWith(ENCODED_TOKEN_MARKER)) {
                    try {
                        String decodedWord = decodeWord(word);
                        if (!previousTokenEncoded && startWhiteSpace != -1) {
                            decodedText.append((CharSequence) text, startWhiteSpace, endWhiteSpace);
                            startWhiteSpace = -1;
                        }
                        previousTokenEncoded = true;
                        decodedText.append(decodedWord);
                    } catch (ParseException e) {
                    }
                }
                if (startWhiteSpace != -1) {
                    decodedText.append((CharSequence) text, startWhiteSpace, endWhiteSpace);
                    startWhiteSpace = -1;
                }
                previousTokenEncoded = false;
                decodedText.append(word);
            }
        }
        return decodedText.toString();
    }

    private static String decodeWord(String word) throws ParseException, UnsupportedEncodingException {
        byte[] decodedData;
        if (!word.startsWith(ENCODED_TOKEN_MARKER)) {
            throw new ParseException("Invalid RFC 2047 encoded-word: " + word);
        }
        int charsetPos = word.indexOf(63, 2);
        if (charsetPos == -1) {
            throw new ParseException("Missing charset in RFC 2047 encoded-word: " + word);
        }
        String charset = word.substring(2, charsetPos).toLowerCase(Locale.ENGLISH);
        int encodingPos = word.indexOf(63, charsetPos + 1);
        if (encodingPos == -1) {
            throw new ParseException("Missing encoding in RFC 2047 encoded-word: " + word);
        }
        String encoding = word.substring(charsetPos + 1, encodingPos);
        int encodedTextPos = word.indexOf(ENCODED_TOKEN_FINISHER, encodingPos + 1);
        if (encodedTextPos == -1) {
            throw new ParseException("Missing encoded text in RFC 2047 encoded-word: " + word);
        }
        String encodedText = word.substring(encodingPos + 1, encodedTextPos);
        if (encodedText.isEmpty()) {
            return "";
        }
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream(encodedText.length());
            if (encoding.equals(BASE64_ENCODING_MARKER)) {
                decodedData = Base64.decodeBase64(encodedText);
            } else if (encoding.equals(QUOTEDPRINTABLE_ENCODING_MARKER)) {
                byte[] encodedData = encodedText.getBytes(StandardCharsets.US_ASCII);
                QuotedPrintableDecoder.decode(encodedData, out);
                decodedData = out.toByteArray();
            } else {
                throw new UnsupportedEncodingException("Unknown RFC 2047 encoding: " + encoding);
            }
            return new String(decodedData, javaCharset(charset));
        } catch (IOException e) {
            throw new UnsupportedEncodingException("Invalid RFC 2047 encoding");
        }
    }

    private static String javaCharset(String charset) {
        if (charset == null) {
            return null;
        }
        String mappedCharset = MIME2JAVA.get(charset.toLowerCase(Locale.ENGLISH));
        if (mappedCharset == null) {
            return charset;
        }
        return mappedCharset;
    }
}
