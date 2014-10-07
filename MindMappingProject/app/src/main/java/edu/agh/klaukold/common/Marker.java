package edu.agh.klaukold.common;

import android.widget.CheckBox;

import java.io.Serializable;

import edu.agh.klaukold.enums.MarkerType;
import edu.agh.klaukold.interfaces.Element;

public class Marker implements  Serializable {

	public Marker(String url, MarkerType type, CheckBox imageBox) {
		super();
		this.url = url;
		this.type = type;
		this.imageBox = imageBox;
	}
	private String url;
	private MarkerType type;
	private CheckBox imageBox;
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public MarkerType getType() {
		return type;
	}

	public void setType(MarkerType type) {
		this.type = type;
	}

	public CheckBox getImageBox() {
		return imageBox;
	}

	public void setImageBox(CheckBox imageBox) {
		this.imageBox = imageBox;
	}

	
	public Marker()
	{
		
	}

}
