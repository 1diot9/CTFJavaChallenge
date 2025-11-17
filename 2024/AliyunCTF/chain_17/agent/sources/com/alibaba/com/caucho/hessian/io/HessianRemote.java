package com.alibaba.com.caucho.hessian.io;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/HessianRemote.class */
public class HessianRemote {
    private String type;
    private String url;

    public HessianRemote(String type, String url) {
        this.type = type;
        this.url = url;
    }

    public HessianRemote() {
    }

    public String getType() {
        return this.type;
    }

    public String getURL() {
        return this.url;
    }

    public void setURL(String url) {
        this.url = url;
    }

    public int hashCode() {
        return this.url.hashCode();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof HessianRemote)) {
            return false;
        }
        HessianRemote remote = (HessianRemote) obj;
        return this.url.equals(remote.url);
    }

    public String toString() {
        return "[HessianRemote " + this.url + "]";
    }
}
