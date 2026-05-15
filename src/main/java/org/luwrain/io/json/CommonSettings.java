// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

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
