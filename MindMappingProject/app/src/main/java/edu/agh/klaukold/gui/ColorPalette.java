package edu.agh.klaukold.gui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Properties;

import edu.agh.R;
import edu.agh.klaukold.commands.EditBox;
import edu.agh.klaukold.commands.EditSheet;


public class ColorPalette extends Activity {
    Hashtable<View, Integer> colors = new Hashtable<View, Integer>();
    View rgb;
    EditText editText;
    EditText editText1;
    EditText editText2;
    public String type;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.color_palette);
        colors.clear();
        Resources res = getResources();
        Intent intent = getIntent();
        type = intent.getStringExtra("ACTIVITY");
        View.OnClickListener listener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (type.equals(EditBoxScreen.ACTIVITY_TYPE)) {
                    EditBox editBox = new EditBox();
                    Properties properties = new Properties();
                    properties.put("box", MainActivity.boxEdited);
                    properties.put("box_color", String.valueOf(colors.get(v)));
                    properties.put("boxes", MainActivity.toEditBoxes);
                    editBox.execute(properties);
                    MainActivity.addCommendUndo(editBox);
                } else if (type.equals(EditBoxScreen.ACTIVITY_TYPE1)) {
                    EditBox editBox = new EditBox();
                    Properties properties = new Properties();
                    properties.put("text_color", String.valueOf(colors.get(v)));
                    properties.put("box", MainActivity.boxEdited);
                    properties.put("boxes", MainActivity.toEditBoxes);
                    editBox.execute(properties);
                    MainActivity.addCommendUndo(editBox);
                } else if (type.equals(EditBoxScreen.ACTIVITY_TYPE2)) {
                    EditBox editBox = new EditBox();
                    Properties properties = new Properties();
                    properties.put("box", MainActivity.boxEdited);
                    properties.put("line_color", String.valueOf(colors.get(v)));
                    properties.put("boxes", MainActivity.toEditBoxes);
                    editBox.execute(properties);
                    MainActivity.addCommendUndo(editBox);
                } else if (type.equals(EditSheetScreen.ACTIVITY_TYPE)) {
                    EditSheet editSheet = new EditSheet();
                    Properties properties = new Properties();
                    properties.put("sheet", MainActivity.sheet1);
                    properties.put("color", new ColorDrawable(colors.get(v)));
                    editSheet.execute(properties);
                    MainActivity.addCommendUndo(editSheet);
                    //  MainActivity.sheet.setColor(new ColorDrawable(colors.get(v)));
                    EditSheetScreen.COLOR = colors.get(v);
                }
                finish();
            }
        };
        int color = res.getColor(R.color.light_blue);
        View view = (View) findViewById(R.id.color1);
        ((GradientDrawable) view.getBackground()).setColor(color);
        view.setOnClickListener(listener);
        colors.put(view, color);
        color = res.getColor(R.color.conn);
        view = findViewById(R.id.color2);
        ((GradientDrawable) view.getBackground()).setColor(color);
        view.setOnClickListener(listener);
        colors.put(view, color);
        color = res.getColor(R.color.light_gray);
        view = findViewById(R.id.color3);
        ((GradientDrawable) view.getBackground()).setColor(color);
        view.setOnClickListener(listener);
        colors.put(view, color);
        color = res.getColor(R.color.black);
        view = findViewById(R.id.color4);
        ((GradientDrawable) view.getBackground()).setColor(color);
        view.setOnClickListener(listener);
        colors.put(view, color);
        color = res.getColor(R.color.red);
        view = findViewById(R.id.color5);
        ((GradientDrawable) view.getBackground()).setColor(color);
        view.setOnClickListener(listener);
        colors.put(view, color);
        color = res.getColor(R.color.yellow);
        view = findViewById(R.id.color6);
        ((GradientDrawable) view.getBackground()).setColor(color);
        view.setOnClickListener(listener);
        colors.put(view, color);
        color = res.getColor(R.color.light_yellow);
        view = findViewById(R.id.color7);
        ((GradientDrawable) view.getBackground()).setColor(color);
        view.setOnClickListener(listener);
        colors.put(view, color);
        color = res.getColor(R.color.dark_yellow);
        view = findViewById(R.id.color8);
        ((GradientDrawable) view.getBackground()).setColor(color);
        view.setOnClickListener(listener);
        colors.put(view, color);
        color = res.getColor(R.color.green);
        view = findViewById(R.id.color9);
        ((GradientDrawable) view.getBackground()).setColor(color);
        view.setOnClickListener(listener);
        colors.put(view, color);
        color = res.getColor(R.color.dark_green);
        view = findViewById(R.id.color10);
        ((GradientDrawable) view.getBackground()).setColor(color);
        view.setOnClickListener(listener);
        colors.put(view, color);
        color = res.getColor(R.color.blue);
        view = findViewById(R.id.color11);
        ((GradientDrawable) view.getBackground()).setColor(color);
        view.setOnClickListener(listener);
        colors.put(view, color);
        color = res.getColor(R.color.dark_blue);
        view = findViewById(R.id.color12);
        ((GradientDrawable) view.getBackground()).setColor(color);
        view.setOnClickListener(listener);
        colors.put(view, color);
        color = res.getColor(R.color.purple);
        view = findViewById(R.id.color13);
        ((GradientDrawable) view.getBackground()).setColor(color);
        view.setOnClickListener(listener);
        colors.put(view, color);
        color = res.getColor(R.color.dark_purple);
        view = findViewById(R.id.color14);
        ((GradientDrawable) view.getBackground()).setColor(color);
        view.setOnClickListener(listener);
        colors.put(view, color);
        color = res.getColor(R.color.dark_gray);
        view = findViewById(R.id.color15);
        ((GradientDrawable) view.getBackground()).setColor(color);
        view.setOnClickListener(listener);
        colors.put(view, color);
        color = res.getColor(R.color.gray);
        view = findViewById(R.id.color16);
        ((GradientDrawable) view.getBackground()).setColor(color);
        view.setOnClickListener(listener);
        colors.put(view, color);
        color = res.getColor(R.color.dark_red);
        view = findViewById(R.id.color17);
        ((GradientDrawable) view.getBackground()).setColor(color);
        view.setOnClickListener(listener);
        colors.put(view, color);
        color = res.getColor(R.color.rose);
        view = findViewById(R.id.color18);
        ((GradientDrawable) view.getBackground()).setColor(color);
        view.setOnClickListener(listener);
        colors.put(view, color);
        color = res.getColor(R.color.orange);
        view = findViewById(R.id.color19);
        ((GradientDrawable) view.getBackground()).setColor(color);
        view.setOnClickListener(listener);
        colors.put(view, color);
        color = res.getColor(R.color.lemon);
        view = findViewById(R.id.color20);
        ((GradientDrawable) view.getBackground()).setColor(color);
        view.setOnClickListener(listener);
        colors.put(view, color);
        color = res.getColor(R.color.lime_green);
        view = findViewById(R.id.color21);
        ((GradientDrawable) view.getBackground()).setColor(color);
        view.setOnClickListener(listener);
        colors.put(view, color);
        color = res.getColor(R.color.turquoise);
        view = findViewById(R.id.color22);
        ((GradientDrawable) view.getBackground()).setColor(color);
        view.setOnClickListener(listener);
        colors.put(view, color);
        color = res.getColor(R.color.copper);
        view = findViewById(R.id.color23);
        ((GradientDrawable) view.getBackground()).setColor(color);
        view.setOnClickListener(listener);
        colors.put(view, color);
        color = res.getColor(R.color.golden_olive);
        view = findViewById(R.id.color24);
        ((GradientDrawable) view.getBackground()).setColor(color);
        view.setOnClickListener(listener);
        colors.put(view, color);
        color = res.getColor(R.color.beige);
        view = findViewById(R.id.color25);
        ((GradientDrawable) view.getBackground()).setColor(color);
        view.setOnClickListener(listener);
        colors.put(view, color);
        color = res.getColor(R.color.sapphire);
        view = findViewById(R.id.color26);
        ((GradientDrawable) view.getBackground()).setColor(color);
        view.setOnClickListener(listener);
        colors.put(view, color);
        color = res.getColor(R.color.lavender);
        view = findViewById(R.id.color27);
        ((GradientDrawable) view.getBackground()).setColor(color);
        view.setOnClickListener(listener);
        colors.put(view, color);
        color = res.getColor(R.color.white);
        view = findViewById(R.id.color28);
        ((GradientDrawable) view.getBackground()).setColor(color);
        view.setOnClickListener(listener);
        colors.put(view, color);
        color = res.getColor(R.color.khaki);
        view = findViewById(R.id.color29);
        ((GradientDrawable) view.getBackground()).setColor(color);
        view.setOnClickListener(listener);
        colors.put(view, color);
        color = res.getColor(R.color.forest_green);
        view = findViewById(R.id.color30);
        ((GradientDrawable) view.getBackground()).setColor(color);
        view.setOnClickListener(listener);
        colors.put(view, color);
        color = res.getColor(R.color.antique_blue);
        view = findViewById(R.id.color31);
        ((GradientDrawable) view.getBackground()).setColor(color);
        view.setOnClickListener(listener);
        colors.put(view, color);
        color = res.getColor(R.color.indigo);
        view = findViewById(R.id.color32);
        ((GradientDrawable) view.getBackground()).setColor(color);
        view.setOnClickListener(listener);
        colors.put(view, color);
        color = res.getColor(R.color.violet);
        view = findViewById(R.id.color33);
        ((GradientDrawable) view.getBackground()).setColor(color);
        view.setOnClickListener(listener);
        colors.put(view, color);
        editText = (EditText) findViewById(R.id.editText2);
        editText1 = (EditText) findViewById(R.id.editText3);
        editText2 = (EditText) findViewById(R.id.editText4);
        //todo walidacja
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int r = Integer.parseInt(editText.getText().toString());
                    int g = Integer.parseInt(editText1.getText().toString());
                    int b = Integer.parseInt(editText2.getText().toString());
                    Resources res = getResources();
                    if (r >= 0 && r <= 255) {
                        if (g >= 0 && g <= 255) {
                            if (b >= 0 && b <= 255) {
                                ((GradientDrawable) rgb.getBackground()).setColor(Color.rgb(r, g, b));
                            } else {

                                editText2.setError(res.getString(R.string.error_rgb));
                            }
                        } else {
                            editText1.setError(res.getString(R.string.error_rgb));
                        }

                    } else {
                        editText.setError(res.getString(R.string.error_rgb));
                    }

                } catch (Exception e) {
                    // Log.w("text", e.getMessage());
                }
            }
        };
        editText.addTextChangedListener(textWatcher);
        editText1.addTextChangedListener(textWatcher);
        editText2.addTextChangedListener(textWatcher);
        rgb = findViewById(R.id.color34);
        ((GradientDrawable) rgb.getBackground()).setColor(Color.rgb(Integer.parseInt(editText.getText().toString()), Integer.parseInt(editText1.getText().toString()), Integer.parseInt(editText2.getText().toString())));
        View.OnClickListener listener1 = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int r = Integer.parseInt(editText.getText().toString());
                int g = Integer.parseInt(editText1.getText().toString());
                int b = Integer.parseInt(editText2.getText().toString());
                if (type.equals(EditBoxScreen.ACTIVITY_TYPE)) {
                    //todo kolor boxa
                    EditBox editBox = new EditBox();
                    Properties properties = new Properties();
                    properties.put("box", MainActivity.boxEdited);
                    properties.put("box_color", String.valueOf(Color.rgb(r, g, b)));
                    properties.put("boxes", MainActivity.toEditBoxes);
                    editBox.execute(properties);
                    MainActivity.addCommendUndo(editBox);
                } else if (type.equals(EditBoxScreen.ACTIVITY_TYPE1)) {
                    //todo kolor czcionki
                    EditBox editBox = new EditBox();
                    Properties properties = new Properties();

                    properties.put("text_color", String.valueOf(Color.rgb(r, g, b)));
                    properties.put("box", MainActivity.boxEdited);
                    properties.put("boxes", MainActivity.toEditBoxes);
                    editBox.execute(properties);
                    MainActivity.addCommendUndo(editBox);
                } else if (type.equals(EditBoxScreen.ACTIVITY_TYPE2)) {
                    EditBox editBox = new EditBox();
                    Properties properties = new Properties();
                    properties.put("box", MainActivity.boxEdited);
                    properties.put("line_color", String.valueOf(Color.rgb(r, g, b)));
                    properties.put("boxes", MainActivity.toEditBoxes);
                    editBox.execute(properties);
                    MainActivity.addCommendUndo(editBox);
                    //todo kolor lini
                } else if (type.equals(EditSheetScreen.ACTIVITY_TYPE)) {
                    EditSheet editSheet = new EditSheet();
                    Properties properties = new Properties();
                    properties.put("sheet", MainActivity.sheet1);
                    properties.put("color", new ColorDrawable(Color.rgb(r, g, b)));
                    editSheet.execute(properties);
                    MainActivity.addCommendUndo(editSheet);
                    //MainActivity.sheet.setColor(new ColorDrawable(Color.rgb(r, g, b)));
                    EditSheetScreen.COLOR = Color.rgb(r, g, b);
                }
                finish();
            }
        };
        rgb.setOnClickListener(listener1);
    }
//        Intent intent = getIntent();
//        int color =  intent.getIntExtra(MainActivity.BACKGROUNDCOLOR, 1);
//        int inten =  intent.getIntExtra(MainActivity.INTENSIVITY, 100);
//        intensivity = (SeekBar) findViewById(R.id.seekBarIntensivity);
//        intensivity.setProgress((inten));
//        backgroud = (TextView) findViewById(R.id.textViewBackgroundColor);
//        View view = (View) findViewById(R.id.color1);
//        view.getBackground().setAlpha(intensivity.getProgress());
//        view.setBackgroundColor((color));


}
