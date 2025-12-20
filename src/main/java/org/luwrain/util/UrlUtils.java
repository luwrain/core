// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.util;

import java.io.*;
import java.net.*;

import org.luwrain.core.*;

public final class UrlUtils
{
    static public File urlToFile(String urlStr)
    {
NullCheck.notEmpty(urlStr, "urlStr");
final URL url;
try {
    url = new URL(urlStr);
}
catch(MalformedURLException e)
{
    return null;
}
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

    static public String fileToUrl(File file)
    {
	NullCheck.notNull(file, "file");
	try {
	    return file.toURI().toURL().toString();
	}
	catch(IOException e)
	{
	    return null;
	}
    }
}
