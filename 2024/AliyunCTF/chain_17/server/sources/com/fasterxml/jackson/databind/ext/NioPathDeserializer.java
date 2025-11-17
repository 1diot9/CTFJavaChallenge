package com.fasterxml.jackson.databind.ext;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.spi.FileSystemProvider;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ext/NioPathDeserializer.class */
public class NioPathDeserializer extends StdScalarDeserializer<Path> {
    private static final long serialVersionUID = 1;
    private static final boolean areWindowsFilePathsSupported;

    static {
        boolean isWindowsRootFound = false;
        File[] listRoots = File.listRoots();
        int length = listRoots.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            File file = listRoots[i];
            String path = file.getPath();
            if (path.length() < 2 || !Character.isLetter(path.charAt(0)) || path.charAt(1) != ':') {
                i++;
            } else {
                isWindowsRootFound = true;
                break;
            }
        }
        areWindowsFilePathsSupported = isWindowsRootFound;
    }

    public NioPathDeserializer() {
        super((Class<?>) Path.class);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Path deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (!p.hasToken(JsonToken.VALUE_STRING)) {
            return (Path) ctxt.handleUnexpectedToken(Path.class, p);
        }
        String value = p.getText();
        if (value.indexOf(58) < 0) {
            return Paths.get(value, new String[0]);
        }
        if (areWindowsFilePathsSupported && value.length() >= 2 && Character.isLetter(value.charAt(0)) && value.charAt(1) == ':') {
            return Paths.get(value, new String[0]);
        }
        try {
            URI uri = new URI(value);
            try {
                return Paths.get(uri);
            } catch (FileSystemNotFoundException cause) {
                try {
                    String scheme = uri.getScheme();
                    Iterator it = ServiceLoader.load(FileSystemProvider.class).iterator();
                    while (it.hasNext()) {
                        FileSystemProvider provider = (FileSystemProvider) it.next();
                        if (provider.getScheme().equalsIgnoreCase(scheme)) {
                            return provider.getPath(uri);
                        }
                    }
                    return (Path) ctxt.handleInstantiationProblem(handledType(), value, cause);
                } catch (ServiceConfigurationError e) {
                    e.addSuppressed(cause);
                    return (Path) ctxt.handleInstantiationProblem(handledType(), value, e);
                }
            } catch (Exception e2) {
                return (Path) ctxt.handleInstantiationProblem(handledType(), value, e2);
            }
        } catch (URISyntaxException e3) {
            return (Path) ctxt.handleInstantiationProblem(handledType(), value, e3);
        }
    }
}
