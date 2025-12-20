// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import java.io.*;
import lombok.*;

@Data
@NoArgsConstructor
public final class Config
{
    File appDir, dataDir, userHomeDir, userDataDir, configsDir;
    File jsDir, packsDir, soundsDir, userVarDir;
    String lang;
    Args args;
    Configs configs;
    ClassLoader coreClassLoader;
    Interaction interaction;
    OperatingSystem operatingSystem;
}
