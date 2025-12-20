// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.i18n;

import java.util.*;

public interface I18n
{
    String staticStr(LangStatic id);
    String getStaticStr(String id);
    String hasSpecialNameOfChar(char ch);
    String getCommandTitle(String command);
    Object getStrings(String component);
    <E> E getStrings(Class<E> stringsClass);
    String getPastTimeBrief(Date date);
    String getNumberStr(int count, String entities);
    String getExceptionDescr(Exception e);
    Lang getActiveLang();
    Lang getLang(String langName);
    Map<String, Lang> getAllLangs();
}
