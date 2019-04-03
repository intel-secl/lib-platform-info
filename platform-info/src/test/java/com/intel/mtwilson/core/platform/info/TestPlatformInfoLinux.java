/*
 * Copyright (C) 2019 Intel Corporation
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.intel.mtwilson.core.platform.info;

import com.intel.mtwilson.core.common.PlatformInfoException;
import com.intel.mtwilson.core.common.model.ComponentStatus;
import com.intel.mtwilson.core.common.model.FeatureStatus;
import com.intel.mtwilson.core.common.model.HostInfo;
import com.intel.mtwilson.core.platform.info.mock.HostInfoCommandMockLinux;
import org.junit.*;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.is;

public class TestPlatformInfoLinux {

    private PlatformInfo platformInfo;
    private HostInfo expectedHostInfoLinux;
    private HostInfoCommandMockLinux mockCmd = new HostInfoCommandMockLinux();

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        platformInfo = new PlatformInfo(mockCmd);
        expectedHostInfoLinux = new HostInfo();
        expectedHostInfoLinux.setBiosName("Intel Corp.");
        expectedHostInfoLinux.setBiosVersion("SE5C600.86B.02.03.0003.041920141333");
        expectedHostInfoLinux.setProcessorInfo("E4 06 03 00 FF FB EB BF");
        expectedHostInfoLinux.setOsName("Ubuntu");
        expectedHostInfoLinux.setOsVersion("16.04");
        expectedHostInfoLinux.setNoOfSockets("2");
        expectedHostInfoLinux.setTxtEnabled("true");
        expectedHostInfoLinux.setVmmVersion("2.5.0");
        expectedHostInfoLinux.setHardwareUuid("76262A9F-A72B-E411-BAB9-001E67C2ECAE");
        expectedHostInfoLinux.setVmmName("QEMU");
        expectedHostInfoLinux.setHostName("RHEL7.3");
        expectedHostInfoLinux.setTbootInstalled("true");
        expectedHostInfoLinux.setProcessorFlags("fpu vme de pse tsc msr pae mce cx8 apic sep mtrr pge mca cmov pat pse36 clflush dts mmx fxsr sse sse2 ss syscall nx rdtscp lm constant_tsc arch_perfmon pebs bts nopl xtopology tsc_reliable nonstop_tsc aperfmperf pni ssse3 cx16 sse4_1 sse4_2 x2apic popcnt tsc_deadline_timer hypervisor lahf_lm ida arat epb dtherm tsc_adjust");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getBiosName() throws IOException, PlatformInfoException {
        String actual = platformInfo.getBiosName();
        String expected = expectedHostInfoLinux.getBiosName();
        assertThat(actual, is(expected));
    }

    @Test
    public void getBiosVersion() throws IOException, PlatformInfoException {
        assertThat(platformInfo.getBiosVersion(), is(expectedHostInfoLinux.getBiosVersion()));
    }

    @Test
    public void getHardwareUuid() throws IOException, PlatformInfoException {
        assertThat(platformInfo.getHardwareUuid(), is(expectedHostInfoLinux.getHardwareUuid()));
    }

    @Test
    public void getOsName() throws IOException, PlatformInfoException {
        assertThat(platformInfo.getOsName(), is(expectedHostInfoLinux.getOsName()));
    }

    @Test
    public void getOsVersion() throws IOException, PlatformInfoException {
        assertThat(platformInfo.getOsVersion(), is(expectedHostInfoLinux.getOsVersion()));
    }

    @Test
    public void getProcessorFlags() throws IOException, PlatformInfoException {
        assertThat(platformInfo.getProcessorFlags(), is(expectedHostInfoLinux.getProcessorFlags()));
    }

    @Test
    public void getProcessorInfo() throws IOException, PlatformInfoException {
        assertThat(platformInfo.getProcessorInfo(), is(expectedHostInfoLinux.getProcessorInfo()));
    }

    @Test
    public void getVmmName() throws IOException, PlatformInfoException {
        mockCmd.skipDocker(true);
        assertThat(platformInfo.getVmmName(), is(expectedHostInfoLinux.getVmmName()));
    }

    @Test
    public void getVmmVersion() throws IOException, PlatformInfoException {
        mockCmd.skipDocker(true);
        assertThat(platformInfo.getVmmVersion(), is(expectedHostInfoLinux.getVmmVersion()));
    }

    @Test
    public void getVmmNameDocker() throws IOException, PlatformInfoException {
        assertThat(platformInfo.getVmmName(), is("Docker"));
    }

    @Test
    public void getVmmVersionDocker() throws IOException, PlatformInfoException {
        assertThat(platformInfo.getVmmVersion(), is("17.04.0-ce"));
    }

    @Test
    public void getHostName() throws IOException, PlatformInfoException {
        assertThat(platformInfo.getHostName(), is(expectedHostInfoLinux.getHostName()));
    }

    @Test
    public void getNoOfSockets() throws IOException, PlatformInfoException {
        assertThat(platformInfo.getNoOfSockets(), is(expectedHostInfoLinux.getNoOfSockets()));
    }

    @Test
    public void getTxtStatus() throws IOException, PlatformInfoException {
        assertThat(String.valueOf(platformInfo.getTxtStatus().equals(FeatureStatus.ENABLED.getValue())), is(expectedHostInfoLinux.getTxtEnabled()));
    }

    @Test
    public void getTbootStatus() throws IOException, PlatformInfoException {
        assertThat(String.valueOf(platformInfo.getTbootStatus().equals(ComponentStatus.INSTALLED.getValue())), is(expectedHostInfoLinux.getTbootInstalled()));
    }
}