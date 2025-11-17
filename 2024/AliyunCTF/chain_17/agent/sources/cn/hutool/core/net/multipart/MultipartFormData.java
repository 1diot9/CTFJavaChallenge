package cn.hutool.core.net.multipart;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.multi.ListValueMap;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/net/multipart/MultipartFormData.class */
public class MultipartFormData {
    private final ListValueMap<String, String> requestParameters;
    private final ListValueMap<String, UploadFile> requestFiles;
    private final UploadSetting setting;
    private boolean loaded;

    public MultipartFormData() {
        this(null);
    }

    public MultipartFormData(UploadSetting uploadSetting) {
        this.requestParameters = new ListValueMap<>();
        this.requestFiles = new ListValueMap<>();
        this.setting = uploadSetting == null ? new UploadSetting() : uploadSetting;
    }

    /* JADX WARN: Code restructure failed: missing block: B:20:0x009e, code lost:            r0.reset();     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x00ac, code lost:            return;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void parseRequestStream(java.io.InputStream r6, java.nio.charset.Charset r7) throws java.io.IOException {
        /*
            r5 = this;
            r0 = r5
            r0.setLoaded()
            cn.hutool.core.net.multipart.MultipartRequestInputStream r0 = new cn.hutool.core.net.multipart.MultipartRequestInputStream
            r1 = r0
            r2 = r6
            r1.<init>(r2)
            r8 = r0
            r0 = r8
            byte[] r0 = r0.readBoundary()
        L12:
            r0 = r8
            r1 = r7
            cn.hutool.core.net.multipart.UploadFileHeader r0 = r0.readDataHeader(r1)
            r9 = r0
            r0 = r9
            if (r0 != 0) goto L21
            goto Lac
        L21:
            r0 = r9
            boolean r0 = r0.isFile
            r1 = 1
            if (r0 != r1) goto L73
            r0 = r9
            java.lang.String r0 = r0.fileName
            r10 = r0
            r0 = r10
            int r0 = r0.length()
            if (r0 <= 0) goto L4d
            r0 = r9
            java.lang.String r0 = r0.contentType
            java.lang.String r1 = "application/x-macbinary"
            boolean r0 = r0.contains(r1)
            if (r0 == 0) goto L4d
            r0 = r8
            r1 = 128(0x80, double:6.3E-322)
            r0.skipBytes(r1)
        L4d:
            cn.hutool.core.net.multipart.UploadFile r0 = new cn.hutool.core.net.multipart.UploadFile
            r1 = r0
            r2 = r9
            r3 = r5
            cn.hutool.core.net.multipart.UploadSetting r3 = r3.setting
            r1.<init>(r2, r3)
            r11 = r0
            r0 = r11
            r1 = r8
            boolean r0 = r0.processStream(r1)
            if (r0 == 0) goto L70
            r0 = r5
            r1 = r9
            java.lang.String r1 = r1.formFieldName
            r2 = r11
            r0.putFile(r1, r2)
        L70:
            goto L81
        L73:
            r0 = r5
            r1 = r9
            java.lang.String r1 = r1.formFieldName
            r2 = r8
            r3 = r7
            java.lang.String r2 = r2.readString(r3)
            r0.putParameter(r1, r2)
        L81:
            r0 = r8
            r1 = 1
            r0.skipBytes(r1)
            r0 = r8
            r1 = 1
            r0.mark(r1)
            r0 = r8
            int r0 = r0.read()
            r10 = r0
            r0 = r10
            r1 = -1
            if (r0 == r1) goto L9e
            r0 = r10
            r1 = 45
            if (r0 != r1) goto La5
        L9e:
            r0 = r8
            r0.reset()
            goto Lac
        La5:
            r0 = r8
            r0.reset()
            goto L12
        Lac:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: cn.hutool.core.net.multipart.MultipartFormData.parseRequestStream(java.io.InputStream, java.nio.charset.Charset):void");
    }

    public String getParam(String paramName) {
        List<String> values = getListParam(paramName);
        if (CollUtil.isNotEmpty((Collection<?>) values)) {
            return values.get(0);
        }
        return null;
    }

    public Set<String> getParamNames() {
        return this.requestParameters.keySet();
    }

    public String[] getArrayParam(String paramName) {
        List<String> listParam = getListParam(paramName);
        if (null != listParam) {
            return (String[]) listParam.toArray(new String[0]);
        }
        return null;
    }

    public List<String> getListParam(String paramName) {
        return (List) this.requestParameters.get(paramName);
    }

    public Map<String, String[]> getParamMap() {
        return Convert.toMap(String.class, String[].class, getParamListMap());
    }

    public ListValueMap<String, String> getParamListMap() {
        return this.requestParameters;
    }

    public UploadFile getFile(String paramName) {
        UploadFile[] values = getFiles(paramName);
        if (values != null && values.length > 0) {
            return values[0];
        }
        return null;
    }

    public UploadFile[] getFiles(String paramName) {
        List<UploadFile> fileList = getFileList(paramName);
        if (null != fileList) {
            return (UploadFile[]) fileList.toArray(new UploadFile[0]);
        }
        return null;
    }

    public List<UploadFile> getFileList(String paramName) {
        return (List) this.requestFiles.get(paramName);
    }

    public Set<String> getFileParamNames() {
        return this.requestFiles.keySet();
    }

    public Map<String, UploadFile[]> getFileMap() {
        return Convert.toMap(String.class, UploadFile[].class, getFileListValueMap());
    }

    public ListValueMap<String, UploadFile> getFileListValueMap() {
        return this.requestFiles;
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    private void putFile(String name, UploadFile uploadFile) {
        this.requestFiles.putValue(name, uploadFile);
    }

    private void putParameter(String name, String value) {
        this.requestParameters.putValue(name, value);
    }

    private void setLoaded() throws IOException {
        if (this.loaded) {
            throw new IOException("Multi-part request already parsed.");
        }
        this.loaded = true;
    }
}
