
//LWR_API 1.0

package org.luwrain.popups;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.util.*;

public class ListPopup<E> extends ListPopupBase<E>
{
    protected Object result;

    public ListPopup(Luwrain luwrain, ListArea.Params<E> params,
		     Set<Popup.Flags> popupFlags)
    {
	super(luwrain, params, popupFlags);
    }

    @Override public boolean onInputEvent(InputEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.isSpecial() && !event.isModified())
	    switch(event.getSpecial())
	{
case ENTER:
return closing.doOk();
 }
return super.onInputEvent(event);
    }

    @Override public boolean onOk()
    {
	result = selected();
	return result != null;
    }

    @Override public boolean onCancel()
    {
	return true;
    }
}
