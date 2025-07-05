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

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import org.luwrain.util.*;

public class ConfigsTest
{
    private TempDir tempDir = null;

    @Test void withClosing()
    {
	try (final var c = new Configs(tempDir.getFile())) {
	    TestClass testClass = new TestClass();
	    testClass.testStr = "testValue";
	    c.save(testClass);
	}
	try (final var c = new Configs(tempDir.getFile())) {
	    final TestClass testClass = c.load(TestClass.class);
	    assertNotNull(testClass);
	}
    }

    @Test void withoutClosing()
    {
	try (final var c = new Configs(tempDir.getFile())) {
	    TestClass testClass = new TestClass();
	    testClass.testStr = "testValue";
	    c.save(testClass);
	    testClass = c.load(TestClass.class);
	    assertNotNull(testClass);
	}
    }

    @BeforeEach void createTempDir()
    {
	tempDir = new TempDir();
    }

    @AfterEach void closeTempDir()
    {
	tempDir.close();
	tempDir = null;
    }

    static private final class TestClass
    {
	String testStr;
    }
}
