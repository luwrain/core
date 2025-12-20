// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.cpanel;

public final class StandardElements
{
    static public final Element
	ROOT = new SimpleElement(null, SimpleElement.class.getName() + ":ROOT"),
	APPLICATIONS = new SimpleElement(ROOT, SimpleElement.class.getName() + ":APPLICATIONS"),
	INPUT_OUTPUT = new SimpleElement(ROOT, SimpleElement.class.getName() + ":InputOutput"),
	KEYBOARD = new SimpleElement(INPUT_OUTPUT, SimpleElement.class.getName() + ":KEYBOARD"),
	SOUND = new SimpleElement(INPUT_OUTPUT, SimpleElement.class.getName() + ":SOUNDS"),
	BRAILLE = new SimpleElement(INPUT_OUTPUT, SimpleElement.class.getName() + ":BRAILLE"),
	SPEECH = new SimpleElement(INPUT_OUTPUT, SimpleElement.class.getName() + ":SPEECH"),
	NETWORK = new SimpleElement(ROOT, SimpleElement.class.getName() + ":NETWORD"),
	HARDWARE = new SimpleElement(ROOT, SimpleElement.class.getName() + ":HARDWARE"),
	UI = new SimpleElement(ROOT, SimpleElement.class.getName() + ":UI"),
	EXTENSIONS = new SimpleElement(ROOT, SimpleElement.class.getName() + ":EXTENSIONS"),
	WORKERS = new SimpleElement(ROOT, SimpleElement.class.getName() + ":WORKERS");
}
