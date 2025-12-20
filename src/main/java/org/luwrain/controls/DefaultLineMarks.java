// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.controls;

import java.util.*;
import java.util.function.*;

import org.luwrain.core.*;

import static org.luwrain.util.RangeUtils.*;

public class DefaultLineMarks implements LineMarks
{
    final Mark[] marks;
    DefaultLineMarks(Mark[] marks)
    {
	NullCheck.notNullItems(marks, "marks");
	this.marks = marks;
    }
    @Override public Mark[] getMarks()
    {
	return this.marks.clone();
    }
    @Override public LineMarks.Mark[] findAtPos(int pos)
    {
	 final List<LineMarks.Mark> res = new ArrayList<>();
	 for(LineMarks.Mark m: marks)
	     if (between(pos, m.getPosFrom(), m.getPosTo()))
		 res.add(m);
	 return res.toArray(new LineMarks.Mark[res.size()]);
	 }
@Override public DefaultLineMarks filter(Predicate<LineMarks.Mark> cond)
{
NullCheck.notNull(cond, "cond");
final List<LineMarks.Mark> res = new ArrayList<>();
for(LineMarks.Mark m: marks)
if (cond.test(m))
res.add(m);
return new DefaultLineMarks(res.toArray(new LineMarks.Mark[res.size()]));
}

    static public final class Builder
    {
	private final List<LineMarks.Mark> res = new ArrayList<>();
	public Builder(LineMarks marks)
	{
	    if (marks != null)
	    {
		final LineMarks.Mark[] newMarks = marks.getMarks();
		if (newMarks != null)
		    res.addAll(Arrays.asList(newMarks));
	    }
	}
	public Builder add(LineMarks.Mark mark)
	{
	    NullCheck.notNull(mark, "mark");
	    res.add(mark);
	    return this;
	}
	public Builder addAll(List<LineMarks.Mark> marks)
	{
	    NullCheck.notNull(marks, "marks");
	    res.addAll(marks);
	    return this;
	}
	public Builder addAll(LineMarks.Mark[] marks)
	{
	    NullCheck.notNullItems(marks, "marks");
	    res.addAll(Arrays.asList(marks));
	    return this;
	}
	public DefaultLineMarks build()
	{
	    return new DefaultLineMarks(res.toArray(new LineMarks.Mark[res.size()]));
	}
    }

    static public final class MarkImpl implements LineMarks.Mark
    {
	final Type type;
	final int posFrom, posTo;
	final MarkObject obj;
	public MarkImpl(Type type, int posFrom, int posTo, MarkObject obj)
	{
	    NullCheck.notNull(type, "type");
	    this.type = type;
	    this.posFrom = posFrom;
	    this.posTo = posTo;
	    this.obj = obj;
	}
	@Override public Type getType() { return type; }
	@Override public int getPosFrom() { return posFrom; }
	@Override public int getPosTo() { return posTo; }
	@Override public MarkObject getMarkObject() { return obj; }
    }
}
