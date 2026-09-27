// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.util;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.util.*;

public class StreamUtilsTest
{
    @Test void copyAllBytesWritesAllData() throws Exception
    {
	final int size = 5000;
	final byte[] data = makeData(size);
	final ByteArrayInputStream is = new ByteArrayInputStream(data);
	final ByteArrayOutputStream os = new ByteArrayOutputStream();
	final long res = StreamUtils.copyAllBytes(is, os, null, null);
	assertEquals((long)size, res);
	assertArrayEquals(data, os.toByteArray());
    }

    @Test void copyAllBytesEmptyStream() throws Exception
    {
	final ByteArrayInputStream is = new ByteArrayInputStream(new byte[0]);
	final ByteArrayOutputStream os = new ByteArrayOutputStream();
	final long res = StreamUtils.copyAllBytes(is, os, null, null);
	assertEquals(0L, res);
	assertEquals(0, os.size());
    }

    @Test void copyAllBytesReportsProgress() throws Exception
    {
	final int size = StreamUtils.BUF_SIZE * 2 + 10;
	final byte[] data = makeData(size);
	final List<Integer> chunkSizes = new ArrayList<>();
	final List<Long> totalSizes = new ArrayList<>();
	final StreamUtils.Progress progress = (chunk, total) -> {
	    chunkSizes.add(chunk);
	    totalSizes.add(total);
	};
	final ByteArrayOutputStream os = new ByteArrayOutputStream();
	final long copied = StreamUtils.copyAllBytes(new ByteArrayInputStream(data), os, progress, null);
	assertEquals((long)size, copied);
	assertArrayEquals(data, os.toByteArray());
	assertEquals(List.of(StreamUtils.BUF_SIZE, StreamUtils.BUF_SIZE, 10), chunkSizes);
	assertEquals(List.of((long)StreamUtils.BUF_SIZE, 2L * StreamUtils.BUF_SIZE, (long)size), totalSizes);
    }

    @Test void copyAllBytesDoesNotCallProgressForEmptyStream() throws Exception
    {
	final int[] calls = new int[1];
	final StreamUtils.Progress progress = (chunk, total) -> calls[0]++;
	final long copied = StreamUtils.copyAllBytes(
	    new ByteArrayInputStream(new byte[0]),
	    new ByteArrayOutputStream(),
	    progress,
	    null);
	assertEquals(0L, copied);
	assertEquals(0, calls[0]);
    }

    @Test void copyAllBytesCancelsBeforeReading() throws Exception
    {
	final byte[] data = makeData(100);
	final ByteArrayOutputStream os = new ByteArrayOutputStream();
	final StreamUtils.Cancelling cancelling = () -> true;
	final long res = StreamUtils.copyAllBytes(new ByteArrayInputStream(data), os, null, cancelling);
	assertEquals(0L, res);
	assertEquals(0, os.size());
    }

    @Test void copyAllBytesCancelsAfterFirstChunk() throws Exception
    {
	final int size = 100;
	final byte[] data = makeData(size);
	final int[] calls = new int[1];
	final StreamUtils.Cancelling cancelling = () -> {
	    calls[0]++;
	    return calls[0] > 1;
	};
	final ByteArrayOutputStream os = new ByteArrayOutputStream();
	final long res = StreamUtils.copyAllBytes(new ByteArrayInputStream(data), os, null, cancelling);
	assertEquals((long)size, res);
	assertArrayEquals(data, os.toByteArray());
	assertEquals(2, calls[0]);
    }

    @Test void copyAllBytesRejectsNullArguments()
    {
	assertThrows(NullPointerException.class,
	    () -> StreamUtils.copyAllBytes(null, new ByteArrayOutputStream(), null, null));
	assertThrows(NullPointerException.class,
	    () -> StreamUtils.copyAllBytes(new ByteArrayInputStream(new byte[0]), null, null, null));
    }

    @Test void copyAllBytesInputFailureThrows()
    {
	assertThrows(IOException.class,
	    () -> StreamUtils.copyAllBytes(new ThrowingInputStream(), new ByteArrayOutputStream(), null, null));
    }

    @Test void copyAllBytesOutputFailureThrows()
    {
	assertThrows(IOException.class,
	    () -> StreamUtils.copyAllBytes(new ByteArrayInputStream(makeData(10)), new ThrowingOutputStream(), null, null));
    }

    @Test void writeAllBytesWritesRequestedBytes() throws Exception
    {
	final byte[] data = makeData(20);
	final ByteArrayOutputStream os = new ByteArrayOutputStream();
	StreamUtils.writeAllBytes(os, data, 8);
	assertArrayEquals(Arrays.copyOf(data, 8), os.toByteArray());
    }

    @Test void writeAllBytesZeroBytes() throws Exception
    {
	final byte[] data = makeData(5);
	final ByteArrayOutputStream os = new ByteArrayOutputStream();
	StreamUtils.writeAllBytes(os, data, 0);
	assertEquals(0, os.size());
    }

