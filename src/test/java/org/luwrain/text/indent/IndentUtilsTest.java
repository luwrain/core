// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.text.indent;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import org.luwrain.controls.*;
import org.luwrain.controls.DefaultLineMarks.MarkImpl;

import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.*;
import com.oracle.truffle.js.runtime.JSContextOptions;

public class IndentUtilsTest
{
    @Test void basic()
    {
	final var lines = new MutableMarkedLinesImpl();
	lines.add("abc");
	final var u = new IndentUtils();
	u.setIndent(lines, 0, 0, 10);
	assertEquals(1, lines.getLineCount());
	assertEquals("\t  abc", lines.getLine(0));
	assertEquals(10, u.getIndent(lines, 0));
	u.setIndent(lines, 0, 10, 0);
	assertEquals("abc", lines.getLine(0));
	assertEquals(0, u.getIndent(lines, 0));
    }

    @Test void mark()
    {
	final var lines = new MutableMarkedLinesImpl();
	lines.add("abc");
	lines.setLineMarks(0, new DefaultLineMarks.Builder()
		       .add(new MarkImpl(MarkImpl.Type.WEAK, 1, 2, null))
		       .build());
	assertEquals(1, lines.getLineMarks(0).getMarks().length);
	final var u = new IndentUtils();
	u.setIndent(lines, 0, 0, 10);
	assertEquals(1, lines.getLineMarks(0).getMarks().length);
	
    }
}
