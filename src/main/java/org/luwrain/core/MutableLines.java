// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import java.util.*;

public interface MutableLines extends Lines
{
    public interface Updating
    {
	public void update(MutableLines lines);
    }

    void update(Updating updating);
    String[] getLinesAsArray();
    List<String> getLines();
    void setLines(String[] lines);
    boolean add(String line);
    void add(int index, String line);
    void removeLine(int index);
    void setLine(int index, String line);
    void clear();
}
