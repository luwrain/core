// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import java.util.function.*;

public class SimpleObjFactory implements ObjFactory
{
    protected final String extName;
    protected final String className;
protected final Supplier<Object> func;

    public SimpleObjFactory(String extName, String className, Supplier<Object> func)
    {
	NullCheck.notEmpty(extName, "extName");
	NullCheck.notNull(className, "className");
NullCheck.notNull(func, "func");
	this.extName = extName;
this.className = className;
	this.func = func;
    }

        @Override public String getExtObjName()
    {
	return extName;
    }

@Override public Object newObject(String name)
{
if (!className.equals(name))
return null;
return func.get();
}
}
