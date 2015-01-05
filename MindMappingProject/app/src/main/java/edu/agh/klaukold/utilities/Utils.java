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

package edu.agh.klaukold.utilities;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;


import edu.agh.R;
import edu.agh.klaukold.common.Box;
import edu.agh.klaukold.common.Line;
import edu.agh.klaukold.common.Point;
import edu.agh.klaukold.enums.Actions;
import edu.agh.klaukold.gui.DrawView;
import edu.agh.klaukold.gui.MainActivity;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;

import org.xmind.core.IRelationship;
import org.xmind.core.ITopic;
import org.xmind.core.style.IStyle;
import org.xmind.core.style.IStyleSheet;
import org.xmind.ui.style.Styles;

public class Utils {
	public static DrawView lay;
	public static Activity context;
	static String base;
    private static int i = 0;

    public static void calculateAll() {
        Queue<Box> q1 = new LinkedList<Box>();
        Queue<Box> q2 = new LinkedList<Box>();

        for (int i=0;  i< MainActivity.root.getChildren().size()/2; i++) {
            q1.add(MainActivity.root.getChildren().get(i));
        }

        for (int i=MainActivity.root.getChildren().size()/2;  i< MainActivity.root.getChildren().size(); i++) {
            q2.add(MainActivity.root.getChildren().get(i));
        }

        calculatePosition(q2, MainActivity.root, true);
        calculatePosition(q1, MainActivity.root, false);
    }

    public static void calculatePosition(Queue<Box> queue, Box parent, Boolean left) {
        int sum = 0;
        HashMap< Box, Integer> hights = new HashMap<Box, Integer>();
        for (Box b : queue) {
            int single = calculateHight(b);
            hights.put(b, new Integer(single));
            sum += single;
        }
        int fstart = parent.drawableShape.getBounds().centerY() - sum/2;
        IStyle parentStyle = MainActivity.workbook.getStyleSheet().findStyle(parent.topic.getStyleId());
        String shape = null;
        if (!parent.topic.isRoot()) {
            shape = Styles.BRANCH_CONN_STRAIGHT;
        }
        String width = null;
        String color = null;
        if (parentStyle != null) {
            if (parent.topic.isRoot()) {
                shape = parentStyle.getProperty(Styles.LineClass);
            } else {
                shape = Styles.BRANCH_CONN_STRAIGHT;
            }
            width = parentStyle.getProperty(Styles.LineWidth);
            if (width == null) {
                width = "1";
            } else {
                width =  width.substring(0, parentStyle.getProperty(Styles.LineWidth).length() - 2);
            }
            color = parentStyle.getProperty(Styles.LineColor);
            if (color == null) {
                color = "#A0A0A0";
            }
        } else {
            width = "1";
            color = "#A0A0A0";
        }
        if (left) {
            for (Box b : hights.keySet()) {
                fstart += hights.get(b).intValue()/2;
                if (parent.topic.isRoot()) {
                    b.point.x = parent.getDrawableShape().getBounds().left - (b.getWidth() + 200);
                } else {
                    b.point.x = parent.getDrawableShape().getBounds().left - (b.getWidth() + 50);
                }
                b.point.y = fstart;
                Line l;
                l = new Line(shape, Integer.parseInt(width), new ColorDrawable(Color.parseColor(color)),
                        new Point(parent.getDrawableShape().getBounds().left, parent.getDrawableShape().getBounds().centerY()),
                        new Point(b.getDrawableShape().getBounds().right, b.getDrawableShape().getBounds().centerY()), true);
                parent.getLines().put(b, l);
                fstart += hights.get(b).intValue()/2;
                Queue<Box> q = b.getChildren();
                calculatePosition(q, b, true);
            }
        } else {
            for (Box b : hights.keySet()) {
                fstart += hights.get(b).intValue()/2;
                if (parent.topic.isRoot()) {
                    b.point.x = parent.getDrawableShape().getBounds().right + 200;
                } else {
                    b.point.x = parent.getDrawableShape().getBounds().right + 50;
                }
                b.point.y = fstart;
                Line l;
                l = new Line(shape, Integer.parseInt(width), new ColorDrawable(Color.parseColor(color)),
                        new Point(parent.getDrawableShape().getBounds().right, parent.getDrawableShape().getBounds().centerY()),
                        new Point(b.getDrawableShape().getBounds().left, b.getDrawableShape().getBounds().centerY()), true);
                parent.getLines().put(b, l);
                fstart += hights.get(b).intValue()/2;
                Queue<Box> q = b.getChildren();
                calculatePosition(q, b, false);
            }
        }
    }

