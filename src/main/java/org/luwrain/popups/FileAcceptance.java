// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.popups;

import java.util.*;
import java.io.*;

public interface FileAcceptance
{
    public enum Flags {};

    	boolean isPathAcceptable(File file, boolean announce);
}
