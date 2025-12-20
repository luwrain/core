// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

public interface HookContainer
{
        public enum HookResult { CONTINUE, BREAK };

    public interface Hook
    {
	Object run(Object[] args);
    }

    public interface HookRunner
    {
	HookResult runHook(Hook hook);
    }

    boolean runHooks(String hookName, HookRunner runner);
}
