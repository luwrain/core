// SPDX-License-Identifier: Apache-2.0
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.popups;

import java.util.*;
import java.io.*;

public interface FileAcceptance
{
    public enum Flags {};

    	boolean isPathAcceptable(File file, boolean announce);
}
