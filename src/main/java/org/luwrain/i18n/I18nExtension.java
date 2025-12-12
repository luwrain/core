// SPDX-License-Identifier: Apache-2.0
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.i18n;

public interface I18nExtension
{
    void addCommandTitle(String lang, String command, String title);
    void addStrings(String lang, String component, Object obj);
    boolean addLang(String name, Lang lang);
}
