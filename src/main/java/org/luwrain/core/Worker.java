// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

public interface Worker extends Runnable, ExtensionObject
{
    int getFirstLaunchDelay();
    int getLaunchPeriod();
}
