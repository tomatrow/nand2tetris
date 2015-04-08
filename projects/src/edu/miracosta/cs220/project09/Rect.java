public class Rect {
    private Point origin;
    private Size size;

    public Rect(Point origin, Size size) {
        this.origin = origin;
        this.size = size;
    }

    public Point getOrigin() {
        return this.origin;
    }

    public Size getSize() {
        return this.size;
    }
}
