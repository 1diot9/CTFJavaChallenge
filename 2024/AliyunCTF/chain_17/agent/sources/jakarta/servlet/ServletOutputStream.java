package jakarta.servlet;

import java.io.CharConversionException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ResourceBundle;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/ServletOutputStream.class */
public abstract class ServletOutputStream extends OutputStream {
    private static final String LSTRING_FILE = "jakarta.servlet.LocalStrings";
    private static final ResourceBundle lStrings = ResourceBundle.getBundle(LSTRING_FILE);

    public abstract boolean isReady();

    public abstract void setWriteListener(WriteListener writeListener);

    public void print(String s) throws IOException {
        if (s == null) {
            s = "null";
        }
        int len = s.length();
        byte[] buffer = new byte[len];
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if ((c & 65280) != 0) {
                String errMsg = lStrings.getString("err.not_iso8859_1");
                Object[] errArgs = {Character.valueOf(c)};
                throw new CharConversionException(MessageFormat.format(errMsg, errArgs));
            }
            buffer[i] = (byte) (c & 255);
        }
        write(buffer);
    }

    public void print(boolean b) throws IOException {
        String msg;
        if (b) {
            msg = lStrings.getString("value.true");
        } else {
            msg = lStrings.getString("value.false");
        }
        print(msg);
    }

    public void print(char c) throws IOException {
        print(String.valueOf(c));
    }

    public void print(int i) throws IOException {
        print(String.valueOf(i));
    }

    public void print(long l) throws IOException {
        print(String.valueOf(l));
    }

    public void print(float f) throws IOException {
        print(String.valueOf(f));
    }

    public void print(double d) throws IOException {
        print(String.valueOf(d));
    }

    public void println() throws IOException {
        print("\r\n");
    }

    public void println(String s) throws IOException {
        print(s + "\r\n");
    }

    public void println(boolean b) throws IOException {
        StringBuilder sb = new StringBuilder();
        if (b) {
            sb.append(lStrings.getString("value.true"));
        } else {
            sb.append(lStrings.getString("value.false"));
        }
        sb.append("\r\n");
        print(sb.toString());
    }

    public void println(char c) throws IOException {
        println(String.valueOf(c));
    }

    public void println(int i) throws IOException {
        println(String.valueOf(i));
    }

    public void println(long l) throws IOException {
        println(String.valueOf(l));
    }

    public void println(float f) throws IOException {
        println(String.valueOf(f));
    }

    public void println(double d) throws IOException {
        println(String.valueOf(d));
    }
}
