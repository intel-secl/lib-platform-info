/*
 * Copyright (C) 2019 Intel Corporation
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.intel.mtwilson.core.common;

import com.intel.mtwilson.core.platform.info.CommandLineRunner;
import com.intel.mtwilson.core.common.model.HostInfo;

/**
 *
 * @author dsmagadX
 */
public interface Command {
    public HostInfo getContext();
    public CommandLineRunner getCmd();
    public void setCmd(CommandLineRunner cmd);
    public void execute() throws PlatformInfoException;
}
