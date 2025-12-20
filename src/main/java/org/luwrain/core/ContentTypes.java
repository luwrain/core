// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

public final class ContentTypes
{
    public enum ExpectedType {
	ANY,
	AUDIO,
	TEXT,
    };

    static public final String UNKNOWN = "content/unknown";
    static public boolean isUnknown(String value)
    {
	NullCheck.notNull(value, "value");
	return value.trim().toLowerCase().equals(UNKNOWN.trim().toLowerCase());
    }

    static private final String DATA_BINARY[] = new String[]{
	"application/octet-stream",
    };
    static public final String DATA_BINARY_DEFAULT = DATA_BINARY[0];

    static private final String[] TEXT_PLAIN = new String[]{
	"text/plain",
    };
    static public final String TEXT_PLAIN_DEFAULT = TEXT_PLAIN[0];

    static private final String[] TEXT_HTML = new String[]{
	"text/html",
    };
    static public final String TEXT_HTML_DEFAULT = "text/html";

        static private final String[] APP_PDF = new String[]{
	"application/pdf",
    };
    static public final String APP_PDF_DEFAULT = "application/pdf";
    static public String[] getAppPdf()
    {
	return APP_PDF.clone();
    }
        static public boolean isAppPdf(String value)
    {
	NullCheck.notEmpty(value, "value");
	final String v = value.trim().toLowerCase();
	for(String s: APP_PDF)
	    if (s.trim().toLowerCase().equals(v))
		return true;
	return false;
    }

    static private final String[] APP_DOC = new String[]{
	"application/msword",
    };
    static public final String APP_DOC_DEFAULT = "application/msword";
    static public String[] getAppDoc()
    {
	return APP_DOC.clone();
    }
    static public boolean isAppDoc(String value)
    {
	NullCheck.notNull(value, "value");
	final String v = value.trim().toLowerCase();
	for(String s: APP_DOC)
	    if (s.trim().toLowerCase().equals(v))
		return true;
	return false;
    }

    static private final String[] APP_DOCX = new String[]{
	"application/vnd.openxmlformats-officedocument.wordprocessingml.document",
    };
    static public final String APP_DOCX_DEFAULT = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    static public String[] getAppDocX()
    {
	return APP_DOCX.clone();
    }
    static public boolean isAppDocX(String value)
    {
	NullCheck.notNull(value, "value");
	final String v = value.trim().toLowerCase();
	for(String s: APP_DOCX)
	    if (s.trim().toLowerCase().equals(v))
		return true;
	return false;
    }

        static private final String[] APP_XLSX = new String[]{
	"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
    };
    static public final String APP_XLSX_DEFAULT = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static public String[] getAppXlsX()
    {
	return APP_XLSX.clone();
    }
    static public boolean isAppXlsX(String value)
    {
	NullCheck.notNull(value, "value");
	final String v = value.trim().toLowerCase();
	for(String s: APP_XLSX)
	    if (s.trim().toLowerCase().equals(v))
		return true;
	return false;
    }


    static public final String[] SOUND_WAVE = new String[]{
	"audio/vnd.wave",
    };
    static public final String SOUND_WAVE_DEFAULT = SOUND_WAVE[0];

    static public final String[] SOUND_MP3 = new String[]{
	"audio/mpeg",
	"audio/MPA",
	"audio/mpa-robust",
    };

    static public final String SOUND_MP3_DEFAULT = SOUND_MP3[0];
}
