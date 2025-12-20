// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.util;


import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.util.*;
import org.luwrain.core.*;

public class Sha1Test
{
    @Test public void emptyStream() throws Exception
    {
	final byte[] emptyBuf = new byte[0];
final ByteArrayInputStream s = new ByteArrayInputStream(emptyBuf);
final String res = Sha1.getSha1(s);
//assertTrue(res.equals("da39a3ee5e6b4b0d3255bfef95601890afd80709"));
for(var f: List.of("yutf-8.txt", "cp1251.txt", "koi8-r.txt", "866.txt"))
{
}
    }
}
