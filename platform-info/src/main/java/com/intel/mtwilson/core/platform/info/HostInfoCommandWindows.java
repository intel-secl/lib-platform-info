/*
 * Copyright (C) 2019 Intel Corporation
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.intel.mtwilson.core.platform.info;

import com.intel.mtwilson.core.common.ErrorCode;
import com.intel.mtwilson.core.common.PlatformInfoException;
import com.intel.mtwilson.core.common.model.ComponentStatus;
import com.intel.mtwilson.core.common.model.FeatureStatus;
import com.intel.mtwilson.util.exec.Result;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Logic to retrieve Platform/Architecture related information for Windows Host
 *
 * @author purvades
 * @author dtiwari
 */
public class HostInfoCommandWindows implements HostInfoCommand {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(HostInfoCommandWindows.class);

    private CommandLineRunner runner = new CommandLineRunner();

    public CommandLineRunner getRunner() {
        return runner;
    }

    @Override
    public String getOsName() throws IOException, PlatformInfoException {
        String osName = "";

        Result result = getRunner().executeCommand("wmic", "os", "get", "caption");
        if (result.getExitCode() != 0) {
            log.error("Error running command [wmic os get caption]: {}", result.getStderr());
            throw new PlatformInfoException(ErrorCode.ERROR, result.getStderr());
        }
        log.debug("command stdout: {}", result.getStdout());
        if (result.getStdout() != null) {
            String[] resultArray = result.getStdout().split("\n");
            if (resultArray.length > 1) {
                osName = resultArray[1].replaceAll("\\r", "").trim();
                log.debug("OS full Name: " + osName);
            } else {
                log.error("[wmic os get caption] does not return OS full name");
            }
        } else {
            log.error("Error executing the [wmic os get caption] to retrieve the OS details");
        }

        return osName;
    }

    @Override
    public String getOsVersion() throws PlatformInfoException, IOException {
        String osVersion = "";

        Result result = getRunner().executeCommand("wmic", "os", "get", "version");
        if (result.getExitCode() != 0) {
            log.error("Error running command [wmic os get version]: {}", result.getStderr());
            throw new PlatformInfoException(ErrorCode.ERROR, result.getStderr());
        }
        log.debug("command stdout: {}", result.getStdout());
        if (result.getStdout() != null) {
            String[] resultArray = result.getStdout().split("\n");
            if (resultArray.length > 1) {
                osVersion = resultArray[1].replaceAll("\\r|\\s", "");
                log.debug("OS version: " + osVersion);
            } else {
                log.error("[wmic os get version] does not return OS full name");
            }
        } else {
            log.error("Error executing the [wmic os get version] to retrieve the OS details");
        }

        return osVersion;
    }

    @Override
    public String getBiosName() throws IOException, PlatformInfoException {
        String biosName = "";
        Result result = getRunner().executeCommand("wmic", "bios", "get", "manufacturer");
        if (result.getExitCode() != 0) {
            log.error("Error running command [wmic bios get manufacturer]: {}", result.getStderr());
            throw new PlatformInfoException(ErrorCode.ERROR, result.getStderr());
        }
        log.debug("command stdout: {}", result.getStdout());
        if (result.getStdout() != null) {
            String[] resultArray = result.getStdout().split("\n");
            if (resultArray.length > 1) {
                biosName = resultArray[1].replaceAll("\\r|\\s", "");
                log.debug("OS full Name: " + biosName);
            } else {
                log.error("[wmic bios get manufacturer]");
            }
        } else {
            log.error("Error executing the [wmic bios get manufacturer]");
        }
        return biosName;
    }

    @Override
    public String getBiosVersion() throws PlatformInfoException, IOException {
        String biosVersion = "";
        Result result = getRunner().executeCommand("wmic", "bios", "get", "smbiosbiosversion");
        if (result.getExitCode() != 0) {
            log.error("Error running command [wmic bios get smbiosbiosversion]: {}", result.getStderr());
            throw new PlatformInfoException(ErrorCode.ERROR, result.getStderr());
        }
        log.debug("command stdout: {}", result.getStdout());
        if (result.getStdout() != null) {
            String[] resultArray = result.getStdout().split("\n");
            if (resultArray.length > 1) {
                biosVersion = resultArray[1].replaceAll("\\r|\\s", "");
                log.debug("Bios Version: " + biosVersion);
            } else {
                log.error("[wmic bios get smbiosbiosversion] does not return Bios Version");
            }
        } else {
            log.error("Error executing the [wmic bios get smbiosbiosversion]");
        }
        return biosVersion;
    }

    @Override
    public String getVmmName() throws IOException, PlatformInfoException {
        Pair<String,String> vmmNameAndVersion = getVmmNameAndVersion();
        return vmmNameAndVersion.getLeft();
    }

