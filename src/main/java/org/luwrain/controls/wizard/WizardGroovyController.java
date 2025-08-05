/*
   Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

   This file is part of LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.controls.wizard;

import java.util.*;

import groovy.lang.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.controls.WizardArea.*;

import static java.util.Objects.*;

public class WizardGroovyController
{
    final Luwrain luwrain;
    final WizardArea area;
    final Map<String, Frame> frames = new HashMap<>();

    public WizardGroovyController(Luwrain luwrain, WizardArea area)
    {
	this.luwrain = requireNonNull(luwrain, "luwrain can't be null");
	this.area = requireNonNull(area, "area can't be null");
    }

    public void call(String title, String firstFrame, Closure frames)
    {
	requireNonNull(title, "title can't be null");
	requireNonNull(firstFrame, "firstFrame can't be null");
	requireNonNull(frames, "frames can't be null");
	if (firstFrame.isEmpty())
	    throw new IllegalArgumentException("firstFrame can't be empty");
	frames.setDelegate(this);
	frames.call();
	final var frame = this.frames.get(firstFrame);
	if (frame == null)
	    throw new IllegalArgumentException("No first frame: " + firstFrame);
	area.show(frame);
    }

    public void frame(String id, Closure closure)
    {
	requireNonNull(id, "id can't be null");
	requireNonNull(closure, "closure can't be null");
	if (id.isEmpty())
	    throw new IllegalArgumentException("id can't be empty");
	final var f = area.newFrame();
	final var d = new FrameDelegate(this, f);
	closure.setDelegate(d);
	closure.call();
	frames.put(id, f);
    }
    }
