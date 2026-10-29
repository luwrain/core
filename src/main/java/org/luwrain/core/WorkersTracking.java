// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import java.util.*;
import java.util.concurrent.*;
import org.apache.logging.log4j.*;

import static java.util.Objects.*;

final class WorkersTracking 
{
    static private final Logger log = LogManager.getLogger();

        private final Map<String, Entry> workers = new HashMap<>();
    private volatile boolean continueWork = true;

    boolean runExplicitly(String workerName)
    {
	requireNonNull(workerName, "workerName can't be null");
	if (workerName.isBlank())
	    throw new IllegalArgumentException("workerName can't be empty");
	if (!workers.containsKey(workerName))
	    return false;
	final Entry e = workers.get(workerName);
	synchronized (e) {
	    if (e.task != null && !e.task.isDone())
	    {
			    log.trace("Rejecting explicit run of the worker {} since it's already running", workerName);
		return false;
	    }
	    e.task = new FutureTask<>(e.worker, null);
	    e.task.run();
	}
	return true;
    }

    void doWork(Worker[] workersList)
    {
	requireNonNull(workersList, "workersList can't be null");
	workers.clear();
	for(Worker w: workersList)
	{
	    final String name = w.getExtObjName();
	    if (workers.containsKey(name))
	    {
		log.warn("Trying to register workers with the same name {}, only the first one is accepted", name);
		continue;
	    }
	    log.trace("Adding the worker {}, firstLaunchDelay={}, launchPeriod={}", name, w.getFirstLaunchDelay(), w.getLaunchPeriod());
	    workers.put(name, new Entry(name, w));
	}
	new Thread(()->{
		int counter = 0;
		while(continueWork)
		{
		    for(Map.Entry<String, Entry> entry: workers.entrySet())
		    {
			final Entry e = entry.getValue();
			synchronized(e) {
			    if (e.task != null && !e.task.isDone())
				continue;
			    final int
			    delay = e.worker.getFirstLaunchDelay(),
period =     e.worker.getLaunchPeriod();
			    if (delay < 0 || period <= 0)
				continue;
			    if (counter >= delay && (counter - delay) % period == 0)
			    {
				e.task = new FutureTask<>(e.worker, null);
				e.task.run();
			    }
			}
		    } //for(entries);
		    try {
			Thread.sleep(1000);
		    } catch (InterruptedException ie)
		    {
			Thread.currentThread().interrupt();
			return;
		    }
		    ++counter;
		}
		log.trace("Finishing workers");
	}, "luwrain.workers").start();
    }

    void finish()
    {
	continueWork = false;
    }

        static private final class Entry 
    {
final String name;
final Worker worker;
	FutureTask task = null;

Entry(String name, Worker worker)
	{
	    this.name = requireNonNull(name, "name can't be null");
	    this.worker = requireNonNull(worker, "worker can't be null");
	}
    }
}
