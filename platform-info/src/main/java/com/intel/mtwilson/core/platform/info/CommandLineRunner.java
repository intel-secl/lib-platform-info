/*
 * Copyright (C) 2019 Intel Corporation
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.intel.mtwilson.core.platform.info;

import com.intel.mtwilson.core.common.PlatformInfoException;
import com.intel.mtwilson.util.exec.ExecUtil;
import com.intel.mtwilson.util.exec.Result;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.exec.CommandLine;

/**
 * Form the command with the supplied arguments and execute the command
 *
 * @author purvades
 * @author dtiwari
 */
public class CommandLineRunner {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CommandLineRunner.class);
    private Function<String[], String[]> hook;

    public CommandLineRunner() {

    }

    public CommandLineRunner(Function<String[], String[]>  hook) {
        this.hook = hook;
    }

    public void setHook(Function<String[], String[]> hook) {
        this.hook = hook;
    }

    public Result executeCommand(String baseCmd, String... args) throws PlatformInfoException, IOException {
        List<String> flatArgs = new ArrayList(Arrays.asList(args));
        flatArgs.add(0, baseCmd);
        String[] commandArgs = flatArgs.toArray(new String[0]);
        if(hook != null) {
            commandArgs = hook.apply(commandArgs);
        }

        CommandLine command = new CommandLine(commandArgs[0]);
        for(int i = 1; i < commandArgs.length; i++) {
            command.addArgument(commandArgs[i]);
        }
        
        return ExecUtil.execute(command);
    }
    
    public Result executeCommand(String baseCmd, boolean handleQuotes, String... args) throws PlatformInfoException, IOException {
        List<String> flatArgs = new ArrayList(Arrays.asList(args));
        flatArgs.add(0, baseCmd);
        String[] commandArgs = flatArgs.toArray(new String[0]);
        if(hook != null) {
            commandArgs = hook.apply(commandArgs);
        }

        CommandLine command = new CommandLine(commandArgs[0]);
        for(int i = 1; i < commandArgs.length; i++) {
            command.addArgument(commandArgs[i], handleQuotes);
        }
        
        return ExecUtil.execute(command);
    }
}