package edu.agh.klaukold.common;

import edu.agh.klaukold.interfaces.Element;

public class Note implements Element {
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

	@Override
	public void draw() {
		// TODO Auto-generated method stub
		
	}

}
