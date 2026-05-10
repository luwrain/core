// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.controls;

public interface EmbeddedEditLines
{
    String getEmbeddedEditLine(int editPosX, int editPosY);
    void setEmbeddedEditLine(int editPosX, int editPosY, String value);
}
