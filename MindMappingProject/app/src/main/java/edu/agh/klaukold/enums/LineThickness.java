package edu.agh.klaukold.enums;

public enum LineThickness {
	THINNEST(2), THIN(3), MEDIUM(4), FAT(5), FATTEST(6);
    float value;
    LineThickness(int v) {
        value = v;
    }

    public float getValue() {
        return value;
    }
}
