// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.util;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.*;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;

public class FileUtilsTest
{
    @TempDir Path tempDir;

    @Test void writeRandomFileDoesNotCreateFileWithNegativeLength()
    {
	final Path path = tempDir.resolve("neg.bin");
	assertThrows(IllegalArgumentException.class, () -> FileUtils.writeRandomFile(path, -1));
	assertFalse(Files.exists(path));
    }

    @Test void writeRandomFileRejectsNullPath()
    {
	assertThrows(NullPointerException.class, () -> FileUtils.writeRandomFile(null, 10));
    }

    @Test void writeRandomFileCreatesFileOfGivenLength() throws IOException
    {
	final int len = 1024;
	final Path path = tempDir.resolve("data.bin");
	final String sha1 = FileUtils.writeRandomFile(path, len);

	assertTrue(Files.exists(path));
	assertEquals((long)len, Files.size(path));
	assertNotNull(sha1);
	//The checksum must match the actual file content
	assertEquals(sha1, sha1Of(path));
    }

    @Test void writeRandomFileProducesChecksumOfWrittenBytes() throws IOException
    {
	final int len = 257;
	final Path path = tempDir.resolve("data2.bin");
	final String returned = FileUtils.writeRandomFile(path, len);
	final String expected = sha1Of(path);
	assertEquals(expected, returned);
	//SHA-1 hex is exactly 40 characters long
	assertEquals(40, expected.length());
    }

    @Test void writeRandomFileEmptyFile() throws IOException
    {
	final Path path = tempDir.resolve("empty.bin");
	final String returned = FileUtils.writeRandomFile(path, 0);
	assertEquals(0L, Files.size(path));
	//SHA-1 of an empty byte sequence is a well-known constant
	assertEquals("da39a3ee5e6b4b0d3255bfef95601890afd80709", returned);
    }

    @Test void readTextFileReadsUtf8Content() throws Exception
    {
	final File file = write(tempDir.resolve("utf8.txt"), "Hello, мир!", StandardCharsets.UTF_8);
	assertEquals("Hello, мир!", FileUtils.readTextFile(file, FileUtils.UTF_8));
	assertEquals("Hello, мир!", FileUtils.readTextFile(file));
    }

    @Test void readTextFileReadsCp1251Content() throws Exception
    {
	final Charset cp1251 = Charset.forName("CP1251");
	final File file = write(tempDir.resolve("cp1251.txt"), "Привет, мир!", cp1251);
	assertEquals("Привет, мир!", FileUtils.readTextFile(file, cp1251.name()));
    }

    @Test void readTextFileEmptyFile() throws IOException
    {
	final File file = write(tempDir.resolve("empty.txt"), new byte[0]);
	assertEquals("", FileUtils.readTextFile(file));
	assertEquals("", FileUtils.readTextFile(file, FileUtils.UTF_8));
    }

    @Test void readTextFileRejectsNullArguments()
    {
	assertThrows(NullPointerException.class, () -> FileUtils.readTextFile((File)null, FileUtils.UTF_8));
	assertThrows(NullPointerException.class, () -> FileUtils.readTextFile(tempDir.toFile(), null));
	assertThrows(NullPointerException.class, () -> FileUtils.readTextFile((File)null));
    }

    @Test void readTextFileRejectsEmptyCharset()
    {
	final File file = write(tempDir.resolve("whatever.txt"), new byte[0]);
	assertThrows(IllegalArgumentException.class, () -> FileUtils.readTextFile(file, ""));
    }

    @Test void readTextFileMissingFileThrows() throws Exception
    {
	final File missing = tempDir.resolve("missing.txt").toFile();
	assertThrows(IOException.class, () -> FileUtils.readTextFile(missing));
    }

    @Test void writeTextFileWritesUtf8Content() throws Exception
    {
	final File file = tempDir.resolve("out-utf8.txt").toFile();
	FileUtils.writeTextFile(file, "Hello, мир!", FileUtils.UTF_8);
	assertEquals("Hello, мир!", new String(java.nio.file.Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8));
    }

    @Test void writeTextFileWritesCp1251Content() throws Exception
    {
	final Charset cp1251 = Charset.forName("CP1251");
	final File file = tempDir.resolve("out-cp1251.txt").toFile();
	FileUtils.writeTextFile(file, "Привет, мир!", cp1251.name());
	assertArrayEquals("Привет, мир!".getBytes(cp1251), java.nio.file.Files.readAllBytes(file.toPath()));
    }

    @Test void writeTextFileOverwritesExistingFile() throws Exception
    {
	final File file = tempDir.resolve("overwrite.txt").toFile();
	FileUtils.writeTextFile(file, "first", FileUtils.UTF_8);
	FileUtils.writeTextFile(file, "second", FileUtils.UTF_8);
	assertEquals("second", FileUtils.readTextFile(file));
    }

    @Test void writeTextFileWritesEmptyString() throws Exception
    {
	final File file = tempDir.resolve("empty-out.txt").toFile();
	FileUtils.writeTextFile(file, "", FileUtils.UTF_8);
	assertEquals(0L, java.nio.file.Files.size(file.toPath()));
    }

    @Test void writeTextFileRejectsNullArguments()
    {
	final File file = tempDir.resolve("null.txt").toFile();
	assertThrows(NullPointerException.class, () -> FileUtils.writeTextFile(null, "x", FileUtils.UTF_8));
	assertThrows(NullPointerException.class, () -> FileUtils.writeTextFile(file, null, FileUtils.UTF_8));
	assertThrows(NullPointerException.class, () -> FileUtils.writeTextFile(file, "x", null));
    }

