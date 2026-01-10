// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.text.indent;

import org.luwrain.core.*;

import static java.util.Objects.*;

public class IndentUtils
{
    public int getIndent(MutableLines lines, int lineIndex)
    {
	requireNonNull(lines, "lines can't be null");
	final String line = lines.getLine(lineIndex);
	int res = 0;
	for(int i = 0;i < line.length();i++)
	{
	    final int len = getIndentLen(line.charAt(i));
	    if (len <= 0)
		return res;
res += len;
	}
	return res;
    }

    public void setIndent(MutableLines lines, int lineIndex, int oldIndent, int newIndent)
    {
	requireNonNull(lines, "lines can't be null");
	final String line = lines.getLine(lineIndex);
	final LineMarks marks;
	if (lines instanceof MutableMarkedLines markedLines)
	    marks = markedLines.getLineMarks(lineIndex); else
marks = null;
	int fromPos = 0;
	while(fromPos < line.length() && getIndentLen(line.charAt(fromPos)) > 0)
	    fromPos++;
	int indent = newIndent;
	final var b = new StringBuilder();
	final int tabLen = getTabLen();
	while (indent > 0)
	    if (indent >= tabLen)
	{
	    b.append("\t");
	    indent -= tabLen;
	} else
	    {
		b.append(" ");
		indent--;
	    }
	b.append(line.substring(fromPos));
	lines.setLine(lineIndex, new String(b));

    }

    protected int getIndentLen(char ch)
    {
	if (ch == '\t')
	    return getTabLen();
	if (Character.isWhitespace(ch))
	    return 1;
	return 0;
    }

    protected int getTabLen()
    {
	return 8;
    }
    }
