// SPDX-License-Identifier: Apache-2.0
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.settings;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.cpanel.*;

import static java.util.Objects.*;

public final class StandardFactory implements Factory
{
    static private final String ELEMENT_PREFIX = StandardFactory.class.getName() + ".";
    static private final Element
	personalInfo = new SimpleElement(StandardElements.ROOT, ELEMENT_PREFIX + "PersonalInfo"),
	uiGeneral = new SimpleElement(StandardElements.UI, ELEMENT_PREFIX + "UIGeneral"),
	hotKeys = new SimpleElement(StandardElements.UI, ELEMENT_PREFIX + "HotKeys"),
	fileTypes = new SimpleElement(StandardElements.UI, ELEMENT_PREFIX + "FileTypes"),
	mainMenu = new SimpleElement(StandardElements.UI, ELEMENT_PREFIX + "MainMenu"),
	hardwareCpuMem = new SimpleElement(StandardElements.HARDWARE, ELEMENT_PREFIX + "HardwareCpuMem"),
	hardwareSysDevices = new SimpleElement(StandardElements.HARDWARE, ELEMENT_PREFIX + "HardwareSysDevices"),
	version = new SimpleElement(StandardElements.ROOT, ELEMENT_PREFIX + "Version"),
	dateTime = new SimpleElement(StandardElements.ROOT, ELEMENT_PREFIX + "DateTime"),
	speechGeneral = new SimpleElement(StandardElements.SPEECH, ELEMENT_PREFIX + "SpeechCurrent"),
	soundsList = new SimpleElement(StandardElements.UI, ELEMENT_PREFIX + "SoundsList"),
	soundSchemes = new SimpleElement(StandardElements.UI, ELEMENT_PREFIX + "SoundSchemes");

    private final Luwrain luwrain;

    public StandardFactory(Luwrain luwrain)
    {
	this.luwrain = requireNonNull(luwrain, "luwrain can't be null");
    }

    @Override public Element[] getElements()
    {
	return new Element[]{
	    	    	    version,
	    StandardElements.ROOT,
	    personalInfo,
	    StandardElements.APPLICATIONS,
	    StandardElements.UI,
	    uiGeneral, 
	    hotKeys,
	    mainMenu,
			    //soundSchemes,
	    soundsList,
	    fileTypes,
	    StandardElements.INPUT_OUTPUT,
	    StandardElements.SPEECH,
	    //	    StandardElements.BRAILLE,
	    StandardElements.SOUND,
	    StandardElements.KEYBOARD,
			    	    StandardElements.HARDWARE,
	    hardwareCpuMem,
	    hardwareSysDevices,
	    StandardElements.NETWORK,
	    dateTime,
	    StandardElements.EXTENSIONS,
	    StandardElements.WORKERS,
			    speechGeneral,
	};
    }

    @Override public Element[] getOnDemandElements(Element parent)
    {
	requireNonNull(parent, "parent can't be null");
	return new Element[0];
    }

    @Override public org.luwrain.cpanel.Section createSection(Element el)
    {
	requireNonNull(el, "el can't be null");
	if (el.equals(hardwareSysDevices))
	    return new SimpleSection(hardwareSysDevices, luwrain.getString("STATIC:CpSysDevices"));
	if (el.equals(StandardElements.ROOT))
	    return new SimpleSection(StandardElements.ROOT, luwrain.getString("STATIC:CpTreeRoot"));
	if (el.equals(StandardElements.APPLICATIONS))
	    return new SimpleSection(StandardElements.APPLICATIONS, luwrain.getString("STATIC:CpApplications"));
	if (el.equals(dateTime))
	    return new SimpleSection(dateTime, luwrain.getString("STATIC:CpDateTime"), (controlPanel)->{return new DateTime(controlPanel);});
	if (el.equals(StandardElements.INPUT_OUTPUT))
	    return new SimpleSection(StandardElements.INPUT_OUTPUT, luwrain.getString("STATIC:CpInputOutput"));
	if (el.equals(StandardElements.KEYBOARD))
	    return new SimpleSection(StandardElements.KEYBOARD, luwrain.getString("STATIC:CpKeyboard"));
	if (el.equals(StandardElements.SOUND))
	    return new SimpleSection(StandardElements.SOUND, luwrain.getString("STATIC:CpSound"));
	if (el.equals(StandardElements.BRAILLE))
	    return new SimpleSection(StandardElements.BRAILLE, "Браиль", (controlPanel)->Braille.create(controlPanel));
	if (el.equals(StandardElements.SPEECH))
	    return new SimpleSection(StandardElements.SPEECH, luwrain.getString("STATIC:CpSpeech"));
	if (el.equals(StandardElements.NETWORK))
	    return new SimpleSection(StandardElements.NETWORK, luwrain.getString("STATIC:CpNetwork"));
	if (el.equals(StandardElements.HARDWARE))
	    return new SimpleSection(StandardElements.HARDWARE, luwrain.getString("STATIC:CpHardware"));
	if (el.equals(StandardElements.UI))
	    return new SimpleSection(StandardElements.UI, luwrain.getString("STATIC:CpUserInterface"));
	if (el.equals(StandardElements.EXTENSIONS))
	    return new SimpleSection(StandardElements.EXTENSIONS, luwrain.getString("STATIC:CpExtensions"));
	if (el.equals(StandardElements.WORKERS))
	    return new SimpleSection(StandardElements.WORKERS, luwrain.getString("STATIC:CpBackgroundWorkers"));
	if (el.equals(uiGeneral))
	    return new SimpleSection(uiGeneral, luwrain.getString("STATIC:CpUiGeneral"), (controlPanel)->UserInterface.create(controlPanel));
	if (el.equals(hotKeys))
	    return new SimpleSection(hotKeys, luwrain.getString("STATIC:CpHotKeys"), (controlPanel)->HotKeys.create(controlPanel));
	if (el.equals(personalInfo))
	    return new SimpleSection(personalInfo, luwrain.getString("STATIC:CpPersonalInfoSection"), (controlPanel)->{return new PersonalInfo(controlPanel);});
	if (el.equals(fileTypes))
	    return new SimpleSection(fileTypes, luwrain.getString("STATIC:CpFileTypes"), (controlPanel)->FileTypes.create(controlPanel));
	if (el.equals(hardwareCpuMem))
	    return new SimpleSection(hardwareCpuMem, luwrain.getString("STATIC:CpCpuAndMem"), (controlPanel)->HardwareCpuMem.create(controlPanel));
	if (el.equals(version))
	    return new SimpleSection(version, luwrain.getString("STATIC:CpVersion"), (controlPanel)->Version.create(controlPanel));
	if (el.equals(speechGeneral))
	    return new SimpleSection(speechGeneral, luwrain.getString("STATIC:CpSpeechGeneral"), (controlPanel)->Speech.create(controlPanel));
	if (el.equals(soundsList))
	    return new SimpleSection(soundsList, luwrain.getString("STATIC:CpSoundsList"), (controlPanel)->SoundsList.create(controlPanel));
	if (el.equals(mainMenu))
	    return new SimpleSection(mainMenu, luwrain.getString("STATIC:CpMainMenu"), (controlPanel)->MainMenu.create(controlPanel));
	if (el.equals(soundSchemes))
	    return new SimpleSection(soundSchemes, "Звуковые схемы", (controlPanel)->SoundSchemes.create(controlPanel));
	return null;
    }
}
