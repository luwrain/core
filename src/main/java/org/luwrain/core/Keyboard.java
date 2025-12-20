// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import org.luwrain.core.events.*;

class Keyboard
{
    static InputEvent translate(InputEvent event)
    {
	NullCheck.notNull(event, "event");
	InputEvent e = event;
e = translateControl(e);
e = translateAlternative(e);
return e;
    }

    static InputEvent translateAlternative(InputEvent event)
    {
	NullCheck.notNull(event, "event");
	if (!event.isSpecial() || !event.withControlOnly())
	    return event;
	switch (event.getSpecial())
	{
	case ARROW_UP:
	    return new InputEvent(true, InputEvent.Special.ALTERNATIVE_ARROW_UP, ' ');
	case ARROW_DOWN:
	    return new InputEvent(true, InputEvent.Special.ALTERNATIVE_ARROW_DOWN, ' ');
	case ARROW_LEFT:
	    return new InputEvent(true, InputEvent.Special.ALTERNATIVE_ARROW_LEFT, ' ');
	case ARROW_RIGHT:
	    return new InputEvent(true, InputEvent.Special.ALTERNATIVE_ARROW_RIGHT, ' ');
	case PAGE_DOWN:
	    return new InputEvent(true, InputEvent.Special.ALTERNATIVE_PAGE_DOWN, ' ');
	case PAGE_UP:
	    return new InputEvent(true, InputEvent.Special.ALTERNATIVE_PAGE_UP, ' ');
	case HOME:
	    return new InputEvent(true, InputEvent.Special.ALTERNATIVE_HOME, ' ');
	case END:
	    return new InputEvent(true, InputEvent.Special.ALTERNATIVE_END, ' ');
	case DELETE:
	    return new InputEvent(true, InputEvent.Special.ALTERNATIVE_DELETE, ' ');
	default:
	    return event;
	}
    }

	static private InputEvent translateControl(InputEvent event)
	{
	    if (event.isSpecial())
		return event;
	    return new InputEvent(translateControlChar(event.getChar()), event.withShift(), event.withControl(), event.withAlt());
	}

    static private char translateControlChar(char c)
    {
	switch(c)
	{
	case '':
	    return 'a';
	case '':
	    return 'b';
	case '':
	    return 'c';
	case '':
	    return 'd';
	case '':
	    return 'e';
	case '':
	    return 'f';
	case '':
	    return 'g';
	    //FIXME:h
	    //FIXME:i
	case '':
	    return 'k';
	case '':
	    return 'l';
	    //FIXME:m
	case '':
	    return 'n';
	case '':
	    return 'o';
	case '':
	    return 'p';
	case '':
	    return 'q';
	case '':
	    return 'r';
	case '':
	    return 's';
	case '':
	    return 't';
	case '':
	    return 'u';
	case '':
	    return 'v';
	case '':
	    return 'w';
	case '':
	    return 'x';
	case '':
	    return 'y';
	case '':
	    return 'z';
	default:
	    return c;
	}
    }
    }
