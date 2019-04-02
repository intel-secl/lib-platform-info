/*
 * Copyright (C) 2019 Intel Corporation
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.intel.mtwilson.core.platform.info;

import java.util.Arrays;
import java.util.stream.Stream;

public class HostInfoCommandDocker extends HostInfoCommandLinux {

    public HostInfoCommandDocker() {
        super();
        getRunner().setHook(HostInfoCommandDocker::commandLineHook);
    }

    /*
        prepend chroot /mount/path before a command, for example:
        running "docker -v" becomes "chroot /mount/path docker -v"
     */
    private static String[] commandLineHook(String[] args) {
        // get the host root file mount path
        String hostMountPath = System.getenv("TRUSTAGENT_DOCKER_HOST_MOUNT");
        String[] chrootHook = new String[] { "chroot", hostMountPath };
        return Stream.concat(Arrays.stream(chrootHook), Arrays.stream(args)).toArray(String[]::new);
    }
}
