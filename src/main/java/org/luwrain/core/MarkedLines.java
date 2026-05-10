// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

public interface MarkedLines extends Lines
{
    LineMarks getLineMarks(int index);
}
