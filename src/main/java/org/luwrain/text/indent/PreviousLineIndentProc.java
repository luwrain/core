// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.text.indent;

import java.util.*;
import org.luwrain.core.*;

public class PreviousLineIndentProc implements IndentProc
{
    protected List<String> lines = null;
    protected final IndentUtils utils = new IndentUtils();
    
    @Override public void updateLines(MutableLines lines)
    {
	this.lines = new ArrayList<>(lines.getLines());
    }
    
    @Override public String getIndentedLine(int lineIndex)
    {
	if (lineIndex < 0)
	    throw new IllegalArgumentException("lineIndex can't be negative");
	if (lineIndex == 0)
	    return "";
	return null;
    }
}
