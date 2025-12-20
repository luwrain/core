// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

/**
 * A context-independent action in the system. 
 * A command represents a named action capable of assigning to a hotkey.
 * This kind of operations can be performed regardless of the context (no matter which applications a user keeps opened etc).
 * A command can hav either a native implementation provided through extensions or a JavaScript implementation.
 *
 * @see Shortcut
 */
public interface Command
{
    String getName();
    void onCommand(Luwrain luwrain);
}
