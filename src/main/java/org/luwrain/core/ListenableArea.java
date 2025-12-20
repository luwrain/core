// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

public interface ListenableArea
{
    ListeningInfo onListeningStart();
    void onListeningFinish(ListeningInfo listeningInfo);

    static public class ListeningInfo
    {
	protected final String text;
	protected final int posX;
	protected final int posY;
	public ListeningInfo(String text, int posX, int posY)
	{
	    NullCheck.notNull(text, "text");
	    this.text = text;
	    this.posX = posX;
	    this.posY = posY;
	}
	public ListeningInfo(String text)
	{
	    this(text, -1, -1);
	}
	public ListeningInfo()
	{
	    this("", -1, -1);
	}
	public final String getText()
	{
	    return this.text;
	}
	public final int getPosX()
	{
	    return this.posX;
	}
	public final int getPosY()
	{
	    return this.posY;
	}
	public final boolean noMore()
	{
	    return text.isEmpty();
	}
	@Override public String toString()
	{
	    return this.text;
	}
    }
}
