package cn.hutool.system.oshi;

import cn.hutool.core.util.NumberUtil;
import java.text.DecimalFormat;
import oshi.hardware.CentralProcessor;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/system/oshi/CpuInfo.class */
public class CpuInfo {
    private static final DecimalFormat LOAD_FORMAT = new DecimalFormat("#.00");
    private Integer cpuNum;
    private double toTal;
    private double sys;
    private double user;
    private double wait;
    private double free;
    private String cpuModel;
    private CpuTicks ticks;

    public CpuInfo() {
    }

    public CpuInfo(CentralProcessor processor, long waitingTime) {
        init(processor, waitingTime);
    }

    public CpuInfo(Integer cpuNum, double toTal, double sys, double user, double wait, double free, String cpuModel) {
        this.cpuNum = cpuNum;
        this.toTal = toTal;
        this.sys = sys;
        this.user = user;
        this.wait = wait;
        this.free = free;
        this.cpuModel = cpuModel;
    }

    public Integer getCpuNum() {
        return this.cpuNum;
    }

    public void setCpuNum(Integer cpuNum) {
        this.cpuNum = cpuNum;
    }

    public double getToTal() {
        return this.toTal;
    }

    public void setToTal(double toTal) {
        this.toTal = toTal;
    }

    public double getSys() {
        return this.sys;
    }

    public void setSys(double sys) {
        this.sys = sys;
    }

    public double getUser() {
        return this.user;
    }

    public void setUser(double user) {
        this.user = user;
    }

    public double getWait() {
        return this.wait;
    }

    public void setWait(double wait) {
        this.wait = wait;
    }

    public double getFree() {
        return this.free;
    }

    public void setFree(double free) {
        this.free = free;
    }

    public String getCpuModel() {
        return this.cpuModel;
    }

    public void setCpuModel(String cpuModel) {
        this.cpuModel = cpuModel;
    }

    public CpuTicks getTicks() {
        return this.ticks;
    }

    public void setTicks(CpuTicks ticks) {
        this.ticks = ticks;
    }

    public double getUsed() {
        return NumberUtil.sub(100.0f, this.free);
    }

    public String toString() {
        return "CpuInfo{CPU核心数=" + this.cpuNum + ", CPU总的使用率=" + this.toTal + ", CPU系统使用率=" + this.sys + ", CPU用户使用率=" + this.user + ", CPU当前等待率=" + this.wait + ", CPU当前空闲率=" + this.free + ", CPU利用率=" + getUsed() + ", CPU型号信息='" + this.cpuModel + "'}";
    }

    private void init(CentralProcessor processor, long waitingTime) {
        CpuTicks ticks = new CpuTicks(processor, waitingTime);
        this.ticks = ticks;
        this.cpuNum = Integer.valueOf(processor.getLogicalProcessorCount());
        this.cpuModel = processor.toString();
        long totalCpu = ticks.totalCpu();
        this.toTal = totalCpu;
        this.sys = formatDouble(ticks.cSys, totalCpu);
        this.user = formatDouble(ticks.user, totalCpu);
        this.wait = formatDouble(ticks.ioWait, totalCpu);
        this.free = formatDouble(ticks.idle, totalCpu);
    }

    private static double formatDouble(long tick, long totalCpu) {
        if (0 == totalCpu) {
            return 0.0d;
        }
        return Double.parseDouble(LOAD_FORMAT.format(tick <= 0 ? 0.0d : (100.0d * tick) / totalCpu));
    }
}