    public static int calculateHight(Box box) {
        box.prepareDrawableShape();
        lay.calculateBoxSize(box);
        int hight = box.getHeight() + 50;
        if (box.getChildren().size() > 1) {
            for (Box b : box.getChildren()) {
                hight += calculateHight(b);
            }
        }
        return hight;
    }

    public static  IRelationship findRelationship(Box box1, Box box2) {
        Iterator<IRelationship> iterator = MainActivity.sheet1.getRelationships().iterator();
        while(iterator.hasNext()) {
            IRelationship rel = iterator.next();
            if ((rel.getEnd1Id().equals(box1.topic.getId()) && rel.getEnd2Id().equals(box2.topic.getId())) || (rel.getEnd2Id().equals(box1.topic.getId()) && rel.getEnd1Id().equals(box2.topic.getId()))){
                return rel;
            }
        }
        return null;
    }

    public static  void findRelationships(HashMap<String, Box> boxes) {
        Iterator<IRelationship> iterator = MainActivity.sheet1.getRelationships().iterator();
        while(iterator.hasNext()) {
            IRelationship rel = iterator.next();
            for (String t : boxes.keySet()) {
                if (rel.getEnd1Id().equals(t)) {
                    boxes.get(t).relationships.put( rel, boxes.get(rel.getEnd2Id()));
                }
            }
        }
    }

    public static Pair<Box, IRelationship> whichRelationship(DrawView draw, MotionEvent event, int id) {
        float[] mClickCoords = getCoordsInView(draw, event, id);

        int x = (int) mClickCoords[0];
        int y = (int) mClickCoords[1];

        for (Path p : MainActivity.allRelationship.keySet()){
            RectF rectF = new RectF();
            p.computeBounds(rectF, true);
            if (rectF.contains(x,y)) {
                return new Pair<Box, IRelationship>(MainActivity.allRelationship.get(p).second, MainActivity.allRelationship.get(p).first);
            }
        }
        return null;
    }

    public static void fireUnSelect(Box box) {
        box.isSelected = (false);
        for (Box b : box.getChildren()) {
            box.isSelected = (false);
            fireUnSelect(b);
        }
    }

    public static void fireAddSubtopic(Box p, HashMap<String, Box> boxes) {
        for (ITopic t : p.topic.getAllChildren()) {
            Box b = new Box();
            b.setWidth(70);
            b.setHeight(50);
            b.topic = t;
            b.setDrawableShape((GradientDrawable) MainActivity.res.getDrawable(R.drawable.round_rect));
            b.parent = p;
            b.point = new edu.agh.klaukold.common.Point();
            p.addChild(b);
            p.topic.add(b.topic, 0, ITopic.ATTACHED);
            boxes.put(t.getId(), b);
            fireAddSubtopic(b, boxes);
        }
    }
	
	public static void  fireSetVisible(Box box, Boolean visible) {
        box.topic.setFolded(visible);
        for (Box b : box.getLines().keySet()) {
            b.topic.setFolded(visible);
            box.getLines().get(b).setVisible(visible);
            fireSetVisible(b, visible);
        }
    }
	

