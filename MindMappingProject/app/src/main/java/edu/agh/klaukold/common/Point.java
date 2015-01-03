package edu.agh.klaukold.common;

public class Point implements  Comparable<Point> {
    @Override
    public int compareTo(Point another) {
        if (x == another.x && y == another.y) {
            return 0;
        } else if (x < another.x && y < another.y) {
            return 1;
        } else {
            return -1;
        }
    }

    public int x;
	public int y;
	
	public Point() {
        x = 0;
        y = 0;

    };
	
	public Point(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
}
