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

package org.luwrain.io.json;

import java.util.*;
import java.lang.reflect.*;

import com.google.gson.*;
import com.google.gson.reflect.*;
import lombok.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

@NoArgsConstructor
public final class HotKey
{
    static public final Type LIST_TYPE = new TypeToken<List<HotKey>>(){}.getType();

    private InputEvent ev;
    private String cmd;
    private String scr;

    public InputEvent getInputEvent() { return ev; }
    public void setInputEvent(InputEvent ev) { this.ev = ev; }
    public String getCommand() { return cmd; }
    public void setCommand(String cmd) { this.cmd = cmd; }
    public String getScript() { return scr; }
    public void setScript(String scr) { this.scr = scr; }
}
