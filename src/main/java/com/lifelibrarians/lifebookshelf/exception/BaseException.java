package com.lifelibrarians.lifebookshelf.exception;

import com.lifelibrarians.lifebookshelf.exception.status.ErrorReason;

public interface BaseException {

	ErrorReason getErrorReason();
}

