package com.lifelibrarians.lifebookshelf.exception.status;

import com.lifelibrarians.lifebookshelf.exception.ControllerException;
import com.lifelibrarians.lifebookshelf.exception.DomainException;
import com.lifelibrarians.lifebookshelf.exception.ServiceException;

public interface ExceptionStatus {

	ErrorReason getErrorReason();

	ControllerException toControllerException();

	ServiceException toServiceException();

	DomainException toDomainException();
}
