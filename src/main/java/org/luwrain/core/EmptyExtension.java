// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

public class EmptyExtension implements Extension
{
    @Override public String init(Luwrain luwrain)
    {
	return null;
    }

    @Override public Command[] getCommands(Luwrain luwrain)
    {
	return new Command[0];
    }

    @Override public ExtensionObject[] getExtObjects(Luwrain luwrain)
    {
	return new ExtensionObject[0];
    }

    @Override public void i18nExtension(Luwrain luwrain, org.luwrain.i18n.I18nExtension i18nExt)
    {
    }

    @Override public org.luwrain.cpanel.Factory[] getControlPanelFactories(Luwrain luwrain)
    {
	return new org.luwrain.cpanel.Factory[0];
    } 

    @Override public UniRefProc[] getUniRefProcs(Luwrain luwrain)
    {
	return new UniRefProc[0];
    }

    @Override public void close()
    {
    }
}
