// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.controls;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.util.*;

import static java.util.Objects.*;

/**
 * A generic list area that displays a model of items and allows navigation,
 * selection, clipboard operations, and custom appearance. The list area
 * integrates with the LUWRAIN area management, handling input events,
 * system events, and area queries.
 *
 * <p>
 * The class is parameterized by the type of item {@code <E>}. It uses a
 * {@link Model} to provide the data, an {@link Appearance} to control how
 * items are displayed and announced, a {@link Transition} to define movement
 * behaviour, and a {@link ClipboardSaver} for clipboard operations.
 * </p>
 *
 * <p>
 * The list can optionally have an empty line at the top and/or bottom, and
 * can announce the selected item when the area is introduced.
 * </p>
 *
 * @param <E> the type of items in the list
 */
public class ListArea<E>  implements Area, ClipboardTranslator.Provider, RegionTextQueryTranslator.Provider
{
    /**
     * Flags that control the behaviour of the list area.
     */
    public enum Flags {
	/** An empty line is shown at the top of the list. */
	EMPTY_LINE_TOP,
	/** An empty line is shown at the bottom of the list. */
	EMPTY_LINE_BOTTOM,
	/** When the area is introduced, the selected item is announced instead of the area name. */
	AREA_ANNOUNCE_SELECTED
    };

    static protected final Set<Appearance.Flags> NONE_APPEARANCE_FLAGS = EnumSet.noneOf(Appearance.Flags.class);
    static protected final Set<Appearance.Flags> BRIEF_ANNOUNCEMENT_ONLY = EnumSet.of(Appearance.Flags.BRIEF);

    /**
     * Provides the data for the list. The model is responsible for the number
     * of items, retrieving an item by index, and notifying the list when the
     * underlying data has changed.
     *
     * @param <E> the type of items in the list
     */
    public interface Model<E>
    {
	/**
	 * Returns the total number of items in the model.
	 *
	 * @return the item count
	 */
	int getItemCount();

	/**
	 * Returns the item at the given index.
	 *
	 * @param index the zero-based index of the item
	 * @return the item at that position, or {@code null} if the index is out of bounds
	 */
	E getItem(int index);

	/**
	 * Called when the underlying data has changed and the list needs to
	 * be refreshed. Implementations should reload or update their data.
	 */
	void refresh();
    }

    /**
     * Controls the visual and audible presentation of items. It provides the
     * screen text, observable boundaries, and speech announcement for each
     * item.
     *
     * @param <E> the type of items in the list
     */
    public interface Appearance<E>
    {
	/**
	 * Flags that modify the appearance behaviour.
	 */
	public enum Flags {
	    /** Brief announcement (e.g. when moving quickly). */
	    BRIEF,
	    /** The text is being prepared for clipboard copy. */
	    CLIPBOARD
	};

	/**
	 * Announces the given item using the screen reader.
	 *
	 * @param item  the item to announce
	 * @param flags a set of flags that may affect the announcement style
	 */
	void announceItem(E item, Set<Flags> flags);

	/**
	 * Returns the text representation of the item as it should appear on
	 * the screen.
	 *
	 * @param item  the item to display
	 * @param flags a set of flags that may affect the representation
	 * @return the screen text for the item
	 */
	String getScreenAppearance(E item, Set<Flags> flags);

	/**
	 * Returns the leftmost observable column index for the item. The hot
	 * point cannot move to the left of this bound.
	 *
	 * @param item the item
	 * @return the left bound (inclusive)
	 */
	int getObservableLeftBound(E item);

	/**
	 * Returns the rightmost observable column index for the item. The hot
	 * point cannot move to the right of this bound.
	 *
	 * @param item the item
	 * @return the right bound (inclusive)
	 */
	int getObservableRightBound(E item);
    }

    /**
     * Handler for click (activation) events on list items. This is typically
     * triggered by pressing Enter or a dedicated OK command.
     *
     * @param <E> the type of items in the list
     */
    public interface ClickHandler<E>
    {
	/**
	 * Called when an item is activated.
	 *
	 * @param area  the list area that received the click
	 * @param index the index of the clicked item in the model
	 * @param item  the clicked item
	 * @return {@code true} if the event was handled; {@code false} otherwise
	 */
	boolean onListClick(ListArea<E> area, int index, E item);
    }

    /**
     * Responsible for saving a range of items to the clipboard. This allows
     * custom formatting of the clipboard content.
     *
     * @param <E> the type of items in the list
     */
    public interface ClipboardSaver<E>
    {
	/**
	 * Saves the items from {@code fromIndex} (inclusive) to
	 * {@code toIndex} (exclusive) to the given clipboard.
	 *
	 * @param listArea    the list area requesting the save
	 * @param model       the model providing the items (may differ from the area's model in subclasses)
	 * @param appearance  the appearance used for text conversion
	 * @param fromIndex   the starting index (inclusive)
	 * @param toIndex     the ending index (exclusive)
	 * @param clipboard   the clipboard to write to
	 * @return {@code true} if the save succeeded, {@code false} otherwise
	 */
	boolean saveToClipboard(ListArea<E> listArea, Model<E> model, Appearance<E> appearance, int fromIndex, int toIndex, Clipboard clipboard);
    }

    /**
     * Defines how the hot point moves in response to navigation commands
     * (arrow keys, page up/down, home/end). The transition logic can be
     * customised by providing a different implementation.
     */
    public interface Transition
    {
	/**
	 * Types of navigation actions.
	 */
	public enum Type {
	    /** Move one item down. */
	    SINGLE_DOWN,
	    /** Move one item up. */
	    SINGLE_UP,
	    /** Move one page down. */
	    PAGE_DOWN,
	    /** Move one page up. */
	    PAGE_UP,
	    /** Jump to the first item. */
	    HOME,
	    /** Jump to the last item. */
	    END
	};

	/**
	 * Represents a possible state of the hot point: on an empty line
	 * (top or bottom), on a specific item index, or no transition
	 * possible.
	 */
	static public final class State
	{
	    /**
	     * The type of the state.
	     */
	    public enum Type {
		/** Hot point is on the top empty line. */
		EMPTY_LINE_TOP,
		/** Hot point is on the bottom empty line. */
		EMPTY_LINE_BOTTOM,
		/** Hot point is on a valid item. */
		ITEM_INDEX,
		/** No transition is possible (e.g. already at the boundary). */
		NO_TRANSITION
	    };

