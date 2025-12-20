// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import java.util.*;

import java.net.*;


public interface UriProc
{
    String getUriType();
    Instance process(URI uri, Luwrain luwrain);

    public interface Instance
    {
	String getTitle();
	void announce();
	void activate();
    }
}
