// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import java.io.*;

public interface PropertiesBase
{
    String getProperty(String propName);
    File getFileProperty(String propName);
}
