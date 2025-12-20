// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

public interface Extension
{
    String init(Luwrain luwrain);
    Command[] getCommands(Luwrain luwrain);
    ExtensionObject[] getExtObjects(Luwrain luwrain);
    void i18nExtension(Luwrain luwrain, org.luwrain.i18n.I18nExtension i18nExt);
    org.luwrain.cpanel.Factory[] getControlPanelFactories(Luwrain luwrain);
    UniRefProc[] getUniRefProcs(Luwrain luwrain);
    void close();
}
