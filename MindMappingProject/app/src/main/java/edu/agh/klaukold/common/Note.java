package edu.agh.klaukold.common;

import java.io.Serializable;

import edu.agh.klaukold.interfaces.Element;

public class Note implements Serializable{
	private Text text;
	
	public Note(Text text) {
		super();
		this.text = text;
	}

	public Text getText() {
		return text;
	}

	public void setText(Text text) {
		this.text = text;
	}


}
