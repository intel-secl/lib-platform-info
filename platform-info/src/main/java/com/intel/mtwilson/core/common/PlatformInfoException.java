/*
 * Copyright (C) 2019 Intel Corporation
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.intel.mtwilson.core.common;

/**
 * PlatformInfo Exception Class
 *
 * @author purvades
 * @author dtiwari
 */
public class PlatformInfoException extends Exception {

    ErrorCode errorCode = null;

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    private PlatformInfoException() {}

    public PlatformInfoException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public PlatformInfoException(ErrorCode errorCode, String message, Exception e) {
        super(message, e);
        this.errorCode = errorCode;
    }
}
