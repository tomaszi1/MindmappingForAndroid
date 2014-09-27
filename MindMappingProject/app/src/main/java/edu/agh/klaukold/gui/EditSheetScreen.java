package edu.agh.klaukold.gui;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Properties;

import edu.agh.R;
import edu.agh.klaukold.commands.EditSheet;


public class EditSheetScreen extends Activity {
	public ColorPalette getColorPalette() {
		return colorPalette;
	}

	public void setColorPalette(ColorPalette colorPalette) {
		this.colorPalette = colorPalette;
	}

	public CheckBox getMuliBranchColor() {
		return muliBranchColor;
	}

	public void setMuliBranchColor(CheckBox muliBranchColor) {
		this.muliBranchColor = muliBranchColor;
	}

	public SeekBar getIntensivity() {
		return intensivity;
	}

	public void setIntensivity(SeekBar intensivity) {
		this.intensivity = intensivity;
	}

	public Wallpaper getWallPaper() {
		return wallPaper;
	}

	public void setWallPaper(Wallpaper wallPaper) {
		this.wallPaper = wallPaper;
	}

	private ColorPalette colorPalette;
	private CheckBox muliBranchColor;
	private SeekBar intensivity;
	private Wallpaper wallPaper;
    public static TextView backgroud;
    public static View backdroundColorEditScreen;
    public static int COLOR;

    //ToDO obsługa wallpaper oraz multibranch color
	@Override
	public void onResume() {
        super.onResume();
        ((GradientDrawable) backdroundColorEditScreen.getBackground()).setColor(COLOR);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_screen);
        Intent intent = getIntent();
        COLOR =  intent.getIntExtra(MainActivity.BACKGROUNDCOLOR, 1);
        int inten =  intent.getIntExtra(MainActivity.INTENSIVITY, 0);
        intensivity = (SeekBar) findViewById(R.id.seekBarIntensivity);
        intensivity.setProgress((inten));
        backgroud = (TextView) findViewById(R.id.textViewBackgroundColor);
       backdroundColorEditScreen = (View) findViewById(R.id.sheet_color);
        ((GradientDrawable)backdroundColorEditScreen.getBackground()).setColor(COLOR);
        backdroundColorEditScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditSheetScreen.this, ColorPalette.class);
                startActivity(intent);
            }
        });
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
	}

}
