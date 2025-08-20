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

package org.luwrain.io.json;

import java.util.*;
import lombok.*;

import org.luwrain.core.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public final class CommonSettings
{
    String desktopTitle, desktopEscapeCommand, windowTitle, timeZone;
    boolean hintsSounds, hintsText, hintsSoundsSpace, filePopupSkipHidden;
    List<MainMenuItem> mainMenuItems;

    static public CommonSettings createInitial(List<Starter> starters)
    {
	final var s = new CommonSettings();
	s.setHintsSounds(true);
	s.setHintsText(true);
	s.setHintsSoundsSpace(true);
	s.setFilePopupSkipHidden(true);
	s.setDesktopTitle("LUWRAIN");
	s.setWindowTitle("LUWRAIN");
	s.setDesktopEscapeCommand("quit");
	final var mainMenu = new ArrayList<MainMenuItem>();
	for(var st: starters)
	    mainMenu.add(new MainMenuItem(MainMenuItem.TYPE_UNIREF, st.getUri().toString()));
	s.setMainMenuItems(mainMenu);
	return s;
    }
}
