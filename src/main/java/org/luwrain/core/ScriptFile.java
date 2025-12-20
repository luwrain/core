// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import java.io.*;

public final class ScriptFile extends ScriptSource
{
    private final String component, file, dataDir;

    public ScriptFile(String component, String file, String dataDir)
    {
	NullCheck.notEmpty(component, "component");
	NullCheck.notEmpty(file, "file");
	NullCheck.notEmpty(dataDir, "dataDir");
	this.component = component;
	this.file = file;
	this.dataDir = dataDir;
    }

    public String getComponent()
    {
	return this.component;
    }

    public String getFile()
    {
	return this.file;
    }

    public String getDataDir()
    {
	return this.dataDir;
    }

    public File asFile()
    {
	return new File(file);
    }

    public File getDataDirAsFile()
    {
	return new File(dataDir);
    }

    @Override public String toString()
    {
	return file;
    }
}
