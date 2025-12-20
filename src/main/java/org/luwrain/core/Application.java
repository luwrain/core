// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

/**
 * A general interface for objects suitable for running in LUWRAIN as
 * an application. Applications in LUWRAIN are objects which provide some
 * interactive features to users.  They offer a set of interactive
 * areas forming a working space.
 * <p>
 * The application instance is created by some client code with only very
 * preliminary initialization. Main initialization happens in 
 * {@code onLaunchApp()} method when a reference to {@link Luwrain} object
 * obtained. This object is used for access to core features and as an
 * identifier of an particular application instance.
 * <p>
 * Each application class may have multiple instances running
 * simultaneously. However, LUWRAIN core can maintain so called mono
 * applications, which support only one instance at any given time (for
 * example, it is pointless to have multiple running instances of a
 * control panel). If the application is required to be a mono
 * application, it must implement {@link MonoApp} interface instead.
 *
 * @see Area Luwrain MonoApp
 * @since 1.0
 */
public interface Application
{
    void onAppClose();
    String getAppName();
    AreaLayout getAreaLayout();
    InitResult onLaunchApp(Luwrain luwrain);
}