	    /** The type of this state. */
	    public final Type type;
	    /** The item index if type is {@code ITEM_INDEX}, otherwise -1. */
	    public final int itemIndex;

	    /**
	     * Creates a state with the given type and no item index.
	     *
	     * @param type the state type (must not be {@code null})
	     * @throws NullPointerException if {@code type} is {@code null}
	     */
	    public State(Type type)
	    {
		requireNonNull(type, "type can't be null");
		this.type = type;
		this.itemIndex = -1;
	    }

	    /**
	     * Creates a state representing an item index.
	     *
	     * @param itemIndex the index of the item
	     */
	    public State(int itemIndex)
	    {
		this.type = Type.ITEM_INDEX;
		this.itemIndex = itemIndex;
	    }
	}

	/**
	 * Computes the new state after a navigation action.
	 *
	 * @param type               the type of navigation action
	 * @param fromState          the current state of the hot point
	 * @param itemCount          the total number of items in the model
	 * @param hasEmptyLineTop    whether the list has a top empty line
	 * @param hasEmptyLineBottom whether the list has a bottom empty line
	 * @return the new state after the transition; never {@code null}
	 */
	State transition(Type type, State fromState, int itemCount,
			 boolean hasEmptyLineTop, boolean hasEmptyLineBottom);
    }

    /**
     * Parameters for constructing a {@link ListArea}. All fields must be set
     * before passing to the constructor.
     *
     * @param <E> the type of items in the list
     */
    static public class Params<E>
    {
	/** The control context (required). */
	public ControlContext context = null;
	/** The data model (required). */
	public Model<E> model = null;
	/** The appearance controller (required). */
	public Appearance<E> appearance = null;
	/** Optional click handler. */
	public ListArea.ClickHandler<E> clickHandler;
	/** The transition logic (defaults to {@link ListUtils.DefaultTransition}). */
	public Transition transition = new ListUtils.DefaultTransition();
	/** The clipboard saver (defaults to {@link ListUtils.DefaultClipboardSaver}). */
	public ClipboardSaver<E> clipboardSaver = new ListUtils.DefaultClipboardSaver<E>();
	/** The area name (required). */
	public String name = null;
	/** Flags controlling list behaviour (default includes {@link Flags#EMPTY_LINE_BOTTOM}). */
	public Set<Flags> flags = EnumSet.of(Flags.EMPTY_LINE_BOTTOM);
    }

    protected final ControlContext context;
    protected final RegionPoint regionPoint = new RegionPoint();
    protected final ClipboardTranslator clipboardTranslator = new ClipboardTranslator(this, regionPoint, EnumSet.of(ClipboardTranslator.Flags.ALLOWED_EMPTY, ClipboardTranslator.Flags.ALLOWED_WITHOUT_REGION_POINT));
    protected final RegionTextQueryTranslator regionTextQueryTranslator = new RegionTextQueryTranslator(this, regionPoint, EnumSet.noneOf(RegionTextQueryTranslator.Flags.class));
    protected String areaName = "";
    protected final Model<E> listModel;
    protected final Appearance<E> listAppearance;
    protected final Transition listTransition;
    protected final ClipboardSaver<E> listClipboardSaver;
    protected final Set<Flags> listFlags;
    protected ListArea.ClickHandler<E> listClickHandler = null;

    protected int hotPointX = 0;
    protected int hotPointY = 0;

    /**
     * Constructs a new list area with the given parameters.
     *
     * @param params the configuration parameters (must not be {@code null})
     * @throws NullPointerException if {@code params} or any required field is {@code null}
     */
    public ListArea(Params<E> params)
    {
	requireNonNull(params, "params can't be null");
	NullCheck.notNull(params.context, "params.context");
	NullCheck.notNull(params.model, "params.model");
	NullCheck.notNull(params.appearance, "params.appearance");
	NullCheck.notNull(params.transition, "params.transition");
	NullCheck.notNull(params.clipboardSaver, "params.clipboardSaver");
	NullCheck.notNull(params.name, "params.name");
	NullCheck.notNull(params.flags, "params.flags");
	this.context = params.context;
	this.listModel = params.model;
	this.listAppearance = params.appearance;
	this.listTransition = params.transition;
	this.listClipboardSaver = params.clipboardSaver;
	this.listClickHandler = params.clickHandler;
	this.areaName = params.name;
	this.listFlags = params.flags;
	resetHotPoint();
    }

    /**
     * Sets the click handler for item activation.
     *
     * @param clickHandler the handler to set (may be {@code null})
     */
    public void setListClickHandler(ListArea.ClickHandler<E> clickHandler)
    {
	this.listClickHandler = clickHandler;
    }

    /**
     * Returns the data model used by this list.
     *
     * @return the model
     */
    public Model<E> getListModel()
    {
	return listModel;
    }

    /**
     * Returns the appearance controller used by this list.
     *
     * @return the appearance
     */
    public Appearance<E> getListAppearance()
    {
	return listAppearance;
    }

    /**
     * Returns the object in the model corresponding to current hot point
     * position.  If the model is empty or hot point is on an empty line,
     * this method always returns {@code null}. 
     *
     * @return The object in the model associated with the currently selected line or {@code null} if there is no any
     */
    public final E selected()
    {
	final int index = selectedIndex();
	return (index >= 0 && index < listModel.getItemCount())?listModel.getItem(index):null;
    }

    /**
     * The index of the item in the model which is under the hot point in
     * this list. This method returns the index in the model, not on the
     * screen. It means that the value returned by this method may be
     * different than the value returned by {@code getHotPointY()} (but may
     * be equal as well). If the list is empty or an empty line is selected,
     * this method returns -1. 
     *
     * @return The index of the selected line in the model or -1 if there is no any
     */
    public final int selectedIndex()
    {
	return getExistingItemIndexOnLine(hotPointY);
    }

