/*
 * Copyright (C) 2019 Intel Corporation
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.intel.mtwilson.core.platforminfo.integration;

import com.intel.mtwilson.core.platform.info.PlatformInfo;
import com.intel.mtwilson.core.common.PlatformInfoException;
import com.intel.kunit.annotations.*;

import java.io.IOException;
import java.util.Set;

/**
 *
 * @author purvades
 */
public class PlatforminfoIntegrationTest {

    private final PlatformInfo platformInfo;

    /**
     *
     * @throws PlatformInfoException
     */
    public PlatforminfoIntegrationTest() throws PlatformInfoException {
        platformInfo = new PlatformInfo();
    }
    
    /**
     *
     * @return
     */
    @Integration
    public String getBiosName() throws IOException, PlatformInfoException {
        return platformInfo.getBiosName().trim();
    }

    /**
     *
     * @return
     */
    @Integration
    public String getBiosVersion() throws IOException, PlatformInfoException {
        return platformInfo.getBiosVersion().trim();
    }
    
    /**
     *
     * @return
     */
    @Integration
    public String getHardwareUuid() throws IOException, PlatformInfoException {
        return platformInfo.getHardwareUuid().trim();
    }
    
    /**
     *
     * @return
     */
    @Integration
    public String getOsName() throws IOException, PlatformInfoException {
        return platformInfo.getOsName().trim();
    }
    
    /**
     *
     * @return
     */
    @Integration
    public String getOsVersion() throws IOException, PlatformInfoException {
        return platformInfo.getOsVersion().trim();
    }
    
    /**
     *
     * @return
     */
    @Integration
    public String getProcessorFlags() throws IOException, PlatformInfoException {
        return platformInfo.getProcessorFlags().trim();
    }
    
    /**
     *
     * @return
     */
    @Integration
    public String getProcessorInfo() throws IOException, PlatformInfoException {
        return platformInfo.getProcessorInfo().trim();
    }
    
    /**
     *
     * @return
     */
    @Integration
    public String getVmmName() throws IOException, PlatformInfoException {
        return platformInfo.getVmmName().trim();
    }
    
    /**
     *
     * @return
     */
    @Integration
    public String getVmmVersion() throws IOException, PlatformInfoException {
        return platformInfo.getVmmVersion().trim();
    }

    /**
     *
     * @return
     */
    @Integration
    public String getTpmVersion() throws IOException, PlatformInfoException {
        return platformInfo.getTpmVersion().trim();
    }
    
    /**
     *
     * @return
     */
    @Integration
    public String getHostName() throws IOException, PlatformInfoException {
        return platformInfo.getHostName().trim();
    }
    
    /**
     *
     * @return
     */
    @Integration
    public String getNoOfSockets() throws IOException, PlatformInfoException {
        return platformInfo.getNoOfSockets();
    }
    
    /**
     *
     * @return
     */
    @Integration
    public String getTpmEnabled() throws IOException, PlatformInfoException {
        return platformInfo.getTpmEnabled();
    }
    
    /**
     *
     * @return
     */
    @Integration
    public String getTxtStatus() throws IOException, PlatformInfoException {
        return platformInfo.getTxtStatus();
    }

    /**
     *
     * @return
     */
    @Integration
    public String getTbootStatus() throws IOException, PlatformInfoException {
        return platformInfo.getTbootStatus();
    }

    /**
     *
     * @return
     */
    @Integration
    public String getCbntStatus() throws IOException, PlatformInfoException {
        return platformInfo.getCbntStatus();
    }

    /**
     *
     * @return
     */
    @Integration
    public String getCbntProfile() throws IOException, PlatformInfoException {
        return platformInfo.getCbntProfile();
    }

    /**
     *
     * @return
     */
    @Integration
    public String getSuefiStatus() throws IOException, PlatformInfoException {
        return platformInfo.getSuefiStatus();
    }

    /**
     *
     * @return
     */
    @Integration
    public Set<String> getInstalledComponents() throws IOException, PlatformInfoException {
        return platformInfo.getInstalledComponents();
    }
}
