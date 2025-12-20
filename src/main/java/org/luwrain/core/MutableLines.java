// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

public interface MutableLines extends Lines
{
    public interface Updating
    {
	public void update(MutableLines lines);
    }

    void update(Updating updating);
    String[] getLines();
    void setLines(String[] lines);
    void addLine(String line);
    void insertLine(int index, String line);
    void removeLine(int index);
    void setLine(int index, String line);
    void clear();
}
