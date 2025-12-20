// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.controls.edit;

import org.luwrain.core.*;

public interface MultilineEditCorrector extends MultilineEdit.Model
{
    MultilineEdit.ModificationResult doEditAction(TextEditAction action);
}
