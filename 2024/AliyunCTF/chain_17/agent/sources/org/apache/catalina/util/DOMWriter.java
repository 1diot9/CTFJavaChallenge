package org.apache.catalina.util;

import java.io.PrintWriter;
import java.io.Writer;
import org.apache.tomcat.util.security.Escape;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/util/DOMWriter.class */
public class DOMWriter {
    private final PrintWriter out;

    public DOMWriter(Writer writer) {
        this.out = new PrintWriter(writer);
    }

    public void print(Node node) {
        if (node == null) {
            return;
        }
        int type = node.getNodeType();
        switch (type) {
            case 1:
                this.out.print('<');
                this.out.print(node.getLocalName());
                Attr[] attrs = sortAttributes(node.getAttributes());
                for (Attr attr : attrs) {
                    this.out.print(' ');
                    this.out.print(attr.getLocalName());
                    this.out.print("=\"");
                    this.out.print(Escape.xml("", true, attr.getNodeValue()));
                    this.out.print('\"');
                }
                this.out.print('>');
                printChildren(node);
                break;
            case 3:
                this.out.print(Escape.xml("", true, node.getNodeValue()));
                break;
            case 4:
                this.out.print(Escape.xml("", true, node.getNodeValue()));
                break;
            case 5:
                printChildren(node);
                break;
            case 7:
                this.out.print("<?");
                this.out.print(node.getLocalName());
                String data = node.getNodeValue();
                if (data != null && data.length() > 0) {
                    this.out.print(' ');
                    this.out.print(data);
                }
                this.out.print("?>");
                break;
            case 9:
                print(((Document) node).getDocumentElement());
                this.out.flush();
                break;
        }
        if (type == 1) {
            this.out.print("</");
            this.out.print(node.getLocalName());
            this.out.print('>');
        }
        this.out.flush();
    }

    private void printChildren(Node node) {
        NodeList children = node.getChildNodes();
        if (children != null) {
            int len = children.getLength();
            for (int i = 0; i < len; i++) {
                print(children.item(i));
            }
        }
    }

    private Attr[] sortAttributes(NamedNodeMap attrs) {
        if (attrs == null) {
            return new Attr[0];
        }
        int len = attrs.getLength();
        Attr[] array = new Attr[len];
        for (int i = 0; i < len; i++) {
            array[i] = (Attr) attrs.item(i);
        }
        for (int i2 = 0; i2 < len - 1; i2++) {
            String name = array[i2].getLocalName();
            int index = i2;
            for (int j = i2 + 1; j < len; j++) {
                String curName = array[j].getLocalName();
                if (curName.compareTo(name) < 0) {
                    name = curName;
                    index = j;
                }
            }
            if (index != i2) {
                Attr temp = array[i2];
                array[i2] = array[index];
                array[index] = temp;
            }
        }
        return array;
    }
}
