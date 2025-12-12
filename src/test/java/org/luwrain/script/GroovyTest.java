
package org.luwrain.script;

import java.util.*;
import java.util.concurrent.atomic.*;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import groovy.lang.*;
import groovy.util.Eval;

public class GroovyTest
{
    @Test public void simple()
    {
	final var res = Eval.me("2");
	assertNotNull(res);
	assertTrue(res instanceof Integer);
	assertEquals(2, ((Integer)res).intValue());
    }

    @Test public void arg()
    {
	final var res = Eval.me("arg", "value", "arg");
	assertNotNull(res);
	assertTrue(res instanceof String);
	assertEquals("value", res.toString());
    }

    @Test public void closure()
    {
	var res = Eval.me("{it -> 'proba' }");
	assertNotNull(res);
	assertTrue(res instanceof Closure);
	final Closure c = (Closure)res;
	res = c.call();
	assertTrue(res instanceof String);
	assertEquals("proba", res.toString());
    }

    @Test public void main()
    {
	var res = Eval.me("{it -> testFunc 'Tomsk'  }");
	assertNotNull(res);
	assertTrue(res instanceof Closure);
	final Closure c = (Closure)res;
	final var d = new TestDelegate();
	c.setDelegate(d);
	res = c.call();
	assertTrue(res instanceof String);
	assertEquals("London", res.toString());
	assertNotNull(d.arg);
	assertEquals("Tomsk", d.arg);
    }

        @Test public void binding()
    {
	final var res = new AtomicReference<String>();
	final var func = new Object(){
		public void call(String arg)
		{
		    res.set(arg);
		}
	    };
Eval.me("func", func, "func 'proba'");
assertNotNull(res.get());
assertEquals("proba", res.get());
    }

    static public final class TestDelegate
    {
	String arg = null;
	public String testFunc(String arg)
	{
	    this.arg = arg;
	    return "London";
	}
    }
}
