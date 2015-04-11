package edu.miracosta.cs220.project09;

public class Driver {
    public static void main(String[] args) {
        Float ten = new Float((short)10, (short)0);
        Float two = new Float((short)2, (short)0);
        Point point = new Point(Float.zero.copy(), Float.zero.copy());

        Transform t = new Transform(Transform.identity);
        Size size = new Size(ten.copy(), ten.copy());
        Rect rect = new Rect(point, size);

        t.scale(two.copy(), two.copy());
        t.translate(ten.copy(), ten.copy());
        ten.neg();
        t.translate(ten.copy(), ten.copy());
        t.apply(rect);

        System.out.println(t);
        System.out.println(rect);
    }
}
