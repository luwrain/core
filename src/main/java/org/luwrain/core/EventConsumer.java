// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

public interface EventConsumer
{
    void enqueueEvent(org.luwrain.core.Event event);
}
