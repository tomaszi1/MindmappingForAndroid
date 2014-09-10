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

package edu.agh.klaukold.gui;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import edu.agh.R;
import edu.agh.klaukold.common.Box;
import edu.agh.klaukold.common.Root;
import edu.agh.klaukold.common.Text;
import edu.agh.klaukold.enums.BlockShape;
import edu.agh.klaukold.enums.MapStyle;
import edu.agh.klaukold.interfaces.Command;
import edu.agh.klaukold.utilities.AsyncInvalidate;
import edu.agh.klaukold.utilities.Callback;
import edu.agh.klaukold.utilities.DialogFactory;
import edu.agh.klaukold.utilities.Utils;


import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;

public class MainActivity extends Activity {

	private GestureDetector gestureDetector;
	public static DrawView lay;
	public static ActionMode mActionMode;
	private DeleteBoxCallback callback = new DeleteBoxCallback();
	private MoveBoxCallback moveCallback = new MoveBoxCallback();
	private boolean mIsScrolling = false;
	private GestureListener gestList = new GestureListener();
	public static Root root;

	public LinkedList<Command> commands = new LinkedList<Command>();
    private Button trash;

	
	public static int id = 1;
	
	private PointF mid = new PointF();
	private ScaleGestureDetector detector;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		lay = (DrawView) findViewById(R.id.myLay);
        root = new Root();
        Intent intent = getIntent();
        String style = intent.getStringExtra(WelcomeScreen.STYLE);
        Resources res = getResources();
        if (style.equals("Default"))
        {
            Text text = new Text();
            root.setShape(BlockShape.ROUNDED_RECTANGLE);
            int color = res.getColor(R.color.light_blue);
            root.setColor(new ColorDrawable(color));
            root.setText(text);
            root.setDrawableShape((GradientDrawable)res.getDrawable(R.drawable.green_rect));

            //TODO dopisac cechy stylu
        }
        //root.draw();
	    gestureDetector = new GestureDetector(this, gestList);
//	    
	    Utils.lay = lay;
        Utils.context = this;
//	    //zeby byla czysta mapa przy wczytywaniu nowej
//	    core = null;
//	    id = 1;
//	    
//	    if(getIntent() != null && getIntent().getStringExtra("filename") != null) {
//	    	//DbAdapter.filename = getIntent().getStringExtra("filename");
//	    }
//	    
	    if(getIntent() != null && getIntent().getBooleanExtra("present", false)) {
            Callback call = new Callback() {
                @Override
                public void execute() {
                    //core = Utils.db.getCore();

                    for (Box box : root.getLeftChildren()) {
                        //	lay.addLine(core, box);
                        Utils.drawAllLines(box);
                    }

                    for (Box box : root.getRightChildren()) {
                        //lay.addLine(core, box);
                        Utils.drawAllLines(box);
                    }

//					for(Box box: core.detached) {
//						Utils.drawAllLines(box);
//					}
                }
            };
//			
            AsyncInvalidate async = new AsyncInvalidate(this);
            async.setCallback(call);
            async.execute();
        }
//	    } else if(getIntent() != null && getIntent().getBooleanExtra("import", false)) { 
//	    	Utils.loadMaps(this);
//	    }
// else {
//	    	core = new Core();
//	    	core.setText("CENTRAL BOX");
//	    	core.setId((id++)+"");
//	    	core.rect = new Rect(200, 120, 300, 220);
//		   // Utils.db.insertCore(core);
//		    lay.revalidate();
//		    core.refresh();
//		    lay.invalidate();
//	    }
//	    
//		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//		
		lay.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getActionMasked()) {
				case (MotionEvent.ACTION_OUTSIDE) :
		            return true;

				case (MotionEvent.ACTION_UP) :
					gestList.click = false;
					if(mIsScrolling) {
						String txt = "default text";
                        Text t = new Text();
                        t.setText(txt);
						gestList.myRect.setText(t);

						gestList.myRect.setParent(gestList.clicked);
						//gestList.myRect.setId(Utils.giveId()+"");

						//editContent(gestList.myRect);

//						if(gestList.clicked.getId().equals(root.getId())) {
//							if(core.mid_x < gestList.myRect.rect.left) {
//								gestList.myRect.position = Position.RIGHT;
//								lay.updateRight = true;
//							} else {
//								gestList.myRect.position = Position.LEFT;
//								lay.updateLeft = true;
//							}

							//root.addChild(gestList.myRect);
						//	lay.addLine(gestList.clicked, gestList.myRect);
						//	Utils.db.insertTopic(gestList.myRect);
						//	Utils.db.updateCore(core);
						} else {
//							if(gestList.clicked.position == Position.LEFT) {
//								gestList.myRect.position = Position.LEFT;
//								lay.updateLeft = true;
//							} else {
//								gestList.myRect.position = Position.RIGHT;
//								lay.updateRight = true;
							}

						//	gestList.clicked.addChild(gestList.myRect);
						//	lay.addLine(gestList.clicked, gestList.myRect);
							//Utils.db.insertTopic(gestList.myRect);
							//Utils.db.updateTopic(gestList.clicked);
						lay.revalidate();
						lay.invalidate();

	                    mIsScrolling = false;

	                    gestList.clicked = null;
	                    gestList.myRect = new Box();

					break;
				case MotionEvent.ACTION_POINTER_DOWN:
					// multitouch!! - touch down
					int count = event.getPointerCount(); // Number of 'fingers' in this time

				     if(count > 1) {
				    	 Box b1 = Utils.whichBox(lay, event, 0);
				    	 Box b2 = Utils.whichBox(lay, event, 1);


				    		 mIsScrolling = false;
				    		 return true;
				    	 } else {
				    		 return detector.onTouchEvent(event);
				    	 }
				case MotionEvent.ACTION_POINTER_UP:
					if(event.getPointerCount() > 1) {
						return detector.onTouchEvent(event);
					}

				     break;
				case MotionEvent.ACTION_MOVE:
					if(event.getPointerCount() > 1) {
						return detector.onTouchEvent(event);
					}

				default: break;
				}

				boolean response = gestureDetector.onTouchEvent(event);
				lay.requestFocus();
				InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(lay.getApplicationWindowToken(), 0);

				return response;
			}
		});
