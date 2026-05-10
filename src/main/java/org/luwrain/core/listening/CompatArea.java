// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core.listening;

import org.luwrain .core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import static java.util.Objects.*;

final class CompatArea implements ListenableArea
{
    private final Area area;

    CompatArea(Area area)
    {
	requireNonNull(area, "area can't be null");
	this.area = area;
    }

    @Override public ListeningInfo onListeningStart()
    {
	final BeginListeningQuery query = new BeginListeningQuery();
	if (!AreaQuery.ask(area, query))
	    return null;
	return new Info(query.getAnswer().getText(), query.getAnswer().getExtraInfo(), -1, -1);
    }

    @Override public void onListeningFinish(ListeningInfo listeningInfo)
    {
	requireNonNull(listeningInfo, "listeningInfo can't be null");
	if (!(listeningInfo instanceof Info))
	    return;
	final Info info = (Info)listeningInfo;
	area.onSystemEvent(new ListeningFinishedEvent(info.extraData));
    }

    static private final class Info extends ListeningInfo
    {
	final Object extraData;
	Info(String text, Object extraData, int posX, int posY)
	{
	    super(text, posX, posY);
	    this.extraData = extraData;
	}
    }
}
