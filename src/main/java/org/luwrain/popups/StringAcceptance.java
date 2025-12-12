// SPDX-License-Identifier: Apache-2.0
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.popups;

import java.util.*;

public interface StringAcceptance 
{
    public enum Flags {};

    boolean acceptable(String inputLine);
}
