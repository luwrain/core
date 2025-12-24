// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.app.crash;

import org.luwrain.core.*;

public class InitResultException extends CustomMessageException
{
    	private final InitResult initRes;

    public InitResultException(InitResult initRes)
    {
	super(new String[]{initRes.getType().toString()});
	    this.initRes = initRes;
    }

    public InitResult getInitResult()
    {
	return this.initRes;
    }
}
