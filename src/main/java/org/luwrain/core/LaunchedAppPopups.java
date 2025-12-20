// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import java.util.*;

import static org.luwrain.core.NullCheck.*;

class LaunchedAppPopups
{
    final List<OpenedArea> popupWrappings = new ArrayList<>();

    //Returns the index of the new popup
    int addPopup(Area popup)
    {
	NullCheck.notNull(popup, "popup");
	//	popups.add(popup);
	final OpenedArea wrapping = new OpenedArea(popup);
	popupWrappings.add(wrapping);
	return popupWrappings.size() - 1;
    }

    void closeLastPopup()
    {
	popupWrappings.remove(popupWrappings.size() - 1);
	//	popups.remove(popups.size() - 1);
    }

    Area getNativeAreaOfPopup(int index)
    {
	if (index < 0 || index >= popupWrappings.size())
	    throw new IllegalArgumentException("index (" + index + ") must be non-negative and less than " + popupWrappings.size());
	return popupWrappings.get(index).area;
    }

    Area getFrontAreaOfPopup(int index)
    {
		if (index < 0 || index >= popupWrappings.size())
	    throw new IllegalArgumentException("index (" + index + ") must be non-negative and less than " + popupWrappings.size());
			return popupWrappings.get(index).getFrontArea();
    }

    /**
     * Looks for the effective area for the specified one. Provided reference
     * may designate the required effective area, pointing either to the
     * natural area, either to the security wrapper or to the review
     * wrapper. This method may return the provided reference itself (e.g. if
     * provided reference points to the security wrapper and there is no a
     * review wrapper).
     *
     * @param area The area designating a cell in application layout by the natural area itself or by any of its wrappers
     * @return The effective area which corresponds to the requested cell in the application layout
    */
    Area getCorrespondingFrontArea(Area area)
    {
	notNull(area, "area");
	for(OpenedArea w: popupWrappings)
	    if (w.hasArea(area))
		return w.getFrontArea();
	return null;
    }

    /**
     * Returns the area wrapping object for the required area. Provided
     * reference designates a cell in the application layout, pointing either
     * to the natural area, either to the security wrapper or to the review
     * wrapper.
     *
     * @param area The area designating a cell in application layout by the natural area itself or by any of its wrappers
     * @return The area wrapping which corresponds to  the requested cell of the application layout
     */
    OpenedArea getAreaWrapping(Area area)
    {
	notNull(area, "area");
	for(OpenedArea w: popupWrappings)
	    if (w.hasArea(area))
		return w;
	return null;
    }

        void sendBroadcastEvent(org.luwrain.core.events.SystemEvent event)
    {
	NullCheck.notNull(event, "event");
	for(OpenedArea area: popupWrappings)
	    area.area.onSystemEvent(event);
    }
}
