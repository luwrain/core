/*
   Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

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

import com.google.gson.annotations.*;
import lombok.*;
import org.luwrain.core.events.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public final class HotKey
{
    @SerializedName("ev")
    private InputEvent inputEvent;

    @SerializedName("cmd")
    private String command;

    @SerializedName("scr")
    private String script;
}
