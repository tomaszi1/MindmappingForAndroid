package edu.agh.klaukold.gui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;

import java.util.Properties;

import edu.agh.R;
import edu.agh.klaukold.commands.EditBox;
import edu.agh.klaukold.common.Box;
import edu.agh.klaukold.common.Text;
import edu.agh.klaukold.enums.Align;
import edu.agh.klaukold.enums.BlockShape;
import edu.agh.klaukold.enums.LineStyle;
import edu.agh.klaukold.enums.LineThickness;

public class EditBoxScreen extends Activity {

    private Spinner boxShape;
    private Spinner lineShape;
    private Spinner lineThickness;
    private Spinner textHeight;
    private Spinner textAlign;
    private Spinner font;

    private ColorPalette colorBoxPalette;
    private ColorPalette colorLinePalette;
    private CheckBox italic;
    private CheckBox bold;
    private CheckBox strikeOut;
    private int BoxColor;
    private int TextColor;
    private int LineColor;
    private LineThickness lineThicknessEnum;

    public static String BOX_COLOR = "BOX COLOR";
    public static String TEXT_COLOR = "TEXT COLOR";
    public static String LINE_COLOR = "LINE COLOR";
    public static String BOX_SHAPE = "BOX_SHAPE";
    public static String LINE_SHAPE = "LINE_SHAPE";
    public static String ACTIVITY_TYPE = "ADD_BOX";
    public static String ACTIVITY_TYPE1 = "EDIT_TEXT_COLOR";
    public static String ACTIVITY_TYPE2 = "EDIT_LINE_COLOR";
    public static String LINE_THICKNESS = "LINE_THICKNESS";
    public static String BOX = "BOX";
    public static View BOXCOLOR;
    public static View TEXTCOLOR;
    public static View LINECOLOR;
    public BlockShape blockShape;
    public LineStyle lineStyle;
    public static Box box;

