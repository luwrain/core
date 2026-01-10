// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.controls;

import java.util.*;
import java.util.function.*;

import org.luwrain.core.*;

import static java.util.Objects.*;
import static org.luwrain.util.RangeUtils.*;

public class DefaultLineMarks implements LineMarks
{
    final List<Mark> marks = new ArrayList<>();
    
    DefaultLineMarks(Mark[] marks)
    {
	NullCheck.notNullItems(marks, "marks");
	this.marks.addAll(Arrays.asList (marks));
    }
    
    @Override public Mark[] getMarks()
    {
	return marks.toArray(new Mark[marks.size()]);
    }
    
    @Override public LineMarks.Mark[] findAtPos(int pos)
    {
	final List<LineMarks.Mark> res = marks.parallelStream()
	.filter(e -> between(pos, e.getPosFrom(), e.getPosTo()))
	.toList();
	 return res.toArray(new LineMarks.Mark[res.size()]);
	 }
    
    @Override public DefaultLineMarks filter(Predicate<LineMarks.Mark> cond)
{
requireNonNull(cond, "cond can't be null");
final List<LineMarks.Mark> res = marks.parallelStream()
.filter(e -> cond.test(e))
.toList();
return new DefaultLineMarks(res.toArray(new LineMarks.Mark[res.size()]));
}

    @Override public     void transform(Function<Mark, Mark> transformation)
    {
	final var res = new ArrayList<Mark>();
	res.ensureCapacity(marks.size());
	for(var m: marks)
	{
	    final var mm = transformation.apply(m);
	    if (mm != null)
		res.add(mm);
	}
	marks.clear();
	marks.addAll(res);
    }

    static public final class Builder
    {
	private final List<LineMarks.Mark> res = new ArrayList<>();
	public Builder(LineMarks marks)
	{
	    if (marks != null)
	    {
		final var newMarks = marks.getMarks();
		if (newMarks != null)
		    res.addAll(Arrays.asList(newMarks));
	    }
	}
		public Builder() { this(null); }
	public Builder add(LineMarks.Mark mark)
	{
	    requireNonNull(mark, "mark can't be null");
	    res.add(mark);
	    return this;
	}
	public Builder addAll(List<LineMarks.Mark> marks)
	{
	    requireNonNull(marks, "marks can't be null");
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
	    requireNonNull(type, "type can't be null");
	    if (posFrom < 0)
		throw new IllegalArgumentException("posFrom can't be negative");
	    if (posTo < 0)
		throw new IllegalArgumentException("posTo can't be negative");
	    if (posTo <= posFrom)
		throw new IllegalArgumentException("posTo must be strictly greater than posFrom");
	    this.type = type;
	    this.posFrom = posFrom;
	    this.posTo = posTo;
	    this.obj = obj;
	}

	@Override public MarkImpl repos(int newPosFrom, int newPosTo)
	{
	    return new MarkImpl(type, newPosFrom, newPosTo, obj);    
	}
	
	@Override public Type getType()
	{
	    return type;
	}
	
	@Override public int getPosFrom()
	{
	    return posFrom;
	}
	
	@Override public int getPosTo()
	{
	    return posTo;
	}
	
	@Override public MarkObject getMarkObject()
	{
	    return obj;
	}
    }
}
