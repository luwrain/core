// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.util;

import java.io.*;
import static java.util.Objects.*;

/**
 * Provides utility methods for reading, writing and copying byte streams.
 *
 * <p>All methods of this class work with raw byte data. None of them closes
 * any stream passed as an argument; closing streams remains the responsibility
 * of the caller.</p>
 *
 * <p>The class cannot be instantiated.</p>
 */
public final class StreamUtils
{
    static public final int BUF_SIZE = 2048;

    /**
     * Receives notifications about the progress of {@code copyAllBytes}.
     */
    public interface Progress
    {
	/**
	 * Called after the next portion of data has been copied.
	 *
	 * @param chunkNumBytes number of bytes copied by this chunk
	 * @param totalNumBytes total number of bytes copied so far
	 */
	void processed(int chunkNumBytes, long totalNumBytes);
    }

    /**
     * Allows {@code copyAllBytes} to be cancelled by an external condition.
     */
    public interface Cancelling
    {
	/**
	 * Checks whether the copy operation should stop.
	 *
	 * @return {@code true} if the operation should be cancelled, {@code false} otherwise
	 */
	boolean cancelling();
    }

    /**
     * Copies all available bytes from an input stream to an output stream.
     *
     * <p>Data is copied using an internal buffer of {@link #BUF_SIZE} bytes.
     * The streams are not closed.</p>
     *
     * <p>If {@code cancelling} is not {@code null}, it is checked before
     * reading each chunk. When it reports cancellation, the method returns the
     * number of bytes already copied.</p>
     *
     * <p>If {@code progress} is not {@code null}, it is notified after each
     * successfully copied chunk.</p>
     *
     * @param is input stream to read from, must not be {@code null}
     * @param os output stream to write to, must not be {@code null}
     * @param progress optional progress listener, may be {@code null}
     * @param cancelling optional cancellation check, may be {@code null}
     * @return total number of bytes copied
     * @throws IOException if an I/O error occurs
     * @throws NullPointerException if {@code is} or {@code os} is {@code null}
     */
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

    /**
     * Writes the specified number of bytes from a byte array to an output
     * stream.
     *
     * <p>The method writes exactly {@code numBytes} bytes starting from the
     * beginning of the array. The stream is not closed and is not flushed.</p>
     *
     * @param os output stream to write to, must not be {@code null}
     * @param bytes byte array containing data to write, must not be {@code null}
     * @param numBytes number of bytes to write
     * @throws IOException if an I/O error occurs
     * @throws NullPointerException if {@code os} or {@code bytes} is {@code null}
     * @throws IllegalArgumentException if {@code numBytes} is negative or
     * greater than {@code bytes.length}
     */
    static public void writeAllBytes(OutputStream os, byte[] bytes, int numBytes) throws IOException
    {
	requireNonNull(os, "os can't be null");
	requireNonNull(bytes, "bytes can't be null");
	if (numBytes < 0)
	    throw new IllegalArgumentException("numBytes (" + String.valueOf(numBytes) + ") can't be negative");
	if (numBytes > bytes.length)
	    throw new IllegalArgumentException("numBytes (" + String.valueOf(numBytes) + ") can't be greater than bytes.length (" + String.valueOf(bytes.length) + ")");
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

    /**
     * Writes the whole byte array to an output stream.
     *
     * @param os output stream to write to, must not be {@code null}
     * @param bytes byte array to write, must not be {@code null}
     * @throws IOException if an I/O error occurs
     * @throws NullPointerException if {@code os} or {@code bytes} is {@code null}
     */
    static public void writeAllBytes(OutputStream os, byte[] bytes) throws IOException
    {
	requireNonNull(os, "os can't be null");
	requireNonNull(bytes, "bytes can't be null");
	writeAllBytes(os, bytes, bytes.length);
    }

    /**
     * Reads all remaining bytes from an input stream.
     *
     * <p>The method reads until the end of the stream and returns all read
     * bytes as a new array. The stream is not closed.</p>
     *
     * @param is input stream to read from, must not be {@code null}
     * @return byte array with all data read from the stream
     * @throws IOException if an I/O error occurs
     * @throws NullPointerException if {@code is} is {@code null}
     */
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