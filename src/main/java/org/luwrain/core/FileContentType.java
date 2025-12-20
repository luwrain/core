// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import java.util.*;
import java.io.*;
import java.net.*;
import java.util.regex.*;

//Must be thread-safe
final class FileContentType
{
    private final Map<String, String> contentTypes = new HashMap<>();

    FileContentType()
    {
	//Raw 
	contentTypes.put(".*\\.dat$", ContentTypes.DATA_BINARY_DEFAULT);
	contentTypes.put(".*\\.raw$", ContentTypes.DATA_BINARY_DEFAULT);

	//Text
	contentTypes.put(".*\\.txt$", ContentTypes.TEXT_PLAIN_DEFAULT);
	contentTypes.put(".*\\.htm$", ContentTypes.TEXT_HTML_DEFAULT);
	contentTypes.put(".*\\.html$", ContentTypes.TEXT_HTML_DEFAULT);
	contentTypes.put(".*\\.doc$", ContentTypes.APP_DOC_DEFAULT);
	contentTypes.put(".*\\.docx$", ContentTypes.APP_DOCX_DEFAULT);
		contentTypes.put(".*\\.xlsx$", ContentTypes.APP_XLSX_DEFAULT);

	//Audio
	contentTypes.put(".*\\.wav$", ContentTypes.SOUND_WAVE_DEFAULT);
	contentTypes.put(".*\\.wave$", ContentTypes.SOUND_WAVE_DEFAULT);
	contentTypes.put(".*\\.mp3$", ContentTypes.SOUND_MP3_DEFAULT);

	contentTypes.put(".*\\.xhtml$", "application/xhtml");
	contentTypes.put(".*\\.xhtm$", "application/xhtml");
	contentTypes.put(".*\\.pdf$", ContentTypes.APP_PDF_DEFAULT);
	contentTypes.put(".*\\.ps$", "application/postscript");
	contentTypes.put(".*\\.zip$", "application/zip");
	contentTypes.put(".*\\.fb2$", "application/fb2");
    }

    String suggestContentType(File file, ContentTypes.ExpectedType expectedType)
    {
	NullCheck.notNull(file, "file");
	NullCheck.notNull(expectedType, "expectedType");
	if (file.getAbsolutePath().isEmpty())
	    return "";
	final String res = find(file.getName());
	if (!res.isEmpty())
	    return res;
	switch(expectedType)
	{
	case TEXT:
	    return ContentTypes.TEXT_PLAIN_DEFAULT;
	case AUDIO:
	    return ContentTypes.SOUND_MP3_DEFAULT;
	default:
	    return ContentTypes.DATA_BINARY_DEFAULT;
	}
    }

    String suggestContentType(URL url, ContentTypes.ExpectedType expectedType)
    {
	NullCheck.notNull(url, "url");
	NullCheck.notNull(expectedType, "expectedType");
	final String res = find(url.getFile());
	if (!res.isEmpty())
	    return res;
	switch(expectedType)
	{
	case TEXT:
	    return ContentTypes.TEXT_PLAIN_DEFAULT;
	case AUDIO:
	    return ContentTypes.SOUND_MP3_DEFAULT;
	default:
	    return ContentTypes.DATA_BINARY_DEFAULT;
	}
    }

    private String find(String fileName)
    {
	if (fileName == null || fileName.isEmpty())
	    return "";
	for(Map.Entry<String, String> e: contentTypes.entrySet())
	    if (match(e.getKey(), fileName))
		return e.getValue();
	return "";
    }

    static private boolean match(String pattern, String line)
    {
	NullCheck.notEmpty(pattern, "pattern");
	NullCheck.notNull(line, "line");
	final Pattern pat = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
	final Matcher matcher = pat.matcher(line);
	return matcher.find();
    }
}
