// SPDX-License-Identifier: Apache-2.0
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.util;

import java.io.*;
import static java.util.Objects.*;

public final class StreamUtils
{
    static public final int BUF_SIZE = 2048;

    public interface Progress
    {
	void processed(int chunkNumBytes, long totalNumBytes);
    }

    public interface Cancelling
    {
	boolean cancelling();
    }

    static public long copyAllBytes(InputStream is, OutputStream os, Progress progress, Cancelling cancelling) throws IOException
    {
	requireNonNull(is, "is can't be null");
	requireNonNull(os, "os can't be null");
	long totalBytes = 0;
	final byte[] buf = new byte[BUF_SIZE];
	while(true)
	{
	    if (cancelling != null && cancelling.cancelling())
		return totalBytes;
	    final int length = is.read(buf);
	    if (length == -1)//According to javadoc, The marker that there is no more data to read
		return totalBytes;
	    writeAllBytes(os, buf, length);
	    totalBytes += length;
	    if (progress != null)
		progress.processed(length, totalBytes);
	}
    }

    static public void writeAllBytes(OutputStream os, byte[] bytes, int numBytes) throws IOException
    {
	requireNonNull(os, "os can't be null");
	requireNonNull(bytes, "bytes can't be null");
	if (numBytes < 0)
	    throw new IllegalArgumentException("numBytes (" + String.valueOf(numBytes) + ") can't be negative");
	if (numBytes == 0)
	    return;
	int pos = 0;
	while   (pos < numBytes)
	{
	    final int remaining = numBytes - pos;
	    final int numToWrite = remaining > BUF_SIZE?BUF_SIZE:remaining;
	    os.write(bytes, pos, numToWrite);
	    pos += numToWrite;
	}
    }

        static public void writeAllBytes(OutputStream os, byte[] bytes) throws IOException
    {
	requireNonNull(os, "os can't be null");
	requireNonNull(bytes, "bytes can't be null");
	writeAllBytes(os, bytes, bytes.length);
    }

    static public byte [] readAllBytes(InputStream is) throws IOException
    {
	requireNonNull(is, "is can't be null");
	final byte[] buf = new byte[BUF_SIZE];
	final ByteArrayOutputStream res = new ByteArrayOutputStream();
	int length = 0;
	do {
	    length = is.read(buf);
	    if (length > 0)
		writeAllBytes(res, buf, length);
	} while(length >= 0);
	return res.toByteArray();
    }
    
}