	//Który bloczek został kliknięty palcem o indeksie id
	public static Box whichBox(DrawView draw, MotionEvent event, int id) {
		float[] mClickCoords = getCoordsInView(draw, event, id);

		int x = (int) mClickCoords[0];
		int y = (int) mClickCoords[1];
		
		Box c = MainActivity.root;
				
		
		if(c.getDrawableShape().getBounds().contains(x, y)) {
			return c;
		}
		
		//BFS do przejścia drzewa
		
		Queue<Box> q= new LinkedList<Box>();
		
		for(Box b: c.getChildren()) {
			q.add(b);
		}

		while(!q.isEmpty()) {
			Box box = q.remove();
			
			if(box.topic.getParent() == null || !box.topic.getParent().isFolded()) {
				Rect rec = box.getDrawableShape().getBounds();
				if(rec.contains(x, y)) {
					q.clear();
					return box;
				}
				
				for(Box b: box.getChildren()) {
					q.add(b);
				}
			}
		}
		return null;
	}
    public static Pair<Box, Actions> IsBoxAction(DrawView draw, MotionEvent event, int id) {
        float[] mClickCoords = getCoordsInView(draw, event, id);

        int x = (int) mClickCoords[0];
        int y = (int) mClickCoords[1];
        Queue<Box> q = (Queue<Box>) MainActivity.toEditBoxes.clone();
        while(!q.isEmpty()) {
            Box box = q.remove();

            if(box.topic.getParent() == null || !box.topic.getParent().isFolded()) {
        if(box.addBox.getBounds().contains(x, y)) {
            Pair p = new Pair(box, Actions.ADD_BOX);
            q.clear();
            return p;
        }
                for(Box b: box.getChildren()) {
                    q.add(b);
                }
            }
        }
        return null;
    }

    public static Pair<Box, Actions> whichBoxAction(DrawView draw, MotionEvent event) {
        return whichBoxAction(draw, event, 0);
    }
    public static Pair<Box, Actions> whichBoxAction(DrawView draw, MotionEvent event, int id) {
        float[] mClickCoords = getCoordsInView(draw, event, id);

        int x = (int) mClickCoords[0];
        int y = (int) mClickCoords[1];


        //BFS do przejścia drzewa
        Queue<Box> q= (Queue<Box>) MainActivity.toEditBoxes.clone();
        if (MainActivity.root.newNote != null && (MainActivity.root.newNote.getBounds().contains(x,y))) {
            Pair p = new Pair(MainActivity.root, Actions.NEW_NOTE);
            q.clear();
            return p;
        }

        for (Box box : MainActivity.root.getChildren()) {
            q.add(box);
        }

        while(!q.isEmpty()) {
            Box box = q.remove();

            if(box.topic.getParent() == null || !box.topic.getParent().isFolded()) {
                if (box.newNote != null && box.newNote.getBounds().contains(x, y)) {
                    Pair p = new Pair(box, Actions.NEW_NOTE);
                    q.clear();
                    return p;
                }  else if(box.collapseAction != null && box.collapseAction.getBounds().contains(x, y)) {
                    Pair p = new Pair(box, Actions.COLLAPSE);
                    box.collapseAction = null;
                    q.clear();
                    return p;
                } else if(box.expandAction != null && box.expandAction.getBounds().contains(x, y)) {
                    Pair p = new Pair(box, Actions.EXPAND);
                    box.expandAction = null;
                    q.clear();
                    return  p;
                } else if (box.addNote != null && box.addNote.getBounds().contains(x, y)) {
                    Pair p = new Pair(box, Actions.ADD_NOTE);
                    return  p;
                }

                for(Box b: box.getChildren()) {
                    q.add(b);
                }
            }
        }
        return null;
    }

	public static Box whichBox(DrawView draw, MotionEvent event) {
		return whichBox(draw, event, 0);
	}
	
	public static float[] getCoordsInView(DrawView draw, MotionEvent event, int id) {
		int mActivePointerId = event.getPointerId(id);
		int index = event.findPointerIndex(mActivePointerId);
		
		return getCoordsInView(draw, new float[] {event.getX(index), event.getY(index)});
	}
	
	//Transformacja współrzędnych na te na akeranie
	public static float[] getCoordsInView(DrawView draw, float[] coords) {
		float[] mClickCoords = new float[2];
		
		mClickCoords[0] = coords[0];
		mClickCoords[1] = coords[1];
		Matrix matrix = new Matrix();
		matrix.set(draw.getMatrix());
		
		matrix.preTranslate(draw.transx, draw.transy);
		matrix.preScale(draw.zoomx, draw.zoomy);
		matrix.invert(matrix); 
		matrix.mapPoints(mClickCoords);
		
		return mClickCoords;
	}
	
