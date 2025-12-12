// SPDX-License-Identifier: Apache-2.0
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.cpanel;

public interface Factory
{
    Element[] getElements();
    Element[] getOnDemandElements(Element parent);
    Section createSection(Element el);
}