    @Override
    public void onResume() {
        super.onResume();
        ((GradientDrawable) BOXCOLOR.getBackground()).setColor(MainActivity.boxEdited.getColor().getColor());
        ((GradientDrawable) TEXTCOLOR.getBackground()).setColor(MainActivity.boxEdited.getText().getColor().getColor());
        ((GradientDrawable) LINECOLOR.getBackground()).setColor(MainActivity.boxEdited.getLineColor());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_box);
        Intent intent = getIntent();
        //box = (Box)intent.getSerializableExtra(EditBoxScreen.BOX);
        BoxColor = intent.getIntExtra(BOX_COLOR, 0);
        TextColor = intent.getIntExtra(TEXT_COLOR, 0);
        LineColor = intent.getIntExtra(LINE_COLOR, 0);
        BOXCOLOR = (View) findViewById(R.id.box_color);
        ((GradientDrawable) BOXCOLOR.getBackground()).setColor(BoxColor);
        BOXCOLOR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditBoxScreen.this, ColorPalette.class);
                intent.putExtra("ACTIVITY", ACTIVITY_TYPE);
                startActivity(intent);
            }
        });
        TEXTCOLOR = (View) findViewById(R.id.text_color);
        ((GradientDrawable) TEXTCOLOR.getBackground()).setColor(TextColor);
        TEXTCOLOR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditBoxScreen.this, ColorPalette.class);
                intent.putExtra("ACTIVITY", ACTIVITY_TYPE1);
                startActivity(intent);
            }
        });
        LINECOLOR = (View) findViewById(R.id.line_color);
        ((GradientDrawable) LINECOLOR.getBackground()).setColor(LineColor);
        LINECOLOR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditBoxScreen.this, ColorPalette.class);
                intent.putExtra("ACTIVITY", ACTIVITY_TYPE2);
                startActivity(intent);
            }
        });
        boxShape = (Spinner) findViewById(R.id.spinnerBoxShapes);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.shapes_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        boxShape.setAdapter(adapter);
        blockShape = (BlockShape) intent.getSerializableExtra(BOX_SHAPE);
        if (blockShape == BlockShape.RECTANGLE) {
            boxShape.setSelection(2);
        } else if (blockShape == BlockShape.ROUNDED_RECTANGLE) {
            boxShape.setSelection(1);
        } else if (blockShape == BlockShape.ELLIPSE) {
            boxShape.setSelection(0);
        } else if (blockShape == BlockShape.DIAMOND) {
            boxShape.setSelection(3);
        } else if (blockShape == BlockShape.NO_BORDER) {
            boxShape.setSelection(5);
        } else if (blockShape == BlockShape.UNDERLINE) {
            boxShape.setSelection(4);
        }
        //dodanie lisener'a do spinnera
        boxShape.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (boxShape.getSelectedItem().toString().equals("Ellipse")) {
                    blockShape = BlockShape.ELLIPSE;
                } else if (boxShape.getSelectedItem().toString().equals("Rounded Rectangle")) {
                    blockShape = BlockShape.ROUNDED_RECTANGLE;
                } else if (boxShape.getSelectedItem().toString().equals("Rectangle")) {
                    blockShape = BlockShape.RECTANGLE;
                } else if (boxShape.getSelectedItem().toString().equals("Diamond")) {
                    blockShape = BlockShape.DIAMOND;
                } else if (boxShape.getSelectedItem().toString().equals("Underline")) {
                    blockShape = BlockShape.UNDERLINE;
                } else if (boxShape.getSelectedItem().toString().equals("No Border")) {
                    blockShape = BlockShape.NO_BORDER;
                }
                Properties properties = new Properties();
                properties.put("box", MainActivity.boxEdited);
                properties.put("shape", blockShape);
                properties.put("boxes", MainActivity.toEditBoxes);
                EditBox editBox = new EditBox();
                editBox.execute(properties);
                MainActivity.addCommendUndo(editBox);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        lineShape = (Spinner) findViewById(R.id.spinnerLines);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.lines_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        lineShape.setAdapter(adapter1);
        //dodanie lisener'a do spinnera
        lineShape.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (lineShape.getSelectedItem().toString().equals("Curve")) {
                    lineStyle = LineStyle.CURVE;
                } else if (lineShape.getSelectedItem().toString().equals("Straight")) {
                    lineStyle = LineStyle.STRAIGHT;
//                } else if (lineShape.getSelectedItem().toString().equals("Arrowed Curve")) {
//                    lineStyle = LineStyle.ARROWED_CURVE;
                } else if (lineShape.getSelectedItem().toString().equals("Elbow")) {
                    lineStyle = LineStyle.ELBOW;
                } else if (lineShape.getSelectedItem().toString().equals("Rounded Elbow")) {
                    lineStyle = LineStyle.ROUNDED_ELBOW;
                }
                EditBox editBox = new EditBox();
                Properties properties = new Properties();
                properties.put("box", MainActivity.boxEdited);
                properties.put("line_shape", lineStyle);
                properties.put("boxes", MainActivity.toEditBoxes);
                editBox.execute(properties);
                MainActivity.addCommendUndo(editBox);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        lineStyle = (LineStyle) intent.getSerializableExtra(LINE_SHAPE);
        if (lineStyle == LineStyle.CURVE) {
            lineShape.setSelection(0);
        } else if (lineStyle == LineStyle.STRAIGHT) {
            lineShape.setSelection(1);
//        } else if (lineStyle == LineStyle.ARROWED_CURVE) {
//            lineShape.setSelection(2);
        } else if (lineStyle == LineStyle.ELBOW) {
            lineShape.setSelection(2);
        } else if (lineStyle == LineStyle.ROUNDED_ELBOW) {
            lineShape.setSelection(3);
        }

        lineThickness = (Spinner) findViewById(R.id.spinnerThin);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.thin_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        lineThickness.setAdapter(adapter2);
        lineThicknessEnum = (LineThickness) intent.getSerializableExtra(EditBoxScreen.LINE_THICKNESS);
        if (lineThicknessEnum == LineThickness.THINNEST) {
            lineThickness.setSelection(0);
        } else if (lineThicknessEnum == LineThickness.THIN) {
            lineThickness.setSelection(1);
        } else if (lineThicknessEnum == LineThickness.MEDIUM) {
            lineThickness.setSelection(2);
        } else if (lineThicknessEnum == LineThickness.FAT) {
            lineThickness.setSelection(3);
        } else if (lineThicknessEnum == LineThickness.FATTEST) {
            lineThickness.setSelection(4);
        }

        lineThickness.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LineThickness lt = LineThickness.THINNEST;
                if (lineThickness.getSelectedItem().toString().equals("Thinnest")) {
                    lt = LineThickness.THINNEST;
                } else if (lineThickness.getSelectedItem().toString().equals("Thin")) {
                    lt = (LineThickness.THIN);
                } else if (lineThickness.getSelectedItem().toString().equals("Medium")) {
                    lt = (LineThickness.MEDIUM);
                } else if (lineThickness.getSelectedItem().toString().equals("Fat")) {
                    lt = (LineThickness.FAT);
                } else if (lineThickness.getSelectedItem().toString().equals("Fattest")) {
                    lt = (LineThickness.FATTEST);
                }
                EditBox editBox = new EditBox();
                Properties properties = new Properties();
                properties.put("box", MainActivity.boxEdited);
                properties.put("line_thickness", lt);
                properties.put("boxes", MainActivity.toEditBoxes);
                editBox.execute(properties);
                MainActivity.addCommendUndo(editBox);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        textAlign = (Spinner) findViewById(R.id.spinnerAlign);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,
                R.array.align_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        textAlign.setAdapter(adapter3);
        if (MainActivity.boxEdited.getText().getAlign() == Align.RIGHT) {
            textAlign.setSelection(0);
        } else if (MainActivity.boxEdited.getText().getAlign() == Align.CENTER) {
            textAlign.setSelection(2);
        } else if (MainActivity.boxEdited.getText().getAlign() == Align.LEFT) {
            textAlign.setSelection(1);
        }
        textAlign.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Align align = Align.CENTER;
                if (textAlign.getSelectedItem().toString().equals("Right")) {
                    align = (Align.RIGHT);
                } else if (textAlign.getSelectedItem().toString().equals("Center")) {
                    align = (Align.CENTER);
                } else if (textAlign.getSelectedItem().toString().equals("Left")) {
                    align = (Align.LEFT);
                }
                EditBox editBox = new EditBox();
                Properties properties = new Properties();
                try {
                    Text text = MainActivity.boxEdited.getText().TextClone();
                    text.setAlign(align);
                    properties.put("box_text", text);
                    properties.put("box", MainActivity.boxEdited);
                    properties.put("boxes", MainActivity.toEditBoxes);
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
                editBox.execute(properties);
                MainActivity.addCommendUndo(editBox);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        textHeight = (Spinner) findViewById(R.id.spinnerHeigth);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(this,
                R.array.height_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        textHeight.setAdapter(adapter4);
        if (MainActivity.boxEdited.getText().getSize() == 8) {
            textHeight.setSelection(0);
        } else if (MainActivity.boxEdited.getText().getSize() == 9) {
            textHeight.setSelection(1);
        } else if (MainActivity.boxEdited.getText().getSize() == 10) {
            textHeight.setSelection(2);
        } else if (MainActivity.boxEdited.getText().getSize() == 11) {
            textHeight.setSelection(3);
        } else if (MainActivity.boxEdited.getText().getSize() == 12) {
            textHeight.setSelection(4);
        } else if (MainActivity.boxEdited.getText().getSize() == 13) {
            textHeight.setSelection(5);
        } else if (MainActivity.boxEdited.getText().getSize() == 14) {
            textHeight.setSelection(6);
        } else if (MainActivity.boxEdited.getText().getSize() == 16) {
            textHeight.setSelection(7);
        } else if (MainActivity.boxEdited.getText().getSize() == 18) {
            textHeight.setSelection(8);
        } else if (MainActivity.boxEdited.getText().getSize() == 20) {
            textHeight.setSelection(9);
        } else if (MainActivity.boxEdited.getText().getSize() == 22) {
            textHeight.setSelection(10);
        } else if (MainActivity.boxEdited.getText().getSize() == 24) {
            textHeight.setSelection(11);
        } else if (MainActivity.boxEdited.getText().getSize() == 36) {
            textHeight.setSelection(12);
        } else if (MainActivity.boxEdited.getText().getSize() == 48) {
            textHeight.setSelection(13);
        } else if (MainActivity.boxEdited.getText().getSize() == 56) {
            textHeight.setSelection(14);
        }
        textHeight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                 @Override
                                                 public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                     EditBox editBox = new EditBox();
                                                     Properties properties = new Properties();
                                                     try {
                                                         Text text = MainActivity.boxEdited.getText().TextClone();
                                                         text.setSize(Integer.parseInt(textHeight.getSelectedItem().toString()));
                                                         properties.put("box_text", text);
                                                         properties.put("box", MainActivity.boxEdited);
                                                         properties.put("boxes", MainActivity.toEditBoxes);
                                                     } catch (CloneNotSupportedException e) {
                                                         e.printStackTrace();
                                                     }
                                                     editBox.execute(properties);
                                                     MainActivity.addCommendUndo(editBox);
                                                 }

                                                 @Override
                                                 public void onNothingSelected(AdapterView<?> parent) {

                                                 }
                                             }
        );


        font = (Spinner) findViewById(R.id.spinnerFonts);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(this,
                R.array.fonts_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        font.setAdapter(adapter5);
        if (MainActivity.boxEdited.getText().typeface == Typeface.DEFAULT) {
            font.setSelection(0);
        } else  if (MainActivity.boxEdited.getText().typeface == Typeface.MONOSPACE) {
            font.setSelection(1);
        } else if (MainActivity.boxEdited.getText().typeface == Typeface.SANS_SERIF) {
            font.setSelection(2);
        } else if (MainActivity.boxEdited.getText().typeface == Typeface.SERIF) {
            font.setSelection(3);
        }

        font.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                EditBox editBox = new EditBox();
                Properties properties = new Properties();
                try {
                    Text text = MainActivity.boxEdited.getText().TextClone();
                    if (font.getSelectedItem().toString().equals("DEFAULT")) {
                        text.typeface = Typeface.DEFAULT;
                    } else if (font.getSelectedItem().toString().equals("MONOSPACE")) {
                        text.typeface = Typeface.MONOSPACE;
                    } else if (font.getSelectedItem().toString().equals("SANS_SERIF")) {
                        text.typeface = Typeface.SANS_SERIF;
                    } else if (font.getSelectedItem().toString().equals("SERIF")) {
                        text.typeface = Typeface.SERIF;
                    }
                    properties.put("box_text", text);
                    properties.put("box", MainActivity.boxEdited);
                    properties.put("boxes", MainActivity.toEditBoxes);
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
                editBox.execute(properties);
                MainActivity.addCommendUndo(editBox);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //todo akcja zmiany wartosci
        //todo dokonczycwartosci
        bold = (CheckBox) findViewById(R.id.checkBoxBold);
        if (MainActivity.boxEdited.getText().isBold()) {
            bold.setChecked(true);
        }
        //todo listenery
        bold.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                            @Override
                                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                if (isChecked) {
                                                   // MainActivity.boxEdited.getText().setBold(true);
                                                    EditBox editBox = new EditBox();
                                                    Properties properties = new Properties();
                                                    try {
                                                        Text text = MainActivity.boxEdited.getText().TextClone();
                                                        text.setBold(true);
                                                        properties.put("box_text", text);
                                                        properties.put("box", MainActivity.boxEdited);
                                                        properties.put("boxes", MainActivity.toEditBoxes);
                                                    } catch (CloneNotSupportedException e) {
                                                        e.printStackTrace();
                                                    }
                                                    editBox.execute(properties);
                                                    MainActivity.addCommendUndo(editBox);
                                                } else {
                                                   // MainActivity.boxEdited.getText().setBold(false);
                                                    EditBox editBox = new EditBox();
                                                    Properties properties = new Properties();
                                                    try {
                                                        Text text = MainActivity.boxEdited.getText().TextClone();
                                                        text.setBold(false);
                                                        properties.put("box_text", text);
                                                        properties.put("box", MainActivity.boxEdited);
                                                        properties.put("boxes", MainActivity.toEditBoxes);
                                                    } catch (CloneNotSupportedException e) {
                                                        e.printStackTrace();
                                                    }
                                                    editBox.execute(properties);
                                                    MainActivity.addCommendUndo(editBox);
                                                }

                                            }
                                        }
        );
        italic = (CheckBox) findViewById(R.id.checkBoxItalic);
        if (MainActivity.boxEdited.getText().isItalic()) {
            italic.setChecked(true);
        }
        italic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                              @Override
                                              public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                  if (isChecked) {
                                                   //   MainActivity.boxEdited.getText().setItalic(true);
                                                      EditBox editBox = new EditBox();
                                                      Properties properties = new Properties();
                                                      try {
                                                          Text text = MainActivity.boxEdited.getText().TextClone();
                                                          text.setItalic(true);
                                                          properties.put("box_text", text);
                                                          properties.put("box", MainActivity.boxEdited);
                                                          properties.put("boxes", MainActivity.toEditBoxes);
                                                      } catch (CloneNotSupportedException e) {
                                                          e.printStackTrace();
                                                      }
                                                      editBox.execute(properties);
                                                      MainActivity.addCommendUndo(editBox);
                                                  } else {
                                                     // MainActivity.boxEdited.getText().setItalic(false);
                                                      EditBox editBox = new EditBox();
                                                      Properties properties = new Properties();
                                                      try {
                                                          Text text = MainActivity.boxEdited.getText().TextClone();
                                                          text.setItalic(false);
                                                          properties.put("box_text", text);
                                                          properties.put("box", MainActivity.boxEdited);
                                                          properties.put("boxes", MainActivity.toEditBoxes);
                                                      } catch (CloneNotSupportedException e) {
                                                          e.printStackTrace();
                                                      }
                                                      editBox.execute(properties);
                                                      MainActivity.addCommendUndo(editBox);
                                                  }

                                              }
                                          }
        );
        strikeOut = (CheckBox) findViewById(R.id.checkBoxStrikeOut);
        if (MainActivity.boxEdited.getText().isStrikeOut()) {
            strikeOut.setChecked(true);
        }
        strikeOut.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                 @Override
                                                 public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                     if (isChecked && textAlign.getSelectedItem().toString().equals("Left")) {
                                                        // MainActivity.boxEdited.getText().setStrikeOut(true);
                                                         EditBox editBox = new EditBox();
                                                         Properties properties = new Properties();
                                                         try {
                                                             Text text = MainActivity.boxEdited.getText().TextClone();
                                                             text.setStrikeOut(true);
                                                             properties.put("box_text", text);
                                                             properties.put("box", MainActivity.boxEdited);
                                                             properties.put("boxes", MainActivity.toEditBoxes);
                                                         } catch (CloneNotSupportedException e) {
                                                             e.printStackTrace();
                                                         }
                                                         editBox.execute(properties);
                                                         MainActivity.addCommendUndo(editBox);
                                                     } else if (textAlign.getSelectedItem().toString().equals("Left")){
                                                       //  MainActivity.boxEdited.getText().setStrikeOut(false);
                                                         EditBox editBox = new EditBox();
                                                         Properties properties = new Properties();
                                                         try {
                                                             Text text = MainActivity.boxEdited.getText().TextClone();
                                                             text.setStrikeOut(false);
                                                             properties.put("box_text", text);
                                                             properties.put("box", MainActivity.boxEdited);
                                                             properties.put("boxes", MainActivity.toEditBoxes);
                                                         } catch (CloneNotSupportedException e) {
                                                             e.printStackTrace();
                                                         }
                                                         editBox.execute(properties);
                                                         MainActivity.addCommendUndo(editBox);
                                                     } else if (isChecked && !textAlign.getSelectedItem().toString().equals("Left")){
                                                         strikeOut.setError("Supported only for left text align.");
                                                     }

                                                 }
                                             }
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
