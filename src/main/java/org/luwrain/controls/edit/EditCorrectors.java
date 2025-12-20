// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.controls.edit;

import org.luwrain.core.*;
import org.luwrain.controls.edit.MultilineEdit.*;
import org.luwrain.controls.edit.EditUtils.*;

public final class EditCorrectors
{
    static public class IndentationCorrector extends MultilineEditModelWrap
    {
	public IndentationCorrector(MultilineEdit.Model basicCorrector) { super(basicCorrector); }
	@Override public ModificationResult splitLine(int pos, int lineIndex)
	{
	    final ModificationResult res = wrappedModel.splitLine(pos, lineIndex);
	    if (!res.isPerformed())
		return res;
	    final int indent = getIndent(lineIndex);
	    if (!deleteIndent(lineIndex + 1))
		return new ModificationResult(false);
	    if (!addIndent(lineIndex + 1, indent))
		return new ModificationResult(false);  
	    return res;
	}
	protected int getIndent(int lineIndex)
	{
	    final int tabLen = getTabLen();
	    final String line = getLine(lineIndex);
	    int res = 0;
	    for(int i = 0;i < line.length() && Character.isWhitespace(line.charAt(i));i++)
		if (line.charAt(i) == '\t')
		    res += tabLen; else
		    ++res;
	    return res;
	}
	protected boolean deleteIndent(int lineIndex)
	{
	    final String line = getLine(lineIndex);
	    int pos = 0;
	    while (pos < line.length() && Character.isWhitespace(line.charAt(pos)))
		pos++;
	    if (pos == 0)
		return true;
	    return wrappedModel.deleteRegion(0, lineIndex, pos, lineIndex).isPerformed();
	}
	protected boolean addIndent(int lineIndex, int len)
	{
	    if (len == 0)
		return true;
	    final int tabLen = getTabLen();
	    final StringBuilder b = new StringBuilder();
	    final int tabCount = len / tabLen;
	    for(int i = 0;i < tabCount;i++)
		b.append('\t');
	    final int spaceCount = len % tabLen;
	    for(int i = 0;i < spaceCount;i++)
		b.append(' ');
	    return wrappedModel.putChars(0, lineIndex, new String(b)).isPerformed();
	}
	protected int getTabLen()
	{
	    return 8;
	}
    }

    static public class WordWrapCorrector extends MultilineEditModelWrap
    {
	protected final int lineLen;
	public WordWrapCorrector(MultilineEditCorrector basicCorrector, int lineLen) {
	    super(basicCorrector);
	    if (lineLen < 0)
		throw new IllegalArgumentException("lineLen can't be negative");
	    this.lineLen = lineLen;
	}
	@Override public ModificationResult putChars(int pos, int lineIndex, String str)
	{
	    NullCheck.notNull(str, "str");
	    if (!str.equals(" ") || pos != getHotPointX() || lineIndex != getHotPointY())
		return super.putChars(pos, lineIndex, str);
	    final String line = getLine(lineIndex);
	    NullCheck.notNull(line, "line");
	    if (line.length() <= lineLen || getHotPointX() <= lineLen)
		return super.putChars(pos, lineIndex, str);
	    //Looking for the first space before lineLen
	    int i = -1;
	    for(i = lineLen;i >= 0;i--)
		if (Character.isWhitespace(line.charAt(i)))
		    break;
	    if (i <= 0)
		return super.putChars(pos, lineIndex, str);
super.splitLine(i, lineIndex);
	    return new ModificationResult(true, getLine(lineIndex + 1), ' ');
	}
    }
}
