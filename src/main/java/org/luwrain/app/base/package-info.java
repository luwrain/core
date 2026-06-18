// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

/**
 * Provides a template for the creation of a typical app for
 * LUWRAIN. We highly encourage you to utilize the classes of this
 * package since in addition to your convenience they help to create an
 * easily recognizable app covering the most important parts of our
 * experience automatically. Nevertheless, there are no things you wouldn't be able to
 * do without the classes of this package.
 *
 * <h2>Key Classes</h2>
 * <ul>
 *   <li>{@link org.luwrain.app.base.AppBase AppBase} — the abstract base class
 *       for your application's main class. It handles the application lifecycle,
 *       input and system events, background task management, and i18n strings
 *       retrieval.</li>
 *   <li>{@link org.luwrain.app.base.LayoutBase LayoutBase} — the base class for
 *       every layout of areas your application consists of. It provides area
 *       wrapping (for exception isolation), action management, control context
 *       handling, and factory methods for common area parameters.</li>
 *   <li>{@link org.luwrain.app.base.TaskCancelling TaskCancelling} — the
 *       superclass of {@code AppBase} that provides a thread-safe mechanism for
 *       managing cancellable background tasks with race-condition protection.</li>
 * </ul>
 *
 * <h2>Typical Usage</h2>
 * <pre>{@code
 * public class MyApp extends AppBase<MyStrings> {
 *     public MyApp() {
 *         super(MyStrings.class);
 *     }
 *
 *     protected AreaLayout onAppInit() {
 *         setAppName("My Application");
 *         return new MainLayout(this).getAreaLayout();
 *     }
 * }
 *
 * public class MainLayout extends LayoutBase {
 *     MainLayout(AppBase app) {
 *         super(app);
 *         // create areas, set area layout
 *     }
 * }
 * }</pre>
 *
 * <h2>Design Notes</h2>
 * <p>
 * The central design pattern of this package is <em>area wrapping</em>: every
 * area added to a layout via {@link LayoutBase#getWrappingArea(Area) getWrappingArea()}
 * is wrapped in a protective shell that catches all exceptions thrown by the
 * area's methods and reports them to the LUWRAIN core as crashes, preventing the
 * entire system from failing due to a single misbehaving application.
 * </p>
 * <p>
 * Background task management is handled cooperatively by {@code AppBase} (which
 * holds the {@link java.util.concurrent.FutureTask}) and {@code TaskCancelling}
 * (which provides unique task IDs to prevent stale completions from affecting the
 * current task).
 * </p>
 *
 * @see org.luwrain.core.Application
 * @see org.luwrain.core.Area
 * @see org.luwrain.core.AreaLayout
 */
package org.luwrain.app.base;