    @Override
    public String getVmmVersion() throws IOException, PlatformInfoException {
        Pair<String,String> vmmNameAndVersion = getVmmNameAndVersion();
        return vmmNameAndVersion.getRight();
    }

    private Pair<String, String> getVmmNameAndVersion() throws PlatformInfoException, IOException {
        String vmmName = "";
        String vmmVersion = "";
        boolean vmmEnabled = false;
        Result result = getRunner().executeCommand("wmic", "path", "WIN32_ServerFeature", "get", "ID");
        if (result.getExitCode() != 0) {
            log.error("Error running command [wmic path WIN32_ServerFeature get ID]: {}", result.getStderr());
            throw new PlatformInfoException(ErrorCode.ERROR, result.getStderr());
        }
        log.debug("command stdout: {}", result.getStdout());///
        if (result.getStdout() != null) {
            String[] resultArray = result.getStdout().split("\n");
            String vmmID = "" + 20;
            for (String str : resultArray) {
                str = str.replaceAll("\\s+", ""); //remove all whitespace
                if (str.equals(vmmID)) {
                    log.debug("Setting Hyper-V");
                    vmmName = "Microsoft Windows Hyper-V";
                    vmmEnabled = true;
                    break;
                }
            }
        } else {
            log.error("Error executing the [wmic path WIN32_ServerFeature get ID] to retrieve the VMM details");
        }

        if(!vmmEnabled) {
            return new ImmutablePair<>(vmmName, "");
        }

        result = getRunner().executeCommand("wmic", false, "datafile", "where", "\"name=\'C:\\\\Windows\\\\System32\\\\vmms.exe\'\"", "get", "version");
        if (result.getExitCode() != 0) {
            log.error("Error running command [wmic datafile where \"name=\'C:\\\\Windows\\\\System32\\\\vmms.exe\'\" get version]: {}", result.getStderr());
            throw new PlatformInfoException(ErrorCode.ERROR, result.getStderr());
        }
        log.debug("command stdout: {}", result.getStdout());
        if (result.getStdout() != null) {
            String[] resultArray = result.getStdout().split("\n");
            if (resultArray.length > 1) {
                vmmVersion = resultArray[1].replaceAll("\\r|\\s", "");
                log.debug("VMM version: " + vmmVersion);
            } else {
                log.error("[wmic datafile where \"name=\'C:\\\\Windows\\\\System32\\\\vmms.exe\'\" get version] does not return vmm version");
            }
        } else {
            log.error("Error executing [wmic datafile where \"name=\'C:\\\\Windows\\\\System32\\\\vmms.exe\'\" get version]");
        }

        return new ImmutablePair<>(vmmName, vmmVersion);
    }

    @Override
    public String getProcessorInfo() throws PlatformInfoException, IOException {
        Result result;
        String processorInfo = "";
        result = getRunner().executeCommand("wmic", "cpu", "get", "ProcessorId");
        if (result.getExitCode() != 0) {
            log.error("Error running command [wmic cpu get ProcessorId]: {}", result.getStderr());
            throw new PlatformInfoException(ErrorCode.ERROR, result.getStderr());
        }
        log.debug("command stdout: {}", result.getStdout());
        if (result.getStdout() != null) {
            String[] resultArray = result.getStdout().split("\n");
            if (resultArray.length > 1) {
                processorInfo = resultArray[1].replaceAll("\\r|\\s", "");
                log.debug("OS full Name: " + processorInfo);
            } else {
                log.error("[wmic cpu get ProcessorId] does not return ProcessorId");
            }
        } else {
            log.error("Error executing the [wmic cpu get ProcessorId]");
        }
        return processorInfo;
    }


    @Override
    public String[] getProcessorFlags() throws PlatformInfoException, IOException {
        Result result;
        String[] processorFlags = new String[0];
        File temp = null;
        try(InputStream fi = this.getClass().getClassLoader().getResourceAsStream("cpuid_amd64.exe")) {
            temp = File.createTempFile("temp_exe", "");
            Files.copy(fi, temp.toPath(), REPLACE_EXISTING);
            result = getRunner().executeCommand(temp.getPath());
        }
        finally {
            if(temp != null)
                temp.delete();
        }
        if (result.getExitCode() != 0) {
            log.error("Error running excutable [cpuid_amd64.exe]: {}", result.getStderr());
            throw new PlatformInfoException(ErrorCode.ERROR, result.getStderr());
        }
        log.debug("command stdout: {}", result.getStdout());
        if (result.getStdout() != null) {
            processorFlags = result.getStdout().split(" ");
            log.debug("Processor Flags: " + processorFlags);
        } else {
            log.error("Error executing the [cpuid_amd64.exe]");
        }

        return processorFlags;
    }

