// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

public final class ExtensionException extends Exception
{
    public ExtensionException(Throwable ex)
    {
	super(ex);
    }

    public ExtensionException(String message)
    {
	super(message);
    }
}