    /**
     * Searches for the item in the model and sets hot point on it. Given an
     * arbitrary object, this method looks through all items in the model and
     * does a couple of checks: literal pointers equality and a check with
     * {@code equals()} method. If at least one of these checks succeeds, the
     * item is considered equal to the given one, and hot points is set on
     * it.  
     *
     * @param obj The object to search for
     * @param announce Must be true if it is necessary to introduce the object, once it's found
     * @return True if the request object is found, false otherwise
     */
    public boolean select(E obj, boolean announce)
    {
	requireNonNull(obj, "obj can't be null");
	for(int i = 0;i < listModel.getItemCount();++i)
	{
	    final E o = listModel.getItem(i);
	    if (o == null)
		continue;
		if (obj != o && !obj.equals(o))
	continue;
	    hotPointY = getLineIndexByItemIndex(i);
	hotPointX = listAppearance.getObservableLeftBound(o);
	context.onAreaNewHotPoint(this);
	if (announce)
	    listAppearance.announceItem(o, NONE_APPEARANCE_FLAGS);
	return true;
	}
	return false;
    }

    /**
     * Selects the item by its index. Given the non-negative integer value as
     * an index, this method sets the hot point on the item addressed with
     * this index, checking only that index is in appropriate bounds. Index must address
     * the object as a number in the model, ignoring any empty lines.
     *
     * @param index The item index to select
     * @param announce Must be true, if it is necessary to announce the item , once it has been selected
     * @return True if the index is valid and the item gets hot point on it
     */
    public boolean select(int index, boolean announce)
    {
	if (index < 0 || index >= listModel.getItemCount())
	    return false;
	final int emptyCountAbove = listFlags.contains(Flags.EMPTY_LINE_TOP)?1:0;
	hotPointY = index + emptyCountAbove;
	final E item = listModel.getItem(index);
	if (item != null)
	{
	    hotPointX = listAppearance.getObservableLeftBound(item);
	    if (announce)
		listAppearance.announceItem(item, NONE_APPEARANCE_FLAGS);
	} else
	{
	    hotPointX = 0;
	    if (announce)
		context.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE));
	}
	context.onAreaNewHotPoint(this);
	return true;
    }

    /**
     * Moves the hot point to the bottom empty line, if the list has one.
     *
     * @param announce if {@code true}, the empty line is announced
     * @return {@code true} if the operation succeeded, {@code false} if there is no bottom empty line
     */
    public boolean selectEmptyLineBottom(boolean announce)
    {
	if (!listFlags.contains(Flags.EMPTY_LINE_BOTTOM))
	    return false;
	final int emptyCountAbove = listFlags.contains(Flags.EMPTY_LINE_TOP)?1:0;
	hotPointY = listModel.getItemCount() + emptyCountAbove;
	    hotPointX = 0;
	    if (announce)
		context.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE));
	context.onAreaNewHotPoint(this);
	return true;
    }

    /**
     * Given a line index (screen coordinate), returns the corresponding
     * item index in the model, or -1 if the line does not contain a valid
     * item.
     *
     * @param lineIndex the screen line index (non-negative)
     * @return the model index, or -1 if not on an item
     * @throws IllegalArgumentException if {@code lineIndex} is negative
     */
    public int getExistingItemIndexOnLine(int lineIndex)
    {
	if (lineIndex < 0)
	    throw new IllegalArgumentException("lineIndex is negative (" + lineIndex + ")");
	final int res = getItemIndexOnLine(lineIndex);
	return res < listModel.getItemCount()?res:-1;
    }

    /**
     * Converts a screen line index to a model index, without checking
     * whether the resulting index is within the model bounds. This is used
     * internally for calculations.
     *
     * @param index the screen line index
     * @return the model index (may be out of bounds)
     */
    public int getItemIndexOnLine(int index)
    {
	final int linesTop = listFlags.contains(Flags.EMPTY_LINE_TOP)?1:0;
	if (index < linesTop)
	    return -1;
	if (index - linesTop <= listModel.getItemCount())
	    return index - linesTop;
	return -1;
    }

    /**
     * Converts a model item index to the corresponding screen line index.
     *
     * @param index the model index
     * @return the screen line index, or -1 if the index is invalid
     */
    public int getLineIndexByItemIndex(int index)
    {
	final int count = listModel.getItemCount();
	if (index < 0 || index >= count)
	    return -1;
	final int linesTop = listFlags.contains(Flags.EMPTY_LINE_TOP)?1:0;
	return index + linesTop;
    }

    /**
     * Returns the item displayed on the given screen line.
     *
     * @param lineIndex the screen line index (non-negative)
     * @return the item, or {@code null} if the line is empty or out of range
     * @throws IllegalArgumentException if {@code lineIndex} is negative
     */
    public E getItemOnLine(int lineIndex)
    {
	if (lineIndex < 0)
	    throw new IllegalArgumentException("lineIndex may not be negative (" + lineIndex + ")");
	final int index = getExistingItemIndexOnLine(lineIndex);
	if (index < 0)
	    return null;
	return listModel.getItem(index);
    }

    /**
     * Returns the total number of items in the model.
     *
     * @return the item count
     */
    public int getListItemCount()
    {
	return listModel.getItemCount();
    }

    /**
     * Resets the region point and the hot point to the beginning of the list.
     *
     * @param announce if {@code true}, the newly selected item is announced
     */
    public void reset(boolean announce)
    {
	regionPoint.reset();
	resetHotPoint(announce);
    }

    /**
     * Resets the hot point to the first item (or the top empty line if the
     * list is empty) without any announcement.
     */
    public void resetHotPoint()
    {
	resetHotPoint(false);
    }

    /**
     * Resets the hot point to the first item (or the top empty line if the
     * list is empty).
     *
     * @param introduce if {@code true}, the newly selected item is announced
     */
    public void resetHotPoint(boolean introduce)
    {
	hotPointY = 0;
	final int count = listModel.getItemCount();
	if (count < 1)
	{
	    hotPointX = 0;
	    context.onAreaNewHotPoint(this);
	    return;
	}
	final E item = listModel.getItem(0);
	if (item != null)
	{
	    hotPointX = item != null?listAppearance.getObservableLeftBound(item):0;
	    if (introduce)
		listAppearance.announceItem(item, NONE_APPEARANCE_FLAGS);
	} else
	{
	    hotPointX = 0;
	    context.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE));
	}
	context.onAreaNewHotPoint(this);
    }

    /**
     * Announces the currently selected item using the appearance.
     */
    public void announceSelected()
    {
	final E item = selected();
	if (item != null)
	    listAppearance.announceItem(item, NONE_APPEARANCE_FLAGS);
    }

    /**
     * Refreshes the content of the list. This method calls {@code refresh()}
     * method of the model and displays new items. It does not produce any
     * speech announcement of the change. HotPointY is preserved if it is
     * possible (meaning, the new number of lines not less than old value of
     * hotPointY), but hotPointX is moved to the beginning of the line.
     */
    public void refresh()
    {
	final E previouslySelected = selected();
	listModel.refresh();
	context.onAreaNewContent(this);
	final int count = listModel.getItemCount();
	if (count == 0)
	{
	    hotPointX = 0;
	    hotPointY = 0;
	    context.onAreaNewHotPoint(this);
	    return;
	}
	if (previouslySelected != null)
	{
	    if (previouslySelected == selected())
		return;
	    if (select(previouslySelected, false))
		return;
	}
	hotPointY = Math.min(hotPointY, getLineCount() - 1);
	final E item = getItemOnLine(hotPointY);
	if (item != null)
	{
	    hotPointX = Math.min(hotPointX, listAppearance.getObservableRightBound(item));
	    hotPointX = Math.max(hotPointX, listAppearance.getObservableLeftBound(item));
	}else
	    hotPointX = 0;
	context.onAreaNewHotPoint(this);
    }

    /**
     * Redraws the area content and adjusts the hot point similarly to
     * {@link #refresh()}, but without calling {@code Model.refresh()}.
     */
    public void redraw()
    {
	final E previouslySelected = selected();
	context.onAreaNewContent(this);
	final int count = listModel.getItemCount();
	if (count == 0)
	{
	    hotPointX = 0;
	    hotPointY = 0;
	    context.onAreaNewHotPoint(this);
	    return;
	}
	if (previouslySelected != null)
	{
	    if (previouslySelected == selected())
		return;
	    if (select(previouslySelected, false))
		return;
	}
	hotPointY = Math.min(hotPointY, getLineCount() - 1);
	final E item = getItemOnLine(hotPointY);
	if (item != null)
	{
	    hotPointX = Math.min(hotPointX, listAppearance.getObservableRightBound(item));
	    hotPointX = Math.max(hotPointX, listAppearance.getObservableLeftBound(item));
	}else
	    hotPointX = 0;
	context.onAreaNewHotPoint(this);
    }

    /**
     * Checks whether the list model contains any items.
     *
     * @return {@code true} if the model is empty, {@code false} otherwise
     */
    public boolean isEmpty()
    {
	return listModel.getItemCount() <= 0;
    }

    @Override public boolean onInputEvent(InputEvent event)
    {
	requireNonNull(event, "even tcan't be null");
	if (!event.isSpecial() && (!event.isModified() || event.withShiftOnly()))
	    return onChar(event);
	if (!event.isSpecial() || event.isModified())
	    return false;
	switch(event.getSpecial())
	{
	case ARROW_DOWN:
	    return onMoveDown(event, false);
	case ARROW_UP:
	    return onMoveUp(event, false);
	case ARROW_RIGHT:
	    return onMoveRight(event);
	case ARROW_LEFT:
	    return onMoveLeft(event);
	case ALTERNATIVE_ARROW_DOWN:
	    return onMoveDown(event, true);
	case ALTERNATIVE_ARROW_UP:
	    return onMoveUp(event, true);
	case ALTERNATIVE_ARROW_RIGHT:
	    return onAltRight(event);
	case ALTERNATIVE_ARROW_LEFT:
	    return onAltLeft(event);
	case HOME:
	    return onHome(event);
	case END:
	    return onEnd(event);
	case ALTERNATIVE_HOME:
	    return onAltHome(event);
	case ALTERNATIVE_END:
	    return onAltEnd(event);
	case PAGE_DOWN:
	    return onPageDown(event, false);
	case PAGE_UP:
	    return onPageUp(event, false);
	case  ALTERNATIVE_PAGE_DOWN:
	    return onPageDown(event, true);
	case ALTERNATIVE_PAGE_UP:
	    return onPageUp(event, true);
	case ENTER:
	    return onEnter(event);
	default:
	    return false;
	}
    }

    @Override public boolean onSystemEvent(SystemEvent event)
    {
	requireNonNull(event, "event can't be null");
	if (event.getType() != SystemEvent.Type.REGULAR)
	    return false;
	switch (event.getCode())
	{
	case REFRESH:
	    refresh();
	    return true;
	case INTRODUCE:
	    return onAnnounce();
	case ANNOUNCE_LINE:
	    return onAnnounceLine();
	case OK:
	    return onOk(event);
	case LISTENING_FINISHED:
	    if (event instanceof ListeningFinishedEvent)
		return onListeningFinishedEvent((ListeningFinishedEvent)event);
	    return false;
	case MOVE_HOT_POINT:
	    if (event instanceof MoveHotPointEvent)
		return onMoveHotPoint((MoveHotPointEvent)event);
	    return false;
	default:
	    return clipboardTranslator.onSystemEvent(event, hotPointX, hotPointY);
	}
    }

    @Override public boolean onAreaQuery(AreaQuery query)
    {
	requireNonNull(query, "query can't be null");
	switch(query.getQueryCode())
	{
	case AreaQuery.BEGIN_LISTENING:
	    if (query instanceof BeginListeningQuery)
		return onBeginListeningQuery((BeginListeningQuery)query);
		return false;
	case AreaQuery.REGION_TEXT:
	    return regionTextQueryTranslator.onAreaQuery(query, getHotPointX(), getHotPointY());
	default:
	    return false;
	}
    }

    @Override public Action[] getAreaActions()
    {
	return new Action[0];
    }

    @Override public int getLineCount()
    {
	final int emptyCountTop = listFlags.contains(Flags.EMPTY_LINE_TOP)?1:0;
	final int emptyCountBottom = listFlags.contains(Flags.EMPTY_LINE_BOTTOM)?1:0;
	final int res = listModel.getItemCount() + emptyCountTop + emptyCountBottom;
	return res>= 1?res:1;
    }

    @Override public String getLine(int index)
    {
	if (index < 0)
	    return "";
	if (isEmpty())
	    return index == 0?noContentStr():"";
	final int itemIndex = getExistingItemIndexOnLine(index);
	if (itemIndex < 0)
	    return "";
	final E res = listModel.getItem(itemIndex);
	return res != null?listAppearance.getScreenAppearance(res, NONE_APPEARANCE_FLAGS):"";
    }

    @Override public int getHotPointX()
    {
	return hotPointX >= 0?hotPointX:0;
    }

    @Override public int getHotPointY()
    {
	return hotPointY >= 0?hotPointY:0;
    }

    @Override public String getAreaName()
    {
	requireNonNull(areaName, "areaName can't be null");
	return areaName;
    }

    /**
     * Sets the area name and notifies the context of the change.
     *
     * @param areaName the new area name (must not be {@code null})
     * @throws NullPointerException if {@code areaName} is {@code null}
     */
    public void setAreaName(String areaName)
    {
	requireNonNull(areaName, "areaName can't be null");
	this.areaName = areaName;
	context.onAreaNewName(this);
    }

    /**
     * Announces the list area. If the area is created with {@code AREA_ANNOUNCE_SELECTED} 
     * flag, this method speaks the screen text of
     * the currently selected item or the area name otherwise (without the
     * flag or if there is no selected item).
     */
    protected boolean onAnnounce()
    {
	if (!listFlags.contains(Flags.AREA_ANNOUNCE_SELECTED))
	{
	    context.say(getAreaName(), Sounds.INTRO_REGULAR);
	    return true;
	}
	final String item;
	if (selected() != null)
	{
	    final String value = listAppearance.getScreenAppearance(selected(), EnumSet.noneOf(Appearance.Flags.class)).trim();
	    if (value != null && !value.trim().isEmpty())
		item = value; else
		item = selected().toString();
	} else
	    item = "";
	if (!item.trim().isEmpty())
	    context.say(context.getSpeakableText(item, Luwrain.SpeakableTextType.NATURAL), Sounds.INTRO_REGULAR); else
	    context.say(getAreaName(), Sounds.INTRO_REGULAR);
	return true;
    }

    /**
     * Announces the current line (the item under the hot point).
     *
     * @return {@code true} if the announcement was handled
     */
    protected boolean onAnnounceLine()
    {
	if (isEmpty())
	    return false;
	final E item = selected();
	if (item == null)
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE));
	    return true;
	}
	listAppearance.announceItem(item, NONE_APPEARANCE_FLAGS);
	return true;
    }

    /**
     * Handles a {@link MoveHotPointEvent} to reposition the hot point.
     *
     * @param event the event (must not be {@code null})
     * @return {@code true} if the hot point was moved
     */
    protected boolean onMoveHotPoint(MoveHotPointEvent event)
    {
	requireNonNull(event, "event can't be null");
	final int x = event.getNewHotPointX();
	final int y = event.getNewHotPointY();
	final int newY;
	if (y >= getLineCount())
	{
	    if (event.precisely())
		return false;
newY = getLineCount() - 1;
	} else
newY = y;
	if (getExistingItemIndexOnLine(newY) >= 0)
	    {
		//Line with item, not empty
		final E item = listModel.getItem(getExistingItemIndexOnLine(newY));
		final int leftBound = listAppearance.getObservableLeftBound(item);
final int rightBound = listAppearance.getObservableRightBound(item);
		if (event.precisely() &&
		    (x < leftBound || x > rightBound))
		    return false;
		hotPointY = newY;
		hotPointX = x;
		if (hotPointX < leftBound)
		    hotPointX = leftBound;
		if (hotPointX > rightBound)
		    hotPointX = rightBound;
		context.onAreaNewHotPoint(this);
		return true;
	    }
	    //On empty line
	    hotPointY = newY;
	    hotPointX = 0;
	    context.onAreaNewHotPoint(this);
	    return true;
    }

    /**
     * Handles a {@link BeginListeningQuery} to start continuous reading from
     * the current hot point position.
     *
     * @param query the query (must not be {@code null})
     * @return {@code true} if the query was answered
     */
    protected boolean onBeginListeningQuery(BeginListeningQuery query)
    {
	requireNonNull(query, "query can't be null");
	final int index = selectedIndex();
	if (index < 0)
	    return false;
	final int count = listModel.getItemCount();
	if (index >= count)
	    return false;
	final E current = listModel.getItem(index);
	final String text = listAppearance.getScreenAppearance(current, NONE_APPEARANCE_FLAGS).substring(Math.max(hotPointX, listAppearance.getObservableLeftBound(current)), listAppearance.getObservableRightBound(current));
	if (text.isEmpty() && index + 1 >= count)
	    return false;
	if (index + 1 < count)
	{
	    final E next = listModel.getItem(index + 1);
	    query.answer(new BeginListeningQuery.Answer(text, new ListeningInfo(index + 1, listAppearance.getObservableLeftBound(next))));
	} else
	    query.answer(new BeginListeningQuery.Answer(text, new ListeningInfo(index, listAppearance.getObservableRightBound(current))));
	return true;
    }

    /**
     * Handles the completion of a listening session, moving the hot point to
     * the position where listening stopped.
     *
     * @param event the event (must not be {@code null})
     * @return {@code true} if the hot point was updated
     */
    protected boolean onListeningFinishedEvent(ListeningFinishedEvent event)
    {
	requireNonNull(event, "event can't be null");
	if (!(event.getExtraInfo() instanceof ListeningInfo))
	    return false;
	final ListeningInfo info = (ListeningInfo)event.getExtraInfo();
	final int count = listModel.getItemCount();
	if (info.itemIndex >= count)
	    return false;
	final E item = listModel.getItem(info.itemIndex);
	final int leftBound = listAppearance.getObservableLeftBound(item);
	final int rightBound = listAppearance.getObservableRightBound(item);
	if (info.pos < leftBound || info.pos > rightBound)
	    return false;
	hotPointY = getLineIndexByItemIndex(info.itemIndex);
	hotPointX = info.pos;
	context.onAreaNewHotPoint(this);
	return true;
    }

    /**
     * Handles character input for incremental search. Moves the hot point to
     * the first item whose observable text starts with the accumulated prefix.
     *
     * @param event the input event
     * @return {@code true} if the character was processed
     */
    protected boolean onChar(InputEvent event)
    {
	if (noContent())
	    return true;
	final int count = listModel.getItemCount();
	final char c = Character.toLowerCase(event.getChar());
	final String beginning;
	if (selected() != null)
	{
	    if (hotPointX >= listAppearance.getObservableRightBound(selected()))
		return false;
	    final String name = getObservableSubstr(selected()).toLowerCase();
	    final int pos = Math.min(hotPointX - listAppearance.getObservableLeftBound(selected()), name.length());
	    if (pos < 0)
		return false;
	    beginning = name.substring(0, pos);
	} else
	    beginning = "";
	final String mustBegin = beginning + c;
	for(int i = 0;i < count;++i)
	{
	    final String name = getObservableSubstr(listModel.getItem(i)).toLowerCase();
	    if (!name.startsWith(mustBegin))
		continue;
	    hotPointY = getLineIndexByItemIndex(i);
	    ++hotPointX;
	    listAppearance.announceItem(listModel.getItem(hotPointY), NONE_APPEARANCE_FLAGS);
	    context.onAreaNewHotPoint(this);
	    return true;
	}
	return false;
    }

    /**
     * Handles the "move down" command.
     *
     * @param event the input event
     * @param briefAnnouncement if {@code true}, use brief announcement
     * @return {@code true} if handled
     */
    protected boolean onMoveDown(InputEvent event, boolean briefAnnouncement)
    {
	return onTransition(Transition.Type.SINGLE_DOWN, Hint.NO_ITEMS_BELOW, briefAnnouncement);
    }

    /**
     * Handles the "move up" command.
     *
     * @param event the input event
     * @param briefAnnouncement if {@code true}, use brief announcement
     * @return {@code true} if handled
     */
    protected boolean onMoveUp(InputEvent event, boolean briefAnnouncement)
    {
	return onTransition(Transition.Type.SINGLE_UP, Hint.NO_ITEMS_ABOVE, briefAnnouncement);
    }

    /**
     * Handles the "page down" command.
     *
     * @param event the input event
     * @param briefAnnouncement if {@code true}, use brief announcement
     * @return {@code true} if handled
     */
    protected boolean onPageDown(InputEvent event, boolean briefAnnouncement)
    {
	return onTransition(Transition.Type.PAGE_DOWN, Hint.NO_ITEMS_BELOW, briefAnnouncement);
    }

    /**
     * Handles the "page up" command.
     *
     * @param event the input event
     * @param briefAnnouncement if {@code true}, use brief announcement
     * @return {@code true} if handled
     */
    protected boolean onPageUp(InputEvent event, boolean briefAnnouncement)
    {
	return onTransition(Transition.Type.PAGE_UP, Hint.NO_ITEMS_ABOVE, briefAnnouncement);
    }

    /**
     * Handles the "end" command (jump to last item).
     *
     * @param event the input event
     * @return {@code true} if handled
     */
    protected boolean onEnd(InputEvent event)
    {
	return onTransition(Transition.Type.END, Hint.NO_ITEMS_BELOW, false);
    }

    /**
     * Handles the "home" command (jump to first item).
     *
     * @param event the input event
     * @return {@code true} if handled
     */
    protected boolean onHome(InputEvent event)
    {
	return onTransition(Transition.Type.HOME, Hint.NO_ITEMS_ABOVE, false);
    }

    /**
     * Performs a transition based on the given type, using the current state
     * and the list's transition logic.
     *
     * @param type the type of transition
     * @param hint the hint to provide if no transition is possible
     * @param briefAnnouncement if {@code true}, use brief announcement
     * @return {@code true} if the transition was processed
     */
    protected boolean onTransition(Transition.Type type, Hint hint, boolean briefAnnouncement)
    {
	requireNonNull(type, "type can't be null");
	requireNonNull(hint, "hint can't be null");
	if (noContent())
	    return true;
	final int index = selectedIndex();
	final int count = listModel.getItemCount();
	final int emptyCountTop = listFlags.contains(Flags.EMPTY_LINE_TOP)?1:0;
	final Transition.State current;
	if (index >= 0)
	    current = new Transition.State(index); else
	    if (listFlags.contains(Flags.EMPTY_LINE_TOP) && hotPointY == 0)
		current = new Transition.State(Transition.State.Type.EMPTY_LINE_TOP); else
		if (listFlags.contains(Flags.EMPTY_LINE_BOTTOM) && hotPointY == count + emptyCountTop)
		    current = new Transition.State(Transition.State.Type.EMPTY_LINE_BOTTOM); else
		    return false;
	final Transition.State newState = listTransition.transition(type, current, count,
								    listFlags.contains(Flags.EMPTY_LINE_TOP), listFlags.contains(Flags.EMPTY_LINE_BOTTOM));
	requireNonNull(newState, "newState can't be null");
	switch(newState.type)
	{
	case NO_TRANSITION:
	    context.setEventResponse(DefaultEventResponse.hint(hint));
	    return true;
	case EMPTY_LINE_TOP:
	    if (!listFlags.contains(Flags.EMPTY_LINE_TOP))
		return false;
	    hotPointY = 0;
	    break;
	case EMPTY_LINE_BOTTOM:
	    if (!listFlags.contains(Flags.EMPTY_LINE_BOTTOM))
		return false;
	    hotPointY = count + emptyCountTop;
	    break;
	case ITEM_INDEX:
	    if (newState.itemIndex < 0 || newState.itemIndex >= count)
		return false;
	    hotPointY = newState.itemIndex + emptyCountTop;
	    break;
	default:
	    return false;
	}
	onNewHotPointY(briefAnnouncement);
	return true;
    }

    /**
     * Handles the "move right" command (character by character).
     *
     * @param event the input event
     * @return {@code true} if handled
     */
    protected boolean onMoveRight(InputEvent event)
    {
	if (noContent())
	    return true;
	final E item = selected();
	if (item == null)
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE));
	    return true;
	}
	final String line = listAppearance.getScreenAppearance(item, NONE_APPEARANCE_FLAGS);
	if (line == null || line.isEmpty())
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE));
	    return true;
	}