//		
		//menu domyślne
		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);
	    actionBar.show();
//
	    detector = new ScaleGestureDetector(this, new SimpleOnScaleGestureListener() {
			 @Override
			 public boolean onScale(ScaleGestureDetector detector) {
				 lay.setPivotX(mid.x);
				 lay.setPivotY(mid.y);
				 lay.zoomx *= detector.getScaleFactor();
				 lay.zoomy *= detector.getScaleFactor();
				 lay.revalidate();
				 lay.invalidate();
				 return true;
			 }
		});
	}
	

	//tutaj rozpoznajemy przytrzymanie, jedno kliknięcie, dwa kliknięcia
	private class GestureListener extends GestureDetector.SimpleOnGestureListener {
		Box myRect = new Box();
		Box clicked;
		boolean click = false;
		
        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            if(mActionMode == null && clicked != null) {
            	if(clicked.isVisible()) {
            		//clicked.changeDescendantsVisibility();
            	}
            	clicked = null;
            	lay.revalidate();
            	lay.invalidate();
            }
//            
           return true;
        }
        
		@Override
		public void onLongPress(MotionEvent e) {
			if(!click || Utils.whichBox(lay, e) == null) {
				return;
			}
			
			if(mActionMode == null) {
				//mActionMode = startActionMode(callback);
				mActionMode.setTitle("Delete");
			}

			if(mActionMode.getTitle().toString().equalsIgnoreCase("delete")) {
				boolean b = clicked.isSelected();
				if(b) {
					//callback.removeObserver(clicked);
				} else {
					//callback.addObserver(clicked);
				}
				
				lay.invalidate();
			}
		}
		
	    @Override
	    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
	    	if(mActionMode != null) {
	    		if(clicked != null && click) {
	    			int newx = (int) (e2.getX()-e1.getX());
	        		int newy = (int) (e2.getY()-e1.getY());

	        		newx /= lay.zoomx;
	        		newy /= lay.zoomy;

	        		newx = (int) -distanceX;
	        		newy = (int) -distanceY;

	        		Utils.moveChildX(clicked, newx);
	        		Utils.moveChildY(clicked, newy);
	    	        lay.revalidate();
	    	        lay.invalidate();
	    			return true;
	    		}
	    	} else if(click && clicked != null) {
	        	mIsScrolling = true;
        		int newx = (int) (e2.getX()-lay.transx);
        		int newy = (int) (e2.getY()-lay.transy);

        		newx /= lay.zoomx;
        		newy /= lay.zoomy;

                Rect r = new Rect(newx, newy, newx + 100, newy + 50);

        		//myRect.rect.set();
        		return false;
	        }

	        lay.transx -= distanceX;
	        lay.transy -= distanceY;
	        lay.invalidate();
	        return true;
	    }
	    
	    @Override
	    public boolean onDown(MotionEvent e) {
	    	if(mActionMode != null && mActionMode.getTitle().toString().equalsIgnoreCase("move")) {
	    		if(Utils.whichBox(lay, e) == clicked) {
	    			click = true;
	    		}
	    	} else {
	    		clicked = Utils.whichBox(lay, e);
	    		if(clicked != null) {
	    			click = true;
	    		}
	    	}
			
	        return true;
	    }

	    @Override
	    public boolean onDoubleTap(MotionEvent e) {
//	    	if(mActionMode != null && mActionMode.getTitle().toString().equalsIgnoreCase("move")) {
//	    		return true;
//	    	}
//	    	
//	    	if(Utils.whichBox(lay, e) != null) {
//	    		editContent(Utils.whichBox(lay, e));
//	        	return true;
//	    	}
//	    	
//	        final Dialog dialog = DialogFactory.boxContentDialog(MainActivity.this);
//	        final EditText et = (EditText) dialog.findViewById(R.id.editText);
//	        et.requestFocus();
//
//	        final Box myClicked = new Box();
//	        myClicked.create(Utils.getCoordsInView(lay, e, 0));
//	        myClicked.setText("default text");
//	        
//	        if(core.mid_x < (myClicked.rect.left + myClicked.rect.right)/2) {
//	        	myClicked.position = Position.RIGHT;
//				lay.updateRight = true;
//			} else {
//				myClicked.position = Position.LEFT;
//				lay.updateLeft = true;
//			}
//	        
//	       // myClicked.setId(Utils.giveId()+"");
//	        MainActivity.core.addChild(myClicked);
//	        
//	        myClicked.setTimestamp(new Date().getTime());
//	        
//	       // lay.triggerLastDetachMove();
//	        lay.revalidate();
//	        
//	        try {
//	        	Callback call = new Callback() {
//					@Override
//					public void execute() {
//						//.db.insertTopic(myClicked);
//				        //Utils.db.updateCore(core);
//					}
//				};
//				
//				AsyncInvalidate async = new AsyncInvalidate(MainActivity.this);
//				async.setCallback(call);
//				async.execute();
//			} catch (Exception e1) {
//				e1.printStackTrace();
//			}
//	        
//	        editContent(myClicked);
	        return true;
	    }
	}
	
	private class DeleteBoxCallback implements ActionMode.Callback {
		List<Box> observers = new ArrayList<Box>();
		
		public void addObserver(Box box) {
			if(!observers.contains(box)) {
				box.setSelected(true);
				observers.add(box);
			}
		}
		
		public void removeObserver(Box box) {
			observers.remove(box);
			box.setSelected(false);
			if(observers.isEmpty()) {
				mActionMode.finish();
			}
		}
		
		private void notifyObservers() {
			for(Box box: observers) {
        		box.setSelected(false);
        	}
		}
		
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // inflate contextual menu
            mode.getMenuInflater().inflate(R.menu.context_menu, menu);
            return true;
        }
 
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }
 
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//            switch (item.getItemId()) {
//            	case R.id.menu_delete:
//            		
//            	for(Box v: observers) {
//            		if(v instanceof Core) {
//            			continue;
//            		}
//            		
//            		//lay.deleteHimAndChildren(v);
//            		//Utils.db.deleteChild(v.getId());
//            	}
//            	
//                mode.finish(); // Action picked, so close the CAB
//                return true;
//            default:
//                return false;
//            }
        	return true;
        }
 
        @Override
        public void onDestroyActionMode(ActionMode mode) {
//            // remove selection
//        	notifyObservers();
//        	observers.clear();
//        	
//        	lay.revalidate();
//        	lay.invalidate();
//        	mActionMode = null;
        }
    }
	
	private class MoveBoxCallback implements ActionMode.Callback {
		Box observer;
		
		public void setObserver(Box box) {
		}
		
		public void removeObserver() {
		}
		
		private void notifyObserver() {
		}
		
		private void determinePosition() { }
		
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            return true;
        }
 
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }
 
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        	return true;
        }
 
        @Override
        public void onDestroyActionMode(ActionMode mode) {
        }
	}
	private void editContent(final Box myClicked) {
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_menu, menu);
        return super.onCreateOptionsMenu(menu);
	}
	//}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        return true;
	} 
}

