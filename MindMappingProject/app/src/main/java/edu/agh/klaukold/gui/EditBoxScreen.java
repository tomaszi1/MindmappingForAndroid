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

import org.xmind.core.internal.Style;
import org.xmind.core.style.IStyle;
import org.xmind.ui.style.Styles;

import java.util.Properties;

import edu.agh.R;
import edu.agh.klaukold.commands.EditBox;
import edu.agh.klaukold.common.Box;


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
    private String blockShape;

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
    public static Box box;
    public static IStyle style;

    @Override
    public void onResume() {
        super.onResume();
        ((GradientDrawable) BOXCOLOR.getBackground()).setColor(Integer.parseInt(MainActivity.workbook.getStyleSheet().findStyle(MainActivity.boxEdited.topic.getStyleId()).getProperty(Styles.FillColor)));
        ((GradientDrawable) TEXTCOLOR.getBackground()).setColor(Integer.parseInt(MainActivity.workbook.getStyleSheet().findStyle(MainActivity.boxEdited.topic.getStyleId()).getProperty(Styles.TextColor)));
        ((GradientDrawable) LINECOLOR.getBackground()).setColor(Integer.parseInt(MainActivity.workbook.getStyleSheet().findStyle(MainActivity.boxEdited.topic.getStyleId()).getProperty(Styles.LineColor)));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        style = MainActivity.workbook.getStyleSheet().findStyle(MainActivity.boxEdited.topic.getStyleId());
        setContentView(R.layout.edit_box);
        Intent intent = getIntent();
        //box = (Box)intent.getSerializableExtra(EditBoxScreen.BOX);
        BoxColor = Integer.parseInt(style.getProperty(Styles.FillColor));
        TextColor = Integer.parseInt(style.getProperty(Styles.TextColor));
        LineColor = Integer.parseInt(style.getProperty(Styles.LineColor));
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
        if (style.getProperty(Styles.ShapeClass).equals(Styles.TOPIC_SHAPE_RECT)) {
            boxShape.setSelection(2);
        } else if (style.getProperty(Styles.ShapeClass).equals(Styles.TOPIC_SHAPE_ROUNDEDRECT)) {
            boxShape.setSelection(1);
        } else if (style.getProperty(Styles.ShapeClass).equals(Styles.TOPIC_SHAPE_ELLIPSE)) {
            boxShape.setSelection(0);
        } else if (style.getProperty(Styles.ShapeClass).equals(Styles.TOPIC_SHAPE_DIAMOND)) {
            boxShape.setSelection(3);
        } else if (style.getProperty(Styles.ShapeClass).equals(Styles.TOPIC_SHAPE_NO_BORDER)) {
            boxShape.setSelection(5);
        } else if (style.getProperty(Styles.ShapeClass).equals(Styles.TOPIC_SHAPE_UNDERLINE)) {
            boxShape.setSelection(4);
        }
        //dodanie lisener'a do spinnera
        boxShape.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (boxShape.getSelectedItem().toString().equals("Ellipse")) {
                    blockShape = Styles.TOPIC_SHAPE_ELLIPSE;
                } else if (boxShape.getSelectedItem().toString().equals("Rounded Rectangle")) {
                    blockShape = Styles.TOPIC_SHAPE_ROUNDEDRECT;
                } else if (boxShape.getSelectedItem().toString().equals("Rectangle")) {
                    blockShape = Styles.TOPIC_SHAPE_RECT;
                } else if (boxShape.getSelectedItem().toString().equals("Diamond")) {
                    blockShape = Styles.TOPIC_SHAPE_DIAMOND;
                } else if (boxShape.getSelectedItem().toString().equals("Underline")) {
                    blockShape = Styles.TOPIC_SHAPE_UNDERLINE;
                } else if (boxShape.getSelectedItem().toString().equals("No Border")) {
                    blockShape = Styles.TOPIC_SHAPE_NO_BORDER;
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
                    style.setProperty(Styles.LineClass, Styles.BRANCH_CONN_CURVE);

                } else if (lineShape.getSelectedItem().toString().equals("Straight")) {
                    ;
                    style.setProperty(Styles.LineClass, Styles.BRANCH_CONN_STRAIGHT);
//                } else if (lineShape.getSelectedItem().toString().equals("Arrowed Curve")) {
//                    lineStyle = LineStyle.ARROWED_CURVE;
                } else if (lineShape.getSelectedItem().toString().equals("Elbow")) {
                    style.setProperty(Styles.LineClass, Styles.BRANCH_CONN_ELBOW);
                } else if (lineShape.getSelectedItem().toString().equals("Rounded Elbow")) {
                    style.setProperty(Styles.LineClass, Styles.BRANCH_CONN_ROUNDEDELBOW);
                }
                EditBox editBox = new EditBox();
                Properties properties = new Properties();
                properties.put("box", MainActivity.boxEdited);
                properties.put("line_shape", style);
                properties.put("boxes", MainActivity.toEditBoxes);
                editBox.execute(properties);
                MainActivity.addCommendUndo(editBox);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        //lineStyle = (LineStyle) intent.getSerializableExtra(LINE_SHAPE);
        if (style.getProperty(Styles.LineClass).equals(Styles.BRANCH_CONN_CURVE)) {
            lineShape.setSelection(0);
        } else if (style.getProperty(Styles.LineClass).equals(Styles.BRANCH_CONN_STRAIGHT)) {
            lineShape.setSelection(1);
//        } else if (lineStyle == LineStyle.ARROWED_CURVE) {
//            lineShape.setSelection(2);
        } else if (style.getProperty(Styles.LineClass).equals(Styles.BRANCH_CONN_ELBOW)) {
            lineShape.setSelection(2);
        } else if (style.getProperty(Styles.LineClass).equals(Styles.BRANCH_CONN_ROUNDEDELBOW)) {
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
        if (style.getProperty(Styles.LineWidth).equals("1pt")) {
            lineThickness.setSelection(0);
        } else if (style.getProperty(Styles.LineWidth).equals("2pt")) {
            lineThickness.setSelection(1);
        } else if (style.getProperty(Styles.LineWidth).equals("3pt")) {
            lineThickness.setSelection(2);
        } else if (style.getProperty(Styles.LineWidth).equals("4pt")) {
            lineThickness.setSelection(3);
        } else if (style.getProperty(Styles.LineWidth).equals("5pt")) {
            lineThickness.setSelection(4);
        }

        lineThickness.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (lineThickness.getSelectedItem().toString().equals("Thinnest")) {
                    style.setProperty(Styles.LineWidth, "1pt");
                } else if (lineThickness.getSelectedItem().toString().equals("Thin")) {
                    style.setProperty(Styles.LineWidth, "2pt");
                } else if (lineThickness.getSelectedItem().toString().equals("Medium")) {
                    style.setProperty(Styles.LineWidth, "3pt");
                } else if (lineThickness.getSelectedItem().toString().equals("Fat")) {
                    style.setProperty(Styles.LineWidth, "4pt");
                } else if (lineThickness.getSelectedItem().toString().equals("Fattest")) {
                    style.setProperty(Styles.LineWidth, "5pt");
                }
                EditBox editBox = new EditBox();
                Properties properties = new Properties();
                properties.put("box", MainActivity.boxEdited);
                properties.put("line_thickness", style);
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
        if (style.getProperty(Styles.TextAlign).equals(Styles.ALIGN_RIGHT)) {
            textAlign.setSelection(0);
        } else if (style.getProperty(Styles.TextAlign).equals(Styles.ALIGN_CENTER)) {
            textAlign.setSelection(2);
        } else if (style.getProperty(Styles.TextAlign).equals(Styles.ALIGN_LEFT)) {
            textAlign.setSelection(1);
        }
        textAlign.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String align = "";
                if (textAlign.getSelectedItem().toString().equals("Right")) {
                    align = Styles.ALIGN_RIGHT;
                } else if (textAlign.getSelectedItem().toString().equals("Center")) {
                    align = Styles.ALIGN_RIGHT;
                } else if (textAlign.getSelectedItem().toString().equals("Left")) {
                    align = Styles.ALIGN_LEFT;
                }
                EditBox editBox = new EditBox();
                Properties properties = new Properties();
                properties.put("align", align);
                properties.put("box", MainActivity.boxEdited);
                properties.put("boxes", MainActivity.toEditBoxes);
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
        if (style.getProperty(Styles.FontSize).equals("8pt")) {
            textHeight.setSelection(0);
        } else if (style.getProperty(Styles.FontSize).equals("9pt")) {
            textHeight.setSelection(1);
        } else if (style.getProperty(Styles.FontSize).equals("10pt")) {
            textHeight.setSelection(2);
        } else if (style.getProperty(Styles.FontSize).equals("11pt")) {
            textHeight.setSelection(3);
        } else if (style.getProperty(Styles.FontSize).equals("12pt")) {
            textHeight.setSelection(4);
        } else if (style.getProperty(Styles.FontSize).equals("13pt")) {
            textHeight.setSelection(5);
        } else if (style.getProperty(Styles.FontSize).equals("14pt")) {
            textHeight.setSelection(6);
        } else if (style.getProperty(Styles.FontSize).equals("16pt")) {
            textHeight.setSelection(7);
        } else if (style.getProperty(Styles.FontSize).equals("18pt")) {
            textHeight.setSelection(8);
        } else if (style.getProperty(Styles.FontSize).equals("20pt")) {
            textHeight.setSelection(9);
        } else if (style.getProperty(Styles.FontSize).equals("22pt")) {
            textHeight.setSelection(10);
        } else if (style.getProperty(Styles.FontSize).equals("24pt")) {
            textHeight.setSelection(11);
        } else if (style.getProperty(Styles.FontSize).equals("36pt")) {
            textHeight.setSelection(12);
        } else if (style.getProperty(Styles.FontSize).equals("48pt")) {
            textHeight.setSelection(13);
        } else if (style.getProperty(Styles.FontSize).equals("56pt")) {
            textHeight.setSelection(14);
        }
        textHeight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                 @Override
                                                 public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                     EditBox editBox = new EditBox();
                                                     Properties properties = new Properties();
                                                     properties.put("box_size", textHeight.getSelectedItem().toString() + "pt");
                                                     properties.put("box", MainActivity.boxEdited);
                                                     properties.put("boxes", MainActivity.toEditBoxes);
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
        if (style.getProperty(Styles.FontFamily).equals("Times New Roman")) {
            font.setSelection(0);
        } else if (style.getProperty(Styles.FontFamily).equals("Courier New")) {
            font.setSelection(1);
        } else if (style.getProperty(Styles.FontFamily).equals("Arial")) {
            font.setSelection(2);
        }

        font.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                EditBox editBox = new EditBox();
                Properties properties = new Properties();
                String f = "";
                if (font.getSelectedItem().toString().equals("Courier New")) {
                    f = "Courier New";
                } else if (font.getSelectedItem().toString().equals("Arial ")) {
                    f = "Arial";
                } else if (font.getSelectedItem().toString().equals("Times New Roman")) {
                    f = "Times New Roman";
                }
                properties.put("font", f);
                properties.put("box", MainActivity.boxEdited);
                properties.put("boxes", MainActivity.toEditBoxes);
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
        if (style.getProperty(Styles.FontStyle).equals(Styles.FONT_WEIGHT_BOLD)) {
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
                                                    properties.put("bold", true);
                                                    properties.put("box", MainActivity.boxEdited);
                                                    properties.put("boxes", MainActivity.toEditBoxes);

                                                    editBox.execute(properties);
                                                    MainActivity.addCommendUndo(editBox);
                                                } else {
                                                    // MainActivity.boxEdited.getText().setBold(false);
                                                    EditBox editBox = new EditBox();
                                                    Properties properties = new Properties();
                                                    properties.put("bold", false);
                                                    properties.put("box", MainActivity.boxEdited);
                                                    properties.put("boxes", MainActivity.toEditBoxes);
                                                    editBox.execute(properties);
                                                    MainActivity.addCommendUndo(editBox);
                                                }

                                            }
                                        }
        );
        italic = (CheckBox) findViewById(R.id.checkBoxItalic);
        if (style.getProperty(Styles.FontStyle).equals(Styles.FONT_STYLE_ITALIC)) {
            italic.setChecked(true);
        }
        italic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                              @Override
                                              public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                  if (isChecked) {
                                                      //   MainActivity.boxEdited.getText().setItalic(true);
                                                      EditBox editBox = new EditBox();
                                                      Properties properties = new Properties();
                                                      properties.put("italic", true);
                                                      properties.put("box", MainActivity.boxEdited);
                                                      properties.put("boxes", MainActivity.toEditBoxes);
                                                      editBox.execute(properties);
                                                      MainActivity.addCommendUndo(editBox);
                                                  } else {
                                                      // MainActivity.boxEdited.getText().setItalic(false);
                                                      EditBox editBox = new EditBox();
                                                      Properties properties = new Properties();
                                                      properties.put("italic", false);
                                                      properties.put("box", MainActivity.boxEdited);
                                                      properties.put("boxes", MainActivity.toEditBoxes);
                                                      editBox.execute(properties);
                                                      MainActivity.addCommendUndo(editBox);
                                                  }

                                              }
                                          }
        );
        strikeOut = (CheckBox) findViewById(R.id.checkBoxStrikeOut);
        if (style.getProperty(Styles.TextDecoration) == Styles.TEXT_DECORATION_LINE_THROUGH) {
            strikeOut.setChecked(true);
        }
        strikeOut.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                 @Override
                                                 public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                     if (isChecked && textAlign.getSelectedItem().toString().equals("Left")) {
                                                         // MainActivity.boxEdited.getText().setStrikeOut(true);
                                                         EditBox editBox = new EditBox();
                                                         Properties properties = new Properties();
                                                         properties.put("strikeout", "true");
                                                         properties.put("box", MainActivity.boxEdited);
                                                         properties.put("boxes", MainActivity.toEditBoxes);
                                                         editBox.execute(properties);
                                                         MainActivity.addCommendUndo(editBox);
                                                     } else if (textAlign.getSelectedItem().toString().equals("Left")) {
                                                         //  MainActivity.boxEdited.getText().setStrikeOut(false);
                                                         EditBox editBox = new EditBox();
                                                         Properties properties = new Properties();
                                                         properties.put("strikeout", "false");
                                                         properties.put("box", MainActivity.boxEdited);
                                                         properties.put("boxes", MainActivity.toEditBoxes);
                                                         editBox.execute(properties);
                                                         MainActivity.addCommendUndo(editBox);
                                                     } else if (isChecked && !textAlign.getSelectedItem().toString().equals("Left")) {
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
