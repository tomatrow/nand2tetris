package edu.miracosta.cs220.project09;

class Point {
    Float x;
    Float y;

    Point(Float x, Float y) {
        this.x = x;
        this.y = y;
    }

    Float getX() {
        return this.x;
    } 

    Float getY() {
        return this.y;
    }

    Point copy() {
        return new Point(x,y);
    }

    public String toString() {
        return String.format("(%s, %s)", x, y);
    }
}
