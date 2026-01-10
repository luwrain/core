// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.text.indent;

import org.luwrain.core.*;

public interface IndentProc
{
    void updateLines(MutableLines lines);
    String getIndentedLine(int lineIndex);
}
