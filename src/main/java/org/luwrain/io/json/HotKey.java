// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

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
