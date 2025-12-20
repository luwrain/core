// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.controls;

public interface TableClickHandler
{
    boolean onClick(TableArea.Model model,
		    int col,
		    int row,
		    Object obj);
}
