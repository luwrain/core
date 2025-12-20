// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.util;

import java.io.*;
import java.net.*;

import org.luwrain.core.*;

public final class Urls
{
    static public File toFile(URL url)
    {
NullCheck.notNull(url, "url");
if (url.getProtocol() == null || !url.getProtocol().toLowerCase().equals("file"))
    return null;
try {
return new File(url.toURI());
}
catch(URISyntaxException e)
{
    return new File(url.getPath());
}
    }

    static public URL toUrl(File file)
    {
	NullCheck.notNull(file, "file");
	try {
	    return file.toURI().toURL();
	}
	catch(IOException e)
	{
	    return null;
	}
    }
}
