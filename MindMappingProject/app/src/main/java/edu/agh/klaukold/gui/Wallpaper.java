package edu.agh.klaukold.gui;

import android.graphics.Rect;

import org.xmind.core.internal.Image;

public class Wallpaper {
    private Rect rectangle;

    public Wallpaper(Rect rectangle, Image image) {
        super();
        this.rectangle = rectangle;
        this.image = image;
    }

    public Rect getRectangle() {
        return rectangle;
    }

    public void setRectangle(Rect rectangle) {
        this.rectangle = rectangle;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    private Image image;
}
