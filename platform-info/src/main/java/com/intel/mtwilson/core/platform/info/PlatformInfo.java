/*
 * Copyright (C) 2019 Intel Corporation
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.intel.mtwilson.core.platform.info;

import com.intel.mtwilson.core.common.PlatformInfoException;
import com.intel.mtwilson.core.common.model.HostInfo;

import java.io.IOException;

/**
 * A class for Platform/Architecture related information for Host
 *
 * @author purvades
 * @author dtiwari
 *
 * @since 1.0
 */
public class PlatformInfo {

    /**
     * Sole constructor.
     * Detects and executes appropriate OS commands to get Platform information.
     *
     * @throws PlatformInfoException
     *
     * @since 1.0
     */

    private HostInfoCommand hostInfoCommand;
    public PlatformInfo() {
        // get SystemOs

        String os = System.getProperty("os.name");
        if (os.toLowerCase().contains("windows")) {
            hostInfoCommand = new HostInfoCommandWindows();
        } else {
            if ("docker".equalsIgnoreCase(System.getenv("container"))) {
                hostInfoCommand = new HostInfoCommandDocker();
            } else {
                hostInfoCommand = new HostInfoCommandLinux();
            }
        }
    }

    public PlatformInfo(HostInfoCommand hostInfoCommand) {
        this.hostInfoCommand = hostInfoCommand;
    }

    public HostInfo getHostInfo() throws IOException, PlatformInfoException {
        HostInfo hostInfo = new HostInfo();
        hostInfo.setBiosName(getBiosName());
        hostInfo.setBiosVersion(getBiosVersion());
        hostInfo.setOsName(getOsName());
        hostInfo.setOsVersion(getOsVersion());
        hostInfo.setVmmName(getVmmName());
        hostInfo.setVmmVersion(getVmmVersion());
        hostInfo.setProcessorFlags(getProcessorFlags());
        hostInfo.setProcessorInfo(getProcessorInfo());
        hostInfo.setHardwareUuid(getHardwareUuid());
        hostInfo.setTpmVersion(getTpmVersion());
        hostInfo.setTxtEnabled((getTxtEnabled()));
        hostInfo.setTpmEnabled(getTpmEnabled());
        hostInfo.setNoOfSockets(getNoOfSockets());
        hostInfo.setHostName(getHostName());
        return hostInfo;
    }


    String biosName;
    /**
     * Returns the BIOS OEM name
     *
     * @return BIOS OEM
     *
     * @since 1.0
     */
    public String getBiosName() throws IOException, PlatformInfoException {
        // cache results
        if (biosName == null) {
            biosName = hostInfoCommand.getBiosName();
        }
        return biosName;
    }

    String biosVersion;
    /**
     * Returns the BIOS Version
     *
     * @return BIOS Version
     *
     * @since 1.0
     */
    public String getBiosVersion() throws IOException, PlatformInfoException {
        if (biosVersion == null) {
            biosVersion = hostInfoCommand.getBiosVersion();
        }
        return biosVersion;
    }

    String hardwareUuid;
    /**
     * Returns the Host's UUID
     *
     * @return Host UUID
     *
     * @since 1.0
     */
    public String getHardwareUuid() throws IOException, PlatformInfoException {
        if (hardwareUuid == null) {
            hardwareUuid = hostInfoCommand.getHardwareUUID();
        }
        return hardwareUuid;
    }

    String osName;
    /**
     * Returns the Operating System(OS) Name
     *
     * @return OS Name
     *
     * @since 1.0
     */
    public String getOsName() throws IOException, PlatformInfoException {
        if (osName == null) {
            osName = hostInfoCommand.getOsName();
        }
        return osName;
    }

    String osVersion;
    /**
     * Returns the Operating System(OS) Version
     *
     * @return OS Name
     *
     * @since 1.0
     */
    public String getOsVersion() throws IOException, PlatformInfoException {
        if (osVersion == null) {
            osVersion = hostInfoCommand.getOsVersion();
        }
        return osVersion;
    }

    String processorFlags;
    /**
     * Returns the Processor(CPU) supported Flags/Features
     *
     * @return VMM Version
     *
     * @since 1.0
     */
    public String getProcessorFlags() throws IOException, PlatformInfoException {
        if (processorFlags == null) {
            processorFlags = String.join(" ", hostInfoCommand.getProcessorFlags());
        }
        return processorFlags;
    }

    String processorInfo;
    /**
     * Returns the Processor(CPU) Information
     *
     * @return Processor Information
     *
     * @since 1.0
     */
    public String getProcessorInfo() throws IOException, PlatformInfoException {
        if (processorInfo == null) {
            processorInfo = hostInfoCommand.getProcessorInfo();
        }
        return processorInfo;
    }

    String vmmName;
    /**
     * Returns the VMM(Hypervisor) Name
     *
     * @return VMM Name
     *
     * @since 1.0
     */
    public String getVmmName() throws IOException, PlatformInfoException {
        if(vmmName == null) {
            vmmName = hostInfoCommand.getVmmName();
        }
        return vmmName;
    }

    String vmmVersion;
    /**
     * Returns the VMM(Hypervisor) Version
     *
     * @return VMM Version
     *
     * @since 1.0
     */
    public String getVmmVersion() throws IOException, PlatformInfoException {
        if (vmmVersion == null) {
            vmmVersion = hostInfoCommand.getVmmVersion();
        }
        return vmmVersion;
    }

    String tpmVersion;
    /**
     * Returns the TPM Chip Version
     *
     * @return TPM Version
     *
     * @since 1.0
     */
    public String getTpmVersion() throws IOException, PlatformInfoException {
        if (tpmVersion == null) {
            tpmVersion = hostInfoCommand.getTpmVersion();
        }
        return tpmVersion;
    }

    String hostName;
    /**
     * Returns the Host Name
     *
     * @return Host Name
     *
     * @since 1.0
     */
    public String getHostName() throws IOException, PlatformInfoException {
        if (hostName == null) {
            hostName = hostInfoCommand.getHostName();
        }
        return hostName;
    }

    String numberOfSockets;
    /**
     * Returns the number of sockets
     *
     * @return Number of sockets
     *
     * @since 1.0
     */
    public String getNoOfSockets() throws IOException, PlatformInfoException {
        if(numberOfSockets == null) {
            numberOfSockets = String.valueOf(hostInfoCommand.getNumberOfSockets());
        }
        return numberOfSockets;
    }

    String tpmEnabled;
    /**
     * Returns status of tpm(enabled/disabled)
     *
     * @return Status of tpm
     *
     * @since 1.0
     */
    public String getTpmEnabled() throws IOException, PlatformInfoException {
        if (tpmEnabled == null) {
            tpmEnabled = String.valueOf(hostInfoCommand.getTpmEnabled());
        }
        return tpmEnabled;
    }

    String txtEnabled;
    /**
     * Returns the status of txt(enabled/disabled)
     *
     * @return Status of txt
     *
     * @since 1.0
     */
    public String getTxtEnabled() throws IOException, PlatformInfoException {
        if (txtEnabled == null) {
            txtEnabled = String.valueOf(hostInfoCommand.getTxtEnabled());
        }
        return txtEnabled;
    }
}
