package edu.miracosta.cs220.project09;

class Size {
    Float width;
    Float height;

    Size(Float width, Float height) {
        this.width = width;
        this.height = height;
    } 

    Float getWidth() {
        return this.width;
    }

    Float getHeight() {
        return this.height;
    }

    Size copy() {
        return new Size(width, height);
    }

    public wmString toString() {
        return String.format("(%s, %s)", width, height);
    }
}
