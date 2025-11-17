package org.h2.server.web;

import cn.hutool.core.text.CharSequenceUtil;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;
import org.apache.coyote.http11.Constants;
import org.h2.engine.SysProperties;
import org.h2.message.DbException;
import org.h2.util.IOUtils;
import org.h2.util.NetUtils;
import org.h2.util.NetworkConnectionInfo;
import org.h2.util.StringUtils;
import org.h2.util.Utils;
import org.h2.util.Utils21;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/server/web/WebThread.class */
public class WebThread extends WebApp implements Runnable {
    private static final byte[] RN = {13, 10};
    private static final byte[] RNRN = {13, 10, 13, 10};
    protected OutputStream output;
    protected final Socket socket;
    private final Thread thread;
    private InputStream input;
    private String host;
    private int dataLength;
    private String ifModifiedSince;

    /* JADX INFO: Access modifiers changed from: package-private */
    public WebThread(Socket socket, WebServer webServer) {
        super(webServer);
        this.socket = socket;
        this.thread = webServer.virtualThreads ? Utils21.newVirtualThread(this) : new Thread(this);
        this.thread.setName("H2 Console thread");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void start() {
        this.thread.start();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void join(int i) throws InterruptedException {
        this.thread.join(i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void stopNow() {
        this.stop = true;
        try {
            this.socket.close();
        } catch (IOException e) {
        }
    }

    private String getAllowedFile(String str) {
        if (!allow()) {
            return "notAllowed.jsp";
        }
        if (str.length() == 0) {
            return "index.do";
        }
        if (str.charAt(0) == '?') {
            return "index.do" + str;
        }
        return str;
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            this.input = new BufferedInputStream(this.socket.getInputStream());
            this.output = new BufferedOutputStream(this.socket.getOutputStream());
            while (!this.stop && process()) {
            }
        } catch (Exception e) {
            DbException.traceThrowable(e);
        }
        IOUtils.closeSilently(this.output);
        IOUtils.closeSilently(this.input);
        try {
            this.socket.close();
        } catch (IOException e2) {
        } finally {
            this.server.remove(this);
        }
    }

    private boolean process() throws IOException {
        String str;
        Iterator it;
        String readHeaderLine = readHeaderLine();
        boolean startsWith = readHeaderLine.startsWith("GET ");
        if ((!startsWith && !readHeaderLine.startsWith("POST ")) || !readHeaderLine.endsWith(" HTTP/1.1")) {
            writeSimple("HTTP/1.1 400 Bad Request", "Bad request");
            return false;
        }
        String trimSubstring = StringUtils.trimSubstring(readHeaderLine, startsWith ? 4 : 5, readHeaderLine.length() - 9);
        if (trimSubstring.isEmpty() || trimSubstring.charAt(0) != '/') {
            writeSimple("HTTP/1.1 400 Bad Request", "Bad request");
            return false;
        }
        this.attributes = new Properties();
        boolean parseHeader = parseHeader();
        if (!checkHost(this.host)) {
            return false;
        }
        String substring = trimSubstring.substring(1);
        trace(readHeaderLine + ": " + substring);
        String allowedFile = getAllowedFile(substring);
        int indexOf = allowedFile.indexOf(63);
        this.session = null;
        String str2 = null;
        if (indexOf >= 0) {
            parseAttributes(allowedFile.substring(indexOf + 1));
            String property = this.attributes.getProperty("jsessionid");
            str2 = this.attributes.getProperty("key");
            allowedFile = allowedFile.substring(0, indexOf);
            this.session = this.server.getSession(property);
        }
        parseBodyAttributes();
        String processRequest = processRequest(allowedFile, new NetworkConnectionInfo(NetUtils.ipToShortForm(new StringBuilder(this.server.getSSL() ? "https://" : "http://"), this.socket.getLocalAddress().getAddress(), true).append(':').append(this.socket.getLocalPort()).toString(), this.socket.getInetAddress().getAddress(), this.socket.getPort(), null));
        if (processRequest.length() == 0) {
            return true;
        }
        if (this.cache && this.ifModifiedSince != null && this.ifModifiedSince.equals(this.server.getStartDateTime())) {
            writeSimple("HTTP/1.1 304 Not Modified", (byte[]) null);
            return parseHeader;
        }
        byte[] file = this.server.getFile(processRequest);
        if (file == null) {
            writeSimple("HTTP/1.1 404 Not Found", "File not found: " + processRequest);
            return parseHeader;
        }
        if (this.session != null && processRequest.endsWith(".jsp")) {
            if (str2 != null) {
                this.session.put("key", str2);
            }
            String str3 = new String(file, StandardCharsets.UTF_8);
            if (SysProperties.CONSOLE_STREAM && (it = (Iterator) this.session.map.remove("chunks")) != null) {
                String str4 = ((("HTTP/1.1 200 OK\r\nContent-Type: " + this.mimeType + "\r\n") + "Cache-Control: no-cache\r\n") + "Transfer-Encoding: chunked\r\n") + "\r\n";
                trace(str4);
                this.output.write(str4.getBytes(StandardCharsets.ISO_8859_1));
                while (it.hasNext()) {
                    byte[] bytes = PageParser.parse((String) it.next(), this.session.map).getBytes(StandardCharsets.UTF_8);
                    if (bytes.length != 0) {
                        this.output.write(Integer.toHexString(bytes.length).getBytes(StandardCharsets.ISO_8859_1));
                        this.output.write(RN);
                        this.output.write(bytes);
                        this.output.write(RN);
                        this.output.flush();
                    }
                }
                this.output.write(48);
                this.output.write(RNRN);
                this.output.flush();
                return parseHeader;
            }
            file = PageParser.parse(str3, this.session.map).getBytes(StandardCharsets.UTF_8);
        }
        String str5 = "HTTP/1.1 200 OK\r\nContent-Type: " + this.mimeType + "\r\n";
        if (!this.cache) {
            str = str5 + "Cache-Control: no-cache\r\n";
        } else {
            str = (str5 + "Cache-Control: max-age=10\r\n") + "Last-Modified: " + this.server.getStartDateTime() + "\r\n";
        }
        String str6 = (str + "Content-Length: " + file.length + "\r\n") + "\r\n";
        trace(str6);
        this.output.write(str6.getBytes(StandardCharsets.ISO_8859_1));
        this.output.write(file);
        this.output.flush();
        return parseHeader;
    }

    private void writeSimple(String str, String str2) throws IOException {
        writeSimple(str, str2 != null ? str2.getBytes(StandardCharsets.UTF_8) : null);
    }

    private void writeSimple(String str, byte[] bArr) throws IOException {
        trace(str);
        this.output.write(str.getBytes(StandardCharsets.ISO_8859_1));
        if (bArr != null) {
            this.output.write(RN);
            String str2 = "Content-Length: " + bArr.length;
            trace(str2);
            this.output.write(str2.getBytes(StandardCharsets.ISO_8859_1));
            this.output.write(RNRN);
            this.output.write(bArr);
        } else {
            this.output.write(RNRN);
        }
        this.output.flush();
    }

    private boolean checkHost(String str) throws IOException {
        if (str == null) {
            writeSimple("HTTP/1.1 400 Bad Request", "Bad request");
            return false;
        }
        int indexOf = str.indexOf(58);
        if (indexOf >= 0) {
            str = str.substring(0, indexOf);
        }
        if (str.isEmpty()) {
            return false;
        }
        String lowerEnglish = StringUtils.toLowerEnglish(str);
        if (lowerEnglish.equals(this.server.getHost()) || lowerEnglish.equals("localhost") || lowerEnglish.equals("127.0.0.1")) {
            return true;
        }
        String externalNames = this.server.getExternalNames();
        if (externalNames != null && !externalNames.isEmpty()) {
            for (String str2 : externalNames.split(",")) {
                if (lowerEnglish.equals(str2.trim())) {
                    return true;
                }
            }
        }
        writeSimple("HTTP/1.1 404 Not Found", "Host " + lowerEnglish + " not found");
        return false;
    }

    private String readHeaderLine() throws IOException {
        StringBuilder sb = new StringBuilder();
        while (true) {
            int read = this.input.read();
            if (read == -1) {
                throw new IOException("Unexpected EOF");
            }
            if (read == 13) {
                if (this.input.read() == 10) {
                    if (sb.length() > 0) {
                        return sb.toString();
                    }
                    return null;
                }
            } else {
                if (read == 10) {
                    if (sb.length() > 0) {
                        return sb.toString();
                    }
                    return null;
                }
                sb.append((char) read);
            }
        }
    }

    private void parseBodyAttributes() throws IOException {
        if (this.dataLength > 0) {
            byte[] newBytes = Utils.newBytes(this.dataLength);
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 < this.dataLength) {
                    i = i2 + this.input.read(newBytes, i2, this.dataLength - i2);
                } else {
                    parseAttributes(new String(newBytes, StandardCharsets.UTF_8));
                    return;
                }
            }
        }
    }

    private void parseAttributes(String str) {
        int indexOf;
        String str2;
        trace("data=" + str);
        while (str != null && (indexOf = str.indexOf(61)) >= 0) {
            String substring = str.substring(0, indexOf);
            str = str.substring(indexOf + 1);
            int indexOf2 = str.indexOf(38);
            if (indexOf2 >= 0) {
                str2 = str.substring(0, indexOf2);
                str = str.substring(indexOf2 + 1);
            } else {
                str2 = str;
            }
            this.attributes.put(substring, StringUtils.urlDecode(str2));
        }
        trace(this.attributes.toString());
    }

    private boolean parseHeader() throws IOException {
        Locale locale;
        boolean z = false;
        trace("parseHeader");
        int i = 0;
        this.host = null;
        this.ifModifiedSince = null;
        boolean z2 = false;
        while (true) {
            String readHeaderLine = readHeaderLine();
            if (readHeaderLine == null) {
                break;
            }
            trace(CharSequenceUtil.SPACE + readHeaderLine);
            String lowerEnglish = StringUtils.toLowerEnglish(readHeaderLine);
            if (lowerEnglish.startsWith("host")) {
                this.host = getHeaderLineValue(readHeaderLine);
            } else if (lowerEnglish.startsWith("if-modified-since")) {
                this.ifModifiedSince = getHeaderLineValue(readHeaderLine);
            } else if (lowerEnglish.startsWith("connection")) {
                if (Constants.KEEP_ALIVE_HEADER_VALUE_TOKEN.equals(getHeaderLineValue(readHeaderLine))) {
                    z = true;
                }
            } else if (lowerEnglish.startsWith("content-type")) {
                if (getHeaderLineValue(readHeaderLine).startsWith("multipart/form-data")) {
                    z2 = true;
                }
            } else if (lowerEnglish.startsWith("content-length")) {
                i = Integer.parseInt(getHeaderLineValue(readHeaderLine));
                trace("len=" + i);
            } else if (lowerEnglish.startsWith("user-agent")) {
                if (lowerEnglish.contains("webkit/") && this.session != null) {
                    this.session.put("frame-border", CustomBooleanEditor.VALUE_1);
                    this.session.put("frameset-border", "2");
                }
            } else if (lowerEnglish.startsWith("accept-language")) {
                if ((this.session == null ? null : this.session.locale) == null) {
                    StringTokenizer stringTokenizer = new StringTokenizer(getHeaderLineValue(readHeaderLine), ",;");
                    while (true) {
                        if (stringTokenizer.hasMoreTokens()) {
                            String nextToken = stringTokenizer.nextToken();
                            if (!nextToken.startsWith("q=") && this.server.supportsLanguage(nextToken)) {
                                int indexOf = nextToken.indexOf(45);
                                if (indexOf >= 0) {
                                    locale = new Locale(nextToken.substring(0, indexOf), nextToken.substring(indexOf + 1));
                                } else {
                                    locale = new Locale(nextToken, "");
                                }
                                this.headerLanguage = locale.getLanguage();
                                if (this.session != null) {
                                    this.session.locale = locale;
                                    this.session.put("language", this.headerLanguage);
                                    this.server.readTranslations(this.session, this.headerLanguage);
                                }
                            }
                        }
                    }
                }
            } else if (StringUtils.isWhitespaceOrEmpty(readHeaderLine)) {
                break;
            }
        }
        this.dataLength = 0;
        if (!z2 && i > 0) {
            this.dataLength = i;
        }
        return z;
    }

    private static String getHeaderLineValue(String str) {
        return StringUtils.trimSubstring(str, str.indexOf(58) + 1);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.h2.server.web.WebApp
    public String adminShutdown() {
        stopNow();
        return super.adminShutdown();
    }

    private boolean allow() {
        if (this.server.getAllowOthers()) {
            return true;
        }
        try {
            return NetUtils.isLocalAddress(this.socket);
        } catch (UnknownHostException e) {
            this.server.traceError(e);
            return false;
        }
    }

    private void trace(String str) {
        this.server.trace(str);
    }
}
