// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.settings;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;

class HardwareCpuMem extends SimpleArea implements SectionArea
{
    private final ControlPanel controlPanel;
    private final Luwrain luwrain;

    HardwareCpuMem(ControlPanel controlPanel)
    {
	super(new DefaultControlContext(controlPanel.getCoreInterface()), "Процессор и память");
	NullCheck.notNull(controlPanel, "controlPanel");
	this.controlPanel = controlPanel;
	this.luwrain = controlPanel.getCoreInterface();
	fillData();
    }

    private void fillData()
    {
	update((lines)->{
	int i = 0;
	while(true)
	{
	    final String cpu = luwrain.getProperty("luwrain.hardware.cpu." + i);
	    if (cpu.trim().isEmpty())
		break;
	    lines.addLine("Центральный процессор " + (i + 1) + ": " + cpu);
	    ++i;
	    if (i >= 1024)
		break;
	}
	lines.addLine("Объём оперативной памяти (МБ): " + luwrain.getProperty("luwrain.hardware.ramsizemb"));
	lines.addLine("");
	    });
    }

    @Override public boolean onInputEvent(InputEvent event)
    {
	NullCheck.notNull(event, "event");
	if (controlPanel.onInputEvent(event))
	    return true;
	return super.onInputEvent(event);
    }

    @Override public boolean onSystemEvent(SystemEvent event)
    {
	NullCheck.notNull(event, "event");
	if (controlPanel.onSystemEvent(event))
	    return true;
	return super.onSystemEvent(event);
    }

    @Override public boolean saveSectionData()
    {
	return true;
    }

    static HardwareCpuMem create(ControlPanel controlPanel)
    {
	NullCheck.notNull(controlPanel, "controlPanel");
	return new HardwareCpuMem(controlPanel);
    }
}
