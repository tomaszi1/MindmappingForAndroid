package edu.agh.klaukold.common;

import android.graphics.Color;

import org.xmind.core.internal.Image;

import edu.agh.klaukold.enums.MapStyle;

public class Sheet {
	public MapStyle getStyle() {
		return style;
	}
	public void setStyle(MapStyle style) {
		this.style = style;
	}
	public Root getRoot() {
		return root;
	}
	public void setRoot(Root root) {
		this.root = root;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public Image getWallpaper() {
		return wallpaper;
	}
	public void setWallpaper(Image wallpaper) {
		this.wallpaper = wallpaper;
	}
	public int getIntensivity() {
		return intensivity;
	}
	public void setIntensivity(int intensivity) {
		this.intensivity = intensivity;
	}
	public boolean isMultiBranchColor() {
		return isMultiBranchColor;
	}
	public void setMultiBranchColor(boolean isMultiBranchColor) {
		this.isMultiBranchColor = isMultiBranchColor;
	}
	public Sheet(MapStyle style, Root root, Color color, Image wallpaper,
			int intensivity, boolean isMultiBranchColor) {
		super();
		this.style = style;
		this.root = root;
		this.color = color;
		this.wallpaper = wallpaper;
		this.intensivity = intensivity;
		this.isMultiBranchColor = isMultiBranchColor;
	}
	private MapStyle style;
	private Root root;
	private Color color;
	private Image wallpaper;
	private int intensivity;
	private boolean isMultiBranchColor;
}
