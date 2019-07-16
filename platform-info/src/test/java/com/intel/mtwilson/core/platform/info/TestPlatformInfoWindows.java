/*
 * Copyright (C) 2019 Intel Corporation
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.intel.mtwilson.core.platform.info;

import com.intel.mtwilson.core.common.PlatformInfoException;
import com.intel.mtwilson.core.common.model.FeatureStatus;
import com.intel.mtwilson.core.platform.info.mock.HostInfoCommandMockWindows;
import org.junit.*;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.is;

public class TestPlatformInfoWindows {

    private PlatformInfo platformInfo;
    private HostInfoCommandMockWindows mockCmd = new HostInfoCommandMockWindows();

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        platformInfo = new PlatformInfo(mockCmd);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getBiosName() throws IOException, PlatformInfoException {
        assertThat(platformInfo.getBiosName(), is("HP"));
    }

    @Test
    public void getBiosVersion() throws IOException, PlatformInfoException {
        assertThat(platformInfo.getBiosVersion(), is("SE5C620.86B.00.01.0004.071220170215"));
    }

    @Test
    public void getHardwareUuid() throws IOException, PlatformInfoException {
        assertThat(platformInfo.getHardwareUuid(), is("C8C8411F-F0CB-11E5-8343-9025330C6062"));
    }

    @Test
    public void getOsName() throws IOException, PlatformInfoException {
        assertThat(platformInfo.getOsName(), is("Microsoft Windows 10 Enterprise"));
    }

    @Test
    public void getOsVersion() throws IOException, PlatformInfoException {
        assertThat(platformInfo.getOsVersion(), is("10.0.10586"));
    }


    @Test
    public void getProcessorInfo() throws IOException, PlatformInfoException {
        assertThat(platformInfo.getProcessorInfo(), is("BFEBFBFF000406E3"));
    }

    @Test
    public void getVmmName() throws IOException, PlatformInfoException {
        assertThat(platformInfo.getVmmName(), is("Microsoft Windows Hyper-V"));
    }

    @Test
    public void getVmmVersion() throws IOException, PlatformInfoException {
        assertThat(platformInfo.getVmmVersion(), is("10.0.10586"));
    }

    @Test
    public void getHostName() throws IOException, PlatformInfoException {
        assertThat(platformInfo.getHostName(), is("WIN-GLU9NEPGT1L"));
    }

    @Test
    public void getNoOfSockets() throws IOException, PlatformInfoException {
        assertThat(platformInfo.getNoOfSockets(), is("2"));
    }

    @Test
    public void getTxtEnabled() throws IOException, PlatformInfoException {
        assertThat(String.valueOf(platformInfo.getTxtStatus().equals(FeatureStatus.ENABLED.getValue())), is("false"));
    }

    @Test
    public void getTbootEnabled() throws IOException, PlatformInfoException {
        assertThat(String.valueOf(platformInfo.getTxtStatus().equals(FeatureStatus.ENABLED.getValue())), is("false"));
    }
}
