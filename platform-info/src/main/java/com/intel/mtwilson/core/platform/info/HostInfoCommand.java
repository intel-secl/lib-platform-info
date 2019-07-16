/*
 * Copyright (C) 2019 Intel Corporation
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.intel.mtwilson.core.platform.info;

import com.intel.mtwilson.core.common.PlatformInfoException;

import java.io.IOException;
import java.util.Set;

/**
 * Logic to retrieve Platform/Architecture related information for a Host
 *
 * @author dczech
 * @author purvades
 * @author dtiwari
 */
public interface HostInfoCommand {

    String getOsName() throws PlatformInfoException, IOException;

    String getOsVersion() throws PlatformInfoException, IOException;

     String getBiosName() throws PlatformInfoException, IOException;

     String getBiosVersion() throws PlatformInfoException, IOException;
    /*
     * Sample response of "virsh version" command: 
     * root@mwdevubuk02h:~# virsh version 
     * Compiled against library: libvir 0.9.2 
     * Using library: libvir 0.9.2 
     * Using API: QEMU 0.9.2 
     * Running hypervisor: QEMU 0.14.1
     */

    String getVmmName() throws PlatformInfoException, IOException;

    String getVmmVersion() throws PlatformInfoException, IOException;

    String getProcessorInfo() throws PlatformInfoException, IOException;

    String[] getProcessorFlags() throws PlatformInfoException, IOException;

    String getHardwareUUID() throws IOException, PlatformInfoException;

    String getTpmVersion() throws PlatformInfoException, IOException;

    String getHostName() throws PlatformInfoException, IOException;

    int getNumberOfSockets() throws PlatformInfoException, IOException;
  
    boolean getTpmEnabled() throws PlatformInfoException, IOException;

    String getTxtStatus() throws PlatformInfoException, IOException;

    String getCbntStatus() throws PlatformInfoException, IOException;

    String getCbntProfile() throws PlatformInfoException, IOException;

    String getSuefiStatus() throws PlatformInfoException, IOException;

    String getMktmeStatus() throws PlatformInfoException, IOException;

    String getMktmeEncryptionAlgorithm() throws PlatformInfoException, IOException;

    int getMktmeMaxKeysPerCpu() throws PlatformInfoException, IOException;

    String getTbootStatus() throws PlatformInfoException, IOException;

    Set<String> getInstalledComponents() throws PlatformInfoException, IOException;

    boolean isDockerEnv() throws PlatformInfoException, IOException;
}
