// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core.roles;

import java.io.*;

public interface TextEditor extends Role
{
    String getEditorFileName();
    boolean isUnsaved();
    void save();
}
