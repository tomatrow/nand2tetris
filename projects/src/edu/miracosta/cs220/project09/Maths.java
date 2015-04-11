package edu.miracosta.cs220.project09;

class Maths {
    static final Float coeffcient01 = new Float((short)9724, (short)-4);
    static final Float coeffcient02 = new Float((short)1920, (short)-4);

    // Accurate to ~ 3 places...I know...
    // [Approximation](http://goo.gl/eBmL5Q) of [Remez approximation](http://en.wikipedia.org/wiki/Remez_algorithm) of [atan2](http://en.wikipedia.org/wiki/Atan2): 
    // atan(z) = s - r = (0.97179803008 * z) - (0.19065470515 * z**3)
    static Float atan2(Point point) {
        return atan2(point.getX(), point.getY());
    }

    static Float atan2(Float x, Float y) {
        Float z = y.copy();
        z.div(x);

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

        // Just doing things according to math. 
        if (y.getMantissa() < 0 && z.getMantissa() > 0) {
            z.neg();
        }

        return z;
    }
}
