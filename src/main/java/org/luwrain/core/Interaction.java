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

package org.luwrain.core;

import static java.util.Objects.*;

public interface Interaction
{
    public static final class Color
    {
	public enum Predefined {WHITE,LIGHT_GRAY,GRAY,DARK_GRAY,BLACK,RED,PINK,ORANGE,YELLOW,GREEN,MAGENTA,CYAN,BLUE};
	public Predefined predefined=null;
	public float red = 0, green = 0, blue = 0;

	public Color(float red,float green,float blue)
	{
	    this.red = red;
	    this.green = green;
	    this.blue = blue;
	    predefined = null;
	}

	public Color(Predefined predefined)
	{
	    this.predefined = requireNonNull(predefined, "predefined can't be null");
	}
    }

    public static final class Params
    {
	public int wndLeft = 0, wndTop = 0, wndWidth = -1 /* -1 means screen width*/, wndHeight = -1 /* -1 means screen height */;
	public int marginLeft = 16, marginTop = 16, marginRight = 16, marginBottom = 16;
	public Color fontColor = new Color(Color.Predefined.GRAY), font2Color = new Color(Color.Predefined.WHITE);
	public Color bkgColor = new Color(Color.Predefined.BLACK), splitterColor = new Color(Color.Predefined.LIGHT_GRAY);
	public int initialFontSize = 14;
	public String fontName = "Monospaced";
    }

    public interface GraphicalModeControl
    {
	void close();
    }

public interface GraphicalMode
{
    Object getGraphicalObj(GraphicalModeControl control);
}

    boolean init(Params params,OperatingSystem os);
    void close();
    void startInputEventsAccepting(EventConsumer eventConsumer);
    void stopInputEventsAccepting();
    boolean setDesirableFontSize(int size);
    int getFontSize();
    int getWidthInCharacters();
    int getHeightInCharacters();
    void startDrawSession();
    void clearRect(int left, int top, int right, int bottom);
    void drawText(int x, int y, String text);
	void drawText(int x,int y,String text,boolean font2);
    void endDrawSession();
    void setHotPoint(int x, int y);
    void drawVerticalLine(int top, int bottom, int x);
    void drawHorizontalLine(int left, int right, int y);
    void showGraphical(GraphicalMode graphicalMode);
}
