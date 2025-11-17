package org.apache.catalina.webresources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.startup.ExpandWar;
import org.apache.catalina.util.IOTools;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.scan.Constants;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/webresources/ExtractingRoot.class */
public class ExtractingRoot extends StandardRoot {
    private static final StringManager sm = StringManager.getManager((Class<?>) ExtractingRoot.class);
    private static final String APPLICATION_JARS_DIR = "application-jars";

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.webresources.StandardRoot
    public void processWebInfLib() throws LifecycleException {
        if (!super.isPackedWarFile()) {
            super.processWebInfLib();
            return;
        }
        File expansionTarget = getExpansionTarget();
        if (!expansionTarget.isDirectory() && !expansionTarget.mkdirs()) {
            throw new LifecycleException(sm.getString("extractingRoot.targetFailed", expansionTarget));
        }
        WebResource[] possibleJars = listResources("/WEB-INF/lib", false);
        for (WebResource possibleJar : possibleJars) {
            if (possibleJar.isFile() && possibleJar.getName().endsWith(".jar")) {
                try {
                    File dest = new File(expansionTarget, possibleJar.getName()).getCanonicalFile();
                    InputStream sourceStream = possibleJar.getInputStream();
                    try {
                        OutputStream destStream = new FileOutputStream(dest);
                        try {
                            IOTools.flow(sourceStream, destStream);
                            destStream.close();
                            if (sourceStream != null) {
                                sourceStream.close();
                            }
                            createWebResourceSet(WebResourceRoot.ResourceSetType.CLASSES_JAR, Constants.WEB_INF_CLASSES, dest.toURI().toURL(), "/");
                        } finally {
                        }
                    } finally {
                    }
                } catch (IOException ioe) {
                    throw new LifecycleException(sm.getString("extractingRoot.jarFailed", possibleJar.getName()), ioe);
                }
            }
        }
    }

    private File getExpansionTarget() {
        File tmpDir = (File) getContext().getServletContext().getAttribute("jakarta.servlet.context.tempdir");
        File expansionTarget = new File(tmpDir, APPLICATION_JARS_DIR);
        return expansionTarget;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.webresources.StandardRoot
    public boolean isPackedWarFile() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.webresources.StandardRoot, org.apache.catalina.util.LifecycleBase
    public void stopInternal() throws LifecycleException {
        super.stopInternal();
        if (super.isPackedWarFile()) {
            File expansionTarget = getExpansionTarget();
            ExpandWar.delete(expansionTarget);
        }
    }
}
