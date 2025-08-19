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

package org.luwrain.core.events.resp;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.i18n.*;
import org.luwrain.io.json.*;

import static java.util.Objects.*;

final class  Base
{
    //May return null
    static String getSuggestionText(Suggestions suggestion, I18n i18n)
    {
	requireNonNull(suggestion, "suggestion");
	requireNonNull(i18n, "i18n can't be null");
	switch(suggestion)
	{
	case CLICKABLE_LIST_ITEM:
	    return i18n.getStaticStr("SuggestionClickableListItem");
	case LIST_ITEM:
	    return i18n.getStaticStr("SuggestionListItem");
	case POPUP_LIST_ITEM:
	    return i18n.getStaticStr("SuggestionPopupListItem");
	default:
	    return null;
	}
    }

    static String getTextForHint(Luwrain luwrain, org.luwrain.core.Hint hint)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(hint, "hint");
	switch(hint)
	{
	case SPACES:
	    return luwrain.i18n().getStaticStr ("Spaces");
	case TREE_BRANCH_EXPANDED:
	    return "Раскрыто";//FIXME:
	case TREE_BRANCH_COLLAPSED:
	    return "свёрнуто";
	default:
	    return luwrain.i18n().staticStr(hintToStaticStrMap(hint));
	}
    }

    static LangStatic hintToStaticStrMap(org.luwrain.core.Hint hint)
    {
	switch (hint)
	{
	case SPACE:
	    return LangStatic.SPACE;
	case TAB:
	    return LangStatic.TAB;
	case EMPTY_LINE:
	    return LangStatic.EMPTY_LINE;
	case NO_CONTENT:
	    return LangStatic.NO_CONTENT;
	case BEGIN_OF_LINE:
	    return LangStatic.BEGIN_OF_LINE;
	case END_OF_LINE:
	case LINE_BOUND:
	    return LangStatic.END_OF_LINE;
	case BEGIN_OF_TEXT:
	    return LangStatic.BEGIN_OF_TEXT;
	case END_OF_TEXT:
	    return LangStatic.END_OF_TEXT;
	case NO_LINES_ABOVE:
	    return LangStatic.NO_LINES_ABOVE;
	case NO_LINES_BELOW:
	    return LangStatic.NO_LINES_BELOW;
	case NO_ITEMS_ABOVE:
	    return LangStatic.NO_ITEMS_ABOVE;
	case NO_ITEMS_BELOW:
	    return LangStatic.NO_ITEMS_BELOW;
	case TREE_BEGIN:
	    return LangStatic.BEGIN_OF_TREE;
	case TREE_END:
	    return LangStatic.END_OF_TREE;
	case TABLE_NO_ROWS_ABOVE:
	    return LangStatic.TABLE_NO_ROWS_ABOVE;
	case TABLE_NO_ROWS_BELOW:
	    return LangStatic.TABLE_NO_ROWS_BELOW;
	case TABLE_END_OF_COL:
	    return LangStatic.TABLE_END_OF_COL;
	case TABLE_BEGIN_OF_ROW:
	    return LangStatic.TABLE_BEGIN_OF_ROW;
	case TABLE_END_OF_ROW:
	    return LangStatic.TABLE_END_OF_ROW;
	default:
	    return null;
	}
    }

    static Sounds getSoundForHint(org.luwrain.core.Hint hint)
    {
	NullCheck.notNull(hint, "hint");
	switch (hint)
	{
	case NO_ITEMS_ABOVE:
	case TREE_BEGIN:
	case TABLE_NO_ROWS_ABOVE:
	    return Sounds.NO_ITEMS_ABOVE;
	case NO_ITEMS_BELOW:
	case TREE_END:
	case TABLE_NO_ROWS_BELOW:
	    return Sounds.NO_ITEMS_BELOW;
	case NO_LINES_ABOVE:
	    return Sounds.NO_LINES_ABOVE;
	case BEGIN_OF_LINE:
	case BEGIN_OF_TEXT:
	case END_OF_TEXT:
	case END_OF_LINE:
	    return Sounds.END_OF_LINE;
	case NO_LINES_BELOW:
	    return Sounds.NO_LINES_BELOW;
	case NO_CONTENT:
	    return Sounds.NO_CONTENT;
	case LINE_BOUND:
	case EMPTY_LINE:
	case SPACES:
	    return Sounds.EMPTY_LINE;
	case TREE_BRANCH_COLLAPSED:
	    return Sounds.COLLAPSED;
	case TREE_BRANCH_EXPANDED:
	    return Sounds.EXPANDED;
	default:
	    return null;
	}
    }
}