    @Test void writeTextFileRejectsEmptyCharset()
    {
	final File file = tempDir.resolve("empty-charset.txt").toFile();
	assertThrows(IllegalArgumentException.class, () -> FileUtils.writeTextFile(file, "x", ""));
    }

    @Test void readTextFileMultipleStringsSplitsByCustomSeparator() throws Exception
    {
	final File file = write(tempDir.resolve("mult.txt"), "alpha||beta||gamma||", StandardCharsets.UTF_8);
	final String[] res = FileUtils.readTextFileMultipleStrings(file, FileUtils.UTF_8, "||");
	assertArrayEquals(new String[]{"alpha", "beta", "gamma", ""}, res);
    }

    @Test void readTextFileMultipleStringsSplitsByNewline() throws Exception
    {
	final File file = write(tempDir.resolve("mult-nl.txt"), "one\ntwo\nthree", StandardCharsets.UTF_8);
	final String[] res = FileUtils.readTextFileMultipleStrings(file, FileUtils.UTF_8, "\n");
	assertArrayEquals(new String[]{"one", "two", "three"}, res);
    }

    @Test void readTextFileMultipleStringsKeepsTrailingEmptyStrings() throws Exception
    {
	final File file = write(tempDir.resolve("mult-trailing.txt"), "a,b,c,,\n", StandardCharsets.UTF_8);
	final String[] res = FileUtils.readTextFileMultipleStrings(file, FileUtils.UTF_8, ",");
	assertArrayEquals(new String[]{"a", "b", "c", "", "\n"}, res);
    }

    @Test void readTextFileMultipleStringsUsesSystemSeparatorWhenNull() throws Exception
    {
	final String sep = System.getProperty("line.separator");
	final File file = write(tempDir.resolve("mult-sys.txt"), "x" + sep + "y", StandardCharsets.UTF_8);
	final String[] res = FileUtils.readTextFileMultipleStrings(file, FileUtils.UTF_8, null);
	assertArrayEquals(new String[]{"x", "y"}, res);
    }

    @Test void readTextFileMultipleStringsEmptyFile() throws Exception
    {
	final File file = write(tempDir.resolve("mult-empty.txt"), new byte[0]);
	assertArrayEquals(new String[0], FileUtils.readTextFileMultipleStrings(file, FileUtils.UTF_8, "\n"));
    }

    @Test void readTextFileMultipleStringsSingleLineNoSeparator() throws Exception
    {
	final File file = write(tempDir.resolve("mult-single.txt"), "only one line", StandardCharsets.UTF_8);
	final String[] res = FileUtils.readTextFileMultipleStrings(file, FileUtils.UTF_8, ";");
	assertArrayEquals(new String[]{"only one line"}, res);
    }

    @Test void readTextFileMultipleStringsRejectsNullArguments()
    {
	final File file = tempDir.resolve("mult-null.txt").toFile();
	assertThrows(NullPointerException.class, () -> FileUtils.readTextFileMultipleStrings(null, FileUtils.UTF_8, "\n"));
	assertThrows(NullPointerException.class, () -> FileUtils.readTextFileMultipleStrings(file, null, "\n"));
    }

    @Test void readTextFileMultipleStringsRejectsEmptyCharset()
    {
	final File file = tempDir.resolve("mult-empty-charset.txt").toFile();
	assertThrows(IllegalArgumentException.class, () -> FileUtils.readTextFileMultipleStrings(file, "", "\n"));
    }

    @Test void ifNotAbsoluteReturnsAbsolutePathUnchanged()
    {
	final File abs = new File("/tmp", "file.txt");
	assertEquals(abs, FileUtils.ifNotAbsolute(tempDir.toFile(), abs.getPath()));
    }

    @Test void ifNotAbsoluteResolvesRelativePathAgainstBaseDir()
    {
	final File expected = new File(tempDir.toFile(), "sub/file.txt");
	assertEquals(expected, FileUtils.ifNotAbsolute(tempDir.toFile(), "sub/file.txt"));
    }

    @Test void ifNotAbsoluteRejectsNullArguments()
    {
	assertThrows(NullPointerException.class, () -> FileUtils.ifNotAbsolute(null, "x"));
	assertThrows(NullPointerException.class, () -> FileUtils.ifNotAbsolute(tempDir.toFile(), null));
    }

    @Test void ifNotAbsoluteRejectsEmptyPath()
    {
	assertThrows(IllegalArgumentException.class, () -> FileUtils.ifNotAbsolute(tempDir.toFile(), ""));
    }

    @Test void ifNotAbsoluteDoesNotTouchFileSystem()
    {
	//The method must resolve paths without creating anything on disk
	final File result = FileUtils.ifNotAbsolute(tempDir.toFile(), "no/such/dir/file.txt");
	assertFalse(result.exists());
    }

    private static File write(Path path, byte[] bytes)
    {
	try {
	    java.nio.file.Files.write(path, bytes);
	    return path.toFile();
	}
	catch(IOException e)
	{
	    throw new RuntimeException(e);
	}
    }

    private static File write(Path path, String text, Charset charset)
    {
	return write(path, text.getBytes(charset));
    }

    private static String sha1Of(Path path)
    {
	try {
	    final MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
	    final byte[] digest = sha1.digest(java.nio.file.Files.readAllBytes(path));
	    final StringBuilder res = new StringBuilder();
	    for (byte b : digest)
		res.append(String.format("%02x", b));
	    return res.toString();
	}
	catch(IOException | NoSuchAlgorithmException e)
	{
	    throw new RuntimeException(e);
	}
    }
}
