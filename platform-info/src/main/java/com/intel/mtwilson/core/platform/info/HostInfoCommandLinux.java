/*
 * Copyright (C) 2019 Intel Corporation
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.intel.mtwilson.core.platform.info;

import com.intel.mtwilson.core.common.ErrorCode;
import com.intel.mtwilson.core.common.PlatformInfoException;
import com.intel.mtwilson.core.common.model.*;
import com.intel.mtwilson.util.exec.Result;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import com.intel.mtwilson.core.common.model.HostComponents;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HostInfoCommandLinux implements HostInfoCommand {

    protected final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(getClass());

    private CommandLineRunner runner = new CommandLineRunner();

    public CommandLineRunner getRunner() {
        return runner;
    }

    public String getOsName() throws IOException, PlatformInfoException {
        Pair<String, String> osNameAndVersion = getOsNameAndVersion();
        return osNameAndVersion.getLeft();
    }

    public String getOsVersion() throws PlatformInfoException, IOException {
        Pair<String, String> osNameAndVersion = getOsNameAndVersion();
        return osNameAndVersion.getRight();
    }

    private Pair<String, String> getOsNameAndVersion() throws PlatformInfoException, IOException {
         /*
         Sample response of 'lsb_release -a'
         No LSB modules are available.
         Distributor ID: Ubuntu
         Description:    Ubuntu 11.10
         Release:        11.10
         Codename:       oneiric
         */

        log.debug("Fetching OS Name and OS Version using \"lsb_release -a\"");
        Result result = getRunner().executeCommand("lsb_release", "-a");
        String osName = "";
        String osVersion = "";
        if (result.getStdout() != null) {
            String[] resultArray = result.getStdout().split("\n");
            for (String str : resultArray) {
                String[] parts = str.split(":");

                if (parts != null && parts.length > 1) { //Get OS Name
                    if (parts[0].trim().equalsIgnoreCase("Distributor ID")) {
                        if (parts[1] != null) {
                            osName = parts[1].trim();
                        }
                    } else if (parts[0].trim().equalsIgnoreCase("Release")) { //Get OS Version
                        if (parts[1] != null) {
                            osVersion = parts[1].trim();
                        }
                    }
                }
            }
            log.debug("OS Name: " + osName);
            log.debug("OS Version: " + osVersion);
        } else {
            log.error("Error executing the lsb_release command to retrieve the OS details");
        }
        return new ImmutablePair<>(osName, osVersion);
    }

    @Override
    public String getBiosName() throws PlatformInfoException, IOException {
        String biosName = "";
        log.debug("Getting BIOS Name using dmidecode -s bios-vendor");
        Result dmidecodeResult = getRunner().executeCommand("dmidecode", "-s", "bios-vendor");
        /*
         Sample response of 'dmidecode -s bios-vendor'
         Intel Corp.
         */
        if (dmidecodeResult == null || dmidecodeResult.getStdout() == null) {
            throw new PlatformInfoException(ErrorCode.COMMAND_ERROR, "Command \"dmidecode -s bios-vendor\" gave a null response");
        }

        List<String> resultList = Arrays.asList(dmidecodeResult.getStdout().split("\n"));
        if (resultList != null && resultList.size() > 0) {
            for (String data : resultList) {
                if (data.trim().startsWith("#")) // ignore the comments
                {
                    continue;
                }
                biosName = data.trim();
                break;
            }
        }
        log.debug("BIOS Name: " + biosName);

        return biosName;
    }

    @Override
    public String getBiosVersion() throws IOException, PlatformInfoException {
        // Get BIOS Version
        String biosVersion = "";
        log.debug("Getting BIOS Version using \"dmidecode -s bios-version\"");
        Result dmidecodeResult = getRunner().executeCommand("dmidecode", "-s", "bios-version");
        /*
         Sample response of "dmidecode -s bios-version"
         S5500.86B.01.00.0060.090920111354
         */
        if (dmidecodeResult == null || dmidecodeResult.getStdout() == null) {
            throw new IOException("Command \"dmidecode -s bios-version\" gave a null response");
        }

        List<String> resultList = Arrays.asList(dmidecodeResult.getStdout().split("\n"));
        if (resultList != null && resultList.size() > 0) {
            for (String data : resultList) {
                if (data.trim().startsWith("#")) // ignore the comments
                {
                    continue;
                }
                biosVersion = data.trim();
                break;
            }
        }
        log.debug("BIOS Version: " + biosVersion);

        return biosVersion;
    }

    @Override
    public String getVmmName() {
        Pair<String, String> vmmNameAndVersion = getVmmNameAndVersion();
        return vmmNameAndVersion.getLeft();
    }

    @Override
    public String getVmmVersion() {
        Pair<String,String> vmmNameAndVersion = getVmmNameAndVersion();
        return vmmNameAndVersion.getRight();
    }

    private Pair<String, String> getVmmNameAndVersion() {
        Result result;
        String vmmName = "";
        String vmmVersion = "";
        try {
            log.debug("Getting docker version using \"docker -v\"");
            result = getRunner().executeCommand("docker", "-v");

            if (result == null || result.getExitCode() != 0 || result.getStdout() == null || result.getStdout().isEmpty()) {
                // If Docker is not installed the exitcode is 127
                log.info("Error running command docker version");
                log.info("Docker might not be installed or the docker version command returned a null response. Will check for the hypervisor installed.");
            } else {
                /*
                 Sample response of 'docker -v'
                 Docker version 1.9.1, build a34a1d5
                 */
                String cmdOutput = result.getStdout();
                log.debug("getVmmAndVersion: output of docker -v command is {}.", cmdOutput);
                String[] resultArray = cmdOutput.split("\n");
                if (resultArray.length > 0) {
                    if (resultArray[0].startsWith("Docker")) {
                        String[] versionInfo = resultArray[0].split(" ");
                        if (versionInfo[0] != null) {
                            vmmName = versionInfo[0];
                        }
                        if (versionInfo[2] != null) {
                            vmmVersion = versionInfo[2].substring(0, versionInfo[2].length() - 1); // remove the comma
                        }
                        log.debug("VMM Name: " + vmmName);
                        log.debug("VMM Version: " + vmmVersion);

                        return new ImmutablePair<>(vmmName, vmmVersion);
                    }
                }
                log.debug("command stdout: {}", result.getStdout());
            }
        }
        catch (PlatformInfoException | IOException ex){
            log.debug("Error while getting docker version, getting virsh version next", ex);
        }

        try {
            result = getRunner().executeCommand("virsh", "version");
            if (result == null || result.getExitCode() != 0) {
                String error = result == null ? "VMM is not installed" : result.getStderr();
		log.debug("Error in running command virsh version: {}", error);
		log.debug("KVM might not be installed or hardware virtualization is not enabled");
                throw new PlatformInfoException(ErrorCode.ERROR, error);
            }

            if (result.getStdout() == null || result.getStdout().isEmpty()) {
                log.info("getVmmAndVersion: empty virsh version file, assuming no VMM installed");
                vmmName = "Host_No_VMM";
                vmmVersion = "0.0";
                return new ImmutablePair<>(vmmName, vmmVersion);
            }

            if (result.getStdout() != null) {
                String cmdOutput = result.getStdout();
                log.debug("getVmmAndVersion: output of virsh version command is {}.", cmdOutput);
                String[] resultArray = cmdOutput.split("\n");

                //String[] result = "The program 'virsh' is currently not installed. You can install it by typing:\n apt-get install libvirt-bin".split("\n");
                // For hosts where VMM is not installed, the output of the above command would look something like
                // The program 'virsh' is currently not installed. You can install it by typing:
                // apt-get install libvirt-bin
                // and
                // for hosts where VMM is installed the output would be
                // Compiled against library: libvir 0.1.7
                // Using library: libvir 0.1.7
                // Using API: Xen 3.0.1
                // Running hypervisor: Xen 3.0.0
                // For cases where VMM is not installed, we would hardcode the VMM name and version as below. This is needed
                // for supporting hosts without VMM
                if (resultArray.length > 0) {
                    String virshCmdSupport = resultArray[0];
                    if (virshCmdSupport.startsWith("The program 'virsh' is currently not installed")) {
                        vmmName = "Host_No_Vmm";
                        vmmVersion = "0.0";
                        return new ImmutablePair<>(vmmName, vmmVersion);
                    } else {
                        for (String str : resultArray) {
                            String[] parts = str.split(":");

                            if (parts != null && parts.length > 1) {
                                if (parts[0].trim().equalsIgnoreCase("Running hypervisor")) {
                                    if (parts[1] != null) {
                                        String[] subParts = parts[1].trim().split(" ");
                                        if (subParts[0] != null) {
                                            vmmName = subParts[0];
                                        }
                                        if (subParts[1] != null) {
                                            vmmVersion = subParts[1];
                                        }
                                    }
                                }
                            }
                            log.debug("VMM Name: " + vmmName);
                            log.debug("VMM Version: " + vmmVersion);
                        }
                        if (vmmName == null) {
                            vmmName = "Host_No_VMM";
                            vmmVersion = "0.0";
                        }
                    }
                } else {
                    log.error("Unable to execute virsh command to retrieve the hypervisor details");
                }
            } else {
                log.error("Error executing the virsh version command to retrieve the hypervisor details.");
            }
        }
        catch (PlatformInfoException | IOException ex){
            log.debug("Error while getting virsh version", ex);
            vmmName = "";
            vmmVersion = "";
        }
        return new ImmutablePair<>(vmmName, vmmVersion);
    }


    /**
     * Retrieve the CPU ID of the processor. This is used to identify the
     * processor generation.
     *
     * @throws PlatformInfoException
     */
    @Override
    public String getProcessorInfo() throws PlatformInfoException, IOException {
        log.debug("Getting Processor Info using \"dmidecode --type processor\"");
        Result result = getRunner().executeCommand("dmidecode", "--type", "processor");
        String processorInfo = "";
        if (result.getStdout() != null) {
            String[] resultArray = result.getStdout().split("\n");
            /*
             Sample output would look like below for a 2 CPU system. We will extract the processor info between CPU and the @ sign
             Processor Information
             Socket Designation: CPU1
             Type: Central Processor
             Family: Xeon
             Manufacturer: Intel(R) Corporation
             ID: C2 06 02 00 FF FB EB BF -- This is the CPU ID
             Signature: Type 0, Family 6, Model 44, Stepping 2
             */
            for (String entry : resultArray) {
                if (entry != null && !entry.isEmpty() && entry.trim().startsWith("ID:")) {
                    String[] parts = entry.trim().split(":");
                    if (parts != null && parts.length > 1) {
                        processorInfo = parts[1].trim();
                        break;
                    }
                }
            }
            log.debug("Processor Information " + processorInfo);
        } else {
            log.error("Error retrieving the processor information");
        }
        return processorInfo;
    }

    @Override
    public String[] getProcessorFlags() throws PlatformInfoException, IOException {
        log.debug("Getting Processor Flags from /proc/cpuinfo");
        Result result = getRunner().executeCommand("cat", "/proc/cpuinfo");
        String[] processorFlags = new String[0];
        /*
         Sample response for 'cat /proc/cpuinfo'
         Architecture:          x86_64
         CPU op-mode(s):        32-bit, 64-bit
         Byte Order:            Little Endian
         CPU(s):                1
         On-line CPU(s) list:   0
         Thread(s) per core:    1
         Core(s) per socket:    1
         Socket(s):             1
         NUMA node(s):          1
         Vendor ID:             GenuineIntel
         CPU family:            6
         Model:                 26
         Model name:            Intel(R) Xeon(R) CPU E5-2699 v4 @ 2.20GHz
         Stepping:              4
         CPU MHz:               2194.917
         BogoMIPS:              4389.83
         Hypervisor vendor:     VMware
         Virtualization type:   full
         L1d cache:             32K
         L1i cache:             32K
         L2 cache:              256K
         L3 cache:              56320K
         NUMA node0 CPU(s):     0
         Flags:                 fpu vme de pse tsc msr pae mce cx8 apic sep
                                mtrr pge mca cmov pat pse36 clflush dts mmx
                                fxsr sse sse2 ss syscall nx rdtscp lm
                                constant_tsc arch_perfmon pebs bts nopl
                                xtopology tsc_reliable nonstop_tsc aperfmperf
                                pni ssse3 cx16 sse4_1 sse4_2 x2apic popcnt
                                tsc_deadline_timer hypervisor lahf_lm epb
                                tsc_adjust dtherm ida arat pln pts
         */
        if (result.getStdout() != null) {
            String[] resultArray = result.getStdout().split("\n");
            for (String str : resultArray) {
                String[] parts = str.split(":");

                if (parts != null && parts.length > 1) { //Get Processor Flags
                    if (parts[0].trim().equalsIgnoreCase("Flags")) {
                        if (parts[1] != null) {
                            processorFlags = parts[1].trim().split(" ");
                        }
                    }
                }
            }
            log.debug("Processor flags: " + processorFlags);
        } else {
            log.error("Error retrieving the processor flags");
        }
        return processorFlags;
    }

    @Override
    public String getHardwareUUID() throws IOException, PlatformInfoException {
        String hardwareUUID = "";
        log.debug("Getting Hardware UUID using \"dmidecode -s system-uuid\"");
        Result result = getRunner().executeCommand("dmidecode", "-s", "system-uuid");
        /*
         Sample response for 'dmidecode -s system-uuid'
         4235D571-8542-FFD3-5BFE-6D9DAC874C84
         */
        if (result == null || result.getStdout() == null) {
            throw new PlatformInfoException(ErrorCode.COMMAND_ERROR, "Command \"dmidecode -s system-uuid\" gave a null response");
        }

        List<String> resultList = Arrays.asList(result.getStdout().split("\n"));
        if (resultList != null && resultList.size() > 0) {
            for (String data : resultList) {
                if (data.trim().startsWith("#")) { // ignore the comments
                    continue;
                }
                hardwareUUID = data.trim();
                break;
            }
        }
        log.debug("Context set with host UUID info: " + hardwareUUID);
        return hardwareUUID;
    }

    @Override
    public String getTpmVersion() {
        if (!new File("/dev/tpm0").exists()) { //No TPM chip present on host
            return "0";
        } else if (new File("/sys/class/misc/tpm0/device/caps").exists()
                || new File("/sys/class/tpm/tpm0/device/caps").exists()) {
            log.debug("TPM Version: 1.2");
            return "1.2";
        } else {
            log.debug("TPM Version: 2.0");
            return "2.0";
        }
    }


    @Override
    public String getHostName() throws PlatformInfoException, IOException {
        String hostname = "";
        log.debug("Getting hostname using \"hostname\"");
        Result result = getRunner().executeCommand("hostname");
        /*
         Sample response for 'hostname'
         RedHat
         */
        if (result == null || result.getStdout() == null) {
            throw new IOException("Command \"hostname\" gave a null response");
        }

        List<String> resultList = Arrays.asList(result.getStdout().split("\n"));
        if (resultList != null && resultList.size() > 0) {
            for (String data : resultList) {
                hostname = data.trim();
                break;
            }
        }
        log.debug("Context set with host name info: " + hostname);
        return hostname;
    }

    @Override
    public int getNumberOfSockets() throws PlatformInfoException, IOException {
        int numberOfSockets = 0;
        Result result = getRunner().executeCommand("lscpu");
        /*
        Sample response for 'number of sockets'
        Architecture:          x86_64
        CPU op-mode(s):        32-bit, 64-bit
        Byte Order:            Little Endian
        CPU(s):                72
        On-line CPU(s) list:   0-71
        Thread(s) per core:    2
        Core(s) per socket:    18
        Socket(s):             2
        NUMA node(s):          2
        Vendor ID:             GenuineIntel
        CPU family:            6
        Model:                 63
        Model name:            Intel(R) Xeon(R) CPU E5-2699 v3 @ 2.30GHz
        Stepping:              2
        CPU MHz:               2229.023
        BogoMIPS:              4595.69
        Virtualization:        VT-x
        L1d cache:             32K
        L1i cache:             32K
        L2 cache:              256K
        L3 cache:              46080K
        NUMA node0 CPU(s):     0-17,36-53
        NUMA node1 CPU(s):     18-35,54-71
         */
        if (result.getStdout() != null) {
            String[] resultArray = result.getStdout().split("\n");
            for (String entry : resultArray) {
                if (entry != null && !entry.isEmpty() && entry.trim().startsWith("Socket(s):")) {
                    String[] parts = entry.trim().split(":");
                    if (parts != null && parts.length > 1) {
                        numberOfSockets = Integer.valueOf(parts[1].trim());
                        break;
                    }
                }
            }
            log.debug("Context is being set with number of sockets: " + numberOfSockets);
        } else {
            log.error("Error retrieving the number of sockets");
        }
        return numberOfSockets;
    }

    @Override
    public boolean getTpmEnabled() {
        boolean tpmEnabled = false;
        final String tpmVersion = getTpmVersion();
        try {
            if (tpmVersion.equals("1.2")) {
                Result result = getRunner().executeCommand("cat", "/sys/class/tpm/tpm0/device/enabled");
                if (result.getStdout() != null) {
                    tpmEnabled = result.getStdout().trim().equals("1");
                    log.debug("The TPM status is : {}", tpmEnabled);
                }
            } else if (tpmVersion.equals("2.0")) {
                if (new File("/sys/class/tpm/tpm0/device/description").exists()) {
                    tpmEnabled = true;
                } else tpmEnabled = new File("/sys/class/tpm/tpm0/device/firmware_node/description").exists();
                log.debug("The TPM status is : {}", tpmEnabled);
                return tpmEnabled;
            } else {
                log.error("Error in getting the TPM status");
            }
        } catch (PlatformInfoException | IOException Ex) {
            log.error("Exception in getting the TPM status - {}", Ex.getMessage());
        }
        return tpmEnabled;
    }

    @Override
    public String getTxtStatus() {
        FeatureStatus txtStatus = FeatureStatus.UNSUPPORTED;
        try {
            log.debug("Getting TXT Support using 'cpuid -1'");
            Result result = getRunner().executeCommand("cpuid","-1");
            String commandResult = result.getStdout();

            if (commandResult != null) {
                log.debug("Getting TXT status using 'rdmsr 0x3a -f 1:0'");
                if(commandResult.contains("VMX: virtual machine extensions         = true")) {
                    result = getRunner().executeCommand("rdmsr", "0x3a", "-f", "1:0");
                    if(result.getStdout() != null) {
                        if (result.getStdout().trim().equals("3")) {
                            txtStatus = FeatureStatus.ENABLED;
                        } else {
                            txtStatus = FeatureStatus.DISABLED;
                        }
                    } else {
                        log.debug("Error during executing 'rdmsr 0x3a' command");
                    }
                }
                log.debug("The TXT status is : {}", txtStatus);

            } else {
                log.debug("Error during executing 'cpuid -1' command");
            }
        } catch (PlatformInfoException | IOException Ex) {
            log.debug("Exception during executing 'cpuid -1' or 'rdmsr 0x3a'command - {}", Ex.getMessage());
        }
        return txtStatus.getValue();
    }

    @Override
    public String getCbntStatus() {
        FeatureStatus cbntStatus = FeatureStatus.UNSUPPORTED;
        try {
            log.debug("Getting CBnT Status using \"rdmsr -xf 32:32 0x13A\"");
            Result result = getRunner().executeCommand("rdmsr", "-f", "32:32", "0x13A");

            if (result.getExitCode() == 0 && result.getStdout() != null) {
                String output = result.getStdout().trim();
                /*
                The MSR 0x13a [32] bit is set if CBnT is supported
                 */
                if (Integer.valueOf(output) == 1) {
                    /*
                     Boot guard disabled is bootguard with profile 0
                     BTGP0 has MSR[7:4] = 0 (Verify/Measure/FACB) and MSR[0] = 0 (BTG enabled and passed startup ACM)
                     */
                    Result bitsVMF = getRunner().executeCommand("rdmsr", "-f", "7:4", "0x13A");
                    Result bitBTGEnabled = getRunner().executeCommand("rdmsr", "-f", "0:0", "0x13A");
                    if (Integer.valueOf(bitsVMF.getStdout().trim()) == 0 && Integer.valueOf(bitBTGEnabled.getStdout().trim()) == 0){
                        cbntStatus = FeatureStatus.DISABLED;
                    } else {
                        cbntStatus = FeatureStatus.ENABLED;
                    }
                }
                log.debug("The CBnT status is : {}", cbntStatus.getValue());
            } else {
                log.debug("Error during executing 'rdmsr -xf 32:32 0x13A' command");
            }
        } catch (PlatformInfoException | IOException Ex) {
            log.debug("Exception during executing 'rdmsr -xf 32:32 0x13A' command - {}", Ex.getMessage());
        }
        return cbntStatus.getValue();
    }


    @Override
    public String getCbntProfile() {
        BootGuardProfile cbntProfile = null;
        if(getCbntStatus().equals(FeatureStatus.ENABLED.getValue())) {
            try {
                log.debug("Getting CBNT Profile using \"rdmsr -f 7:0 0x13A\"");
                Result result = getRunner().executeCommand("rdmsr", "-f", "7:0", "0x13A");
                if (result.getStdout() != null) {
                    String output = result.getStdout().trim().toUpperCase();
                    if (BootGuardProfile.BTGP5.getValue().equals(output)) {
                        cbntProfile = BootGuardProfile.BTGP5;
                    } else if (BootGuardProfile.BTGP4.getValue().equals(output)) {
                        cbntProfile = BootGuardProfile.BTGP4;
                    }
                    log.debug("The CBNT profile is : {}", cbntProfile);
                } else {
                    log.debug("Error during executing 'rdmsr -f 7:0 0x13A' command");
                }
            } catch (PlatformInfoException | IOException Ex) {
                log.debug("Exception during executing 'rdmsr -f 7:0 0x13A' command - {}", Ex.getMessage());
            }
        }
        return cbntProfile == null ? "" : cbntProfile.getName();
    }

    @Override
    public String getSuefiStatus() {
        FeatureStatus suefiEnabled = FeatureStatus.UNSUPPORTED; // EFI variables are not supported on this system
        try {
            log.debug("Getting SUEFI Status using \"bootctl status\"");
            Result result = getRunner().executeCommand("bootctl", "status");
            if (result.getStdout() != null) {
                if(result.getStdout().contains("Secure Boot: enabled")) { // SecureBoot enabled
                    suefiEnabled = FeatureStatus.ENABLED;
                } else if(result.getStdout().contains("Secure Boot: disabled")) { // SecureBoot disabled
                    suefiEnabled = FeatureStatus.DISABLED;
                }
                else {
                    suefiEnabled = FeatureStatus.UNSUPPORTED;
                }
                log.debug("The SUEFI status is : {}", suefiEnabled);
            } else {
                log.debug("Error during executing 'bootctl status' command");
            }
        } catch (PlatformInfoException | IOException Ex) {
            log.debug("Exception during executing 'bootctl status' command - {}", Ex.getMessage());
        }
        return suefiEnabled.getValue();
    }

    @Override
    public String getTbootStatus() throws PlatformInfoException, IOException {
        ComponentStatus tbootInstalled = ComponentStatus.NOT_INSTALLED;
        try {
            log.debug("Getting Tboot Status using \"txt-stat\"");
            Result result = getRunner().executeCommand("txt-stat");
            if (result.getStdout() != null) {
                tbootInstalled = ComponentStatus.INSTALLED;
                log.debug("The Tboot status is : {}", tbootInstalled);
            } else {
                log.debug("Error during executing 'txt-stat' command");
            }
        } catch (PlatformInfoException | IOException Ex) {
            log.debug("Exception during executing 'txt-stat' command - {}", Ex.getMessage());
        }
        return tbootInstalled.getValue();
    }

    @Override
    public Set<String> getInstalledComponents() {
        Set<String> installedComponents = new HashSet<>();
        for (String component : HostComponents.getValues()) {
            if (component.equals("tagent")) {
                installedComponents.add(component);
            } else {
                log.debug("Running {} status command...", component);
                if (isComponentInstalled(component)) {
                    installedComponents.add(component);
                }
            }
        }
        return installedComponents;
    }

    @Override
    public boolean isDockerEnv() throws PlatformInfoException, IOException {
        return new File("/.dockerenv").exists();
    }

    public boolean isComponentInstalled(String componentName) {
        try{
            Result result = getRunner().executeCommand(componentName, "status");
            if (result.getExitCode() == 0 && result.getStdout() != null) {
                String output = result.getStdout().trim();
                if (!output.contains("No such file or directory") || !output.contains("command not found")) {
                    return true;
                }
            }
        } catch (PlatformInfoException | IOException Ex) {
            log.debug("Exception during executing {} status command", componentName, Ex.getMessage());
        }
        return false;
    }
}
