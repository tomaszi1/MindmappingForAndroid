/*
 This file is part of MindMap.

    MindMap is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    MindMap is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with MindMap; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

package edu.agh.klaukold.common;

import java.util.ArrayList;
import java.util.List;


import android.graphics.Rect;


public class Root extends Box {
	private float midX;
	private float midY;
	
	//@Attribute(name="structure-class")
	//private String structure = "org.xmind.ui.map.clockwise";
	
	private List<Box> leftChildren = new ArrayList<Box>();
	private List<Box> rightChildren = new ArrayList<Box>();
	private List<Box> detached = new ArrayList<Box>();
	
	public float getMidX() {
		return midX;
	}

	public void setMidX(float midX) {
		this.midX = midX;
	}

	public float getMidY() {
		return midY;
	}

	public void setMidY(float midY) {
		this.midY = midY;
	}

	public List<Box> getLeftChildren() {
		return leftChildren;
	}

	public void setLeftChildren(List<Box> leftChildren) {
		this.leftChildren = leftChildren;
	}

	public List<Box> getRightChildren() {
		return rightChildren;
	}

	public void setRightChildren(List<Box> rightChildren) {
		this.rightChildren = rightChildren;
	}

	public List<Box> getDetached() {
		return detached;
	}

	public void setDetached(List<Box> detached) {
		this.detached = detached;
	}

	public Root() {
	}
	
	public Root(Root root) {
		super(root);
		
		for(Box box: root.leftChildren) {
			leftChildren.add(new Box(box));
		}
		
		for(Box box: root.rightChildren) {
			rightChildren.add(new Box(box));
		}
		
		for(Box box: root.detached) {
			detached.add(new Box(box));
		}
		
		children.clear();
		
		children.addAll(rightChildren);
		children.addAll(leftChildren);
		children.addAll(detached);
		
		midX = root.midX;
		midY = root.midY;
	}
	
}
