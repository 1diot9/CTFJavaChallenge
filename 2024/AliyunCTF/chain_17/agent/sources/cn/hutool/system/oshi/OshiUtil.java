package cn.hutool.system.oshi;

import java.util.List;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.ComputerSystem;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.hardware.Sensors;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/system/oshi/OshiUtil.class */
public class OshiUtil {
    private static final SystemInfo systemInfo = new SystemInfo();
    private static final HardwareAbstractionLayer hardware = systemInfo.getHardware();
    private static final OperatingSystem os = systemInfo.getOperatingSystem();

    public static OperatingSystem getOs() {
        return os;
    }

    public static OSProcess getCurrentProcess() {
        return os.getProcess(os.getProcessId());
    }

    public static HardwareAbstractionLayer getHardware() {
        return hardware;
    }

    public static ComputerSystem getSystem() {
        return hardware.getComputerSystem();
    }

    public static GlobalMemory getMemory() {
        return hardware.getMemory();
    }

    public static CentralProcessor getProcessor() {
        return hardware.getProcessor();
    }

    public static Sensors getSensors() {
        return hardware.getSensors();
    }

    public static List<HWDiskStore> getDiskStores() {
        return hardware.getDiskStores();
    }

    public static List<NetworkIF> getNetworkIFs() {
        return hardware.getNetworkIFs();
    }

    public static CpuInfo getCpuInfo() {
        return getCpuInfo(1000L);
    }

    public static CpuInfo getCpuInfo(long waitingTime) {
        return new CpuInfo(getProcessor(), waitingTime);
    }
}
