/*
 * Copyright (C) 2019 Intel Corporation
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.intel.mtwilson.core.platform.info.mock;

import com.intel.mtwilson.core.common.PlatformInfoException;
import com.intel.mtwilson.core.platform.info.CommandLineRunner;
import com.intel.mtwilson.core.platform.info.HostInfoCommand;
import com.intel.mtwilson.core.platform.info.HostInfoCommandLinux;

import java.io.IOException;

public class HostInfoCommandMockLinux extends HostInfoCommandLinux {


    private boolean skipDocker = false;

    public void skipDocker(boolean val) {
        skipDocker = val;
    }

    @Override
    public CommandLineRunner getRunner() {
        return new CommandLineRunnerMock(skipDocker);
    }
}
