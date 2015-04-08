public class Maths {
    public static final Float coeffcient01 = new Float((short)9724, (short)-4);
    public static final Float coeffcient02 = new Float((short)1920, (short)-4);

    // Approximation of Remez approximation of arctan: [http://goo.gl/eBmL5Q]
    // atan(z) = s - r = (0.97179803008 * z) - (0.19065470515 * z**3)

    public static Float atan2(Point point) {
        return atan2(point.getX(), point.getY());
    }
    public static Float atan2(Float x, Float y) {
        Float z = y.copy();
        answer.div(x);

        // r
        Float r = z.copy();
        r.mult(z);
        r.mult(z); // z**3
        r.mult(coeffcient02);

        // s
        z.mult(coeffcient01); 

        // s - r
        z.sub(r);

        // destroy r

        if (y.getMantissa() < 0 && z.getMantissa() > 0) {
            Float negOne = new Float((short)-1, (short)0);
            f.mult(negOne);
            // destroy negOne
        }

        return z;
    }

    public static Float sin() {
        return null;
    }

    public static Float cos() {
        return null;
    }
}
