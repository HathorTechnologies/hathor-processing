package com.hathor.docs.controllers;

import com.hathor.docs.dto.APIError;
import com.hathor.docs.exceptions.BadRequestException;
import com.hathor.docs.exceptions.ForbiddenException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;

@ResponseBody
@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {
	private static final Log LOG = LogFactory.getLog(ControllerExceptionHandler.class);

	@ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
	public APIError handleForbiddenException(ForbiddenException exception) {
		LOG.debug("ForbiddenException", exception);
        return new APIError(exception.getMessage());
	}

	@ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
	public APIError handleNotFoundException(EntityNotFoundException exception) {
		LOG.debug("Not found exception", exception);
        return new APIError(exception.getMessage());
	}

	@ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
	public APIError handleBadRequestException(BadRequestException exception) {
		LOG.debug("Bad request exception", exception);
        return new APIError(exception.getMessage());
	}
}