final int rightBound = listAppearance.getObservableRightBound(item);
	if (hotPointX >= rightBound)
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.END_OF_LINE));
	    return true;
	}
	++hotPointX;
	announceChar(line, hotPointX, rightBound);
	context.onAreaNewHotPoint(this);
	return true;
    }

    /**
     * Handles the "move left" command (character by character).
     *
     * @param event the input event
     * @return {@code true} if handled
     */
    protected boolean onMoveLeft(InputEvent event)
    {
	if (noContent())
	    return true;
	final E item = selected();
	if (item == null)
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE));
	    return true;
	}
	final String line = listAppearance.getScreenAppearance(item, NONE_APPEARANCE_FLAGS);
	if (line == null || line.isEmpty())
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE));
	    return true;
	}
	final int leftBound = listAppearance.getObservableLeftBound(item);
	final int rightBound = listAppearance.getObservableRightBound(item);
	if (hotPointX <= leftBound)
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.BEGIN_OF_LINE));
	    return true;
	}
	--hotPointX;
	announceChar(line, hotPointX, rightBound);
	context.onAreaNewHotPoint(this);
	return true;
    }

    /**
     * Handles the alternative right command (move by word forward).
     *
     * @param event the input event
     * @return {@code true} if handled
     */
    protected boolean onAltRight(InputEvent event)
    {
	if (noContent())
	    return true;
	final E item = selected();
	if (item == null)
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE));
	    return true;
	}
	final String line = listAppearance.getScreenAppearance(item, NONE_APPEARANCE_FLAGS);
	requireNonNull(line, "line can't be null");
	if (line.isEmpty())
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE));
	    return true;
	}
		final int leftBound = listAppearance.getObservableLeftBound(item);
