// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import java.util.*;
import java.io.*;

import org.luwrain.core.*;

public interface PropertiesProvider extends ExtensionObject
{
    public enum Flags {
	PUBLIC,
	READ_ONLY,
	FILE,
    };

    public interface Listener
    {
	void onNewPropertyValue(String propName, String propValue);
    }

    String[] getPropertiesRegex();
    Set<Flags> getPropertyFlags(String propName);
    String getProperty(String propName);
    boolean setProperty(String propName, String value);
    void setListener(Listener listener);
}