    @Override
    public String getHardwareUUID() throws IOException, PlatformInfoException {
        String hardwareUuid = "";
        Result result = getRunner().executeCommand("wmic", "path", "Win32_ComputerSystemProduct", "get", "uuid");
        if (result.getExitCode() != 0) {
            log.error("Error running command [wmic path Win32_ComputerSystemProduct get uuid]: {}", result.getStderr());
            throw new PlatformInfoException(ErrorCode.ERROR, result.getStderr());
        }
        log.debug("command stdout: {}", result.getStdout());
        if (result.getStdout() != null) {
            String[] resultArray = result.getStdout().split("\n");
            if (resultArray.length > 1) {
                hardwareUuid = resultArray[1].replaceAll("\\s|\\r", "");
                log.debug("Host UUID: " + hardwareUuid);
            } else {
                log.error("[wmic path Win32_ComputerSystemProduct get uuid] does not return uuid");
            }
        } else {
            log.error("Error executing the [wmic path Win32_ComputerSystemProduct get uuid]");
        }
        return hardwareUuid;
    }


    @Override
    public String getTpmVersion() throws PlatformInfoException, IOException {
        String tpmVersion = "";
        Result result = getRunner().executeCommand("wmic", "/namespace:\\\\root\\CIMV2\\Security\\MicrosoftTpm", "path", "Win32_Tpm", "get", "/value");
        /*
         Sample Response for the tpm command:
         IsActivated_InitialValue=TRUE
         IsEnabled_InitialValue=TRUE
         IsOwned_InitialValue=TRUE
         ManufacturerId=1398033696
         ManufacturerVersion=8.28
         ManufacturerVersionInfo=Not Supported
         PhysicalPresenceVersionInfo=1.2
         SpecVersion=1.2, 2, 3
         */
        if (result.getExitCode() != 0) {
            log.error("Error running command [wmic /namespace:\\\\root\\CIMV2\\Security\\MicrosoftTpm path Win32_Tpm get /value]: {}", result.getStderr());
            throw new PlatformInfoException(ErrorCode.ERROR, result.getStderr());
        }
        log.debug("command stdout: {}", result.getStdout());
        if (result.getStdout() != null) {
            String[] resultArray = result.getStdout().split("\n");
            if (resultArray.length > 1) {
                for (String str : resultArray) {
                    String[] parts = str.split("=");
                    if (parts != null && parts.length > 1) {
                        if (parts[0].trim().equalsIgnoreCase("SpecVersion")) {
                            if (parts[1] != null) {
                                tpmVersion = parts[1].split(",")[0];
                                log.debug("Tpm Version: " + tpmVersion);
                            }
                        }
                    }
                }
            } else {
                log.error("[wmic /namespace:\\\\root\\CIMV2\\Security\\MicrosoftTpm path Win32_Tpm get /value] does not return tpm version");
            }
        } else {
            log.error("Error executing the [wmic /namespace:\\\\root\\CIMV2\\Security\\MicrosoftTpm path Win32_Tpm get /value]");
        }
        return tpmVersion;
    }

    @Override
    public String getHostName() throws PlatformInfoException, IOException {
        String hostname = "";
        Result result = getRunner().executeCommand("wmic", "computersystem", "get", "Name");
        /*
         Sample Response for the host name:
         Name
         WIN-GLU9NEPGT1L
         */
        if (result.getExitCode() != 0) {
            log.error("Error running command [wmic computersystem get Name]: {}", result.getStderr());
            throw new PlatformInfoException(ErrorCode.ERROR, result.getStderr());
        }
        log.debug("command stdout: {}", result.getStdout());
        if (result.getStdout() != null) {
            String[] resultArray = result.getStdout().split("\n");
            if (resultArray.length > 1) {
                hostname = resultArray[1].replaceAll("\\r|\\s", "");
                log.debug("Host Name: " + hostname);
            } else {
                log.error("[wmic computersystem get Name] does not return host name");
            }
        } else {
            log.error("Error executing the [wmic computersystem get Name]");
        }

        return hostname;
    }

    @Override
    public int getNumberOfSockets() throws PlatformInfoException, IOException {
        int numberOfSockets = 0;
        Result result = getRunner().executeCommand("wmic", "cpu", "get", "SocketDesignation");
        /*
         Sample Response for the number of sockets:
         SocketDesignation
         CPU0
         CPU1
         */
        if (result.getExitCode() != 0) {
            log.error("Error running command [wmic cpu get SocketDesignation]: {}", result.getStderr());
            throw new PlatformInfoException(ErrorCode.ERROR, result.getStderr());
        }
        log.debug("command stdout: {}", result.getStdout());
        if (result.getStdout() != null) {
            String[] resultArray = result.getStdout().trim().split("\n");
            if (resultArray.length > 1) {
                if(resultArray[0].trim().replaceAll("[\u0000-\u001f]", "").contains("SocketDesignation")) {
                    numberOfSockets = resultArray.length - 1;
                    log.debug("Number of sockets: " + numberOfSockets);
                }
            } else {
                log.error("[wmic cpu get SocketDesignation] does not return number of sockets");
            }
        } else {
            log.error("Error executing the [wmic cpu get SocketDesignation]");
        }

        return numberOfSockets;
    }

