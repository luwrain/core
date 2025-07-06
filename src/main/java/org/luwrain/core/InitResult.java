/*
   Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

   This file is part of LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

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
