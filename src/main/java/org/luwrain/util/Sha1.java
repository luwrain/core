// SPDX-License-Identifier: Apache-2.0
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.util;

import java.io.*;
import java.security.*;
import java.io.*;
import java.nio.file.*;

import org.luwrain.core.*;

import static java.util.Objects.*;
import static java.nio.file.Files.*;

public final class Sha1
{
    static public String getSha1(InputStream inputStream) throws IOException, NoSuchAlgorithmException
    {
	NullCheck.notNull(inputStream, "inputStream");
	final MessageDigest sha1;
	sha1 = MessageDigest.getInstance("SHA-1");
	sha1.reset();
	final byte[] buf = new byte[2048];
	while (true)
	{
	    final int len = inputStream.read(buf);
	    if (len <= 0)
		break;
	    sha1.update(buf, 0, len);
	}
	final StringBuilder res = new StringBuilder();
	for(byte b: sha1.digest())
	{
	    int value = Byte.valueOf(b).intValue();
	    if (value < 0)
		value = 256 + value;
	    final String hex = Integer.toHexString(value);
	    if (hex.length() < 2)
		res.append("0");
	    res.append(hex);
	}
	return new String(res);
    }

    static public String getSha1(String str, String charset)
    {
	NullCheck.notNull(str, "str");
	NullCheck.notEmpty(charset, "charset");
	try {
	    final ByteArrayInputStream s = new ByteArrayInputStream(str.getBytes(charset));
	    return getSha1(s);
	}
	catch(IOException | NoSuchAlgorithmException e)
	{
	    throw new RuntimeException(e);
	}
    }

        static public String getSha1(byte[] bytes)
    {
	NullCheck.notNull(bytes, "bytes");
	try {
	    final ByteArrayInputStream s = new ByteArrayInputStream(bytes);
	    return getSha1(s);
	}
	catch(IOException | NoSuchAlgorithmException e)
	{
	    throw new RuntimeException(e);
	}
    }

    static public String getSha1(Path path) throws IOException, NoSuchAlgorithmException
    {
	requireNonNull(path, "path can't be null");
	try (final var is = newInputStream(path)) {
	    return getSha1(is);
	}
    }
}
