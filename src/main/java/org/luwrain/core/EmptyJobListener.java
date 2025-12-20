// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import java.util.*;

public class EmptyJobListener implements Job.Listener
{
    @Override public void onStatusChange(Job instance) {}
    @Override public void onInfoChange(Job instance, String infoType, List<String> value) {}
}
