/*
 * Copyright (C) 2019 Intel Corporation
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.intel.mtwilson.core.platform.info.mock;

import com.intel.mtwilson.core.platform.info.CommandLineRunner;
import com.intel.mtwilson.core.platform.info.HostInfoCommandWindows;

public class HostInfoCommandMockWindows extends HostInfoCommandWindows {
    @Override
    public CommandLineRunner getRunner() {
        return new CommandLineRunnerMock(false);
    }
}
