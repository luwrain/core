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

/**
 * A comprehensive mechanism for supporting scripts based on GraalVM.
 * Primarily focused on JavaScript, this package offers a wide range of functionalities to integrate scripting capabilities
 * into applications.
 * <p>
 * This package includes various components such as core scripting utilities, hooks for different purposes,
 * and controls for handling user interactions. It is designed to facilitate the development of applications that require
 * dynamic scripting features.
 * <p>
 * The package structure is organized into several sub-packages and classes to provide a modular and extensible framework
 * for script execution and management.</p>
 *
 * <h2>Core Components</h2>
 * <ul>
 *     <li><code>core</code>: Contains fundamental classes and interfaces for the scripting mechanism, including objects
 *     for handling language specifics, system events, and core functionalities.</li>
 *     <li><code>hooks</code>: Includes various hook implementations for different purposes such as providing, transforming,
 *     notifying, and managing permissions within the scripting environment.</li>
 * </ul>
 *
 * <h2>Utility Classes</h2>
 * <ul>
 *     <li><code>AsyncUtils</code>: Provides utilities for asynchronous operations within the scripting context.</li>
 *     <li><code>ScriptUtils</code>: Offers general utilities for script handling and manipulation.</li>
 * </ul>
 *
 * <h2>Control Components</h2>
 * <ul>
 *     <li><code>controls</code>: Contains classes for handling user interface controls such as wizard areas and edit areas,
 *     along with hooks for edit correctors.</li>
 * </ul>
 *
 * <h2>Machine Learning Support</h2>
 * <ul>
 *     <li><code>ml</code>: Includes classes for integrating machine learning capabilities, such as handling JSoup nodes
 *     and documents.</li>
 * </ul>
 *
 * <p>Overall, the <code>org.luwrain.script</code> package is a powerful tool for developers looking to incorporate
 * dynamic scripting features into their applications, leveraging the capabilities of GraalVM and primarily supporting
 * JavaScript.</p>
 */
package org.luwrain.script;
