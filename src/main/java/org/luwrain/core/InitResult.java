// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import lombok.*;

import static java.util.Objects.*;

@Data
public final class InitResult 
{
    public enum Type {
	OK,
	FAILURE,
	EXCEPTION,
	NO_STRINGS_OBJ,
	NETWORK_SERVICE_INACCESSIBLE,
    };

    private final Type type;
    private final String message;
    private Throwable exception;

    public InitResult()
    {
	this.type = Type.OK;
	this.message = null;
	this.exception = null;
    }

    public InitResult(Type type)
    {
	this.type = requireNonNull(type, "type can't be null");
	this.message = null;
	this.exception = null;
    }

    public InitResult(Type type, String message)
    {
	this.type = requireNonNull(type, "type can't be null");
	this.message = requireNonNull(message, "message can't be null");
    }

    public InitResult(Throwable e)
    {
	this.type = Type.EXCEPTION;
	this.message = null;
	this.exception = requireNonNull(e, "e can't be null");
    }

    public boolean isOk()
    {
	return type == Type.OK;
    }
}
