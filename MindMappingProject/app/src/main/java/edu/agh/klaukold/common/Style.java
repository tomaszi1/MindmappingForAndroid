package edu.agh.klaukold.common;

public class Style {
	public static Box root;
	public static Box nodes;
	
	public Style(Box _root, Box _nodes)
	{
		root = _root;
		if (nodes != null)
		{
			nodes = _nodes;
		}
		else
		{
			nodes = _root;
		}
	}
}
