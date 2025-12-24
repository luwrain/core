// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.app.calc;

import java.util.*;
import java.util.function.*;
import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.app.base.*;
import org.luwrain.script.core.*;
import static org.luwrain.script.ScriptUtils.*;

public final class App extends AppBase<Strings>
{
    static final String
	LOG_COMPONENT = "calc";

    private org.luwrain.script.core.Module module = null;
    private MainLayout mainLayout = null;

    public App()
    {
	super(Strings.NAME, Strings.class, "luwrain.calc");
    }

    @Override public AreaLayout onAppInit()
    {
	this.module = new org.luwrain.script.core.Module(getLuwrain(), (bindings, syncObj)->{
		bindings.putMember("pi", Math.PI);
		bindings.putMember("abs", (ProxyExecutable)(args)->singleArg(args, (x)->Math.abs(x.doubleValue())));
		bindings.putMember("sqrt", (ProxyExecutable)(args)->singleArg(args, (x)->Math.sqrt(x.doubleValue())));
		bindings.putMember("exp", (ProxyExecutable)(args)->singleArg(args, (x)->Math.exp(x.doubleValue())));
		bindings.putMember("log", (ProxyExecutable)(args)->singleArg(args, (x)->Math.log(x.doubleValue())));
		bindings.putMember("log10", (ProxyExecutable)(args)->singleArg(args, (x)->Math.log10(x.doubleValue())));
		bindings.putMember("floor", (ProxyExecutable)(args)->singleArg(args, (x)->Math.floor(x.doubleValue())));
		//bindings.putMember("round", (ProxyExecutable)(args)->singleArg(args, (x)->Math.round(x.doubleValue())));
		//bindings.putMember("rand", (ProxyExecutable)(args)->singleArg(args, (x)->Math.random(x.doubleValue())));
		bindings.putMember("sin", (ProxyExecutable)(args)->singleArg(args, (x)->Math.sin(x.doubleValue())));
		bindings.putMember("cos", (ProxyExecutable)(args)->singleArg(args, (x)->Math.cos(x.doubleValue())));
		bindings.putMember("tan", (ProxyExecutable)(args)->singleArg(args, (x)->Math.tan(x.doubleValue())));
		bindings.putMember("toDegrees", (ProxyExecutable)(args)->singleArg(args, (x)->Math.toDegrees(x.doubleValue())));
		bindings.putMember("toRadians", (ProxyExecutable)(args)->singleArg(args, (x)->Math.toRadians(x.doubleValue())));
		bindings.putMember("asin", (ProxyExecutable)(args)->singleArg(args, (x)->Math.asin(x.doubleValue())));
		bindings.putMember("acos", (ProxyExecutable)(args)->singleArg(args, (x)->Math.acos(x.doubleValue())));
		bindings.putMember("atan", (ProxyExecutable)(args)->singleArg(args, (x)->Math.atan(x.doubleValue())));
		bindings.putMember("cbrt", (ProxyExecutable)(args)->singleArg(args, (x)->Math.cbrt(x.doubleValue())));
		bindings.putMember("sinh", (ProxyExecutable)(args)->singleArg(args, (x)->Math.sinh(x.doubleValue())));
		bindings.putMember("cosh", (ProxyExecutable)(args)->singleArg(args, (x)->Math.cosh(x.doubleValue())));
	    });
	this.mainLayout = new MainLayout(this);
	setAppName(getStrings().appName());
	return mainLayout.getAreaLayout();
    }

    @Override public boolean onEscape()
    {
	closeApp();
	return true;
    }

    Number calculate(String[] expr) throws Exception
    {
	final StringBuilder text = new StringBuilder();
	for(String s: expr)
	{
	    final String str = s.replaceAll("//", "#");
	    final int pos = str.indexOf("#");
	    if (pos < 0)
		text.append(str + " "); else
		text.append(str.substring(0, pos) + " ");
	}
	return asNumber(module.eval(/*prescript +*/ new String(text)));
    }

    @Override public void closeApp()
    {
	this.module.close();
	super.closeApp();
    }

    static Double singleArg(Value[] args, Function<Double, Double> f)
    {
	if (!notNullAndLen(args, 1))
	    throw new IllegalArgumentException("Required one not null arg");
	if (!args[0].isNumber())
	    throw new IllegalArgumentException("The first argument must be a number");
	return f.apply(args[0].asDouble());
    }
}
