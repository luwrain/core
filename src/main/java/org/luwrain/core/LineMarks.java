// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import java.util.*;
import java.util.function.*;

public interface LineMarks
{
    Mark[] getMarks();
    Mark[] findAtPos(int pos);
LineMarks filter(Predicate<Mark> cond);
    void transform(Function<Mark, Mark> transformation);

    public interface MarkObject
    {
    }

    public interface Mark
    {
	public enum Type {WEAK, EXPANDABLE};
	Type getType();
	int getPosFrom();
	int getPosTo();
	MarkObject getMarkObject();
	Mark repos(int newPosFrom, int newPosTo);
    }
}
