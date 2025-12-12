// SPDX-License-Identifier: Apache-2.0
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.util;

import java.util.*;

import java.io.*;

import org.luwrain.core.*;

public final class LinesSaver
{
    static public void saveLines(File file, Lines lines) throws IOException
    {
	NullCheck.notNull(file, "file");
	NullCheck.notNull(lines, "lines");
	FileOutputStream s = null;
	BufferedWriter w = null;
	try {
	    s = new FileOutputStream(file);
	    w = new BufferedWriter(new OutputStreamWriter(s));
	    final int count = lines.getLineCount();
	    for(int i = 0;i < count;++i)
	    {
		w.write(lines.getLine(i));
		w.newLine();
	    }
	}
	finally {
	    if (w != null)
	    {
		w.flush();
		w.close();
	    }
	    if (s != null)
	    {
		s.flush();
		s.close();
	    }
	}
    }
}