final int rightBound = listAppearance.getObservableRightBound(item);
	if (hotPointX >= rightBound)
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.END_OF_LINE));
	    return true;
	}
	final String subline = line.substring(leftBound, rightBound);
	final WordIterator it = new WordIterator(subline, hotPointX - leftBound);
	if (!it.stepForward())
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.END_OF_LINE));
	    return true;
	}
	hotPointX = it.pos() + leftBound;
	if (it.announce().length() > 0)
	    context.setEventResponse(DefaultEventResponse.text(it.announce())); else
	    context.setEventResponse(DefaultEventResponse.hint(Hint.END_OF_LINE));
	context.onAreaNewHotPoint(this);
	return true;
    }

    /**
     * Handles the alternative left command (move by word backward).
     *
     * @param event the input event
     * @return {@code true} if handled
     */
    protected boolean onAltLeft(InputEvent event)
    {
	if (noContent())
	    return true;
	final E item = selected();
	if (item == null)
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE));
	    return true;
	}
	final String line = listAppearance.getScreenAppearance(item, NONE_APPEARANCE_FLAGS);
	if (line == null || line.isEmpty())
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE));
	    return true;
	}
	final int leftBound = listAppearance.getObservableLeftBound(item);
	final int rightBound = listAppearance.getObservableRightBound(item);
	if (hotPointX <= leftBound)
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.BEGIN_OF_LINE));
	    return true;
	}
	final String subline = line.substring(leftBound, rightBound);
	final WordIterator it = new WordIterator(subline, hotPointX - leftBound);
	if (!it.stepBackward())
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.BEGIN_OF_LINE));
	    return true;
	}
	hotPointX = it.pos() + leftBound;
	context.setEventResponse(DefaultEventResponse.text(it.announce()));
	context.onAreaNewHotPoint(this);
	return true;
    }

    /**
     * Handles the alternative end command (jump to the end of the line).
     *
     * @param event the input event
     * @return {@code true} if handled
     */
    protected boolean onAltEnd(InputEvent event)
    {
	if (noContent())
	    return true;
	final E item = selected();
	if (item == null)
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE));
	    return true;
	}
	final String line = listAppearance.getScreenAppearance(item, NONE_APPEARANCE_FLAGS);
	requireNonNull(line, "line can't be null");
	hotPointX = listAppearance.getObservableRightBound(item);
	context.setEventResponse(DefaultEventResponse.hint(Hint.LINE_BOUND));
	context.onAreaNewHotPoint(this);
	return true;
    }

    /**
     * Handles the alternative home command (jump to the beginning of the line).
     *
     * @param event the input event
     * @return {@code true} if handled
     */
    protected boolean onAltHome(InputEvent event)
    {
	if (noContent())
    	    return true;
	final E item = selected();
	if (item == null)
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE));
	    return true;
	}
	final String line = listAppearance.getScreenAppearance(item, NONE_APPEARANCE_FLAGS);
	requireNonNull(line, "line can't be null");
	hotPointX = listAppearance.getObservableLeftBound(item);
	announceChar(line, hotPointX, listAppearance.getObservableRightBound(item));
	context.onAreaNewHotPoint(this);
	return true;
    }

    /**
     * Handles the Enter key activation.
     *
     * @param event the input event
     * @return {@code true} if the click was handled
     */
    protected boolean onEnter(InputEvent event)
    {
	if (isEmpty() || listClickHandler == null)
	    return false;
	if (selected() == null || selectedIndex() < 0)
	    return false;
	if (!listClickHandler.onListClick(this, selectedIndex(), selected()))
	    return false;
	redraw();
	return true;
    }

    /**
     * Handles the OK system event (alternative activation).
     *
     * @param event the system event
     * @return {@code true} if the click was handled
     */
    protected boolean onOk(SystemEvent event)
    {
	if (listClickHandler == null)
	    return false;
	final int index = selectedIndex();
	final E item = selected();
	if (index < 0 || item == null)
	    return false;
	if (!listClickHandler.onListClick(this, index, item))
	    return false;
	redraw();
	return true;
    }

    @Override public String onRegionTextQuery(int fromX, int fromY, int toX, int toY)
    {
	if (isEmpty())
	    return null;
	if (fromX < 0 || fromY < 0 ||
	    (fromX == toX && fromY == toY))
	{
	    //Taking text of the current line
	    final int index = getExistingItemIndexOnLine(toY);
	    if (index < 0)
		return null;
	    return listAppearance.getScreenAppearance(listModel.getItem(index), NONE_APPEARANCE_FLAGS);
	}
	final int modelFromY = getExistingItemIndexOnLine(fromY);
	final int modelToY = getItemIndexOnLine(toY);//not necessarily existing
	if (modelFromY < 0 || modelToY < 0)
	    return null;
	if (modelFromY == modelToY)
	{
	    final String line = listAppearance.getScreenAppearance(listModel.getItem(modelFromY), NONE_APPEARANCE_FLAGS);
	    if (line == null || line.isEmpty())
		return null;
	    final int fromPos = Math.min(fromX, line.length());
	    final int toPos = Math.min(toX, line.length());
	    if (fromPos >= toPos)
		return null;
	    return line.substring(fromPos, toPos);
	}
	final StringBuilder b = new StringBuilder();
	b.append(listAppearance.getScreenAppearance(listModel.getItem(fromY), NONE_APPEARANCE_FLAGS));
	for(int i = fromY + 1;i < toY;++i)
	    b.append("\n" + listAppearance.getScreenAppearance(listModel.getItem(i), NONE_APPEARANCE_FLAGS));
	return new String(b);
    }

    @Override public boolean onClipboardCopyAll()
    {
	if (isEmpty())
	    return false;
	return listClipboardSaver.saveToClipboard(this, listModel, listAppearance, 0, listModel.getItemCount(), context.getClipboard());
    }

    @Override public boolean onClipboardCopy(int fromX, int fromY, int toX, int toY, boolean withDeleting)
    {
	if (isEmpty() || withDeleting)
	    return false;
	if (fromX < 0 || fromY < 0 ||
	    (fromX == toX && fromY == toY))
	{
	    final int index = getExistingItemIndexOnLine(toY);
	    if (index < 0)
		return false;
	    return listClipboardSaver.saveToClipboard(this, listModel, listAppearance, index, index + 1, context.getClipboard());
	}
	final int modelFromY = getExistingItemIndexOnLine(fromY);
	final int modelToY = getItemIndexOnLine(toY);//not necessarily existing
	if (modelFromY < 0 || modelToY < 0)
	    return false;
	if (modelFromY == modelToY)
	{
	    final String line = listAppearance.getScreenAppearance(listModel.getItem(modelFromY), EnumSet.of(Appearance.Flags.CLIPBOARD));
	    if (line == null || line.isEmpty())
		return false;
	    final int fromPos = Math.min(fromX, line.length());
	    final int toPos = Math.min(toX, line.length());
	    if (fromPos >= toPos)
		return false;
	    context.getClipboard().set(line.substring(fromPos, toPos));
	    return true;
	}
return listClipboardSaver.saveToClipboard(this, listModel, listAppearance, modelFromY, modelToY, context.getClipboard());
    }

    @Override public boolean onDeleteRegion(int fromX, int fromY, int toX, int toY)
    {
	return false;
    }

    /**
     * Called after the hot point Y coordinate changes. Updates the hot point X
     * to the left bound of the new item and announces it.
     *
     * @param briefAnnouncement if {@code true}, use brief announcement
     */
    protected void onNewHotPointY(boolean briefAnnouncement)
    {
	final int index = selectedIndex();
	if (index < 0)
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE));
	    hotPointX = 0;
	    context.onAreaNewHotPoint(this);
	    return;
	}
	final E item = listModel.getItem(index);
	if (item == null)
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE));
	    hotPointX = 0;
	    context.onAreaNewHotPoint(this);
	    return;
	}
	listAppearance.announceItem(item, briefAnnouncement?BRIEF_ANNOUNCEMENT_ONLY:NONE_APPEARANCE_FLAGS);
	hotPointX = listAppearance.getObservableLeftBound(item);
	context.onAreaNewHotPoint(this);
    }

    /**
     * Returns the observable substring of an item's screen representation,
     * i.e., the part between its left and right bounds.
     *
     * @param item the item (must not be {@code null})
     * @return the observable substring, or an empty string if not applicable
     */
    protected String getObservableSubstr(E item)
    {
	requireNonNull(item, "item can't be null");
final String line = listAppearance.getScreenAppearance(item, NONE_APPEARANCE_FLAGS);
requireNonNull(line, "line can't be null");
if (line.isEmpty())
return "";
final int leftBound = Math.min(listAppearance.getObservableLeftBound(item), line.length());
final int rightBound = Math.min(listAppearance.getObservableRightBound(item), line.length());
if (leftBound >= rightBound)
    return "";
return line.substring(leftBound, rightBound);
    }

    /**
     * Returns the string to display when the list has no content.
     *
     * @return the "no content" string
     */
    protected String noContentStr()
    {
	return context.getStaticStr("ListNoContent");
    }

    /**
     * Announces a character at the given position, or a line bound hint if
     * at the end.
     *
     * @param line the full line text
     * @param pos the current hot point column
     * @param rightBound the right observable bound
     */
    protected void announceChar(String  line, int pos, int rightBound)
    {
	requireNonNull(line, "line can't be null");
	if (pos < rightBound)
	    context.setEventResponse(DefaultEventResponse.letter(line.charAt(pos))); else
	    context.setEventResponse(DefaultEventResponse.hint(Hint.LINE_BOUND));
    }

    /**
     * Checks whether the list has no content and, if so, sets an appropriate
     * hint response.
     *
     * @return {@code true} if the list is empty, {@code false} otherwise
     */
    protected boolean noContent()
    {
	if (listModel == null || listModel.getItemCount() < 1)
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.NO_CONTENT, noContentStr()));
	    return true;
	}
	return false;
    }

    /**
     * Internal class used to pass information about the listening position
     * between the begin-listening query and the listening-finished event.
     */
    static protected class ListeningInfo
    {
	final int itemIndex;
	final int pos;

	ListeningInfo(int itemIndex, int pos)
	{
	    this.itemIndex = itemIndex;
	    this.pos = pos;
	}
    }
}
