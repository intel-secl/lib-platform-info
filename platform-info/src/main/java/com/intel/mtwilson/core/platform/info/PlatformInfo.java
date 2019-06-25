/*
 * Copyright (C) 2019 Intel Corporation
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.intel.mtwilson.core.platform.info;

import com.intel.mtwilson.core.common.PlatformInfoException;
import com.intel.mtwilson.core.common.model.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.intel.mtwilson.core.common.model.HardwareFeature.*;
import java.util.Set;

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
        hostInfo.setTxtEnabled(String.valueOf(getTxtStatus().equals(FeatureStatus.ENABLED.getValue())));
        hostInfo.setTpmEnabled(getTpmEnabled());
        hostInfo.setTbootInstalled(String.valueOf(getTbootStatus().equals(ComponentStatus.INSTALLED.getValue())));
        hostInfo.setNoOfSockets(getNoOfSockets());
        hostInfo.setHostName(getHostName());
        hostInfo.setHardwareFeatures(getHardwareFeatures());
        hostInfo.setInstalledComponents(getInstalledComponents());
        return hostInfo;
    }

    private Map<HardwareFeature, HardwareFeatureDetails> getHardwareFeatures() throws IOException, PlatformInfoException {
        Map<HardwareFeature, HardwareFeatureDetails> hardwareFeatureDetails = new HashMap<>();
        hardwareFeatureDetails.put(TPM, getTpmDetails());
        hardwareFeatureDetails.put(TXT, getTxtDetails());
        if (!getSuefiStatus().equals(FeatureStatus.UNSUPPORTED.getValue())) {
            hardwareFeatureDetails.put(SUEFI, getSuefiDetails());
        }
        if (!getMktmeStatus().equals(FeatureStatus.UNSUPPORTED.getValue())) {
            hardwareFeatureDetails.put(MKTME, getMktmeDetails());
        }
        if (!getCbntStatus().equals(FeatureStatus.UNSUPPORTED.getValue())) {
            hardwareFeatureDetails.put(CBNT, getCbntDetails());
        }
        return hardwareFeatureDetails;
    }

    private HardwareFeatureDetails getSuefiDetails() throws IOException, PlatformInfoException {
        HardwareFeatureDetails suefi = new HardwareFeatureDetails();
        suefi.setEnabled(getSuefiStatus().equals(FeatureStatus.ENABLED.getValue()));
        return suefi;
    }

    private HardwareFeatureDetails getCbntDetails() throws IOException, PlatformInfoException {
        HardwareFeatureDetails cbnt = new HardwareFeatureDetails();
        cbnt.setEnabled(getCbntStatus().equals(FeatureStatus.ENABLED.getValue()));
        Map<String, String> meta = new HashMap<>();
        meta.put("profile", getCbntProfile());
        //TODO: Replace dummy values
        meta.put("force_bit", "true");
        meta.put("msr", "mk ris kfm");
        cbnt.setMeta(meta);
        return cbnt;
    }

    private HardwareFeatureDetails getMktmeDetails() throws IOException, PlatformInfoException {
        HardwareFeatureDetails mktme = new HardwareFeatureDetails();
        mktme.setEnabled(getMktmeStatus().equals(FeatureStatus.ENABLED.getValue()));
        Map<String, String> meta = new HashMap<>();
        meta.put("encryption_algorithm", getMktmeEncryptionAlgorithm());
        meta.put("max_keys_per_cpu", getMktmeMaxKeysPerCpu());
        mktme.setMeta(meta);
        return mktme;
    }

    private HardwareFeatureDetails getTxtDetails() throws IOException, PlatformInfoException {
        HardwareFeatureDetails txt = new HardwareFeatureDetails();
        txt.setEnabled(getTxtStatus().equals(FeatureStatus.ENABLED.getValue()));
        return txt;
    }

    private HardwareFeatureDetails getTpmDetails() throws IOException, PlatformInfoException {
        HardwareFeatureDetails tpm = new HardwareFeatureDetails();
        tpm.setEnabled(Boolean.valueOf(getTpmEnabled()));
        Map<String, String> meta = new HashMap<>();
        meta.put("tpm_version", getTpmVersion());
        tpm.setMeta(meta);
        return tpm;
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

    String txtStatus;
    /**
     * Returns the status of txt(enabled/disabled)
     *
     * @return Status of txt
     *
     * @since 1.0
     */
    public String getTxtStatus() throws IOException, PlatformInfoException {
        if (txtStatus == null) {
            txtStatus = String.valueOf(hostInfoCommand.getTxtStatus());
        }
        return txtStatus;
    }

    String tbootStatus;
    /**
     * Returns the status of tboot(enabled/disabled)
     *
     * @return Status of tboot
     *
     * @since 1.0
     */
    public String getTbootStatus() throws IOException, PlatformInfoException {
        if (tbootStatus == null) {
            tbootStatus = String.valueOf(hostInfoCommand.getTbootStatus());
        }
        return tbootStatus;
    }

    String cbntStatus;
    /**
     * Returns the status of cbnt(enabled/disabled/unsupported)
     *
     * @return Status of cbnt
     *
     * @since 1.0
     */
    public String getCbntStatus() throws IOException, PlatformInfoException {
        if (cbntStatus == null) {
            cbntStatus = String.valueOf(hostInfoCommand.getCbntStatus());
        }
        return cbntStatus;
    }

    String cbntProfile;
    /**
     * Returns the profile of cbnt(P0/P4/P5)
     *
     * @return Profile of cbnt
     *
     * @since 1.0
     */
    public String getCbntProfile() throws IOException, PlatformInfoException {
        if (cbntProfile == null) {
            cbntProfile = hostInfoCommand.getCbntProfile();
        }
        return cbntProfile;
    }

    String suefiEnabled;
    /**
     * Returns the status of suefi(enabled/disabled)
     *
     * @return Status of suefi
     *
     * @since 1.0
     */
    public String getSuefiStatus() throws IOException, PlatformInfoException {
        if (suefiEnabled == null) {
            suefiEnabled = String.valueOf(hostInfoCommand.getSuefiStatus());
        }
        return suefiEnabled;
    }

    String mktmeStatus;
    /**
     * Returns the status of mktme(enabled/disabled/unsupported)
     *
     * @return Status of mktme
     *
     * @since 1.0
     */
    public String getMktmeStatus() throws IOException, PlatformInfoException {
        if (mktmeStatus == null) {
            mktmeStatus = hostInfoCommand.getMktmeStatus();
        }
        return mktmeStatus;
    }

    String mktmeEncryptionAlgorithm;
    /**
     * Returns the encryption algorithm of mktme(AES-XTS-128)
     *
     * @return Encryption algorithm of mktme
     *
     * @since 1.0
     */
    public String getMktmeEncryptionAlgorithm() throws IOException, PlatformInfoException {
        if (mktmeEncryptionAlgorithm == null) {
            mktmeEncryptionAlgorithm = hostInfoCommand.getMktmeEncryptionAlgorithm();
        }
        return mktmeEncryptionAlgorithm;
    }

    String mktmeMaxKeysPerCpu;
    /**
     * Returns the max keys per cpu of mktme
     *
     * @return Max keys per cpu of mktme
     *
     * @since 1.0
     */
    public String getMktmeMaxKeysPerCpu() throws IOException, PlatformInfoException {
        if (mktmeMaxKeysPerCpu == null) {
            mktmeMaxKeysPerCpu = String.valueOf(hostInfoCommand.getMktmeMaxKeysPerCpu());
        }
        return mktmeMaxKeysPerCpu;
    }

    Set<String> installedComponents;
    /**
     * Returns the Installed node component names
     *
     * @return Installed node component names
     *
     * @since 1.0
     */
    public Set<String> getInstalledComponents() throws IOException, PlatformInfoException {
        if (installedComponents == null) {
            installedComponents = hostInfoCommand.getInstalledComponents();
        }
        return installedComponents;
    }
}
