/*
   Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

   This file is part of LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.core;

import java.util.*;

public interface JobLauncher extends ExtensionObject
{
    public enum Flags {WITH_SHORTCUT, INTERACTIVE_SHORTCUT};


    public enum Status {RUNNING, FINISHED};

        Job launch(Job.Listener listener, String[] args, String dir);
    Set<Flags> getJobFlags();
}
