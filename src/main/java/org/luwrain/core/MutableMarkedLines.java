// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

public interface MutableMarkedLines extends MarkedLines, MutableLines
{
    void setLineMarks(int index, LineMarks lineMarks);
}