	//Transformacja współrzędnych z ekranu na normalne
	public static float[] getCoordsFromView(DrawView draw, float[] mClickCoords) {
		Matrix matrix = new Matrix();
		matrix.set(draw.getMatrix());
		
		matrix.preTranslate(-draw.transx,-draw.transy);
		matrix.preScale(1/draw.zoomx, 1/draw.zoomy);
		matrix.invert(matrix); 
		matrix.mapPoints(mClickCoords);
		
		return mClickCoords;
	}
	
	//Rozpięcie klocka od rodzica i przesunięcie go do współrzędnych w coords
	public static void unlink(Box child, float[] coords) {
//		child.parent.removeChild(child);
//		child.xmlPosition = new XmlPosition();
//		child.xmlPosition.x = (int) coords[0];
//		child.xmlPosition.y = (int) coords[1];
//		//Utils.db.updateTopic(child);
//		Utils.moveChildX(child, child.xmlPosition.x - child.rect.left);
//		Utils.moveChildY(child, child.xmlPosition.y - child.rect.top);
//		lay.deleteLine(child, child.parent);
//		
//		child.parent = MainActivity.core;
//		if((child.rect.left + child.rect.right)/2 > (child.parent.rect.left + child.parent.rect.right)/2) {
//			propagatePosition(child, Position.RIGHT);
//		} else {
//			propagatePosition(child, Position.LEFT);
//		}
//		child.parent.addChild(child);
//		//Utils.db.changeParent(child);
//	}
//	
//	//zmiana rodzica na newParent, sprzątanie w bazie dla starego rodzica i uaktualnienie nowego i dziecka
//	public static boolean changeParent(Box child, Box newParent) {
//		if(child instanceof Core || (child.parent == newParent && child.xmlPosition == null)) {
//			return false;
//		}
//		
//		if(child.parent != null) {
//			lay.deleteLine(child, child.parent);
//			child.parent.removeChild(child);
//		}
//		
//		child.parent = newParent;
//		
//		if(child.xmlPosition != null) {
//			child.xmlPosition = null;
//			//Utils.db.updateTopic(child);
//		}
//		
//		if(newParent instanceof Core) {
//			if((child.rect.left + child.rect.right)/2 > (newParent.rect.left + newParent.rect.right)/2) {
//				propagatePosition(child, Position.RIGHT);
//			} else {
//				propagatePosition(child, Position.LEFT);
//			}
//			newParent.addChild(child);
//		} else {
//			propagatePosition(child, newParent.position);
//			newParent.addChild(child);
//		}
//		
//		child.color = newParent.color;
//		lay.addLine(newParent, child);
//		
//		//Utils.db.changeParent(child);
//		lay.updateLineForChildren(child);
//		
		//return true;
	}
	
	//propagacja orientacji
    public static void propagatePosition(Box box, Point point) {
        box.setPoint(point);
        for(Box my: box.getChildren()) {
            propagatePosition(my, point);
        }
    }

    //propagacja orientacji
    public static void propagatePosition(Box box) {
       // box.setPoint(point);
        for(Box my: box.getChildren()) {
            propagatePosition(my);
        }
    }
	
	//przesuwanie bloczka z potomkami w płaszczyźnie poziomej
	public static void moveChildX(Box b, int diff) {
		for(Box bx: b.getChildren()) {
			moveChildX(bx, diff);
		}
		b.getDrawableShape().getBounds().left += diff;
		b.getDrawableShape().getBounds().right += diff;
	}
	
	//przesuwanie bloczka z potomkami w płaszczyźnie poziomej
	public static void moveChildY(Box b, int diff) {
		for(Box bx: b.getChildren()) {
			moveChildY(bx, diff);
		}
		b.getDrawableShape().getBounds().top += diff;
        b.getDrawableShape().getBounds().bottom += diff;
	}
	
	public static int countLines(String s) {
		int counter = 0;
		
		for(int i = 0; i < s.length(); i++) {
			if(s.charAt(i) == '\n') {
				counter++;
			}
		}
		
		return counter;
	}
	
	private static void insertHimAndChildren(Box parent) {
		for(Box box: parent.getChildren()) {
			insertHimAndChildren(box);
		}
	}
	
	public static void drawAllLines(Box box) {
		for(Box b: box.getChildren()) {
			//lay.addLine(box, b);
		}
		
		for(Box b: box.getChildren()) {
			drawAllLines(b);
		}
	}


}
