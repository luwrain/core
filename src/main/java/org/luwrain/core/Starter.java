// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

public interface Starter extends ExtensionObject
{
    StarterCategory getCategory();
    java.net.URI getUri();
}
