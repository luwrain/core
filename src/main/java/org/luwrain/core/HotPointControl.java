// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

public interface HotPointControl extends HotPoint
{
    void beginHotPointTrans();
    void endHotPointTrans();
    int getHotPointX();
    void setHotPointX(int value);
    int getHotPointY();
    void setHotPointY(int value);
}
