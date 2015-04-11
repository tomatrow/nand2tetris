package edu.miracosta.cs220.project09;

class Rect {
    Point origin;
    Size size;

    Rect(Point origin, Size size) {
        this.origin = origin;
        this.size = size;
    }

    Point getOrigin() {
        return this.origin;
    }

    Size getSize() {
        return this.size;
    }

    Rect copy() {
        return new Rect(origin.copy(), size.copy());
    }

    public String toString() {
        return String.format("[%s, %s]", origin, size);
    }
}

