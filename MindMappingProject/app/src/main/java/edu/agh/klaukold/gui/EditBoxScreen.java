package edu.agh.klaukold.gui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;

import edu.agh.R;
import edu.agh.klaukold.common.Text;
import edu.agh.klaukold.enums.BlockShape;
import edu.agh.klaukold.enums.LineStyle;

public class EditBoxScreen extends Activity {

	private Spinner boxShape;
	private Spinner lineShape;
	private Spinner lineThickness;
	private ColorPalette colorBoxPalette;
	private ColorPalette colorLinePalette;
	private Spinner textHeight;
	private Spinner textAlign;
	private Spinner font;
	private CheckBox italic;
	private CheckBox bold;
	private CheckBox strikeOut;
    private int BoxColor;
    private int  TextColor;
    private int LineColor;

    public static String BOX_COLOR = "BOX COLOR";
    public static String TEXT_COLOR = "TEXT COLOR";
    public static String LINE_COLOR = "LINE COLOR";
    public static String BOX_SHAPE = "BOX_SHAPE";
    public static  String LINE_SHAPE = "LINE_SHAPE";
    public static View BOXCOLOR;
    public static  View TEXTCOLOR;
    public static  View LINECOLOR;
    public BlockShape blockShape;
    public LineStyle lineStyle;

	@Override
	public void onResume() {
        super.onResume();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_box);
        Intent intent = getIntent();
        BoxColor = intent.getIntExtra(BOX_COLOR, 0);
        TextColor = intent.getIntExtra(TEXT_COLOR, 0);
        LineColor = intent.getIntExtra(LINE_COLOR, 0);
        BOXCOLOR = (View) findViewById(R.id.box_color);
        ((GradientDrawable)BOXCOLOR.getBackground()).setColor(BoxColor);
        TEXTCOLOR = (View) findViewById(R.id.text_color);
        ((GradientDrawable)TEXTCOLOR.getBackground()).setColor(TextColor);
        LINECOLOR = (View) findViewById(R.id.line_color);
        boxShape = (Spinner) findViewById(R.id.spinnerBoxShapes);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.shapes_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        boxShape.setAdapter(adapter);
        //dodanie lisener'a do spinnera
        boxShape.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 0)
                {
                    blockShape = BlockShape.ELLIPSE;
                } else if (position == 1)
                {
                    blockShape = BlockShape.ROUNDED_RECTANGLE;
                } else if (position == 2) {
                    blockShape = BlockShape.RECTANGLE;
                } else if (position == 3) {
                    blockShape = BlockShape.DIAMOND;
                } else if (position == 4) {
                    blockShape = BlockShape.UNDERLINE;
                } else if (position == 5) {
                    blockShape = BlockShape.NO_BORDER;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        blockShape = (BlockShape) intent.getSerializableExtra(BOX_SHAPE);
        if (blockShape ==  BlockShape.RECTANGLE) {
            boxShape.setSelection(2);
        } else if (blockShape == BlockShape.ROUNDED_RECTANGLE)
        {
            boxShape.setSelection(1);
        }
        else if (blockShape== BlockShape.ELLIPSE)
        {
            boxShape.setSelection(0);
        }
        else if (blockShape == BlockShape.DIAMOND)
        {
            boxShape.setSelection(3);
        }
        else if (blockShape == BlockShape.NO_BORDER)
        {
            boxShape.setSelection(5);
        }
        else if (blockShape == BlockShape.UNDERLINE)
        {
            boxShape.setSelection(4);
        }
        lineShape  = (Spinner) findViewById(R.id.spinnerLines);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.lines_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        lineShape.setAdapter(adapter1);
        //dodanie lisener'a do spinnera
        lineShape.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 0)
                {
                    lineStyle = LineStyle.CURVE;
                } else if (position == 1)
                {
                    lineStyle = LineStyle.STRAIGHT;
                } else if (position == 2) {
                    lineStyle = LineStyle.ARROWED_CURVE;
                } else if (position == 3) {
                    lineStyle = LineStyle.ELBOW;
                } else if (position == 4) {
                    lineStyle = LineStyle.ROUNDED_ELBOW;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        lineStyle = (LineStyle) intent.getSerializableExtra(LINE_SHAPE);
        if (lineStyle == LineStyle.CURVE)
        {
            lineShape.setSelection(0);
        } else if (lineStyle == LineStyle.STRAIGHT) {
            lineShape.setSelection(1);
        } else if (lineStyle == LineStyle.ARROWED_CURVE) {
            lineShape.setSelection(2);
        } else if (lineStyle == LineStyle.ELBOW) {
            lineShape.setSelection(3);
        } else if (lineStyle == LineStyle.ROUNDED_ELBOW) {
            lineShape.setSelection(4);
        }

    }
	
	@Override
	public void onDestroy() {
        super.onDestroy();
	}
}