    @Test void writeAllBytesWritesWholeArray() throws Exception
    {
	final byte[] data = makeData(StreamUtils.BUF_SIZE + 7);
	final ByteArrayOutputStream os = new ByteArrayOutputStream();
	StreamUtils.writeAllBytes(os, data);
	assertArrayEquals(data, os.toByteArray());
    }

    @Test void writeAllBytesSplitsIntoChunks() throws Exception
    {
	final int size = StreamUtils.BUF_SIZE * 3 + 5;
	final byte[] data = makeData(size);
	final ChunkRecordingOutputStream os = new ChunkRecordingOutputStream();
	StreamUtils.writeAllBytes(os, data, size);
	assertArrayEquals(data, os.toByteArray());
	assertEquals(List.of(StreamUtils.BUF_SIZE, StreamUtils.BUF_SIZE, StreamUtils.BUF_SIZE, 5), os.chunkSizes);
    }

    @Test void writeAllBytesRejectsNegativeNumBytes()
    {
	final byte[] data = new byte[4];
	assertThrows(IllegalArgumentException.class,
	    () -> StreamUtils.writeAllBytes(new ByteArrayOutputStream(), data, -1));
    }

    @Test void writeAllBytesRejectsTooLargeNumBytes()
    {
	final byte[] data = new byte[4];
	assertThrows(IllegalArgumentException.class,
	    () -> StreamUtils.writeAllBytes(new ByteArrayOutputStream(), data, 5));
    }

    @Test void writeAllBytesRejectsNullArguments()
    {
	final byte[] data = new byte[4];
	assertThrows(NullPointerException.class,
	    () -> StreamUtils.writeAllBytes(null, data, 4));
	assertThrows(NullPointerException.class,
	    () -> StreamUtils.writeAllBytes(new ByteArrayOutputStream(), null, 4));
	assertThrows(NullPointerException.class,
	    () -> StreamUtils.writeAllBytes(null, data));
	assertThrows(NullPointerException.class,
	    () -> StreamUtils.writeAllBytes(new ByteArrayOutputStream(), null));
    }

    @Test void writeAllBytesOutputFailureThrows()
    {
	final byte[] data = makeData(3);
	assertThrows(IOException.class,
	    () -> StreamUtils.writeAllBytes(new ThrowingOutputStream(), data, data.length));
    }

    @Test void readAllBytesReadsAllData() throws Exception
    {
	final int size = StreamUtils.BUF_SIZE * 3 + 100;
	final byte[] data = makeData(size);
	assertArrayEquals(data, StreamUtils.readAllBytes(new ByteArrayInputStream(data)));
    }

    @Test void readAllBytesExactBufferSize() throws Exception
    {
	final byte[] data = makeData(StreamUtils.BUF_SIZE);
	assertArrayEquals(data, StreamUtils.readAllBytes(new ByteArrayInputStream(data)));
    }

    @Test void readAllBytesExactMultipleOfBufferSize() throws Exception
    {
	final byte[] data = makeData(StreamUtils.BUF_SIZE * 2);
	assertArrayEquals(data, StreamUtils.readAllBytes(new ByteArrayInputStream(data)));
    }

    @Test void readAllBytesEmptyStream() throws Exception
    {
	assertArrayEquals(new byte[0], StreamUtils.readAllBytes(new ByteArrayInputStream(new byte[0])));
    }

    @Test void readAllBytesRejectsNullStream()
    {
	assertThrows(NullPointerException.class, () -> StreamUtils.readAllBytes(null));
    }

    @Test void readAllBytesInputFailureThrows()
    {
	assertThrows(IOException.class, () -> StreamUtils.readAllBytes(new ThrowingInputStream()));
    }

    private static byte[] makeData(int size)
    {
	final byte[] data = new byte[size];
	for (int i = 0; i < size; ++i)
	    data[i] = (byte)(i % 251);
	return data;
    }

    private static final class ThrowingInputStream extends InputStream
    {
	@Override public int read() throws IOException
	{
	    throw new IOException("read failed");
	}

	@Override public int read(byte[] b, int off, int len) throws IOException
	{
	    throw new IOException("read failed");
	}
    }

    private static final class ThrowingOutputStream extends OutputStream
    {
	@Override public void write(int b) throws IOException
	{
	    throw new IOException("write failed");
	}

	@Override public void write(byte[] b, int off, int len) throws IOException
	{
	    throw new IOException("write failed");
	}
    }

    private static final class ChunkRecordingOutputStream extends OutputStream
    {
	final List<Integer> chunkSizes = new ArrayList<>();
	final ByteArrayOutputStream data = new ByteArrayOutputStream();

	@Override public void write(int b)
	{
	    data.write(b);
	}

	@Override public void write(byte[] b, int off, int len)
	{
	    chunkSizes.add(len);
	    data.write(b, off, len);
	}

	byte[] toByteArray()
	{
	    return data.toByteArray();
	}
    }
}