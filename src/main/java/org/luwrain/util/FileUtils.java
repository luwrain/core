// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.util;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static java.util.Objects.*;
import static java.nio.file.Files.*;
import static org.luwrain.util.StreamUtils.*;
import static org.luwrain.util.Sha1.*;

/**
 * Provides utility methods for reading and writing files.
 *
 * <p>The class is centered around text files. It offers methods to read a
 * whole file as a {@code String}, to write a {@code String} to a file, to
 * split file content into lines using an arbitrary separator, and to resolve a
 * possibly relative path against a base directory. It also provides a helper
 * that creates a file filled with random bytes and returns the SHA-1 checksum
 * of the written data.</p>
 *
 * <p>All text operations accept an explicit character encoding name. The
 * {@link #UTF_8} constant holds the name of the UTF-8 encoding, and two
 * convenience overloads use it automatically. The methods in this class do not
 * perform any automatic character-set detection.</p>
 *
 * <p>Most methods validate their arguments with {@link Object#requireNonNull}
 * and throw {@link IllegalArgumentException} for semantically invalid values,
 * such as an empty encoding name. Methods that perform I/O throw
 * {@link IOException}.</p>
 *
 * <p>This class cannot be instantiated.</p>
 */
public final class FileUtils
{
    /**
     * Name of the UTF-8 character encoding, as accepted by Java character-set
     * converters.
     */
    static public final String UTF_8 = "UTF-8";

    /**
     * Creates a file containing the requested number of pseudo-random bytes and
     * returns the SHA-1 checksum of those bytes.
     *
     * <p>The bytes are produced by {@link Random}. This source is sufficient
     * for non-security purposes, such as generating temporary content, but must
     * not be used where cryptographic strength is required.</p>
     *
     * @param path path of the file to create, must not be {@code null}
     * @param len number of random bytes to write, must not be negative
     * @return SHA-1 checksum of the written bytes as a lowercase hex string
     * @throws IOException if an I/O error occurs
     * @throws NullPointerException if {@code path} is {@code null}
     * @throws IllegalArgumentException if {@code len} is negative
     */
    static public String writeRandomFile(Path path, int len) throws IOException
    {
	requireNonNull(path, "path can't be null");
	if (len < 0)
	    throw new IllegalArgumentException("len (" + String.valueOf(len) + ") can't be negative");
	byte[] data = new byte[len];
	new Random().nextBytes(data);
	write(path, data);
	return getSha1(data);
    }

    /**
     * Reads the entire file and returns its content as a string decoded with
     * the specified character set.
     *
     * @param file file to read, must not be {@code null}
     * @param charset name of the character encoding to use, must not be
     * {@code null} or empty
     * @return file content as a string
     * @throws IOException if an I/O error occurs
     * @throws NullPointerException if {@code file} or {@code charset} is
     * {@code null}
     * @throws IllegalArgumentException if {@code charset} is empty
     */
    static public String readTextFile(File file, String charset) throws IOException
    {
	requireNonNull(file, "file can't be null");
	requireNonNull(charset, "charset can't be null");
	if (charset.isEmpty())
	    throw new IllegalArgumentException("charset can't be empty");
	try (final var is = new FileInputStream(file)) {
    	    return new String(readAllBytes(is), charset);
	}
    }

    /**
     * Reads the entire file as UTF-8 encoded text.
     *
     * @param file file to read, must not be {@code null}
     * @return file content as a string
     * @throws IOException if an I/O error occurs
     * @throws NullPointerException if {@code file} is {@code null}
     * @see #readTextFile(File, String)
     */
    static public String readTextFile(File file) throws IOException
    {
	return readTextFile(file, UTF_8);
    }

    /**
     * Writes a string to a file using the specified character set.
     *
     * <p>If the file does not exist, it is created; otherwise its previous
     * content is overwritten.</p>
     *
     * @param file file to write, must not be {@code null}
     * @param text text to write, must not be {@code null}
     * @param charset name of the character encoding to use, must not be
     * {@code null} or empty
     * @throws IOException if an I/O error occurs
     * @throws NullPointerException if {@code file}, {@code text} or
     * {@code charset} is {@code null}
     * @throws IllegalArgumentException if {@code charset} is empty
     */
    static public void writeTextFile(File file, String text, String charset) throws IOException
    {
	requireNonNull(file, "file can't be null");
	requireNonNull(text, "text can't be null");
	requireNonNull(charset, "charset can't be null");
	if (charset.isEmpty())
	    throw new IllegalArgumentException("charset can't be empty");
	final OutputStream os = new FileOutputStream(file);
	try {
	    writeAllBytes(os, text.getBytes(charset));
	}
	finally {
	    os.flush();
	    os.close();
	}
    }

    /**
     * Reads the entire file and splits its content into strings using the
     * specified line separator.
     *
     * <p>If {@code lineSeparator} is {@code null}, the system default line
     * separator is used. The separator is interpreted as a regular expression,
     * as required by {@link String#split}. An empty file results in an empty
     * array.</p>
     *
     * @param file file to read, must not be {@code null}
     * @param charset name of the character encoding to use, must not be
     * {@code null} or empty
     * @param lineSeparator line separator to split on, may be {@code null} to
     * use the system default
     * @return array of strings representing the lines of the file
     * @throws IOException if an I/O error occurs
     * @throws NullPointerException if {@code file} or {@code charset} is
     * {@code null}
     * @throws IllegalArgumentException if {@code charset} is empty
     */
    static public String[] readTextFileMultipleStrings(File file, String charset, String lineSeparator) throws IOException
    {
	requireNonNull(file, "file can't be null");
	requireNonNull(charset, "charset can't be null");
	if (charset.isEmpty())
	    throw new IllegalArgumentException("charset can't be empty");
	final String text = readTextFile(file, charset);
	if (text.isEmpty())
	    return new String[0];
	return text.split(lineSeparator != null?lineSeparator:System.getProperty("line.separator"), -1);
    }

    /**
     * Resolves a possibly relative path against a base directory.
     *
     * <p>If {@code path} is absolute, it is returned as a {@code File}
     * representing that path. Otherwise a {@code File} is created from
     * {@code baseDir} and {@code path}.</p>
     *
     * @param baseDir base directory to resolve relative paths against, must not
     * be {@code null}
     * @param path path to resolve, must not be {@code null} or empty
     * @return the resolved {@code File}
     * @throws NullPointerException if {@code baseDir} or {@code path} is
     * {@code null}
     * @throws IllegalArgumentException if {@code path} is empty
     */
    static public File ifNotAbsolute(File baseDir, String path)
    {
	requireNonNull(baseDir, "baseDir can't be null");
	requireNonNull(path, "path can't be null");
	if (path.isEmpty())
	    throw new IllegalArgumentException("path can't be empty");
	final File file = new File(path);
	if (file.isAbsolute())
	    return file;
	return new File(baseDir, path);
    }
}