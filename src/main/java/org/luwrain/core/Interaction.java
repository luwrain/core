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

public interface Interaction
{
    public static final class Color
    {
    public enum Predefined {WHITE,LIGHT_GRAY,GRAY,DARK_GRAY,BLACK,RED,PINK,ORANGE,YELLOW,GREEN,MAGENTA,CYAN,BLUE};
    Predefined predefined=null;
    float red=0,green=0,blue=0;

    public Predefined getPredefined(){return predefined;}
    public void setPredefined(Predefined predefined){this.predefined=predefined;}

    public float getRed(){return red;}
    public void setRed(float red){this.red=red;}
    public float getGreen(){return green;}
    public void setGreen(float green){this.green=green;}
    public float getBlue(){return blue;}
    public void setBlue(float blue){this.blue=blue;}

    public InteractionParamColor(float red,float green,float blue)
    {
	this.red=red;
	this.green=green;
	this.blue=blue;
	predefined=null;
    }

    public InteractionParamColor(Predefined predefined)
    {
	this.predefined=predefined;
    }
}

    
public class InteractionParams
{
    public int wndLeft = 0;
    public int wndTop = 0;
    public int wndWidth = -1;//-1 means screen with;
    public int wndHeight = -1;//-1 means screen height;
    public int marginLeft = 16;
    public int marginTop = 16;
    public int marginRight = 16;
    public int marginBottom = 16;
    public InteractionParamColor fontColor = new InteractionParamColor(InteractionParamColor.Predefined.GRAY);
    public InteractionParamColor font2Color = new InteractionParamColor(InteractionParamColor.Predefined.WHITE);
    public InteractionParamColor bkgColor = new InteractionParamColor(InteractionParamColor.Predefined.BLACK);
    public InteractionParamColor splitterColor = new InteractionParamColor(InteractionParamColor.Predefined.LIGHT_GRAY);
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

    boolean init(InteractionParams params,OperatingSystem os);
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