    @Override
    public boolean getTpmEnabled() {
        try {
            Result result = getRunner().executeCommand("wmic", "/namespace:\\\\root\\CIMV2\\Security\\MicrosoftTpm", "path", "Win32_Tpm", "get", "/value");
            /*
         Sample Response for the tpm command:
         IsActivated_InitialValue=TRUE
         IsEnabled_InitialValue=TRUE
         IsOwned_InitialValue=TRUE
         ManufacturerId=1398033696
         ManufacturerVersion=8.28
         ManufacturerVersionInfo=Not Supported
         PhysicalPresenceVersionInfo=1.2
         SpecVersion=1.2, 2, 3
             */
            if (result.getExitCode() != 0) {
                log.error("Error running command [wmic /namespace:\\\\root\\CIMV2\\Security\\MicrosoftTpm path Win32_Tpm get /value]: {}", result.getStderr());
                throw new PlatformInfoException(ErrorCode.ERROR, result.getStderr());
            }
            log.debug("command stdout: {}", result.getStdout());
            if (result.getStdout() != null) {
                String[] resultArray = result.getStdout().split("\n");
                if (resultArray.length > 1) {
                    for (String str : resultArray) {
                        String[] parts = str.split("=");
                        if (parts != null && parts.length > 1) {
                            if (parts[0].trim().equalsIgnoreCase("IsEnabled_InitialValue")) {
                                if (parts[1].trim().equals("TRUE")) {
                                    return true;
                                } else {
                                    log.debug("The tpm enabled value is set to false");
                                    return false;
                                }
                            }
                        }
                    }
                } else {
                    log.error("[wmic /namespace:\\\\root\\CIMV2\\Security\\MicrosoftTpm path Win32_Tpm get /value] does not return tpm imfomation");
                }
            } else {
                log.error("Error executing the [wmic /namespace:\\\\root\\CIMV2\\Security\\MicrosoftTpm path Win32_Tpm get /value]");
            }
            return false;
        } catch (PlatformInfoException | IOException Ex) {
            return false;
        }

    }

    @Override
    public String getTxtStatus() {
        return FeatureStatus.DISABLED.getValue();
    }

    @Override
    public String getCbntStatus() throws PlatformInfoException, IOException {
        return FeatureStatus.UNSUPPORTED.getValue();
    }

    @Override
    public String getCbntProfile() throws PlatformInfoException, IOException {
        return "";
    }

    @Override
    public String getSuefiStatus() throws PlatformInfoException, IOException {
        return FeatureStatus.UNSUPPORTED.getValue();
    }

    @Override
    public String getMktmeStatus() throws PlatformInfoException, IOException {
        return FeatureStatus.UNSUPPORTED.getValue();
    }

    @Override
    public String getMktmeEncryptionAlgorithm() throws PlatformInfoException, IOException {
        return "";
    }

    @Override
    public int getMktmeMaxKeysPerCpu() throws PlatformInfoException, IOException {
        return 0;
    }

    @Override
    public String getTbootStatus() throws PlatformInfoException, IOException {
        return ComponentStatus.UNSUPPORTED.getValue();
    }

    @Override
    public Set<String> getInstalledComponents() throws PlatformInfoException, IOException {
        return new HashSet<>();
    }

    public boolean getTxtEnabled() {
        boolean txtEnabled = false;
        boolean txtSupported = false;
        try {
            log.debug("Checking if TXT is supported and enabled...");
            log.debug("Running coreinfo application...");
            Result coreInfoResult = getRunner().executeCommand("coreinfo", "/accepteula");
            if (coreInfoResult.getStdout() != null) {
                txtSupported = coreInfoResult.getStdout().contains("Supports Intel trusted execution") &&
                        coreInfoResult.getStdout().contains("Supports Intel hardware-assisted virtualization");
                log.debug("Is TXT supported : {}", txtSupported);
                if (txtSupported) {
                    log.debug("Running systeminfo command...");
                    Result systemInfoResult = getRunner().executeCommand("systeminfo");
                    txtEnabled = systemInfoResult.getStdout().contains("Virtualization Enabled In Firmware: Yes") ||
                        systemInfoResult.getStdout().contains("A hypervisor has been detected");
                    log.debug("The TXT status is : {}", txtEnabled);
                }
            } else {
                log.error("Error getting txt status");
            }

        } catch (PlatformInfoException | IOException Ex) {
            txtEnabled =  false;
        }
        return txtEnabled;
    }
}
