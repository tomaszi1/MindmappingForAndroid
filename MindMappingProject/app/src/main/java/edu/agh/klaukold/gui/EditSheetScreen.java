package edu.agh.klaukold.gui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.SeekBar;

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
	
	@Override
	public void onResume() {
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

	}
	
	@Override
	public void onDestroy() {
	}
}
