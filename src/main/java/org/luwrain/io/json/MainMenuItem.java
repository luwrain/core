// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.json;

import lombok.*;

import static java.util.Objects.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MainMenuItem
{
    static public final String TYPE_UNIREF = "uri";

    private String type, value;

    public String getType()
    {
	return requireNonNull(type, "");
    }

    public String getValue()
    {
	return requireNonNullElse(value, "");
    }
}
