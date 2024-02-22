/*
   Copyright 2012-2024 Michael Pozhidaev <msp@luwrain.org>

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

import static org.luwrain.core.NullCheck.*;

final class TileManager 
{
    enum Orientation {HORIZ, VERT};

    abstract class Node
    {
	public static final int LEAF = 0;
	public static final int COMPOSITE = 1;

	abstract int getType();
    }

    final class CompositeNode extends Node
    {
	Orientation orientation = Orientation.HORIZ;
	Node node1 = null, node2 = null;
int leafCount = 0;

CompositeNode()
	{
	}

CompositeNode(Orientation orientation, Node node1, Node node2)
	{
	    this.orientation = orientation;
	    this.node1 = node1;
	    this.node2 = node2;
	}

	public int getType()
	{
	    return COMPOSITE;
	}
    }

    class LeafNode extends Node
    {
	public Object obj;

	public LeafNode()
	{
	}

	public LeafNode(Object obj)
	{
	    this.obj = obj;
	}

	public int getType()
	{
	    return LEAF;
	}
    }

    private Node nodes;

    public void createSingle(Object obj)
    {
	nodes = new LeafNode(obj);
    }

    public void createLeftRight(Object obj1, Object obj2)
    {
	LeafNode node1 = new LeafNode(obj1);
	LeafNode node2 = new LeafNode(obj2);
	nodes = new CompositeNode(Orientation.HORIZ, node1, node2);
    }

    public void createTopBottom(Object obj1, Object obj2)
    {
	LeafNode node1 = new LeafNode(obj1);
	LeafNode node2 = new LeafNode(obj2);
	nodes = new CompositeNode(Orientation.VERT, node1, node2);
    }

    public void createLeftTopBottom(Object obj1,
				    Object obj2, 
				    Object obj3)
    {
	LeafNode node1 = new LeafNode(obj1);
	LeafNode node2 = new LeafNode(obj2);
	LeafNode node3 = new LeafNode(obj3);
	CompositeNode rightSide = new CompositeNode(Orientation.VERT, node2, node3);
	nodes = new CompositeNode(Orientation.HORIZ, node1, rightSide);
    }

    public void createLeftRightBottom(Object obj1,
				    Object obj2, 
				    Object obj3)
    {
	LeafNode node1 = new LeafNode(obj1);
	LeafNode node2 = new LeafNode(obj2);
	LeafNode node3 = new LeafNode(obj3);
	CompositeNode top = new CompositeNode(Orientation.HORIZ, node1, node2);
	nodes = new CompositeNode(Orientation.VERT, top, node3);
    }

    public void createHorizontally(Object[] objects)
    {
	if (objects == null || objects.length <= 0)
	{
	    nodes = null;
	    return;
	}
	if (objects.length == 1)
	{
	    nodes = new LeafNode(objects[0]);
	    return;
	}
	LeafNode node1 = new LeafNode(objects[0]);
	LeafNode node2 = new LeafNode(objects[1]);
	CompositeNode node = new CompositeNode(Orientation.HORIZ, node1, node2);
	for(int i = 2;i < objects.length;i++)
	    node = new CompositeNode(Orientation.HORIZ, node, new LeafNode(objects[i]));
	nodes = node;
    }

        public void createVertically(Object[] objects)
    {
	if (objects == null || objects.length <= 0)
	{
	    nodes = null;
	    return;
	}
	if (objects.length == 1)
	{
	    nodes = new LeafNode(objects[0]);
	    return;
	}
	LeafNode node1 = new LeafNode(objects[0]);
	LeafNode node2 = new LeafNode(objects[1]);
	CompositeNode node = new CompositeNode(Orientation.VERT, node1, node2);
	for(int i = 2;i < objects.length;i++)
	    node = new CompositeNode(Orientation.VERT, node, new LeafNode(objects[i]));
	nodes = node;
    }

    public void addTop(Object o)
    {
	if (o == null)
	    return;
	if (nodes == null)
	{
	    nodes = new LeafNode(o);
	    return;
	}
	nodes = new CompositeNode(Orientation.VERT, new LeafNode(o), nodes);
    }

    public void addBottom(Object o)
    {
	if (o == null)
	    return;
	if (nodes == null)
	{
	    nodes = new LeafNode(o);
	    return;
	}
	nodes = new CompositeNode(Orientation.VERT, nodes, new LeafNode(o));
    }

    public void addLeftSide(Object o)
    {
	if (o == null)
	    return;
	if (nodes == null)
	{
	    nodes = new LeafNode(o);
	    return;
	}
	nodes = new CompositeNode(Orientation.HORIZ, nodes, new LeafNode(o));
    }

    public void addRightSide(Object o)
    {
	if (o == null)
	    return;
	if (nodes == null)
	{
	    nodes = new LeafNode(o);
	    return;
	}
	nodes = new CompositeNode(Orientation.HORIZ, new LeafNode(o), nodes);
    }

    public void replace(Object obj, TileManager replaceWith)
    {
	if (obj == null || replaceWith == null || replaceWith.nodes == null)
	    return;
	nodes = replaceImpl(nodes, obj, replaceWith.nodes);
    }

    private Node replaceImpl(Node node, Object obj, Node replaceWith)
    {
	if (node == null)
	    return null;
	if (node.getType() == Node.LEAF)
	{
	    LeafNode leaf = (LeafNode)node;
	    return leaf.obj == obj?replaceWith:leaf;
	}
	CompositeNode composite = (CompositeNode)node;
	composite.node1 = replaceImpl(composite.node1, obj, replaceWith);
	composite.node2 = replaceImpl(composite.node2, obj, replaceWith);
	return composite;
    }

    public void clear()
    {
	nodes = null;
    }

    public Object[] getObjects()
    {
	return getObjectsImpl(nodes);
    }

    private Object[] getObjectsImpl(Node node)
    {
	if (node == null)
	    return new Object[0];
	if (node.getType() == Node.LEAF)
	{
	    LeafNode leaf = (LeafNode)node;
	    Object[] o = new Object[1];
	    o[0] = leaf.obj;
	    return o;
	}
	CompositeNode composite = (CompositeNode)node;
	Object[] o1 = getObjectsImpl(composite.node1);
	Object[] o2 = getObjectsImpl(composite.node2);
	Object[] o = new Object[o1.length + o2.length];
	for(int i = 0;i < o1.length;i++)
	    o[i] = o1[i];
	for(int i = 0;i < o2.length;i++)
	    o[o1.length + i] = o2[i];
	return o;
    }

    public void enumerate(TileVisitor it)
    {
	enumerateImpl(nodes, it);
    }

    private void enumerateImpl(Node node, TileVisitor it)
    {
	if (node == null)
	    return;
	if (node.getType() == Node.COMPOSITE)
	{
	    CompositeNode compositeNode = (CompositeNode)node;
	    enumerateImpl(compositeNode.node1, it);
	    enumerateImpl(compositeNode.node2, it);
	    return;
	}
	if (node.getType() == Node.LEAF)
	{
	    LeafNode leafNode = (LeafNode)node;
	    it.onTile(leafNode.obj);
	    return;
	}
	//Should never goes here;
    }

    public int countLeaves()
    {
	return countLeavesImpl(nodes);
    }

    private int countLeavesImpl(Node node)
    {
	if (node == null)
	    return 0;
	if (node.getType() == Node.LEAF)
	    return 1;
	CompositeNode composite = (CompositeNode)node;
	composite.leafCount = countLeavesImpl(composite.node1) + countLeavesImpl(composite.node2);
	return composite.leafCount;
    }

    public Object getRoot()
    {
	return nodes;
    }

    public boolean isLeaf(Object o)
    {
	if (o == null)
	    return false;
	Node n = (Node)o;
	return n.getType() == Node.LEAF;
    }

    public int getLeafCount(Object o)
    {
	if (o == null)
	    return 0;
	Node n = (Node)o;
	if (n.getType() == Node.LEAF)
	    return 1;
	CompositeNode c = (CompositeNode)n;
	return c.leafCount;
    }

    public Object getBranch1(Object o)
    {
	if (o == null)
	    return null;
	CompositeNode n = (CompositeNode)o;
	return n.node1;
    }

    public Object getBranch2(Object o)
    {
	if (o == null)
	    return null;
	CompositeNode n = (CompositeNode)o;
	return n.node2;
    }

    Orientation getOrientation(Object o)
    {
	if (o == null)
	    return Orientation.HORIZ;
	final CompositeNode n = (CompositeNode)o;
	return n.orientation;
    }

    public Object getLeafObject(Object o)
    {
	if (o == null)
	    return null;
	LeafNode n = (LeafNode)o;
	return n.obj;
    }
}
