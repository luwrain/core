// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import java.nio.file.*;

public interface OperatingSystem
{
    org.luwrain.core.InitResult init(PropertiesBase props);
    String escapeString(String style, String value);
    org.luwrain.core.Braille getBraille();
    void openFileInDesktop(Path path);
    org.luwrain.interaction.KeyboardHandler getCustomKeyboardHandler(String subsystem);
}
