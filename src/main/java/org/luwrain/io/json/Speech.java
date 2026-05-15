// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.json;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Speech
{
    private String mainEngineName, mainEngineParams;
    private String listeningEngineName, listeningEngineParams;
    private int pitch, rate, listeningPitch, listeningRate;
}
