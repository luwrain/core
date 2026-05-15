// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.json;

import java.util.*;
import lombok.*;

@Data
@NoArgsConstructor
public final class PersonalInfo
{
    private String  fullName, mailAddr;
	private List<String> signature;
}
