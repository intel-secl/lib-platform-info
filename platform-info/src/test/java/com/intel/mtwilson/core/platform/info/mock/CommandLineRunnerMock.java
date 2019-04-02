/*
 * Copyright (C) 2019 Intel Corporation
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.intel.mtwilson.core.platform.info.mock;

import com.intel.mtwilson.core.common.PlatformInfoException;
import com.intel.mtwilson.core.platform.info.CommandLineRunner;
import com.intel.mtwilson.util.exec.Result;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandLineRunnerMock extends CommandLineRunner {

    final static String LINUX = "linux";
    final static String WINDOWS = "windows";
    private String readResourceFileAsString(String osType, String file) {
        try (InputStream is = getClass().getResourceAsStream(String.format("/" + osType + "/%s", file))) {
            return IOUtils.toString(is, "UTF-8");
        } catch (IOException e) {
            return null;
        }
    }

    private boolean skipDocker = false;
    public CommandLineRunnerMock(boolean skipDocker) {
        this.skipDocker = skipDocker;
    }

    private Result createResult(String stdout) {
        return new Result(0, stdout, "");
    }

    @Override
    public Result executeCommand(String baseCmd, String... args) throws PlatformInfoException, IOException {
        List<String> argsList = new ArrayList<String>(Arrays.asList(args));
        argsList.add(0, baseCmd);
        String flattened = String.join(" ", argsList);
        switch (flattened) {
            case "lsb_release -a":
                return createResult(readResourceFileAsString(LINUX,"lsb_release"));
            case "dmidecode -s bios-vendor":
                return createResult(readResourceFileAsString(LINUX, "dmidecode.bios-vendor"));
            case "dmidecode -s bios-version":
                return createResult(readResourceFileAsString(LINUX, "dmidecode.bios-version"));
            case "docker -v":
                if (!skipDocker)
                    return createResult(readResourceFileAsString(LINUX, "docker.version"));
                else
                    return new Result(1, "", "");
            case "virsh version":
                return createResult(readResourceFileAsString(LINUX, "virsh.version"));
            case "dmidecode --type processor":
                return createResult(readResourceFileAsString(LINUX,"dmidecode.processor"));
            case "dmidecode -s system-uuid":
                return createResult(readResourceFileAsString(LINUX, "dmidecode.system-uuid"));
            case "txt-stat":
                return createResult(readResourceFileAsString(LINUX, "txt-stat"));
            case "lscpu":
                return createResult(readResourceFileAsString(LINUX, "lscpu"));
            case "hostname":
                return createResult(readResourceFileAsString(LINUX, "host-name"));
            case "cat /proc/cpuinfo":
                return createResult(readResourceFileAsString(LINUX, "proc-cpuinfo-rhel"));
            case "wmic os get caption":
                return createResult(readResourceFileAsString(WINDOWS, "os-name"));
            case "wmic os get version":
                return createResult(readResourceFileAsString(WINDOWS, "os-version"));
            case "wmic bios get manufacturer":
                return createResult(readResourceFileAsString(WINDOWS, "bios-name"));
            case "wmic bios get smbiosbiosversion":
                return createResult(readResourceFileAsString(WINDOWS, "bios-version"));
            case "wmic datafile where name=\"c:\\\\windows\\\\system32\\\\vmms.exe\" get version":
                return createResult(readResourceFileAsString(WINDOWS, "vmm-version"));
            case "wmic path WIN32_ServerFeature get ID":
                return createResult("20");
            case "wmic cpu get ProcessorId":
                return createResult(readResourceFileAsString(WINDOWS, "processor-info"));
            case "wmic path Win32_ComputerSystemProduct get uuid":
                return createResult(readResourceFileAsString(WINDOWS, "hardware-uuid"));
            case "wmic /namespace:\\\\root\\CIMV2\\Security\\MicrosoftTpm path Win32_Tpm get /value":
                return createResult(readResourceFileAsString(WINDOWS, "tpm-version"));
            case "wmic computersystem get Name":
                return createResult(readResourceFileAsString(WINDOWS, "host-name"));
            case "wmic cpu get SocketDesignation":
                return createResult(readResourceFileAsString(WINDOWS, "no-of-sockets"));
            default:
                return new Result(1, "", "Unmockable command");
        }
    }
}
